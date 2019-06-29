package com.pingxun.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.pingxun.activity.R;
import com.pingxundata.pxcore.absactivitys.PXRecommendActivity;
import com.pingxundata.pxcore.absactivitys.PXSimpleWebViewActivity;
import com.pingxundata.pxcore.applications.BaseApplication;
import com.pingxundata.pxmeta.utils.ActivityUtil;
import com.pingxundata.pxmeta.utils.ObjectHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.Iterator;
import java.util.List;


/**
 * Activity基类
 */
public abstract class BaseActivity<SV extends ViewDataBinding> extends AppCompatActivity{


    protected String TAG;
    protected Activity me;
    // 布局view
    protected SV bindingView;

    public static final int WEBVIEW_RESULT = 2005;
    public String productName;
    public String productId;
    public String appName;
    public String channelNo;
    public String applyArea;
    public String sourceProductId = "";
    int backImg;
    int titleColor;
    int topBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG = this.getClass().getSimpleName();
        this.me = this;//引用me自己，便于子类调用


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设置竖屏
        bindingView = DataBindingUtil.setContentView(me, getLayoutId());
        initData();

    }


    /**
     * 获取根布局
     * @return 布局ID
     */
    protected abstract int getLayoutId();

    /**
     * 加载数据
     */
    protected abstract void initData();

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    /**
     * 初始化头部返回的View
     */
    public void initTopView(String titleStr) {
        RelativeLayout back = (RelativeLayout)findViewById(R.id.iv_topview_back);
        TextView title = (TextView) findViewById(R.id.tv_topview_title);
        if (back != null) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        if (title != null) {
            title.setText(titleStr);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }

    private ProgressDialog dialog;


    public void showLoading() {
        if (dialog != null && dialog.isShowing()) return;
        dialog = new ProgressDialog(me);
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

    public void startWebForRecommend(int recommendCd, Bundle dataBundle, int backImg, int titleColor, int topBack) {
        this.productName = dataBundle.getString("productName");
        this.productId = dataBundle.getString("productId");
        this.appName = dataBundle.getString("appName");
        this.sourceProductId = ObjectHelper.isNotEmpty(this.getIntent().getExtras())?this.getIntent().getExtras().getString("sourceProductId"):"";
        this.backImg = backImg;
        this.titleColor = titleColor;
        this.topBack = topBack;
        dataBundle.putInt("backImg", this.backImg);
        dataBundle.putInt("titleColor", this.titleColor);
        dataBundle.putInt("topBack", this.topBack);
        dataBundle.putString("sourceProductId", this.sourceProductId);
        this.closeRecommendActivity();
        BaseApplication.addActivity(this);
        if(ObjectHelper.isNotEmpty(Integer.valueOf(recommendCd)) && recommendCd == 0) {
            ActivityUtil.goForward(this, PXSimpleWebViewActivity.class, 2005, dataBundle);
        } else {
            ActivityUtil.goForward(this, PXSimpleWebViewActivity.class, dataBundle, true);
        }

    }

    private void closeRecommendActivity() {
        try {
            List<Activity> allActivitys = BaseApplication.activitys;
            if(ObjectHelper.isNotEmpty(allActivitys)) {
                Iterator var2 = allActivitys.iterator();

                while(true) {
                    Activity temp;
                    do {
                        if(!var2.hasNext()) {
                            return;
                        }

                        temp = (Activity)var2.next();
                    } while(!temp.getClass().getName().equalsIgnoreCase(PXRecommendActivity.class.getName()) && !temp.getClass().getName().equalsIgnoreCase(this.getClass().getName()));

                    temp.finish();
                }
            }
        } catch (Exception var4) {
            Log.e("1001001", "立即申请关闭推荐activity出错", var4);
        }

    }
}
