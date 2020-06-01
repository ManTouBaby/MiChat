package com.hy.chatlibrary.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.ViewGroup;

import com.hy.chatlibrary.base.SmartVH;

/**
 * @author:MtBaby
 * @date:2020/06/01 15:18
 * @desc:
 */
public class HistoryShowAdapter extends RecyclerView.Adapter<SmartVH> {
    SparseIntArray sparseIntArray = new SparseIntArray();
    public HistoryShowAdapter() {
        // 0:文字 1:语音 2:视屏 3:图片 4:地图 5:文件 6：指令

    }

    @Override
    public SmartVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(SmartVH holder, int position) {

    }


    @Override
    public int getItemCount() {
        return 0;
    }
}
