package com.hy.chatlibrary.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.hy.chatlibrary.MiChatHelper;
import com.hy.chatlibrary.R;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.db.DBHelper;
import com.hy.chatlibrary.db.dao.ChatMessageDAO;
import com.hy.chatlibrary.db.dao.NoDisturbingDAO;
import com.hy.chatlibrary.db.entity.ChatMessage;
import com.hy.chatlibrary.db.entity.InstructBean;
import com.hy.chatlibrary.db.entity.NoDisturbing;
import com.hy.chatlibrary.page.ChatActivity;
import com.hy.chatlibrary.utils.DateUtil;
import com.hy.chatlibrary.utils.IMLog;
import com.hy.chatlibrary.utils.SPHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author:MtBaby
 * @date:2020/04/26 14:35
 * @desc:处理消息收发
 */
public class ChatService extends Service {
    String TAG = "ChatService =====>>>>>";
    public static String LOGIN_MEMBER = "LOGIN_MEMBER";
    public static String LOGIN_PW = "LOGIN_PW";
    private static IMQManager mImqManager;
    private ChatMessageDAO mChatMessageDAO;
    private NotificationManager mNotifyManager;
    private Map<String, Integer> integerMap = new HashMap<>();
    private MessageHolder mMessageHolder;
    Handler mHandler = new Handler();
    private EBChat ebChat = null;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        mChatMessageDAO = DBHelper.getInstance(this).getChatMessageDAO();
        //获取NotificationManager实例
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (mImqManager != null) {
            Log.d(TAG, "initMQ");
            mImqManager.initMQ(this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String ditTime = SPHelper.getInstance(this).getString("DitTime");
//        System.out.println("service销毁时间:" + ditTime);
        startForeground(1, new Notification());
        mMessageHolder = (MessageHolder) intent.getSerializableExtra(LOGIN_MEMBER);
        String memberMQPW = intent.getStringExtra(LOGIN_PW);
        if (mImqManager != null) {
            Log.d(TAG, "loginMQ");
            mHandler.post(() -> mImqManager.loginMQ(mMessageHolder, memberMQPW));
        }
        return START_STICKY;
    }

    @Subscribe()
    public void chatMessageControl(EBChatMessage message) {//自己的消息成功与否控制
        ChatMessage chatMessage = message.getChatMessage();
        ChatMessage queryMessage = mChatMessageDAO.queryByGroupIdAndMessageId(chatMessage.getMessageGroupId(), chatMessage.getMessageId());
        switch (message.getType()) {
            case EBChatMessage.TYPE_SEND_ERROR://消息发送失败
                ebChat = new EBChat(EBChat.TYPE_SEND_ERROR, chatMessage, message.getErrorLabel());
                queryMessage.setMessageStatus(1);
                mChatMessageDAO.updateChatMessage(queryMessage);
                break;
            case EBChatMessage.TYPE_SEND_SUCCESS://消息发送成功
                ebChat = new EBChat(EBChat.TYPE_SEND_SUCCESS, chatMessage);
                queryMessage.setMessageStatus(0);
                mChatMessageDAO.updateChatMessage(queryMessage);
                break;
            case EBChatMessage.TYPE_REMOVE_ERROR://消息撤回失败
                ebChat = new EBChat(EBChat.TYPE_REMOVE_ERROR, chatMessage, message.getErrorLabel());
                break;
            case EBChatMessage.TYPE_REMOVE_SUCCESS://消息撤回成功
                ebChat = new EBChat(EBChat.TYPE_REMOVE_SUCCESS, queryMessage);
                queryMessage.setItemType(10);
                queryMessage.setMessageContent("撤回消息");
                mChatMessageDAO.updateChatMessage(queryMessage);//更新本地聊天消息
                break;
            case EBChatMessage.MQ_NOTIFY_REMOVE:
                ebChat = new EBChat(EBChat.MQ_MSG_CALL_BACK, chatMessage);
                mChatMessageDAO.deleteChatMessage(chatMessage);//删除本地聊天消息
                break;
            case EBChatMessage.MQ_NOTIFY_SEND:
                ebChat = new EBChat(EBChat.MQ_NEW_MSG, chatMessage);
                mChatMessageDAO.insertChatMessage(chatMessage);
                if (!isChatActivityForeground(this, chatMessage.getMessageGroupId())) {
                    mChatMessageDAO.insertChatMessage(chatMessage);
                    //TODO 控制未开启免打扰的群聊、收到@消息、收到指令消息弹出提示
                    boolean isAcceptInstruct = false;//是否接受指令
                    boolean isATMessage = false;//是否@自己的消息
                    if (chatMessage.getItemType() == 6) {//指令类型消息处理
                        InstructBean instructBean = chatMessage.getInstructBean();
                        List<MessageHolder> acceptors = instructBean.getAcceptors();
                        for (MessageHolder messageHolder : acceptors) {
                            if (mMessageHolder.getId().equals(messageHolder.getId())) {
                                isAcceptInstruct = true;
                                break;
                            }
                        }
                    }
                    if (chatMessage.getItemType() == 9) {//@消息处理
                        List<MessageHolder> messageHolders = chatMessage.getMessageHolders();
                        for (MessageHolder messageHolder : messageHolders) {
                            if (mMessageHolder.getId().equals(messageHolder.getId())) {
                                isATMessage = true;
                                break;
                            }
                        }
                    }
                    //开启免打扰处理
                    NoDisturbingDAO noDisturbingDAO = DBHelper.getInstance(this).getNoDisturbingDAO();
                    NoDisturbing noDisturbing = noDisturbingDAO.getNoDisturbing(mMessageHolder.getId(), chatMessage.getMessageGroupId());

                    if (noDisturbing == null || !noDisturbing.isOpen() || isAcceptInstruct || isATMessage) {
                        showNotification(chatMessage);
                    }
                }
                break;
        }
        if (ebChat != null) EventBus.getDefault().post(ebChat);
    }

    @Subscribe()
    public void initChatGroup(EBChatInit ebInitChatGroup) {//初始化群聊信息
        EBChat groupControl = new EBChat(EBChat.TYPE_GROUP_INIT, ebInitChatGroup.getChatMessages(), null, ebInitChatGroup.getErrorLabel());
        EventBus.getDefault().post(groupControl);
        if (isChatActivityForeground(this, ebInitChatGroup.getInitChatMessageID())) {
        }
    }

    @Subscribe()
    public void initChatGroupMember(EBInitChatGroupMember ebInitChatGroupMember) {//初始化群聊成员
        EBChat groupControl = new EBChat(EBChat.TYPE_GROUP_MEMBER_INIT, null, ebInitChatGroupMember.getMessageHolders(), ebInitChatGroupMember.getErrorLabel());
        EventBus.getDefault().post(groupControl);
        if (isChatActivityForeground(this, ebInitChatGroupMember.getInitChatMessageID())) {
        }
    }

    @Subscribe()
    public void initFriendList(EBFriendInit ebFriendInit) {
        EBChat groupControl = new EBChat(EBChat.TYPE_FRIEND_LIST_INIT, ebFriendInit, ebFriendInit.getErrorLabel());
        EventBus.getDefault().post(groupControl);
    }

    @Subscribe()
    public void updateChatGroupShowName(EBChatManager ebUpdateChat) {//处理群聊名称显示
        ChatMessage chatChatMessage = ebUpdateChat.getChatMessage();
        String chatGroupId = chatChatMessage.getMessageGroupId();
        MessageHolder controlHolder = chatChatMessage.getMessageHolder();
        List<ChatMessage> chatMessages;
        ChatMessage messageByTop = mChatMessageDAO.queryMMessageByTop(chatGroupId, controlHolder.getId());
        if (messageByTop != null)
            mMessageHolder.setGroupName(messageByTop.getMessageHolderShowName());
        switch (ebUpdateChat.getType()) {
//            case EBChatManager.TYPE_UPDATE_CHAT_DISPLAY_ERROR:
//                ebChat = new EBChat(EBChat.MQ_UPDATE_CHAT_DISPLAY_NAME, ebUpdateChat, ebUpdateChat.getErrorLabel());
//                break;
            case EBChatManager.MQ_UPDATE_CHAT_DISPLAY_NAME:
            case EBChatManager.TYPE_UPDATE_CHAT_DISPLAY_SUCCESS:
                ebChat = new EBChat(EBChat.MQ_UPDATE_CHAT_DISPLAY_NAME, ebUpdateChat, ebUpdateChat.getErrorLabel());
                chatMessages = mChatMessageDAO.queryByGroupIdAndHolderId(chatGroupId, chatChatMessage.getMessageHolderId());
                //更新数据库中对应群聊、对应人员的群显示名称
                for (ChatMessage chatMessage : chatMessages) {
                    chatMessage.getMessageHolder().setGroupName(chatChatMessage.getLabel());
                    chatMessage.setMessageHolderShowName(chatChatMessage.getLabel());
                }
                mChatMessageDAO.updateChatMessage(chatMessages);
                mChatMessageDAO.insertChatMessage(ebUpdateChat.getChatMessage());
                break;
//            case EBChatManager.TYPE_UPDATE_GROUP_NAME_FAIL:
//                ebChat = new EBChat(EBChat.MQ_UPDATE_CHAT_DISPLAY_NAME, ebUpdateChat, ebUpdateChat.getErrorLabel());
//                break;
            case EBChatManager.TYPE_UPDATE_GROUP_NAME_SUCCESS:
            case EBChatManager.MQ_UPDATE_GROUP_NAME:
                ebChat = new EBChat(EBChat.MQ_UPDATE_CHAT_DISPLAY_NAME, ebUpdateChat, ebUpdateChat.getErrorLabel());
                chatMessages = mChatMessageDAO.queryByGroupId(chatGroupId);
                for (ChatMessage chatMessage : chatMessages) {
                    chatMessage.setMessageGroupName(chatChatMessage.getLabel());
                }
                mChatMessageDAO.updateChatMessage(chatMessages);
                mChatMessageDAO.insertChatMessage(ebUpdateChat.getChatMessage());
                break;
            case EBChatManager.TYPE_UPDATE_GROUP_DESC_SUCCESS:
            case EBChatManager.MQ_UPDATE_GROUP_DESC:
                ebChat = new EBChat(EBChat.MQ_UPDATE_CHAT_DISPLAY_NAME, ebUpdateChat, ebUpdateChat.getErrorLabel());
                chatMessages = mChatMessageDAO.queryByGroupId(chatGroupId);
                for (ChatMessage chatMessage : chatMessages) {
                    chatMessage.setMessageChatGroupDetail(chatChatMessage.getLabel());
                }
                mChatMessageDAO.updateChatMessage(chatMessages);
                mChatMessageDAO.insertChatMessage(ebUpdateChat.getChatMessage());
                break;
//            case EBChatManager.TYPE_ADD_MEMBER_FAIL:
//                break;
//            case EBChatManager.TYPE_EXIST_FAIL:
//                break;
            case EBChatManager.TYPE_ADD_MEMBER_SUCCESS:
            case EBChatManager.TYPE_EXIST_SUCCESS:
                ebChat = new EBChat(EBChat.MQ_UPDATE_CHAT_DISPLAY_NAME, ebUpdateChat, ebUpdateChat.getErrorLabel());
                mChatMessageDAO.insertChatMessage(ebUpdateChat.getChatMessage());
                break;
            case EBChatManager.MQ_ADD_MEMBER:
                ebChat = new EBChat(EBChat.MQ_ADD_MEMBER, ebUpdateChat, ebUpdateChat.getErrorLabel());
                mChatMessageDAO.insertChatMessage(ebUpdateChat.getChatMessage());
                break;
            case EBChatManager.MQ_EXIST_MEMBER:
                ebChat = new EBChat(EBChat.MQ_EXIST_CHAT, ebUpdateChat, ebUpdateChat.getErrorLabel());
                mChatMessageDAO.insertChatMessage(ebUpdateChat.getChatMessage());
                break;
            default:
                ebChat = new EBChat(EBChat.MQ_UPDATE_CHAT_DISPLAY_NAME, ebUpdateChat, ebUpdateChat.getErrorLabel());
        }
        EventBus.getDefault().post(ebChat);
    }

    private void showNotification(ChatMessage chatMessage) {
        int notifyId;
        if (integerMap.containsKey(chatMessage.getMessageGroupId())) {
            notifyId = integerMap.get(chatMessage.getMessageGroupId());
        } else {
            notifyId = (int) System.currentTimeMillis();
            integerMap.put(chatMessage.getMessageGroupId(), notifyId);
        }

        //高版本需要渠道
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //只在Android O之上需要渠道，这里的第一个参数要和下面的channelId一样
            NotificationChannel notificationChannel = new NotificationChannel(getPackageName(), "name", NotificationManager.IMPORTANCE_HIGH);
            //如果这里用IMPORTANCE_NOENE就需要在系统的设置里面开启渠道，通知才能正常弹出
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
        //获取PendingIntent
        Intent mainIntent = new Intent(this, ChatActivity.class);
        mainIntent.putExtra(MiChatHelper.CHAT_GROUP_ID, chatMessage.getMessageGroupId());
        mainIntent.putExtra(MiChatHelper.CHAT_GROUP_NAME, chatMessage.getMessageGroupName());
        mainIntent.putExtra(MiChatHelper.CHAT_GROUP_DETAIL, chatMessage.getMessageChatGroupDetail());
//        mainIntent.putExtra(MiChatHelper.CHAT_GROUP_MEMBER, groupMembers);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //创建 Notification.Builder 对象
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getPackageName())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)    //点击通知后自动清除
                .setContentTitle(chatMessage.getMessageHolderShowName())
                .setContentText(chatMessage.getMessageContent())
                .setContentIntent(mainPendingIntent);
        //发送通知
        mNotifyManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public static void setImqManager(IMQManager imqManager) {
        if (mImqManager == null) mImqManager = imqManager;
    }

    @Override
    public void onDestroy() {
        SPHelper.getInstance(this).putString("DitTime", DateUtil.getSystemTime());
        IMLog.d("Service被销毁==============>>>>>>>>>>>>>>>>>>>");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 判断某个Activity 界面是否在前台 如果在前台则判断最新消息是否属于当前聊天组
     *
     * @param context
     * @param initChatMessageID
     * @return
     */
    public static boolean isChatActivityForeground(Context context, String initChatMessageID) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        boolean isChatActivityForeground = false;
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if ("com.hy.chatlibrary.page.ChatActivity".equals(cpn.getClassName())) {
                isChatActivityForeground = true;
            }
        }
        return isChatActivityForeground && initChatMessageID.equals(ChatActivity.mChatGroupId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
