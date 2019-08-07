package com.dbhh.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.dbhh.bigwhiteflowers.R;
import com.dbhh.adapter.BankAdapter;
import com.dbhh.adapter.CreditCardAdapter;
import com.dbhh.base.App;
import com.dbhh.base.BaseFragment;
import com.dbhh.data.CreditCardBean;
import com.dbhh.bigwhiteflowers.databinding.FragmentCreditCardBinding;
import com.dbhh.http.ServerApi;
import com.dbhh.other.InitDatas;
import com.dbhh.other.RequestFlag;
import com.pingxundata.answerliu.pxcore.data.ServerModelList;
import com.pingxundata.answerliu.pxcore.view.EmptyLayout;
import com.pingxundata.pxmeta.http.PXHttp;
import com.pingxundata.pxmeta.pojo.RequestResult;
import com.pingxundata.pxmeta.utils.NetUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import static com.dbhh.other.RequestFlag.GET_BANK;
import static com.dbhh.other.RequestFlag.POST_APPLY_CREDIT_CARD;
import static com.dbhh.other.RequestFlag.REFRESH;


/**
 * Created by LH on 2017/12/6.
 * Describe:信用卡Fragment
 */

public class CreditCardFragment extends BaseFragment<FragmentCreditCardBinding> implements PXHttp.OnResultHandler,OnRefreshListener,View.OnClickListener{

    private List<ServerModelList> mBankList;
    private List<CreditCardBean> mCreditCardist;
    private BankAdapter mBankAdapter;
    private CreditCardAdapter mCreditCardAdapter;
    private int page_size = 10;//每一次请求加载的条数
    private int mCurrentCounter = 0;//上一次加载的个数
    private int TOTAL_COUNTER;//总数
    private int page = 1;
    private Bundle mBundle;

    @Override
    protected int getRootLayoutResID() {
        return R.layout.fragment_credit_card;
    }

    @Override
    protected void initData() {
        initAdapter();
        bindingView.emptyLayout.setOnClickListener(this);
        bindingView.swipeLayout.setOnRefreshListener(this);
        bindingView.emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
    }

    @Override
    protected void onLazyLoadOnce() {
        super.onLazyLoadOnce();
        bindingView.swipeLayout.autoRefresh();
    }

    private void initAdapter() {
        mBankAdapter = new BankAdapter(R.layout.rv_item_bank,mBankList);
        bindingView.rvTop.setHasFixedSize(true);
        bindingView.rvTop.setLayoutManager(new GridLayoutManager(getContext(), 3));
        bindingView.rvTop.setAdapter(mBankAdapter);
        bindingView.rvTop.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                String id= String.valueOf(mBankList.get(position).getId());
                String url=mBankList.get(position).getUrl();
                String name=mBankList.get(position).getName();
                String deviceNumber= Build.MODEL+"(" + Build.MANUFACTURER + ")";
                String applyArea= InitDatas.province + "/" + InitDatas.city + "/" + InitDatas.district;

                isLoginToWeb(initBundle(id,url,name,2));
                ServerApi.postApplyCreditCard(CreditCardFragment.this,id,deviceNumber,applyArea,InitDatas.CHANNEL_NO,InitDatas.APP_NAME);
            }
        });

        mCreditCardAdapter=new CreditCardAdapter(R.layout.rv_item_credit_card,mCreditCardist);
        bindingView.rvList.setHasFixedSize(true);
        bindingView.rvList.setLayoutManager(new LinearLayoutManager(App.getAppContext()));
        bindingView.rvList.setAdapter(mCreditCardAdapter);
        bindingView.rvList.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                String id= String.valueOf(mCreditCardist.get(position).getId());
                String url=mCreditCardist.get(position).getUrl();
                String name=mCreditCardist.get(position).getName();
                String deviceNumber= Build.MODEL+"(" + Build.MANUFACTURER + ")";
                String applyArea= InitDatas.province + "/" + InitDatas.city + "/" + InitDatas.district;

                isLoginToWeb(initBundle(id,url,name,2));
                ServerApi.postApplyCreditCard(CreditCardFragment.this,id,deviceNumber,applyArea,InitDatas.CHANNEL_NO,InitDatas.APP_NAME);
            }
        });

    }


    @Override
    public void onResult(RequestResult requestResult, String jsonStr, int flag) {
        switch (flag){
            case GET_BANK:
                try {
                    if (requestResult.isSuccess()){
                        mBankList=(List<ServerModelList>)requestResult.getResultList();
                        mBankAdapter.setNewData(mBankList);
                    }
                }catch (Exception e){}
                page=1;
                ServerApi.getCreditCard(CreditCardFragment.this,page,REFRESH);
                break;
            case REFRESH:
                try {
                    if (requestResult.isSuccess()) {
                        TOTAL_COUNTER = requestResult.getTotalElements();
                        mCreditCardist =(List<CreditCardBean>)requestResult.getResultList();
                        mCreditCardAdapter.setNewData(mCreditCardist);
                    }
                } catch (Exception e) {}
                bindingView.swipeLayout.finishRefresh();
                bindingView.emptyLayout.setErrorType(EmptyLayout.NO_ERROR);
                break;
            case POST_APPLY_CREDIT_CARD:

                break;

        }
    }

    @Override
    public void onError(int flag) {
        bindingView.swipeLayout.finishRefresh();
        if (NetUtil.getNetWorkState(mActivity) == -1) {
            bindingView.emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        }else {
            if (flag!= RequestFlag.POST_APPLY_CREDIT_CARD){
                bindingView.emptyLayout.setErrorType(EmptyLayout.ERROR);
            }
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        ServerApi.getBank(CreditCardFragment.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.empty_layout://空布局
                bindingView.emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                bindingView.swipeLayout.autoRefresh();
                break;
        }
    }
}
