package com.pingxun.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.pingxun.activity.R;
import com.pingxun.adapter.ApplyListAdapter;
import com.pingxun.base.App;
import com.pingxun.base.BaseActivity;
import com.pingxun.http.ServerApi;
import com.pingxun.other.InitDatas;
import com.pingxundata.answerliu.pxcore.data.ApplyListBean;
import com.pingxundata.answerliu.pxcore.databinding.ActivityApplyListBinding;
import com.pingxundata.answerliu.pxcore.other.Constant;
import com.pingxundata.answerliu.pxcore.other.CustomAnimation;
import com.pingxundata.pxmeta.http.PXHttp;
import com.pingxundata.pxmeta.pojo.RequestResult;
import com.pingxundata.pxmeta.utils.ActivityUtil;
import com.pingxundata.pxmeta.utils.NetUtil;
import com.pingxundata.pxmeta.utils.SharedPrefsUtil;
import com.pingxundata.pxmeta.utils.ToastUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import static com.pingxun.other.RequestFlag.LOADMORE;
import static com.pingxun.other.RequestFlag.REFRESH;


/**
 * 申请记录界面
 */
public class ApplyListActivity extends BaseActivity<ActivityApplyListBinding> implements OnRefreshListener, PXHttp.OnResultHandler, BaseQuickAdapter.RequestLoadMoreListener {

    private View notDataView;//无数据View
    private View errorView;//异常View
    private View notNetView;//网络异常View;

    private int TOTAL_COUNTER;//总数

    private ApplyListAdapter mAdapter;
    private List<ApplyListBean> mApplyList;
    private int mCurrentCounter = 0;//上一次加载的个数
    private int page = 1;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_apply_list;
    }

    @Override
    protected void initData() {
        initTopView("申请记录");
        initAdapter();
        bindingView.smartrefreshlayout.setEnableHeaderTranslationContent(false);
        bindingView.smartrefreshlayout.setOnRefreshListener(this);
        bindingView.smartrefreshlayout.autoRefresh();
    }



    /**
     * 初始化Adapter
     */
    private void initAdapter() {
        notDataView = getLayoutInflater().inflate(R.layout.view_empty, (ViewGroup) bindingView.rv.getParent(), false);
        errorView = getLayoutInflater().inflate(R.layout.view_error, (ViewGroup) bindingView.rv.getParent(), false);
        notNetView = getLayoutInflater().inflate(R.layout.view_notnet, (ViewGroup) bindingView.rv.getParent(), false);

        mAdapter = new ApplyListAdapter(R.layout.activity_my_product_list_one, mApplyList);
        mAdapter.setOnLoadMoreListener(this, bindingView.rv);
        mAdapter.openLoadAnimation(new CustomAnimation());
        bindingView.rv.setAdapter(mAdapter);
        bindingView.rv.setHasFixedSize(true);
        bindingView.rv.setLayoutManager(new LinearLayoutManager(App.getAppContext()));
        bindingView.rv.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString(InitDatas.INFOR_ID, String.valueOf(mApplyList.get(position).getProductId()));
                ActivityUtil.goForward(me, ProductInfoActivity.class, bundle, false);
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
            case REFRESH:
                try {
                    if (requestResult.isSuccess()) {
                        TOTAL_COUNTER =requestResult.getTotalElements();
                        mApplyList = (List<ApplyListBean>)requestResult.getResultList();
                        if (mApplyList.size() == 0) {//如果没有数据，设置空布局
                            mAdapter.setNewData(null);
                            mAdapter.setEmptyView(notDataView);
                        } else {
                            mAdapter.setNewData(mApplyList);
                            mCurrentCounter = mAdapter.getData().size();//获取adapter的size
                        }
                    }else {
                        SharedPrefsUtil.putValue(App.getAppContext(),InitDatas.SP_NAME,InitDatas.UserIsLogin,false);
                        ActivityUtil.recallGoForward(me,LoginActivity.class,ApplyListActivity.class,null,true);
                    }
                } catch (Exception e) {
                    SharedPrefsUtil.putValue(App.getAppContext(),InitDatas.SP_NAME,InitDatas.UserIsLogin,false);
                    ActivityUtil.recallGoForward(me,LoginActivity.class,ApplyListActivity.class,null,true);
                }
                bindingView.smartrefreshlayout.finishRefresh();
                break;

            case LOADMORE:
                try {
                    if (requestResult.isSuccess()) {
                        mAdapter.loadMoreComplete();
                        List<ApplyListBean> mListMore;
                        mListMore = (List<ApplyListBean>)requestResult.getResultList();
                        mAdapter.addData(mListMore);
                        mCurrentCounter = mAdapter.getData().size();
                    }else {
                        mAdapter.loadMoreFail();
                    }
                }catch (Exception e){
                    mAdapter.loadMoreFail();
                }
                break;

        }

    }



    @Override
    public void onError(int flag) {
        mAdapter.setNewData(null);
        bindingView.smartrefreshlayout.finishRefresh();
        if (NetUtil.getNetWorkState(App.getAppContext()) ==NetUtil.NETWORK_NONE) {
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
                ServerApi.getApplyList(ApplyListActivity.this,page, REFRESH);
            }
        },InitDatas.waitTime);
    }


    /**
     * 上拉加载
     */
    @Override
    public void onLoadMoreRequested() {
        int page_size = 10;
        if (mCurrentCounter < page_size) {
            mAdapter.loadMoreEnd(true);
        } else {
            if (mCurrentCounter >= TOTAL_COUNTER) {
                mAdapter.loadMoreEnd(false);
            } else {
                if (NetUtil.getNetWorkState(App.getAppContext()) !=NetUtil.NETWORK_NONE) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            page++;
                            ServerApi.getApplyList(ApplyListActivity.this,page, LOADMORE);
                        }
                    },InitDatas.waitTime);

                } else {
                    ToastUtils.showToast(App.getAppContext(), Constant.NO_NETWORK_MSG);
                    mAdapter.loadMoreFail();
                }
            }

        }
    }
}
