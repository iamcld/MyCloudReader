package com.chenld.mycloudreader.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chenld.mycloudreader.R;
import com.chenld.mycloudreader.base.baseadapter.BaseRecyclerViewAdapter;
import com.chenld.mycloudreader.base.baseadapter.BaseRecyclerViewHolder;
import com.chenld.mycloudreader.bean.moviechild.SubjectsBean;
import com.chenld.mycloudreader.databinding.ItemDoubanTopBinding;
import com.chenld.mycloudreader.ui.one.OneMovieDetailActivity;
import com.chenld.mycloudreader.utils.PerfectClickListener;

/**
 * Created by chenld on 2017/3/5.
 */

public class DouBanTopAdapter extends BaseRecyclerViewAdapter<SubjectsBean> {

    private Activity activity;

    public DouBanTopAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_douban_top);
    }

    class ViewHolder extends BaseRecyclerViewHolder<SubjectsBean, ItemDoubanTopBinding>{

        /**
         * 构造函数
         *
         * @param parent
         * @param layoutId
         */
        public ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
        }

        @Override
        public void onBindViewHolder(final SubjectsBean bean, final int position) {
            //为item_douban_top.xml设置数据
            binding.setBean(bean);
            /**
             * 当数据改变时，binding会在下一帧去改变数据，如果我们需要立即改变，就去调用executePendingBindings方法。
             */
            binding.executePendingBindings();
            binding.llItemTop.setOnClickListener(new PerfectClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    OneMovieDetailActivity.start(activity, bean, binding.ivTopPhoto);
                }
            });

            binding.llItemTop.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    View view = View.inflate(v.getContext(), R.layout.title_douban_top, null);
                    TextView titleTop = (TextView) view.findViewById(R.id.title_top);
                    titleTop.setText("Top" + (position + 1) + ": " + bean.getTitle());
                    builder.setCustomTitle(view);
                    builder.setPositiveButton("查看详情", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            OneMovieDetailActivity.start(activity, bean, binding.ivTopPhoto);
                        }
                    });
                    builder.show();
                    return false;
                }
            });

        }
    }
}
