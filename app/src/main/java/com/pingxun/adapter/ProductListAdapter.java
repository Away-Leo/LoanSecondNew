package com.pingxun.adapter;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pingxun.activity.R;
import com.pingxun.data.ProductListMoreBean;
import com.pingxundata.answerliu.pxcore.data.ProductListBean;
import com.pingxundata.answerliu.pxcore.view.FluidLayout;
import com.pingxundata.pxmeta.utils.DensityUtils;
import com.pingxundata.pxmeta.utils.GlideImgManager;
import com.pingxundata.pxmeta.utils.MyTools;

import java.util.List;

/**
 * Created by Lh on 2017-08-03.
 * 产品列表adapter
 */

public class ProductListAdapter extends BaseQuickAdapter<ProductListMoreBean, BaseViewHolder> {


    public ProductListAdapter(int layoutResId, List<ProductListMoreBean> dataBeanList) {
        super(layoutResId, dataBeanList);
    }

    @Override
    protected void convert(BaseViewHolder helper, ProductListMoreBean item) {
        GlideImgManager.glideLoader(mContext, item.getImg(), R.mipmap.img_default, R.mipmap.img_default, (ImageView) helper.getView(R.id.iv), 1);

        helper.setText(R.id.tv_title, item.getName());
        helper.setText(R.id.tv_edu, MyTools.initTvQuota(item.getStartAmount(), item.getEndAmount()));
        helper.setText(R.id.tv_qixian, String.valueOf(item.getStartPeriod())+"~"+ String.valueOf(item.getEndPeriod())+item.getPeriodType());
        helper.setText(R.id.tv_lilv_danwei,item.getPeriodType() + "利率");
        helper.setText(R.id.tv_lilv, String.valueOf(item.getServiceRate()) + "%");
        SpannableString spannableString = new SpannableString("申请人数"+ String.valueOf(item.getClickNum())+"人");
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 4,spannableString.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        helper.setText(R.id.tv_apply_num, spannableString);

        if (item.getIsRecommend() == 1) {
            helper.setVisible(R.id.iv_tuijian, true);
        } else {
            helper.setVisible(R.id.iv_tuijian, false);
        }

        FluidLayout flowLayout = helper.getView(R.id.flow_layout);
        flowLayout.removeAllViews();
        String[] sLabel;
        if (!TextUtils.isEmpty(item.getProductLabel())) {
            sLabel = item.getProductLabel().split(";");
            for (int i = 0; i < sLabel.length; i++) {
                final TextView textView = new TextView(mContext);
                //设置textView  start
                textView.setText(sLabel[i]);
                textView.setPadding(DensityUtils.dip2px(mContext, 20), DensityUtils.dip2px(mContext, 2), DensityUtils.dip2px(mContext, 20), DensityUtils.dip2px(mContext, 2));
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(12);
                textView.setBackgroundResource(R.drawable.shap_tag);
                //设置textView  end

                FluidLayout.LayoutParams params = new FluidLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(12, 0, 0, 12);
                flowLayout.addView(textView, params);
            }
        }


    }


}