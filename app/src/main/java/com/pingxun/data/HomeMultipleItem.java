package com.pingxun.data;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by Administrator on 2017/8/19.
 */

public class HomeMultipleItem implements MultiItemEntity {
    public static final int PRODUCT_TYPE_FIRST = 1;
    public static final int PRODUCT_TYPE_NEXT = 2;
    private int itemType;
    private int spanSize;

    /**
     * 产品分类
     */
    private int id;
    private String name;
    private String description;
    private String img;
    private String code;
    /**
     * 信用卡推荐
     */
//  private int id;
//  private String name;
    private String icon;
    private String url;
    private boolean isValid;
    private boolean isRecommend;
    private int showOrder;
    private String memo;



    /**
     * 产品推荐
     */
//  private int id;
//  private String name;
//  private String img;
    private double serviceRate;
    private int startPeriod;
    private int endPeriod;
    private String periodType;
    private double startAmount;
    private double endAmount;
    //  private boolean isValid;
    private int viewNum;
    private int clickNum;
    private String productFlag;
    private String productLabel;
//  private boolean isRecommend;

    /**
     * 产品分类
     * @param itemType
     * @param spanSize
     * @param id
     * @param name
     * @param description
     * @param img
     * @param code
     */
    public HomeMultipleItem(int itemType, int spanSize, int id, String name, String description, String img, String code) {
        this.itemType = itemType;
        this.spanSize = spanSize;
        this.id = id;
        this.name = name;
        this.description = description;
        this.img = img;
        this.code = code;
    }





    public int getSpanSize() {
        return spanSize;
    }

    public void setSpanSize(int spanSize) {
        this.spanSize = spanSize;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isRecommend() {
        return isRecommend;
    }

    public void setRecommend(boolean recommend) {
        isRecommend = recommend;
    }

    public int getShowOrder() {
        return showOrder;
    }

    public void setShowOrder(int showOrder) {
        this.showOrder = showOrder;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public double getServiceRate() {
        return serviceRate;
    }

    public void setServiceRate(double serviceRate) {
        this.serviceRate = serviceRate;
    }

    public int getStartPeriod() {
        return startPeriod;
    }

    public void setStartPeriod(int startPeriod) {
        this.startPeriod = startPeriod;
    }

    public int getEndPeriod() {
        return endPeriod;
    }

    public void setEndPeriod(int endPeriod) {
        this.endPeriod = endPeriod;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    public double getStartAmount() {
        return startAmount;
    }

    public void setStartAmount(double startAmount) {
        this.startAmount = startAmount;
    }

    public double getEndAmount() {
        return endAmount;
    }

    public void setEndAmount(double endAmount) {
        this.endAmount = endAmount;
    }

    public int getViewNum() {
        return viewNum;
    }

    public void setViewNum(int viewNum) {
        this.viewNum = viewNum;
    }

    public int getClickNum() {
        return clickNum;
    }

    public void setClickNum(int clickNum) {
        this.clickNum = clickNum;
    }

    public String getProductFlag() {
        return productFlag;
    }

    public void setProductFlag(String productFlag) {
        this.productFlag = productFlag;
    }

    public String getProductLabel() {
        return productLabel;
    }

    public void setProductLabel(String productLabel) {
        this.productLabel = productLabel;
    }
    @Override
    public int getItemType() {
        return itemType;
    }
}
