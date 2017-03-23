package com.chenld.mycloudreader.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.chenld.mycloudreader.R;
import com.chenld.mycloudreader.base.baseadapter.BaseRecyclerViewAdapter;
import com.chenld.mycloudreader.base.baseadapter.BaseRecyclerViewHolder;
import com.chenld.mycloudreader.bean.moviechild.PersonBean;
import com.chenld.mycloudreader.databinding.ItemMovieDetailPersonBinding;
import com.chenld.mycloudreader.utils.PerfectClickListener;
import com.chenld.mycloudreader.view.webview.WebViewActivity;

/**
 * Created by chenld on 2017/3/8.
 */

public class MovieDetailAdapter extends BaseRecyclerViewAdapter<PersonBean> {
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_movie_detail_person);
    }

    private class ViewHolder extends BaseRecyclerViewHolder<PersonBean, ItemMovieDetailPersonBinding> {

        /**
         * 构造函数
         *
         * @param viewGroup
         * @param layoutId
         */
        public ViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        public void onBindViewHolder(final PersonBean bean, int position) {
            binding.setPersonBean(bean);
            binding.llItem.setOnClickListener(new PerfectClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    if (bean != null && !TextUtils.isEmpty(bean.getAlt())) {
                         WebViewActivity.loadUrl(v.getContext(), bean.getAlt(), bean.getName());
                    }
                }
            });

        }
    }
}
