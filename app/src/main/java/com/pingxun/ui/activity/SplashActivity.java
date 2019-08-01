package com.pingxun.ui.activity;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dbhh.bigwhiteflowers.R;
import com.pingxun.base.App;
import com.pingxun.other.InitDatas;
import com.pingxun.service.LoginService;
import com.pingxundata.answerliu.pxcore.databinding.ActivitySplashBinding;
import com.pingxundata.pxmeta.utils.ActivityUtil;
import com.pingxundata.pxmeta.utils.SharedPrefsUtil;


/**
 * 启动页
 */
public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding bindingView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 避免从桌面启动程序后，会重新实例化入口类的activity
        if (!this.isTaskRoot()) {
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                    return;
                }
            }
        }
        bindingView = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        initData();
    }




    protected void initData() {
        Intent serviceIn=new Intent(this, LoginService.class);
        startService(serviceIn);
        bindingView.ivSplash.setImageResource(R.mipmap.splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!SharedPrefsUtil.getValue(App.getAppContext(), InitDatas.SP_NAME, InitDatas.UserIsFirstSplash, false)){
                    ActivityUtil.goForward(SplashActivity.this,GuidActivity.class,null,true);
                    overridePendingTransition(R.anim.screen_zoom_in, R.anim.screen_zoom_out);
                }else {
                    ActivityUtil.goForward(SplashActivity.this,MainActivity.class,null,true);
                    overridePendingTransition(R.anim.screen_zoom_in, R.anim.screen_zoom_out);
                }
            }
        },2000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}