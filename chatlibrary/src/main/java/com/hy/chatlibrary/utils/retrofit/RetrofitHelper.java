package com.hy.chatlibrary.utils.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hy.chatlibrary.utils.IMLog;

import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author:MtBaby
 * @date:2020/04/22 16:15
 * @desc:
 */
public class RetrofitHelper {
    private static Retrofit mRetrofit;
    static RetrofitHelper mRetrofitHelper;

    private RetrofitHelper() {
        if (mRetrofit == null) {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);
            mRetrofit = new Retrofit.Builder().client(OkHttpManager.getInstance())
                    .baseUrl("http://112.94.13.13:8006/")
                    .client(getOkHttpClient())
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
    }

    private static OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(interceptorLog)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    private static Interceptor interceptorLog = chain -> {
        Request request = chain.request();
        String requestDate = request.url().toString();
        long startTime = System.currentTimeMillis();
        Response response = chain.proceed(request);
        String bodyString = response.body() != null ? response.body().string() : null;
        MediaType mediaType = response.body() != null ? response.body().contentType() : null;
        long duration = System.currentTimeMillis() - startTime;
        IMLog.d("");
        IMLog.d("----------Request Start----------------");
        IMLog.d("|" + URLDecoder.decode(requestDate));
        IMLog.d("|" + bodyString);
        IMLog.d("----------Request End:" + duration + "毫秒----------");
        return response.newBuilder()
                .body(ResponseBody.create(mediaType, bodyString))
                .build();
    };

    public static synchronized RetrofitHelper buildRetrofit() {
        if (mRetrofitHelper == null) {
            synchronized (RetrofitHelper.class) {
                if (mRetrofitHelper == null) {
                    mRetrofitHelper = new RetrofitHelper();
                }
            }
        }
        return mRetrofitHelper;
    }

    public <T> T create(Class<T> clz) {
        return mRetrofit.create(clz);
    }

}
