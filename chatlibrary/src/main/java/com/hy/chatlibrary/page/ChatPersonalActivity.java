package com.hy.chatlibrary.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.hrw.chatlibrary.R;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.utils.StatusBarUtil;
import com.hy.chatlibrary.utils.glide.GlideHelper;

/**
 * @author:MtBaby
 * @date:2020/05/06 15:33
 * @desc:
 */
public class ChatPersonalActivity extends AppCompatActivity {
    ImageView chatMemberPor;
    TextView memberName;
    TextView memberDepart;
    TextView memberDuty;
    TextView memberPhone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mi_activity_chat_personal);
        findViewById(R.id.mi_iv_back).setOnClickListener(v -> finish());
        memberName = findViewById(R.id.mi_member_name);
        memberDepart = findViewById(R.id.mi_member_depart);
        memberDuty = findViewById(R.id.mi_member_duty);
        memberPhone = findViewById(R.id.mi_member_phone);
        MessageHolder messageHolder = (MessageHolder) getIntent().getSerializableExtra(ChatActivity.CHAT_MEMBER);
        StatusBarUtil.setStatueColor(this, R.color.mi_chat_main_bg, true);
        chatMemberPor = findViewById(R.id.mi_member_por);
        GlideHelper.loadIntoUseNoCorner(this, messageHolder.getPortrait(), chatMemberPor);
        memberName.setText(messageHolder.getName());
        memberDepart.setText(messageHolder.getDepartName());
        memberDuty.setText(messageHolder.getDuty());
        memberPhone.setText(messageHolder.getMobile());
    }
}
