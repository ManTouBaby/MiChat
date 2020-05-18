package com.hy.chatlibrary.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hy.chatlibrary.base.SmartVH;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/05/07 11:07
 * @desc:
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<SmartVH> {
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private OnItemChildClickListener mOnItemChildClickListener;
    private OnItemChildLongClickListener mOnItemChildLongClickListener;

    protected List<T> mDates = new ArrayList<>();
    private int mResLayout;




    public BaseAdapter(int mResLayout) {
        this(new ArrayList<>(), mResLayout);
    }

    public BaseAdapter(List<T> mDates, int mResLayout) {
        this.mDates = mDates;
        this.mResLayout = mResLayout;
    }

    @Override
    public SmartVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mResLayout, null);
        return new SmartVH(view);
    }

    public void setDates(List<T> mDates) {
        this.mDates = mDates;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    public void setOnItemChildClickListener(OnItemChildClickListener<T> mOnItemChildClickListener) {
        this.mOnItemChildClickListener = mOnItemChildClickListener;
    }

    public void setOnItemChildLongClickListener(OnItemChildLongClickListener mOnItemChildLongClickListener) {
        this.mOnItemChildLongClickListener = mOnItemChildLongClickListener;
    }

    void addChildViewClick(View view, final T data) {
        view.setOnClickListener(v -> {
            if (mOnItemChildClickListener != null) {
                mOnItemChildClickListener.onItemChildClick(v, data);
            }
        });
    }

    void addChildViewLongClick(View view, final T data) {
        view.setOnLongClickListener(v -> {
            if (mOnItemChildLongClickListener != null) {
                mOnItemChildLongClickListener.onItemChildLongClick(v, data);
            }
            return true;
        });
    }

    @Override
    public void onBindViewHolder(SmartVH holder, int position) {
        if (mDates != null && mDates.size() > 0) {
            T data = mDates.get(position);
            onBindView(holder, data, position);

            holder.getItemView().setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, data);
                }
            });
            holder.getItemView().setOnLongClickListener(v -> {
                if (mOnItemLongClickListener != null) {
                    mOnItemLongClickListener.onItemLongClick(v, data);
                }
                return true;
            });
        }
    }



    protected abstract void onBindView(SmartVH holder, T data, int position);

    @Override
    public int getItemCount() {
        return mDates == null ? 0 : mDates.size();
    }
}
