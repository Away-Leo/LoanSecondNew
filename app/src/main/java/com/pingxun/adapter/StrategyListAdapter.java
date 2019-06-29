package com.pingxun.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pingxun.activity.R;
import com.pingxun.data.RaidersListBean;
import com.pingxundata.pxmeta.utils.GlideImgManager;

import java.util.List;

/**
 * Created by LH on 2017/8/4.
 * 信用卡Icon Adapter(九宫格)
 */

public class StrategyListAdapter extends BaseQuickAdapter<RaidersListBean,BaseViewHolder>{

    public StrategyListAdapter(int layoutResId, List<RaidersListBean> dataBeanList) {
        super(layoutResId, dataBeanList);

    }
    @Override
    protected void convert(BaseViewHolder helper, RaidersListBean item) {
        GlideImgManager.glideLoader(mContext,item.getCmsImg(), R.mipmap.img_default,R.mipmap.img_default,(ImageView) helper.getView(R.id.iv),1);
        helper.setText(R.id.tv_title,item.getTitle());
        helper.setText(R.id.tv_time,"发布时间  "+item.getPublishDate());
        helper.setText(R.id.tv_num, "浏览量  "+item.getClickNum());
    }
}
