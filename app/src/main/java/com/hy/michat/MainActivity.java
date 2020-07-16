package com.hy.michat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.hy.chatlibrary.MiChatHelper;
import com.hy.chatlibrary.bean.ChatGroupDetail;

public class MainActivity extends AppCompatActivity {
    EditText mGroupId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGroupId = findViewById(R.id.chat_group_id);

        findViewById(R.id.go_back_login).setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        findViewById(R.id.goto_chat).setOnClickListener(v -> {
            ChatGroupDetail chatGroupDetail = new ChatGroupDetail();
            chatGroupDetail.setMessageGroupDes("群聊描述测试");
            chatGroupDetail.setMessageGroupId(mGroupId.getText().toString());
            chatGroupDetail.setMessageGroupName("测试聊天群");
            chatGroupDetail.setMessageGroupType(MiChatHelper.CHAT_GROUP);
            ChatManager.getInstance().gotoChatGroup(this, chatGroupDetail);
        });
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
