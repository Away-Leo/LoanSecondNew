package com.dbhh.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dbhh.bigwhiteflowers.R;
import com.dbhh.data.RecommendSection;
import com.dbhh.other.GetResourcesUtils;
import com.pingxundata.answerliu.pxcore.data.ServerModelList;
import com.pingxundata.pxmeta.utils.GlideImgManager;
import com.pingxundata.pxmeta.utils.MyTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/19.
 */

public class HomeHotAdapter extends BaseSectionQuickAdapter<RecommendSection, BaseViewHolder> {

    List<String> shaps;

    public HomeHotAdapter(int layoutResId, int sectionHeadResId, List<RecommendSection> data) {
        super(layoutResId, sectionHeadResId, data);
        shaps = new ArrayList<>(3);
        shaps.add("1");
        shaps.add("2");
        shaps.add("3");
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

        if (shaps.size() == 0) {
            shaps.add("3");
            shaps.add("2");
            shaps.add("1");
        }
        String drawName = "shap" + shaps.get(0) + "";
        shaps.remove(0);

        helper.setBackgroundRes(R.id.hot_container, GetResourcesUtils.getDrawableId(mContext, drawName));
        helper.setText(R.id.hot_proname, dataBean.getName());
        helper.setText(R.id.hot_prorange, MyTools.initTvQuota(dataBean.getStartAmount(), dataBean.getEndAmount()));

    }
}
