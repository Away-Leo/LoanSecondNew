package com.pingxun.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dbhh.bigwhiteflowers.R;
import com.pingxundata.answerliu.pxcore.data.ApplyListBean;
import com.pingxundata.pxmeta.utils.GlideImgManager;
import com.pingxundata.pxmeta.utils.MyTools;

import java.util.List;


/**
 * Created by Lh on 2017-08-03.
 * 申请列表adapter
 */

public class ApplyListAdapter extends BaseQuickAdapter<ApplyListBean, BaseViewHolder> {


    public ApplyListAdapter(int layoutResId, List<ApplyListBean> dataBeanList) {
        super(layoutResId, dataBeanList);

    }

    @Override
    protected void convert(BaseViewHolder helper, ApplyListBean item) {
        GlideImgManager.glideLoader(mContext, item.getImg(), R.mipmap.img_default, R.mipmap.img_default, (ImageView) helper.getView(R.id.it_product_icon), 0);

        helper.setText(R.id.it_product_name, item.getProductName());
        helper.setText(R.id.it_product_range, MyTools.initTvQuota(item.getStartAmount(), item.getEndAmount()));
        helper.setText(R.id.it_product_rate_unit,item.getPeriodType() + "利率");
        helper.setText(R.id.it_product_range, String.valueOf(item.getServiceRate()) + "%");
        helper.setVisible(R.id.it_product_label, false);
    }


}