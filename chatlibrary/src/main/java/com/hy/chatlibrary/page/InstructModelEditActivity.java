package com.hy.chatlibrary.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.hy.chatlibrary.R;
import com.hy.chatlibrary.utils.StatusBarUtil;
import com.hy.chatlibrary.utils.UUIDUtil;

/**
 * @author:MtBaby
 * @date:2020/04/29 9:48
 * @desc:
 */
public class InstructModelEditActivity extends AppCompatActivity {
    private EditText mInstructTitle;
    private EditText mInstructContent;
    private String uuid;
    private String instructTitle;
    private String instructContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatueColor(this, R.color.mi_white_bg, true);
        setContentView(R.layout.mi_activity_instruct_model_edit);
        mInstructTitle = findViewById(R.id.mi_et_instruct_title);
        mInstructContent = findViewById(R.id.mi_et_instruct_content);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            uuid = bundle.getString("instructId");
            instructTitle = bundle.getString("instructTitle");
            instructContent = bundle.getString("instructContent");

            mInstructTitle.setText(instructTitle);
            mInstructContent.setText(instructContent);
        }


        findViewById(R.id.mi_iv_back).setOnClickListener(v -> goBack());
        findViewById(R.id.mi_tv_save_model).setOnClickListener(v -> {
            instructTitle = mInstructTitle.getText().toString();
            instructContent = mInstructContent.getText().toString();
            if (TextUtils.isEmpty(instructContent) || TextUtils.isEmpty(instructTitle)) {
                Toast.makeText(this, "标题和指令内容都不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("instructTitle", instructTitle);
            intent.putExtra("instructContent", instructContent);
            intent.putExtra("instructId", uuid == null ? UUIDUtil.getUUID() : uuid);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void goBack() {
        String title = mInstructTitle.getText().toString();
        if (!TextUtils.isEmpty(title)) {
            new AlertDialog.Builder(this).setTitle("温馨提示")
                    .setMessage("确定放弃当前指令模板编辑？")
                    .setPositiveButton("确定", (dialog, which) -> finish())
                    .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
}
