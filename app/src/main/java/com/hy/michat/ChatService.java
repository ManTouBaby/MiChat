package com.hy.michat;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hy.chatlibrary.db.ChatMessage;
import com.hy.chatlibrary.listener.OnChatInputListener;
import com.hy.chatlibrary.listener.OnNetMessageControl;

/**
 * @author:MtBaby
 * @date:2020/04/28 8:59
 * @desc:
 */
public class ChatService extends Service implements OnChatInputListener {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String userId = bundle.getString("userId");
            String userName = bundle.getString("userName");
            String userPW = bundle.getString("userPW");
            String exchangeId = bundle.getString("exchangeId");

//            MessageHolder messageHolder = new MessageHolder();
//            messageHolder.setId(mUserName.getText().toString());
//            messageHolder.setName(mUserName.getText().toString());
//            messageHolder.setDepartId("111111");
//            messageHolder.setDepartName("黑猫典狱");
//            messageHolder.setDuty("典狱长");
//            messageHolder.setGender(0);
//            messageHolder.setMobile("13829793334");
//            messageHolder.setPortrait("http://i0.hdslb.com/bfs/article/8e87829cde9559c8407892aa6110f83a4631c6b3.jpg");
//            MiChatHelper.Option option = new MiChatHelper.Option();
//            option.setFileDirName(getPackageName());
//            option.setNetTimeUrl("http://www.baidu.com").setOpenNetTime(true).setMessageHolder(messageHolder).setOnChatInputListener(this);
//            MiChatHelper miChatHelper = MiChatHelper.getInstance().setOption(option);
//            miChatHelper.gotoChat(this, StringUtil.isEmpty(mGroupId.getText().toString(), "0001"), "测试聊天群");
        }

        return START_STICKY_COMPATIBILITY;
    }


    @Override
    public void onMessageSend(ChatMessage message, String chatMessageJson) {

    }

    @Override
    public void onChatMessageCallBack(ChatMessage message) {

    }


    @Override
    public void onInitChatList(ChatMessage chatMessage, OnNetMessageControl onNetMessageControl) {

    }

    class ChatIBinder extends Binder {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
