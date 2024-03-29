package com.dbhh.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.dbhh.bigwhiteflowers.R;
import com.dbhh.bigwhiteflowers.databinding.ActivityMyProductListBinding;
import com.dbhh.adapter.ProductListAdapter;
import com.dbhh.base.App;
import com.dbhh.base.BaseActivity;
import com.dbhh.data.ProductListMoreBean;
import com.dbhh.http.ServerApi;
import com.dbhh.other.InitDatas;
import com.pingxundata.answerliu.pxcore.data.ServerModelList;
import com.pingxundata.answerliu.pxcore.other.Constant;
import com.pingxundata.answerliu.pxcore.other.CustomAnimation;
import com.pingxundata.answerliu.pxcore.view.ListPopup;
import com.pingxundata.pxcore.absactivitys.PXSimpleWebViewActivity;
import com.pingxundata.pxmeta.http.PXHttp;
import com.pingxundata.pxmeta.pojo.RequestResult;
import com.pingxundata.pxmeta.utils.ActivityUtil;
import com.pingxundata.pxmeta.utils.NetUtil;
import com.pingxundata.pxmeta.utils.ObjectHelper;
import com.pingxundata.pxmeta.utils.SharedPrefsUtil;
import com.pingxundata.pxmeta.utils.ToastUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import static com.dbhh.other.RequestFlag.LOADMORE;
import static com.dbhh.other.RequestFlag.REFRESH;


/**
 * Created by LH on 2017/9/2.
 * Describe:产品超市
 */

public class ProductListActivity extends BaseActivity<ActivityMyProductListBinding> implements PXHttp.OnResultHandler, View.OnClickListener , OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener{


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
    protected int getLayoutId() {
        return R.layout.activity_my_product_list;
    }

    @Override
    protected void initData() {
        initAdapter();
        bindingView.tvMessageTip.setText(InitDatas.messageTip);
        bindingView.smartRefreshLayout.setEnableHeaderTranslationContent(false);
        bindingView.smartRefreshLayout.setOnRefreshListener(this);
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
                isLoginToProductWeb(mList.get(position));
            }
        });

        errorView.setOnClickListener(view -> bindingView.smartRefreshLayout.autoRefresh());
        notDataView.setOnClickListener(v -> bindingView.smartRefreshLayout.autoRefresh());
        notNetView.setOnClickListener(v -> bindingView.smartRefreshLayout.autoRefresh());

    }

    @Override
    public void onClick(View view) {
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
    public void onError(int i) {
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
            ServerApi.postProductSearch(App.getAppContext(),ProductListActivity.this,page, REFRESH,zsType, InitDatas.LeftCode);
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
                        ServerApi.postProductSearch(App.getAppContext(),ProductListActivity.this,page, LOADMORE,zsType,InitDatas.LeftCode);
                    },1000);
                } else {
                    ToastUtils.showToast(App.getAppContext(), Constant.NO_NETWORK_MSG);
                    mAdapter.loadMoreFail();
                }
            }
        }
    }


    /**
     * 判断是否登录，跳转到产品详情页
     */
    private void isLoginToProductWeb(ProductListMoreBean productListMoreBean) {
        appName = InitDatas.APP_NAME;
        channelNo = InitDatas.CHANNEL_NO;
        applyArea = InitDatas.province + "/" + InitDatas.city + "/" + InitDatas.district;

        Bundle bundle = new Bundle();
        bundle.putString("url", ObjectHelper.isNotEmpty(productListMoreBean.getUrl())?productListMoreBean.getUrl():"");
        bundle.putString("productId", productListMoreBean.getId()+"");
        bundle.putString("productName", productListMoreBean.getName());
        bundle.putString("appName", appName);
        bundle.putString("channelNo", channelNo);
        bundle.putString("applyArea", applyArea);
        if (!SharedPrefsUtil.getValue(App.getAppContext(), InitDatas.SP_NAME, InitDatas.UserIsLogin, false)) {
            bundle.putInt("backImg", R.mipmap.icon_back);
            bundle.putInt("titleColor", R.color.mainColor);
            bundle.putInt("topBack", R.color.tab_font_bright);
            ActivityUtil.recallGoForward(me, LoginActivity.class, PXSimpleWebViewActivity.class, bundle, false);
        } else {
            startWebForRecommend(0, bundle, R.mipmap.icon_back,R.color.mainColor, R.color.tab_font_bright);
        }
    }
}
