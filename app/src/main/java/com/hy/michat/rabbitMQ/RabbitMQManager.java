package com.hy.michat.rabbitMQ;

import android.content.Context;
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

    public static final String PUSH_CHAT_MESSAGE = "PUSH_CHAT_MESSAGE";
    public static final String PUSH_CHAT_MESSAGE_CALL_BACK = "PUSH_CHAT_MESSAGE_CALL_BACK";    //发送消息撤回消息
    public static final String PUSH_UPDATE_CHAT_DISPLAY = "PUSH_UPDATE_CHAT_DISPLAY";    //发送群聊显示名称通知
    public static final String PUSH_UPDATE_CHAT_NAME = "PUSH_UPDATE_CHAT_NAME";    //发送群聊名称通知
    public static final String PUSH_UPDATE_CHAT_DESC = "PUSH_UPDATE_CHAT_DESC";    //发送群公告修改通知
    public static final String PUSH_ADD_MEMBER = "PUSH_ADD_MEMBER";                //添加群成员通知
    public static final String PUSH_EXIST_MEMBER = "PUSH_EXIST_MEMBER";            //退出群聊通知
    private Context mContent;

    @Override
    public void myListTask(List<JSONObject> list) {

    }

    private static RabbitMQManager rabbitMQManager;

    private RabbitMQManager() {
    }

    public static RabbitMQManager getInstance() {
        if (rabbitMQManager == null) {
            synchronized (RabbitMQManager.class) {
                if (rabbitMQManager == null) {
                    rabbitMQManager = new RabbitMQManager();
                }
            }
        }
        return rabbitMQManager;
    }

    @Override
    public void myTask(JSONObject jsonObject) {
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
                IMLog.d("消息发送成功：" + JSON.toJSONString(chatMessage));
            } else {
                IMLog.d("新消息：" + JSON.toJSONString(chatMessage));
                MiChatHelper.getInstance().addMQMessage(chatMessage);
            }
        }
        if (PUSH_UPDATE_CHAT_DISPLAY.equals(exchangeType)) {//群聊显示名称修改推送
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
                chatMessageControl.onUpdateChatDisplayNameSuccess(message);
                removeSendMSG(msgId);
                IMLog.d("群聊显示名称成功：" + chatMessage.getMessageHolderName() + "---" + chatMessage.getLabel());
            } else {
                IMLog.d("群聊显示名称推送：" + chatMessage.getMessageHolderName() + "---" + chatMessage.getLabel());
                MiChatHelper.getInstance().notifyChatGroupShowName(chatMessage);
            }
        }
        if (PUSH_UPDATE_CHAT_NAME.equals(exchangeType)) {//群聊显示名称修改推送
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
                chatMessageControl.onUpdateGroupNameSuccess(chatMessage);
                removeSendMSG(msgId);
                IMLog.d("群聊名称修改成功：" + chatMessage.getMessageHolderName() + "---" + chatMessage.getLabel());
            } else {
                IMLog.d("群聊名称修改推送：" + chatMessage.getMessageHolderName() + "---" + chatMessage.getLabel());
                MiChatHelper.getInstance().notifyChatGroupName(chatMessage);
            }
        }

        if (PUSH_CHAT_MESSAGE_CALL_BACK.equals(exchangeType)) {//消息撤回通知
            ChatMessage chatMessage = JSON.parseObject(data.toJSONString(), ChatMessage.class);
            MiChatHelper.ChatMessageControl chatMessageControl = MiChatHelper.getInstance().getChatMessageControl();
            if (isContainer(msgId)) {
                chatMessageControl.onRemoveSuccess(chatMessage);
                removeSendMSG(msgId);
                IMLog.d("消息撤回成功：" + JSON.toJSONString(chatMessage));
            } else {
                IMLog.d("消息撤回通知：" + JSON.toJSONString(chatMessage));
                chatMessageControl.notifyRemove(chatMessage);
            }
        }

        if (PUSH_ADD_MEMBER.equals(exchangeType)) {
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
                chatMessageControl.addMemberSuccess(message);
                IMLog.d("新成员加入群成功：" + chatMessage.getMessageGroupId() + "---" + chatMessage.getNewHolders().get(0).getName());
            } else {
                MiChatHelper.getInstance().notifyAddMember(chatMessage);
                IMLog.d("新成员加入群聊MQ：" + chatMessage.getMessageGroupId() + "---" + chatMessage.getNewHolders().get(0).getName());
            }
        }

        if (PUSH_EXIST_MEMBER.equals(exchangeType)) {
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
                chatMessageControl.onExistMemberSuccess(chatMessage);
                IMLog.d("退出群聊成功：" + chatMessage.getMessageGroupId() + "---" + chatMessage.getMessageHolderName());
            } else {
                MiChatHelper.getInstance().notifyExistMember(chatMessage);
                IMLog.d("退出群聊MQ：" + chatMessage.getMessageGroupId() + "---" + chatMessage.getMessageHolderName());
            }
        }

        if (PUSH_UPDATE_CHAT_DESC.equals(exchangeType)) {
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
                chatMessageControl.onUpdateGroupDescSuccess(chatMessage);
                IMLog.d("修改群聊公告成功：" + chatMessage.getMessageGroupId() + "---" + chatMessage.getMessageHolderName());
            } else {
                MiChatHelper.getInstance().notifyChatGroupDesc(chatMessage);
                IMLog.d("修改群聊公告失败：" + chatMessage.getMessageGroupId() + "---" + chatMessage.getMessageHolderName());
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
        IMLog.d("失败:" + s);
        EventBus.getDefault().post(new MQLoginResult(false));
    }

    @Override
    public void initMQ(Context context) {
        this.mContent = context;
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
        this.mUserName = messageHolder.getId();
        this.mExchangeId = messageHolder.getId();
        this.mUerPassWord = loginMQPW;
        if (mRabbitMQInit != null) {
            mRabbitMQInit.closeConnAndChannel();
            mRabbitMQInit.delShutdownListener();
            mRabbitMQInit.consumerOffline();
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

    @StringDef({PUSH_CHAT_MESSAGE,
            PUSH_CHAT_MESSAGE_CALL_BACK,
            PUSH_UPDATE_CHAT_DISPLAY,
            PUSH_UPDATE_CHAT_NAME,
            PUSH_UPDATE_CHAT_DESC,
            PUSH_ADD_MEMBER,
            PUSH_EXIST_MEMBER})
    @Retention(RetentionPolicy.SOURCE)
    @interface DATAType {
    }


    public static final String MSG_TO = "msgTo";//私聊
    public static final String GROUP_ID = "groupId";//群聊

    //群聊消息
    public void sendChatMsg(@MQType String mqType, @DATAType String dataType, ChatMessage chatMessage) {
        if (mRabbitMQInit == null) {
            loginMQ(mMessageHolder, mUerPassWord);
        }
        mSendMsgs.put(chatMessage.getMessageId(), chatMessage);
        MQExchange<ChatMessage> mqExchange = new MQExchange<>();
        mqExchange.setData(chatMessage);
        mqExchange.setExchangeType(dataType);
        String sendMsg = getDateJson(mqType, chatMessage.getMessageGroupId(), chatMessage.getMessageHolderId(), chatMessage.getItemType(), mqExchange);
        try {
            mRabbitMQInit.sendMessageCenter("msg-send-process", sendMsg, chatMessage.getMessageId(), "", DateUtil.getDateByMilli(chatMessage.getMessageCTMillis()), "0");
        } catch (Exception e) {
            e.printStackTrace();
            if (dataType.equals(PUSH_CHAT_MESSAGE)) {
                MiChatHelper.ChatMessageControl chatMessageControl = MiChatHelper.getInstance().getChatMessageControl();
                chatMessageControl.onSendFail(chatMessage, e.getMessage());
            } else if (dataType.equals(PUSH_CHAT_MESSAGE_CALL_BACK)) {
                MiChatHelper.ChatMessageControl chatMessageControl = MiChatHelper.getInstance().getChatMessageControl();
                chatMessageControl.onRemoveFail(chatMessage, e.getMessage());
            } else if (dataType.equals(PUSH_UPDATE_CHAT_DISPLAY)) {
                MiChatHelper.ChatMessageControl chatMessageControl = MiChatHelper.getInstance().getChatMessageControl();
                chatMessageControl.onUpdateChatDisplayNameFail(chatMessage, e.toString());
            } else if (dataType.equals(PUSH_UPDATE_CHAT_NAME)) {
                MiChatHelper.ChatMessageControl chatMessageControl = MiChatHelper.getInstance().getChatMessageControl();
                chatMessageControl.onUpdateGroupNameFail(chatMessage, e.toString());
            } else if (dataType.equals(PUSH_UPDATE_CHAT_DESC)) {
                MiChatHelper.ChatMessageControl chatMessageControl = MiChatHelper.getInstance().getChatMessageControl();
                chatMessageControl.onUpdateGroupNameFail(chatMessage, e.toString());
            } else if (dataType.equals(PUSH_ADD_MEMBER)) {
                MiChatHelper.ChatMessageControl chatMessageControl = MiChatHelper.getInstance().getChatMessageControl();
                chatMessageControl.addMemberFail(chatMessage, e.toString());
            } else if (dataType.equals(PUSH_EXIST_MEMBER)) {
                MiChatHelper.ChatMessageControl chatMessageControl = MiChatHelper.getInstance().getChatMessageControl();
                chatMessageControl.onExistMemberFail(chatMessage, e.toString());
            }
        }
    }


    private String getDateJson(@MQType String mqType, String targetId, String holderId, int msgType, Object object) {
        String dataJson = JSON.toJSONString(object, SerializerFeature.WriteMapNullValue);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"msgSend\":{\"");
        stringBuilder.append(mqType).append("\":\"");
        stringBuilder.append(targetId).append("\",\"msgFrom\":\"");
        stringBuilder.append(holderId).append("\",\"msgType\":\"");
        stringBuilder.append(msgType).append("\",\"type\":\"jstx\",\"dataType\":\"jq\",\"ifRead\":\"1\",\"data\":");
        stringBuilder.append(dataJson).append("}}");
        IMLog.d("发送消息:" + stringBuilder.toString());
        return stringBuilder.toString();
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
