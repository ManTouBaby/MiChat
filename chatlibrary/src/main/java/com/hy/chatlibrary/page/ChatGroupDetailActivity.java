package com.hy.chatlibrary.page;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.navi.enums.NaviType;


import com.hy.chatlibrary.MiChatHelper;
import com.hy.chatlibrary.R;
import com.hy.chatlibrary.adapter.ChatGroupMemberAdapter;
import com.hy.chatlibrary.bean.ChatGroupMembers;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.db.ChatMessageCreator;
import com.hy.chatlibrary.db.DBHelper;
import com.hy.chatlibrary.db.dao.NoDisturbingDAO;
import com.hy.chatlibrary.db.entity.NoDisturbing;
import com.hy.chatlibrary.listener.OnChatManagerListener;
import com.hy.chatlibrary.service.EBChat;
import com.hy.chatlibrary.service.EBChatManager;
import com.hy.chatlibrary.utils.LoadingHelper;
import com.hy.chatlibrary.utils.PopupWindowsHelper;
import com.hy.chatlibrary.utils.SPHelper;
import com.hy.chatlibrary.utils.StatusBarUtil;
import com.hy.gdlibrary.GDHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.hy.chatlibrary.MiChatHelper.CHAT_GROUP_DETAIL;
import static com.hy.chatlibrary.MiChatHelper.CHAT_GROUP_NAME;
import static com.hy.chatlibrary.page.ChatActivity.CHAT_MEMBER;

/**
 * @author:MtBaby
 * @date:2020/05/06 15:29
 * @desc:群聊详情
 */
public class ChatGroupDetailActivity extends AppCompatActivity {
    RecyclerView mChatMemberShow;
    LinearLayout mChatGroupContainer;
    TextView mExistGroup;
    TextView mGroupMemberCount;
    Switch mIsOpenNoDisturbing;
    Switch mIsOpenName;
    TextView mChatDisplayName;
    TextView mChatGroupName;
    TextView mGroupDesc;
    private NoDisturbingDAO mNoDisturbingDAO;
    private NoDisturbing mNoDisturbing;
    private boolean isOpenGroupName;//是否显示群名称
    private boolean isChangeShow;//是否改变显示
    private String mGroupId;
    private String mMemberId;
    //    private String mGroupName;
    private String mOriginalChatDisplayName;//群聊原始显示名称
    private String mOriginalGroupName;
    private String mOriginalGroupDetail;
    private int mChatGroupType;
    private ChatGroupMemberAdapter groupMemberAdapter;
    private OnChatManagerListener mOnChatManagerListener;
    private MiChatHelper miChatHelper;
    private MessageHolder mHolder;
    Handler mHandler = new Handler();
    private ChatMessageCreator mChatMessageCreator;
    private AMapLocation mAMapLocation;
    private GDHelper mGdHelper;

    private static int FRIEND_LIST_CODE = 0x200;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatueColor(this, R.color.mi_white_bg, true);
        mGroupId = getIntent().getStringExtra(MiChatHelper.CHAT_GROUP_ID);
        mMemberId = getIntent().getStringExtra(MiChatHelper.CHAT_GROUP_MEMBER_ID);
        mOriginalGroupName = getIntent().getStringExtra(MiChatHelper.CHAT_GROUP_NAME);
        mOriginalGroupDetail = getIntent().getStringExtra(MiChatHelper.CHAT_GROUP_DETAIL);
        mChatGroupType = getIntent().getIntExtra(MiChatHelper.CHAT_GROUP_TYPE, MiChatHelper.CHAT_GROUP);
        mOriginalChatDisplayName = getIntent().getStringExtra(MiChatHelper.CHAT_GROUP_MEMBER_GROUP_NAME);
        ArrayList<MessageHolder> mGroupMembers = (ArrayList<MessageHolder>) getIntent().getSerializableExtra(MiChatHelper.CHAT_GROUP_MEMBER);
        EventBus.getDefault().register(this);
        miChatHelper = MiChatHelper.getInstance();
        mHolder = miChatHelper.getMessageHolder();
        mOnChatManagerListener = miChatHelper.getOnChatManagerListener();
        mChatMessageCreator = new ChatMessageCreator(mGroupId, mOriginalGroupName, miChatHelper, mHolder, mHandler, null);
        mGdHelper = new GDHelper.Builder().setNaviType(NaviType.GPS).build(getApplicationContext());

        mGdHelper.startContinueLocation(aMapLocation -> {
            if (aMapLocation.getErrorCode() == 0) mAMapLocation = aMapLocation;
            Log.d("Location-->", aMapLocation.getAddress());
        });

        setContentView(R.layout.mi_activity_chat_detail);
        findViewById(R.id.mi_iv_back).setOnClickListener(v -> onBack());
        mIsOpenNoDisturbing = findViewById(R.id.mi_is_open_no_disturbing);
        mChatDisplayName = findViewById(R.id.mi_chat_display_name);
        mChatGroupName = findViewById(R.id.mi_group_name);
        mIsOpenName = findViewById(R.id.mi_is_open_name);
        mGroupDesc = findViewById(R.id.mi_group_desc);
        mGroupMemberCount = findViewById(R.id.mi_member_count);
        mExistGroup = findViewById(R.id.mi_exist_group);
        mChatGroupContainer = findViewById(R.id.chat_group_container);
        mChatGroupContainer.setVisibility(mChatGroupType == MiChatHelper.CHAT_GROUP ? View.VISIBLE : View.GONE);
        new Thread(() -> {
            mNoDisturbingDAO = DBHelper.getInstance(this).getNoDisturbingDAO();
            mNoDisturbing = mNoDisturbingDAO.getNoDisturbing(mMemberId, mGroupId);
            if (mNoDisturbing == null) {
                mNoDisturbing = new NoDisturbing();
                mNoDisturbing.setChatGroupHolderID(mMemberId);
                mNoDisturbing.setChatGroupId(mGroupId);
                mNoDisturbing.setOpen(false);
                mNoDisturbingDAO.insertNoDisturbing(mNoDisturbing);
            }
            runOnUiThread(() -> {
                //开关初始化
                mIsOpenNoDisturbing.setChecked(mNoDisturbing.isOpen());
                //获取是否显示群聊名称
                isOpenGroupName = SPHelper.getInstance(this).getBoolean(SPHelper.IS_OPEN_GROUP_NAME);
                mIsOpenName.setChecked(isOpenGroupName);
            });
        }).start();

        mChatGroupName.setText(mOriginalGroupName);
        mGroupDesc.setText(mOriginalGroupDetail);
        mChatDisplayName.setText(mOriginalChatDisplayName);
        mGroupMemberCount.setText("(" + mGroupMembers.size() + ")");

        findViewById(R.id.mi_show_chat_history).setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatGroupHistoryActivity.class);
            intent.putExtra(MiChatHelper.CHAT_GROUP_ID, mGroupId);
            startActivity(intent);
        });
        mIsOpenNoDisturbing.setOnCheckedChangeListener((buttonView, isChecked) -> {//开启免打扰
            mNoDisturbing.setOpen(isChecked);
            mNoDisturbingDAO.upDateNoDisturbing(mNoDisturbing);
        });

        mIsOpenName.setOnCheckedChangeListener((buttonView, isChecked) -> {//显示群聊名称
            SPHelper.getInstance(this).putBoolean(SPHelper.IS_OPEN_GROUP_NAME, isChecked);
            isChangeShow = !isChecked == isOpenGroupName;
        });

        List<ChatGroupMembers> members = new ArrayList<>();
        for (MessageHolder messageHolder : mGroupMembers) {
            ChatGroupMembers groupMembers = new ChatGroupMembers(1, messageHolder);
            members.add(groupMembers);
        }
        members.add(new ChatGroupMembers(2, new MessageHolder()));
        mChatMemberShow = findViewById(R.id.mi_member_show);
        mChatMemberShow.setLayoutManager(new GridLayoutManager(this, 5));
        mChatMemberShow.setHasFixedSize(true);

        for (ChatGroupMembers groupMembers:members){
            if (miChatHelper.getMessageHolder().getId().equals(groupMembers.getMessageHolder().getId())){
                members.remove(groupMembers);
                break;
            }
        }

        mChatMemberShow.setAdapter(groupMemberAdapter = new ChatGroupMemberAdapter(members));
        groupMemberAdapter.setOnItemChildClickListener((view, data) -> {
            if (view.getId() == R.id.mi_tag_por) {
                Intent intent = new Intent(this, ChatPersonalActivity.class);
                intent.putExtra(CHAT_MEMBER, data.getMessageHolder());
                startActivity(intent);
            }
            if (view.getId() == R.id.mi_tag_add) {
                Intent intent = new Intent(this, ChatFriendListActivity.class);
                intent.putExtra(MiChatHelper.CHAT_GROUP_MEMBER, mGroupMembers);
                startActivityForResult(intent, FRIEND_LIST_CODE);
            }
        });
        mChatGroupName.setOnClickListener(v -> PopupWindowsHelper.showEditWindows(this, "群名称", mOriginalGroupName, "设置本群群名称", label -> {
            if (mOnChatManagerListener != null) {
                mChatMessageCreator.createChatMessage(11, mAMapLocation, "修改群名称", label, chatMessage -> {
                    mOnChatManagerListener.changeChatGroupName(mGroupId, chatMessage);
                    LoadingHelper.showLoading(this);
                });
            }
        }));

        mChatDisplayName.setOnClickListener(v -> PopupWindowsHelper.showEditWindows(this, "我在本群的昵称", mOriginalChatDisplayName, "设置你在本群的昵称，该昵称只在本群内显示", label -> {
            if (mOnChatManagerListener != null) {
                mChatMessageCreator.createChatMessage(12, mAMapLocation, "修改昵称", label, chatMessage -> {
                    mOnChatManagerListener.changeChatDisplayName(mGroupId, chatMessage);
                    LoadingHelper.showLoading(this);
                });
            }

        }));

        mGroupDesc.setOnClickListener(v -> PopupWindowsHelper.showEditWindows(this, "群公告", mOriginalGroupDetail, "修改本群公告，该公告只在本群内显示", label -> {
            if (mOnChatManagerListener != null) {
                mChatMessageCreator.createChatMessage(15, mAMapLocation, "修改群公告", label, chatMessage -> {
                    mOnChatManagerListener.changeChatGroupDesc(mGroupId, chatMessage);
                    LoadingHelper.showLoading(this);
                });
            }

        }));

        mExistGroup.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示").setMessage("是否退出群组？")
                    .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("确定", (dialog, which) -> {
                        dialog.dismiss();
                        if (mOnChatManagerListener != null) {
                            mChatMessageCreator.createChatMessage(14, mAMapLocation, "退出群聊", chatMessage -> {
                                mOnChatManagerListener.existChatGroup(mGroupId, chatMessage);
                                LoadingHelper.showLoading(this);
                            });
                        }
                    });
            builder.show();

        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void chatGroupControl(EBChat groupControl) {
        if (groupControl.getType() == EBChat.MQ_UPDATE_CHAT_DISPLAY_NAME) {
            LoadingHelper.closeLoading();
            EBChatManager ebChatManager = groupControl.getEbChatManager();
            switch (ebChatManager.getType()) {
                case EBChatManager.TYPE_ADD_MEMBER_FAIL:
                    Toast.makeText(this, "添加群成员失败", Toast.LENGTH_SHORT).show();
                    break;
                case EBChatManager.TYPE_ADD_MEMBER_SUCCESS:
                    Toast.makeText(this, "添加群成员成功", Toast.LENGTH_SHORT).show();
                    List<MessageHolder> newHolders = ebChatManager.getChatMessage().getNewHolders();
                    groupMemberAdapter.addData(groupMemberAdapter.getItemCount() - 1, new ChatGroupMembers(1, newHolders.get(0)));
                    break;
                case EBChatManager.TYPE_EXIST_FAIL:
                    Toast.makeText(this, "退出群聊失败", Toast.LENGTH_SHORT).show();
                    break;
                case EBChatManager.TYPE_EXIST_SUCCESS:
                    Toast.makeText(this, "退出群聊成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("isExistGroup", true);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case EBChatManager.TYPE_UPDATE_CHAT_DISPLAY_SUCCESS: //更新Adapter中群聊显示名称
                    mChatDisplayName.setText(ebChatManager.getChatMessage().getLabel());
                    mOriginalChatDisplayName = ebChatManager.getChatMessage().getLabel();
                    Toast.makeText(this, "群聊显示名称修改成功", Toast.LENGTH_SHORT).show();
                    break;
                case EBChatManager.TYPE_UPDATE_CHAT_DISPLAY_ERROR://自己更新群聊显示名称失败
                    mChatDisplayName.setText(mOriginalChatDisplayName);
                    Toast.makeText(this, "群聊显示名称修改失败", Toast.LENGTH_SHORT).show();
                    break;
                case EBChatManager.TYPE_UPDATE_GROUP_NAME_SUCCESS://自己更新群聊显示名称成功
                    mChatGroupName.setText(ebChatManager.getChatMessage().getLabel());
                    mOriginalGroupName = ebChatManager.getChatMessage().getLabel();
                    Toast.makeText(this, "群名称修改成功", Toast.LENGTH_SHORT).show();
                    break;
                case EBChatManager.TYPE_UPDATE_GROUP_NAME_FAIL://自己更新群名称失败
                    mChatGroupName.setText(mOriginalGroupName);
                    SPHelper.getInstance(this).putString(CHAT_GROUP_NAME, mOriginalGroupName);
                    Toast.makeText(this, "群名称修改失败", Toast.LENGTH_SHORT).show();
                    break;
                case EBChatManager.TYPE_UPDATE_GROUP_DESC_SUCCESS://自己更新群公告成功
                    mGroupDesc.setText(ebChatManager.getChatMessage().getLabel());
                    mOriginalGroupDetail = ebChatManager.getChatMessage().getLabel();
                    Toast.makeText(this, "群公告修改成功", Toast.LENGTH_SHORT).show();
                    break;
                case EBChatManager.TYPE_UPDATE_GROUP_DESC_FAIL://自己更新群公告失败
                    mGroupDesc.setText(mOriginalGroupDetail);
                    SPHelper.getInstance(this).putString(CHAT_GROUP_DETAIL, mOriginalGroupDetail);
                    Toast.makeText(this, "群公告修改失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == FRIEND_LIST_CODE) {
                ArrayList<MessageHolder> messageHolders = (ArrayList<MessageHolder>) data.getSerializableExtra(MiChatHelper.CHAT_GROUP_MEMBER);
                if (mOnChatManagerListener != null) {
                    mChatMessageCreator.createChatMessage(13, mAMapLocation, "加入群聊", chatMessage -> {
                        chatMessage.setNewHolders(messageHolders);
                        mOnChatManagerListener.addChatGroupMember(mGroupId, chatMessage);
                    });
                    LoadingHelper.showLoading(this);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    private void onBack() {
        Intent intent = new Intent();
        intent.putExtra("isChangeShow", isChangeShow);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
