package com.pingxun.ui.fragment;

import android.annotation.SuppressLint;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.pingxun.activity.R;
import com.pingxun.base.BaseFragment;
import com.pingxun.activity.databinding.FragmentAccurateBinding;
import com.pingxun.http.ServerApi;
import com.pingxun.other.InitDatas;
import com.pingxun.ui.activity.ProductListActivity;
import com.pingxundata.answerliu.pxcore.data.ServerModelList;
import com.pingxundata.answerliu.pxcore.data.ServerModelObject;
import com.pingxundata.answerliu.pxcore.view.EmptyLayout;
import com.pingxundata.pxcore.utils.WechatBanner;
import com.pingxundata.pxmeta.http.PXHttp;
import com.pingxundata.pxmeta.pojo.RequestResult;
import com.pingxundata.pxmeta.utils.ActivityUtil;
import com.pingxundata.pxmeta.utils.MyTools;
import com.pingxundata.pxmeta.utils.NetUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.math.BigDecimal;
import java.util.List;

import static com.pingxun.other.RequestFlag.GET_FIND_PARAMETER;
import static com.pingxun.other.RequestFlag.GET_WX_BANNER;


/**
 * Created by LH on 2017/8/12.
 * 精准Fragment
 */

public class AccurateFragment extends BaseFragment<FragmentAccurateBinding> implements PXHttp.OnResultHandler,OnRefreshListener,View.OnClickListener {



    private int mLimitLow;//最低期限
    private int mLimitHeight;//最高期限
    private int progressData;//拖动条拖动的比例
    private String mLoanUnitStr;

    @Override
    protected int getRootLayoutResID() {
        return R.layout.fragment_accurate;
    }

    @Override
    protected void initData() {
        bindingView.swipeLayout.setOnRefreshListener(this);
        bindingView.checkboxDayOrMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    InitDatas.loanUnit = "D";
                    mLimitLow = Integer.parseInt(InitDatas.dayStart.replaceAll("天", "").trim());
                    mLimitHeight = Integer.parseInt(InitDatas.dayEnd.replaceAll("天", "").trim());
                    mLoanUnitStr = "天";
                    limitDateUIThread(InitDatas.dayStart, InitDatas.dayEnd);
                } else {
                    InitDatas.loanUnit = "M";
                    mLimitLow = Integer.parseInt(InitDatas.mounthStart.replaceAll("月", "").trim());
                    mLimitHeight = Integer.parseInt(InitDatas.mounthEnd.replaceAll("月", "").trim());
                    mLoanUnitStr = "月";
                    limitDateUIThread(InitDatas.mounthStart, InitDatas.mounthEnd);
                }
                calculateTimeInfo(progressData);
            }
        });

        bindingView.btnSearch.setOnClickListener(this);
        bindingView.emptyLayout.setOnClickListener(this);

        bindingView.emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        bindingView.swipeLayout.autoRefresh();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_search://搜索
                InitDatas.loanType="";
                ActivityUtil.goForward(mActivity, ProductListActivity.class, null, false);
                break;
            case R.id.empty_layout:
                bindingView.emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                bindingView.swipeLayout.autoRefresh();
                break;
        }
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onResult(RequestResult requestResult, String jsonStr, int flag) {

        switch (flag) {
            case GET_FIND_PARAMETER://获取贷款参数
                try {
                    if (requestResult.isSuccess()) {
                        ServerModelObject moneyParamBean = (ServerModelObject)requestResult.getEntityResult();
                        InitDatas.limitLowAmount = new BigDecimal(moneyParamBean.getStartAmount() + "").intValue(); // 贷款最低金额
                        InitDatas.limitHeightAmount = new BigDecimal(moneyParamBean.getEndAmount() + "").intValue(); //贷款最高金额
                        InitDatas.dayStart = moneyParamBean.getStartPeriodDay() + "天"; //日 单位 起始
                        InitDatas.dayEnd = moneyParamBean.getEndPeriodDay() + "天";  //日 单位 结束
                        InitDatas.mounthStart = moneyParamBean.getStartPeriodMonth() + "月"; //月 单位 开始
                        InitDatas.mounthEnd = moneyParamBean.getEndPeriodMonth() + "月"; //月 单位 结束
                        InitDatas.rateYear = BigDecimal.valueOf(moneyParamBean.getLoanRate());  //年利率
                        InitDatas.rateDay = InitDatas.rateYear.divide(new BigDecimal(100)).divide(new BigDecimal(360)); //日利率
                        InitDatas.rateMounth = InitDatas.rateDay.multiply(new BigDecimal(30)); //月利率
//                      Log.e("贷款最低金额==>>", InitDatas.limitLowAmount + "");
//                      Log.e("贷款最高金额==>>", InitDatas.limitHeightAmount + "");
//                      Log.e("日 单位 起始==>>", InitDatas.dayStart);
//                      Log.e("日 单位 结束==>>", InitDatas.dayEnd);
//                      Log.e("月 单位 开始==>>", InitDatas.mounthStart);
//                      Log.e("月 单位 结束==>>", InitDatas.mounthEnd);
//                      Log.e("年利率==>>", String.valueOf(InitDatas.rateYear));
//                      Log.e("日利率==>>", String.valueOf(InitDatas.rateDay));
//                      Log.e("月利率==>>", String.valueOf(InitDatas.rateMounth));

                        bindingView.tvStartTime.setText(InitDatas.mounthStart);//开始时间
                        bindingView.tvEndTime.setText(InitDatas.mounthEnd);//结束时间
                        bindingView.tvStartMoney.setText(String.valueOf(InitDatas.limitLowAmount) + "元");//最低金额
                        bindingView.tvEndMoney.setText(String.valueOf(InitDatas.limitHeightAmount) + "元");//最高金额

                        bindingView.checkboxDayOrMonth.setChecked(false);
                        bindingView.sbMoney.setValus(InitDatas.limitLowAmount, InitDatas.limitHeightAmount, "元");
                        calculateTimeInfo(0);
                        calculateParInfo(0);
                        runinUI();
                    }
                } catch (Exception e) {}
                break;
            case GET_WX_BANNER:
                try {
                    if (requestResult.isSuccess()){
                        List<ServerModelList> mList=(List<ServerModelList>)requestResult.getResultList();
                        WechatBanner.with(mActivity,mList.get(0).getBannerImg()).pop(mList.get(0).getBannerDetailImg());
                    }
                }catch (Exception e){}
                bindingView.swipeLayout.finishRefresh();
                bindingView.emptyLayout.setErrorType(EmptyLayout.NO_ERROR);
                break;

        }
    }

    @Override
    public void onError(int flag) {
        bindingView.swipeLayout.finishRefresh();
        if (NetUtil.getNetWorkState(mActivity) == NetUtil.NETWORK_NONE) {
            bindingView.emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        } else {
            bindingView.emptyLayout.setErrorType(EmptyLayout.ERROR);
        }
    }

    /**
     * 更新UI
     */
    @SuppressLint("SetTextI18n")
    private void runinUI() {


        bindingView.sbMoney.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                calculateParInfo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        bindingView.sbTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                calculateTimeInfo(progress);
                progressData = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void calculateParInfo(int progress) {
        //贷款金额
        Float loanMonerfloat = InitDatas.limitLowAmount + (InitDatas.limitHeightAmount - InitDatas.limitLowAmount) * (progress / 100F);
        int loanMoneyInt = loanMonerfloat.intValue();
        InitDatas.loanAmount = loanMoneyInt;

        int count = loanMoneyInt;
        //每期应还金额
        int loanAmountBigDecimal;
        if ("天".equals(mLoanUnitStr)) {
            BigDecimal rateAmount = InitDatas.rateDay.multiply(new BigDecimal(InitDatas.loanDate)).multiply(new BigDecimal(count));
            count = count + rateAmount.intValue();
            loanAmountBigDecimal = new BigDecimal(count).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        } else {
            //1+月利率
            BigDecimal r1=InitDatas.rateMounth.add(BigDecimal.ONE);
            //(1+月利率)次方
            BigDecimal pore1=r1.pow(InitDatas.loanDate);
            //被除数
            BigDecimal byCount=new BigDecimal(count).multiply(InitDatas.rateMounth).multiply(pore1);
            //除数
            BigDecimal byCount2=r1.pow(InitDatas.loanDate).subtract(new BigDecimal(1));
            BigDecimal resu=byCount.divide(byCount2,0,BigDecimal.ROUND_HALF_UP);
            loanAmountBigDecimal=resu.setScale(0,BigDecimal.ROUND_HALF_UP).intValue();
        }
        bindingView.tvMqyhNum.setText("¥" + loanAmountBigDecimal);
        bindingView.tvTotalMoney.setText("¥ " + MyTools.addComma(String.valueOf(loanMoneyInt)));
    }


    /**
     * 计算贷款时间信息
     *
     * @param progress
     */
    @SuppressLint("SetTextI18n")
    private void calculateTimeInfo(int progress) {


        if (InitDatas.loanUnit.equals("M")) {
            mLimitLow = Integer.parseInt(InitDatas.mounthStart.replaceAll("月", "").trim());
            mLimitHeight = Integer.parseInt(InitDatas.mounthEnd.replaceAll("月", "").trim());
            mLoanUnitStr = "月";

        } else {
            mLimitLow = Integer.parseInt(InitDatas.dayStart.replaceAll("天", "").trim());
            mLimitHeight = Integer.parseInt(InitDatas.dayEnd.replaceAll("天", "").trim());
            mLoanUnitStr = "天";
        }
        //贷款期限
        Float loanTimefloat = mLimitLow + (mLimitHeight - mLimitLow) * (progress / 100F);
        int loanTimeInt = loanTimefloat.intValue();
        InitDatas.loanDate = loanTimeInt;
        //  BigDecimal loanTimeBigDecimal = new BigDecimal(loanTimeInt).setScale(0, BigDecimal.ROUND_DOWN);

        //贷款期限页面显示数据
        String loanTimeStr = String.valueOf(loanTimeInt);


        int count = InitDatas.loanAmount;
        //每期应还金额
        int loanAmountBigDecimal;
        if ("天".equals(mLoanUnitStr)) {
            BigDecimal rateAmount = InitDatas.rateDay.multiply(new BigDecimal(InitDatas.loanDate)).multiply(new BigDecimal(count));
            count = count + rateAmount.intValue();
            loanAmountBigDecimal = new BigDecimal(count).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        } else {
            //1+月利率
            BigDecimal r1=InitDatas.rateMounth.add(BigDecimal.ONE);
            //(1+月利率)次方
            BigDecimal pore1=r1.pow(InitDatas.loanDate);
            //被除数
            BigDecimal byCount=new BigDecimal(count).multiply(InitDatas.rateMounth).multiply(pore1);
            //除数
            BigDecimal byCount2=r1.pow(InitDatas.loanDate).subtract(new BigDecimal(1));
            BigDecimal resu=byCount.divide(byCount2,0,BigDecimal.ROUND_HALF_UP);
            loanAmountBigDecimal=resu.setScale(0,BigDecimal.ROUND_HALF_UP).intValue();
        }
        bindingView.tvMqyhNum.setText("¥" + loanAmountBigDecimal);
        repayDataUIThread(progress, loanTimeStr, mLimitHeight, mLimitLow, mLoanUnitStr);
    }

    /**
     * 贷款期限UI更新
     */
    @UiThread
    void repayDataUIThread(int progress, String loanTimeStr, int limitHeight, int limitLow, String loanUnitStr) {
        bindingView.sbTime.setProgress(progress);
        bindingView.sbTime.setValus(limitLow, limitHeight, loanUnitStr);
        bindingView.tvHkqxNum.setText(loanTimeStr);
        bindingView.tvHkqx.setText("还款期限" + "(" + loanUnitStr + ")");
    }

    /**
     * 拖动条最大时间以及最小时间UI刷新
     *
     * @param startData
     * @param endData
     */
    @UiThread
    void limitDateUIThread(String startData, String endData) {
        bindingView.tvStartTime.setText(startData);
        bindingView.tvEndTime.setText(endData);
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        ServerApi.getParameter(AccurateFragment.this);
    }

}
