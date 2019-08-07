package com.dbhh.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dbhh.bigwhiteflowers.R;
import com.pingxundata.answerliu.pxcore.data.ServerModelList;
import com.pingxundata.pxmeta.utils.GlideImgManager;

import java.util.List;

/**
 * Created by LH on 2017/8/4.
 * 信用卡Icon Adapter(九宫格)
 */

public class StrategyTopAdapter extends BaseQuickAdapter<ServerModelList,BaseViewHolder>{

    public StrategyTopAdapter(int layoutResId, List<ServerModelList> dataBeanList) {
        super(layoutResId, dataBeanList);

    }
    @Override
    protected void convert(BaseViewHolder helper, ServerModelList item) {
        GlideImgManager.glideLoader(mContext,item.getCmsImg(), R.mipmap.img_default,R.mipmap.img_default,(ImageView) helper.getView(R.id.iv_icon),1);
        helper.setText(R.id.tv_icon,item.getTitle());
    }
}
