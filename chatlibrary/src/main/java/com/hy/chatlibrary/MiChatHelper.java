package com.hy.chatlibrary;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.IntDef;

import com.hy.chatlibrary.adapter.BaseChatAdapter;
import com.hy.chatlibrary.adapter.ChatAdapter;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.listener.OnChatInputListener;
import com.hy.chatlibrary.page.ChatActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * @author:MtBaby
 * @date:2020/03/30 15:12
 * @desc:
 */
public class MiChatHelper {
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

    public MiChatHelper setOption(Option option) {
        mOption = option;
        return mMiChatHelper;
    }

    public String getFileDirName() {
        return mOption.fileDirName;
    }

    public ArrayList<MessageHolder> getGroupMembers() {
        return mOption.groupMembers;
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


    public void gotoChat(Context context, String chatGroupId, String chatGroupName, @ChatGroupType int groupType) {
        if (mOption.messageHolder == null) {
            throw new NullPointerException("The MessageHolder of Option con`t be null");
        }
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("chatGroupId", chatGroupId);
        intent.putExtra("chatGroupName", chatGroupName);
        intent.putExtra("chatGroupType", groupType);

        context.startActivity(intent);
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
        private ArrayList<MessageHolder> groupMembers;//群成员列表
        private BaseChatAdapter adapter = new ChatAdapter();
        OnChatInputListener onChatInputListener;

        public Option setGroupMembers(ArrayList<MessageHolder> groupMembers) {
            this.groupMembers = groupMembers;
            return this;
        }

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
