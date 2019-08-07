package com.dbhh.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dbhh.bigwhiteflowers.R;

/**
 * @Project OneKeyLogin
 * @PackageName com.android.xinyan.onekeylogin
 * @Author Loe Zou
 * @GreatDate 2019/3/14/014
 */
public class MessageDialog extends Dialog {


    private Window dialogWindow;
    private Activity mContext;
    public TextView login_status_txt,message_txt,commit_txt,cancelBtn;
    public ImageView status_img;

    private View.OnClickListener onSubmitClickListener;
    private View.OnClickListener onCancelClickListener;


    public void setOnSubmitClickListener(View.OnClickListener onClickListener) {
        this.onSubmitClickListener = onClickListener;
    }
    public void setOnCancekClickListener(View.OnClickListener onClickListener) {
        this.onCancelClickListener = onClickListener;
    }

    public MessageDialog(@NonNull Activity context) {
        super(context);
        this.mContext = context;
        initDialog();
    }

    public MessageDialog(@NonNull Activity context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        initDialog();
    }

    protected MessageDialog(@NonNull Activity context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
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
        lp.width = (int)(dm.widthPixels * 1f);
        lp.height = (int)(dm.heightPixels * 1f);
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
        setCancelable(false);
        setCanceledOnTouchOutside(true);
        //        dialogWindow.setWindowAnimations(R.style.dialogWindowAnim);
    }

    private void initDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_message,null);
        login_status_txt = view.findViewById(R.id.login_status_txt);
        message_txt = view.findViewById(R.id.message_txt);
        commit_txt = view.findViewById(R.id.commit_txt);
        cancelBtn = view.findViewById(R.id.cancel_dia);
        status_img = view.findViewById(R.id.status_img);
        cancelBtn.setOnClickListener(view1 -> {
            if(isShowing()){
                this.onCancelClickListener.onClick(cancelBtn);
            }
        });
        commit_txt.setOnClickListener(v -> {
            if(isShowing()){
                this.onSubmitClickListener.onClick(commit_txt);
            }
            //                mContext.finish();
        });
        setContentView(view);
    }

    public void setMessage(String message, boolean isSucess){
        String message_str = "";
        if(isSucess){
            String tokenFormat="一键登录的oclToken：\n";
            message_str = tokenFormat+message;
        }else{
            message_str = String.format(mContext.getString(R.string.failed_message),message);
        }
        message_txt.setText(message_str);
    }

    public void setAuthMessage(String message, boolean isSucess){
        String message_str = "";
        if(isSucess){
            login_status_txt.setText("号码认证成功");
            message_str = message;
        }else{
            message_str = String.format(mContext.getString(R.string.failed_message),message);
            login_status_txt.setText("号码认证失败");
        }
        message_txt.setText(message_str);
        commit_txt.setText("再次体验");
    }

    public void setStatus(String status){
        login_status_txt.setText(String.format(mContext.getString(R.string.status),status));
    }

    public void setImageSource(boolean isSucess){
        status_img.setImageResource(isSucess? R.drawable.success: R.drawable.defate);
    }

    public void dialogDimiss(){
        dismiss();
    }
}
