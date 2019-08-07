package com.dbhh.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dbhh.bigwhiteflowers.R;
import com.dbhh.data.HomeMultipleItem;
import com.pingxundata.pxmeta.utils.GlideImgManager;

import java.util.List;

/**
 * Created by Administrator on 2017/8/19.
 */

public class HomeMultipleItemAdapter extends BaseMultiItemQuickAdapter<HomeMultipleItem, BaseViewHolder>{


    public HomeMultipleItemAdapter(Context context,List data) {
        super(data);
        addItemType(HomeMultipleItem.PRODUCT_TYPE_FIRST, R.layout.rv_item_fragment_home_class_large);
        addItemType(HomeMultipleItem.PRODUCT_TYPE_NEXT, R.layout.rv_item_fragment_home_class);

    }



    @Override
    protected void convert(com.chad.library.adapter.base.BaseViewHolder helper, HomeMultipleItem item) {
         switch (helper.getItemViewType()){
             case HomeMultipleItem.PRODUCT_TYPE_FIRST:
                 helper.setText(R.id.tv_f1_item_class_title,item.getName());
                 helper.setText(R.id.tv_f1_item_class_sub_title,item.getDescription());
                 GlideImgManager.glideLoader(mContext,item.getImg(), R.mipmap.img_default,R.mipmap.img_default,(ImageView)helper.getView(R.id.iv_f1_item_class));

                 break;
             case HomeMultipleItem.PRODUCT_TYPE_NEXT:
                 helper.setText(R.id.tv_f1_item_class_title,item.getName());
                 helper.setText(R.id.tv_f1_item_class_sub_title,item.getDescription());
                 GlideImgManager.glideLoader(mContext,item.getImg(), R.mipmap.img_default,R.mipmap.img_default,(ImageView)helper.getView(R.id.iv_f1_item_class));


                 break;
         }
    }
}
