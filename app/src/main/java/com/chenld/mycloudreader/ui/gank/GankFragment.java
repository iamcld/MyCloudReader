package com.chenld.mycloudreader.ui.gank;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import com.apkfuns.logutils.LogUtils;
import com.chenld.mycloudreader.R;
import com.chenld.mycloudreader.base.BaseFragment;
import com.chenld.mycloudreader.databinding.FragmentGankBinding;
import com.chenld.mycloudreader.http.rx.RxBus;
import com.chenld.mycloudreader.http.rx.RxCodeConstants;
import com.chenld.mycloudreader.ui.gank.child.AndroidFragment;
import com.chenld.mycloudreader.ui.gank.child.CustomFragment;
import com.chenld.mycloudreader.ui.gank.child.EverydayFragment;
import com.chenld.mycloudreader.ui.gank.child.WelfareFragment;
import com.chenld.mycloudreader.view.MyFragmentPagerAdapter;

import java.util.ArrayList;

import rx.Subscription;
import rx.functions.Action1;

/**
 * 展示干货的页面
 */
public class GankFragment extends BaseFragment<FragmentGankBinding> {

    private ArrayList<String> mTitleList = new ArrayList<>(4);
    private ArrayList<Fragment> mFragments = new ArrayList<>(4);


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        showLoading();
        initFragmentList();
        /**
         * 注意使用的是：getChildFragmentManager，Fragment里面嵌套Fragment 的话：一定要用getChildFragmentManager()
         * 这样setOffscreenPageLimit()就可以添加上，保留相邻3个实例，切换时不会卡
         * 但会内存溢出，在显示时加载数据
         */

        MyFragmentPagerAdapter myAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), mFragments, mTitleList);
        bindingView.vpGank.setAdapter(myAdapter);
        // 左右预加载页面的个数
        bindingView.vpGank.setOffscreenPageLimit(3);
        myAdapter.notifyDataSetChanged();
        bindingView.tabGank.setTabMode(TabLayout.MODE_FIXED);
        //绑定tablayout和viewpager
        bindingView.tabGank.setupWithViewPager(bindingView.vpGank);
        showContentView();
        // item点击跳转
        initRxBus();

    }

    @Override
    public int setContent() {
        return R.layout.fragment_gank;
    }

    private void initFragmentList() {
        mTitleList.add("每日推荐");
        mTitleList.add("福利");
        mTitleList.add("干货订制");
        mTitleList.add("大安卓");
        mFragments.add(new EverydayFragment());
        mFragments.add(new WelfareFragment());
        mFragments.add(new CustomFragment());
        mFragments.add(AndroidFragment.newInstance("Android"));
//        mFragments.add(AndroidFragment.newInstance("iOS"));
    }

    /**
     * 每日推荐点击"更多"跳转
     */
    private void initRxBus() {
        //接收事件
        Subscription subscribe = RxBus.getDefault()
                .toObservable(RxCodeConstants.JUMP_TYPE, Integer.class)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        if (integer == 0) {
                            LogUtils.d("setCurrentItem(3)");
                            bindingView.vpGank.setCurrentItem(3);
                        } else if (integer == 1) {
                            LogUtils.d("setCurrentItem(1)");
                            bindingView.vpGank.setCurrentItem(1);
                        } else if (integer == 2) {
                            LogUtils.d("setCurrentItem(2)");
                            bindingView.vpGank.setCurrentItem(2);
                        }
                    }
                });
        addSubscription(subscribe);
    }

}
