package com.hy.michat.rabbitMQ;

import android.support.annotation.StringDef;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hy.chatlibrary.db.ChatMessage;
import com.hy.chatlibrary.utils.DateUtil;
import com.hy.message.center.entity.Conn;
import com.hy.message.center.entity.MessageCenter;
import com.hy.message.center.init.RabbitMQInit;
import com.hy.message.center.listener.ConnSuccessListener;
import com.hy.message.center.listener.ReConnErrorListener;
import com.hy.message.center.listener.TaskListener;

import org.greenrobot.eventbus.EventBus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:MtBaby
 * @date:2020/04/21 14:07
 * @desc:
 */
public class RabbitMQManager implements TaskListener, ConnSuccessListener, ReConnErrorListener {
    private static RabbitMQManager mRabbitMQManager;
    private MessageCenter mMessageCenter;
    private Conn mConn;
    private Option mOption = new Option();
    private Map<String, ChatMessage> mSendMsgs = new HashMap<>();
    //    private OnMessageListener mOnMessageListener;
    private RabbitMQInit mRabbitMQInit;
    private boolean isLoginSuccess;//MQ是否连接成功

    private String mUserName;
    private String mUerPassWord;
    private String mExchangeId;

    private RabbitMQManager() {
        mConn = new Conn();
        mMessageCenter = new MessageCenter();
        mMessageCenter.setConfigServerUrl(mOption.configServerUrl);
        mConn.setLoginType(mOption.loginType);
        mConn.setAddresses(mOption.loginAddress);
        mConn.setPort(mOption.loginPort);
        mConn.setVirtualHost(mOption.virtualHost);
    }

    public static RabbitMQManager getInstance() {
        if (mRabbitMQManager == null) {
            synchronized (RabbitMQManager.class) {
                if (mRabbitMQManager == null) {
                    mRabbitMQManager = new RabbitMQManager();
                }
            }
        }
        return mRabbitMQManager;
    }


    @Override
    public void myListTask(List<JSONObject> list) {

    }

    @Override
    public void myTask(JSONObject jsonObject) {
        BaseRabbitBean baseRabbitBean = JSON.parseObject(jsonObject.toJSONString(), BaseRabbitBean.class);
        MsgBean msgBean = baseRabbitBean.getMsgSend();
        ChatMessage chatMessage = msgBean.getData();
        if (msgBean.getMsgFrom().equals(mUserName)) {
            chatMessage.setMessageOwner(0);
        } else {
            chatMessage.setMessageOwner(1);
        }
        if (isContainer(baseRabbitBean.getMsgId())) {
            ChatMessage message = mSendMsgs.get(baseRabbitBean.getMsgId());
            removeSendMSG(baseRabbitBean.getMsgId());
            message.setMessageContent(chatMessage.getMessageContent());
            EventBus.getDefault().post(new EvenBusChatMessage(message, EvenBusChatMessage.TYPE_SUCCESS));
        } else {
            System.out.println("别人的消息");
            EventBus.getDefault().post(new EvenBusChatMessage(chatMessage, EvenBusChatMessage.TYPE_MQ_MSG));
        }
//        String msg = jsonObject.toJSONString();
//        System.out.println("接收到的Json：" + msg);
    }

    @Override
    public void ConnSuccess() {
        isLoginSuccess = true;
        System.out.println("登录成功");
        EventBus.getDefault().post(isLoginSuccess);
    }

    @Override
    public void reConnError(String s) {
        isLoginSuccess = false;
        System.out.println("失败");
        EventBus.getDefault().post(isLoginSuccess);
    }

    @StringDef({MSG_TO, GROUP_ID})
    @Retention(RetentionPolicy.SOURCE)
    @interface MQType {
    }

    public static final String MSG_TO = "msgTo";//私聊
    public static final String GROUP_ID = "groupId";//群聊


    public void sendMsg(@MQType String mqType, String targetId, String holderId, ChatMessage chatMessage) {
        mSendMsgs.put(chatMessage.getMessageId(), chatMessage);
        String dataJson = JSON.toJSONString(chatMessage, SerializerFeature.WriteMapNullValue);
        StringBuilder stringBuilder = new StringBuilder();
        //"msg-send-process", "{\"msgSend\":{msgTo:\"testtest\",msgFrom:\"pyh\",type:\"0\",dataType:\"jstx\",ifRead:\"1\",data:{}}}", "", "", new Date(), "0"
        stringBuilder.append("{\"msgSend\":{");
        stringBuilder.append(mqType).append(":\"");
        stringBuilder.append(targetId).append("\",msgFrom:\"");
        stringBuilder.append(holderId).append("\",type:\"0\",dataType:\"jstx\",ifRead:\"1\",data:");
        stringBuilder.append(dataJson).append("}}");
//        System.out.println("原始json:" + "{\"msgSend\":{msgTo:\"testtest\",msgFrom:\"pyh\",type:\"0\",dataType:\"jstx\",ifRead:\"1\",data:{}}}");
//        System.out.println("组合json:" + stringBuilder.toString());
//        System.out.println("发送的JSON：" + dataJson);
        try {
            mRabbitMQInit.sendMessageCenter("msg-send-process", stringBuilder.toString(), chatMessage.getMessageId(), "", DateUtil.getDateByMilli(chatMessage.getMessageCTMillis()), "0");
        } catch (Exception e) {
            EventBus.getDefault().post(new EvenBusChatMessage(chatMessage, EvenBusChatMessage.TYPE_ERROR));
            e.printStackTrace();
        }
    }

    private void removeSendMSG(String msgId) {
        mSendMsgs.remove(msgId);
    }

    private boolean isContainer(String msgId) {
        return mSendMsgs.containsKey(msgId);
    }

    public boolean isLoginSuccess() {
        return isLoginSuccess;
    }

    public void connect(String exChangerId, String userName, String userPassWord) {
        this.mUserName = userName;
        this.mUerPassWord = userPassWord;
        this.mExchangeId = exChangerId;
        if (mRabbitMQInit != null) {
            mRabbitMQInit.closeConnAndChannel();
            mRabbitMQInit.delShutdownListener();
            mRabbitMQInit = null;
        }
        try {
            mConn.setUsername(userName);
            mConn.setPassword(userPassWord);
            mMessageCenter.setMyExchangeId(exChangerId);
            mRabbitMQInit = new RabbitMQInit(mConn, mMessageCenter);
            mRabbitMQInit.setConnSuccessListener(this);
            mRabbitMQInit.addReConnErrorListener(this);
            mRabbitMQInit.addMyTask(this);
            new Thread(mRabbitMQInit::Conn).start();
        } catch (Exception e) {
            if (mRabbitMQInit != null) {
                mRabbitMQInit.closeConnAndChannel();
                mRabbitMQInit.delShutdownListener();
            }
            e.printStackTrace();
        }

    }

    public static class Option {
        String loginType = "user";//登录类型
        String loginAddress = "112.94.13.13";//登录地址
        int loginPort = 50029;//登录端口
        String virtualHost = "/";
        String configServerUrl = "http://112.94.13.13:50017";//映射地址
    }

}
