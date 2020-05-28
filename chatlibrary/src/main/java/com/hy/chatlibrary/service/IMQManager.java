package com.hy.chatlibrary.service;

import android.content.Context;

import com.hy.chatlibrary.bean.MessageHolder;

/**
 * @author:MtBaby
 * @date:2020/05/09 21:18
 * @desc:
 */
public interface IMQManager {
    void initMQ(Context context);
    void loginMQ(MessageHolder messageHolder,String loginMQPW);
}
