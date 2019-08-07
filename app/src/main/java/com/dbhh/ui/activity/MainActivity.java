package com.dbhh.ui.activity;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.dbhh.base.App;
import com.dbhh.base.BaseActivity;
import com.dbhh.bigwhiteflowers.R;
import com.dbhh.bigwhiteflowers.databinding.UiHomeBinding;
import com.dbhh.other.InitDatas;
import com.dbhh.other.Urls;
import com.dbhh.ui.view.MessageDialog;
import com.orhanobut.logger.Logger;
import com.pingxundata.pxcore.autoupdate.UpdateChecker;
import com.pingxundata.pxcore.utils.WallManager;
import com.pingxundata.pxmeta.utils.GDlocationUtil;
import com.pingxundata.pxmeta.utils.SharedPrefsUtil;
import com.pingxundata.pxmeta.views.DragFloatActionButton;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;

//import com.dbhh.bigwhiteflowers.databinding.UiHomeBinding;


public class MainActivity extends BaseActivity<UiHomeBinding> implements TabHost.OnTabChangeListener {


    private long mLastPressBackTime;
    private MainTab[] mTabs;
    private MessageDialog messageDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.ui_home;
    }

    @Override
    protected void initData() {
        mTabs = MainTab.values();
        getAppModule();
        checkUpdate();
        WallManager.with(me,(DragFloatActionButton)findViewById(R.id.wallFloatingButton), InitDatas.APP_NAME,InitDatas.SUSPENDFLAG).doWall();
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.LOCATION, Permission.Group.STORAGE)
                .onGranted(permissions -> {
                    GDlocationUtil.init(App.getAppContext());
                    GDlocationUtil.getCurrentLocation(new GDlocationUtil.MyLocationListener() {
                        @Override
                        public void result(AMapLocation location) {
                            InitDatas.province = location.getProvince();
                            InitDatas.city = location.getCity();
                            InitDatas.district = location.getDistrict();

                            Logger.d(InitDatas.province+"/"+InitDatas.city+"/"+InitDatas.district);
                        }
                    });
                })
                .onDenied((Action) permissions -> {
                })
                .rationale(mRationale)
                .start();

    }
    private Rationale mRationale = (context, permissions, executor) -> {
        // 这里使用一个Dialog询问用户是否继续授权。
        showDialog(false, "为了更好的服务需要开启定位权限和存储权限，是否开启？", view -> {
            messageDialog.dismiss();
            // 如果用户中断：
            executor.cancel();
        }, view -> {
            messageDialog.dismiss();
            // 如果用户继续：
            executor.execute();
        });
    };
    private void showDialog(boolean isOk, String message, View.OnClickListener cancel, View.OnClickListener submit){
        if(messageDialog == null){
            messageDialog = new MessageDialog(this, R.style.loading_dialog);
        }
        messageDialog.setOnCancekClickListener(cancel);
        messageDialog.setOnSubmitClickListener(submit);
        if(isOk){
            messageDialog.setMessage(message,isOk);
            messageDialog.setStatus("成功");
        }else{
            messageDialog.setMessage(message,isOk);
            messageDialog.setStatus("失败");
        }
        messageDialog.setImageSource(isOk);
        messageDialog.show();
    }
    /**
     * 获取APP模块
     */
    private void getAppModule() {
        String appModule= SharedPrefsUtil.getValue(App.getAppContext(),InitDatas.SP_NAME, InitDatas.AppModule,"1,6,5");//默认开启信用卡模块
//        if (!appModule.contains("3")){//0:是否通过,1:首页,2:消息,3:信用卡,4:产品超市,5:我的,6:产品大全,7:精准,8:攻略
//            mTabs[mTabs.length - 2] = mTabs[mTabs.length - 1];//把最后一个元素替代指定的元素
//            mTabs = Arrays.copyOf(mTabs, mTabs.length - 1);//数组缩容
//        }
        initTabs();
    }

    /**
     * 检查更新
     */
    private void checkUpdate() {
        UpdateChecker.checkForDialog(me,InitDatas.APP_UPDATE, Urls.URL_GET_FIND_PRODUCT_VERSION);
    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        WallManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }




    @SuppressLint("ObsoleteSdkInt")
    private void initTabs() {
        bindingView.myTabhost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        bindingView.myTabhost.getTabWidget().setShowDividers(0);

        for (MainTab mainTab : mTabs) {
            TabHost.TabSpec tabSpec = bindingView.myTabhost.newTabSpec(getString(mainTab.getResName()));
            @SuppressLint("InflateParams") ViewGroup indicator = (ViewGroup) getLayoutInflater().inflate(R.layout.tab_indicator, null, false);
            TextView title =indicator.findViewById(R.id.tab_title);
            title.setText(getString(mainTab.getResName()));
            ImageView icon = indicator.findViewById(R.id.tab_icon);
            icon.setImageResource(mainTab.getResIcon());
            tabSpec.setIndicator(indicator);
            tabSpec.setContent(new TabHost.TabContentFactory() {
                @Override
                public View createTabContent(String tag) {
                    return new View(getApplicationContext());
                }
            });
            bindingView.myTabhost.addTab(tabSpec, mainTab.getClazz(), null);
        }
        bindingView.myTabhost.setOnTabChangedListener(this);
    }

    @Override
    public void onTabChanged(String s) {
        final int size = bindingView.myTabhost.getTabWidget().getTabCount();
        for (int i = 0; i < size; i++) {
            View v = bindingView.myTabhost.getTabWidget().getChildAt(i);
            if (i == bindingView.myTabhost.getCurrentTab()) {
                v.setSelected(true);
            } else {
                v.setSelected(false);
            }
        }
        supportInvalidateOptionsMenu();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.ACTION_DOWN || keyCode == KeyEvent.KEYCODE_BACK) {
            long current = System.currentTimeMillis();
            if (current - mLastPressBackTime > 2000) {
                mLastPressBackTime = current;
                Toast.makeText(App.getAppContext(), "再按一次退出程序!", Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }





}
