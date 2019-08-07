package com.dbhh.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.dbhh.bigwhiteflowers.R;


/**
 * Created by Joyin on 2015/9/26.
 */
public class LoadingDialog extends Dialog {

    private Window dialogWindow;
    private Activity mContext;
    private ImageView mIcon;
    private Animation animation;
    private String loadingText;

    public String getLoadingText() {
        return loadingText;
    }

    public void setLoadingText(String loadingText) {
        this.loadingText = loadingText;
    }

    public LoadingDialog(Activity context) {
        super(context);
        this.mContext = context;
        initDialog();
    }

    public LoadingDialog(Activity context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        initDialog();
    }

    public LoadingDialog(Activity context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
        initDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogWindow = getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = dialogWindow.getWindowManager();
        windowManager.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.width = (int)(dm.widthPixels * 0.15f);
//        lp.height = (int)(dm.widthPixels * 0.15f);
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
        dialogWindow.setBackgroundDrawableResource(R.drawable.oauth_loading_bg);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    private void initDialog(){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.dialog_loading_layout, null);
        mIcon = (ImageView) v.findViewById(com.xinyan.xinyanoklsdk.R.id.icon_loading);
        animation = AnimationUtils.loadAnimation(mContext, com.xinyan.xinyanoklsdk.R.anim.xy_loading_rotate);
        TextView mTv = (TextView) v.findViewById(com.xinyan.xinyanoklsdk.R.id.tv_loading);
        setContentView(v);
    }

    public void showDialog(){
        try {
            if(null!=mIcon){
                mIcon.startAnimation(animation);
            }
            show();
        } catch (Exception e) {
        }
    }

    @Override
    public void dismiss() {
        if(null!=mIcon){
            mIcon.clearAnimation();
        }
        mContext.finish();
        super.dismiss();
    }
}
