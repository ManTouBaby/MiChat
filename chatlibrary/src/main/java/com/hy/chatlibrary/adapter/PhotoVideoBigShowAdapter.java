package com.hy.chatlibrary.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hrw.chatlibrary.R;
import com.hy.chatlibrary.base.SmartVH;
import com.hy.chatlibrary.db.ChatMessage;
import com.hy.chatlibrary.utils.StringUtil;
import com.hy.chatlibrary.utils.glide.GlideHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

/**
 * @author:MtBaby
 * @date:2020/04/09 11:04
 * @desc:
 */
public class PhotoVideoBigShowAdapter extends RecyclerView.Adapter<SmartVH> {
    private RecyclerView mRecyclerView;
    private List<ChatMessage> mChatMessages = new ArrayList<>();
    private SparseIntArray mSparseIntArray = new SparseIntArray();
    private OnBigShowItemClickListener mOnBigShowItemClickListener;

    public PhotoVideoBigShowAdapter() {
        mSparseIntArray.put(2, R.layout.mi_big_video);
        mSparseIntArray.put(3, R.layout.mi_big_photo);
    }

    @Override
    public int getItemViewType(int position) {
        return mChatMessages.get(position).getItemType();
    }

    @Override
    public SmartVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mSparseIntArray.get(viewType), null);
        return new SmartVH(view);
    }

    @Override
    public void onBindViewHolder(SmartVH holder, int position) {
        final ChatMessage chatMessage = mChatMessages.get(position);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        holder.itemView.setLayoutParams(layoutParams);
//        String filePath;
//        if (!chatMessage.isExistFile()) {
//            filePath = chatMessage.getMessageLocalContent();
//            File file = new File(StringUtil.isEmpty(filePath));
//            if (!file.exists()) filePath = chatMessage.getMessageContent();
//        } else {
//            filePath = chatMessage.getMessageContent();
//        }
        String filePath = chatMessage.getMessageLocalContent();
        File file = new File(StringUtil.isEmpty(filePath));
        if (!file.exists()) {
            filePath = chatMessage.getMessageContent();
        }

        if (chatMessage.getItemType() == 2) {//视屏
            System.out.println("文件路径:" + filePath);
            JzvdStd jzvdStd = holder.getViewById(R.id.mi_video_play);
            jzvdStd.fullscreenButton.setVisibility(View.GONE);
            jzvdStd.setUp(filePath, "", Jzvd.SCREEN_NORMAL);
            jzvdStd.posterImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            GlideHelper.loadIntoUseFitWidth(holder.itemView.getContext(), filePath, jzvdStd.posterImageView);
        }

        if (chatMessage.getItemType() == 3) {//图片0
            GlideHelper.loadIntoUseNoCorner(holder.itemView.getContext(), filePath, holder.getImage(R.id.mi_big_pic));
        }

//        holder.itemView.setOnClickListener(v -> {
//            if (mOnBigShowItemClickListener != null) {
//                mOnBigShowItemClickListener.onBigItemClick(chatMessage);
//            }
//        });
    }

    public void showPosition(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.scrollToPosition(position);
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                Jzvd jzvd = view.findViewById(R.id.mi_video_play);
                if (jzvd != null && Jzvd.CURRENT_JZVD != null &&
                        jzvd.jzDataSource.containsTheUrl(Jzvd.CURRENT_JZVD.jzDataSource.getCurrentUrl())) {
                    if (Jzvd.CURRENT_JZVD != null && Jzvd.CURRENT_JZVD.screen != Jzvd.SCREEN_FULLSCREEN) {
                        Jzvd.releaseAllVideos();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChatMessages.size();
    }

    public void setChatMessages(List<ChatMessage> mChatMessages) {
        this.mChatMessages = mChatMessages;
        notifyDataSetChanged();
    }

    public List<ChatMessage> getChatMessages() {
        return mChatMessages;
    }

    public void setOnBigShowItemClickListener(OnBigShowItemClickListener mOnBigShowItemClickListener) {
        this.mOnBigShowItemClickListener = mOnBigShowItemClickListener;
    }
}
