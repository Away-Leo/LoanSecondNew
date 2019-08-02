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
import com.dbhh.bigwhiteflowers.R;
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
        GlideImgManager.glideLoader(mContext, item.getImg(), R.mipmap.img_default, R.mipmap.img_default, (ImageView) helper.getView(R.id.it_product_icon), 0);

        helper.setText(R.id.it_product_name, item.getName());
        helper.setText(R.id.it_product_range, MyTools.initTvQuota(item.getStartAmount(), item.getEndAmount()));
        helper.setText(R.id.it_product_rate_unit,item.getPeriodType() + "利率");
        helper.setText(R.id.it_product_range, String.valueOf(item.getServiceRate()) + "%");
        helper.setText(R.id.it_product_label, item.getProductLabel());


    }


}