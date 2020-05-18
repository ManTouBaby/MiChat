package com.hy.michat.retrofit;

import java.util.List;

import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @author:MtBaby
 * @date:2020/05/13 16:25
 * @desc:
 */
public interface IChatControl {
    @POST()
    Observable<BaseChatBO<List<GroupMemberBO>>> getChatGroupMember(@Url String url, @Query("groupId") String groupId, @Query("userCode") String userCode);
}
