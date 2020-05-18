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
import com.hy.chatlibrary.page.ChatActivity;
import com.hy.chatlibrary.service.ChatService;
import com.hy.chatlibrary.service.EBChatMessageControl;
import com.hy.chatlibrary.service.EBInitChatGroup;
import com.hy.chatlibrary.service.EBInitChatGroupMember;
import com.hy.chatlibrary.service.EBUpdateChatDisplayName;
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
    public MiChatHelper loginIM(Context context, MessageHolder messageHolder, String userMQPW, IMQManager imqManager, OnChatInputListener onChatInputListener) {
        mOption = new MiChatHelper.Option();
        mOption.setFileDirName(context.getPackageName())
                .setNetTimeUrl("http://www.baidu.com")
                .setOpenNetTime(true)
                .setMessageHolder(messageHolder)
                .setOnChatInputListener(onChatInputListener);

        Intent intent = new Intent(context, ChatService.class);
        intent.putExtra(ChatService.LOGIN_MEMBER, messageHolder);
        intent.putExtra(ChatService.LOGIN_PW, userMQPW);
        context.startService(intent);
        ChatService.setImqManager(imqManager);
        return mMiChatHelper;
    }

    //设置推送
    public void addMQMessage(ChatMessage chatMessage) {
        EventBus.getDefault().post(chatMessage);
    }

    //群聊初始化
    public void initChatGroupList(List<ChatMessage> chatMessages, String initChatMessageID, String errorLabel) {
        EventBus.getDefault().post(new EBInitChatGroup(chatMessages, initChatMessageID, errorLabel));
    }

    //群聊成员初始化
    public void initChatGroupMember(List<MessageHolder> messageHolders, String initChatMessageID, String errorLabel) {
        EventBus.getDefault().post(new EBInitChatGroupMember(messageHolders, initChatMessageID, errorLabel));
    }

    //更新群聊显示名称
    public void updateChatGroupShowName(String mChatGroupId, String holderId, String newChatGroupName) {
        EventBus.getDefault().post(new EBUpdateChatDisplayName(EBUpdateChatDisplayName.UPDATE_MQ, mChatGroupId, holderId, newChatGroupName));
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
            EventBus.getDefault().post(new EBChatMessageControl(EBChatMessageControl.TYPE_ERROR, chatMessage));
        }

        @Override
        public void onSendSuccess(ChatMessage chatMessage) {
            EventBus.getDefault().post(new EBChatMessageControl(EBChatMessageControl.TYPE_SUCCESS, chatMessage));
        }

        @Override
        public void onRemoveSuccess(ChatMessage chatMessage) {
            EventBus.getDefault().post(new EBChatMessageControl(EBChatMessageControl.TYPE_REMOVE_SUCCESS, chatMessage));
        }

        @Override
        public void onRemoveFail(ChatMessage chatMessage, String msg) {
            EventBus.getDefault().post(new EBChatMessageControl(EBChatMessageControl.TYPE_REMOVE_ERROR, chatMessage));
        }

        @Override
        public void onUpdateChatDisplayNameFail(String mChatGroupId, String memberId, String msg) {
            EventBus.getDefault().post(new EBUpdateChatDisplayName(EBUpdateChatDisplayName.TYPE_ERROR, mChatGroupId, memberId, null, msg));
        }

        @Override
        public void onUpdateChatDisplayNameSuccess(String mChatGroupId, String memberId, String newChatGroupName) {
            EventBus.getDefault().post(new EBUpdateChatDisplayName(EBUpdateChatDisplayName.TYPE_SUCCESS, mChatGroupId, memberId, newChatGroupName, null));
        }

        @Override
        public void notifyRemove(ChatMessage message) {
            EventBus.getDefault().post(new EBChatMessageControl(EBChatMessageControl.TYPE_NOTIFY_REMOVE, message));
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
        OnChatInputListener onChatInputListener;

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

        public void setOnChatInputListener(OnChatInputListener onChatInputListener) {
            this.onChatInputListener = onChatInputListener;
        }

    }
}
