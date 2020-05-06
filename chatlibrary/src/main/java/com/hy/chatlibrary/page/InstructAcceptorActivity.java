package com.hy.chatlibrary.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.hrw.chatlibrary.R;

/**
 * @author:MtBaby
 * @date:2020/04/30 14:11
 * @desc:
 */
public class InstructAcceptorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mi_activity_instruct_member_select);
        findViewById(R.id.mi_iv_back).setOnClickListener(v -> goBack());
    }

    private void goBack() {
        new AlertDialog.Builder(this).setTitle("温馨提示")
                .setMessage("放弃指令接收人员设置？")
                .setPositiveButton("确定", (dialog, which) -> finish())
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
}
