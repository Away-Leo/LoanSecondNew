package com.pingxun.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pingxun.activity.R;
import com.pingxundata.answerliu.pxcore.data.ServerModelList;
import com.pingxundata.pxmeta.utils.GlideImgManager;

import java.util.List;


/**
 * Created by Lh on 2017-08-03.
 * 申请列表adapter
 */

public class BankAdapter extends BaseQuickAdapter<ServerModelList, BaseViewHolder> {


    public BankAdapter(int layoutResId, List<ServerModelList> dataBeanList) {
        super(layoutResId, dataBeanList);
    }

    @Override
    protected void convert(BaseViewHolder helper, ServerModelList item) {
        GlideImgManager.glideLoader(mContext,item.getIcon(), R.mipmap.img_default,R.mipmap.img_default,(ImageView) helper.getView(R.id.iv_icon),1);
        helper.setText(R.id.tv_name,item.getName());
    }


}