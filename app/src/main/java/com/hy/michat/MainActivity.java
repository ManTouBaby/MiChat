package com.hy.michat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.hy.chatlibrary.bean.ChatGroupDetail;
import com.hy.chatlibrary.bean.DateModel;
import com.hy.chatlibrary.bean.MessageHolder;

import java.util.ArrayList;

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

            ArrayList<MessageHolder> groupMembers = new ArrayList<>();
            for (int i = 0; i < DateModel.images.length; i++) {
                MessageHolder messageHolder = new MessageHolder();
                messageHolder.setPortrait(DateModel.images[i]);
                messageHolder.setMobile("1111111111" + i);
                messageHolder.setGender(i % 2);
                messageHolder.setDepartName("政治处");
                messageHolder.setDuty("分局局长" + i);
                messageHolder.setName("张晓" + i);
                messageHolder.setId("14254" + i);
                messageHolder.setDepartId("440106970002");
                groupMembers.add(messageHolder);
            }
            ChatGroupDetail chatGroupDetail = new ChatGroupDetail();
            chatGroupDetail.setGroupMembers(groupMembers);
            chatGroupDetail.setMessageGroupDes("群聊描述测试");
            chatGroupDetail.setMessageGroupId(mGroupId.getText().toString());
            chatGroupDetail.setMessageGroupName("测试聊天群");
            String json = JSON.toJSONString(chatGroupDetail);
            ChatManager.getInstance().gotoChatGroup(this, chatGroupDetail);
        });
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
