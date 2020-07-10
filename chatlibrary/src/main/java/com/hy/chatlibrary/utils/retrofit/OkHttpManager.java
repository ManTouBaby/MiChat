package com.hy.chatlibrary.utils.retrofit;



import com.hy.chatlibrary.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @author:MtBaby
 * @date:2020/04/22 14:31
 * @desc:
 */
public class OkHttpManager {
    private static OkHttpClient okHttpClient;

    /**
     * 获取OkHttp单例，线程安全.
     *
     * @return 返回OkHttpClient单例
     */
    public static OkHttpClient getInstance() {
        if (okHttpClient == null) {
            synchronized (OkHttpManager.class) {
                if (okHttpClient == null) {
                    OkHttpClient.Builder builder = new OkHttpClient.Builder();

                    if (BuildConfig.DEBUG) {
                        // 拦截okHttp的日志，如果开启了会导致上传回调被调用两次
//                        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//                        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//                        builder.addInterceptor(interceptor);
                    }

                    // 超时时间
                    builder.connectTimeout(15, TimeUnit.SECONDS);// 15S连接超时
                    builder.readTimeout(20, TimeUnit.SECONDS);// 20s读取超时
                    builder.writeTimeout(20, TimeUnit.SECONDS);// 20s写入超时
                    // 错误重连
                    builder.retryOnConnectionFailure(true);
                    okHttpClient = builder.build();
                }
            }
        }
        return okHttpClient;
    }
}
