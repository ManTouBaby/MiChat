package com.hy.chatlibrary.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hrw.chatlibrary.R;
import com.hy.chatlibrary.base.SmartVH;
import com.hy.chatlibrary.bean.ControlTypeBean;

import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/05/05 17:23
 * @desc:
 */
public abstract class ControlTypeAdapter extends RecyclerView.Adapter<SmartVH> {
    private List<ControlTypeBean> controlTypeBeans;

    public ControlTypeAdapter(List<ControlTypeBean> controlTypeBeans) {
        this.controlTypeBeans = controlTypeBeans;
    }

    @Override
    public SmartVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item_control_show, null);
        return new SmartVH(view);
    }

    @Override
    public void onBindViewHolder(SmartVH holder, int position) {
        ControlTypeBean controlType = controlTypeBeans.get(position);
        holder.getText(R.id.control_title).setText(controlType.getControlTypeName());
        holder.getItemView().setOnClickListener(v -> {
            onItemClick(v, controlType);
        });
    }

    protected abstract void onItemClick(View v, ControlTypeBean controlType);


    @Override
    public int getItemCount() {
        return controlTypeBeans.size();
    }
}
