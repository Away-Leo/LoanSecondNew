package com.pingxun.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.pingxun.activity.R;
import com.pingxun.adapter.HomeHotAdapter;
import com.pingxun.adapter.HomeMultipleItemAdapter;
import com.pingxun.adapter.HomeRecommendAdapter;
import com.pingxun.adapter.HomeSharpAdapter;
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
import com.pingxundata.pxmeta.utils.GlideCircleTransform;
import com.pingxundata.pxmeta.utils.GlideImgManager;
import com.pingxundata.pxmeta.utils.MyTools;
import com.pingxundata.pxmeta.utils.NetUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import static com.pingxun.other.RequestFlag.GET_BANNER;
import static com.pingxun.other.RequestFlag.GET_HEAD_LINES;
import static com.pingxun.other.RequestFlag.GET_PRODUCT_HOT;
import static com.pingxun.other.RequestFlag.GET_PRODUCT_RECOMMEND;
import static com.pingxun.other.RequestFlag.GET_PRODUCT_SHARP;
import static com.pingxun.other.RequestFlag.GET_PRODUCT_TYPE;
import static com.pingxun.other.RequestFlag.GET_WX_BANNER;


/**
 * Created by LH on 2017/8/12.
 * 首页Fragment
 */

public class HomeFragment extends BaseFragment<FragmentHomeBinding> implements PXHttp.OnResultHandler,PXBanner.Delegate<ImageView, String>, PXBanner.Adapter<ImageView, String>,OnRefreshListener,View.OnClickListener {

    private HomeRecommendAdapter homeRecommendAdapter;
    private RecommendSection sharp;
    private HomeHotAdapter homeHotAdapter;
    private List<RecommendSection> recommendSectionList;
    private List<RecommendSection> hotSectionList;
    private List<RecommendSection> sharpSectionList;
    private List<ServerModelList> mBannerList, mRecommendList,mHotList,mSharpList;

    @Override
    protected int getRootLayoutResID() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initData() {
        initAdapter();
        bindingView.swipeContainer.setOnRefreshListener(this);
        bindingView.topView.banner.setDelegate(this);
        bindingView.emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        bindingView.swipeContainer.autoRefresh();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.empty_layout:
                bindingView.emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                bindingView.swipeContainer.autoRefresh();
                break;
            case R.id.sharp_product:
                isLoginToWeb(initBundle(sharp.t.getId()+"",sharp.t.getUrl(),sharp.t.getName(),0));
                break;

        }
    }





    /**
     * 初始化adapter
     */
    private void initAdapter() {
        bindingView.topView.banner.setAdapter(this);

        //===============================初始化推荐adapter=========================================//
        homeRecommendAdapter = new HomeRecommendAdapter(R.layout.activity_my_product_list_one, R.layout.rv_item_fragment_home_label, recommendSectionList);
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
        //===============================初始化热门adapter=========================================//
        homeHotAdapter = new HomeHotAdapter(R.layout.rv_item_fragment_home_hot, R.layout.rv_item_fragment_home_label, hotSectionList);
        bindingView.rvHot.setNestedScrollingEnabled(false);
        bindingView.rvHot.setHasFixedSize(false);
        bindingView.rvHot.setLayoutManager(new GridLayoutManager(mActivity,3));
        bindingView.rvHot.setAdapter(homeHotAdapter);
        bindingView.rvHot.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                RecommendSection recommendSection = hotSectionList.get(position);
                if (recommendSection.isHeader){
                    InitDatas.loanType="";
                    ActivityUtil.goForward(mActivity, ProductListActivity.class, null, false);
                }else {
                    isLoginToWeb(initBundle(mHotList.get(position-1).getId()+"",mHotList.get(position-1).getUrl(),mHotList.get(position-1).getName(),0));
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
                ServerApi.getProductHot(HomeFragment.this,mActivity);
                break;
            case GET_WX_BANNER://获取微信图片
                try {
                    if (requestResult.isSuccess()){
                        List<ServerModelList> mList=(List<ServerModelList>)requestResult.getResultList();
                        WechatBanner.with(mActivity,mList.get(0).getBannerImg()).pop(mList.get(0).getBannerDetailImg());
                    }
                }catch (Exception e){}
                //获取热门产品
                break;
            case GET_PRODUCT_RECOMMEND://热门推荐
                try {
                    if (requestResult.isSuccess()) {
                        mRecommendList = (List<ServerModelList>)requestResult.getResultList();
                        recommendSectionList = new ArrayList<>();
                        recommendSectionList.add(new RecommendSection(true, "热门推荐", false));
                        for (int i = 0; i < mRecommendList.size(); i++) {
                            recommendSectionList.add(new RecommendSection(mRecommendList.get(i)));
                        }
                        homeRecommendAdapter.setNewData(recommendSectionList);
                    }
                }catch (Exception e){}
                bindingView.swipeContainer.finishRefresh();
                bindingView.emptyLayout.setErrorType(EmptyLayout.NO_ERROR);
                break;
            case GET_PRODUCT_SHARP://精品推荐
                try {
                    if (requestResult.isSuccess()) {
                        mSharpList = (List<ServerModelList>)requestResult.getResultList();
                        sharpSectionList = new ArrayList<>();
                        sharp=new RecommendSection(mSharpList.get(0));
                        GlideImgManager.glideLoader(mActivity, sharp.t.getImg(), R.mipmap.img_default, R.mipmap.img_default, getActivity().findViewById(R.id.sharp_proicon), 0);

                        bindingView.sharpTitle.setText(sharp.t.getName());
                        bindingView.sharpProrange.setText(MyTools.initTvQuota(sharp.t.getStartAmount(), sharp.t.getEndAmount()));
                        bindingView.sharpProlable.setText(sharp.t.getProductLabel());

                        getActivity().findViewById(R.id.sharp_product).setOnClickListener(view -> {
                            isLoginToWeb(initBundle(sharp.t.getId()+"",sharp.t.getUrl(),sharp.t.getName(),0));
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                bindingView.swipeContainer.finishRefresh();
                bindingView.emptyLayout.setErrorType(EmptyLayout.NO_ERROR);
                break;
            case GET_PRODUCT_HOT:
                try {
                    if (requestResult.isSuccess()) {
                        mHotList = (List<ServerModelList>)requestResult.getResultList();
                        hotSectionList = new ArrayList<>();
                        hotSectionList.add(new RecommendSection(true, "最新口子", false));
                        for (int i = 0; i < mHotList.size(); i++) {
                            hotSectionList.add(new RecommendSection(mHotList.get(i)));
                        }
                        homeHotAdapter.setNewData(hotSectionList);
                    }
                }catch (Exception e){}
                bindingView.swipeContainer.finishRefresh();
                bindingView.emptyLayout.setErrorType(EmptyLayout.NO_ERROR);
                ServerApi.getProductRecommend(HomeFragment.this,mActivity);
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
        ServerApi.getProductSharp(HomeFragment.this,mActivity);
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
