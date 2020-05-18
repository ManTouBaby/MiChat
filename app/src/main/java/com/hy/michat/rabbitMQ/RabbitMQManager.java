package com.hy.michat.rabbitMQ;

import android.support.annotation.StringDef;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hy.chatlibrary.MiChatHelper;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.db.entity.ChatMessage;
import com.hy.chatlibrary.service.IMQManager;
import com.hy.chatlibrary.utils.DateUtil;
import com.hy.chatlibrary.utils.IMLog;
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
public class RabbitMQManager implements IMQManager, TaskListener, ConnSuccessListener, ReConnErrorListener {
    private static MessageCenter mMessageCenter;
    private static Conn mConn;
    private static Option mOption = new Option();
    private static Map<String, Object> mSendMsgs = new HashMap<>();
    //    private OnMessageListener mOnMessageListener;
    private static RabbitMQInit mRabbitMQInit;
    private boolean isLoginSuccess;//MQ是否连接成功

    private static MessageHolder mMessageHolder;
    private String mUserName;
    private String mUerPassWord;
    private String mExchangeId;

    public static String PUSH_CHAT_MESSAGE = "PUSH_CHAT_MESSAGE";
    public static String PUSH_CHAT_MESSAGE_CALL_BACK = "PUSH_CHAT_MESSAGE_CALL_BACK";
    public static String PUSH_UPDATE_CHAT_DISPLAY = "PUSH_UPDATE_CHAT_DISPLAY";

    @Override
    public void myListTask(List<JSONObject> list) {

    }

    @Override
    public void myTask(JSONObject jsonObject) {
//        RabbitMQMessageBean baseRabbitBean = JSON.parseObject(jsonObject.toJSONString(), RabbitMQMessageBean.class);
        JSONObject msgSendJsonObject = jsonObject.getJSONObject("msgSend");
        String msgId = jsonObject.getString("msgId");
        String msgFrom = msgSendJsonObject.getString("msgFrom");
        JSONObject dataJson = msgSendJsonObject.getJSONObject("data");
        String exchangeType = dataJson.getString("exchangeType");
        JSONObject data = dataJson.getJSONObject("data");
        if (PUSH_CHAT_MESSAGE.equals(exchangeType)) {//群聊消息推送
            ChatMessage chatMessage = JSON.parseObject(data.toJSONString(), ChatMessage.class);
            if (msgFrom.equals(mUserName)) {
                chatMessage.setMessageOwner(0);
            } else {
                chatMessage.setMessageOwner(1);
            }
            if (isContainer(msgId)) {
                ChatMessage message = (ChatMessage) mSendMsgs.get(msgId);
                message.setMessageContent(chatMessage.getMessageContent());
                MiChatHelper.ChatMessageControl chatMessageControl = MiChatHelper.getInstance().getChatMessageControl();
                chatMessageControl.onSendSuccess(message);
                removeSendMSG(msgId);
            } else {
                IMLog.d("新消息："+JSON.toJSONString(chatMessage));
                MiChatHelper.getInstance().addMQMessage(chatMessage);
            }
        }
        if (PUSH_UPDATE_CHAT_DISPLAY.equals(exchangeType)) {//群聊显示名称修改推送
            String targetId = data.getString("targetId");
            String holderId = data.getString("holderId");
            String newChatGroupName = data.getString("newChatGroupName");
            if (isContainer(msgId)) {
                MiChatHelper.ChatMessageControl chatMessageControl = MiChatHelper.getInstance().getChatMessageControl();
                chatMessageControl.onUpdateChatDisplayNameSuccess(targetId, holderId, newChatGroupName);
                removeSendMSG(msgId);
            } else {
                IMLog.d("群聊显示名称修改推送："+targetId+"---"+newChatGroupName);
                MiChatHelper.getInstance().updateChatGroupShowName(targetId, holderId, newChatGroupName);
            }
        }
        if (PUSH_CHAT_MESSAGE_CALL_BACK.equals(exchangeType)) {//消息撤回通知
            ChatMessage chatMessage = JSON.parseObject(data.toJSONString(), ChatMessage.class);
            MiChatHelper.ChatMessageControl chatMessageControl = MiChatHelper.getInstance().getChatMessageControl();
            if (isContainer(msgId)) {
                chatMessageControl.onRemoveSuccess(chatMessage);
                removeSendMSG(msgId);
            } else {
                IMLog.d("消息撤回通知："+JSON.toJSONString(chatMessage));
                chatMessageControl.notifyRemove(chatMessage);
            }
        }

    }

    @Override
    public void ConnSuccess() {
        isLoginSuccess = true;
        IMLog.d("登录成功");
        EventBus.getDefault().post(new MQLoginResult(true));
    }

    @Override
    public void reConnError(String s) {
        isLoginSuccess = false;
        IMLog.d("失败");
        EventBus.getDefault().post(new MQLoginResult(false));
    }

    @Override
    public void initMQ() {
        mConn = new Conn();
        mMessageCenter = new MessageCenter();
        mMessageCenter.setConfigServerUrl(mOption.configServerUrl);
        mConn.setLoginType(mOption.loginType);
        mConn.setAddresses(mOption.loginAddress);
        mConn.setPort(mOption.loginPort);
        mConn.setVirtualHost(mOption.virtualHost);
    }

    @Override
    public void loginMQ(MessageHolder messageHolder, String loginMQPW) {
        this.mMessageHolder = messageHolder;
        this.mUserName = messageHolder.getName();
        this.mExchangeId = messageHolder.getName();
        this.mUerPassWord = loginMQPW;
        if (mRabbitMQInit != null) {
            mRabbitMQInit.closeConnAndChannel();
            mRabbitMQInit.delShutdownListener();
            mRabbitMQInit = null;
        }
        try {
            mConn.setUsername(mUserName);
            mConn.setPassword(mUerPassWord);
            mMessageCenter.setMyExchangeId(mExchangeId);
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


    @StringDef({MSG_TO, GROUP_ID})
    @Retention(RetentionPolicy.SOURCE)
    @interface MQType {
    }

    public static final String MSG_TO = "msgTo";//私聊
    public static final String GROUP_ID = "groupId";//群聊

    //群聊消息
    public void sendChatMsg(@MQType String mqType, String targetId, String holderId, ChatMessage chatMessage) {
        if (mRabbitMQInit==null){
            loginMQ(mMessageHolder,mUerPassWord);
        }

        mSendMsgs.put(chatMessage.getMessageId(), chatMessage);
        MQExchange<ChatMessage> mqExchange = new MQExchange<>();
        mqExchange.setData(chatMessage);
        mqExchange.setExchangeType(PUSH_CHAT_MESSAGE);
        String sendMsg = getDateJson(mqType, targetId, holderId, mqExchange);
        try {
            mRabbitMQInit.sendMessageCenter("msg-send-process", sendMsg, chatMessage.getMessageId(), "", DateUtil.getDateByMilli(chatMessage.getMessageCTMillis()), "0");
        } catch (Exception e) {
            MiChatHelper.ChatMessageControl chatMessageControl = MiChatHelper.getInstance().getChatMessageControl();
            chatMessageControl.onSendFail(chatMessage, e.getMessage());
            e.printStackTrace();
        }
    }

    //发送消息撤回消息
    public void sendChatMsgCallBack(@MQType String mqType, String targetId, String holderId, ChatMessage chatMessage) {
        long currentTimeMillis = System.currentTimeMillis();
        mSendMsgs.put(holderId + "-" + currentTimeMillis, chatMessage);
        MQExchange<ChatMessage> mqExchange = new MQExchange<>();
        mqExchange.setData(chatMessage);
        mqExchange.setExchangeType(PUSH_CHAT_MESSAGE_CALL_BACK);
        String sendMsg = getDateJson(mqType, targetId, holderId, mqExchange);
        try {
            mRabbitMQInit.sendMessageCenter("msg-send-process", sendMsg, holderId + "-" + currentTimeMillis, "", DateUtil.getDateByMilli(chatMessage.getMessageCTMillis()), "0");
        } catch (Exception e) {
            MiChatHelper.ChatMessageControl chatMessageControl = MiChatHelper.getInstance().getChatMessageControl();
            chatMessageControl.onRemoveFail(chatMessage, e.getMessage());
            e.printStackTrace();
        }
    }

    //发送群聊显示名称通知
    public void sendChatDisPlayNameNotify(@MQType String mqType, String targetId, String holderId, String newChatGroupName) {
        MQExchange<Map<String, String>> mqExchange = new MQExchange<>();
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("targetId", targetId);
        stringMap.put("holderId", holderId);
        stringMap.put("newChatGroupName", newChatGroupName);
        mqExchange.setData(stringMap);
        mqExchange.setExchangeType(PUSH_UPDATE_CHAT_DISPLAY);
        mSendMsgs.put(holderId + targetId + newChatGroupName, stringMap);
        String dateJson = getDateJson(mqType, targetId, holderId, mqExchange);
        try {
            mRabbitMQInit.sendMessageCenter("msg-send-process", dateJson, holderId + targetId + newChatGroupName, "", DateUtil.getDateByMilli(System.currentTimeMillis()), "0");
        } catch (Exception e) {
            MiChatHelper.ChatMessageControl chatMessageControl = MiChatHelper.getInstance().getChatMessageControl();
            chatMessageControl.onUpdateChatDisplayNameFail(targetId, holderId, e.toString());
            e.printStackTrace();
        }
    }

    private String getDateJson(@MQType String mqType, String targetId, String holderId, Object object) {
        String dataJson = JSON.toJSONString(object, SerializerFeature.WriteMapNullValue);
        StringBuilder stringBuilder = new StringBuilder();
        //"msg-send-process", "{\"msgSend\":{msgTo:\"testtest\",msgFrom:\"pyh\",type:\"0\",dataType:\"jstx\",ifRead:\"1\",data:{}}}", "", "", new Date(), "0"
        stringBuilder.append("{\"msgSend\":{\"");
        stringBuilder.append(mqType).append("\":\"");
        stringBuilder.append(targetId).append("\",\"msgFrom\":\"");
        stringBuilder.append(holderId).append("\",\"type\":\"0\",\"dataType\":\"jstx\",\"ifRead\":\"1\",\"data\":");
        stringBuilder.append(dataJson).append("}}");
//        IMLog.d("原始json:" + "{\"msgSend\":{msgTo:\"testtest\",msgFrom:\"pyh\",type:\"0\",dataType:\"jstx\",ifRead:\"1\",data:{}}}");
//        IMLog.d("组合json:" + stringBuilder.toString());
        return stringBuilder.toString();
//        IMLog.d("发送的JSON：" + dataJson);

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


    public static class Option {
        String loginType = "user";//登录类型
        String loginAddress = "112.94.13.13";//登录地址
        int loginPort = 50029;//登录端口
        String virtualHost = "/";
        String configServerUrl = "http://112.94.13.13:50017";//映射地址
    }

}
