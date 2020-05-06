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
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import com.hy.chatlibrary.adapter.ControlTypeAdapter;
import com.hy.chatlibrary.adapter.PhotoVideoBigShowAdapter;
import com.hy.chatlibrary.bean.ControlTypeBean;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.db.ChatMessage;
import com.hy.chatlibrary.db.ChatMessageCreator;
import com.hy.chatlibrary.db.ChatMessageDAO;
import com.hy.chatlibrary.db.DBHelper;
import com.hy.chatlibrary.db.InstructBean;
import com.hy.chatlibrary.listener.OnChatInputListener;
import com.hy.chatlibrary.listener.OnLocalMessageControl;
import com.hy.chatlibrary.listener.OnNetMessageControl;
import com.hy.chatlibrary.utils.StatusBarUtil;
import com.hy.chatlibrary.utils.animation.RevealAnimation;
import com.hy.chatlibrary.utils.glide.GlideLoader;
import com.hy.chatlibrary.widget.chatinput.MiChatInputGroup;
import com.hy.chatlibrary.widget.chatinput.OnAudioRecordListener;
import com.mt.camera.CameraHelper;
import com.mt.filepicker.ImagePicker;
import com.mt.filepicker.data.MediaFile;

import java.util.ArrayList;
import java.util.List;

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
public class ChatActivity extends AppCompatActivity implements OnNetMessageControl, OnLocalMessageControl {
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

    private int localHistoryStartIndex = 0;
    private int pageCount = 15;
    private View mCurrentClickItemView;//单前单击的Item项
    private String mChatGroupId;
    private Handler mHandler = new Handler();
    private List<String> sendAgainId = new ArrayList<>();//发送消息列表
    private ChatMessage mQuoteChatMessage;

    private OnChatInputListener mOnChatInputListener;
    private AMapLocationListener aMapLocationListener;
    private ArrayList<MessageHolder> mGroupMembers;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatueColor(this, R.color.mi_chat_main_bg, true);
        setContentView(R.layout.mi_activity_chat_layout);
        mChatGroupId = getIntent().getStringExtra("chatGroupId");
        String mChatGroupName = getIntent().getStringExtra("chatGroupName");
        int  mChatGroupType = getIntent().getIntExtra("chatGroupType", MiChatHelper.CHAT_GROUP);//聊天类型
        miChatHelper = MiChatHelper.getInstance();
        mGroupMembers = miChatHelper.getGroupMembers();
        mRevealAnimation = new RevealAnimation();
        MessageHolder mMessageHolder = miChatHelper.getMessageHolder();
        mOnChatInputListener = miChatHelper.getOnChatInputListener();
        mChatMessageCreator = new ChatMessageCreator(mChatGroupId, mChatGroupName, mChatGroupType, miChatHelper, mMessageHolder, mAMapLocation, mHandler, this);
        mChatMessageDAO = DBHelper.getInstance(getApplicationContext()).getChatMessageDAO();
        mGdHelper = new GDHelper.Builder().setNaviType(NaviType.GPS).build(getApplicationContext());
        initShowView();
        initChatInputGroup();
//        mChatMessageDAO.deleteAllMessage();
        mGdHelper.startContinueLocation(aMapLocationListener = aMapLocation -> {
            if (aMapLocation.getErrorCode() == 0) mAMapLocation = aMapLocation;
            Log.d("Location-->", aMapLocation.getAddress());
        });
        List<ChatMessage> chatMessages = mChatMessageDAO.queryMessageByCount(mChatGroupId, localHistoryStartIndex, pageCount);
        mChatAdapter.addNetMessages(chatMessages);
        miChatInputGroup.scrollToBottom();

        findViewById(R.id.mi_chat_list_menu).setOnClickListener(v -> {
//            mOnChatInputListener.onMenuClick(v);
        });
        findViewById(R.id.mi_iv_back).setOnClickListener(v -> {
            if (miChatInputGroup.isSoftInputGroupShow()) {
                miChatInputGroup.closeSoftInputGroup();
            } else {
                finish();
            }
        });

        //获取已同步的最新消息
        ChatMessage mChatMessage = mChatMessageDAO.queryMessageByTopAndSynchronization(mChatGroupId);
        mOnChatInputListener.onInitChatList(mChatMessage, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initChatInputGroup() {
        miChatInputGroup = findViewById(R.id.mi_cig_view);
        miChatInputGroup.setFilePathDir(miChatHelper.getFileDirName());
        miChatInputGroup.setChatListAdapter(mChatAdapter = miChatHelper.getAdapter());


        mChatAdapter.setOnSendFailTagClickListener(message -> {
            sendAgainId.add(message.getMessageId());
            message.setMessageStatus(2);
            sendMessage(message);
            mOnChatInputListener.onMessageSend(message, mChatMessageCreator.getChatMessageJson(message));
        });
        mChatAdapter.setOnChatItemChildClickListener((view, chatMessage) -> {
            if (chatMessage.getItemType() == 2 || chatMessage.getItemType() == 3) {
                mCurrentClickItemView = view;
                List<ChatMessage> chatMessages = mChatMessageDAO.queryMessageByPhotoAndVideo();
                mShowAdapter.setChatMessages(chatMessages);
                int clickPosition = -1;
                for (int i = 0; i < chatMessages.size(); i++) {
                    ChatMessage message = chatMessages.get(i);
                    if (message.getMessageId().equals(chatMessage.getMessageId())) {
                        clickPosition = i;
                    }
                }
                mRevealAnimation.launchRevealAnimation(mShowPhotoVideoContainer, mCurrentClickItemView);
                if (clickPosition != -1) mShowAdapter.showPosition(clickPosition);
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
        });
        mChatAdapter.setOnChatItemChildLongClickListener((view, chatMessage) -> {

            boolean openCopy = chatMessage.getItemType() == 0;
            boolean openTranslate = chatMessage.getItemType() == 1;
            boolean openInstruct = chatMessage.getItemType() == 6;

            List<ControlTypeBean> controlTypeBeans = new ArrayList<>();
            controlTypeBeans.add(new ControlTypeBean(0, "复制", openCopy));
            controlTypeBeans.add(new ControlTypeBean(1, "引用", true));
            controlTypeBeans.add(new ControlTypeBean(2, "回复", openInstruct));//用于指令
            controlTypeBeans.add(new ControlTypeBean(3, "撤回", true));//用于撤回，一定时间内发出的消息可以进行撤回
            controlTypeBeans.add(new ControlTypeBean(4, "翻译", openTranslate));//用于语音,对语音进行翻译
            View itemMenu = LayoutInflater.from(this).inflate(R.layout.popup_item_control_menu, null);
            RecyclerView recyclerView = itemMenu.findViewById(R.id.show_item_control_menu);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(new ControlTypeAdapter(controlTypeBeans) {
                @Override
                protected void onItemClick(View v, ControlTypeBean controlType) {
                    switch (controlType.getControlTypeIndex()) {
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
                            miChatInputGroup.showQuoteEdite(mQuoteChatMessage.getMessageContent());
                            break;
                        case 3:
                            if (mOnChatInputListener != null) {
                                mOnChatInputListener.onChatMessageCallBack(chatMessage);
                            }
                            break;
                        case 4:
                            break;
                    }
                }
            });

            PopupWindow window = new PopupWindow(itemMenu, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new DrawerArrowDrawable(this));
            window.setOutsideTouchable(true);
            window.setFocusable(true);
            if (chatMessage.getMessageOwner() == 0) {
                window.showAsDropDown(view, -3 * 160, 0, Gravity.RIGHT);
            } else {
                window.showAsDropDown(view, 3 * 164, 0, Gravity.LEFT);
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
            mChatMessageCreator.createChatMessage(0, textLabel, chatMessage -> {
                mOnChatInputListener.onMessageSend(chatMessage, mChatMessageCreator.getChatMessageJson(chatMessage));
            });

        });
        miChatInputGroup.setOnPullDownLoadMoreListener(() -> mHandler.postDelayed(() -> {
            localHistoryStartIndex++;
            List<ChatMessage> chatMessages = mChatMessageDAO.queryMessageByCount(mChatGroupId, pageCount * localHistoryStartIndex, pageCount);
            mChatAdapter.addOldMessages(chatMessages);
            if (chatMessages.size() < pageCount) {
                miChatInputGroup.setLoadMoreComplete();
            } else {
                miChatInputGroup.setLoadMoreSuccess();
            }
        }, 1000));
        miChatInputGroup.setOnAudioRecordListener(new OnAudioRecordListener() {
            @Override
            public void onAudioRecord(String filePath, long duration) {
                mChatMessageCreator.createChatMessage(1, filePath, duration, chatMessage -> {
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


    @Override
    public void onSendFail(ChatMessage message) {
        runOnUiThread(() -> {
            message.setMessageStatus(1);
            mChatAdapter.updateMessage(message);
            mChatMessageDAO.updateChatMessage(message);
        });
    }

    @Override
    public void onSendSuccess(ChatMessage message) {
        runOnUiThread(() -> {
            message.setMessageStatus(0);
            mChatAdapter.updateMessage(message);
            mChatMessageDAO.updateChatMessage(message);
        });
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


    @Override
    public void addMQMessage(ChatMessage chatMessage) {
        runOnUiThread(() -> {
            mChatMessageDAO.insertChatMessage(chatMessage);
            mChatAdapter.addNewMessage(chatMessage);
            miChatInputGroup.scrollToBottom();
        });
    }

    @Override
    public void addNetMessages(List<ChatMessage> chatMessages) {
        //进行消息同步
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
        miChatInputGroup.scrollToBottom();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SELECT_IMAGES_CODE) {
                List<MediaFile> imagePaths = data.getParcelableArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
                for (int i = 0; i < imagePaths.size(); i++) {
                    MediaFile mediaFile = imagePaths.get(i);
                    if (mediaFile.getDuration() > 0) {
//                        System.out.println("选择到的视屏:  \"" + mediaFile.getPath() + "\"," + mediaFile.getDuration());
                        mChatMessageCreator.createChatMessage(2, mediaFile.getPath(), mediaFile.getDuration(), chatMessage -> {
                            mOnChatInputListener.onMessageSend(chatMessage, mChatMessageCreator.getChatMessageJson(chatMessage));
                        });

                    } else {
                        mChatMessageCreator.createChatMessage(3, mediaFile.getPath(), chatMessage -> {
                            mOnChatInputListener.onMessageSend(chatMessage, mChatMessageCreator.getChatMessageJson(chatMessage));
                        });
                    }
                }
            }
            if (requestCode == REQUEST_TAKE_PHOTO_CODE) {
                String path = data.getStringExtra(CameraHelper.DATA);
                String pathOrigin = data.getStringExtra(CameraHelper.DATA_ORIGIN);
                mChatMessageCreator.createChatMessage(3, path, chatMessage -> {
                    mOnChatInputListener.onMessageSend(chatMessage, mChatMessageCreator.getChatMessageJson(chatMessage));
                });
            }
            if (requestCode == REQUEST_TAKE_VIDEO_CODE) {
                String path = data.getStringExtra(CameraHelper.DATA);
                String pathOrigin = data.getStringExtra(CameraHelper.DATA_ORIGIN);
                long realDuration = data.getLongExtra(CameraHelper.DATA_REAL_DURATION, (long) 0F);
                mChatMessageCreator.createChatMessage(2, path, realDuration, chatMessage -> {
                    mOnChatInputListener.onMessageSend(chatMessage, mChatMessageCreator.getChatMessageJson(chatMessage));
                });
            }

            if (requestCode == REQUEST_TAKE_LOCAL_CODE) {
                PoiItem poiItem = data.getParcelableExtra(CodeManager.PoiItem);
                mChatMessageCreator.createChatMessage(4, poiItem.getTitle(), poiItem.getSnippet(), poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude(), chatMessage -> {
                    mOnChatInputListener.onMessageSend(chatMessage, mChatMessageCreator.getChatMessageJson(chatMessage));
                });
            }
            if (requestCode == REQUEST_TAKE_INSTRUCT_CODE) {
                InstructBean instructItem = (InstructBean) data.getSerializableExtra("InstructItem");
                mChatMessageCreator.createChatMessage(instructItem, chatMessage -> {
                    mOnChatInputListener.onMessageSend(chatMessage, mChatMessageCreator.getChatMessageJson(chatMessage));
                });
            }
        }

    }

    @Override
    protected void onDestroy() {
        mChatAdapter.onDestroy();
        mGdHelper.closeLocationListener(aMapLocationListener);
        super.onDestroy();
    }


}
