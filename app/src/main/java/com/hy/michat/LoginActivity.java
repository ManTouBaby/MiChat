package com.hy.michat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.hy.michat.rabbitMQ.MQLoginResult;
import com.hy.michat.rabbitMQ.RabbitMQManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * @author:MtBaby
 * @date:2020/05/07 17:58
 * @desc:
 */
public class LoginActivity extends AppCompatActivity {
    //    private RabbitMQManager mRabbitMQManager;
    private EditText loginUser;
    private EditText loginPW;
    private CheckBox checkBox;
    private AlertDialog dialog;
    private SharedPreferences mLoginRemember;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginUser = findViewById(R.id.mi_login_user);
        loginPW = findViewById(R.id.mi_login_ps);
        checkBox = findViewById(R.id.login_remember);

        EventBus.getDefault().register(this);
//        mRabbitMQManager = RabbitMQManager.getInstance();
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            //让应用主题内容占用系统状态栏的空间,注意:下面两个参数必须一起使用 stable 牢固的
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //设置状态栏颜色为透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        mLoginRemember = getSharedPreferences("LoginRemember", Context.MODE_PRIVATE);
        boolean isRemember = mLoginRemember.getBoolean("isRemember", false);
        if (isRemember) {
            String loginUserLabel = mLoginRemember.getString("loginUser", null);
            String loginPSLabel = mLoginRemember.getString("loginPS", null);
            loginUser.setText(loginUserLabel);
            loginPW.setText(loginPSLabel);
        }
        checkBox.setChecked(isRemember);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mLoginRemember.edit().putBoolean("isRemember", isChecked).apply();
        });
        Button button = findViewById(R.id.mi_login_submit);
        //登录按钮
        button.setOnClickListener(v -> {
            showLoginAnimate();
            String loginUser = this.loginUser.getText().toString();
            String loginPW = this.loginPW.getText().toString();
            ChatManager.getInstance().loginChatGroup(this, loginUser, loginPW,
                    "440106970002",
                    "政治处",
                    "处长",
                    0,
                    "13829793053",
                    "http://i0.hdslb.com/bfs/article/8e87829cde9559c8407892aa6110f83a4631c6b3.jpg",
                    RabbitMQManager.getInstance());
        });

    }

    private void showLoginAnimate() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        dialog = alertDialog.setView(R.layout.popup_loading).show();
        dialog.setCanceledOnTouchOutside(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginMsg(MQLoginResult isLogin) {
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
        if (isLogin.isLogin) {

            boolean isChecked = mLoginRemember.getBoolean("isRemember", false);
            if (isChecked) {
                mLoginRemember.edit().putString("loginUser", loginUser.getText().toString()).apply();
                mLoginRemember.edit().putString("loginPS", loginPW.getText().toString()).apply();
            }
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
