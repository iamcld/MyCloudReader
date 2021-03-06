package com.chenld.mycloudreader.ui.book;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chenld.mycloudreader.R;
import com.chenld.mycloudreader.base.BaseFragment;
import com.chenld.mycloudreader.databinding.FragmentBookBinding;
import com.chenld.mycloudreader.ui.book.child.BookCustomFragment;
import com.chenld.mycloudreader.view.MyFragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by chenld on 17/03/06.
 *  展示书籍的页面
 *  v2.0 将会增加缓存 和 根据api完善更多内容
 */
public class BookFragment extends BaseFragment<FragmentBookBinding> {

    private ArrayList<String> mTitleList = new ArrayList<>(3);
    private ArrayList<Fragment> mFragments = new ArrayList<>(3);

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        showLoading();
        initFragmentList();
        /**
         * 注意使用的是：getChildFragmentManager：Fragment里面嵌套Fragment 的话：一定要用getChildFragmentManager()
         * 这样setOffscreenPageLimit()就可以添加上，保留相邻2个实例，切换时不会卡
         * 但会内存溢出，在显示时加载数据
         */
        MyFragmentPagerAdapter myAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), mFragments, mTitleList);
        bindingView.vpBook.setAdapter(myAdapter);
        // 左右预加载页面的个数
        bindingView.vpBook.setOffscreenPageLimit(2);
        myAdapter.notifyDataSetChanged();
        bindingView.tabBook.setTabMode(TabLayout.MODE_FIXED);
        bindingView.tabBook.setupWithViewPager(bindingView.vpBook);
        showContentView();

    }

    @Override
    public int setContent() {
        return R.layout.fragment_book;
    }


    private void initFragmentList() {
        mTitleList.add("文学");
        mTitleList.add("文化");
        mTitleList.add("生活");
        mFragments.add(BookCustomFragment.newInstance("文学"));
        mFragments.add(BookCustomFragment.newInstance("文化"));
        mFragments.add(BookCustomFragment.newInstance("生活"));
    }

}
