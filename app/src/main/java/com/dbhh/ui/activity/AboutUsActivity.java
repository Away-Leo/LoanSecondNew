package com.dbhh.ui.activity;


import android.annotation.SuppressLint;

import com.dbhh.bigwhiteflowers.R;
import com.dbhh.base.App;
import com.dbhh.base.BaseActivity;
import com.pingxundata.answerliu.pxcore.databinding.ActivityAboutUsBinding;
import com.pingxundata.pxmeta.utils.AppUtils;


public class AboutUsActivity extends BaseActivity<ActivityAboutUsBinding> {



    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_us;
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        initTopView("关于我们");
        bindingView.logo.setImageResource(R.mipmap.app_icon_round);
        bindingView.tvAppVersions.setText("当前版本:"+ AppUtils.getVersionName(App.getAppContext()).replaceAll("_","."));
    }


}
