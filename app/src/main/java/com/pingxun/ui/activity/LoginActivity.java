package com.pingxun.ui.activity;

import android.view.View;

import com.dbhh.bigwhiteflowers.R;
import com.pingxun.base.App;
import com.pingxun.base.BaseActivity;
import com.pingxun.http.ServerApi;
import com.pingxun.other.GetResourcesUtils;
import com.pingxun.other.InitDatas;
import com.pingxundata.answerliu.pxcore.databinding.ActivityLoginBinding;
import com.pingxundata.answerliu.pxcore.other.Constant;
import com.pingxundata.answerliu.pxcore.other.EventMessage;
import com.pingxundata.pxmeta.http.PXHttp;
import com.pingxundata.pxmeta.pojo.RequestResult;
import com.pingxundata.pxmeta.utils.ActivityUtil;
import com.pingxundata.pxmeta.utils.CountDownUtil;
import com.pingxundata.pxmeta.utils.MyTools;
import com.pingxundata.pxmeta.utils.NetUtil;
import com.pingxundata.pxmeta.utils.SharedPrefsUtil;
import com.pingxundata.pxmeta.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import static com.pingxun.other.RequestFlag.GET_CODE;
import static com.pingxun.other.RequestFlag.POST_APPLY;
import static com.pingxun.other.RequestFlag.POST_LOGIN;


/**
 * 登录界面
 */
public class LoginActivity extends BaseActivity<ActivityLoginBinding> implements PXHttp.OnResultHandler ,View.OnClickListener{

    private boolean isAgree = true;//是否同意协议标识
    private String sCode;//验证码
    private String sPhone;//电话号码


    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initData() {
        initTopView("登录");
        if (!SharedPrefsUtil.getValue(App.getAppContext(), InitDatas.SP_NAME, InitDatas.UserPhone,"666").equals("666")){
            bindingView.edPhone.setText(SharedPrefsUtil.getValue(App.getAppContext(), InitDatas.SP_NAME, InitDatas.UserPhone,""));
            bindingView.edPhone.setSelection(bindingView.edPhone.getText().length());//将光标移到末尾
        }
        initListener();
        bindingView.btnLogin.setBackground(getDrawable(GetResourcesUtils.getDrawableId(this, "shap_blue_strok")));

    }

    private void initListener() {
        bindingView.tvGetCode.setOnClickListener(this);
        bindingView.btnLogin.setOnClickListener(this);
        bindingView.ivChoose.setOnClickListener(this);
        bindingView.tvAgreement.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
            sPhone = MyTools.getEdittextStr(bindingView.edPhone);
            sCode = MyTools.getEdittextStr(bindingView.edCode);
            switch (v.getId()) {
                case R.id.tv_get_code://获取验证码
                    if (!MyTools.isEmpty(sPhone)) {
                        ToastUtils.showToast(App.getAppContext(), "手机号不能为空!");
                        break;
                    }
                    if (sPhone.length() != 11) {
                        ToastUtils.showToast(App.getAppContext(), "请输入11位手机号码!");
                        break;
                    }
                    showLoading();
                    ServerApi.postCode(LoginActivity.this,sPhone);
                    CountDownUtil countDownUtil = new CountDownUtil(60000, 1000, bindingView.tvGetCode);
                    countDownUtil.start();
                    break;

                case R.id.btn_login://登录
                    if (!MyTools.isEmpty(sPhone)) {
                        ToastUtils.showToast(App.getAppContext(), "手机号不能为空!");
                        break;
                    }
                    if (!MyTools.isEmpty(sCode)) {
                        ToastUtils.showToast(App.getAppContext(), "验证码不能为空!");
                        break;
                    }
                    if (sPhone.length() != 11) {
                        ToastUtils.showToast(App.getAppContext(), "请输入11位手机号码!");
                        break;
                    }
                    showLoading();
                    ServerApi.postLogin(LoginActivity.this,sPhone, sCode);
                    break;

                case R.id.iv_choose://同意协议选项
                    if (isAgree) {
                        bindingView.ivChoose.setBackgroundResource(R.mipmap.icon_agree_no);
                        bindingView.btnLogin.setEnabled(false);
                        bindingView.btnLogin.setBackgroundResource(R.drawable.shap_dis_login);
                        isAgree = false;
                    } else {
                        bindingView.ivChoose.setBackgroundResource(R.mipmap.icon_agree_yes);
                        bindingView.btnLogin.setEnabled(true);
                        bindingView.btnLogin.setBackgroundResource(R.drawable.shap_login);
                        isAgree = true;
                    }
                    break;
                case R.id.tv_agreement://注册协议跳转
                    ActivityUtil.goForward(me, RegistrationProActivity.class, null, false);
                    break;
            }
        }



    @Override
    public void onResult(RequestResult requestResult, String jsonStr, int flag) {
        switch (flag) {
            case GET_CODE://获取验证码
                dismissLoading();
                try {
                    if (requestResult.isSuccess()) {
                        ToastUtils.showToast(App.getAppContext(), "获取验证码成功，请注意查收短信!");
                    } else {
                        ToastUtils.showToast(App.getAppContext(), "获取验证码失败，请稍后再试!");
                    }
                } catch (Exception e) {
                    ToastUtils.showToast(App.getAppContext(), Constant.ERROR_MSG);
                }
                break;
            case POST_LOGIN://登录
                dismissLoading();
                try {
                    if (requestResult.isSuccess()) {
                        ToastUtils.showToast(App.getAppContext(), "登录成功");
                        SharedPrefsUtil.putValue(App.getAppContext(), InitDatas.SP_NAME, InitDatas.UserPhone, sPhone);
                        SharedPrefsUtil.putValue(App.getAppContext(), InitDatas.SP_NAME, InitDatas.UserPw, sCode);
                        SharedPrefsUtil.putValue(App.getAppContext(), InitDatas.SP_NAME, InitDatas.UserIsLogin, true);
                        EventBus.getDefault().post(new EventMessage(Constant.RefreshMine));//event更新用户名
                        ActivityUtil.doCallBack(me,getIntent().getExtras(),true);
                    } else {
                        ToastUtils.showToast(App.getAppContext(),requestResult.getMessage());
                    }
                } catch (Exception e) {
                    ToastUtils.showToast(App.getAppContext(), Constant.ERROR_MSG);
                }
                break;

        }
    }

    @Override
    public void onError(int flag) {
        dismissLoading();
        if (NetUtil.getNetWorkState(App.getAppContext())== NetUtil.NETWORK_NONE){
            ToastUtils.showToast(App.getAppContext(), "登录失败，请检查您的网络设置!");
        }else {
            if (flag!=POST_APPLY){
                ToastUtils.showToast(App.getAppContext(), Constant.ERROR_MSG);
            }
        }
    }

}
