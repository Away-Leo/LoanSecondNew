package com.pingxun.adapter;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dbhh.bigwhiteflowers.R;
import com.pingxun.data.CreditCardBean;
import com.pingxundata.pxmeta.utils.GlideImgManager;

import java.util.List;


/**
 * Created by Lh on 2017-08-03.
 * 申请列表adapter
 */

public class CreditCardAdapter extends BaseQuickAdapter<CreditCardBean, BaseViewHolder> {


    public CreditCardAdapter(int layoutResId, List<CreditCardBean> dataBeanList) {
        super(layoutResId, dataBeanList);

    }
    @Override
    protected void convert(BaseViewHolder helper, CreditCardBean item) {
        GlideImgManager.glideLoader(mContext,item.getImg(), R.mipmap.img_default,R.mipmap.img_default,(ImageView)helper.getView(R.id.iv),1);

        helper.setText(R.id.tv_title,item.getName());
        helper.setText(R.id.tv_sub_title,item.getCardDesc());
        SpannableString spannableString = new SpannableString("申请人数"+ String.valueOf(item.getClickNum())+"人");
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 4,spannableString.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        helper.setText(R.id.tv_num, spannableString);


    }


}