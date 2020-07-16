package com.hy.chatlibrary.page;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hy.chatlibrary.MiChatHelper;
import com.hy.chatlibrary.R;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.listener.OnChatPrivateListener;
import com.hy.chatlibrary.utils.StatusBarUtil;
import com.hy.chatlibrary.widget.CornerTextView;

/**
 * @author:MtBaby
 * @date:2020/05/06 15:33
 * @desc:
 */
public class ChatPersonalActivity extends AppCompatActivity {
    CornerTextView chatMemberPor;
    TextView memberName;
    TextView memberGroupName;
    TextView memberDepart;
    TextView memberDuty;
    TextView memberPhone;
    OnChatPrivateListener mOnChatPrivateListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MiChatHelper mMiChatHelper = MiChatHelper.getInstance();
        mOnChatPrivateListener = mMiChatHelper.getOnChatPrivateListener();
        MessageHolder messageHolder = (MessageHolder) getIntent().getSerializableExtra(ChatActivity.CHAT_MEMBER);
        StatusBarUtil.setStatueColor(this, R.color.mi_chat_main_bg, true);
        setContentView(R.layout.mi_activity_chat_personal);
        findViewById(R.id.mi_iv_back).setOnClickListener(v -> finish());
        findViewById(R.id.base_send_message).setOnClickListener(v -> {
            mOnChatPrivateListener.sendPrivate(this,messageHolder);
        });
        String mobile = messageHolder.getMobile();
        findViewById(R.id.base_call_number).setOnClickListener(v -> {
            if (TextUtils.isEmpty(mobile)) {
                Toast.makeText(this, "电话号码无效", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent1 = new Intent(Intent.ACTION_DIAL);
                intent1.setData(Uri.parse("tel:" + mobile));
                startActivity(intent1);
            }
        });
        memberName = findViewById(R.id.mi_member_name);
        memberGroupName = findViewById(R.id.mi_member_group_name);
        memberDepart = findViewById(R.id.mi_member_depart);
        memberDuty = findViewById(R.id.mi_member_duty);
        memberPhone = findViewById(R.id.mi_member_phone);
        chatMemberPor = findViewById(R.id.mi_member_por);
        findViewById(R.id.chat_group_display_name_container).setVisibility(TextUtils.isEmpty(messageHolder.getGroupName()) ? View.GONE : View.VISIBLE);
        findViewById(R.id.chat_job_container).setVisibility(TextUtils.isEmpty(messageHolder.getDuty()) ? View.GONE : View.VISIBLE);

        memberName.setText(messageHolder.getName());
        memberDepart.setText(messageHolder.getDepartName());
        memberDuty.setText(messageHolder.getDuty());
        memberPhone.setText(mobile);
        chatMemberPor.setText(messageHolder.getName());
        memberGroupName.setText(messageHolder.getGroupName());
    }
}
