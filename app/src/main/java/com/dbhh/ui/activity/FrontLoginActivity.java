package com.dbhh.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dbhh.base.App;
import com.dbhh.base.BaseActivity;
import com.dbhh.bigwhiteflowers.R;
import com.dbhh.bigwhiteflowers.databinding.ActivityFrontLoginBinding;
import com.dbhh.data.UserDto;
import com.dbhh.http.ServerApi;
import com.dbhh.other.InitDatas;
import com.dbhh.other.PermissionUtil;
import com.dbhh.ui.view.LoadingDialog;
import com.dbhh.ui.view.MessageDialog;
import com.pingxundata.answerliu.pxcore.other.Constant;
import com.pingxundata.answerliu.pxcore.other.EventMessage;
import com.pingxundata.pxcore.absactivitys.PXSimpleWebViewActivity;
import com.pingxundata.pxmeta.http.PXHttp;
import com.pingxundata.pxmeta.pojo.RequestResult;
import com.pingxundata.pxmeta.utils.ActivityUtil;
import com.pingxundata.pxmeta.utils.NetUtil;
import com.pingxundata.pxmeta.utils.SharedPrefsUtil;
import com.pingxundata.pxmeta.utils.ToastUtils;
import com.xinyan.xinyanoklsdk.Interface.OKLCallBack;
import com.xinyan.xinyanoklsdk.Interface.XYUICallBack;
import com.xinyan.xinyanoklsdk.XinYanOKLSDK;
import com.xinyan.xinyanoklsdk.entity.ErrorEntity;
import com.xinyan.xinyanoklsdk.entity.ResultData;
import com.xinyan.xinyanoklsdk.entity.ViewEntity;
import com.xinyan.xinyanoklsdk.oklUI.XYUIConfig;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;

import org.greenrobot.eventbus.EventBus;

import static com.dbhh.other.RequestFlag.POST_APPLY;
import static com.dbhh.other.RequestFlag.POST_LOGIN;

//import com.xinyan.xinyanoklsdk.http.Constants;

/**
 * @Project OneKeyLogin
 * @PackageName com.android.xinyan.onekeylogin
 * @Author Loe Zou
 * @GreatDate 2019/3/14/014
 */
public class FrontLoginActivity extends BaseActivity<ActivityFrontLoginBinding> implements PXHttp.OnResultHandler{

    private LoadingDialog loadingDialog;
    private MessageDialog messageDialog;
    private boolean isSucess;
    private String loginStatus;
    private String loginMessage;
    private final int REQ_CODE_NUMBERVALIDATE=1;//号码认证页面请求码



    @Override
    protected int getLayoutId() {
        return R.layout.activity_front_login;
    }

    @Override
    protected void initData() {
        AndPermission.with(this)
                .runtime()
                .permission(PermissionUtil.needPermissions)
                .onGranted(permissions -> {
                    clickOneKeyLogin();
                })
                .onDenied((Action) permissions -> {
                    ActivityUtil.recallGoForward(FrontLoginActivity.this,LoginActivity.class,PXSimpleWebViewActivity.class,getIntent().getExtras(),true);
                })
                .rationale(mRationale)
                .start();
        loadingDialog = new LoadingDialog(this);
//        XinYanOKLSDK.getInstance().isDebug(false);
//        XinYanOKLSDK.getInstance().setDialog(loadingDialog);
    }
    private Rationale mRationale = (context, permissions, executor) -> {
        ActivityUtil.recallGoForward(FrontLoginActivity.this,LoginActivity.class,PXSimpleWebViewActivity.class,getIntent().getExtras(),true);
//        // 这里使用一个Dialog询问用户是否继续授权。
//        showDialog(false, "一键登录需要开启手机权限，是否开启？", view -> {
//            messageDialog.dismiss();
//            // 如果用户中断：
//            executor.cancel();
//            finish();
//        }, view -> {
//            messageDialog.dismiss();
//            AndPermission.with(this)
//                    .runtime()
//                    .setting()
//                    .start(1);
//        });
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
     * 一键登录
     */
    private void clickOneKeyLogin(){
        initLoginUIConfig();
        showProgressBar();
        XinYanOKLSDK.getInstance().startOneKeyLogin(me, new OKLCallBack() {
            @Override
            public void success(ResultData resultData) {
                resultData.getOclToken();  //新颜授权令牌
                resultData.getOperatorType(); //运营商操作类型
                Log.d("登陆成功", "msg:" + resultData.toString());
                ServerApi.postLogin(FrontLoginActivity.this,null,null,"phoneLogin",resultData.getOclToken());
            }

            @Override
            public void failed(final ErrorEntity errorEntity) {
                hideProgressBar();
                if ("CANCEL".equals(errorEntity.getErrorCode())){
                    // TODO: 用户取消授权
                    hideProgressBar();
                    XinYanOKLSDK.getInstance().finishActivity();
                }else {
                    isSucess = false;
                    loginMessage = errorEntity.getErrorMsg();
                    loginStatus = "失败";
                    ActivityUtil.recallGoForward(FrontLoginActivity.this,LoginActivity.class,PXSimpleWebViewActivity.class,getIntent().getExtras(),true);
                }
            }
        });
    }



    private void initLoginUIConfig(){
        final XYUIConfig xyuiConfig = new XYUIConfig(FrontLoginActivity.this);
        /**
         * 是否隐藏其他登录方式
         */
        xyuiConfig.setOtherLoginHidden(true);
        /**
         *  是否隐藏logo
         */
        xyuiConfig.setLogoHidden(false);

        /**
         * 设置服务隐私协议 (需要和下面的setDisabledFYServiceAgreement(true)同时使用才能生效)
         */
        xyuiConfig.setAgreeServiceProvideUrl("https://www.xinyan.com/");

        /**
         *  禁用默认服务协议
         */
//        xyuiConfig.setDisabledFYServiceAgreement(true);

        /**
         * 设置用户自定义协议（单独设置不起作用需要配合自定义点击事件）
         * 需要配合     xyuiConfig.setCustomView3(ViewEntity.createPrivaceCustomView());同时使用
         *              <string name="agree_customer_service_name"></string> 中必须设置名字
         */
        xyuiConfig.setAgreeCustomerProvideUrl("https://github.com/");
        /**
         * 其他登录方式点击事件
         */
        xyuiConfig.setOtherLoginListener(new XYUICallBack() {
            @Override
            public void clickCallBack(Context context, @Nullable View view) {
                Toast.makeText(FrontLoginActivity.this,"点击了其他登录方式" , Toast.LENGTH_SHORT).show();
            }
        });
        /**
         * 自定义点击事件 详见下面
         */
        initCustom(xyuiConfig);

        /**
         * 设置配置的属性
         */
        XinYanOKLSDK.getInstance().setXyuiConfig(xyuiConfig);
    }

    private void initCustom(XYUIConfig xyuiConfig) {
        /**
         * 设置自定义点击事件
         * @param 参数一 id为在自定义layout中设置的id （在feiyan_layout_custom_head_view.xml或feiyan_layout_custom_view.xml中添加）
         *            注意：1.2个layout不允许用重复id存在。
         *                  2.建议在id命名中增加模块标志。以防止与sdk中id命名冲突。
         * @param 参数二 是否自动关闭页面。当设置为false时，可以在回调中通过 XinYanOKLSDK.getInstance().finishActivity() 手动关闭
         * @param 参数三 回调
         */
        xyuiConfig.setCustomView1(ViewEntity.createCustomView("tv_test1", false, new XYUICallBack() {
            @Override
            public void clickCallBack(Context context, @Nullable View view) {
                ActivityUtil.recallGoForward(FrontLoginActivity.this, LoginActivity.class,PXSimpleWebViewActivity.class,getIntent().getExtras(),true);
            }
        }));

        /**
         * 设置协议自定义点击事件,以下步骤需要配套使用
         * 注意1.  <string name="agree_customer_service_name"></string> 中必须设置名字
         * 注意2.  xyuiConfig.setAgreeCustomerProvideUrl("https://github.com/");
         */
//        xyuiConfig.setCustomView3(ViewEntity.createPrivaceCustomView());
    }


    public void showProgressBar(){
        loadingDialog.setLoadingText("正在登录中，请勿退出");
        loadingDialog.showDialog();
    }

    public void hideProgressBar(){
        if(loadingDialog != null){
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("XYOKLSDK","requestCode=="+requestCode+"=="+resultCode +(data!=null));
        if(requestCode==REQ_CODE_NUMBERVALIDATE && data!=null){
            if(resultCode==RESULT_OK){ //成功
                String oclToken=data.getStringExtra("oclToken");
                String phoneNum=data.getStringExtra("phoneNum");
                String operatorType=data.getStringExtra("operatorType");
                isSucess = true;
                loginMessage = oclToken;
                loginStatus = "成功";
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(FrontLoginActivity.this,loginStatus);
                        }
                    });
                }catch (Throwable e){
                    Log.d("XYOKLSDK",e.toString());
                }
            }else{
                String msgCode=data.getStringExtra("msgCode");
                final String msg=data.getStringExtra("msg");
                isSucess = false;
                loginMessage = msg;
                loginStatus = "失败";
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressBar();
                            Log.d("XYOKLSDK",msg);
                            ToastUtils.showToast(FrontLoginActivity.this,loginStatus);
                        }
                    });
                }catch (Throwable e){
                    Log.d("XYOKLSDK",e.toString());

                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onResult(RequestResult requestResult, String s, int i) {
        switch (i){
            case POST_LOGIN://登录
                dismissLoading();
                hideProgressBar();
                UserDto userDto=(UserDto)requestResult.getEntityResult();
                try {
                    if (requestResult.isSuccess()) {
                        ToastUtils.showToast(App.getAppContext(), "登录成功");
                        SharedPrefsUtil.putValue(App.getAppContext(), InitDatas.SP_NAME, InitDatas.UserPhone, userDto.getUserName());
                        SharedPrefsUtil.putValue(App.getAppContext(), InitDatas.SP_NAME, InitDatas.UserIsLogin, true);
                        EventBus.getDefault().post(new EventMessage(Constant.RefreshMine));//event更新用户名
                        ActivityUtil.doCallBack(me,getIntent().getExtras(),true);
                        XinYanOKLSDK.getInstance().finishActivity();
                    } else {
                        ToastUtils.showToast(App.getAppContext(),requestResult.getMessage());
                    }
                } catch (Exception e) {
                    ToastUtils.showToast(App.getAppContext(), Constant.ERROR_MSG);
                }
                hideProgressBar();
                break;
        }
    }

    @Override
    public void onError(int i) {
        dismissLoading();
        if (NetUtil.getNetWorkState(App.getAppContext())== NetUtil.NETWORK_NONE){
            ToastUtils.showToast(App.getAppContext(), "登录失败，请检查您的网络设置!");
        }else {
            if (i!=POST_APPLY){
                ToastUtils.showToast(App.getAppContext(), Constant.ERROR_MSG);
            }
        }
    }
}
