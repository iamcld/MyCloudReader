package com.chenld.mycloudreader.model;

import android.text.TextUtils;

import com.apkfuns.logutils.LogUtils;
import com.chenld.mycloudreader.app.ConstantsImageUrl;
import com.chenld.mycloudreader.bean.AndroidBean;
import com.chenld.mycloudreader.bean.FrontpageBean;
import com.chenld.mycloudreader.bean.GankIoDayBean;
import com.chenld.mycloudreader.http.HttpUtils;
import com.chenld.mycloudreader.http.RequestImpl;
import com.chenld.mycloudreader.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by chenld on 2017/3/9.
 *
 */

public class EverydayModel {
    private String year = "2016";
    private String month = "11";
    private String day = "24";
    private static final String HOME_ONE = "home_one";
    private static final String HOME_TWO = "home_two";
    private static final String HOME_SIX = "home_six";

    public void setData(String year, String month, String day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public void showBanncerPage(final RequestImpl listener){
        Subscription get = HttpUtils.getInstance()
                .getDongTingServer()//由RestAdapter类来实现RetrofitHttpClient接口，同时接口里面的方法也实现
                .getFrontpage()//调用接口函数来和服务器交互,获取服务器数据FrontpageBean
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FrontpageBean>() {
                    @Override
                    public void onCompleted() {
                        LogUtils.d("轮播图获取成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.d("轮播图获取失败");
                        listener.loadFailed();
                    }

                    @Override
                    public void onNext(FrontpageBean frontpageBean) {
                        //成功回调
                        listener.loadSuccess(frontpageBean);
                    }
                });
        listener.addSubscription(get);
    }

    //显示RecycleView 数据
    public void showRecyclerViewData(final RequestImpl listener){
        SPUtils.putString(HOME_ONE, "");
        SPUtils.putString(HOME_TWO, "");
        SPUtils.putString(HOME_SIX, "");
        /**
         * 被观察者
         * flatMap:再返回一个被观察者
         */
        Func1<GankIoDayBean, Observable<List<List<AndroidBean>>>> func1 = new Func1<GankIoDayBean,
                Observable<List<List<AndroidBean>>>>() {

            @Override
            public Observable<List<List<AndroidBean>>> call(GankIoDayBean gankIoDayBean) {
                List<List<AndroidBean>> lists = new ArrayList<>();
                GankIoDayBean.ResultsBean results = gankIoDayBean.getResults();

                if (results.getAndroid() != null && results.getAndroid().size() > 0){
                    addUrlList(lists, results.getAndroid(), "Android");
                }
                if (results.getWelfare() != null && results.getWelfare().size() > 0) {
                    addUrlList(lists, results.getWelfare(), "福利");
                }
                if (results.getiOS() != null && results.getiOS().size() > 0) {
                    addUrlList(lists, results.getiOS(), "IOS");
                }
                if (results.getRestMovie() != null && results.getRestMovie().size() > 0) {
                    addUrlList(lists, results.getRestMovie(), "休息视频");
                }
                if (results.getResource() != null && results.getResource().size() > 0) {
                    addUrlList(lists, results.getResource(), "拓展资源");
                }
                if (results.getRecommend() != null && results.getRecommend().size() > 0) {
                    addUrlList(lists, results.getRecommend(), "瞎推荐");
                }
                if (results.getFront() != null && results.getFront().size() > 0) {
                    addUrlList(lists, results.getFront(), "前端");
                }
                if (results.getApp() != null && results.getApp().size() > 0) {
                    addUrlList(lists, results.getApp(), "App");
                }

                //just一般用于处理集合
                //just: 将传入的参数依次发送出来.即Android时调用一下观察者的onnext方法
                //福利时调用一下观察者的onnext方法
                //一次执行list中的元素
                return Observable.just(lists);
            }
        };

        /**
         * 观察者
         */
        Observer<List<List<AndroidBean>>> observer = new Observer<List<List<AndroidBean>>>(){

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                listener.loadFailed();
            }

            @Override
            public void onNext(List<List<AndroidBean>> lists) {
                listener.loadSuccess(lists);
            }
        };

        /**
         * 订阅
         */
        Subscription subscription = HttpUtils.getInstance()
                .getGankIOServer()
                .getGankIoDay(year, month, day)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(func1)//返回的是一个Observable对象，主要作用是返回GankIoDayBean类中多个List<AndroidBean>
                //属性，即List<List<AndroidBean>>。一对多的形式。
                //map是把A变成B，而flatMap是把A变成Observable<B>
                .subscribe(observer);

        listener.addSubscription(subscription);

    }
    // subList没有实现序列化！缓存时会出错！
    private void addUrlList(List<List<AndroidBean>> lists, List<AndroidBean> arrayList, String typeTitle){
        //title
        AndroidBean bean = new AndroidBean();
        bean.setType_title(typeTitle);
        ArrayList<AndroidBean> androidBeen = new ArrayList<>();
        androidBeen.add(bean);
        lists.add(androidBeen);

        int androidSize = arrayList.size();

        if (androidSize > 0 && androidSize < 4) {

            lists.add(addUrlList(arrayList, androidSize));
        } else if (androidSize >= 4) {

            ArrayList<AndroidBean> list1 = new ArrayList<>();
            ArrayList<AndroidBean> list2 = new ArrayList<>();

            for (int i = 0; i < androidSize; i++) {
                if (i < 3) {
                    list1.add(getAndroidBean(arrayList, i, androidSize));
                } else if (i < 6) {
                    list2.add(getAndroidBean(arrayList, i, androidSize));
                }
            }
            lists.add(list1);
            lists.add(list2);
        }
    }


    private AndroidBean getAndroidBean(List<AndroidBean> arrayList, int i, int androidSize) {

        AndroidBean androidBean = new AndroidBean();
        // 标题
        androidBean.setDesc(arrayList.get(i).getDesc());
        // 类型
        androidBean.setType(arrayList.get(i).getType());
        // 跳转链接
        androidBean.setUrl(arrayList.get(i).getUrl());
        // 随机图的url
        if (i < 3) {
            androidBean.setImage_url(ConstantsImageUrl.HOME_SIX_URLS[getRandom(3)]);//三小图
        } else if (androidSize == 4) {
            androidBean.setImage_url(ConstantsImageUrl.HOME_ONE_URLS[getRandom(1)]);//一图
        } else if (androidSize == 5) {
            androidBean.setImage_url(ConstantsImageUrl.HOME_TWO_URLS[getRandom(2)]);//两图
        } else if (androidSize == 6) {
            androidBean.setImage_url(ConstantsImageUrl.HOME_SIX_URLS[getRandom(3)]);//三小图
        }
        return androidBean;
    }

    private List<AndroidBean> addUrlList(List<AndroidBean> arrayList, int androidSize) {
        List<AndroidBean> tempList = new ArrayList<>();
        for (int i = 0; i < androidSize; i++) {
            AndroidBean androidBean = new AndroidBean();
            // 标题
            androidBean.setDesc(arrayList.get(i).getDesc());
            // 类型
            androidBean.setType(arrayList.get(i).getType());
            // 跳转链接
            androidBean.setUrl(arrayList.get(i).getUrl());
//            DebugUtil.error("---androidSize:  " + androidSize);
            // 随机图的url
            if (androidSize == 1) {
                androidBean.setImage_url(ConstantsImageUrl.HOME_ONE_URLS[getRandom(1)]);//一图
            } else if (androidSize == 2) {
                androidBean.setImage_url(ConstantsImageUrl.HOME_TWO_URLS[getRandom(2)]);//两图
            } else if (androidSize == 3) {
                androidBean.setImage_url(ConstantsImageUrl.HOME_SIX_URLS[getRandom(3)]);//三图
            }
            tempList.add(androidBean);
        }
        return tempList;
    }

    /**
     * 取不同的随机图，在每次网络请求时重置
     */
    private int getRandom(int type) {
        String saveWhere = null;
        int urlLength = 0;
        if (type == 1) {
            saveWhere = HOME_ONE;
            urlLength = ConstantsImageUrl.HOME_ONE_URLS.length;
        } else if (type == 2) {
            saveWhere = HOME_TWO;
            urlLength = ConstantsImageUrl.HOME_TWO_URLS.length;
        } else if (type == 3) {
            saveWhere = HOME_SIX;
            urlLength = ConstantsImageUrl.HOME_SIX_URLS.length;
        }

        String home_six = SPUtils.getString(saveWhere, "");
        if (!TextUtils.isEmpty(home_six)) {
            // 已取到的值
            String[] split = home_six.split(",");

            Random random = new Random();
            for (int j = 0; j < urlLength; j++) {
                int randomInt = random.nextInt(urlLength);

                boolean isUse = false;
                for (String aSplit : split) {
                    if (!TextUtils.isEmpty(aSplit) && String.valueOf(randomInt).equals(aSplit)) {
                        isUse = true;
                        break;
                    }
                }
                if (!isUse) {
                    StringBuilder sb = new StringBuilder(home_six);
                    sb.insert(0, randomInt + ",");
                    SPUtils.putString(saveWhere, sb.toString());
                    return randomInt;
                }
            }

        } else {
            Random random = new Random();
            int randomInt = random.nextInt(urlLength);
            SPUtils.putString(saveWhere, randomInt + ",");
            return randomInt;
        }
        return 0;
    }

}
