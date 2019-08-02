package com.pingxun.ui.fragment;

import android.annotation.SuppressLint;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.dbhh.bigwhiteflowers.R;
import com.pingxun.base.BaseFragment;
import com.dbhh.bigwhiteflowers.databinding.FragmentMineBinding;
import com.pingxun.other.InitDatas;
import com.pingxun.ui.activity.AboutUsActivity;
import com.pingxun.ui.activity.ApplyListActivity;
import com.pingxun.ui.activity.LoginActivity;
import com.pingxun.ui.activity.MainActivity;
import com.pingxun.ui.view.QuitLoginPopupView;
import com.pingxundata.answerliu.pxcore.other.Constant;
import com.pingxundata.answerliu.pxcore.other.EventMessage;
import com.pingxundata.answerliu.pxcore.view.ContactUsPopupView;
import com.pingxundata.pxmeta.utils.ActivityUtil;
import com.pingxundata.pxmeta.utils.AppUtils;
import com.pingxundata.pxmeta.utils.NetUtil;
import com.pingxundata.pxmeta.utils.SharedPrefsUtil;
import com.pingxundata.pxmeta.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;



/**
 * Created by LH on 2017/8/12.
 * 我的fragment
 */

public class MineFragment extends BaseFragment<FragmentMineBinding> implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener {




    @Override
    protected int getRootLayoutResID() {
        EventBus.getDefault().register(this);//绑定事件接受
        return R.layout.fragment_mine;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//注销事件接受
    }

    @Override
    protected void initData() {
        bindingView.swipeLayout.setColorSchemeResources(R.color.tab_font_bright);
        bindingView.swipeLayout.setOnRefreshListener(this);
        bindingView.mineLogin.setOnClickListener(this);
        bindingView.mineApply.setOnClickListener(this);
        bindingView.mineAboutUs.setOnClickListener(this);
        bindingView.mineContactUs.setOnClickListener(this);
        bindingView.btnQuit.setOnClickListener(this);
        onRefresh();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mine_login://登录
                if (!SharedPrefsUtil.getValue(mActivity, InitDatas.SP_NAME, InitDatas.UserIsLogin, false)) {
                    ActivityUtil.recallGoForward(mActivity,LoginActivity.class,MainActivity.class,null,false);
                } else {
                    ToastUtils.showToast(mActivity, "您已登录");
                }
                break;

            case R.id.mine_apply://申请记录
                if (NetUtil.getNetWorkState(mActivity) != NetUtil.NETWORK_NONE) {
                    if (!SharedPrefsUtil.getValue(mActivity, InitDatas.SP_NAME, InitDatas.UserIsLogin, false)) {
                        ActivityUtil.recallGoForward(mActivity,LoginActivity.class,ApplyListActivity.class,null,false);
                    } else {
                        ActivityUtil.goForward(mActivity, ApplyListActivity.class, null, false);
                    }
                } else {
                    ToastUtils.showToast(mActivity, "网络未连接，请检查您的网络设置!");
                }
                break;

            case R.id.mine_about_us://关于我们
                ActivityUtil.goForward(mActivity, AboutUsActivity.class, null, false);
                break;
            case R.id.mine_contact_us://联系我们
                ContactUsPopupView contactUsPopupView = new ContactUsPopupView(mActivity);
                contactUsPopupView.setPopupWindowFullScreen(true);
                contactUsPopupView.showPopupWindow();
                break;
            case R.id.btn_quit://退出登录
                QuitLoginPopupView quitLoginPopupView = new QuitLoginPopupView(mActivity);
                quitLoginPopupView.setPopupWindowFullScreen(true);
                quitLoginPopupView.showPopupWindow();
                break;
        }
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(EventMessage message) {
        if (message.message.equals(Constant.RefreshMine)) {
            onRefresh();
        }
    }


    @Override
    public void onRefresh() {
        bindingView.swipeLayout.setRefreshing(true);
        bindingView.swipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                initView();
                bindingView.swipeLayout.setRefreshing(false);
            }
        },InitDatas.waitTime);
    }


    @SuppressLint("SetTextI18n")
    private void initView() {
        bindingView.tvVersion.setText("版本号" + AppUtils.getVersionName(mActivity).replaceAll("_","."));
        if (!SharedPrefsUtil.getValue(mActivity, InitDatas.SP_NAME, InitDatas.UserIsLogin, false)) {
            bindingView.tvName.setText("请登录");
            bindingView.btnQuit.setVisibility(View.GONE);
        } else {
            String phoneStr = SharedPrefsUtil.getValue(mActivity, InitDatas.SP_NAME, InitDatas.UserPhone, "");
            String inputStr = phoneStr.substring(0, 4) + "****" + phoneStr.substring(8);
            bindingView.tvName.setText(inputStr);
            bindingView.btnQuit.setVisibility(View.VISIBLE);
        }
    }


}
