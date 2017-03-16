package com.chenld.mycloudreader.model;

import com.apkfuns.logutils.LogUtils;
import com.chenld.mycloudreader.bean.GankIoDataBean;
import com.chenld.mycloudreader.http.HttpUtils;
import com.chenld.mycloudreader.http.RequestImpl;

import rx.Observer;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chenld on 2017/3/9.
 * 分类数据: http://gank.io/api/data/数据类型/请求个数/第几页  的Model
 * 好处之一是请求数据接口可以统一，不用每个地方都写请求的接口，更换接口方便。
 * 其实代码量也没有减少多少，但维护起来方便。
 */

public class GankOtherModel {

    private String id;
    private int page;
    private int per_page;

    public void setData(String id, int page, int per_page) {
        this.id = id;
        this.page = page;
        this.per_page = per_page;
    }

    public void getGankIoData(final RequestImpl listener) {
        //和服务器交互，获取json数据，对应的实体类为gankIoDataBean
        Subscription get = HttpUtils.getInstance()
                .getGankIOServer()//由RestAdapter类来实现RetrofitHttpClient接口，同时接口里面的方法也实现
                .getGankIoData(id, page, per_page)//调用接口函数来和服务器交互,获取服务器数据GankIoDataBean
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GankIoDataBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.loadFailed();
                    }

                    @Override
                    public void onNext(GankIoDataBean gankIoDataBean) {
                        LogUtils.d("当前线程:" + Thread.currentThread());
                        LogUtils.d("gankIoDataBean:" + gankIoDataBean);
                        listener.loadSuccess(gankIoDataBean);
                    }
                });
        listener.addSubscription(get);
    }
}
