package com.hy.chatlibrary.utils.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
                    .baseUrl(AppConfig.HTTP_SERVER)
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
    }

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
