package com.dbhh.ui.activity;


import com.dbhh.bigwhiteflowers.R;
import com.dbhh.ui.fragment.HomeFragment;
import com.dbhh.ui.fragment.MineFragment;
import com.dbhh.ui.fragment.ProductListFragment;

public enum MainTab {

    FRAGMENT1(0, R.string.recommend, R.drawable.activity_main_home, HomeFragment.class),
    FRAGMENT2(1, R.string.home_tab_product, R.drawable.activity_main_accurate, ProductListFragment.class),
    FRAGMENT5(4,R.string.home_tab_mine,R.drawable.activity_main_mine, MineFragment.class);



    private final int index;
    private final int resName;
    private final int resIcon;
    private final Class<?> clazz;

    MainTab(int index, int resName, int resIcon, Class<?> clazz) {
        this.index = index;
        this.resName = resName;
        this.resIcon = resIcon;
        this.clazz = clazz;
    }

    public int getIndex() {
        return index;
    }

    public int getResName() {
        return resName;
    }

    public int getResIcon() {
        return resIcon;
    }

    public Class<?> getClazz() {
        return clazz;
    }

}
