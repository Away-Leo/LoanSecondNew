package com.dbhh.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dbhh.base.App;
import com.dbhh.http.ServerApi;
import com.dbhh.other.InitDatas;
import com.dbhh.other.RequestFlag;
import com.pingxundata.pxmeta.http.PXHttp;
import com.pingxundata.pxmeta.pojo.RequestResult;
import com.pingxundata.pxmeta.utils.SharedPrefsUtil;


/**
 * Created by LH on 2017/8/7.
 * 后台登录服务
 */

public class LoginService extends Service implements PXHttp.OnResultHandler{


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String sPhone= SharedPrefsUtil.getValue(App.getAppContext(), InitDatas.SP_NAME, InitDatas.UserPhone,"");
        String sCode=SharedPrefsUtil.getValue(App.getAppContext(),InitDatas.SP_NAME,InitDatas.UserPw,"");
        ServerApi.postLogin(LoginService.this,sPhone, sCode,"phoneLogin",null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onResult(RequestResult requestResult, String jsonStr, int flag) {
        switch (flag){
            case RequestFlag.POST_LOGIN:
                try {
                    if (requestResult.isSuccess()){
                        SharedPrefsUtil.putValue(App.getAppContext(),InitDatas.SP_NAME,InitDatas.UserIsLogin,true);
                    }else {
                        SharedPrefsUtil.putValue(App.getAppContext(),InitDatas.SP_NAME,InitDatas.UserIsLogin,false);
                    }
                }catch (Exception e){
                    SharedPrefsUtil.putValue(App.getAppContext(),InitDatas.SP_NAME,InitDatas.UserIsLogin,false);
                }
                ServerApi.getParameter(LoginService.this);
                break;
            case RequestFlag.GET_FIND_PARAMETER:
                try {
                    JSONObject jsonObject = JSON.parseObject(jsonStr);
                    String code = jsonObject.getString("code");
//                    String message = jsonObject.getString("message");
                    if (code.equals("000000")){
                        JSONObject mDate = jsonObject.getJSONObject("data");
                        //String转Double
                        Double flags=Double.parseDouble(mDate.getString("suspendFlag"));
                        InitDatas.SUSPENDFLAG= flags.intValue();
//                        String messageTip=mDate.getString("messageTip");
//                        if (!ObjectHelper.isEmpty(messageTip)){
//                            InitDatas.messageTip=messageTip;
//                        }
                    }
                }catch (Exception e){}
                ServerApi.getAppModule(App.getAppContext(),LoginService.this);
                break;
            case RequestFlag.GET_APP_MODULE:
                try {
                    if (requestResult.isSuccess()){
                        String result= (String) requestResult.getEntityResult();
                        SharedPrefsUtil.putValue(App.getAppContext(),InitDatas.SP_NAME,InitDatas.AppModule,result);
                    }
                }catch (Exception e){}
                break;

        }
    }

    @Override
    public void onError(int flag) {
        if (flag== RequestFlag.POST_LOGIN){
            SharedPrefsUtil.putValue(App.getAppContext(),InitDatas.SP_NAME,InitDatas.UserIsLogin,false);
        }
    }
}
