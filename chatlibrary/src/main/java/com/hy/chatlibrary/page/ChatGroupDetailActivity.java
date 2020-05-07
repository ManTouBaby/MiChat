package com.hy.chatlibrary.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hrw.chatlibrary.R;
import com.hy.chatlibrary.MiChatHelper;
import com.hy.chatlibrary.adapter.BaseAdapter;
import com.hy.chatlibrary.base.SmartVH;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.utils.StatusBarUtil;
import com.hy.chatlibrary.utils.glide.GlideHelper;

import java.util.ArrayList;

/**
 * @author:MtBaby
 * @date:2020/05/06 15:29
 * @desc:群聊详情
 */
public class ChatGroupDetailActivity extends AppCompatActivity {
    RecyclerView mChatMemberShow;
    TextView mGroupName;
    TextView mGroupDesc;
    TextView mGroupMemberCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mi_activity_chat_detail);
        StatusBarUtil.setStatueColor(this, R.color.mi_chat_main_bg, true);
        findViewById(R.id.mi_iv_back).setOnClickListener(v -> finish());
        mGroupName = findViewById(R.id.mi_group_name);
        mGroupDesc = findViewById(R.id.mi_group_desc);
        mGroupMemberCount = findViewById(R.id.mi_member_count);
        ArrayList<MessageHolder> mGroupMembers = (ArrayList<MessageHolder>) getIntent().getSerializableExtra(MiChatHelper.CHAT_GROUP_MEMBER);
        String groupId = getIntent().getStringExtra(MiChatHelper.CHAT_GROUP_ID);
        String groupName = getIntent().getStringExtra(MiChatHelper.CHAT_GROUP_NAME);
        String groupDetail = getIntent().getStringExtra(MiChatHelper.CHAT_GROUP_DETAIL);

        mGroupName.setText(groupName);
        mGroupDesc.setText(groupDetail);
        mGroupMemberCount.setText("(" + mGroupMembers.size() + ")");

        mChatMemberShow = findViewById(R.id.mi_member_show);
        mChatMemberShow.setLayoutManager(new GridLayoutManager(this, 5));
        mChatMemberShow.setHasFixedSize(true);
        mChatMemberShow.setAdapter(new BaseAdapter<MessageHolder>(mGroupMembers, R.layout.item_chat_group_member) {
            @Override
            protected void onBindView(SmartVH holder, MessageHolder data, int position) {
                ImageView image = holder.getImage(R.id.mi_tag_por);
                GlideHelper.loadIntoUseNoCorner(ChatGroupDetailActivity.this, data.getPortrait(), image);
                holder.getText(R.id.mi_member_name).setText(data.getName());
            }
        });
    }
}
