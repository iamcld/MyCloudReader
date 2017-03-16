package com.chenld.mycloudreader.http;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit.Ok3Client;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by chenld on 2017/3/5.
 * 网络请求工具类
 * <p>
 * 豆瓣api:
 * 问题：API限制为每分钟40次，一不小心就超了，马上KEY就被封,用不带KEY的API，每分钟只有可怜的10次。
 * 返回：code:112（rate_limit_exceeded2 IP 访问速度限制）
 * 解决：1.使用每分钟访问次数限制（客户端）2.更换ip (更换wifi)
 * 豆瓣开发者服务使用条款: https://developers.douban.com/wiki/?title=terms
 */

public class HttpUtils {
    private static final RestAdapter.LogLevel logLevel = RestAdapter.LogLevel.FULL;
    // gankio、豆瓣、动听（轮播图）
    private final static String API_GANKIO = "http://gank.io/api";
    private final static String API_DOUBAN = "https://api.douban.com";
    private final static String API_DONGTING = "http://api.dongting.com";

    private RestAdapter gankIoRestAdapter;
    private Gson gson;
    private Context context;
    private static HttpUtils sHttpUtils;
    private static RetrofitHttpClient sGankioClient;
    private static RetrofitHttpClient sDouBanClient;
    private static RetrofitHttpClient sDongTingClient;
    /**
     * 分页数据，每页的数量
     */
    public static int per_page = 10;
    public static int per_page_more = 20;

    public void setContext(Context context) {
        this.context = context;
    }

    public static HttpUtils getInstance() {
        if (sHttpUtils == null) {
            sHttpUtils = new HttpUtils();
        }
        return sHttpUtils;
    }

    public RetrofitHttpClient getGankIOServer(){
        if (sGankioClient == null){
            //通过RestAdapter  类来生成一个  RetrofitHttpClient  接口的实现
            sGankioClient = getRestAdapterAdapter(API_GANKIO).create(RetrofitHttpClient.class);
        }
        return sGankioClient;
    }

    public RetrofitHttpClient getDouBanServer() {
        if (sDouBanClient == null) {
            sDouBanClient = getRestAdapterAdapter(API_DOUBAN).create(RetrofitHttpClient.class);
        }
        return sDouBanClient;
    }

    public RetrofitHttpClient getDongTingServer(){
        if (sDongTingClient == null){
            sDongTingClient = getRestAdapterAdapter(API_DONGTING).create(RetrofitHttpClient.class);
        }
        return sDongTingClient;
    }

    private RestAdapter getRestAdapterAdapter(String api) {
        File cacheFile = new File(context.getApplicationContext().getCacheDir().getAbsolutePath(), "HttpCache");
        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(cacheFile, cacheSize);
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.cache(cache);
        okBuilder.readTimeout(20, TimeUnit.SECONDS);
        okBuilder.connectTimeout(10, TimeUnit.SECONDS);
        okBuilder.writeTimeout(20, TimeUnit.SECONDS);
        OkHttpClient client = okBuilder.build();

        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setClient(new Ok3Client(client));
        builder.setEndpoint(api);//设置远程地址
        //服务器返回的是json格式的数组,所以设置GsonConverter完成对象转化
        builder.setConverter(new GsonConverter(getGson()));
//            builder.setErrorHandler(new ErrorHandler() {
//                @Override
//                public Throwable handleError(RetrofitError retrofitError) {
//                    return RxThrowable.ResolveRetrofitError(context, retrofitError);
//                }
//            });
//            builder.setRequestInterceptor(new RequestInterceptor() {
//                @Override
//                public void intercept(RequestFacade requestFacade) {
//                    requestFacade.addHeader("Accept", "application/json;versions=1");
//                    UserUtils userUtils = new UserUtils(context, new SQuser(context).selectKey());
//                    String token = userUtils.getToken();
//                    requestFacade.addHeader("token", token);
//
//                    if (CheckNetwork.isNetworkConnected(context)) {
//                        int maxAge = 60;
//                        requestFacade.addHeader("Cache-Control", "public, max-age=" + maxAge);
//                    } else {
//                        int maxStale = 60 * 60 * 24 * 28;
//                        requestFacade.addHeader("Cache-Control", "public, only-if-cached, " +
//                                "max-stale=" + maxStale);
//                    }
//                }
//            });
        gankIoRestAdapter = builder.build();
        gankIoRestAdapter.setLogLevel(logLevel);
        return gankIoRestAdapter;
    }

    private Gson getGson() {
        if (gson == null) {
            GsonBuilder builder = new GsonBuilder();
            builder.setFieldNamingStrategy(new AnnotateNaming());
            builder.serializeNulls();
            builder.excludeFieldsWithModifiers(Modifier.TRANSIENT);
            gson = builder.create();
        }
        return gson;
    }

    private static class AnnotateNaming implements FieldNamingStrategy {
        @Override
        public String translateName(Field field) {
            //要获取类方法和字段的注解信息，必须通过Java的反射技术来获取 Annotation对象
            ParamNames a = field.getAnnotation(ParamNames.class);
            return a != null ? a.value() : FieldNamingPolicy.IDENTITY.translateName(field);
        }
    }


}
