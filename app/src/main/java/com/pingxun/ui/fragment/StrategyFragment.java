package com.pingxun.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.pingxun.activity.R;
import com.pingxun.adapter.StrategyListAdapter;
import com.pingxun.adapter.StrategyTopAdapter;
import com.pingxun.base.BaseFragment;
import com.pingxun.data.RaidersListBean;
import com.pingxun.activity.databinding.FragmentStrategyBinding;
import com.pingxun.http.ServerApi;
import com.pingxun.other.InitDatas;
import com.pingxun.ui.activity.StrategyInfoActivity;
import com.pingxundata.answerliu.pxcore.data.ServerModelList;
import com.pingxundata.answerliu.pxcore.view.EmptyLayout;
import com.pingxundata.pxmeta.http.PXHttp;
import com.pingxundata.pxmeta.pojo.RequestResult;
import com.pingxundata.pxmeta.utils.ActivityUtil;
import com.pingxundata.pxmeta.utils.NetUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import static com.pingxun.other.RequestFlag.GET_LIST;
import static com.pingxun.other.RequestFlag.GET_TOP;


/**
 * Created by Administrator on 2017/8/12.
 */

public class StrategyFragment extends BaseFragment<FragmentStrategyBinding> implements OnRefreshListener, PXHttp.OnResultHandler,View.OnClickListener {

    private List<ServerModelList> mTopList;//攻略头部集合
    private StrategyTopAdapter mTopAdapter;//攻略头部文章adapter
    private List<RaidersListBean> mList;//攻略列表集合
    private StrategyListAdapter mListAdapter;//攻略列表adapter


    @Override
    protected int getRootLayoutResID() {
        return R.layout.fragment_strategy;
    }

    @Override
    protected void initData() {
        initAdapter();
        bindingView.swipeLayout.setOnRefreshListener(this);
        bindingView.emptyLayout.setOnClickListener(this);
        bindingView.emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        bindingView.swipeLayout.autoRefresh();
    }

    private void initAdapter() {


        mTopAdapter = new StrategyTopAdapter(R.layout.rv_item_credit_top, mTopList);
        bindingView.rvTop.setNestedScrollingEnabled(false);
        bindingView.rvTop.setHasFixedSize(false);
        bindingView.rvTop.setLayoutManager(new GridLayoutManager(getContext(), 4));
        bindingView.rvTop.setAdapter(mTopAdapter);
        bindingView.rvTop.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                      Bundle bundle=new Bundle();
                      bundle.putString(InitDatas.INFOR_ID,String.valueOf(mTopList.get(position).getId()));
                      ActivityUtil.goForward(mActivity, StrategyInfoActivity.class,bundle,false);
            }
        });

        mListAdapter=new StrategyListAdapter(R.layout.rv_item_raiders_list,mList);
        bindingView.rvList.setNestedScrollingEnabled(false);
        bindingView.rvList.setHasFixedSize(false);
        bindingView.rvList.setLayoutManager(new LinearLayoutManager(mActivity));
        bindingView.rvList.setAdapter(mListAdapter);
        bindingView.rvList.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle=new Bundle();
                bundle.putString(InitDatas.INFOR_ID,String.valueOf(mList.get(position).getId()));
                ActivityUtil.goForward(mActivity, StrategyInfoActivity.class,bundle,false);
            }
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.empty_layout:
                bindingView.emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                bindingView.swipeLayout.autoRefresh();
                break;
        }
    }

    @Override
    public void onResult(RequestResult requestResult, String jsonStr, int flag) {

        switch (flag) {
            case GET_TOP:
                try {
                    if (requestResult.isSuccess()){
                        mTopList = (List<ServerModelList>)requestResult.getResultList();
                        mTopAdapter.setNewData(mTopList);
                    }
                }catch (Exception e){}
               ServerApi.getStrategyList(StrategyFragment.this);
                break;
            case GET_LIST:
                try {
                    if (requestResult.isSuccess()){
                        mList = (List<RaidersListBean>)requestResult.getResultList();
                        mListAdapter.setNewData(mList);
                    }
                }catch (Exception e){}
                bindingView.swipeLayout.finishRefresh();
                bindingView.emptyLayout.setErrorType(EmptyLayout.NO_ERROR);
                break;
        }
    }

    @Override
    public void onError(int flag) {
        bindingView.swipeLayout.finishRefresh();
        if (NetUtil.getNetWorkState(mActivity) == NetUtil.NETWORK_NONE) {
            bindingView.emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        } else {
            bindingView.emptyLayout.setErrorType(EmptyLayout.ERROR);
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        ServerApi.getStrategyTop(StrategyFragment.this);
    }


}
