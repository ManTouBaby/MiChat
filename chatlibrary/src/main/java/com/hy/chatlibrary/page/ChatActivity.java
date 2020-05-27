package com.hy.chatlibrary.page;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.PoiItem;
import com.hrw.chatlibrary.R;
import com.hrw.gdlibrary.CodeManager;
import com.hrw.gdlibrary.GDHelper;
import com.hy.chatlibrary.MiChatHelper;
import com.hy.chatlibrary.adapter.BaseChatAdapter;
import com.hy.chatlibrary.adapter.ChatAdapter;
import com.hy.chatlibrary.adapter.OnItemChildClickListener;
import com.hy.chatlibrary.adapter.OnItemChildLongClickListener;
import com.hy.chatlibrary.adapter.PhotoVideoBigShowAdapter;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.db.ChatMessageCreator;
import com.hy.chatlibrary.db.DBHelper;
import com.hy.chatlibrary.db.dao.ChatMessageDAO;
import com.hy.chatlibrary.db.entity.ChatMessage;
import com.hy.chatlibrary.db.entity.InstructBean;
import com.hy.chatlibrary.listener.OnChatInputListener;
import com.hy.chatlibrary.listener.OnLocalMessageControl;
import com.hy.chatlibrary.service.EBChat;
import com.hy.chatlibrary.service.EBChatManager;
import com.hy.chatlibrary.utils.DateUtil;
import com.hy.chatlibrary.utils.PopupWindowsHelper;
import com.hy.chatlibrary.utils.SPHelper;
import com.hy.chatlibrary.utils.StatusBarUtil;
import com.hy.chatlibrary.utils.StringUtil;
import com.hy.chatlibrary.utils.animation.RevealAnimation;
import com.hy.chatlibrary.utils.glide.GlideLoader;
import com.hy.chatlibrary.widget.chatinput.MiChatInputGroup;
import com.hy.chatlibrary.widget.chatinput.OnAudioRecordListener;
import com.mt.camera.CameraHelper;
import com.mt.filepicker.ImagePicker;
import com.mt.filepicker.data.MediaFile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.hy.chatlibrary.MiChatHelper.CHAT_GROUP_MEMBER_GROUP_NAME;
import static com.hy.chatlibrary.MiChatHelper.CHAT_GROUP_NAME;
import static com.hy.chatlibrary.base.ResultCode.REQUEST_GROUP_DETAIL;
import static com.hy.chatlibrary.base.ResultCode.REQUEST_SELECT_IMAGES_CODE;
import static com.hy.chatlibrary.base.ResultCode.REQUEST_TAKE_INSTRUCT_CODE;
import static com.hy.chatlibrary.base.ResultCode.REQUEST_TAKE_LOCAL_CODE;
import static com.hy.chatlibrary.base.ResultCode.REQUEST_TAKE_PHOTO_CODE;
import static com.hy.chatlibrary.base.ResultCode.REQUEST_TAKE_VIDEO_CODE;


/**
 * @author:MtBaby
 * @date:2020/03/30 15:16
 * @desc:
 */
public class ChatActivity extends AppCompatActivity implements OnLocalMessageControl {
    private BaseChatAdapter mChatAdapter;
    private PhotoVideoBigShowAdapter mShowAdapter;
    private MiChatHelper miChatHelper;
    private ChatMessageDAO mChatMessageDAO;
    private ChatMessageCreator mChatMessageCreator;
    private AMapLocation mAMapLocation;
    private RevealAnimation mRevealAnimation;
    private GDHelper mGdHelper;

    private MiChatInputGroup miChatInputGroup;
    private RelativeLayout mShowPhotoVideoContainer;
    private TextView mGroupName;

    private int pageCount = 15;
    private View mCurrentClickItemView;//单前单击的Item项
    private Handler mHandler = new Handler();
    private List<String> sendAgainId = new ArrayList<>();//发送消息列表
    private ChatMessage mQuoteChatMessage;
    private ArrayList<MessageHolder> mGroupMembers;
    private String mChatGroupDetail;
    private String mChatGroupName;
    public static String mChatGroupId;
    public final static String CHAT_MEMBER = "CHAT_MEMBER";

    private OnChatInputListener mOnChatInputListener;
    private AMapLocationListener aMapLocationListener;
    private String initMemberError;
    private MessageHolder mMessageHolder;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        System.out.println("ChatActivity---onCreate");
        EventBus.getDefault().register(this);
        mChatMessageDAO = DBHelper.getInstance(getApplicationContext()).getChatMessageDAO();
        mGdHelper = new GDHelper.Builder().setNaviType(NaviType.GPS).build(getApplicationContext());
        Intent intent = getIntent();
        mChatGroupId = intent.getStringExtra(MiChatHelper.CHAT_GROUP_ID);
        mChatGroupName = intent.getStringExtra(CHAT_GROUP_NAME);
        mChatGroupDetail = intent.getStringExtra(MiChatHelper.CHAT_GROUP_DETAIL);
        miChatHelper = MiChatHelper.getInstance();
        mMessageHolder = miChatHelper.getMessageHolder();
        mRevealAnimation = new RevealAnimation();
        StatusBarUtil.setStatueColor(this, R.color.mi_chat_main_bg, true);
        setContentView(R.layout.mi_activity_chat_layout);
        mGroupName = findViewById(R.id.mt_chat_group_name);


        initShowView();
        initChatInputGroup();
        mRevealAnimation.setOnTargViewVisibleListener(isVisible -> {
            if (!isVisible) mShowAdapter.onDestroy();
        });

        //获取已同步的最新消息
        new Thread(() -> {
            ChatMessage mChatMessage = mChatMessageDAO.queryMessageByTopAndSynchronization(mChatGroupId);
            ChatMessage messageByTop = mChatMessageDAO.queryMMessageByTop(mChatGroupId, mMessageHolder.getId());
            mOnChatInputListener.onInitChatList(mChatMessage, mChatGroupId);
            if (messageByTop != null) {
                mMessageHolder.setGroupName(messageByTop.getMessageHolderShowName());
                mChatGroupName = messageByTop.getMessageGroupName();
                runOnUiThread(() -> mGroupName.setText(mChatGroupName));
            }
        }).start();

        String memberChatGroup = SPHelper.getInstance(this).getString(mMessageHolder.getId() + "-" + mChatGroupId);
        mMessageHolder.setGroupName(TextUtils.isEmpty(memberChatGroup) ? mMessageHolder.getName() : memberChatGroup);
        mOnChatInputListener = miChatHelper.getOnChatInputListener();
        mChatMessageCreator = new ChatMessageCreator(mChatGroupId, mChatGroupName, miChatHelper, mMessageHolder, mHandler, this);

        mGdHelper.startContinueLocation(aMapLocationListener = aMapLocation -> {
            if (aMapLocation.getErrorCode() == 0) mAMapLocation = aMapLocation;
            Log.d("Location-->", aMapLocation.getAddress());
        });

        findViewById(R.id.mi_chat_list_menu).setOnClickListener(v -> {
            if (mGroupMembers == null) {
                Toast.makeText(this, initMemberError == null ? "群成员初始化未完成" : initMemberError, Toast.LENGTH_SHORT).show();
                new Thread(() -> {
                    ChatMessage mChatMessage = mChatMessageDAO.queryMessageByTopAndSynchronization(mChatGroupId);
                    mOnChatInputListener.onInitChatList(mChatMessage, mChatGroupId);
                }).start();
                return;
            }
            Intent gotoDetail = new Intent(this, ChatGroupDetailActivity.class);
            gotoDetail.putExtra(MiChatHelper.CHAT_GROUP_MEMBER, mGroupMembers);
            gotoDetail.putExtra(MiChatHelper.CHAT_GROUP_ID, mChatGroupId);
            gotoDetail.putExtra(MiChatHelper.CHAT_GROUP_MEMBER_ID, mMessageHolder.getId());
            gotoDetail.putExtra(CHAT_GROUP_MEMBER_GROUP_NAME, mMessageHolder.getGroupName());
            gotoDetail.putExtra(CHAT_GROUP_NAME, mChatGroupName);
            gotoDetail.putExtra(MiChatHelper.CHAT_GROUP_DETAIL, mChatGroupDetail);
            startActivityForResult(gotoDetail, REQUEST_GROUP_DETAIL);
        });
        findViewById(R.id.mi_iv_back).setOnClickListener(v -> {
            if (miChatInputGroup.isSoftInputGroupShow()) {
                miChatInputGroup.closeSoftInputGroup();
            } else {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        String scrollTo = SPHelper.getInstance(this).getString("scrollTo");
        if (!TextUtils.isEmpty(scrollTo)) {
            SPHelper.getInstance(this).putString("scrollTo", null);
            new Thread(() -> {
                ChatMessage firstItem = mChatAdapter.getFirstItem();
                ChatMessage chatMessage = mChatMessageDAO.queryByGroupIdAndMessageId(mChatGroupId, scrollTo);
                if (mChatAdapter.isContainer(chatMessage)) {
                    runOnUiThread(() -> miChatInputGroup.scrollToPosition(chatMessage));
                } else {
                    List<ChatMessage> messages = mChatMessageDAO.queryMessageByMilli(mChatGroupId, chatMessage.getMessageSTMillis(), firstItem.getMessageSTMillis());
                    runOnUiThread(() -> {
                        for (ChatMessage msg : messages) {
                            msg.setMessageOwner(mMessageHolder.getId().equals(chatMessage.getMessageHolderId()) ? 0 : 1);
                        }
                        mChatAdapter.addOldMessages(messages);
                        miChatInputGroup.scrollToPosition(mChatAdapter.getFirstItem());
                    });
                }
            }).start();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initChatInputGroup() {
        BaseChatAdapter adapter = miChatHelper.getAdapter();
        if (adapter == null) adapter = new ChatAdapter();
        miChatInputGroup = findViewById(R.id.mi_cig_view);
        miChatInputGroup.setFilePathDir(miChatHelper.getFileDirName());
        miChatInputGroup.setChatListAdapter(mChatAdapter = adapter);

        mChatAdapter.setOnSendFailTagClickListener(message -> {
            sendAgainId.add(message.getMessageId());
            message.setMessageStatus(2);
            sendMessage(message);
            mOnChatInputListener.onMessageSend(message, mChatMessageCreator.getChatMessageJson(message));
        });
        mChatAdapter.setOnItemChildClickListener((OnItemChildClickListener<ChatMessage>) (view, chatMessage) -> {
            int viewId = view.getId();
            if (viewId == R.id.mi_content_container || viewId == R.id.mi_instruct_pic || viewId == R.id.mi_instruct_video_container) {
                if (chatMessage.getItemType() == 2 || chatMessage.getItemType() == 3 || chatMessage.getItemType() == 6) {
                    mCurrentClickItemView = view;
                    List<ChatMessage> chatMessages = mChatMessageDAO.queryMessageByPhotoAndVideo();
                    mShowAdapter.setChatMessages(chatMessages);

                    mRevealAnimation.launchRevealAnimation(mShowPhotoVideoContainer, mCurrentClickItemView);
                    mShowAdapter.showPosition(chatMessage);
                }
                if (chatMessage.getItemType() == 4) {
                    if (mAMapLocation == null) {
                        Toast.makeText(this, "无法获得当前位置，请稍后再进行导航", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    NaviLatLng mMyLocal = new NaviLatLng(mAMapLocation.getLatitude(), mAMapLocation.getLongitude());
                    NaviLatLng destination = new NaviLatLng(chatMessage.getLatitude(), chatMessage.getLongitude());
                    mGdHelper.openNavigation(ChatActivity.this, mMyLocal, destination);
                }
            }
            if (viewId == R.id.mi_holder_pro) {
                MessageHolder messageHolder = chatMessage.getMessageHolder();
                if (messageHolder == null) {
                    Toast.makeText(this, "无法查看该成员详情", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(this, ChatPersonalActivity.class);
                    intent.putExtra(CHAT_MEMBER, messageHolder);
                    startActivity(intent);
                }
            }
            if (viewId == R.id.mi_quote_reply_notify) {
                miChatInputGroup.scrollToPosition(chatMessage.getChatMessage());
            }

        });
        mChatAdapter.setOnItemChildLongClickListener((OnItemChildLongClickListener<ChatMessage>) (view, chatMessage) -> {
            if (view.getId() == R.id.mi_item_pro) {
                miChatInputGroup.addATQuote(chatMessage.getMessageHolder().getGroupName());
            }
            if (view.getId() == R.id.mi_content_container) {
                PopupWindowsHelper.showItemControlWindows(view, chatMessage, (view1, data) -> {
                    switch (data.getControlTypeIndex()) {
                        case 0:
                            //获取剪贴板管理器：
                            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            // 创建普通字符型ClipData
                            ClipData mClipData = ClipData.newPlainText("Label", chatMessage.getMessageContent());
                            // 将ClipData内容放到系统剪贴板里。
                            cm.setPrimaryClip(mClipData);
                            Toast.makeText(ChatActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                        case 2:
                            mQuoteChatMessage = chatMessage;
                            miChatInputGroup.showQuoteEdit(mQuoteChatMessage.getMessageContent());
                            miChatInputGroup.openSoftInput();
                            break;
                        case 3:
                            if (mOnChatInputListener != null) {
                                mOnChatInputListener.onChatMessageCallBack(chatMessage);
                            }
                            break;
                        case 4:

                            break;
                    }
                });
            }
        });

        miChatInputGroup.setOnContentTypeClickListener(contentType -> {
            switch (contentType) {
                case PIC:
                    ImagePicker.getInstance()
                            .setTitle("图片视屏")//设置标题
                            .showCamera(false)//设置是否显示拍照按钮
                            .showImage(true)//设置是否展示图片
                            .showVideo(true)//设置是否展示视频
//                            .filterGif(false)//设置是否过滤gif图片
                            .setSingleType(false)//设置图片视频不能同时选择
                            .setMaxCount(9)//设置最大选择图片数目(默认为1，单选)
//                        .setImagePaths(mImageList)//保存上一次选择图片的状态，如果不需要可以忽略
                            .setImageLoader(new GlideLoader())//设置自定义图片加载器
                            .start(ChatActivity.this, REQUEST_SELECT_IMAGES_CODE);//REQEST_SELECT_IMAGES_CODE为Intent调用的requestCode
                    break;
                case TAKE_PHOTO:
                    CameraHelper.getInstance(getPackageName()).capturePhoto(ChatActivity.this, REQUEST_TAKE_PHOTO_CODE);
                    break;
                case TAKE_VIDEO:
                    CameraHelper.getInstance(getPackageName()).captureRecord(ChatActivity.this, REQUEST_TAKE_VIDEO_CODE, 15000);
                    break;
                case LOCAL:
                    mGdHelper.openNearby(this, REQUEST_TAKE_LOCAL_CODE);
                    break;
                case FILE:
                    System.out.println("单击文件");
                    break;
                case INSTRUCT:
                    Intent intent = new Intent(this, InstructEditActivity.class);
                    intent.putExtra("AcceptorList", mGroupMembers);
                    startActivityForResult(intent, REQUEST_TAKE_INSTRUCT_CODE);
                    break;
            }
        });
        miChatInputGroup.setOnTextSubmitListener(textLabel -> {
            if (mQuoteChatMessage != null) {
                mChatMessageCreator.createChatMessage(mAMapLocation, mQuoteChatMessage, textLabel, chatMessage -> mOnChatInputListener.onMessageSend(chatMessage, mChatMessageCreator.getChatMessageJson(chatMessage)));
                miChatInputGroup.closeQuote();
            } else {
                List<String> stringList = StringUtil.getSubUtil(textLabel, "@(.*?) ");
                if (stringList.size() > 0) {
                    List<MessageHolder> messageHolders = new ArrayList<>();
                    for (String holderId : stringList) {
                        for (MessageHolder messageHolder : mGroupMembers) {
                            if (holderId.equals(messageHolder.getName()) && !messageHolders.contains(messageHolder)) {
                                messageHolders.add(messageHolder);
                                break;
                            }
                        }
                    }
                    mChatMessageCreator.createChatMessage(mAMapLocation, messageHolders, textLabel, chatMessage -> mOnChatInputListener.onMessageSend(chatMessage, mChatMessageCreator.getChatMessageJson(chatMessage)));
                } else {
                    mChatMessageCreator.createChatMessage(0, mAMapLocation, textLabel, chatMessage -> mOnChatInputListener.onMessageSend(chatMessage, mChatMessageCreator.getChatMessageJson(chatMessage)));
                }
            }
        });
        miChatInputGroup.setOnPullDownLoadMoreListener((firstChatMessage) -> new Thread(() -> {
            List<ChatMessage> chatMessages = mChatMessageDAO.queryMessageByMilli(mChatGroupId, firstChatMessage.getMessageSTMillis(), pageCount);
            runOnUiThread(() -> {
                for (ChatMessage chatMessage : chatMessages) {
                    chatMessage.setMessageOwner(mMessageHolder.getId().equals(chatMessage.getMessageHolderId()) ? 0 : 1);
                }
                mChatAdapter.addOldMessages(chatMessages);
                if (chatMessages.size() < pageCount) {
                    mChatAdapter.setLoadMoreComplete();
                } else {
                    mChatAdapter.setLoadMoreSuccess();
                }
            });
        }).start());
        miChatInputGroup.setOnAudioRecordListener(new OnAudioRecordListener() {
            @Override
            public void onAudioRecord(String filePath, long duration) {
                mChatMessageCreator.createChatMessage(1, mAMapLocation, "[语音消息]", filePath, duration, chatMessage -> {
                    mOnChatInputListener.onMessageSend(chatMessage, mChatMessageCreator.getChatMessageJson(chatMessage));
                });

            }

            @Override
            public void onAudioRecordFail(String failReason) {
                if ("java.lang.RuntimeException: stop failed".equals(failReason)) {
                    Toast.makeText(ChatActivity.this, "说话时间太短", Toast.LENGTH_SHORT).show();
                }
            }
        });
        miChatInputGroup.setOnCloseQuoteListener(() -> mQuoteChatMessage = null);
    }


    private void initShowView() {
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        mShowPhotoVideoContainer = findViewById(R.id.mi_show_photo_video_container);
        RecyclerView mShowPhotoVideo = findViewById(R.id.mi_show_photo_video);
        mShowPhotoVideo.setHasFixedSize(true);
        mShowPhotoVideo.setLayoutManager(layout);
        mShowPhotoVideo.setAdapter(mShowAdapter = new PhotoVideoBigShowAdapter());
        pagerSnapHelper.attachToRecyclerView(mShowPhotoVideo);
//        mShowAdapter.setOnBigShowItemClickListener(chatMessage -> mRevealAnimation.launchRevealAnimation(mShowPhotoVideoContainer, mCurrentClickItemView));
    }

    @Override
    public void onBackPressed() {
        if (miChatInputGroup.isSoftInputGroupShow() || mRevealAnimation.isShow()) {
            miChatInputGroup.closeSoftInputGroup();
            if (mCurrentClickItemView != null)
                mRevealAnimation.launchRevealAnimation(mShowPhotoVideoContainer, mCurrentClickItemView);
        } else {
            super.onBackPressed();
        }
    }


    //消息发送，通知本地数据更新
    @Override
    public void sendMessage(ChatMessage message) {
        //消息重新发送时，只更新状态，不重新插入数据库
        if (sendAgainId.contains(message.getMessageId())) {
            mChatAdapter.updateMessage(message);
            mChatMessageDAO.updateChatMessage(message);
            return;
        }
        mChatMessageDAO.insertChatMessage(message);
        mChatAdapter.addNewMessage(message);
        miChatInputGroup.scrollToBottom();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void chatMessageControl(EBChat groupControl) {
        switch (groupControl.getType()) {
            case EBChat.TYPE_SEND_SUCCESS://消息发送成功
                onSendSuccess(groupControl.getChatMessage());
                break;
            case EBChat.TYPE_SEND_ERROR://消息发送失败
                onSendFail(groupControl.getChatMessage(), groupControl.getErrorLabel());
                break;
            case EBChat.TYPE_REMOVE_ERROR:
                onRemoveFail(groupControl.getChatMessage(), groupControl.getErrorLabel());
                break;
            case EBChat.TYPE_REMOVE_SUCCESS://消息撤回
                onRemoveSuccess(groupControl.getChatMessage());
                mChatMessageCreator.createChatMessage(10, mAMapLocation, "撤回消息", chatMessage -> mOnChatInputListener.onMessageSend(chatMessage, mChatMessageCreator.getChatMessageJson(chatMessage)));
                break;
            case EBChat.TYPE_GROUP_INIT://群聊信息初始化
                setNetMessages(groupControl.getChatMessages());
                break;
            case EBChat.TYPE_GROUP_MEMBER_INIT://群组人员初始化
                setGroupMembers((ArrayList<MessageHolder>) groupControl.getMessageHolders());
                initMemberError = groupControl.getErrorLabel();
                break;
            case EBChat.MQ_ADD_MEMBER:
            case EBChat.MQ_EXIST_CHAT:
                addMQMessage(groupControl.getEbChatManager().getChatMessage());
                new Thread(() -> {
                    ChatMessage mChatMessage = mChatMessageDAO.queryMessageByTopAndSynchronization(mChatGroupId);
                    mOnChatInputListener.onInitChatList(mChatMessage, mChatGroupId);
                }).start();
                break;
            case EBChat.MQ_NEW_MSG://新消息
                addMQMessage(groupControl.getChatMessage());
                break;
            case EBChat.MQ_UPDATE_CHAT_DISPLAY_NAME://修改群聊通知
                EBChatManager ebChatManager = groupControl.getEbChatManager();
                ChatMessage chatManagerChatMessage = ebChatManager.getChatMessage();
                String newChatGroupName = chatManagerChatMessage.getLabel();
                switch (ebChatManager.getType()) {
                    case EBChatManager.TYPE_UPDATE_CHAT_DISPLAY_SUCCESS:
                        mMessageHolder.setGroupName(newChatGroupName);
                        mChatMessageCreator = new ChatMessageCreator(mChatGroupId, mChatGroupName, miChatHelper, mMessageHolder, mHandler, this);
                        updateChatDisplayName(chatManagerChatMessage);
                        addMQMessage(chatManagerChatMessage);
                        break;
                    case EBChatManager.MQ_UPDATE_CHAT_DISPLAY_NAME://群成员更新群聊显示名称
                        updateChatDisplayName(chatManagerChatMessage);
                        addMQMessage(chatManagerChatMessage);
                        break;
                    case EBChatManager.TYPE_UPDATE_GROUP_NAME_SUCCESS:
                    case EBChatManager.MQ_UPDATE_GROUP_NAME:////群成员更新群聊名称
                        mChatGroupName = newChatGroupName;
                        mChatMessageCreator = new ChatMessageCreator(mChatGroupId, mChatGroupName, miChatHelper, mMessageHolder, mHandler, this);
                        mGroupName.setText(newChatGroupName);
                        updateChatGroupName(chatManagerChatMessage);
                        addMQMessage(chatManagerChatMessage);
                        break;
                    case EBChatManager.TYPE_ADD_MEMBER_SUCCESS:
                        new Thread(() -> {
                            ChatMessage mChatMessage = mChatMessageDAO.queryMessageByTopAndSynchronization(mChatGroupId);
                            mOnChatInputListener.onInitChatList(mChatMessage, mChatGroupId);
                        }).start();
                        addMQMessage(chatManagerChatMessage);
                        break;
                    case EBChatManager.TYPE_EXIST_SUCCESS:
                        addMQMessage(chatManagerChatMessage);
                        finish();
                        break;
                }
                break;
            case EBChat.MQ_MSG_CALL_BACK:
                onRemoveSuccess(groupControl.getChatMessage());
                break;

        }
    }

    //更新群组名称
    private void updateChatGroupName(ChatMessage chatManagerChatMessage) {
        List<ChatMessage> chatMessages = mChatAdapter.getChatMessages();
        for (ChatMessage chatMessage : chatMessages) {
            MessageHolder holder = chatMessage.getMessageHolder();
            if (holder != null && holder.getId().equals(chatManagerChatMessage.getMessageHolderId())) {
                chatMessage.setMessageGroupName(chatManagerChatMessage.getLabel());
            }
        }
        mChatAdapter.notifyDataSetChanged();
    }

    //更新群组显示名称
    private void updateChatDisplayName(ChatMessage chatManagerChatMessage) {
        List<ChatMessage> adapterChatMessages = mChatAdapter.getChatMessages();
        for (ChatMessage chatMessage : adapterChatMessages) {
            MessageHolder holder = chatMessage.getMessageHolder();
            if (holder != null && holder.getId().equals(chatManagerChatMessage.getMessageHolderId())) {
                holder.setGroupName(chatManagerChatMessage.getLabel());
                chatMessage.setMessageHolderShowName(chatManagerChatMessage.getLabel());
            }
        }
        mChatAdapter.notifyDataSetChanged();
    }

    //消息发送失败
    public void onSendFail(ChatMessage message, String label) {
        runOnUiThread(() -> {
            message.setMessageStatus(1);
            mChatAdapter.updateMessage(message);
        });
    }

    //消息发送成功
    public void onSendSuccess(ChatMessage message) {
        runOnUiThread(() -> {
            message.setMessageStatus(0);
            mChatAdapter.updateMessage(message);
        });
    }

    //消息撤销成功
    public void onRemoveSuccess(ChatMessage message) {
        runOnUiThread(() -> {
            mChatAdapter.removeMessage(message);
        });
    }

    //消息撤销失败
    public void onRemoveFail(ChatMessage message, String label) {
    }

    //新消息
    public void addMQMessage(ChatMessage chatMessage) {
        runOnUiThread(() -> {
            chatMessage.setMessageOwner(mMessageHolder.getId().equals(chatMessage.getMessageHolderId()) ? 0 : 1);
            mChatAdapter.addNewMessage(chatMessage);
            miChatInputGroup.scrollToBottom();
        });
    }

    public void setGroupMembers(ArrayList<MessageHolder> mGroupMembers) {
        this.mGroupMembers = mGroupMembers;
    }

    //初始化聊天列表
    public void setNetMessages(List<ChatMessage> chatMessages) {
        new Thread(() -> DateUtil.getNetDate(miChatHelper.getNetTimeUrl(), date -> {
            if (chatMessages != null) {//新消息存在则进行消息同步
                List<ChatMessage> localChats = mChatMessageDAO.queryAllMessageByUnchronization(mChatGroupId);
                for (ChatMessage message : chatMessages) {
                    boolean isExist = false;
                    if (miChatHelper.getMessageHolder().getId().equals(message.getMessageHolder().getId())) {
                        message.setMessageOwner(0);
                    } else {
                        message.setMessageOwner(1);
                    }
                    for (int i = 0; i < localChats.size(); i++) {
                        ChatMessage localMessage = localChats.get(i);
                        if (localMessage.getMessageId().equals(message.getMessageId())) {
                            isExist = true;
                            localMessage.setMessageContent(message.getMessageContent());
                            localMessage.setInstructBean(message.getInstructBean());
                            localMessage.setSynchronization(true);
                            mChatMessageDAO.updateChatMessage(localMessage);
                        }
                    }
                    if (!isExist) {
                        mChatMessageDAO.insertChatMessage(message);
                    }
                }
            }

            List<ChatMessage> localChats = mChatMessageDAO.queryMessageByMilli(mChatGroupId, date.getTime(), pageCount);
            runOnUiThread(() -> {
                if (chatMessages == null) {
                    if (localChats == null || localChats.size() < pageCount) {//本地数据量小于PageCount或者不存在
                        mChatAdapter.setLoadMoreComplete();//设置没有更多数据可加载
                        miChatInputGroup.setStackFromEnd(false);//设置列表栈，
                    } else {
                        miChatInputGroup.setStackFromEnd(true);
                    }
                } else {
                    int itemCount = chatMessages.size() + localChats.size();
                    if (itemCount < pageCount) {
                        mChatAdapter.setLoadMoreComplete();
                        miChatInputGroup.setStackFromEnd(false);
                    } else {
                        miChatInputGroup.setStackFromEnd(true);
                    }
                }
                for (ChatMessage chatMessage : localChats) {
                    chatMessage.setMessageOwner(mMessageHolder.getId().equals(chatMessage.getMessageHolderId()) ? 0 : 1);
                }
                mChatAdapter.addNetMessages(localChats);
                miChatInputGroup.scrollToBottom();
            });
        })).start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SELECT_IMAGES_CODE) {
                List<MediaFile> imagePaths = data.getParcelableArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
                for (int i = 0; i < imagePaths.size(); i++) {
                    MediaFile mediaFile = imagePaths.get(i);
                    if (mediaFile.getDuration() > 0) {
                        mChatMessageCreator.createChatMessage(2, mAMapLocation, "[视屏消息]", mediaFile.getPath(), mediaFile.getDuration(), chatMessage -> {
                            mOnChatInputListener.onMessageSend(chatMessage, mChatMessageCreator.getChatMessageJson(chatMessage));
                        });

                    } else {
                        mChatMessageCreator.createChatMessage(3, mAMapLocation, "[图片消息]", mediaFile.getPath(), 0, chatMessage -> {
                            mOnChatInputListener.onMessageSend(chatMessage, mChatMessageCreator.getChatMessageJson(chatMessage));
                        });
                    }
                }
            }
            if (requestCode == REQUEST_TAKE_PHOTO_CODE) {
                String path = data.getStringExtra(CameraHelper.DATA);
                String pathOrigin = data.getStringExtra(CameraHelper.DATA_ORIGIN);
                mChatMessageCreator.createChatMessage(3, mAMapLocation, "[图片消息]", path, 0, chatMessage -> {
                    mOnChatInputListener.onMessageSend(chatMessage, mChatMessageCreator.getChatMessageJson(chatMessage));
                });
            }
            if (requestCode == REQUEST_TAKE_VIDEO_CODE) {
                String path = data.getStringExtra(CameraHelper.DATA);
                String pathOrigin = data.getStringExtra(CameraHelper.DATA_ORIGIN);
                long realDuration = data.getLongExtra(CameraHelper.DATA_REAL_DURATION, (long) 0F);
                mChatMessageCreator.createChatMessage(2, mAMapLocation, "[视屏消息]", path, realDuration, chatMessage -> {
                    mOnChatInputListener.onMessageSend(chatMessage, mChatMessageCreator.getChatMessageJson(chatMessage));
                });
            }

            if (requestCode == REQUEST_TAKE_LOCAL_CODE) {
                PoiItem poiItem = data.getParcelableExtra(CodeManager.PoiItem);
                mChatMessageCreator.createChatMessage(mAMapLocation, poiItem.getTitle(), poiItem.getSnippet(), poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude(), chatMessage -> {
                    mOnChatInputListener.onMessageSend(chatMessage, mChatMessageCreator.getChatMessageJson(chatMessage));
                });
            }
            if (requestCode == REQUEST_TAKE_INSTRUCT_CODE) {
                InstructBean instructItem = (InstructBean) data.getSerializableExtra("InstructItem");
                mChatMessageCreator.createChatMessage(mAMapLocation, instructItem, chatMessage -> {
                    mOnChatInputListener.onMessageSend(chatMessage, mChatMessageCreator.getChatMessageJson(chatMessage));
                });
            }
            if (requestCode == REQUEST_GROUP_DETAIL) {
                boolean isChangeShow = data.getBooleanExtra("isChangeShow", false);
                boolean isExistGroup = data.getBooleanExtra("isExistGroup", false);
                if (isChangeShow) {
                    mChatAdapter.notifyDataSetChanged();
                }
                if (isExistGroup) {
//                    finish();
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("ChatActivity销毁");
        mChatAdapter.onDestroy();
        mGdHelper.closeLocationListener(aMapLocationListener);
        EventBus.getDefault().unregister(this);
    }


}
