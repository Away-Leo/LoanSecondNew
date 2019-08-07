package com.dbhh.ui.activity;


import com.dbhh.bigwhiteflowers.R;
import com.dbhh.base.App;
import com.dbhh.base.BaseActivity;
import com.dbhh.other.InitDatas;
import com.pingxundata.answerliu.pxbanner.PXBanner;
import com.pingxundata.answerliu.pxcore.databinding.ActivityGuidBinding;
import com.pingxundata.pxmeta.utils.ActivityUtil;
import com.pingxundata.pxmeta.utils.SharedPrefsUtil;


/**
 * 引导页
 */
public class GuidActivity extends BaseActivity<ActivityGuidBinding> {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_guid;
    }

    @Override
    protected void initData() {
       
        SharedPrefsUtil.putValue(App.getAppContext(), InitDatas.SP_NAME, InitDatas.UserIsFirstSplash, true);//第一次进入APP将值设为true
        bindingView.bannerGuideBackground.setEnterSkipViewIdAndDelegate(R.id.tv_guide_enter, R.id.tv_guide_skip, new PXBanner.GuideDelegate() {
            @Override
            public void onClickEnterOrSkip() {
                ActivityUtil.goForward(me,MainActivity.class,true);
            }
        });
        bindingView.bannerGuideBackground.setData(R.mipmap.guid_1,R.mipmap.guid_2,R.mipmap.guid_3);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindingView.bannerGuideBackground.setBackgroundResource(android.R.color.white);
    }
}
