package com.chenld.mycloudreader.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by chenld on 2017/3/2.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<?> mFragment;
    private List<String> mTitleList;

    /**
     * 普通，主页使用
     */
    public MyFragmentPagerAdapter(FragmentManager fm, List<?> fragment) {
        super(fm);
        this.mFragment = fragment;
    }

    /**
     * 接收首页传递的标题
     */
    public MyFragmentPagerAdapter(FragmentManager fm, List<?> fragment, List<String> titleList) {
        super(fm);
        this.mFragment = fragment;
        this.mTitleList = titleList;
    }


    @Override
    public Fragment getItem(int position) {
        return (Fragment) mFragment.get(position);
    }

    /**
     * 选项卡总数
     * @return
     */
    @Override
    public int getCount() {
        return mFragment.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        //container.removeView((View) mFragment.get(position));
    }

    /**
     * 获取标题
     * 首页显示title，每日推荐等..
     * 若有问题，移到对应单独页面
     */
    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitleList != null) {
            return mTitleList.get(position);
        } else {
            return "";
        }
    }

    public void addFragmentList(List<?> fragment) {
        this.mFragment.clear();
        this.mFragment = null;
        this.mFragment = fragment;
        notifyDataSetChanged();
    }

}
