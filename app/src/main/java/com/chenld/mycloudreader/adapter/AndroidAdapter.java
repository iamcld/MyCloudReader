package com.chenld.mycloudreader.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.chenld.mycloudreader.R;
import com.chenld.mycloudreader.base.baseadapter.BaseRecyclerViewAdapter;
import com.chenld.mycloudreader.base.baseadapter.BaseRecyclerViewHolder;
import com.chenld.mycloudreader.bean.GankIoDataBean;
import com.chenld.mycloudreader.bean.GankIoDayBean;
import com.chenld.mycloudreader.databinding.ItemAndroidBinding;
import com.chenld.mycloudreader.utils.ImgLoadUtil;
import com.chenld.mycloudreader.view.webview.WebViewActivity;

/**
 * Created by chenld on 2017/3/21.
 */

public class AndroidAdapter extends BaseRecyclerViewAdapter<GankIoDataBean.ResultBean>{
    private boolean isAll = false;

    public void setAllType(boolean isAll) {
        this.isAll = isAll;
    }
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_android);
    }
    private class ViewHolder extends BaseRecyclerViewHolder<GankIoDataBean.ResultBean, ItemAndroidBinding> {

        public ViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        public void onBindViewHolder(final GankIoDataBean.ResultBean object, int position) {
            if (isAll && "福利".equals(object.getType())) {
                binding.ivAllWelfare.setVisibility(View.VISIBLE);
                binding.llWelfareOther.setVisibility(View.GONE);
                ImgLoadUtil.displayEspImage(object.getUrl(), binding.ivAllWelfare, 1);
            } else {
                binding.ivAllWelfare.setVisibility(View.GONE);
                binding.llWelfareOther.setVisibility(View.VISIBLE);
            }

            if (isAll) {
                binding.tvContentType.setVisibility(View.VISIBLE);
                binding.tvContentType.setText(" · " + object.getType());
            } else {
                binding.tvContentType.setVisibility(View.GONE);

            }
            binding.setResultsBean(object);

            // 显示gif图片会很耗内存
            if (object.getImages() != null
                    && object.getImages().size() > 0
                    && !TextUtils.isEmpty(object.getImages().get(0))) {
//                binding.ivAndroidPic.setVisibility(View.GONE);
                binding.ivAndroidPic.setVisibility(View.VISIBLE);
                ImgLoadUtil.displayGif(object.getImages().get(0), binding.ivAndroidPic);
            } else {
                binding.ivAndroidPic.setVisibility(View.GONE);
            }


            binding.llAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   WebViewActivity.loadUrl(v.getContext(), object.getUrl(), "加载中...");
                }
            });

        }
    }
}
