package com.pingxun.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.pingxun.activity.R;
import com.pingxun.other.InitDatas;
import com.pingxun.ui.activity.LoginActivity;
import com.pingxun.ui.activity.ProductInfoActivity;
import com.pingxundata.pxcore.absactivitys.PXSimpleWebViewActivity;
import com.pingxundata.pxmeta.utils.ActivityUtil;
import com.pingxundata.pxmeta.utils.SharedPrefsUtil;


/**
 * Created by LH on 2017/8/31.
 * Fragment基类
 * @author LH
 */
public abstract class BaseFragment<SV extends ViewDataBinding> extends Fragment {
    // 布局view
    protected SV bindingView;
    protected View mRootView;
    protected String TAG;
    protected BaseActivity mActivity;
    protected boolean mIsLoadedData = false;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        TAG = this.getClass().getSimpleName();
        mActivity = (BaseActivity) getActivity();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //这里的判断是起到设置缓存的效果，如果不判断则每次点击Tab都会刷新一次
        if (mRootView==null){
            bindingView = DataBindingUtil.inflate(LayoutInflater.from(mActivity),getRootLayoutResID(),null,false);
            mRootView=bindingView.getRoot();
            initData();
        }else if (mRootView.getParent()!=null){
            ((ViewGroup)mRootView.getParent()).removeView(mRootView);
        }
        return mRootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed()) {
            handleOnVisibilityChangedToUser(isVisibleToUser);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            handleOnVisibilityChangedToUser(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            handleOnVisibilityChangedToUser(false);
        }
    }
    /**
     * 处理对用户是否可见

     *
     * @param isVisibleToUser 是否对用户可见
     */
    private void handleOnVisibilityChangedToUser(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            // 对用户可见
            if (!mIsLoadedData) {
                mIsLoadedData = true;
                onLazyLoadOnce();
            }
            onVisibleToUser();
        } else {
            // 对用户不可见
            onInvisibleToUser();
        }
    }

    /**
     * 懒加载一次。如果只想在对用户可见时才加载数据，并且只加载一次数据，在子类中重写该方法

     */
    protected void onLazyLoadOnce() {
    }

    /**
     * 对用户可见时触发该方法。如果只想在对用户可见时才加载数据，在子类中重写该方法
     */
    protected void onVisibleToUser() {
    }

    /**
     * 对用户不可见时触发该方法
     */
    protected void onInvisibleToUser() {
    }

    /**
     * 获取布局文件根视图
     * @return 根布局
     */
    protected abstract int getRootLayoutResID();


    /**
     * 处理业务逻辑，状态恢复等操作
     *
     */
    protected abstract void initData();




    /**
     * 判断是否登录，跳转到产品详情页
     */
    public void isLoginToProuctInfo(String sId) {
        if (!SharedPrefsUtil.getValue(getContext(), InitDatas.SP_NAME, InitDatas.UserIsLogin, false)) {
            Bundle bundle = new Bundle();
            bundle.putString(InitDatas.INFOR_ID, sId);
            ActivityUtil.recallGoForward(mActivity,LoginActivity.class, ProductInfoActivity.class,bundle,false);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(InitDatas.INFOR_ID, sId);
            ActivityUtil.goForward(mActivity, ProductInfoActivity.class, bundle, false);
        }
    }


    /**
     * 判断是否登录，跳转到网页浏览
     */
    public void isLoginToWeb(Bundle bundle) {
        if (!SharedPrefsUtil.getValue(mActivity, InitDatas.SP_NAME, InitDatas.UserIsLogin, false)) {
            ActivityUtil.recallGoForward(mActivity,LoginActivity.class,PXSimpleWebViewActivity.class,bundle,false);
        } else {
            ActivityUtil.goForward(mActivity, PXSimpleWebViewActivity.class, bundle, false);
        }
    }

    /**
     * 组装跳转到WebView的Bundle
     * 如果flag为1代表从首页Banner跳转
     */
    public Bundle initBundle(String id, String jumpUrl, String name,int flag) {
        Bundle mBundle=new Bundle();
        mBundle.putString("productName",name);
        mBundle.putString("productId",id);
        mBundle.putString("appName",InitDatas.APP_NAME);
        mBundle.putString("url", jumpUrl);
        mBundle.putString("applyArea",InitDatas.province + "/" + InitDatas.city + "/" + InitDatas.district);
        if (flag==1){
            mBundle.putString("deviceNumber","BANNER_LINK");
        }
        mBundle.putString("channelNo",InitDatas.CHANNEL_NO);
        mBundle.putInt("backImg", R.mipmap.icon_back);
        mBundle.putInt("titleColor", Color.WHITE);
        mBundle.putInt("topBack", R.color.tab_font_bright);
        return mBundle;
    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public  void onDestroy() {
        super.onDestroy();
    }

    private ProgressDialog dialog;

    public void showLoading() {
        if (dialog != null && dialog.isShowing()) return;
        dialog = new ProgressDialog(mActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("请求网络中...");
        dialog.show();
    }

    public void dismissLoading() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
