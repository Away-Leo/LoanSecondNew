package com.dbhh.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;

import com.dbhh.bigwhiteflowers.BuildConfig;
import com.dbhh.bigwhiteflowers.R;
import com.dbhh.other.InitDatas;
import com.pingxundata.pxcore.applications.BaseApplication;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.xinyan.xinyanoklsdk.XinYanOKLSDK;
import com.umeng.commonsdk.UMConfigure;


/**
 * Created by LH.
 * Application基类
 */
public class App extends BaseApplication {

    private static String APPKEY = "4cb387a38b3f206f0721d247c76ecd46";//替换成您的AppKey
    private static String APPSECRET = "9423d74d327267a6";//替换成您的AppSecret
    private static String merchantNo= "8150725937";//替换成您的商户号

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.white, R.color.tab_font);//全局设置主题颜色
                return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
    }

    @Override
    protected void appInit() {
        UMConfigure.init(this, InitDatas.UMENG_APPKEY, InitDatas.CHANNEL_NO, UMConfigure.DEVICE_TYPE_PHONE, null);
        XinYanOKLSDK.getInstance().isDebug(BuildConfig.DEBUG);
        XinYanOKLSDK.getInstance().initSDK(this,APPKEY,APPSECRET,merchantNo);
    }

}
