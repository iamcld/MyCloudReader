package com.chenld.mycloudreader.ui.gank.child;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chenld.mycloudreader.R;
import com.chenld.mycloudreader.adapter.AndroidAdapter;
import com.chenld.mycloudreader.app.Constants;
import com.chenld.mycloudreader.base.BaseFragment;
import com.chenld.mycloudreader.bean.GankIoDataBean;
import com.chenld.mycloudreader.databinding.FragmentCustomBinding;
import com.chenld.mycloudreader.http.HttpUtils;
import com.chenld.mycloudreader.http.RequestImpl;
import com.chenld.mycloudreader.http.cache.ACache;
import com.chenld.mycloudreader.model.GankOtherModel;
import com.chenld.mycloudreader.utils.SPUtils;
import com.chenld.mycloudreader.utils.ToastUtil;
import com.cocosw.bottomsheet.BottomSheet;
import com.example.xrecyclerview.XRecyclerView;

import rx.Subscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomFragment extends BaseFragment<FragmentCustomBinding> {
    private static final String TAG = "CustomFragment";
    private static final String TYPE = "mType";
    private String mType = "all";
    private int mPage = 1;
    private boolean mIsPrepared;
    private boolean mIsFirst = true;
    private AndroidAdapter mAndroidAdapter;
    private ACache mACache;
    private GankIoDataBean mAllBean;
    private View mHeaderView;
    private GankOtherModel mModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mACache = ACache.get(getContext());
        mModel = new GankOtherModel();

        // 禁止下拉刷新
        bindingView.xrvCustom.setPullRefreshEnabled(false);
        // 去掉刷新头
        bindingView.xrvCustom.clearHeader();
        mAndroidAdapter = new AndroidAdapter();
        bindingView.xrvCustom.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                mPage++;
                loadCustomData();
            }
        });
        // 准备就绪
        mIsPrepared = true;
    }

    @Override
    public int setContent() {
        return R.layout.fragment_custom;
    }

    @Override
    protected void loadData() {
        if (!mIsPrepared || !mIsVisible || !mIsFirst) {
            return;
        }

        if (mAllBean != null
                && mAllBean.getResults() != null
                && mAllBean.getResults().size() > 0) {
            showContentView();
            mAllBean = (GankIoDataBean) mACache.getAsObject(Constants.GANK_CUSTOM);
            setAdapter(mAllBean);
        } else {
            loadCustomData();
        }
    }
    private void loadCustomData() {
        mModel.setData(mType, mPage, HttpUtils.per_page_more);
        mModel.getGankIoData(new RequestImpl() {
            @Override
            public void loadSuccess(Object object) {
                showContentView();
                GankIoDataBean gankIoDataBean = (GankIoDataBean) object;
                if (mPage == 1) {
                    if (gankIoDataBean != null && gankIoDataBean.getResults() != null && gankIoDataBean.getResults().size() > 0) {
                        setAdapter(gankIoDataBean);

                        mACache.remove(Constants.GANK_CUSTOM);
                        // 缓存50分钟
                        mACache.put(Constants.GANK_CUSTOM, gankIoDataBean, 30000);
                    }
                } else {
                    if (gankIoDataBean != null && gankIoDataBean.getResults() != null && gankIoDataBean.getResults().size() > 0) {
                        bindingView.xrvCustom.refreshComplete();
                        mAndroidAdapter.addAll(gankIoDataBean.getResults());
                        mAndroidAdapter.notifyDataSetChanged();
                    } else {
                        bindingView.xrvCustom.noMoreLoading();
                    }
                }
            }

            @Override
            public void loadFailed() {
                showContentView();
                bindingView.xrvCustom.refreshComplete();
                if (mAndroidAdapter.getItemCount() == 0) {
                    showError();
                }
                if (mPage > 1) {
                    mPage--;
                }
            }

            @Override
            public void addSubscription(Subscription subscription) {
                CustomFragment.this.addSubscription(subscription);
            }
        });
    }
    /**
     * 设置adapter
     */
    private void setAdapter(GankIoDataBean mCustomBean) {
        if (mHeaderView == null) {
            mHeaderView = View.inflate(getContext(), R.layout.header_item_gank_custom, null);
            bindingView.xrvCustom.addHeaderView(mHeaderView);
        }
        initHeader(mHeaderView);

        boolean isAll = SPUtils.getString("gank_cala", "全部").equals("全部");
        mAndroidAdapter.clear();
        mAndroidAdapter.setAllType(isAll);
        mAndroidAdapter.addAll(mCustomBean.getResults());
        bindingView.xrvCustom.setLayoutManager(new LinearLayoutManager(getActivity()));
        bindingView.xrvCustom.setAdapter(mAndroidAdapter);
        mAndroidAdapter.notifyDataSetChanged();

        mIsFirst = false;
    }

    private void initHeader(View mHeaderView) {
        final TextView txName = (TextView) mHeaderView.findViewById(R.id.tx_name);
        String gankCala = SPUtils.getString("gank_cala", "全部");
        txName.setText(gankCala);
        View view = mHeaderView.findViewById(R.id.ll_choose_catalogue);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new BottomSheet.Builder(getActivity(), R.style.BottomSheet_StyleDialog).title("选择分类").sheet(R.menu.gank_bottomsheet).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.gank_all:
                                if (isOtherType("全部")) {
                                    txName.setText("全部");
                                    mType = "all";// 全部传 all
                                    mPage = 1;
                                    mAndroidAdapter.clear();
                                    SPUtils.putString("gank_cala", "全部");
                                    showLoading();
                                    loadCustomData();
                                }
                                break;
                            case R.id.gank_ios:
                                if (isOtherType("IOS")) {
                                    txName.setText("IOS");
                                    mType = "iOS";// 这里有严格大小写
                                    mPage = 1;
                                    mAndroidAdapter.clear();
                                    SPUtils.putString("gank_cala", "IOS");
                                    showLoading();
                                    loadCustomData();
                                }
                                break;
                            case R.id.gank_qian:
                                if (isOtherType("前端")) {
                                    changeContent(txName, "前端");
                                }
                                break;
                            case R.id.gank_app:
                                if (isOtherType("App")) {
                                    changeContent(txName, "App");
                                }
                                break;
                            case R.id.gank_movie:
                                if (isOtherType("休息视频")) {
                                    changeContent(txName, "休息视频");
                                }
                                break;
                            case R.id.gank_resouce:
                                if (isOtherType("拓展资源")) {
                                    changeContent(txName, "拓展资源");
                                }
                                break;
                        }
                    }
                }).show();

            }
        });
    }

    private void changeContent(TextView textView, String content) {
        textView.setText(content);
        mType = content;
        mPage = 1;
        mAndroidAdapter.clear();
        SPUtils.putString("gank_cala", content);
        showLoading();
        loadCustomData();
    }

    private boolean isOtherType(String selectType) {
        String clickText = SPUtils.getString("gank_cala", "全部");
        if (clickText.equals(selectType)) {
            ToastUtil.showToast("当前已经是" + selectType + "分类");
            return false;
        } else {
            return true;
        }
    }

    /**
     * 加载失败后点击后的操作
     */
    @Override
    protected void onRefresh() {
        loadCustomData();
    }

}
