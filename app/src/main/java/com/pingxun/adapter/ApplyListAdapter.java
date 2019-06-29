package com.pingxun.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pingxun.activity.R;
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
        helper.setText(R.id.tv_title, item.getProductName());
        helper.setText(R.id.tv_edu, "额度  " + MyTools.initTvQuota(item.getStartAmount(), item.getEndAmount()));
        helper.setText(R.id.tv_lilv, item.getPeriodType() + "利率  " + String.valueOf(item.getServiceRate()) + "%");
        GlideImgManager.glideLoader(mContext, item.getImg(), R.mipmap.img_default, R.mipmap.img_default, (ImageView) helper.getView(R.id.iv), 1);

//        FluidLayout flowLayout = helper.getView(R.id.flow_layout);
//        flowLayout.removeAllViews();
//        String[] sLabel;
//        if (!TextUtils.isEmpty(item.getProductLabel())) {
//            sLabel = item.getProductLabel().split(";");
//            for (int i = 0; i < sLabel.length; i++) {
//                final TextView view = new TextView(mContext);
//                view.setText(sLabel[i]);
//                view.setPadding(DensityUtils.dip2px(mContext, 20), DensityUtils.dip2px(mContext, 2), DensityUtils.dip2px(mContext, 20), DensityUtils.dip2px(mContext, 2));
//                view.setGravity(Gravity.CENTER);
//                view.setTextSize(12);
//                view.setBackgroundResource(R.drawable.shap_tag);
//                FluidLayout.LayoutParams params = new FluidLayout.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT);
//                params.setMargins(0, 0, DensityUtils.dip2px(mContext, 20), 0);
//                flowLayout.addView(view, params);
//            }
//        }
    }


}