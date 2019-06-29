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

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pingxun.activity.R;
import com.pingxun.data.RecommendSection;
import com.pingxundata.answerliu.pxcore.data.ServerModelList;
import com.pingxundata.answerliu.pxcore.view.FluidLayout;
import com.pingxundata.pxmeta.utils.DensityUtils;
import com.pingxundata.pxmeta.utils.GlideImgManager;
import com.pingxundata.pxmeta.utils.MyTools;

import java.util.List;

/**
 * Created by Administrator on 2017/8/19.
 */

public class HomeRecommendAdapter extends BaseSectionQuickAdapter<RecommendSection, BaseViewHolder> {


    public HomeRecommendAdapter(int layoutResId, int sectionHeadResId, List<RecommendSection> data) {
        super(layoutResId, sectionHeadResId, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, RecommendSection item) {
        helper.setText(R.id.title, item.header);
        helper.setVisible(R.id.tv_more, item.isMore());

    }

    @Override
    protected void convert(BaseViewHolder helper, RecommendSection item) {
        ServerModelList dataBean = item.t;
        GlideImgManager.glideLoader(mContext, dataBean.getImg(), R.mipmap.img_default, R.mipmap.img_default, (ImageView) helper.getView(R.id.iv), 1);

        helper.setText(R.id.tv_title, dataBean.getName());
        helper.setText(R.id.tv_edu, MyTools.initTvQuota(dataBean.getStartAmount(), dataBean.getEndAmount()));
        helper.setText(R.id.tv_qixian, String.valueOf(dataBean.getStartPeriod())+"~"+ String.valueOf(dataBean.getEndPeriod())+dataBean.getPeriodType());
        helper.setText(R.id.tv_lilv_danwei,dataBean.getPeriodType() + "利率");
        helper.setText(R.id.tv_lilv, String.valueOf(dataBean.getServiceRate()) + "%");
        SpannableString spannableString = new SpannableString("申请人数"+ String.valueOf(dataBean.getClickNum())+"人");
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 4,spannableString.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        helper.setText(R.id.tv_apply_num, spannableString);

        if (dataBean.getIsRecommend() == 1) {
            helper.setVisible(R.id.iv_tuijian, true);
        } else {
            helper.setVisible(R.id.iv_tuijian, false);
        }

        FluidLayout flowLayout = helper.getView(R.id.flow_layout);
        flowLayout.removeAllViews();
        String[] sLabel;
        if (!TextUtils.isEmpty(dataBean.getProductLabel())) {
            sLabel = dataBean.getProductLabel().split(";");
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
