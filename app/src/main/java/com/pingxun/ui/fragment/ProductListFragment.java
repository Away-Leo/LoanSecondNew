package com.pingxun.ui.fragment;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.dbhh.bigwhiteflowers.R;
import com.dbhh.bigwhiteflowers.databinding.ActivityMyProductListBinding;
import com.pingxun.adapter.ProductListAdapter;
import com.pingxun.base.App;
import com.pingxun.base.BaseFragment;
import com.pingxun.data.ProductListMoreBean;
import com.pingxun.http.ServerApi;
import com.pingxun.other.InitDatas;
import com.pingxundata.answerliu.pxcore.data.ServerModelList;
import com.pingxundata.answerliu.pxcore.other.Constant;
import com.pingxundata.answerliu.pxcore.other.CustomAnimation;
import com.pingxundata.answerliu.pxcore.view.ListPopup;
import com.pingxundata.pxmeta.http.PXHttp;
import com.pingxundata.pxmeta.pojo.RequestResult;
import com.pingxundata.pxmeta.utils.NetUtil;
import com.pingxundata.pxmeta.utils.ToastUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.pingxun.other.RequestFlag.GET_ZS_TYPE;
import static com.pingxun.other.RequestFlag.LOADMORE;
import static com.pingxun.other.RequestFlag.REFRESH;


/**
 * Created by LH on 2017/9/2.
 * Describe:产品超市左列表
 */

public class ProductListFragment extends BaseFragment<ActivityMyProductListBinding> implements PXHttp.OnResultHandler, OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {



    private ListPopup mListPopup;
    private List<ServerModelList> mTypeList;
    private ProductListAdapter mAdapter;
    private List<ProductListMoreBean> mList;//产品集合
    private View notDataView;//无数据View
    private View errorView;//网络异常View
    private View notNetView;//网络异常View;
    private int page_size = 10;//每一次请求加载的条数
    private int mCurrentCounter = 0;//上一次加载的个数
    private int TOTAL_COUNTER;//总数
    private int page = 1;

    private String zsType = "";//综合指数参数


    @Override
    protected int getRootLayoutResID() {
        return  R.layout.activity_my_product_list;
    }

    @Override
    protected void initData() {
        initAdapter();
        bindingView.tvMessageTip.setText(InitDatas.messageTip);
        bindingView.smartRefreshLayout.setEnableHeaderTranslationContent(false);
        bindingView.smartRefreshLayout.setOnRefreshListener(this);
        bindingView.smartRefreshLayout.autoRefresh();
    }

    @Override
    protected void onLazyLoadOnce() {
        super.onLazyLoadOnce();
        bindingView.smartRefreshLayout.autoRefresh();
    }

    /**
     * 初始化adapter
     */
    private void initAdapter() {
        notDataView = getLayoutInflater().inflate(R.layout.view_empty, (ViewGroup) bindingView.rv.getParent(), false);
        errorView = getLayoutInflater().inflate(R.layout.view_error, (ViewGroup) bindingView.rv.getParent(), false);
        notNetView = getLayoutInflater().inflate(R.layout.view_notnet, (ViewGroup) bindingView.rv.getParent(), false);

        bindingView.rv.setLayoutManager(new LinearLayoutManager(App.getAppContext()));
        mAdapter = new ProductListAdapter(R.layout.activity_my_product_list_one, mList);
        mAdapter.setOnLoadMoreListener(this, bindingView.rv);
        mAdapter.openLoadAnimation(new CustomAnimation());

        bindingView.rv.setAdapter(mAdapter);
        bindingView.rv.setHasFixedSize(true);
        bindingView.rv.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                    isLoginToWeb(initBundle(mList.get(position).getId()+"",mList.get(position).getUrl(),mList.get(position).getName(),0));
            }
        });

        errorView.setOnClickListener(view -> bindingView.smartRefreshLayout.autoRefresh());
        notDataView.setOnClickListener(v -> bindingView.smartRefreshLayout.autoRefresh());
        notNetView.setOnClickListener(v -> bindingView.smartRefreshLayout.autoRefresh());

    }

    @Override
    public void onResult(RequestResult requestResult, String jsonStr, int flag) {
        switch (flag) {

            case REFRESH://下拉刷新返回数据的回调
                try {
                    if (requestResult.isSuccess()) {
                        TOTAL_COUNTER = requestResult.getTotalElements();
                        mList = (List<ProductListMoreBean>) requestResult.getResultList();
                        if (mList.size() == 0) {
                            mAdapter.setNewData(null);
                            mAdapter.setEmptyView(notDataView);
                        } else {
                            mAdapter.setNewData(mList);
                            mAdapter.disableLoadMoreIfNotFullPage();
                            bindingView.rv.scrollToPosition(0);
                            mCurrentCounter = mAdapter.getData().size();
                        }
                    } else {
                        mAdapter.setNewData(null);
                        mAdapter.setEmptyView(errorView);
                    }
                } catch (Exception e) {
                    mAdapter.setNewData(null);
                    mAdapter.setEmptyView(errorView);
                }
                bindingView.smartRefreshLayout.finishRefresh();
                break;
            case LOADMORE://上拉加载返回数据的回调
                try {
                    if (requestResult.isSuccess()) {
                        mAdapter.loadMoreComplete();
                        List<ProductListMoreBean> mListMore;
                        mListMore = (List<ProductListMoreBean>) requestResult.getResultList();
                        mAdapter.addData(mListMore);
                        mCurrentCounter = mAdapter.getData().size();
                    } else {
                        mAdapter.loadMoreFail();
                    }
                } catch (Exception e) {
                    mAdapter.loadMoreFail();
                }
                break;
        }
    }



    @Override
    public void onError(int flag) {
        mAdapter.setNewData(null);
        bindingView.smartRefreshLayout.finishRefresh();
        if (NetUtil.getNetWorkState(App.getAppContext()) == -1) {
            mAdapter.setEmptyView(notNetView);
        } else {
            mAdapter.setEmptyView(errorView);
        }
    }



    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        refreshlayout.getLayout().postDelayed(() -> {
            page = 1;
            ServerApi.postProductSearch(App.getAppContext(),this,page, REFRESH,zsType, InitDatas.LeftCode);
        },InitDatas.waitTime);
    }

    @Override
    public void onLoadMoreRequested() {
        if (mCurrentCounter < page_size) {
            mAdapter.loadMoreEnd(true);
        } else {
            if (mCurrentCounter >= TOTAL_COUNTER) {
                mAdapter.loadMoreEnd(false);
            } else {
                if (NetUtil.getNetWorkState(App.getAppContext()) !=-1) {
                    new Handler().postDelayed(() -> {
                        page++;
                        ServerApi.postProductSearch(App.getAppContext(),this,page, LOADMORE,zsType,InitDatas.LeftCode);
                    },1000);
                } else {
                    ToastUtils.showToast(App.getAppContext(), Constant.NO_NETWORK_MSG);
                    mAdapter.loadMoreFail();
                }
            }
        }
    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);//注销事件接受

    }



}
