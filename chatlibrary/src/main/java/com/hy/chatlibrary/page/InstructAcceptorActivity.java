package com.hy.chatlibrary.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hrw.chatlibrary.R;
import com.hy.chatlibrary.adapter.BaseAdapter;
import com.hy.chatlibrary.adapter.OnItemClickListener;
import com.hy.chatlibrary.base.SmartVH;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.widget.CornerTextView;

import java.util.ArrayList;
import java.util.List;

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

        Intent intent = getIntent();
        ArrayList<MessageHolder> AcceptorList = (ArrayList<MessageHolder>) intent.getSerializableExtra("AcceptorList");
        ArrayList<MessageHolder> AcceptorListSelect = (ArrayList<MessageHolder>) intent.getSerializableExtra("AcceptorListSelect");
        for (MessageHolder messageSelect : AcceptorListSelect) {
            for (MessageHolder message : AcceptorList) {
                if (message.getId().equals(messageSelect.getId())) {
                    message.setSelect(true);
                    break;
                }
            }
        }
        findViewById(R.id.mi_iv_back).setOnClickListener(v -> goBack());
        RecyclerView recyclerView = findViewById(R.id.mi_show_instruct_select);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        MemberSelect baseAdapter;
        recyclerView.setAdapter(baseAdapter = new MemberSelect(AcceptorList, R.layout.item_chat_group_member));
        baseAdapter.setOnItemClickListener((OnItemClickListener<MessageHolder>) (view, data) -> {
            data.setSelect(!data.isSelect());
            baseAdapter.updateMessage(data);
        });

        findViewById(R.id.mi_select_complete).setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("AcceptorListSelect", baseAdapter.getSelectMember());
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    class MemberSelect extends BaseAdapter<MessageHolder> {
        public MemberSelect(List<MessageHolder> mDates, int mResLayout) {
            super(mDates, mResLayout);
        }

        @Override
        protected void onBindView(SmartVH holder, MessageHolder data, int position) {
            holder.getImage(R.id.member_select_tag).setVisibility(data.isSelect() ? View.VISIBLE : View.GONE);
            CornerTextView image = holder.getViewById(R.id.mi_tag_por);
//            GlideHelper.loadIntoUseNoCorner(InstructAcceptorActivity.this, data.getPortrait(), image);
            image.setText(data.getName());
            holder.getText(R.id.mi_member_name).setText(data.getName());
        }

        public void updateMessage(MessageHolder data) {
            int position = -1;
            for (int i = 0; i < mDates.size(); i++) {
                MessageHolder messageHolder = mDates.get(i);
                if (data.getId().equals(messageHolder.getId())) {
                    position = i;
                    break;
                }
            }
            if (position > -1) notifyItemChanged(position);
        }

        public ArrayList<MessageHolder> getSelectMember() {
            ArrayList<MessageHolder> selectMessageHolders = new ArrayList<>();
            for (MessageHolder messageHolder : mDates) {
                if (messageHolder.isSelect()) selectMessageHolders.add(messageHolder);
            }
            return selectMessageHolders;
        }
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
