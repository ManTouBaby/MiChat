package com.hy.chatlibrary;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.IntDef;

import com.hy.chatlibrary.adapter.BaseChatAdapter;
import com.hy.chatlibrary.bean.ChatGroupDetail;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.db.entity.ChatMessage;
import com.hy.chatlibrary.listener.IChatMessageControl;
import com.hy.chatlibrary.listener.OnChatInputListener;
import com.hy.chatlibrary.listener.OnChatManagerListener;
import com.hy.chatlibrary.page.ChatActivity;
import com.hy.chatlibrary.service.ChatService;
import com.hy.chatlibrary.service.EBChatMessage;
import com.hy.chatlibrary.service.EBChatInit;
import com.hy.chatlibrary.service.EBFriendInit;
import com.hy.chatlibrary.service.EBInitChatGroupMember;
import com.hy.chatlibrary.service.EBChatManager;
import com.hy.chatlibrary.service.IMQManager;

import org.greenrobot.eventbus.EventBus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/03/30 15:12
 * @desc:
 */
public class MiChatHelper {
    public final static String CHAT_GROUP_ID = "chatGroupId";
    public final static String CHAT_GROUP_MEMBER_ID = "chatGroupMemberId";
    public final static String CHAT_GROUP_MEMBER_GROUP_NAME = "chatGroupMemberGroupName";
    public final static String CHAT_GROUP_NAME = "chatGroupName";
    public final static String CHAT_GROUP_DETAIL = "chatGroupDetail";
    public final static String CHAT_GROUP_MEMBER = "chatGroupMember";

    private static ChatMessageControl chatMessageControl = new ChatMessageControl();
    private static MiChatHelper mMiChatHelper;
    private static Option mOption;

    @IntDef({CHAT_PERSON, CHAT_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChatGroupType {
    }

    public final static int CHAT_GROUP = 0x300;//群聊
    public final static int CHAT_PERSON = 0x400;//私聊


    private MiChatHelper() {
    }

    public static MiChatHelper getInstance() {
        if (mMiChatHelper == null) {
            synchronized (MiChatHelper.class) {
                if (mMiChatHelper == null) {
                    mMiChatHelper = new MiChatHelper();
                }
            }
        }
        return mMiChatHelper;
    }

    //登录聊天
    public MiChatHelper loginIM(Context context, MessageHolder messageHolder, String userMQPW, IMQManager imqManager, OnChatInputListener onChatInputListener, OnChatManagerListener onChatManagerListener) {
        mOption = new MiChatHelper.Option();
        mOption.setFileDirName(context.getPackageName())
                .setNetTimeUrl("http://www.baidu.com")
                .setOpenNetTime(true)
                .setMessageHolder(messageHolder)
                .setOnChatInputListener(onChatInputListener)
                .setOnChatManagerListener(onChatManagerListener);

        Intent intent = new Intent(context, ChatService.class);
        intent.putExtra(ChatService.LOGIN_MEMBER, messageHolder);
        intent.putExtra(ChatService.LOGIN_PW, userMQPW);
        context.startService(intent);
        ChatService.setImqManager(imqManager);
        return mMiChatHelper;
    }

    //设置推送
    public void addMQMessage(ChatMessage chatMessage) {
        EventBus.getDefault().post(new EBChatMessage(EBChatMessage.MQ_NOTIFY_SEND, chatMessage));
    }

    //群聊初始化
    public void initChatGroupList(List<ChatMessage> chatMessages, String initChatMessageID, String errorLabel) {
        EventBus.getDefault().post(new EBChatInit(chatMessages, initChatMessageID, errorLabel));
    }

    //好友列表初始化
    public void initFriendList(List<MessageHolder> messageHolders, String errorLabel) {
        EventBus.getDefault().post(new EBFriendInit(messageHolders, errorLabel));
    }

    //群聊成员初始化
    public void initChatGroupMember(List<MessageHolder> messageHolders, String initChatMessageID, String errorLabel) {
        EventBus.getDefault().post(new EBInitChatGroupMember(messageHolders, initChatMessageID, errorLabel));
    }

    //更新群聊显示名称
    public void notifyChatGroupShowName(ChatMessage chatMessage) {
        EventBus.getDefault().post(new EBChatManager(EBChatManager.MQ_UPDATE_CHAT_DISPLAY_NAME, chatMessage, null));
    }

    //更新群聊名称
    public void notifyChatGroupName(ChatMessage chatMessage) {
        EventBus.getDefault().post(new EBChatManager(EBChatManager.MQ_UPDATE_GROUP_NAME, chatMessage, null));
    }

    //通知新增成员
    public void notifyAddMember(ChatMessage chatMessage) {
        EventBus.getDefault().post(new EBChatManager(EBChatManager.MQ_ADD_MEMBER, chatMessage, null));
    }

    //通知成员退出
    public void notifyExistMember(ChatMessage chatMessage) {
        EventBus.getDefault().post(new EBChatManager(EBChatManager.MQ_EXIST_MEMBER, chatMessage, null));
    }

    //通知成员退出
    public void notifyChatGroupDesc(ChatMessage chatMessage) {
        EventBus.getDefault().post(new EBChatManager(EBChatManager.MQ_UPDATE_GROUP_DESC, chatMessage, null));
    }

    //进入聊天界面一
    public ChatMessageControl gotoChat(Context context, ChatGroupDetail chatGroupDetail) {
        return gotoChat(context, chatGroupDetail.getMessageGroupId(), chatGroupDetail.getMessageGroupName(), chatGroupDetail.getMessageGroupDes());
    }

    //进入聊天界面二
    public ChatMessageControl gotoChat(Context context, String chatGroupId, String chatGroupName, String chatGroupDetail) {
        if (mOption.messageHolder == null) {
            throw new NullPointerException("The MessageHolder of Option con`t be null");
        }
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(CHAT_GROUP_ID, chatGroupId);
        intent.putExtra(CHAT_GROUP_NAME, chatGroupName);
        intent.putExtra(CHAT_GROUP_DETAIL, chatGroupDetail);
        context.startActivity(intent);
        return chatMessageControl;
    }


    public static class ChatMessageControl implements IChatMessageControl {

        @Override
        public void onSendFail(ChatMessage chatMessage, String msg) {
            EventBus.getDefault().post(new EBChatMessage(EBChatMessage.TYPE_SEND_ERROR, chatMessage));
        }

        @Override
        public void onSendSuccess(ChatMessage chatMessage) {
            EventBus.getDefault().post(new EBChatMessage(EBChatMessage.TYPE_SEND_SUCCESS, chatMessage));
        }

        @Override
        public void onRemoveSuccess(ChatMessage chatMessage) {
            EventBus.getDefault().post(new EBChatMessage(EBChatMessage.TYPE_REMOVE_SUCCESS, chatMessage));
        }

        @Override
        public void onRemoveFail(ChatMessage chatMessage, String msg) {
            EventBus.getDefault().post(new EBChatMessage(EBChatMessage.TYPE_REMOVE_ERROR, chatMessage));
        }

        @Override
        public void onUpdateChatDisplayNameFail(ChatMessage chatMessage, String msg) {
            EventBus.getDefault().post(new EBChatManager(EBChatManager.TYPE_UPDATE_CHAT_DISPLAY_ERROR, chatMessage, msg));
        }

        @Override
        public void onUpdateChatDisplayNameSuccess(ChatMessage chatMessage) {
            EventBus.getDefault().post(new EBChatManager(EBChatManager.TYPE_UPDATE_CHAT_DISPLAY_SUCCESS, chatMessage, null));
        }

        @Override
        public void onUpdateGroupNameFail(ChatMessage chatMessage, String msg) {
            EventBus.getDefault().post(new EBChatManager(EBChatManager.TYPE_UPDATE_GROUP_NAME_FAIL, chatMessage, null));
        }

        @Override
        public void onUpdateGroupNameSuccess(ChatMessage chatMessage) {
            EventBus.getDefault().post(new EBChatManager(EBChatManager.TYPE_UPDATE_GROUP_NAME_SUCCESS, chatMessage, null));
        }

        @Override
        public void onUpdateGroupDescFail(ChatMessage chatMessage, String msg) {
            EventBus.getDefault().post(new EBChatManager(EBChatManager.TYPE_UPDATE_GROUP_DESC_FAIL, chatMessage, null));
        }

        @Override
        public void onUpdateGroupDescSuccess(ChatMessage chatMessage) {
            EventBus.getDefault().post(new EBChatManager(EBChatManager.TYPE_UPDATE_GROUP_DESC_SUCCESS, chatMessage, null));
        }

        @Override
        public void onExistMemberSuccess(ChatMessage chatMessage) {
            EventBus.getDefault().post(new EBChatManager(EBChatManager.TYPE_EXIST_SUCCESS, chatMessage, null));
        }

        @Override
        public void onExistMemberFail(ChatMessage chatMessage, String error) {
            EventBus.getDefault().post(new EBChatManager(EBChatManager.TYPE_EXIST_FAIL, chatMessage, error));
        }

        @Override
        public void addMemberSuccess(ChatMessage chatMessage) {
            EventBus.getDefault().post(new EBChatManager(EBChatManager.TYPE_ADD_MEMBER_SUCCESS, chatMessage, null));
        }

        @Override
        public void addMemberFail(ChatMessage chatMessage, String errorLabel) {
            EventBus.getDefault().post(new EBChatManager(EBChatManager.TYPE_ADD_MEMBER_FAIL, chatMessage, errorLabel));
        }

        @Override
        public void notifyRemove(ChatMessage message) {
            EventBus.getDefault().post(new EBChatMessage(EBChatMessage.MQ_NOTIFY_REMOVE, message));
        }
    }

    public ChatMessageControl getChatMessageControl() {
        return chatMessageControl;
    }

    public String getFileDirName() {
        return mOption.fileDirName;
    }

    public boolean isOpenEmotion() {
        return mOption.isOpenEmotion;
    }

    public boolean isOpenRadio() {
        return mOption.isOpenRadio;
    }

    public boolean isOpenVideo() {
        return mOption.isOpenVideo;
    }

    public boolean isOpenPhoto() {
        return mOption.isOpenPhoto;
    }

    public boolean isOpenLocation() {
        return mOption.isOpenLocation;
    }

    public boolean isOpenNetTime() {
        return mOption.isOpenNetTime;
    }

    public String getNetTimeUrl() {
        return mOption.netTimeUrl;
    }

    public Option getOption() {
        return mOption;
    }

    public MessageHolder getMessageHolder() {
        return mOption.messageHolder;
    }


    public BaseChatAdapter getAdapter() {
        return mOption.adapter;
    }


    public OnChatInputListener getOnChatInputListener() {
        return mOption.onChatInputListener;
    }

    public OnChatManagerListener getOnChatManagerListener() {
        return mOption.onChatManagerListener;
    }

    public static class Option {
        private boolean isOpenEmotion = true;//是否开启表情
        private boolean isOpenRadio = true;//是否开启录音功能
        private boolean isOpenVideo = true;//是否开启视屏录制
        private boolean isOpenPhoto = true;//是否开启相册
        private boolean isOpenLocation = true;//是否开启定位发送

        private boolean isOpenNetTime = false;//是否开启网络时间作为消息时间
        private String fileDirName = Environment.getExternalStorageDirectory().getPath() + "/hy/";//视屏、图片、音频文件保存路径
        private String netTimeUrl = "http://www.baidu.com";//获取网络时间的地址
        private MessageHolder messageHolder;
        //        private ArrayList<MessageHolder> groupMembers;//群成员列表
        private BaseChatAdapter adapter;
        private OnChatInputListener onChatInputListener;
        private OnChatManagerListener onChatManagerListener;

//        public Option setGroupMembers(ArrayList<MessageHolder> groupMembers) {
//            this.groupMembers = groupMembers;
//            return this;
//        }


        public Option setMessageHolder(MessageHolder messageHolder) {
            this.messageHolder = messageHolder;
            return this;
        }

        public Option setFileDirName(String fileDirName) {
            this.fileDirName = Environment.getExternalStorageDirectory().getPath() + "/" + fileDirName;
            return this;
        }

        public Option setOpenEmotion(boolean openEmotion) {
            isOpenEmotion = openEmotion;
            return this;
        }


        public Option setOpenRadio(boolean openRadio) {
            isOpenRadio = openRadio;
            return this;
        }

        public Option setOpenVideo(boolean openVideo) {
            isOpenVideo = openVideo;
            return this;
        }

        public Option setAdapter(BaseChatAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public Option setOpenPhoto(boolean openPhoto) {
            isOpenPhoto = openPhoto;
            return this;
        }

        public Option setOpenLocation(boolean openLocation) {
            isOpenLocation = openLocation;
            return this;
        }

        public Option setOpenNetTime(boolean openNetTime) {
            isOpenNetTime = openNetTime;
            return this;
        }

        public Option setNetTimeUrl(String netTimeUrl) {
            this.netTimeUrl = netTimeUrl;
            return this;
        }

        public Option setOnChatInputListener(OnChatInputListener onChatInputListener) {
            this.onChatInputListener = onChatInputListener;
            return this;
        }

        public Option setOnChatManagerListener(OnChatManagerListener onChatManagerListener) {
            this.onChatManagerListener = onChatManagerListener;
            return this;
        }
    }
}
