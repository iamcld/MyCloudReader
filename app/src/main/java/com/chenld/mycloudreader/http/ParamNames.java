package com.chenld.mycloudreader.http;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chenld on 2017/3/4.
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)// 注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target(ElementType.FIELD)//表示该注解可以用于什么地方
public @interface ParamNames {
    String value();
}
