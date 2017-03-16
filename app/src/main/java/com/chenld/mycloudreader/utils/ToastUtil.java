package com.chenld.mycloudreader.utils;

import android.widget.Toast;

import com.chenld.mycloudreader.app.CloudReaderApplication;

/**
 * Created by chenld on 2017/3/9.
 * 单例Toast
 */

public class ToastUtil {
    private static Toast mToast;

    public static void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(CloudReaderApplication.getInstance(), text, Toast.LENGTH_SHORT);
        }
        mToast.setText(text);
        mToast.show();
    }
}
