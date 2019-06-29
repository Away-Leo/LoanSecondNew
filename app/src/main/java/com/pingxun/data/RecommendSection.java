package com.pingxun.data;


import com.chad.library.adapter.base.entity.SectionEntity;
import com.pingxundata.answerliu.pxcore.data.ServerModelList;

/**
 * Created by Administrator on 2017/8/19.
 */

public class RecommendSection extends SectionEntity<ServerModelList> {
    private boolean isMore;
    public RecommendSection(boolean isHeader, String header, boolean isMroe) {
        super(isHeader, header);
        this.isMore = isMroe;
    }

    public RecommendSection(ServerModelList dataBean) {
        super(dataBean);
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean mroe) {
        isMore = mroe;
    }
}
