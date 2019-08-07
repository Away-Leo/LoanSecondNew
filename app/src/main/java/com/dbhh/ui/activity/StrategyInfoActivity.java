package com.dbhh.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.dbhh.bigwhiteflowers.R;
import com.dbhh.base.App;
import com.dbhh.base.BaseActivity;
import com.dbhh.http.ServerApi;
import com.dbhh.other.InitDatas;
import com.pingxundata.answerliu.pxcore.data.ServerModelObject;
import com.pingxundata.answerliu.pxcore.databinding.ActivityStrategyInfoBinding;
import com.pingxundata.answerliu.pxcore.view.EmptyLayout;
import com.pingxundata.pxmeta.http.PXHttp;
import com.pingxundata.pxmeta.pojo.RequestResult;
import com.pingxundata.pxmeta.utils.GlideImgManager;
import com.pingxundata.pxmeta.utils.NetUtil;

import static com.dbhh.other.RequestFlag.GET_FIND_BY_STRATEGY_ID;


public class StrategyInfoActivity extends BaseActivity<ActivityStrategyInfoBinding> implements PXHttp.OnResultHandler, View.OnClickListener {

    private String sId;//攻略ID

    @Override
    protected int getLayoutId() {
        return R.layout.activity_strategy_info;
    }

    @Override
    protected void initData() {
        initTopView("攻略详情");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            sId = bundle.getString(InitDatas.INFOR_ID);
            bindingView.emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            ServerApi.getStrategyInfo(StrategyInfoActivity.this, sId);
        }
        bindingView.emptyLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.empty_layout:
                bindingView.emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                ServerApi.getStrategyInfo(StrategyInfoActivity.this, sId);
                break;
        }
    }


    @Override
    public void onResult(RequestResult requestResult, String jsonStr, int flag) {
        switch (flag) {
            case GET_FIND_BY_STRATEGY_ID:
                try {
                    if (requestResult.isSuccess()) {
                        ServerModelObject InfoBeanData = (ServerModelObject) requestResult.getEntityResult();
                        GlideImgManager.glideLoader(App.getAppContext(), InfoBeanData.getCmsImg(), R.mipmap.img_default, R.mipmap.img_default, bindingView.ivIcon, 1);
                        bindingView.tvContent.setText(InfoBeanData.getContent());
                        bindingView.tvTitle.setText(InfoBeanData.getTitle());
                        bindingView.emptyLayout.setErrorType(EmptyLayout.NO_ERROR);
                    } else {
                        bindingView.emptyLayout.setErrorType(EmptyLayout.ERROR);
                    }
                } catch (Exception e) {
                    bindingView.emptyLayout.setErrorType(EmptyLayout.ERROR);
                }
                break;

        }
    }

    @Override
    public void onError(int flag) {
        if (NetUtil.getNetWorkState(App.getAppContext()) == NetUtil.NETWORK_NONE) {
            bindingView.emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        } else {
            bindingView.emptyLayout.setErrorType(EmptyLayout.ERROR);
        }
    }
}
