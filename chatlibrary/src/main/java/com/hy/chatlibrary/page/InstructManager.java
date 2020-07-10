package com.hy.chatlibrary.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import com.hy.chatlibrary.MiChatHelper;
import com.hy.chatlibrary.R;
import com.hy.chatlibrary.adapter.BaseAdapter;
import com.hy.chatlibrary.base.SmartVH;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.db.DBHelper;
import com.hy.chatlibrary.db.dao.ChatMessageDAO;
import com.hy.chatlibrary.db.entity.ChatMessage;
import com.hy.chatlibrary.db.entity.InstructBean;
import com.hy.chatlibrary.utils.DateUtil;
import com.hy.chatlibrary.utils.StatusBarUtil;
import com.hy.chatlibrary.utils.StringUtil;
import com.hy.chatlibrary.utils.glide.GlideHelper;

import java.io.File;
import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/06/01 9:17
 * @desc:指令管理
 */
public class InstructManager extends AppCompatActivity {
    private ChatMessageDAO mChatMessageDAO;
    private MessageHolder mMessageHolder;
    private BaseAdapter mBaseAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mi_activity_instruct_manager);
        String mGroupId = getIntent().getStringExtra(MiChatHelper.CHAT_GROUP_ID);
        StatusBarUtil.setStatueColor(this, R.color.mi_white_bg, true);
        RecyclerView mInstructList = findViewById(R.id.rv_instruct_list);

        findViewById(R.id.mi_iv_back).setOnClickListener(v -> finish());
        mChatMessageDAO = DBHelper.getInstance(this).getChatMessageDAO();
        mMessageHolder = MiChatHelper.getInstance().getMessageHolder();

        mInstructList.setLayoutManager(new LinearLayoutManager(this));
        mInstructList.setHasFixedSize(true);
        mInstructList.setAdapter(mBaseAdapter = new BaseAdapter<ChatMessage>(R.layout.item_instruct_manager_show) {
            @Override
            protected void onBindView(SmartVH holder, ChatMessage chatMessage, int position) {
                InstructBean instructBean = chatMessage.getInstructBean();
                TextView instructTitle = holder.getText(R.id.mi_instruct_title);
                TextView instructContent = holder.getText(R.id.mi_instruct_content);
                TextView instructVideoTime = holder.getText(R.id.mi_instruct_video_time);
                ImageView instructPic = holder.getImage(R.id.mi_instruct_pic);
                ImageView instructVideoBG = holder.getImage(R.id.mi_instruct_video_bg);
                RelativeLayout instructVideoContainer = holder.getViewById(R.id.mi_instruct_video_container);
                RecyclerView mAcceptorShow = holder.getViewById(R.id.mi_acceptor_show);
                FlexboxLayoutManager manager = new FlexboxLayoutManager(InstructManager.this, FlexDirection.ROW, FlexWrap.WRAP);
                manager.setJustifyContent(JustifyContent.SPACE_BETWEEN);//交叉轴的起点对齐。
                mAcceptorShow.setLayoutManager(manager);
                mAcceptorShow.setHasFixedSize(true);
                mAcceptorShow.setAdapter(new BaseAdapter<MessageHolder>(instructBean.getAcceptors(), R.layout.item_acceptor_tag) {
                    @Override
                    protected void onBindView(SmartVH holder, MessageHolder data, int position) {
                        TextView miTagName = holder.getViewById(R.id.mi_tag_name);
                        miTagName.setText(TextUtils.isEmpty(data.getGroupName()) ? data.getName() : data.getGroupName());
                    }
                });
                instructTitle.setText(StringUtil.isEmpty(instructBean.getTitle()));
                instructContent.setText(StringUtil.isEmpty(instructBean.getContent()));

                String localFilePath = instructBean.getLocalFilePath();
                File instructFile = new File(StringUtil.isEmpty(localFilePath));
                if (!instructFile.exists()) {
                    localFilePath = instructBean.getNetFilePath();
                }
                long duration = instructBean.getDuration();
                instructVideoContainer.setVisibility(View.GONE);
                instructPic.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(localFilePath)) {
                    if (duration > 0) {//视屏文件
                        instructVideoContainer.setVisibility(View.VISIBLE);
                        instructVideoTime.setText(DateUtil.long2String(duration));
                        GlideHelper.loadIntoUseNoCorner(holder.itemView.getContext(), localFilePath, instructVideoBG);
                    } else {//图片文件
                        instructPic.setVisibility(View.VISIBLE);
                        GlideHelper.loadIntoUseNoCorner(holder.itemView.getContext(), localFilePath, instructPic);
                    }
                }
            }
        });

        new Thread(() -> {
            List<ChatMessage> chatMessages = mChatMessageDAO.queryInstructByGroupIdAndHolderId(mGroupId, mMessageHolder.getId());
            runOnUiThread(() -> mBaseAdapter.setDates(chatMessages));
        }).start();

    }
}
