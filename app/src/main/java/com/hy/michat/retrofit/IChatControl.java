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
    Observable<BaseChatBO<List<GroupMemberBO>>> getChatGroupMember(@Url String url, @Query("groupId") String groupId);

    @POST()
    Observable<BaseChatBO<String>> addChatGroupMember(@Url String url, @Query("groupId") String groupId, @Query("userId") String userId);

    @POST()
    Observable<BaseChatBO<String>> existChatGroup(@Url String url, @Query("groupId") String groupId, @Query("userId") String userId);

    @POST()
    Observable<BaseChatBO<List<GroupBo>>> chatGroupList(@Url String url, @Query("userId") String userId);

    @POST()
    Observable<BaseChatBO<List<JoinChatResult>>> joinGroup(@Url String url, @Query("groupId") String groupId, @Query("userIds") String userIds);
}
