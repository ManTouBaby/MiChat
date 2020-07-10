package com.hy.chatlibrary.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hy.chatlibrary.R;
import com.hy.chatlibrary.base.SmartVH;
import com.hy.chatlibrary.bean.ControlTypeBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/05/07 17:29
 * @desc:
 */
public class ItemControlAdapter extends RecyclerView.Adapter<SmartVH> {
    OnItemClickListener<ControlTypeBean> mOnItemClickListener;
    private List<ControlTypeBean> controlTypeBeans = new ArrayList<>();

    public ItemControlAdapter(List<ControlTypeBean> controlTypeBeans) {
        for (ControlTypeBean typeBean : controlTypeBeans) {
            if (typeBean.isShow()) {
                this.controlTypeBeans.add(typeBean);
            }
        }
    }

    @Override
    public SmartVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item_control_show, null);
        return new SmartVH(view);
    }

    @Override
    public void onBindViewHolder(SmartVH holder, int position) {
        holder.getViewById(R.id.v_line).setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        ControlTypeBean data = controlTypeBeans.get(position);
        holder.getText(R.id.control_title).setText(data.getControlTypeName());
        holder.getItemView().setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return controlTypeBeans.size();
    }

    public void setOnItemClickListener(OnItemClickListener<ControlTypeBean> mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
