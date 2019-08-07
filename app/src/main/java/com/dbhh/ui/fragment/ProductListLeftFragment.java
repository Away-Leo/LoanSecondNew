package com.dbhh.ui.fragment;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.dbhh.bigwhiteflowers.R;
import com.dbhh.adapter.ProductListAdapter;
import com.dbhh.base.App;
import com.dbhh.base.BaseFragment;
import com.dbhh.data.ProductListMoreBean;
import com.dbhh.http.ServerApi;
import com.dbhh.other.InitDatas;
import com.pingxundata.answerliu.pxcore.databinding.ActivityProductListViewPagerBinding;
import com.pingxundata.answerliu.pxcore.other.Constant;
import com.pingxundata.answerliu.pxcore.other.CustomAnimation;
import com.pingxundata.answerliu.pxcore.other.EventMessage;
import com.pingxundata.pxmeta.http.PXHttp;
import com.pingxundata.pxmeta.pojo.RequestResult;
import com.pingxundata.pxmeta.utils.NetUtil;
import com.pingxundata.pxmeta.utils.ToastUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static com.dbhh.other.RequestFlag.LOADMORE;
import static com.dbhh.other.RequestFlag.REFRESH;


/**
 * Created by LH on 2017/9/2.
 * Describe:产品超市左列表
 */

public class ProductListLeftFragment extends BaseFragment<ActivityProductListViewPagerBinding> implements PXHttp.OnResultHandler, OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {



    private ProductListAdapter mAdapter;
    private List<ProductListMoreBean> mList;//产品集合
    private View notDataView;//无数据View
    private View errorView;//网络异常View
    private View notNetView;//网络异常View;
    private int page_size = 10;//每一次请求加载的条数
    private int mCurrentCounter = 0;//上一次加载的个数
    private int TOTAL_COUNTER;//总数
    private int page = 1;

    private String zsType="";//综合指数参数


    @Override
    protected int getRootLayoutResID() {
        EventBus.getDefault().register(this);//绑定事件接受
        return R.layout.activity_product_list_view_pager;
    }

    @Override
    protected void initData() {
        initAdapter();
        bindingView.smartrefreshlayout.setEnableHeaderTranslationContent(false);
        bindingView.smartrefreshlayout.setOnRefreshListener(this);
    }

    @Override
    protected void onLazyLoadOnce() {
        super.onLazyLoadOnce();
        bindingView.smartrefreshlayout.autoRefresh();
    }

    /**
     * 初始化adapter
     */
    private void initAdapter() {
        notDataView = mActivity.getLayoutInflater().inflate(R.layout.view_empty, (ViewGroup) bindingView.rv.getParent(), false);
        errorView = mActivity.getLayoutInflater().inflate(R.layout.view_error, (ViewGroup) bindingView.rv.getParent(), false);
        notNetView = mActivity.getLayoutInflater().inflate(R.layout.view_notnet, (ViewGroup) bindingView.rv.getParent(), false);

        bindingView.rv.setLayoutManager(new LinearLayoutManager(App.getAppContext()));
        mAdapter = new ProductListAdapter(R.layout.rv_item_product_style_one, mList);
        mAdapter.setOnLoadMoreListener(this, bindingView.rv);
        mAdapter.openLoadAnimation(new CustomAnimation());

        bindingView.rv.setAdapter(mAdapter);
        bindingView.rv.setHasFixedSize(true);
        bindingView.rv.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                isLoginToProuctInfo(String.valueOf(mList.get(position).getId()));
            }
        });

        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindingView.smartrefreshlayout.autoRefresh();
            }
        });
        notDataView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindingView.smartrefreshlayout.autoRefresh();
            }
        });
        notNetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindingView.smartrefreshlayout.autoRefresh();
            }
        });

    }

    @Override
    public void onResult(RequestResult requestResult, String jsonStr, int flag) {
        switch (flag) {
            case REFRESH://下拉刷新返回数据的回调
                try {
                    if (requestResult.isSuccess()) {
                        TOTAL_COUNTER = requestResult.getTotalElements();
                        mList =(List<ProductListMoreBean>)requestResult.getResultList();
                        if (mList.size() == 0) {
                            mAdapter.setNewData(null);
                            mAdapter.setEmptyView(notDataView);
                        } else {
                            mAdapter.setNewData(mList);
                            mAdapter.disableLoadMoreIfNotFullPage();
                            bindingView.rv.scrollToPosition(0);
                            mCurrentCounter = mAdapter.getData().size();
                        }
                    }else {
                        mAdapter.setNewData(null);
                        mAdapter.setEmptyView(errorView);
                    }
                } catch (Exception e) {
                    mAdapter.setNewData(null);
                    mAdapter.setEmptyView(errorView);
                }
                bindingView.smartrefreshlayout.finishRefresh();
                break;
            case LOADMORE://上拉加载返回数据的回调
                try {
                    if (requestResult.isSuccess()) {
                        mAdapter.loadMoreComplete();
                        List<ProductListMoreBean> mListMore;
                        mListMore = (List<ProductListMoreBean>)requestResult.getResultList();
                        mAdapter.addData(mListMore);
                        mCurrentCounter = mAdapter.getData().size();
                    }else {
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
        bindingView.smartrefreshlayout.finishRefresh();
        if (NetUtil.getNetWorkState(App.getAppContext()) == NetUtil.NETWORK_NONE) {
            mAdapter.setEmptyView(notNetView);
        } else {
            mAdapter.setEmptyView(errorView);
        }
    }



    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        refreshlayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                page = 1;
                ServerApi.postProductSearch(App.getAppContext(),ProductListLeftFragment.this,page, REFRESH,zsType, InitDatas.LeftCode);
            }
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
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            page++;
                            ServerApi.postProductSearch(App.getAppContext(),ProductListLeftFragment.this,page, LOADMORE,zsType,InitDatas.LeftCode);
                        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(EventMessage message) {
        if (message.message.equals(Constant.RefreshProductList)&&message.status==0) {
            zsType=message.id;
            bindingView.smartrefreshlayout.autoRefresh();
        }
    }


}
