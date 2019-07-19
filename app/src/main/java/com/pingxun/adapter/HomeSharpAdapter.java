package com.pingxun.adapter;

import android.annotation.SuppressLint;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pingxun.activity.R;
import com.pingxun.data.RecommendSection;
import com.pingxundata.answerliu.pxcore.data.ServerModelList;
import com.pingxundata.pxmeta.utils.GlideImgManager;
import com.pingxundata.pxmeta.utils.MyTools;

import java.util.List;

/**
 * Created by Administrator on 2017/8/19.
 */

public class HomeSharpAdapter extends BaseSectionQuickAdapter<RecommendSection, BaseViewHolder> {


    public HomeSharpAdapter(int layoutResId, int sectionHeadResId, List<RecommendSection> data) {
        super(layoutResId, sectionHeadResId, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, RecommendSection item) {
        helper.setVisible(R.id.sharp_tv_more, item.isMore());
        helper.setText(R.id.sharp_tv_more, R.string.home_sharp);

    }

    @Override
    protected void convert(BaseViewHolder helper, RecommendSection item) {
        ServerModelList dataBean = item.t;
        GlideImgManager.glideLoader(mContext, dataBean.getImg(), R.mipmap.img_default, R.mipmap.img_default, (ImageView) helper.getView(R.id.sharp_iv), 0);

        helper.setText(R.id.sharp_title, dataBean.getName());
        helper.setText(R.id.sharp_prorange, MyTools.initTvQuota(dataBean.getStartAmount(), dataBean.getEndAmount()));
        helper.setText(R.id.sharp_prolable, dataBean.getProductLabel());

    }
}
