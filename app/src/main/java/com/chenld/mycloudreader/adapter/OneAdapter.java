package com.chenld.mycloudreader.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.apkfuns.logutils.LogUtils;
import com.chenld.mycloudreader.R;
import com.chenld.mycloudreader.base.baseadapter.BaseRecyclerViewAdapter;
import com.chenld.mycloudreader.base.baseadapter.BaseRecyclerViewHolder;
import com.chenld.mycloudreader.bean.moviechild.SubjectsBean;
import com.chenld.mycloudreader.databinding.ItemOneBinding;
import com.chenld.mycloudreader.ui.one.OneMovieDetailActivity;
import com.chenld.mycloudreader.utils.CommonUtils;
import com.chenld.mycloudreader.utils.PerfectClickListener;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import javax.security.auth.Subject;

/**
 * Created by chenld on 2017/3/4.
 */

public class OneAdapter extends BaseRecyclerViewAdapter<SubjectsBean>{

    private Activity activity;

    public OneAdapter(Activity activity) {
        LogUtils.d("OneAdapter...");
        this.activity = activity;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtils.d("OneAdapter.....onCreateViewHolder");
        //创建
        return new ViewHolder(parent, R.layout.item_one);
    }

    //OneAdapter中使用自己的ViewHolder
    private class ViewHolder extends BaseRecyclerViewHolder<SubjectsBean, ItemOneBinding> {

        ViewHolder(ViewGroup context, int layoutId) {
            super(context, layoutId);
            LogUtils.d("ViewHolder");
        }

        @Override
        public void onBindViewHolder(final SubjectsBean positionData, final int position) {
            LogUtils.d("ViewHolder.....onBindViewHolder");
            if (positionData != null) {
                //设置变量数据SubjectsBean到item_one.xml中
                binding.setSubjectsBean(positionData);
                // 图片,通过自定义图片显示属性直接设置到xml中，而不需以下代码实现android:showMovieImg="@{subjectsBean.images.large}"
//                ImgLoadUtil.displayEspImage(positionData.getImages().getLarge(), binding.ivOnePhoto,0);
                // 导演
//                binding.tvOneDirectors.setText(StringFormatUtil.formatName(positionData.getDirectors()));
                // 主演
//                binding.tvOneCasts.setText(StringFormatUtil.formatName(positionData.getCasts()));
                // 类型
//                binding.tvOneGenres.setText("类型：" + StringFormatUtil.formatGenres(positionData.getGenres()));
                // 评分
//                binding.tvOneRatingRate.setText("评分：" + String.valueOf(positionData.getRating().getAverage()));
                // 分割线颜色
                binding.viewColor.setBackgroundColor(CommonUtils.randomColor());

                //设置动画,由nineoldandroids库实现
//                ViewHelper.setScaleX(itemView,0.6f);
                ViewHelper.setScaleX(itemView,0.8f);
                ViewHelper.setScaleY(itemView,0.8f);
                ViewPropertyAnimator.animate(itemView).scaleX(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();
                ViewPropertyAnimator.animate(itemView).scaleY(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();

                binding.llOneItem.setOnClickListener(new PerfectClickListener() {
                    @Override
                    protected void onNoDoubleClick(View v) {
                        if (position % 2 == 0) {

//                            SlideScrollViewActivity.start(activity, positionData, binding.ivOnePhoto);

//                            MovieDetailActivity.start(activity, positionData, binding.ivOnePhoto);
                            OneMovieDetailActivity.start(activity, positionData, binding.ivOnePhoto);

//                            TestActivity.start(activity, positionData, binding.ivOnePhoto);
//                            activity.overridePendingTransition(R.anim.push_fade_out, R.anim.push_fade_in);
                        } else {
//                            SlideScrollViewActivity.start(activity, positionData, binding.ivOnePhoto);
//                            SlideShadeViewActivity.start(activity, positionData, binding.ivOnePhoto);
                            OneMovieDetailActivity.start(activity, positionData, binding.ivOnePhoto);
                        }

                        // 这个可以
//                        SlideScrollViewActivity.start(activity, positionData, binding.ivOnePhoto);
//                        TestActivity.start(activity,positionData,binding.ivOnePhoto);
//                        v.getContext().startActivity(new Intent(v.getContext(), SlideScrollViewActivity.class));

//                        SlideShadeViewActivity.start(activity, positionData, binding.ivOnePhoto);

                    }
                });
            }
        }
    }
}
