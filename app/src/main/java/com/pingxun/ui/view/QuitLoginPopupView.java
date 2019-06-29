package com.pingxun.ui.view;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.pingxun.activity.R;
import com.pingxun.other.InitDatas;
import com.pingxun.other.Urls;
import com.pingxundata.answerliu.pxcore.data.LoginBean;
import com.pingxundata.answerliu.pxcore.other.Constant;
import com.pingxundata.answerliu.pxcore.other.EventMessage;
import com.pingxundata.pxmeta.http.PXHttp;
import com.pingxundata.pxmeta.pojo.RequestResult;
import com.pingxundata.pxmeta.utils.SharedPrefsUtil;
import com.pingxundata.pxmeta.views.basepopupview.BasePopupWindow;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by LH on 2017/8/6.
 * 退出登录Dialog
 */

public class QuitLoginPopupView extends BasePopupWindow implements View.OnClickListener,PXHttp.OnResultHandler{

    private static final int LOGOUT=1;//退出登录
    public QuitLoginPopupView(Activity context) {
        super(context);
        TextView ok = (TextView) findViewById(R.id.tv_right);
        TextView cancel = (TextView) findViewById(R.id.tv_left);
        setViewClickListener(this, ok, cancel);
    }

    @Override
    protected Animation initShowAnimation() {
        return null;
    }


    @Override
    public View onCreatePopupView() {
        return createPopupById(R.layout.dialog_quit_login);
    }

    @Override
    public View initAnimaView() {
        return findViewById(R.id.popup_anima);
    }

//    @Override
//    protected Animation initShowAnimation() {
//        AnimationSet set=new AnimationSet(false);
//        Animation shakeAnima=new RotateAnimation(0,15,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
//        shakeAnima.setInterpolator(new CycleInterpolator(5));
//        shakeAnima.setDuration(400);
//        set.addAnimation(getDefaultAlphaAnimation());
//        set.addAnimation(shakeAnima);
//        return set;
//    }

    @Override
    public View getClickToDismissView() {
        return getPopupWindowView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_left:
                dismiss();
                break;
            case R.id.tv_right:
                PXHttp.getInstance().setHandleInterface(this).getJson(Urls.URL_POST_LOGOUT, null, LOGOUT, LoginBean.class);
                SharedPrefsUtil.remove(getContext(), InitDatas.SP_NAME,InitDatas.UserIsLogin);
                SharedPrefsUtil.remove(getContext(),InitDatas.SP_NAME, InitDatas.UserIsFirstSplash);
                SharedPrefsUtil.remove(getContext(),InitDatas.SP_NAME, InitDatas.UserPw);
                EventBus.getDefault().post(new EventMessage(Constant.RefreshMine));//eventBus更新用户名
                dismiss();
                break;
            default:
                break;
        }
    }


    @Override
    public void onResult(RequestResult requestResult, String jsonStr, int flag) {
        switch (flag){
            case LOGOUT:
                break;
        }
    }

    @Override
    public void onError(int flag) {

    }
}
