package com.hy.michat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.hy.chatlibrary.MiChatHelper;
import com.hy.chatlibrary.bean.ChatGroupDetail;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.db.entity.ChatMessage;
import com.hy.chatlibrary.db.entity.InstructBean;
import com.hy.chatlibrary.listener.OnChatInputListener;
import com.hy.chatlibrary.listener.OnChatManagerListener;
import com.hy.chatlibrary.utils.StringUtil;
import com.hy.chatlibrary.utils.retrofit.RetrofitHelper;
import com.hy.michat.rabbitMQ.RabbitMQManager;
import com.hy.michat.retrofit.AppConfig;
import com.hy.michat.retrofit.BaseChatBO;
import com.hy.michat.retrofit.BaseResult;
import com.hy.michat.retrofit.FileApi;
import com.hy.michat.retrofit.GroupMemberBO;
import com.hy.michat.retrofit.IChatControl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.hy.michat.retrofit.AppConfig.HTTP_SERVER;

/**
 * @author:MtBaby
 * @date:2020/05/06 17:53
 * @desc:
 */
public class ChatManager implements OnChatInputListener, OnChatManagerListener {
    private static ChatManager mChatManager;
    private RabbitMQManager mRabbitMQManager;
    private MiChatHelper mMiChatHelper;
    private MiChatHelper.ChatMessageControl mChatGroupControl;
    private MessageHolder mMessageHolder;
//    private IChatMessageControl mOnNetMessageControl;

    private ChatManager() {
    }

    public static ChatManager getInstance() {
        if (mChatManager == null) {
            synchronized (ChatManager.class) {
                if (mChatManager == null) {
                    mChatManager = new ChatManager();
                }
            }
        }
        return mChatManager;
    }

    public void loginChatGroup(Context context, String userName, String userMQPW, String departId, String departName, String userDuty, int gender, String phone, String portraitUrl, RabbitMQManager imqManager) {
        mRabbitMQManager = imqManager;
        mMessageHolder = new MessageHolder();
        mMessageHolder.setId(userName);
//        mMessageHolder.setRole(1);
        mMessageHolder.setName(userName);
        mMessageHolder.setDepartId(departId);
        mMessageHolder.setDepartName(departName);
        mMessageHolder.setDuty(userDuty);
        mMessageHolder.setGender(gender);
        mMessageHolder.setMobile(phone);
        mMessageHolder.setPortrait(portraitUrl);
        mMiChatHelper = MiChatHelper.getInstance().loginIM(context, mMessageHolder, userMQPW, imqManager, this, this);

    }

    public void gotoChatGroup(Context mContext, ChatGroupDetail chatGroupDetail) {
        gotoChatGroup(mContext, chatGroupDetail.getMessageGroupId(), chatGroupDetail.getMessageGroupName(), chatGroupDetail.getMessageGroupDes());
    }

    //跳转到聊天界面
    public void gotoChatGroup(Context mContext, @NonNull String groupId, String groupName, String groupDes) {
        mChatGroupControl = mMiChatHelper.gotoChat(mContext, groupId, groupName, groupDes);
    }

    @Override
    public void onMessageSend(ChatMessage message, String chatMessageJson) {
        System.out.println("发送消息:" + chatMessageJson);
        if (message.getItemType() == 1 || message.getItemType() == 2 || message.getItemType() == 3 || message.getItemType() == 5 || message.getItemType() == 6 && !TextUtils.isEmpty(message.getInstructBean().getLocalFilePath())) {
            File file;
            final InstructBean instructBean = message.getInstructBean();
            if (message.getItemType() == 6) {
                String instructLocalFilePath = instructBean.getLocalFilePath();
                file = new File(StringUtil.isEmpty(instructLocalFilePath));
            } else {
                file = new File(message.getMessageLocalPath());
            }
            //如果用第二种方式上传头像   可以不用写      //f为file路径
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("filedata", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
            RetrofitHelper.buildRetrofit().create(FileApi.class).uploadFile("http://112.94.13.13:8006/file/uploadFile.do", filePart)
                    .subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            mChatGroupControl.onSendFail(message, e.getMessage());
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                String json = responseBody.string();
                                BaseResult baseResult = JSON.parseObject(json, BaseResult.class);
                                if (message.getItemType() == 6) {
                                    instructBean.setNetFilePath(AppConfig.FILE_SERVER + baseResult.getData().getId());
                                    instructBean.setNetThumbFilePath(AppConfig.FILE_SERVER + baseResult.getData().getAfterFileId());
                                } else {
                                    message.setMessageNetPath(AppConfig.FILE_SERVER + baseResult.getData().getId());
                                    message.setMessageThumbFilePath(AppConfig.FILE_SERVER + baseResult.getData().getAfterFileId());
                                }
                                mRabbitMQManager.sendChatMsg(RabbitMQManager.GROUP_ID, RabbitMQManager.PUSH_CHAT_MESSAGE, message);
                            } catch (IOException e) {
                                e.printStackTrace();
                                onError(e);
                            }
                        }
                    });
        } else {
            if (mRabbitMQManager.isLoginSuccess()) {
                new Thread(() -> mRabbitMQManager.sendChatMsg(RabbitMQManager.GROUP_ID, RabbitMQManager.PUSH_CHAT_MESSAGE, message)).start();
            }
        }

    }

    @Override
    public void onChatMessageCallBack(ChatMessage message) {
        new Thread(() -> mRabbitMQManager.sendChatMsg(RabbitMQManager.GROUP_ID, RabbitMQManager.PUSH_CHAT_MESSAGE_CALL_BACK, message)).start();
    }

    @Override
    public void onInitChatList(ChatMessage chatMessage, String mChatGroupId) {
//        System.out.println("初始化onInitChatList");
        RetrofitHelper.buildRetrofit().create(IChatControl.class).getChatGroupMember(HTTP_SERVER + "/info/list/all", mChatGroupId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseChatBO<List<GroupMemberBO>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseChatBO<List<GroupMemberBO>> listBaseChatBO) {
                        //以chatMessage的时间为查询开始时间，从服务器获取最新数据
                        if (listBaseChatBO.isSuccess()) {
                            List<GroupMemberBO> groupMemberBOList = listBaseChatBO.getData();
                            List<MessageHolder> groupMembers = new ArrayList<>();
                            for (int i = 0; i < groupMemberBOList.size(); i++) {
                                GroupMemberBO memberBO = groupMemberBOList.get(i);
                                MessageHolder messageHolder = new MessageHolder();
//                                messageHolder.setPortrait(DateModel.images[i]);
                                messageHolder.setMobile("1111111111" + i);
                                messageHolder.setGender(i % 2);
                                messageHolder.setDepartName("政治处");
                                messageHolder.setDuty("分局局长" + i);
                                messageHolder.setName(memberBO.getUserId());
                                messageHolder.setGroupName(memberBO.getUserId());
                                messageHolder.setId(memberBO.getUserId());
                                messageHolder.setDepartId("440106970002");
                                groupMembers.add(messageHolder);
                            }
                            mMiChatHelper.initChatGroupMember(groupMembers, mChatGroupId, "");
                        }

                    }
                });

        mMiChatHelper.initChatGroupList(null, mChatGroupId, "获取网络数据失败");
    }

    @Override
    public void onInitFriendList() {
        List<MessageHolder> groupMembers = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            MessageHolder messageHolder = new MessageHolder();
            messageHolder.setMobile("1111111111" + i);
            messageHolder.setGender(i % 2);
            messageHolder.setDepartName("政治处");
            messageHolder.setDuty("分局局长" + i);
            messageHolder.setName("test" + i);
            messageHolder.setGroupName("test" + i);
            messageHolder.setId("test" + i);
            messageHolder.setDepartId("440106970002");
            groupMembers.add(messageHolder);
        }
        mMiChatHelper.initFriendList(groupMembers, null);
    }

    @Override
    public void changeChatDisplayName(String mChatGroupId, ChatMessage chatMessage) {
//        new Thread(() -> mRabbitMQManager.sendChatDisPlayNameNotify(RabbitMQManager.GROUP_ID, mChatGroupId, chatMessage)).start();
        new Thread(() -> mRabbitMQManager.sendChatMsg(RabbitMQManager.GROUP_ID, RabbitMQManager.PUSH_UPDATE_CHAT_DISPLAY, chatMessage)).start();
    }

    @Override
    public void changeChatGroupName(String mChatGroupId, ChatMessage chatMessage) {
//        new Thread(() -> mRabbitMQManager.sendChatNameNotify(RabbitMQManager.GROUP_ID, mChatGroupId, chatMessage)).start();
        new Thread(() -> mRabbitMQManager.sendChatMsg(RabbitMQManager.GROUP_ID, RabbitMQManager.PUSH_UPDATE_CHAT_NAME, chatMessage)).start();
    }

    @Override
    public void changeChatGroupDesc(String mChatGroupId, ChatMessage chatMessage) {
        new Thread(() -> mRabbitMQManager.sendChatMsg(RabbitMQManager.GROUP_ID, RabbitMQManager.PUSH_UPDATE_CHAT_DESC, chatMessage)).start();
    }

    @Override
    public void addChatGroupMember(String mChatGroupId, ChatMessage chatMessage) {
        List<MessageHolder> newHolders = chatMessage.getNewHolders();
        MessageHolder messageHolder = newHolders.get(0);
        RetrofitHelper.buildRetrofit().create(IChatControl.class).addChatGroupMember(HTTP_SERVER + "/info/insert", mChatGroupId, messageHolder.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<BaseChatBO<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mChatGroupControl.addMemberFail(chatMessage, e.getMessage());
                    }

                    @Override
                    public void onNext(BaseChatBO<String> stringBaseChatBO) {
                        if (stringBaseChatBO.isSuccess()) {
                            new Thread(() -> mRabbitMQManager.sendChatMsg(RabbitMQManager.GROUP_ID, RabbitMQManager.PUSH_ADD_MEMBER, chatMessage)).start();
                        }
                    }
                });
    }

    @Override
    public void existChatGroup(String mChatGroupId, ChatMessage chatMessage) {
        RetrofitHelper.buildRetrofit().create(IChatControl.class).existChatGroup(HTTP_SERVER + "/info/delete/pk", mChatGroupId, chatMessage.getMessageHolderId())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<BaseChatBO<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mChatGroupControl.onExistMemberFail(chatMessage, e.getMessage());
                    }

                    @Override
                    public void onNext(BaseChatBO<String> stringBaseChatBO) {
                        if (stringBaseChatBO.isSuccess()) {
                            new Thread(() -> mRabbitMQManager.sendChatMsg(RabbitMQManager.GROUP_ID, RabbitMQManager.PUSH_EXIST_MEMBER, chatMessage)).start();
                            mChatGroupControl.onExistMemberSuccess(chatMessage);
                        }
                    }
                });
    }


}
