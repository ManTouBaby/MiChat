package com.hy.chatlibrary.page;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.hrw.chatlibrary.R;
import com.hy.chatlibrary.MiChatHelper;
import com.hy.chatlibrary.adapter.BaseAdapter;
import com.hy.chatlibrary.adapter.OnItemClickListener;
import com.hy.chatlibrary.base.SmartVH;
import com.hy.chatlibrary.db.DBHelper;
import com.hy.chatlibrary.db.dao.ChatMessageDAO;
import com.hy.chatlibrary.db.entity.ChatMessage;
import com.hy.chatlibrary.utils.DateUtil;
import com.hy.chatlibrary.utils.SPHelper;
import com.hy.chatlibrary.utils.StatusBarUtil;
import com.hy.chatlibrary.widget.CornerTextView;

import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/05/12 16:47
 * @desc:
 */
public class ChatGroupHistoryActivity extends AppCompatActivity {
    EditText mEditText;
    BaseAdapter<ChatMessage> mBaseAdapter;
    RecyclerView mRecyclerView;
    LinearLayout mQuickSearchContainer;
    private ChatMessageDAO mChatMessageDAO;
    private InputMethodManager inputMethodManager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mChatMessageDAO = DBHelper.getInstance(this).getChatMessageDAO();
        String chatGroupId = getIntent().getStringExtra(MiChatHelper.CHAT_GROUP_ID);
        StatusBarUtil.setStatueColor(this, R.color.mi_chat_main_bg, true);
        setContentView(R.layout.mi_activity_chat_history);
        mEditText = findViewById(R.id.mi_history_search_input);
        mRecyclerView = findViewById(R.id.mi_show_history);
        mQuickSearchContainer = findViewById(R.id.mi_quick_search_container);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mBaseAdapter = new BaseAdapter<ChatMessage>(R.layout.item_history_show) {
            @Override
            protected void onBindView(SmartVH holder, ChatMessage data, int position) {
                holder.getText(R.id.mi_holder_name).setText(data.getMessageHolderShowName());
                holder.getText(R.id.mi_message_date).setText(DateUtil.getStringTimeByMilli(data.getMessageSTMillis(), "MM月dd日"));
                holder.getText(R.id.mi_message_content).setText(data.getMessageContent());
                CornerTextView cornerTextView = holder.getViewById(R.id.mi_holder_pro);
                cornerTextView.setAutoBackGroundText(data.getMessageHolderName());
            }
        });

        findViewById(R.id.mi_cancel_search_history).setOnClickListener(v -> {
            closeSoftInput();
            finish();
        });

        mRecyclerView.setOnTouchListener((v, event) -> {
            boolean active = inputMethodManager.isActive();
            if (active){
                closeSoftInput();
            }
            return false;
        });



        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                List<ChatMessage> chatMessages = mChatMessageDAO.queryLikeLabel(chatGroupId, s.toString());
                if (!TextUtils.isEmpty(s)) {
                    mBaseAdapter.setDates(chatMessages);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mQuickSearchContainer.setVisibility(View.GONE);
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    mQuickSearchContainer.setVisibility(View.VISIBLE);
                }
            }
        });

        findViewById(R.id.mi_video_photo_search).setOnClickListener(v -> {// 视频/图片查询

        });
        findViewById(R.id.mi_local_search).setOnClickListener(v -> {//位置查询

        });
        findViewById(R.id.mi_instruct_search).setOnClickListener(v -> {//指令查询

        });

        mBaseAdapter.setOnItemClickListener((OnItemClickListener<ChatMessage>) (view, data) -> {
            closeSoftInput();
            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
            finish();
            SPHelper.getInstance(this).putString("scrollTo", data.getMessageId());
        });
    }

    private void closeSoftInput() {
        inputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
