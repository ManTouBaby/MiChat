package com.hy.chatlibrary.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hy.chatlibrary.R;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.db.entity.InstructBean;
import com.hy.chatlibrary.utils.StatusBarUtil;
import com.hy.filelibrary.FileMode;
import com.hy.filelibrary.FilePicker;
import com.hy.filelibrary.SelectMode;
import com.hy.filelibrary.base.FileBean;
import com.hy.filelibrary.base.OnSelectFinishListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.hy.chatlibrary.base.ResultCode.REQUEST_TAKE_INSTRUCT_MEMBERS;
import static com.hy.chatlibrary.base.ResultCode.REQUEST_TAKE_INSTRUCT_MODEL;
import static com.hy.chatlibrary.base.ResultCode.REQUEST_TAKE_PHOTO_VIDEO;

/**
 * @author:MtBaby
 * @date:2020/04/29 9:46
 * @desc:
 */
public class InstructEditActivity extends AppCompatActivity implements OnSelectFinishListener {
    private ImageView mShow;
    private ImageView mSelectImage;
    private TextView mAcceptorCount;
    private EditText mInstructTitle;
    private EditText mInstructContent;
    private RelativeLayout mShowContainer;

    private InstructBean mInstructBean;
    private ArrayList<MessageHolder> mSelectAcceptor = new ArrayList<>();
    private List<FileBean> mFileBeans;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatueColor(this, R.color.mi_white_bg, true);
        setContentView(R.layout.mi_activity_instruct_edit);
        mAcceptorCount = findViewById(R.id.mi_acceptor_cont);
        mInstructTitle = findViewById(R.id.mi_et_instruct_title);
        mInstructContent = findViewById(R.id.mi_instruct_content);
        mShow = findViewById(R.id.mi_select_photo_show);
        mShowContainer = findViewById(R.id.mi_select_photo_show_container);
        mSelectImage = findViewById(R.id.mi_select_photo);
        ArrayList<MessageHolder> mGroupMembers = (ArrayList<MessageHolder>) getIntent().getSerializableExtra("AcceptorList");
        mSelectAcceptor = mGroupMembers;

        findViewById(R.id.mt_member_show_container).setOnClickListener(v -> {
            Intent intent = new Intent(this, InstructAcceptorActivity.class);
            intent.putExtra("AcceptorList", mGroupMembers);
            intent.putExtra("AcceptorListSelect", mSelectAcceptor);
            startActivityForResult(intent, REQUEST_TAKE_INSTRUCT_MEMBERS);
        });
        findViewById(R.id.mi_delete_select).setOnClickListener(v -> {
            mShowContainer.setVisibility(View.GONE);
            mSelectImage.setVisibility(View.VISIBLE);
            mFileBeans.clear();
        });
        findViewById(R.id.mi_tv_go_model).setOnClickListener(v -> {
            Intent intent = new Intent(this, InstructModelActivity.class);
            startActivityForResult(intent, REQUEST_TAKE_INSTRUCT_MODEL);
        });
        findViewById(R.id.mi_iv_back).setOnClickListener(v -> goBack());
        findViewById(R.id.mi_send_instruct).setOnClickListener(v -> {
            String instructTitle = mInstructTitle.getText().toString();
            String instructContent = mInstructContent.getText().toString();
            if (TextUtils.isEmpty(instructTitle) || TextUtils.isEmpty(instructContent)) {
                Toast.makeText(this, "必须输入指令标题才能进行发送", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mInstructBean == null) mInstructBean = new InstructBean();

            if (mFileBeans != null && mFileBeans.size() > 0) {
                FileBean mFileBean = mFileBeans.get(0);
                mInstructBean.setLocalFilePath(mFileBean.getPath());
                mInstructBean.setNetFilePath(mFileBean.getPath());
                mInstructBean.setDuration(mFileBean.getDuration());
                mInstructBean.setFileName(mFileBean.getFileName());
                mInstructBean.setFileSize(new File(mFileBean.getPath()).length());
            }
            mInstructBean.setTitle(instructTitle);
            mInstructBean.setContent(instructContent);
            mInstructBean.setAcceptors(mSelectAcceptor);

            Intent intent = new Intent();
            intent.putExtra("InstructItem", mInstructBean);
            setResult(RESULT_OK, intent);
            finish();
        });

        mSelectImage.setOnClickListener(v -> {
            new FilePicker.Builder()
                    .setMaxCount(9)
                    .setSelectionMode(SelectMode.SELECT_MODE_MULTI)
                    .setTitle("图片/视频")
                    .setFileMode(FileMode.IMAGE_VIDEO)
                    .builder()
                    .setOnSelectFinishListener(this)
                    .openFilePicker(this);
        });
    }

    private void goBack() {
        String title = mInstructTitle.getText().toString();
        if (!TextUtils.isEmpty(title)) {
            new AlertDialog.Builder(this).setTitle("温馨提示")
                    .setMessage("确定放弃当前指令编辑？")
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_INSTRUCT_MODEL) {
                mInstructBean = (InstructBean) data.getSerializableExtra("InstructItem");
                mInstructTitle.setText(mInstructBean.getTitle());
                mInstructContent.setText(mInstructBean.getContent());
            }
            if (requestCode == REQUEST_TAKE_INSTRUCT_MEMBERS) {
                mSelectAcceptor = (ArrayList<MessageHolder>) data.getSerializableExtra("AcceptorListSelect");
                mAcceptorCount.setText(mSelectAcceptor.size() + "人");
            }
        }
    }

    @Override
    public void onSelectFinish(FileMode fileMode, List<FileBean> fileBeans) {
        this.mFileBeans = fileBeans;
        if (fileBeans.size() < 1) {
            mShowContainer.setVisibility(View.GONE);
            mSelectImage.setVisibility(View.VISIBLE);
        } else {
            FileBean mMediaFile = fileBeans.get(0);
            String path = mMediaFile.getPath();
            mShowContainer.setVisibility(View.VISIBLE);
            mSelectImage.setVisibility(View.GONE);
            Glide.with(this).load(path).into(mShow);
        }
    }
}
