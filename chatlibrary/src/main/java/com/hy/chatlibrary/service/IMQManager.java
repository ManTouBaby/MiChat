package com.hy.chatlibrary.service;

import com.hy.chatlibrary.bean.MessageHolder;

/**
 * @author:MtBaby
 * @date:2020/05/09 21:18
 * @desc:
 */
public interface IMQManager {
    void initMQ();
    void loginMQ(MessageHolder messageHolder,String loginMQPW);
}
