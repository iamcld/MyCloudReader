package com.chenld.mycloudreader.ui.one;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.apkfuns.logutils.LogUtils;
import com.chenld.mycloudreader.R;
import com.chenld.mycloudreader.adapter.DouBanTopAdapter;
import com.chenld.mycloudreader.base.BaseActivity;
import com.chenld.mycloudreader.bean.HotMovieBean;
import com.chenld.mycloudreader.databinding.ActivityDoubanTopBinding;
import com.chenld.mycloudreader.http.HttpUtils;
import com.example.xrecyclerview.XRecyclerView;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DoubanTopActivity extends BaseActivity<ActivityDoubanTopBinding> {

    private DouBanTopAdapter mDouBanTopAdapter;
    private int mStart = 0;
    private int mCount = 21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_douban_top);
        setTitle("豆瓣电影Top250");
        mDouBanTopAdapter = new DouBanTopAdapter(DoubanTopActivity.this);
        loadDouBanTop250();
        //上拉刷新
        bindingView.xrvTop.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                mStart += mCount;
                loadDouBanTop250();
            }
        });
    }

    private void loadDouBanTop250(){
        Subscription get = HttpUtils.getInstance()
                .getDouBanServer()
                .getMovieTop250(mStart, mCount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HotMovieBean>() {
                    @Override
                    public void onCompleted() {
                        showContentView();
                    }

                    @Override
                    public void onError(Throwable e) {
                        bindingView.xrvTop.refreshComplete();
                        if (mDouBanTopAdapter.getItemCount() == 0){
                            showError();
                        }
                    }

                    @Override
                    public void onNext(HotMovieBean hotMovieBean) {
                        LogUtils.d("mStart:" + mStart);
                        if (mStart == 0){
                            if (hotMovieBean != null && hotMovieBean.getSubjects() != null &&
                                    hotMovieBean.getSubjects().size() > 0){
                                mDouBanTopAdapter.clear();
                                mDouBanTopAdapter.addAll(hotMovieBean.getSubjects());
                                //构造器中，第一个参数表示列数或者行数，第二个参数表示滑动方向,瀑布流
                                bindingView.xrvTop.setLayoutManager(new StaggeredGridLayoutManager(3,
                                        StaggeredGridLayoutManager.VERTICAL));
                                bindingView.xrvTop.setAdapter(mDouBanTopAdapter);
                                bindingView.xrvTop.setPullRefreshEnabled(false);
                                bindingView.xrvTop.clearHeader();
                                bindingView.xrvTop.setLoadingMoreEnabled(true);
                                mDouBanTopAdapter.notifyDataSetChanged();

                            }else {
                                bindingView.xrvTop.setVisibility(View.GONE);
                            }
                        }else {
                            if (hotMovieBean != null && hotMovieBean.getSubjects() != null && hotMovieBean.getSubjects().size() > 0) {
                                bindingView.xrvTop.refreshComplete();
                                mDouBanTopAdapter.addAll(hotMovieBean.getSubjects());
                                mDouBanTopAdapter.notifyDataSetChanged();
                            } else {
                                bindingView.xrvTop.noMoreLoading();
                            }

                        }
                    }
                });

        addSubscription(get);
    }

    @Override
    protected void onRefresh() {
        loadDouBanTop250();
    }

    public static void start(Context mContext) {
        Intent intent = new Intent(mContext, DoubanTopActivity.class);
        mContext.startActivity(intent);
    }
}
