package com.hy.michat.retrofit;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @author:MtBaby
 * @date:2020/04/22 14:32
 * @desc:
 */
public interface FileApi {
    String UPLOAD_FILE_URL = AppConfig.HTTP_SERVER + "file/uploadFile.do";

    @POST()
    @Multipart
    Observable<ResponseBody> uploadFile(@Url String url, @Part MultipartBody.Part file);


}
