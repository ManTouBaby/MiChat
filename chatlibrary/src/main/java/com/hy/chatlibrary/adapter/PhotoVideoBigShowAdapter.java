package com.hy.chatlibrary.adapter;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.hrw.chatlibrary.R;
import com.hy.chatlibrary.base.SmartVH;
import com.hy.chatlibrary.db.entity.ChatMessage;
import com.hy.chatlibrary.db.entity.InstructBean;
import com.hy.chatlibrary.utils.StringUtil;
import com.hy.chatlibrary.utils.glide.GlideHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    Map<Integer, Jzvd> stringJzvdMap = new HashMap<>();

    public PhotoVideoBigShowAdapter() {
        mSparseIntArray.put(2, R.layout.mi_big_video);
        mSparseIntArray.put(3, R.layout.mi_big_photo);
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = mChatMessages.get(position);
        if (chatMessage.getItemType() == 6) {
            InstructBean instructBean = chatMessage.getInstructBean();
            if (instructBean.getDuration() > 0) {
                return 2;
            } else {
                return 3;
            }
        } else {
            return chatMessage.getItemType();
        }
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

        if (chatMessage.getItemType() == 6) {
            InstructBean instructBean = chatMessage.getInstructBean();
            String filePath = instructBean.getLocalFilePath();
            File file = new File(StringUtil.isEmpty(filePath));
            if (!file.exists()) {
                filePath = TextUtils.isEmpty(instructBean.getNetThumbFilePath()) ? instructBean.getNetFilePath() : instructBean.getNetThumbFilePath();
            }
            if (instructBean.getDuration() > 0) {
                showVideo(holder, filePath, instructBean.getNetFilePath());
            } else {
                showImage(holder, filePath);
            }
        } else {
            String filePath = chatMessage.getMessageLocalPath();
            File file = new File(StringUtil.isEmpty(filePath));
            if (!file.exists()) {
                filePath = TextUtils.isEmpty(chatMessage.getMessageThumbFilePath()) ? chatMessage.getMessageNetPath() : chatMessage.getMessageThumbFilePath();
            }
            if (chatMessage.getItemType() == 2) {//视屏
                showVideo(holder, filePath, chatMessage.getMessageNetPath());
            }
            if (chatMessage.getItemType() == 3) {//图片0
                showImage(holder, filePath);
            }
        }
    }

    private void showVideo(SmartVH holder, String firstPage, String videoUrl) {
        JzvdStd jzvdStd = holder.getViewById(R.id.mi_video_play);
        jzvdStd.fullscreenButton.setVisibility(View.GONE);
        jzvdStd.setUp(videoUrl, "", Jzvd.SCREEN_NORMAL);
        jzvdStd.posterImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        GlideHelper.loadIntoUseNoCorner(holder.itemView.getContext(), firstPage, jzvdStd.posterImageView);
        if (!stringJzvdMap.containsKey(jzvdStd.hashCode()))
            stringJzvdMap.put(jzvdStd.hashCode(), jzvdStd);
    }

    private void showImage(SmartVH holder, String imageUrl) {
        SubsamplingScaleImageView scaleImageView = holder.getViewById(R.id.mi_big_pic);
//            scaleImageView.setImage(ImageSource.uri(filePath));
        GlideHelper.loadIntoImage(holder.itemView.getContext(), imageUrl, new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) resource;
                scaleImageView.setImage(ImageSource.bitmap(bitmapDrawable.getBitmap()));
            }
        });
    }

    public void showPosition(ChatMessage chatMessage) {
        int clickPosition = -1;
        for (int i = 0; i < mChatMessages.size(); i++) {
            ChatMessage message = mChatMessages.get(i);
            if (message.getMessageId().equals(chatMessage.getMessageId())) {
                clickPosition = i;
            }
        }

        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        if (clickPosition != -1) layoutManager.scrollToPosition(clickPosition);
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
                if (jzvd != null && Jzvd.CURRENT_JZVD != null && jzvd.jzDataSource.containsTheUrl(Jzvd.CURRENT_JZVD.jzDataSource.getCurrentUrl())) {
                    if (Jzvd.CURRENT_JZVD != null && Jzvd.CURRENT_JZVD.screen != Jzvd.SCREEN_FULLSCREEN) {
                        Jzvd.releaseAllVideos();
                    }
                }
            }
        });
    }

    public void onDestroy() {
        for (Map.Entry<Integer, Jzvd> jzvdEntry : stringJzvdMap.entrySet()) {
            Jzvd jzvd = jzvdEntry.getValue();
            if (jzvd != null && Jzvd.CURRENT_JZVD != null && jzvd.jzDataSource.containsTheUrl(Jzvd.CURRENT_JZVD.jzDataSource.getCurrentUrl())) {
                if (Jzvd.CURRENT_JZVD != null && Jzvd.CURRENT_JZVD.screen != Jzvd.SCREEN_FULLSCREEN) {
                    Jzvd.releaseAllVideos();
                }
            }
        }
        stringJzvdMap.clear();
    }

    @Override
    public int getItemCount() {
        return mChatMessages.size();
    }

    public void setChatMessages(List<ChatMessage> mChatMessages) {
        List<ChatMessage> messages = new ArrayList<>();
        for (ChatMessage chatMessage : mChatMessages) {
            if (chatMessage.getItemType() == 6) {
                InstructBean instructBean = chatMessage.getInstructBean();
                if (!TextUtils.isEmpty(instructBean.getLocalFilePath()) || !TextUtils.isEmpty(instructBean.getNetFilePath())) {
                    messages.add(chatMessage);
                }
            } else if (chatMessage.getItemType() == 2 || chatMessage.getItemType() == 3) {
                messages.add(chatMessage);
            }
        }
        this.mChatMessages = messages;
        notifyDataSetChanged();
    }

    public List<ChatMessage> getChatMessages() {
        return mChatMessages;
    }

    public void setOnBigShowItemClickListener(OnBigShowItemClickListener mOnBigShowItemClickListener) {
        this.mOnBigShowItemClickListener = mOnBigShowItemClickListener;
    }
}
