package com.pingxun.http;


import android.content.Context;

import com.pingxun.data.CreditCardBean;
import com.pingxun.data.ProductListMoreBean;
import com.pingxun.data.RaidersListBean;
import com.pingxun.other.InitDatas;
import com.pingxun.other.Urls;
import com.pingxundata.answerliu.pxcore.data.ApplyListBean;
import com.pingxundata.answerliu.pxcore.data.LoginBean;
import com.pingxundata.answerliu.pxcore.data.ProductListBean;
import com.pingxundata.answerliu.pxcore.data.ServerModelList;
import com.pingxundata.answerliu.pxcore.data.ServerModelObject;
import com.pingxundata.pxmeta.http.PXHttp;
import com.pingxundata.pxmeta.utils.AppUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.pingxun.other.RequestFlag.GET_APP_MODULE;
import static com.pingxun.other.RequestFlag.GET_BANK;
import static com.pingxun.other.RequestFlag.GET_BANNER;
import static com.pingxun.other.RequestFlag.GET_CODE;
import static com.pingxun.other.RequestFlag.GET_FIND_BY_ID;
import static com.pingxun.other.RequestFlag.GET_FIND_BY_STRATEGY_ID;
import static com.pingxun.other.RequestFlag.GET_FIND_BY_TYPE;
import static com.pingxun.other.RequestFlag.GET_FIND_PARAMETER;
import static com.pingxun.other.RequestFlag.GET_HEAD_LINES;
import static com.pingxun.other.RequestFlag.GET_LIST;
import static com.pingxun.other.RequestFlag.GET_PRODUCT_HOT;
import static com.pingxun.other.RequestFlag.GET_PRODUCT_RECOMMEND;
import static com.pingxun.other.RequestFlag.GET_PRODUCT_SHARP;
import static com.pingxun.other.RequestFlag.GET_PRODUCT_TYPE;
import static com.pingxun.other.RequestFlag.GET_TOP;
import static com.pingxun.other.RequestFlag.GET_TU;
import static com.pingxun.other.RequestFlag.GET_WX_BANNER;
import static com.pingxun.other.RequestFlag.GET_ZS_TYPE;
import static com.pingxun.other.RequestFlag.POST_APPLY_CREDIT_CARD;
import static com.pingxun.other.RequestFlag.POST_LOGIN;


public class ServerApi {

    /**
     * 获取首页banner
     */
    public static void getBanner(Context context, PXHttp.OnResultHandler handler) {
        Map<String, String> params = new HashMap<>();
        params.put("position", InitDatas.APP_NAME + "_HomePage_TopBanner");
        params.put("versionNo", InitDatas.APP_NAME + AppUtils.getVersionCode(context));
        PXHttp.getInstance().setHandleInterface(handler).getJson(Urls.URL_GET_BANNER, params, GET_BANNER, ServerModelList.class);

    }


//    /**
//     * 获取首页产品分类
//     */
//    public static void getProductType(PXHttp.OnResultHandler handler) {
//        Map<String, String> params = new HashMap<>();
//        params.put("appName", InitDatas.APP_NAME);
//        PXHttp.getInstance().setHandleInterface(handler).getJson(Urls.URL_GET_PRODUCT_TYPE, params, GET_PRODUCT_TYPE,ServerModelList.class);
//    }

    /**
     * 获取热门推荐产品
     */
    public static void getProductRecommend(PXHttp.OnResultHandler handler,Context context) {
        Map<String, String> params = new HashMap<>();
        params.put("appName", InitDatas.APP_NAME);
        params.put("isRecommend", "1");
        params.put("channelNo", InitDatas.CHANNEL_NO);
        params.put("versionNo", InitDatas.APP_NAME + AppUtils.getVersionCode(context));
        PXHttp.getInstance().setHandleInterface(handler).getJson(Urls.URL_GET_PRODUCT_RECOMMEND, params, GET_PRODUCT_RECOMMEND,ServerModelList.class);
    }
    /**
     * 获取最新口子
     */
    public static void getProductHot(PXHttp.OnResultHandler handler,Context context) {
        Map<String, String> params = new HashMap<>();
        params.put("appName", InitDatas.APP_NAME);
        params.put("isRecommend", "3");
        params.put("channelNo", InitDatas.CHANNEL_NO);
        params.put("versionNo", InitDatas.APP_NAME + AppUtils.getVersionCode(context));
        PXHttp.getInstance().setHandleInterface(handler).getJson(Urls.URL_GET_PRODUCT_RECOMMEND, params, GET_PRODUCT_HOT,ServerModelList.class);
    }
    /**
     * 获取精品推荐
     */
    public static void getProductSharp(PXHttp.OnResultHandler handler,Context context) {
        Map<String, String> params = new HashMap<>();
        params.put("appName", InitDatas.APP_NAME);
        params.put("isRecommend", "2");
        params.put("channelNo", InitDatas.CHANNEL_NO);
        params.put("versionNo", InitDatas.APP_NAME + AppUtils.getVersionCode(context));
        PXHttp.getInstance().setHandleInterface(handler).getJson(Urls.URL_GET_PRODUCT_RECOMMEND, params, GET_PRODUCT_SHARP,ServerModelList.class);
    }


    /**
     * 获取贷款参数
     */
    public static void getParameter(PXHttp.OnResultHandler handler) {
        PXHttp.getInstance().setHandleInterface(handler).getJson(Urls.URL_GET_FIND_PARAMETER, null, GET_FIND_PARAMETER, ServerModelObject.class);
    }


    /**
     * 获取申请列表
     *
     * @param page   页码
     * @param status 状态:下拉刷新或者上拉加载
     */
    public static void getApplyList(PXHttp.OnResultHandler handler, int page, int status) {
        HashMap<String, String> params = new HashMap<>();
        params.put("pageNo", page + "");
        params.put("appName", InitDatas.APP_NAME);
        PXHttp.getInstance().setHandleInterface(handler).getJson(Urls.URL_POST_FIND_APPLY_LIST, params, status,ApplyListBean.class);
    }


    /**
     * 获取详情
     */
    public static void getProductInfo(PXHttp.OnResultHandler handler, String sId) {
        Map<String, String> params = new HashMap<>();
        params.put("id", sId);
        PXHttp.getInstance().setHandleInterface(handler).getJson(Urls.URL_GET_FIND_BY_ID, params, GET_FIND_BY_ID,ServerModelObject.class);
    }




    /**
     * 获取攻略详情
     */
    public static void getStrategyInfo(PXHttp.OnResultHandler handler,String sId) {
        Map<String, String> params = new HashMap<>();
        params.put("id", sId);
        PXHttp.getInstance().setHandleInterface(handler).getJson(Urls.URL_GET_CMS_FIND_BY_ID, params, GET_FIND_BY_STRATEGY_ID,ServerModelObject.class);
    }

//    /**
//     * 获取头条
//     */
//    public static void getHeadlines(PXHttp.OnResultHandler handler) {
//        PXHttp.getInstance().setHandleInterface(handler).getJson(Urls.URL_GET_HEAD_LINES, null, GET_HEAD_LINES,ServerModelList.class);
//    }

    /**
     * 获取单独图片
     */
    public static void getTu(PXHttp.OnResultHandler handler) {
        Map<String, String> params = new HashMap<>();
        params.put("position", InitDatas.APP_NAME+"_below_banner");
        PXHttp.getInstance().setHandleInterface(handler).getJson(Urls.URL_GET_BANNER, params, GET_TU,ServerModelList.class);
    }


    /**
     * 获取攻略List
     */
    public static void getStrategyList(PXHttp.OnResultHandler handler) {
        PXHttp.getInstance().setHandleInterface(handler).getJson(Urls.URL_GET_CMS_FIND_BY_CONDITION, null, GET_LIST,RaidersListBean.class);
    }

    /**
     * 获取攻略头部
     */
    public static void getStrategyTop(PXHttp.OnResultHandler handler) {
        PXHttp.getInstance().setHandleInterface(handler).getJson(Urls.URL_GET_CMS_FIND_TITLE, null, GET_TOP,ServerModelList.class);
    }


    /**
     * 获取银行
     */
    public static void getBank(PXHttp.OnResultHandler handler){
        Map<String, String> map = new HashMap<>();
        map.put("flag", "1");
        PXHttp.getInstance().setHandleInterface(handler).getJson(Urls.URL_GET_FIND_BANK_BY_POSITION, map, GET_BANK,ServerModelList.class);
    }


    /**
     * 获取APP模块
     */
    public static void getAppModule(Context context,PXHttp.OnResultHandler handler){
        Map<String, String> params = new HashMap<>();
        params.put("appName", "ANDROID_" + InitDatas.APP_NAME + "_" + AppUtils.getVersionCode(context));
        PXHttp.getInstance().setHandleInterface(handler).getJson(Urls.URL_GET_APP_MODULE, params, GET_APP_MODULE,String.class);
    }



    //-------------------------------------------------------------------------------完美分割线---------------------------------------------------------------------------------//
    /**
     * 获取验证码
     *
     * @param sPhone 手机号
     */
    public static void postCode(PXHttp.OnResultHandler handler,String sPhone) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", sPhone);
        params.put("channelNo", InitDatas.CHANNEL_NO);
        params.put("applyArea", InitDatas.province + "/" + InitDatas.city + "/" + InitDatas.district);
        params.put("appName", InitDatas.APP_NAME);
        JSONObject jsonObject = new JSONObject(params);
        PXHttp.getInstance().setHandleInterface(handler).upJson(Urls.URL_POST_SEND_SMS, jsonObject, GET_CODE, LoginBean.class);
    }

    /**
     * 登录
     *
     * @param sPhone 手机号
     * @param sCode  验证码
     */
    public static void postLogin(PXHttp.OnResultHandler handler, String sPhone, String sCode) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userName", sPhone);
        params.put("password", sCode);
        params.put("appName", InitDatas.APP_NAME);
        params.put("channelNo", InitDatas.CHANNEL_NO);
        JSONObject jsonObject = new JSONObject(params);
        PXHttp.getInstance().setHandleInterface(handler).upJson(Urls.URL_POST_LOGIN,jsonObject, POST_LOGIN,LoginBean.class);
    }


    /**
     * 产品列表搜索
     *
     * @param page   请求的页码
     * @param flag   下拉或者上拉标识
     * @param zsType 综合指数参数
     */
    public static void postProductSearch(Context context, PXHttp.OnResultHandler handler, int page, int flag, String zsType, String productType) {

        HashMap<String, String> params = new HashMap<>();
        params.put("period", String.valueOf(InitDatas.loanDate));//期限
        params.put("dateCycle", InitDatas.loanUnit);//期限周期
        params.put("amount", String.valueOf(InitDatas.loanAmount));//借款金额
        params.put("loanType", InitDatas.loanType);
        params.put("channelNo", InitDatas.CHANNEL_NO);//渠道类型：ios,android,wechat
        params.put("appName", InitDatas.APP_NAME);
        params.put("pageNo", page + "");
        params.put("zsType", zsType);
        params.put("productType", productType);
        params.put("versionNo", InitDatas.APP_NAME + AppUtils.getVersionCode(context));
        JSONObject jsonObject = new JSONObject(params);
        PXHttp.getInstance().setHandleInterface(handler).upJson(Urls.URL_POST_FIND_BY_CONDITION, jsonObject, flag, ProductListMoreBean.class);
//        Log.e("productType==>>",productType);
//        Log.e("zsType==>>",zsType);

    }

    /**
     * 获取信用卡列表
     */
    public static void getCreditCard(PXHttp.OnResultHandler handler,int page, int flag){
        Map<String, String> params = new HashMap<>();
        params.put("pageNo", page+"");
        JSONObject jsonObject = new JSONObject(params);
        PXHttp.getInstance().setHandleInterface(handler).upJson(Urls.URL_POST_FIND_BY_CONDITION_CARD, jsonObject, flag,CreditCardBean.class);
    }


    public static void postApplyCreditCard(PXHttp.OnResultHandler handler,String productId,String deviceNumber,String applyArea,String channelNo,String appName){
        HashMap<String, String> params = new HashMap<>();
        params.put("productId",productId);
        params.put("deviceNumber",deviceNumber);
        params.put("applyArea",applyArea);
        params.put("channelNo",channelNo);
        params.put("appName",appName);
        JSONObject jsonObject=new JSONObject(params);
        PXHttp.getInstance().setHandleInterface(handler).upJson(Urls.URL_POST_APPLY_CREDIT_CARD,jsonObject,POST_APPLY_CREDIT_CARD,String.class);
    }
}
