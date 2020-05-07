package com.hy.michat;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.hy.chatlibrary.MiChatHelper;
import com.hy.chatlibrary.bean.ChatGroupDetail;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.db.ChatMessage;
import com.hy.chatlibrary.listener.OnChatInputListener;
import com.hy.chatlibrary.listener.OnNetMessageControl;
import com.hy.chatlibrary.utils.retrofit.AppConfig;
import com.hy.chatlibrary.utils.retrofit.RetrofitHelper;
import com.hy.michat.rabbitMQ.BaseResult;
import com.hy.michat.rabbitMQ.EvenBusChatMessage;
import com.hy.michat.retrofit.FileApi;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author:MtBaby
 * @date:2020/05/06 17:53
 * @desc:
 */
public class ChatManager implements OnChatInputListener {
    private static ChatManager mChatManager;
    private MiChatHelper mMiChatHelper;
    private OnNetMessageControl mOnNetMessageControl;

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

    public void loginChatGroup(String fileDir, String userName, String departId, String departName, String userDuty, int gender, String phone, String portraitUrl) {
        MessageHolder messageHolder = new MessageHolder();
        messageHolder.setId(userName);
        messageHolder.setName(userName);
        messageHolder.setDepartId(departId);
        messageHolder.setDepartName(departName);
        messageHolder.setDuty(userDuty);
        messageHolder.setGender(gender);
        messageHolder.setMobile(phone);
        messageHolder.setPortrait(portraitUrl);

        MiChatHelper.Option option = new MiChatHelper.Option();
        option.setFileDirName(fileDir)
                .setNetTimeUrl("http://www.baidu.com")
                .setOpenNetTime(true)
                .setMessageHolder(messageHolder)
                .setOnChatInputListener(this);
        mMiChatHelper = MiChatHelper.getInstance().setOption(option);
    }

    public void gotoChatGroup(Context mContext, ChatGroupDetail chatGroupDetail) {
        gotoChatGroup(mContext, chatGroupDetail.getMessageGroupId(), chatGroupDetail.getMessageGroupName(), chatGroupDetail.getMessageGroupDes(), chatGroupDetail.getGroupMembers());
    }

    public void gotoChatGroup(Context mContext, String groupId, String groupName, String groupDes, ArrayList<MessageHolder> groupMembers) {
        mMiChatHelper.gotoChat(mContext, groupId, groupName, groupDes, groupMembers);
    }

    @Override
    public void onMessageSend(ChatMessage message, String chatMessageJson) {
        message.setMessageStatus(0);
        mOnNetMessageControl.onSendSuccess(message);
        if (message.getItemType() == 1 || message.getItemType() == 2 || message.getItemType() == 3 || message.getItemType() == 5) {
            File file = new File(message.getMessageContent());
            //如果用第二种方式上传头像   可以不用写      //f为file路径
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("filedata", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
            RetrofitHelper.buildRetrofit().create(FileApi.class).uploadFile(filePart)
                    .subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
//                            System.out.println("文件上传失败：" + e.getMessage());
                            mOnNetMessageControl.onSendFail(message);
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                String json = responseBody.string();
//                                System.out.println("文件上传结果:" + json);
                                BaseResult baseResult = JSON.parseObject(json, BaseResult.class);
                                message.setMessageContent(AppConfig.FILE_SERVER + baseResult.getData().getId());
//                                mRabbitMQManager.sendMsg(RabbitMQManager.GROUP_ID, mGroupId.getText().toString(), mUserName.getText().toString(), message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } else {
//            if (mRabbitMQManager.isLoginSuccess()) {
//                new Thread(() -> mRabbitMQManager.sendMsg(RabbitMQManager.GROUP_ID, mGroupId.getText().toString(), mUserName.getText().toString(), message)).start();
//            }
        }

    }


    @Override
    public void onChatMessageCallBack(ChatMessage message) {

    }

    @Override
    public void onInitChatList(ChatMessage chatMessage, OnNetMessageControl onNetMessageControl) {
        //以chatMessage的时间为查询开始时间，从服务器获取最新数据
        mOnNetMessageControl = onNetMessageControl;
        EventBus.getDefault().register(this);
//        mOnNetMessageControl.addNetMessages();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    private void sendMessage(final ChatMessage message) {

        Observable.timer(2, TimeUnit.SECONDS)
                .flatMap((Func1<Long, Observable<ChatMessage>>) aLong -> Observable.create(subscriber -> {
                    subscriber.onNext(message);
                    int index = (int) (Math.random() * 2);
                    if (index == 0) {
                        subscriber.onCompleted();
                    }
                    if (index == 1) {
                        subscriber.onError(new Throwable(""));
                    }
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ChatMessage>() {
                    @Override
                    public void onCompleted() {
                        message.setMessageStatus(0);
                        mOnNetMessageControl.onSendSuccess(message);
                    }

                    @Override
                    public void onError(Throwable e) {
                        message.setMessageStatus(1);
                        mOnNetMessageControl.onSendFail(message);
                    }

                    @Override
                    public void onNext(ChatMessage chatMessage) {

                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventbusMes(EvenBusChatMessage message) {
        switch (message.getType()) {
            case EvenBusChatMessage.TYPE_ERROR:
                mOnNetMessageControl.onSendFail(message.getChatMessage());
                break;
            case EvenBusChatMessage.TYPE_SUCCESS:
                mOnNetMessageControl.onSendSuccess(message.getChatMessage());
                break;
            case EvenBusChatMessage.TYPE_MQ_MSG:
                mOnNetMessageControl.addMQMessage(message.getChatMessage());
                break;
        }
    }
}
