package com.hy.chatlibrary.utils.retrofit;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @author:MtBaby
 * @date:2020/04/24 17:05
 * @desc:
 */
public interface FileUploadApi {
    @Streaming
    @GET()
    Observable<ResponseBody> downloadimage(@Url String url);
}
