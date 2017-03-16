package com.chenld.mycloudreader.http;

import rx.Subscription;

/**
 * Created by chenld on 2017/3/9.
 * 用于数据请求的回调
 */

public interface RequestImpl {

    void loadSuccess(Object object);

    void loadFailed();

    void addSubscription(Subscription subscription);
}
