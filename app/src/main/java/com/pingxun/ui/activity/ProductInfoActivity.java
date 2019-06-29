package com.pingxun.ui.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pingxun.activity.R;
import com.pingxun.base.App;
import com.pingxun.http.ServerApi;
import com.pingxun.other.InitDatas;
import com.pingxundata.answerliu.pxcore.data.ServerModelObject;
import com.pingxundata.answerliu.pxcore.databinding.ActivityProductInfoBinding;
import com.pingxundata.answerliu.pxcore.view.EmptyLayout;
import com.pingxundata.pxcore.absactivitys.BaseProductDetailActivity;
import com.pingxundata.pxmeta.http.PXHttp;
import com.pingxundata.pxmeta.pojo.RequestResult;
import com.pingxundata.pxmeta.utils.ActivityUtil;
import com.pingxundata.pxmeta.utils.GlideImgManager;
import com.pingxundata.pxmeta.utils.MyTools;
import com.pingxundata.pxmeta.utils.NetUtil;
import com.pingxundata.pxmeta.utils.ObjectHelper;
import com.pingxundata.pxmeta.utils.SharedPrefsUtil;

import static com.pingxun.other.RequestFlag.GET_FIND_BY_ID;
import static com.pingxun.other.RequestFlag.POST_APPLY;


/**
 * Created by LH on 2017/8/31.
 * Describe:产品详情
 */
public class ProductInfoActivity extends BaseProductDetailActivity implements PXHttp.OnResultHandler, View.OnClickListener {

    private String mWebUrls;
    private int recommendFlag;
    private ActivityProductInfoBinding bindingView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设置竖屏
        bindingView = DataBindingUtil.setContentView(this, R.layout.activity_product_info);
        initData();
    }


    private void initData() {
        initTopView("产品详情");
        bindingView.ivShare.setVisibility(View.GONE);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            productId = bundle.getString(InitDatas.INFOR_ID);
            bindingView.emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            ServerApi.getProductInfo(ProductInfoActivity.this, productId);
        }
        initListener();
    }

    private void initListener() {
        bindingView.btnEnter.setOnClickListener(this);
        bindingView.emptyLayout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_enter://立即借款
                appName = InitDatas.APP_NAME;
                channelNo = InitDatas.CHANNEL_NO;
                applyArea = InitDatas.province + "/" + InitDatas.city + "/" + InitDatas.district;

                Bundle bundle = new Bundle();
                bundle.putString("url", mWebUrls);
                bundle.putString("productId", productId);
                bundle.putString("productName", productName);
                bundle.putString("appName", appName);
                bundle.putString("channelNo", channelNo);
                bundle.putString("applyArea", applyArea);

                startWebForRecommend(recommendFlag, bundle, R.mipmap.icon_back, Color.WHITE, R.color.tab_font_bright);
                break;
            case R.id.empty_layout://重连
                bindingView.emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                ServerApi.getProductInfo(ProductInfoActivity.this, productId);
                break;
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onResult(RequestResult requestResult, String jsonStr, int flag) {
        switch (flag) {
            case GET_FIND_BY_ID:
                try {
                    ServerModelObject productInfo;
                    if (requestResult.isSuccess()) {
                        productInfo = (ServerModelObject) requestResult.getEntityResult();
                        recommendFlag = ObjectHelper.isNotEmpty(productInfo.getRecommendFlag()) ? productInfo.getRecommendFlag() : 1;
                        mWebUrls = productInfo.getUrl();
                        productName = productInfo.getName();
                        bindingView.tvTitle.setText(productName);//产品名称
                        GlideImgManager.glideLoader(App.getAppContext(), productInfo.getImg(), R.mipmap.img_default, R.mipmap.img_default, bindingView.ivProduct, 1);//产品图片
                        bindingView.tvQuota.setText("额度范围   " + MyTools.initTvQuota(productInfo.getStartAmount(), productInfo.getEndAmount()));
                        bindingView.tvTime.setText("额度期限   " + productInfo.getStartPeriod() + "~" + productInfo.getEndPeriod() + productInfo.getPeriodTypeStr());
                        bindingView.tvSpeed.setText("最快放款   " + productInfo.getLoanDay() + "天");
                        bindingView.tvRateExplain.setText(productInfo.getRateMemo());
                        bindingView.tvProcess.setText(productInfo.getApplyFlow());
                        bindingView.tvCondition.setText(productInfo.getApplyCondition());
                        bindingView.tvDatum.setText(productInfo.getApplyMaterial());
                    } else {
                        ErrorIntent();
                    }
                } catch (Exception e) {
                    ErrorIntent();
                }
                bindingView.emptyLayout.setErrorType(EmptyLayout.NO_ERROR);
                break;

        }

    }


    @Override
    public void onError(int flag) {
        if (NetUtil.getNetWorkState(App.getAppContext()) == -1) {
            bindingView.emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        } else {
            if (flag != POST_APPLY) {
                bindingView.emptyLayout.setErrorType(EmptyLayout.ERROR);
            }
        }
    }

    /**
     * 初始化头部返回的View
     */
    private void initTopView(String titleStr) {
        RelativeLayout back = (RelativeLayout) findViewById(R.id.iv_topview_back);
        TextView title = (TextView) findViewById(R.id.tv_topview_title);
        if (back != null) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        if (title != null) {
            title.setText(titleStr);
        }
    }

    /**
     * 发生错误的处理
     */
    private void ErrorIntent() {
        SharedPrefsUtil.putValue(App.getAppContext(), InitDatas.SP_NAME, InitDatas.UserIsLogin, false);
        Bundle bundle = new Bundle();
        bundle.putString(InitDatas.INFOR_ID, productId);
        ActivityUtil.recallGoForward(this, LoginActivity.class, ProductInfoActivity.class, bundle, true);
    }


}
