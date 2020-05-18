package com.hy.chatlibrary.adapter;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hrw.chatlibrary.R;
import com.hy.chatlibrary.base.SmartVH;
import com.hy.chatlibrary.db.entity.InstructBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/04/29 18:28
 * @desc:
 */
public class InstructModelAdapter extends RecyclerView.Adapter<SmartVH> {
    private List<InstructBean> instructBeans = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private InstructBean mInstructBean;

    @Override
    public SmartVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_instruct_model, null);
        return new SmartVH(view);
    }

    @Override
    public void onBindViewHolder(SmartVH holder, int position) {
        InstructBean instructBean = instructBeans.get(position);
        LinearLayout linearLayout = holder.getViewById(R.id.ll_instruct_model_item_edit);
        TextView title = holder.getText(R.id.mi_title);
        View itemView = holder.getItemView();
        title.setText(instructBean.getTitle());
        holder.getText(R.id.mi_content).setText(instructBean.getContent());
        if (mInstructBean != null && mInstructBean.getId().equals(instructBean.getId())) {
            itemView.setSelected(true);
        } else {
            itemView.setSelected(false);
        }
        linearLayout.setVisibility(instructBean.isOpenControl() ? View.VISIBLE : View.GONE);
        itemView.setOnLongClickListener(v -> {
            instructBean.setOpenControl(!instructBean.isOpenControl());
            if (instructBean.isOpenControl()) {
                startAnimation(linearLayout);
            } else {
                stopAnimation(linearLayout);
            }
            return true;
        });
        itemView.setOnClickListener(v -> {
            if (mInstructBean != null && mInstructBean.getId().equals(instructBean.getId()) && itemView.isSelected()) {
                mInstructBean = null;
                itemView.setSelected(false);
            } else {
                mInstructBean = instructBean;
                itemView.setSelected(true);
                notifyDataSetChanged();
            }

            if (mOnInstructItemControlListener != null) {
                mOnInstructItemControlListener.onInstructItemSelect(mInstructBean);
            }
        });
        holder.getButton(R.id.mi_instruct_model_item_delete).setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext()).setTitle("温馨提示")
                    .setMessage("确定删除当前指令模板？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        if (mOnInstructItemControlListener != null) {
                            mOnInstructItemControlListener.onInstructItemRemove(instructBean);
                        }
                    })
                    .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                    .show();

        });
        holder.getButton(R.id.mi_instruct_model_item_edit).setOnClickListener(v -> {
            if (mOnInstructItemControlListener != null) {
                mOnInstructItemControlListener.onInstructItemEdit(instructBean);
            }
        });
    }

    private void startAnimation(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation mHiddenAction = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0f);
        mHiddenAction.setDuration(500);
        view.setAnimation(mHiddenAction);
    }

    private void stopAnimation(View view) {
        view.setVisibility(View.GONE);
        TranslateAnimation mHiddenAction = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(500);
        view.setAnimation(mHiddenAction);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mLinearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
    }

    public void setInstructBeans(List<InstructBean> instructBeans) {
        this.instructBeans = instructBeans;
        notifyDataSetChanged();
    }

    public void addInstructBean(InstructBean instructBean) {
        this.instructBeans.add(0, instructBean);
        notifyItemInserted(0);
        mLinearLayoutManager.scrollToPosition(0);
    }

    public void updateInstructBean(InstructBean instructBean) {
        int updatePosition = -1;
        for (int i = 0; i < instructBeans.size(); i++) {
            InstructBean bean = instructBeans.get(i);
            if (bean.getId().equals(instructBean.getId())) {
                updatePosition = i;
                bean.setTitle(instructBean.getTitle());
                bean.setContent(instructBean.getContent());
                break;
            }
        }
        notifyItemChanged(updatePosition);
    }

    public void removeInstructBean(InstructBean instructBean) {
        if (mInstructBean != null && instructBean.getId().equals(mInstructBean.getId())) {
            mInstructBean = null;
            if (mOnInstructItemControlListener != null) {
                mOnInstructItemControlListener.onInstructItemSelect(mInstructBean);
            }
        }
        int removePosition = -1;
        for (int i = 0; i < instructBeans.size(); i++) {
            InstructBean bean = instructBeans.get(i);
            if (bean.getId().equals(instructBean.getId())) {
                removePosition = i;
                break;
            }
        }
        this.instructBeans.remove(instructBean);
        notifyItemRemoved(removePosition);
    }

    @Override
    public int getItemCount() {
        return instructBeans.size();
    }

    private OnInstructItemControlListener mOnInstructItemControlListener;

    public void setOnInstructItemControlListener(OnInstructItemControlListener mOnInstructItemControlListener) {
        this.mOnInstructItemControlListener = mOnInstructItemControlListener;
    }

    public interface OnInstructItemControlListener {
        void onInstructItemEdit(InstructBean instructBean);

        void onInstructItemSelect(InstructBean instructBean);

        void onInstructItemRemove(InstructBean instructBean);
    }
}
