package com.hongy.adbclient.utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpUtil {

    public static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10,TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)  //连接超时
            .writeTimeout(10,TimeUnit.SECONDS)
            .addNetworkInterceptor(getLoggingInterceptor())   //打印http请求日志
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request()
                            .newBuilder()
                            //.addHeader("Content-Type", "application/x-www-form-urlencoded")
                            //.addHeader("User-Agent", "android")
                            .build();
                    return chain.proceed(request);
                }
            })
            .build();


    private static HttpLoggingInterceptor getLoggingInterceptor(){
        //添加网络日志工具
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {

            @Override
            public void log(String message) {
                //L.logE(message);
            }
        });
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logInterceptor;
    }
}
