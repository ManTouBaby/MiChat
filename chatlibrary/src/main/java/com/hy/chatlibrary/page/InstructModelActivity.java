package com.hy.chatlibrary.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hrw.chatlibrary.R;
import com.hy.chatlibrary.adapter.InstructModelAdapter;
import com.hy.chatlibrary.db.DBHelper;
import com.hy.chatlibrary.db.entity.InstructBean;
import com.hy.chatlibrary.db.dao.InstructDAO;
import com.hy.chatlibrary.utils.DateUtil;
import com.hy.chatlibrary.utils.StatusBarUtil;

import java.util.List;

import static com.hy.chatlibrary.base.ResultCode.REQUEST_TAKE_INSTRUCT_NEW_MODEL;

/**
 * @author:MtBaby
 * @date:2020/04/29 9:48
 * @desc:
 */
public class InstructModelActivity extends AppCompatActivity {
    LinearLayout mMenuContainer;
    RecyclerView mInstructShow;
    Button mSelectComplete;
    TextView mTag;
    private InstructDAO mInstructDAO;
    InstructModelAdapter mInstructModelAdapter;
    private List<InstructBean> mInstructBeans;
    private InstructBean mInstructBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatueColor(this, R.color.mi_white_bg, true);
        setContentView(R.layout.mi_activity_instruct_model);
        mMenuContainer = findViewById(R.id.ll_menu_container);
        mInstructShow = findViewById(R.id.mi_show_instruct_model);
        mSelectComplete = findViewById(R.id.mi_select_complete);
        mTag = findViewById(R.id.mi_tag);
        mInstructDAO = DBHelper.getInstance(this).getInstructDAO();
        findViewById(R.id.mi_iv_back).setOnClickListener(v -> finish());
        findViewById(R.id.mi_new_model).setOnClickListener(v -> {
            Intent intent = new Intent(this, InstructModelEditActivity.class);
            startActivityForResult(intent, REQUEST_TAKE_INSTRUCT_NEW_MODEL);
        });
        mSelectComplete.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("InstructItem", mInstructBean);
            setResult(RESULT_OK, intent);
            finish();
        });
        mInstructShow.setLayoutManager(new LinearLayoutManager(this));
        mInstructShow.setHasFixedSize(true);
        mInstructShow.setAdapter(mInstructModelAdapter = new InstructModelAdapter());
        mInstructBeans = mInstructDAO.queryAllInstruct();
        mInstructModelAdapter.setInstructBeans(mInstructBeans);
        mInstructModelAdapter.setOnInstructItemControlListener(new InstructModelAdapter.OnInstructItemControlListener() {
            @Override
            public void onInstructItemEdit(InstructBean instructBean) {
                Intent intent = new Intent(InstructModelActivity.this, InstructModelEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("instructId", instructBean.getId());
                bundle.putString("instructTitle", instructBean.getTitle());
                bundle.putString("instructContent", instructBean.getContent());
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_TAKE_INSTRUCT_NEW_MODEL);
            }

            @Override
            public void onInstructItemSelect(InstructBean instructBean) {
                if (instructBean != null) {
                    mInstructBean = instructBean;
                    startAnimation();
                } else {
                    stopAnimation();
                }
            }

            @Override
            public void onInstructItemRemove(InstructBean instructBean) {//C9840A23-D756-4618-B1C2-30350AE5301C
                mInstructDAO.deleteInstruct(instructBean);
                mInstructModelAdapter.removeInstructBean(instructBean);
            }
        });
    }

    private void startAnimation() {
        mTag.setVisibility(View.VISIBLE);
        mSelectComplete.setVisibility(View.VISIBLE);
        TranslateAnimation mHiddenAction = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0f);
        mHiddenAction.setDuration(500);

        mMenuContainer.setAnimation(mHiddenAction);
    }

    private void stopAnimation() {
        mTag.setVisibility(View.GONE);
        TranslateAnimation mHiddenAction = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(500);
        mHiddenAction.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSelectComplete.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mMenuContainer.setAnimation(mHiddenAction);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_TAKE_INSTRUCT_NEW_MODEL) {
            String instructTitle = data.getStringExtra("instructTitle");
            String instructContent = data.getStringExtra("instructContent");
            String instructId = data.getStringExtra("instructId");
            InstructBean instructBean = createInstructBean(instructId, instructTitle, instructContent);

            boolean isExist = false;
            for (int i = 0; i < mInstructBeans.size(); i++) {
                InstructBean bean = mInstructBeans.get(i);
                if (instructId.equals(bean.getId())) {
                    isExist = true;
                    break;
                }
            }
            if (isExist) {
                mInstructDAO.updateInstruct(instructBean);
                mInstructModelAdapter.updateInstructBean(instructBean);
            } else {
                mInstructDAO.insertChatMessage(instructBean);
                mInstructModelAdapter.addInstructBean(instructBean);
            }

        }
    }

    private InstructBean createInstructBean(String id, String title, String content) {
        InstructBean instructBean = new InstructBean();
        instructBean.setContent(content);
        instructBean.setTitle(title);
        instructBean.setId(id);
        instructBean.setCreateMillis(DateUtil.getSystemTimeMilli());
        instructBean.setCreateStr(DateUtil.getSystemTime());
        return instructBean;
    }


}
