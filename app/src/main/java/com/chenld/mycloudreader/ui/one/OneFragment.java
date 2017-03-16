package com.chenld.mycloudreader.ui.one;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;

import com.apkfuns.logutils.LogUtils;
import com.chenld.mycloudreader.MainActivity;
import com.chenld.mycloudreader.R;
import com.chenld.mycloudreader.adapter.OneAdapter;
import com.chenld.mycloudreader.app.Constants;
import com.chenld.mycloudreader.app.ConstantsImageUrl;
import com.chenld.mycloudreader.base.BaseFragment;
import com.chenld.mycloudreader.bean.HotMovieBean;
import com.chenld.mycloudreader.databinding.FragmentOneBinding;
import com.chenld.mycloudreader.http.HttpUtils;
import com.chenld.mycloudreader.http.cache.ACache;
import com.chenld.mycloudreader.utils.ImgLoadUtil;
import com.chenld.mycloudreader.utils.PerfectClickListener;
import com.chenld.mycloudreader.utils.SPUtils;
import com.chenld.mycloudreader.utils.TimeUtil;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class OneFragment extends BaseFragment<FragmentOneBinding> {

    // 初始化完成后加载数据
    private boolean isPrepared = false;
    // 第一次显示时加载数据，第二次不显示
    private boolean isFirst = true;
    // 是否正在刷新（用于刷新数据时返回页面不再刷新）
    private boolean mIsLoading = false;
    private ACache aCache;
    private MainActivity activity;
    private HotMovieBean mHotMovieBean;

    //豆瓣电影TOP250布局头视图
    private View mHeaderView = null;
    private OneAdapter oneAdapter;

    @Override
    public int setContent() {
        return R.layout.fragment_one;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showContentView();
        aCache = ACache.get(getActivity());
        oneAdapter = new OneAdapter(activity);
        mHotMovieBean = (HotMovieBean) aCache.getAsObject(Constants.ONE_HOT_MOVIE);
        isPrepared = true;
        LogUtils.d("---OneFragment   --onActivityCreated");
    }

    /**
     * 懒加载
     * 从此页面新开activity界面返回此页面 不会走这里
     */
    @Override
    protected void loadData() {
        LogUtils.d("---OneFragment   --onActivityCreated");
        if (!isPrepared || !mIsVisible) {
            return;
        }

        // 显示，准备完毕，不是当天，则请求数据（正在请求时避免再次请求）
        String oneData = SPUtils.getString("one_data", "2016-11-26");
        if (!oneData.equals(TimeUtil.getData()) && !mIsLoading){
            showLoading();
            /**延迟执行防止卡顿*/
            postDelayLoad();
        }else {
            // 为了正在刷新时不执行这部分
            if (mIsLoading) {
                return;
            }
            if (!isFirst) {
                return;
            }

            showLoading();
            if (mHotMovieBean == null && !mIsLoading) {
                postDelayLoad();
            } else {
                bindingView.listOne.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (this) {
                            setAdapter(mHotMovieBean);
                            showContentView();
                        }
                    }
                }, 150);
                LogUtils.d("----缓存: " + oneData);
            }
        }
    }
    private void loadHotMovie() {
        Subscription subscription = HttpUtils.getInstance()
                .getDouBanServer()
                .getHotMovie()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HotMovieBean>() {
                    @Override
                    public void onCompleted() {
                        showContentView();
                    }

                    @Override
                    public void onError(Throwable e) {
                        showContentView();
                        if (oneAdapter != null && oneAdapter.getItemCount() == 0) {
                            showError();
                        }
                    }

                    @Override
                    public void onNext(HotMovieBean hotMovieBean) {
                        if (hotMovieBean != null){
                            aCache.remove(Constants.ONE_HOT_MOVIE);
                            // 保存12个小时
                            aCache.put(Constants.ONE_HOT_MOVIE, hotMovieBean, 43200);
                            setAdapter(hotMovieBean);
                            //保存请求日期
                            SPUtils.putString("one_data", TimeUtil.getData());
                            //刷新结束
                            mIsLoading = false;
                        }

                        //构造器中，第一个参数表示列数或者行数，第二个参数表示滑动方向,瀑布流
//                        bindingContentView.listOne.setLayoutManager(new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL));
                        // GridView
//                        bindingContentView.listOne.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                    }
                });
        addSubscription(subscription);
    }


    private void setAdapter(HotMovieBean hotMovieBean) {
        //recycleView设置为垂直
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        bindingView.listOne.setLayoutManager(mLayoutManager);

        // 加上这两行代码，下拉出提示才不会产生出现刷新头的bug，不加拉不下来
        bindingView.listOne.setPullRefreshEnabled(false);
        bindingView.listOne.clearHeader();

        bindingView.listOne.setLoadingMoreEnabled(false);
        // 需加，不然滑动不流畅
        bindingView.listOne.setNestedScrollingEnabled(false);
        bindingView.listOne.setHasFixedSize(false);

        if (mHeaderView == null){
            //头视图单独，这里没有使用databinding
            mHeaderView = View.inflate(getContext(), R.layout.header_item_one, null);
            View llMovieTop = mHeaderView.findViewById(R.id.ll_movie_top);
            ImageView ivImg = (ImageView) mHeaderView.findViewById(R.id.iv_img);
            ImgLoadUtil.displayRandom(3, ConstantsImageUrl.ONE_URL_01,ivImg);
            llMovieTop.setOnClickListener(new PerfectClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    DoubanTopActivity.start(v.getContext());
                }
            });
        }
        //将头布局加到recycleview顶部
        bindingView.listOne.addHeaderView(mHeaderView);
        oneAdapter.clear();
        oneAdapter.addAll(hotMovieBean.getSubjects());
        bindingView.listOne.setAdapter(oneAdapter);
        oneAdapter.notifyDataSetChanged();

        isFirst = false;
    }


    /**
     * 延迟执行，避免卡顿
     * 加同步锁，避免重复加载
     */
    private void postDelayLoad() {
        synchronized (this) {
            if (!mIsLoading) {
                mIsLoading = true;
                bindingView.listOne.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadHotMovie();
                    }
                }, 150);
            }
        }
    }

    @Override
    protected void onRefresh() {
        loadHotMovie();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d("--OneFragment   ----onDestroy");
    }

    /**
     * 从此页面新开activity界面返回此页面 走这里
     */
    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d("--OneFragment   ----onResume");
    }
}
