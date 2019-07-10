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

public class HomeHotAdapter extends BaseSectionQuickAdapter<RecommendSection, BaseViewHolder> {


    public HomeHotAdapter(int layoutResId, int sectionHeadResId, List<RecommendSection> data) {
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
        GlideImgManager.glideLoader(mContext, dataBean.getImg(), R.mipmap.img_default, R.mipmap.img_default, (ImageView) helper.getView(R.id.hot_propic), 1);

        helper.setText(R.id.hot_proname, dataBean.getName());
        helper.setText(R.id.hot_prorange, MyTools.initTvQuota(dataBean.getStartAmount(), dataBean.getEndAmount()));

    }
}
