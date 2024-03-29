package com.dbhh.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dbhh.bigwhiteflowers.R;
import com.dbhh.data.RecommendSection;
import com.pingxundata.answerliu.pxcore.data.ServerModelList;
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
        GlideImgManager.glideLoader(mContext, dataBean.getImg(), R.mipmap.img_default, R.mipmap.img_default, (ImageView) helper.getView(R.id.it_product_icon), 0);

        helper.setText(R.id.it_product_name, dataBean.getName());
        helper.setText(R.id.it_product_range, MyTools.initTvQuota(dataBean.getStartAmount(), dataBean.getEndAmount()));
        helper.setText(R.id.it_product_rate_unit,dataBean.getPeriodType() + "利率");
        helper.setText(R.id.it_product_range, String.valueOf(dataBean.getServiceRate()) + "%");
        helper.setText(R.id.it_product_label, dataBean.getProductLabel());
    }
}
