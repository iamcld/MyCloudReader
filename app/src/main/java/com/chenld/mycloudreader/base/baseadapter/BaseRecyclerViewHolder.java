package com.chenld.mycloudreader.base.baseadapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.apkfuns.logutils.LogUtils;

/**
 * 基础的RecyclerView ViewHolder
 * Created by chenld on 2017/3/4.
 */

public abstract class BaseRecyclerViewHolder<T, D extends ViewDataBinding> extends RecyclerView.ViewHolder {

    //布局文件对应的binding，理解成xml
    public D binding;

    /**
     * 构造函数
     * @param viewGroup
     * @param layoutId
     */
    public BaseRecyclerViewHolder(ViewGroup viewGroup, int layoutId) {
        // 注意要依附 viewGroup，不然显示item不全!!
        super(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), layoutId, viewGroup, false).getRoot());
        // 得到这个View绑定的Binding
        binding = DataBindingUtil.getBinding(this.itemView);
    }

    /**
     * @param object   the data of bind
     * @param position the item position of recyclerView
     */
    public abstract void onBindViewHolder(T object, final int position);

    /**
     * 当数据改变时，binding会在下一帧去改变数据，如果我们需要立即改变，就去调用executePendingBindings方法。
     */
    void onBaseBindViewHolder(T object, final int position) {
        LogUtils.d("BaseRecyclerViewHolder.....onBindViewHolder");
        onBindViewHolder(object, position);
        binding.executePendingBindings();
    }
}