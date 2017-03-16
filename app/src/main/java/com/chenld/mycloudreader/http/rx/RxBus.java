package com.chenld.mycloudreader.http.rx;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by chenld on 2017/3/4.
 */
public class RxBus {
    /**
     * 参考网址: http://hanhailong.com/2015/10/09/RxBus%E2%80%94%E9%80%9A%E8%BF%87RxJava%E6%9D%A5%E6%9B%BF%E6%8D%A2EventBus/
     *          http://www.loongwind.com/archives/264.html
     *          https://theseyears.gitbooks.io/android-architecture-journey/content/rxbus.html
     */
    private static volatile RxBus mDefaultInstance;

    private RxBus() {
    }

    //首先添加一个默认的事件总线,这样, 每次就可以这么发送事件:RxBus.getDefault().post(new TapEvent());
    //但是呢, 每次接受事件的时候都需要筛选一遍:if(event instanceof TapEvent)
    public static RxBus getDefault() {
        if (mDefaultInstance == null) {
            synchronized (RxBus.class) {
                if (mDefaultInstance == null) {
                    mDefaultInstance = new RxBus();
                }
            }
        }
        return mDefaultInstance;
    }

    //创建被观察者和观察者
    //Subject同时充当了Observer和Observable的角色，Subject是非线程安全的，要避免该问题，需要将
    // Subject转换为一个 SerializedSubject ，上述RxBus类中把线程非安全的PublishSubject包装成线程安全的Subject。
    private final Subject<Object, Object> _bus = new SerializedSubject<>(PublishSubject.create());


    public void send(Object o) {
        _bus.onNext(o);
    }



    public Observable<Object> toObservable() {
        return _bus;
    }
    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     * @param eventType 事件类型
     * @param <T>
     * @return
     */
    public <T> Observable<T> toObservable(Class<T> eventType) {
        return _bus.ofType(eventType);
    }

    /**
     * RxBus.getDefault()._bus:既是订阅者（观察者），又是发布者(被观察者),此处是作为发布者来发布事件
     *  //发送事件
     * 提供了一个新的事件,根据code进行分发,即发送信息体带有code标志，接收处就可以根据该标志和object类判断具体是哪个
     * 1、用于解决使用Eventbus时，发送相同类型的事件或者消息的时候接收的时候无法区分区分
     * 2、配合 public <T> Observable<T> toObservable(final int code, final Class<T> eventType) 函数一起使用
     * @param code 事件code
     * @param o
     */
    public void post(int code, Object o){
        //将code跟object用RxBusBaseMessage类进行了封装.
        //即用该RxBusBaseMessage类可以区分具体是哪种类型
        //发送RxBusBaseMessage类型和指定code的事件
        _bus.onNext(new RxBusBaseMessage(code,o));

    }


    /**
     * 过滤类型
     * 根据传递的code和 eventType 类型返回特定类型(eventType)的 被观察者
     * 对于注册了code为0，class为voidMessage的观察者，那么就接收不到code为0之外的voidMessage。
     * @param code 事件code
     * @param eventType 事件类型
     * @param <T>
     * @return
     */

    //RxBus.getDefault()._bus:既是订阅者（观察者），又是发布者(被观察者),此处是作为订阅者
    //配合post函数一起使用，用于解决使用Eventbus库时，存在发送相同类型的事件或者消息的时候接收的时候无法区分区分的问题
    //　toObservable(final int code, final Class<T> eventType)对传入code的事件进行分发,
    // 接收RxBusBaseMessage类型和指定code的事件

    // 1、先调用bus.ofType(Message.class)返回Message类的观察者,ofType操作符只发射指定类型的数据
    // 2、然后通过filter操作符返回Message里code跟Object类型跟传入的类型都匹配的观察者
    // 3、再通过map操作符返回Message里的object对象.map作用：修改对象变量
    // 4、最后通过cast转化为特定类的观察者.
    //这样如果有多个消息是相同类型的话就可以通过不同的code进行区分了.
    public <T> Observable<T> toObservable(final int code, final Class<T> eventType) {
        return _bus.ofType(RxBusBaseMessage.class)//ofType = filter + cast，1、先过滤出RxBusBaseMessage类型的类集合
                .filter(new Func1<RxBusBaseMessage,Boolean>() {//2、再过滤出RxBusBaseMessage类集合中指定code的类
                    @Override
                    public Boolean call(RxBusBaseMessage o) {
                        //过滤code和eventType都相同的事件
                        return o.getCode() == code && eventType.isInstance(o.getObject());
                    }
                }).map(new Func1<RxBusBaseMessage,Object>() {
                    @Override
                    public Object call(RxBusBaseMessage o) {
                        return o.getObject();
                    }
                }).cast(eventType);
    }
    /**
     * 判断是否有订阅者
     */
    public boolean hasObservers() {
        return _bus.hasObservers();
    }


}
