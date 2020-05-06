package com.hy.michat;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.hrw.michat.R;
import com.hy.chatlibrary.MiChatHelper;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.db.ChatMessage;
import com.hy.chatlibrary.listener.OnChatInputListener;
import com.hy.chatlibrary.listener.OnNetMessageControl;
import com.hy.chatlibrary.utils.StringUtil;
import com.hy.michat.rabbitMQ.EvenBusChatMessage;
import com.hy.michat.rabbitMQ.RabbitMQManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements OnChatInputListener {
    EditText mGroupId;
    EditText mUserName;
    EditText mUserPS;
    TextView mLoginMsg;
    Handler mHandler = new Handler();
    RabbitMQManager mRabbitMQManager;
    OnNetMessageControl mOnNetMessageControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        mGroupId = findViewById(R.id.chat_group_id);
        mUserName = findViewById(R.id.chat_group_name);
        mUserPS = findViewById(R.id.chat_group_ps);
        mLoginMsg = findViewById(R.id.login_notify);
        mRabbitMQManager = RabbitMQManager.getInstance();

//        GlideApp.with(this).load("http://112.94.13.13:8006/file/downloadFile.do?id=e11c6177-76a8-403e-9aef-358514ed63b0").into(imageView);

        findViewById(R.id.login_mq).setOnClickListener(v -> {
            mRabbitMQManager.connect(mUserName.getText().toString(), mUserName.getText().toString(), mUserPS.getText().toString());
        });

        findViewById(R.id.goto_chat).setOnClickListener(v -> {
            MessageHolder messageHolder = new MessageHolder();
            messageHolder.setId(mUserName.getText().toString());
            messageHolder.setName(mUserName.getText().toString());
            messageHolder.setDepartId("111111");
            messageHolder.setDepartName("黑猫典狱");
            messageHolder.setDuty("典狱长");
            messageHolder.setGender(0);
            messageHolder.setMobile("13829793334");
            messageHolder.setPortrait("http://i0.hdslb.com/bfs/article/8e87829cde9559c8407892aa6110f83a4631c6b3.jpg");
            ArrayList<MessageHolder> groupMembers = new ArrayList<>();
            MiChatHelper.Option option = new MiChatHelper.Option();
            option.setFileDirName(getPackageName())
                    .setNetTimeUrl("http://www.baidu.com")
                    .setOpenNetTime(true)
                    .setMessageHolder(messageHolder)
                    .setGroupMembers(groupMembers)
                    .setOnChatInputListener(this);
            MiChatHelper miChatHelper = MiChatHelper.getInstance().setOption(option);
            miChatHelper.gotoChat(this, StringUtil.isEmpty(mGroupId.getText().toString(), "0001"), "测试聊天群", MiChatHelper.CHAT_GROUP);
        });
    }

    @Override
    public void onMessageSend(ChatMessage message, String chatMessageJson) {
        message.setMessageStatus(0);
        mOnNetMessageControl.onSendSuccess(message);
//        if (message.getItemType() == 1 || message.getItemType() == 2 || message.getItemType() == 3 || message.getItemType() == 5) {
//            File file = new File(message.getMessageContent());
//            //如果用第二种方式上传头像   可以不用写      //f为file路径
//            MultipartBody.Part filePart = MultipartBody.Part.createFormData("filedata", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
//            RetrofitHelper.buildRetrofit().create(FileApi.class).uploadFile(filePart)
//                    .subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
//                    .subscribe(new Observer<ResponseBody>() {
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
////                            System.out.println("文件上传失败：" + e.getMessage());
//                            mOnSendResult.onSendFail(message);
//                        }
//
//                        @Override
//                        public void onNext(ResponseBody responseBody) {
//                            try {
//                                String json = responseBody.string();
////                                System.out.println("文件上传结果:" + json);
//                                BaseResult baseResult = JSON.parseObject(json, BaseResult.class);
//                                message.setMessageContent(AppConfig.FILE_SERVER + baseResult.getData().getId());
//                                mRabbitMQManager.sendMsg(RabbitMQManager.GROUP_ID, mGroupId.getText().toString(), mUserName.getText().toString(), message);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//        } else {
//            if (mRabbitMQManager.isLoginSuccess()) {
//                new Thread(() -> mRabbitMQManager.sendMsg(RabbitMQManager.GROUP_ID, mGroupId.getText().toString(), mUserName.getText().toString(), message)).start();
//            }
//        }

    }


    @Override
    public void onChatMessageCallBack(ChatMessage message) {

    }

    @Override
    public void onInitChatList(ChatMessage chatMessage, OnNetMessageControl onNetMessageControl) {
        //以chatMessage的时间为查询开始时间，从服务器获取最新数据
        mOnNetMessageControl = onNetMessageControl;
//        mOnNetMessageControl.addNetMessages();
    }

    private void sendMessage(final ChatMessage message) {

        Observable.timer(2, TimeUnit.SECONDS)
                .flatMap((Func1<Long, Observable<ChatMessage>>) aLong -> Observable.create(subscriber -> {
                    subscriber.onNext(message);
                    int index = (int) (Math.random() * 2);
                    if (index == 0) {
                        subscriber.onCompleted();
                    }
                    if (index == 1) {
                        subscriber.onError(new Throwable(""));
                    }
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ChatMessage>() {
                    @Override
                    public void onCompleted() {
                        message.setMessageStatus(0);
                        mOnNetMessageControl.onSendSuccess(message);
                    }

                    @Override
                    public void onError(Throwable e) {
                        message.setMessageStatus(1);
                        mOnNetMessageControl.onSendFail(message);
                    }

                    @Override
                    public void onNext(ChatMessage chatMessage) {

                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventbusMes(EvenBusChatMessage message) {
        switch (message.getType()) {
            case EvenBusChatMessage.TYPE_ERROR:
                mOnNetMessageControl.onSendFail(message.getChatMessage());
                break;
            case EvenBusChatMessage.TYPE_SUCCESS:
                mOnNetMessageControl.onSendSuccess(message.getChatMessage());
                break;
            case EvenBusChatMessage.TYPE_MQ_MSG:
                mOnNetMessageControl.addMQMessage(message.getChatMessage());
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginMsg(boolean isLogin) {
        mLoginMsg.setText(isLogin ? "登录成功" : "登录失败");
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
