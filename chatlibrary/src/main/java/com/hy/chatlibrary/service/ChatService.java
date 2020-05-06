package com.hy.chatlibrary.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * @author:MtBaby
 * @date:2020/04/26 14:35
 * @desc:处理控制消息收发
 */
public class ChatService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected class ChatBinder extends Binder {

    }
}
