package com.hy.chatlibrary.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Toast;

import com.hrw.chatlibrary.R;
import com.hy.chatlibrary.MiChatHelper;
import com.hy.chatlibrary.adapter.BaseAdapter;
import com.hy.chatlibrary.adapter.OnItemClickListener;
import com.hy.chatlibrary.base.SmartVH;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.listener.OnChatInputListener;
import com.hy.chatlibrary.service.EBFriendInit;
import com.hy.chatlibrary.utils.StatusBarUtil;
import com.hy.chatlibrary.widget.CornerTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/05/26 14:27
 * @desc:
 */
public class ChatFriendListActivity extends AppCompatActivity {
    RecyclerView mFriendList;
    BaseAdapter<MessageHolder> holderBaseAdapter;
    private ArrayList<MessageHolder> mGroupMembers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mi_activity_friend_list);
        findViewById(R.id.mi_iv_back).setOnClickListener(v -> finish());
        mGroupMembers = (ArrayList<MessageHolder>) getIntent().getSerializableExtra(MiChatHelper.CHAT_GROUP_MEMBER);
        EventBus.getDefault().register(this);
        StatusBarUtil.setStatueColor(this, R.color.mi_white_bg, true);
        OnChatInputListener onChatInputListener = MiChatHelper.getInstance().getOnChatInputListener();
        mFriendList = findViewById(R.id.mi_friend_list_show);
        mFriendList.setLayoutManager(new LinearLayoutManager(this));
        mFriendList.setAdapter(holderBaseAdapter = new BaseAdapter<MessageHolder>(R.layout.item_friend_show) {
            @Override
            protected void onBindView(SmartVH holder, MessageHolder data, int position) {
                CornerTextView cornerTextView = holder.getViewById(R.id.mi_holder_pro);
                holder.getText(R.id.mi_holder_name).setText(data.getName());
                cornerTextView.setText(data.getName());
            }
        });

        if (onChatInputListener != null) {
            onChatInputListener.onInitFriendList();
        }

        holderBaseAdapter.setOnItemClickListener((OnItemClickListener<MessageHolder>) (view, data) -> {
            ArrayList<MessageHolder> messageHolders = new ArrayList<>();
            messageHolders.add(data);
            Intent intent = new Intent();
            intent.putExtra(MiChatHelper.CHAT_GROUP_MEMBER, messageHolders);
            setResult(RESULT_OK,intent);
            finish();
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void initFriendList(EBFriendInit ebFriendInit) {
        if (TextUtils.isEmpty(ebFriendInit.getErrorLabel())) {
            List<MessageHolder> messageHolders = ebFriendInit.getMessageHolders();
            for (MessageHolder messageHolder : mGroupMembers) {
                for (MessageHolder holder : messageHolders) {
                    if (messageHolder.getId().equals(holder.getId())) {
                        messageHolders.remove(holder);
                        break;
                    }
                }
            }
            holderBaseAdapter.setDates(messageHolders);
        } else {
            Toast.makeText(this, "人员列表初始化失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
