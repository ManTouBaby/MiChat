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
import com.hrw.chatlibrary.R;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.db.entity.InstructBean;
import com.hy.chatlibrary.utils.StatusBarUtil;
import com.hy.chatlibrary.utils.glide.GlideLoader;
import com.mt.filepicker.ImagePicker;
import com.mt.filepicker.data.MediaFile;

import java.io.File;
import java.util.ArrayList;

import static com.hy.chatlibrary.base.ResultCode.REQUEST_TAKE_INSTRUCT_MEMBERS;
import static com.hy.chatlibrary.base.ResultCode.REQUEST_TAKE_INSTRUCT_MODEL;
import static com.hy.chatlibrary.base.ResultCode.REQUEST_TAKE_PHOTO_VIDEO;

/**
 * @author:MtBaby
 * @date:2020/04/29 9:46
 * @desc:
 */
public class InstructEditActivity extends AppCompatActivity {
    private ImageView mShow;
    private ImageView mSelectImage;
    private TextView mAcceptorCount;
    private EditText mInstructTitle;
    private EditText mInstructContent;
    private RelativeLayout mShowContainer;

    private ArrayList<MediaFile> mMediaFiles;
    private InstructBean mInstructBean;
    private ArrayList<MessageHolder> mSelectAcceptor = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatueColor(this, R.color.mi_chat_main_bg, true);
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
            mMediaFiles.clear();
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

            if (mMediaFiles != null && mMediaFiles.size() > 0) {
                MediaFile mediaFile = mMediaFiles.get(0);
                mInstructBean.setLocalFilePath(mediaFile.getPath());
                mInstructBean.setNetFilePath(mediaFile.getPath());
                mInstructBean.setDuration(mediaFile.getDuration());
                mInstructBean.setFileName(mediaFile.getFolderName());
                mInstructBean.setFileSize(new File(mediaFile.getPath()).length());
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
            ImagePicker.getInstance()
                    .setTitle("图片视屏")//设置标题
                    .showCamera(true)//设置是否显示拍照按钮
                    .showImage(true)//设置是否展示图片
                    .showVideo(true)//设置是否展示视频
                    .setSingleType(false)//设置图片视频不能同时选择
                    .setMaxCount(1)//设置最大选择图片数目(默认为1，单选)
                    .setImagePaths(mMediaFiles)//保存上一次选择图片的状态，如果不需要可以忽略
                    .setImageLoader(new GlideLoader())//设置自定义图片加载器
                    .start(this, REQUEST_TAKE_PHOTO_VIDEO);//REQEST_SELECT_IMAGES_CODE为Intent调用的requestCode
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
            if (requestCode == REQUEST_TAKE_PHOTO_VIDEO) {
                mMediaFiles = data.getParcelableArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
                if (mMediaFiles.size() < 1) {
                    mShowContainer.setVisibility(View.GONE);
                    mSelectImage.setVisibility(View.VISIBLE);
                } else {
                    MediaFile mMediaFile = mMediaFiles.get(0);
                    String path = mMediaFile.getPath();
                    mShowContainer.setVisibility(View.VISIBLE);
                    mSelectImage.setVisibility(View.GONE);
                    Glide.with(this).load(path).into(mShow);
                }
            }
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
}
