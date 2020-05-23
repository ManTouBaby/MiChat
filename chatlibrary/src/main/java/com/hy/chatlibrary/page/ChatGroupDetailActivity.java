package com.hy.chatlibrary.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.hrw.chatlibrary.R;
import com.hy.chatlibrary.MiChatHelper;
import com.hy.chatlibrary.adapter.BaseAdapter;
import com.hy.chatlibrary.base.SmartVH;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.db.DBHelper;
import com.hy.chatlibrary.db.dao.NoDisturbingDAO;
import com.hy.chatlibrary.db.entity.NoDisturbing;
import com.hy.chatlibrary.utils.SPHelper;
import com.hy.chatlibrary.utils.StatusBarUtil;
import com.hy.chatlibrary.widget.CornerTextView;

import java.util.ArrayList;

/**
 * @author:MtBaby
 * @date:2020/05/06 15:29
 * @desc:群聊详情
 */
public class ChatGroupDetailActivity extends AppCompatActivity {
    RecyclerView mChatMemberShow;
    TextView mGroupMemberCount;
    Switch mIsOpenNoDisturbing;
    Switch mIsOpenName;
    EditText mChatGroupShowName;
    EditText mChatGroupName;
    TextView mGroupDesc;
    private NoDisturbingDAO mNoDisturbingDAO;
    private NoDisturbing mNoDisturbing;
    private boolean isOpenGroupName;//是否显示群名称
    private boolean isChangeShow;//是否改变显示
    private String mGroupId;
    private String mMemberId;
    //    private String mGroupName;
    private String mOriginalGroupMemberName;//群聊原始显示名称
    private String mOriginalGroupName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatueColor(this, R.color.mi_chat_main_bg, true);
        ArrayList<MessageHolder> mGroupMembers = (ArrayList<MessageHolder>) getIntent().getSerializableExtra(MiChatHelper.CHAT_GROUP_MEMBER);
        mGroupId = getIntent().getStringExtra(MiChatHelper.CHAT_GROUP_ID);
        mMemberId = getIntent().getStringExtra(MiChatHelper.CHAT_GROUP_MEMBER_ID);
        mOriginalGroupName = getIntent().getStringExtra(MiChatHelper.CHAT_GROUP_NAME);
        String groupDetail = getIntent().getStringExtra(MiChatHelper.CHAT_GROUP_DETAIL);
        mOriginalGroupMemberName = getIntent().getStringExtra(MiChatHelper.CHAT_GROUP_MEMBER_GROUP_NAME);

        setContentView(R.layout.mi_activity_chat_detail);
        findViewById(R.id.mi_iv_back).setOnClickListener(v -> onBack());
        mIsOpenNoDisturbing = findViewById(R.id.mi_is_open_no_disturbing);
        mChatGroupShowName = findViewById(R.id.mi_group_member_name);
        mChatGroupName = findViewById(R.id.mi_group_name);
        mIsOpenName = findViewById(R.id.mi_is_open_name);
        mGroupDesc = findViewById(R.id.mi_group_desc);
        mGroupMemberCount = findViewById(R.id.mi_member_count);

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
        mGroupDesc.setText(groupDetail);
        mChatGroupShowName.setText(mOriginalGroupMemberName);
        mGroupMemberCount.setText("(" + mGroupMembers.size() + ")");

        findViewById(R.id.mi_show_chat_history).setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatGroupHistoryActivity.class);
            intent.putExtra(MiChatHelper.CHAT_GROUP_ID, mGroupId);
            startActivity(intent);
        });
        mIsOpenNoDisturbing.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mNoDisturbing.setOpen(isChecked);
            mNoDisturbingDAO.upDateNoDisturbing(mNoDisturbing);
        });

        mIsOpenName.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SPHelper.getInstance(this).putBoolean(SPHelper.IS_OPEN_GROUP_NAME, isChecked);
            isChangeShow = !isChecked == isOpenGroupName;
        });

        mChatMemberShow = findViewById(R.id.mi_member_show);
        mChatMemberShow.setLayoutManager(new GridLayoutManager(this, 5));
        mChatMemberShow.setHasFixedSize(true);
        mChatMemberShow.setAdapter(new BaseAdapter<MessageHolder>(mGroupMembers, R.layout.item_chat_group_member) {
            @Override
            protected void onBindView(SmartVH holder, MessageHolder data, int position) {
                CornerTextView textView = holder.getViewById(R.id.mi_tag_por);
                textView.setText(data.getName());
//                GlideHelper.loadIntoUseNoCorner(ChatGroupDetailActivity.this, data.getPortrait(), image);
                holder.getText(R.id.mi_member_name).setText(data.getName());
            }
        });
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    private void onBack() {
        String memberGroupShowName = mChatGroupShowName.getText().toString();
        String mNewGroupName = mChatGroupName.getText().toString();
        boolean isChangeShowName = !mOriginalGroupMemberName.equals(memberGroupShowName);
        boolean isChangeGroupName = !mOriginalGroupName.equals(mNewGroupName);
        SPHelper.getInstance(this).putString(mMemberId + "-" + mGroupId, memberGroupShowName);
        Intent intent = new Intent();
        intent.putExtra("isChangeShow", isChangeShow);
        intent.putExtra("originalName", mOriginalGroupMemberName);
        intent.putExtra("originalGroupName", mOriginalGroupName);
        if (isChangeShowName) intent.putExtra("changeName", memberGroupShowName);
        if (isChangeGroupName) intent.putExtra("changeGroupName", mNewGroupName);

        setResult(RESULT_OK, intent);
        finish();
    }
}
