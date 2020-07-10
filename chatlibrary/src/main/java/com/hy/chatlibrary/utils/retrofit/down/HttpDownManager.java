package com.hy.chatlibrary.utils.retrofit.down;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class HttpDownManager {
    private final HashSet<Object> downInfos;
    /*回调sub队列*/
    private HashMap<String, ProgressDownSubscriber> subMap;
    /*单利对象*/
    private volatile static HttpDownManager INSTANCE;

    private HttpDownManager() {
        downInfos = new HashSet<>();
        subMap = new HashMap<>();
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static HttpDownManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpDownManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpDownManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 开始下载
     */
    public void startDown(DownInfo info){
        /*正在下载不处理*/
        if(info==null||subMap.get(info.getUrl())!=null){
            return;
        }
        /*添加回调处理类*/
        ProgressDownSubscriber subscriber=new ProgressDownSubscriber(info);
        /*记录回调sub*/
        subMap.put(info.getUrl(),subscriber);
        /*获取service，多次请求公用一个sercie*/
        HttpService httpService;
        if(downInfos.contains(info)){
            httpService=info.getService();
        }else{
            DownloadInterceptor interceptor = new DownloadInterceptor(subscriber);
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            //手动创建一个OkHttpClient并设置超时时间
            builder.connectTimeout(info.getDEFAULT_TIMEOUT(), TimeUnit.SECONDS);
            builder.addInterceptor(interceptor);

            Retrofit retrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .baseUrl(info.getBaseUrl())
                    .build();
            httpService= retrofit.create(HttpService.class);
            info.setService(httpService);
        }
        /*得到rx对象-上一次下載的位置開始下載*/
        httpService.download("bytes=" + info.getReadLength() + "-",info.getUrl())
                /*指定线程*/
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                /*失败后的retry配置*/
//                .retryWhen(new RetryWhenNetworkException())
                /*读取下载写入文件*/
//                .map(new Func1<ResponseBody, DownInfo>() {
//                    @Override
//                    public DownInfo call(ResponseBody responseBody) {
//                        try {
//                            writeCache(responseBody,new File(info.getSavePath()),info);
//                        } catch (IOException e) {
//                            /*失败抛出异常*/
//                            throw new HttpTimeException(e.getMessage());
//                        }
//                        return info;
//                    }
//                })
                /*回调线程*/
                .observeOn(AndroidSchedulers.mainThread())
                /*数据回调*/
                .subscribe(subscriber);

    }
}
