package com.chenld.mycloudreader.app;

import android.app.Application;

import com.apkfuns.logutils.LogLevel;
import com.apkfuns.logutils.LogUtils;
import com.chenld.mycloudreader.http.HttpUtils;

/**
 * Created by chenld on 2017/3/1.
 */

public class CloudReaderApplication extends Application {
    private static CloudReaderApplication cloudReaderApplication;

    public static CloudReaderApplication getInstance() {
        return cloudReaderApplication;
    }

    @SuppressWarnings("unused")
    @Override
    public void onCreate() {
        super.onCreate();
        cloudReaderApplication = this;
        HttpUtils.getInstance().setContext(getApplicationContext());

        /**
         *configAllowLog:是否允许日志输出
         * configTagPrefix:日志log的前缀
         * configShowBorders:是否显示边界
         * configLevel:日志显示等级
         */
        LogUtils.getLogConfig().configAllowLog(true).configTagPrefix("LogUtils")
                .configShowBorders(false).configLevel(LogLevel.TYPE_DEBUG);

        LogUtils.v("test...");
        LogUtils.d("test...");
        LogUtils.i("test...");
        LogUtils.w("test...");
        LogUtils.e("test...");
    }
}
