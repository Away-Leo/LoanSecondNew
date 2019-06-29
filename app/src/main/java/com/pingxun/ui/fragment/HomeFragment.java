package com.pingxun.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.pingxun.activity.R;
import com.pingxun.adapter.HomeMultipleItemAdapter;
import com.pingxun.adapter.HomeRecommendAdapter;
import com.pingxun.base.BaseFragment;
import com.pingxun.data.HomeMultipleItem;
import com.pingxun.data.RecommendSection;
import com.pingxun.activity.databinding.FragmentHomeBinding;
import com.pingxun.http.ServerApi;
import com.pingxun.other.InitDatas;
import com.pingxun.other.RequestFlag;
import com.pingxun.ui.activity.ProductListActivity;
import com.pingxundata.answerliu.pxbanner.PXBanner;
import com.pingxundata.answerliu.pxcore.data.ServerModelList;
import com.pingxundata.answerliu.pxcore.view.EmptyLayout;
import com.pingxundata.pxcore.utils.WechatBanner;
import com.pingxundata.pxmeta.http.PXHttp;
import com.pingxundata.pxmeta.pojo.RequestResult;
import com.pingxundata.pxmeta.utils.ActivityUtil;
import com.pingxundata.pxmeta.utils.GlideImgManager;
import com.pingxundata.pxmeta.utils.MyTools;
import com.pingxundata.pxmeta.utils.NetUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import static com.pingxun.other.RequestFlag.GET_BANNER;
import static com.pingxun.other.RequestFlag.GET_HEAD_LINES;
import static com.pingxun.other.RequestFlag.GET_PRODUCT_RECOMMEND;
import static com.pingxun.other.RequestFlag.GET_PRODUCT_TYPE;
import static com.pingxun.other.RequestFlag.GET_WX_BANNER;


/**
 * Created by LH on 2017/8/12.
 * 首页Fragment
 */

public class HomeFragment extends BaseFragment<FragmentHomeBinding> implements PXHttp.OnResultHandler,PXBanner.Delegate<ImageView, String>, PXBanner.Adapter<ImageView, String>,OnRefreshListener,View.OnClickListener {

    private HomeMultipleItemAdapter homeMultipleItemAdapter;
    private HomeRecommendAdapter homeRecommendAdapter;
    private List<HomeMultipleItem> homeMultipleItemList;
    private List<RecommendSection> recommendSectionList;
    private List<SpannableString> mStringList = new ArrayList<>();
    private List<ServerModelList> mBannerList, mRecommendList, mTypeList,mHeadLinesList;
    private Bundle mDataBundle = new Bundle();

    @Override
    protected int getRootLayoutResID() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initData() {
        initAdapter();
        bindingView.swipeContainer.setOnRefreshListener(this);
        bindingView.topView.rbDydk.setOnClickListener(this);
        bindingView.topView.rbGxdk.setOnClickListener(this);
        bindingView.topView.rbXesd.setOnClickListener(this);
        bindingView.topView.rbXydk.setOnClickListener(this);
        bindingView.topView.banner.setDelegate(this);
        bindingView.emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        bindingView.swipeContainer.autoRefresh();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_gxdk://工薪贷款
                goList("salaryloan");
                break;
            case R.id.rb_xydk://信用贷款
                goList("creditcardloan");
                break;
            case R.id.rb_dydk://抵押贷款
                goList("diyaloan");
                break;
            case R.id.rb_xesd://小额贷款
                goList("xiaoejishuloan");
                break;
            case R.id.empty_layout:
                bindingView.emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                bindingView.swipeContainer.autoRefresh();
                break;

        }
    }





    /**
     * 初始化adapter
     */
    private void initAdapter() {
        bindingView.topView.banner.setAdapter(this);
        //===============================初始化产品分类adapter=========================================//
        homeMultipleItemAdapter = new HomeMultipleItemAdapter(getContext(), homeMultipleItemList);
        bindingView.rvType.setNestedScrollingEnabled(false);
        bindingView.rvType.setHasFixedSize(false);
        bindingView.rvType.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        bindingView.rvType.setAdapter(homeMultipleItemAdapter);
        bindingView.rvType.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                goList(mTypeList.get(position).getCode());
            }
        });

        //===============================初始化热门推荐adapter=========================================//
        homeRecommendAdapter = new HomeRecommendAdapter(R.layout.rv_item_product_style_one, R.layout.rv_item_fragment_home_label, recommendSectionList);
        bindingView.rvRecommend.setNestedScrollingEnabled(false);
        bindingView.rvRecommend.setHasFixedSize(false);
        bindingView.rvRecommend.setLayoutManager(new LinearLayoutManager(mActivity));
        bindingView.rvRecommend.setAdapter(homeRecommendAdapter);
        bindingView.rvRecommend.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                RecommendSection recommendSection = recommendSectionList.get(position);
                if (recommendSection.isHeader){
                    InitDatas.loanType="";
                    ActivityUtil.goForward(mActivity, ProductListActivity.class, null, false);
                }else {
                    isLoginToWeb(initBundle(mRecommendList.get(position-1).getId()+"",mRecommendList.get(position-1).getUrl(),mRecommendList.get(position-1).getName(),0));
                }
            }
        });

    }

    @Override
    public void onResult(RequestResult requestResult, String jsonStr, int flag) {
        switch (flag) {
            case GET_BANNER://轮播图
                try {
                    if (requestResult.isSuccess()) {
                        mBannerList=(List<ServerModelList>)requestResult.getResultList();
                        if (mBannerList.size()!=0){
                            List<String> bannerList=new ArrayList<>();
                            for (int i=0;i<mBannerList.size();i++){
                                bannerList.add(mBannerList.get(i).getBannerImg());
                            }
                            bindingView.topView.banner.setData(bannerList,null);
                        }
                    }
                } catch (Exception e) {}
                ServerApi.getHeadlines(HomeFragment.this);
                break;

            case GET_HEAD_LINES://头条
                try {
                    if (requestResult.isSuccess()) {
                        mHeadLinesList = (List<ServerModelList>)requestResult.getResultList();
                        mStringList.clear();
                        for (int i = 0; i < mHeadLinesList.size(); i++) {
                            String s1 = mHeadLinesList.get(i).getPhone();
                            String s2 = mHeadLinesList.get(i).getName();
                            String s3 = MyTools.addComma(String.valueOf(mHeadLinesList.get(i).getApplyAmount()));
                            SpannableString spannableString = new SpannableString(s1 + "成功通过" + s2 + "贷到" + s3 + "元");
                            ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(Color.parseColor("#FF0000"));
                            ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(Color.parseColor("#FF0000"));
                            spannableString.setSpan(colorSpan1, s1.length()+4, spannableString.length() - (s3.length() + 3), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(colorSpan2, spannableString.length() - (s3.length() + 1), spannableString.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            mStringList.add(spannableString);
                        }
                        bindingView.topView.marqueeView.startWithList(mStringList);
                        bindingView.topView.marqueeView.startFlipping();
                    }
                } catch (Exception e) {}
                ServerApi.getProductType(HomeFragment.this);
                break;
            case GET_PRODUCT_TYPE://产品分类
                try {
                    if (requestResult.isSuccess()) {
                        mTypeList = (List<ServerModelList>)requestResult.getResultList();
                        homeMultipleItemList = new ArrayList<>();
                        homeMultipleItemList.add(new HomeMultipleItem(HomeMultipleItem.PRODUCT_TYPE_FIRST, 2, mTypeList.get(0).getId(), mTypeList.get(0).getName(), mTypeList.get(0).getDescription(), mTypeList.get(0).getImg(), mTypeList.get(0).getCode()));
                        for (int i = 1; i < 5; i++) {
                            homeMultipleItemList.add(new HomeMultipleItem(HomeMultipleItem.PRODUCT_TYPE_NEXT, 2, mTypeList.get(i).getId(), mTypeList.get(i).getName(), mTypeList.get(i).getDescription(), mTypeList.get(i).getImg(), mTypeList.get(i).getCode()));
                        }
                        homeMultipleItemAdapter.setNewData(homeMultipleItemList);
                    }
                }catch (Exception e){}
                ServerApi.getWxBanner(mActivity,HomeFragment.this);
                break;
            case GET_WX_BANNER://获取微信图片
                try {
                    if (requestResult.isSuccess()){
                        List<ServerModelList> mList=(List<ServerModelList>)requestResult.getResultList();
                        WechatBanner.with(mActivity,mList.get(0).getBannerImg()).pop(mList.get(0).getBannerDetailImg());
                    }
                }catch (Exception e){}
                ServerApi.getProductRecommend(HomeFragment.this,mActivity);
                break;
            case GET_PRODUCT_RECOMMEND://产品推荐
                try {
                    if (requestResult.isSuccess()) {
                        mRecommendList = (List<ServerModelList>)requestResult.getResultList();
                        recommendSectionList = new ArrayList<>();
                        recommendSectionList.add(new RecommendSection(true, "热门推荐", true));
                        for (int i = 0; i < mRecommendList.size(); i++) {
                            recommendSectionList.add(new RecommendSection(mRecommendList.get(i)));
                        }
                        homeRecommendAdapter.setNewData(recommendSectionList);
                    }
                }catch (Exception e){}
                bindingView.swipeContainer.finishRefresh();
                bindingView.emptyLayout.setErrorType(EmptyLayout.NO_ERROR);
                break;
        }
    }

    @Override
    public void onError(int flag) {
        bindingView.swipeContainer.finishRefresh();
        if (NetUtil.getNetWorkState(mActivity) == NetUtil.NETWORK_NONE) {
            bindingView.emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        } else {
            if (flag!= RequestFlag.POST_APPLY){
                bindingView.emptyLayout.setErrorType(EmptyLayout.ERROR);
            }
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        bindingView.topView.marqueeView.stopFlipping();
        ServerApi.getBanner(mActivity,HomeFragment.this);
    }


    @Override
    public void fillBannerItem(PXBanner pxBanner, ImageView imageView, String imgUrl, int i) {
        GlideImgManager.glideLoader(imageView.getContext(), imgUrl, R.mipmap.tu_banner, R.mipmap.tu_banner, imageView);
    }

    /**
     * Banner点击事件
     * @param pxBanner pxBanner
     * @param imageView imageView
     * @param imgUrl 图片地址
     * @param position 下标
     */
    @Override
    public void onBannerItemClick(PXBanner pxBanner, ImageView imageView, String imgUrl, int position) {
        if (!TextUtils.isEmpty(mBannerList.get(position).getJumpUrl())) {
            isLoginToWeb( initBundle(String.valueOf(mBannerList.get(position).getId()),mBannerList.get(position).getJumpUrl(),mBannerList.get(position).getName(),1));
        }
    }




    /**
     * 跳转到产品列表界面
     *
     * @param sId loanType
     */
    private void goList(String sId) {
        InitDatas.loanType=sId;
        ActivityUtil.goForward(mActivity, ProductListActivity.class, null, false);
    }


    @Override
    public void onPause() {
        super.onPause();
        bindingView.topView.banner.stopAutoPlay();
    }



    @Override
    public void onResume() {
        super.onResume();
        bindingView.topView.banner.startAutoPlay();
    }


}
