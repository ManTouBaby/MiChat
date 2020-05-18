package com.hy.chatlibrary.adapter;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hrw.chatlibrary.R;
import com.hy.chatlibrary.base.SmartVH;
import com.hy.chatlibrary.db.entity.ChatMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/04/05 17:45
 * @desc:
 */
public abstract class BaseChatAdapter extends RecyclerView.Adapter<SmartVH> implements OnDateChangeListener {
    private SparseIntArray itemTypes;
    List<ChatMessage> mChatMessages = new ArrayList<>();
    private OnSendFailTagClickListener mOnSendFailTagClickListener;
    private OnItemChildClickListener mOnChatItemChildClickListener;
    private OnItemChildLongClickListener mOnChatItemChildLongClickListener;
    private OnPullDownLoadMoreListener mOnPullDownLoadMoreListener;
    private boolean onPullDowning;//是否正在下拉加载
    private boolean onLoadMoreComplete;//是否下拉完成

    public BaseChatAdapter() {
        itemTypes = getSparseIntArray();
    }

    protected abstract SparseIntArray getSparseIntArray();

    private void addHeaderRefresh(@LayoutRes int resLayout) {
        if (mChatMessages.size() > 0 && "-1".equals(mChatMessages.get(0).getMessageId())) return;
        itemTypes.put(-1, resLayout);
        ChatMessage chatMessage = createLoadMoreChatMessage();
        mChatMessages.add(0, chatMessage);
    }

    @Override
    public SmartVH onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = itemTypes.get(viewType);
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, null);
        return new SmartVH(view);
    }

    @Override
    public void onBindViewHolder(SmartVH holder, int position) {
        onBindView(holder, position);
        final ChatMessage chatMessage = mChatMessages.get(position);
        View view = getSendFailTagView(holder, position);
        if (view != null) view.setOnClickListener(v -> {
            if (mOnSendFailTagClickListener != null) {
                mOnSendFailTagClickListener.onSendFailTagClick(chatMessage);
            }
        });
//        View itemView = holder.getItemView();
//        itemView.setOnLongClickListener(v -> {
//            Rect rect = new Rect();
//            //1、获取main在窗体的可视区域
//            itemView.getWindowVisibleDisplayFrame(rect);
//            System.out.println("可视化区域位置:" + rect.toString());
//            return true;
//        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (onLoadMoreComplete) return;
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstVisibleItemPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
//                System.out.println("下拉检查:"+firstVisibleItemPosition+"  是否真正下拉:"+onPullDowning);
                if (firstVisibleItemPosition == 0 && newState == RecyclerView.SCROLL_STATE_IDLE && !onPullDowning) {
                    onPullDowning = true;
                    if (mOnPullDownLoadMoreListener != null) {
//                        System.out.println("下拉加载...");
                        mOnPullDownLoadMoreListener.onLoadMore(getFirstItem());
                    }
                }
            }
        });
    }

    void addChildViewClick(View view, final ChatMessage chatMessage) {
        if (view != null) view.setOnClickListener(v -> {
            if (mOnChatItemChildClickListener != null) {
                mOnChatItemChildClickListener.onItemChildClick(v, chatMessage);
            }
        });
    }

    void addChildViewLongClick(View view, final ChatMessage chatMessage) {
        if (view != null) view.setOnLongClickListener(v -> {
            if (mOnChatItemChildLongClickListener != null) {
                mOnChatItemChildLongClickListener.onItemChildLongClick(v, chatMessage);
            }
            return true;
        });
    }

    protected abstract void onBindView(SmartVH holder, int position);

    protected abstract View getSendFailTagView(SmartVH holder, int position);

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = mChatMessages.get(position);
        if (chatMessage.getMessageOwner() == 0) {
            return chatMessage.getItemType();
        } else if (chatMessage.getMessageOwner() == 1) {
            return chatMessage.getItemType() + 100;
        } else {
            return -1;
        }
    }

    @Override
    public int getItemCount() {
        return mChatMessages.size();
    }

    public List<ChatMessage> getChatMessages() {
        return mChatMessages;
    }

    public boolean isContainer(ChatMessage chatMessage) {
        boolean isExist = false;
        for (ChatMessage message : mChatMessages) {
            if (chatMessage.getMessageId().equals(message.getMessageId())) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    //获取第一个存在的Item
    public ChatMessage getFirstItem() {
        if (mChatMessages.size() == 0) {
            return null;
        } else {
            if (mChatMessages.get(0).getItemType() == -1) {
                return mChatMessages.get(1);
            } else {
                return mChatMessages.get(0);
            }
        }
    }

    @Override
    public void addNetMessages(List<ChatMessage> chatMessages) {
        if (chatMessages.size() > 10 && !onLoadMoreComplete) {
            addHeaderRefresh(R.layout.mimo_item_pull_down);
        }
        Collections.reverse(chatMessages);
        this.mChatMessages.addAll(chatMessages);
        notifyDataSetChanged();
    }

//    public void setChatMessages(List<ChatMessage> mChatMessages) {
//        this.mChatMessages = mChatMessages;
//        notifyDataSetChanged();
//    }

    @Override
    public void addOldMessages(List<ChatMessage> chatMessages) {
        int size = chatMessages.size();
        if (size > 10 && !onLoadMoreComplete) {
            addHeaderRefresh(R.layout.mimo_item_pull_down);
        }
        Collections.reverse(chatMessages);
        mChatMessages.addAll(1, chatMessages);
        notifyItemRangeInserted(1, size);
    }

    @Override
    public void addNewMessage(ChatMessage message) {
        int size = mChatMessages.size();
        mChatMessages.add(message);
        notifyItemChanged(size);
    }

    @Override
    public void removeMessage(ChatMessage message) {
        int removePosition = -1;
        for (int i = 0; i < mChatMessages.size(); i++) {
            ChatMessage bean = mChatMessages.get(i);
            if (bean.getMessageId().equals(message.getMessageId())) {
                removePosition = i;
                break;
            }
        }
       if (removePosition!=-1){
           this.mChatMessages.remove(removePosition);
           notifyItemRemoved(removePosition);
       }
    }

    @Override
    public void updateMessage(ChatMessage message) {
        int position = -1;
        for (int i = 0; i < mChatMessages.size(); i++) {
            ChatMessage msg = mChatMessages.get(i);
            if (message.getMessageId().equals(msg.getMessageId())) {
                position = i;
                mChatMessages.remove(position);
                mChatMessages.add(position, message);
                break;
            }
        }
        if (position > -1) notifyItemChanged(position);
    }

    @Override
    public void onDestroy() {
        mChatMessages.clear();
        notifyDataSetChanged();
    }

    //下拉加载完成
    public void setLoadMoreComplete() {
        onLoadMoreComplete = true;
        if (mChatMessages.size() > 0 && "-1".equals(mChatMessages.get(0).getMessageId())) {
            mChatMessages.remove(0);
            notifyItemRemoved(0);
        }
    }

    //下拉加载成功
    public void setLoadMoreSuccess() {
        onPullDowning = false;
    }

    private ChatMessage createLoadMoreChatMessage() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageId("-1");
        chatMessage.setItemType(-1);
        chatMessage.setMessageOwner(-1);
        return chatMessage;
    }


    public void setOnSendFailTagClickListener(OnSendFailTagClickListener mOnSendFailTagClickListener) {
        this.mOnSendFailTagClickListener = mOnSendFailTagClickListener;
    }

    public void setOnItemChildClickListener(OnItemChildClickListener mOnChatItemChildClickListener) {
        this.mOnChatItemChildClickListener = mOnChatItemChildClickListener;
    }

    public void setOnPullDownLoadMoreListener(OnPullDownLoadMoreListener mOnPullDownLoadMoreListener) {
        this.mOnPullDownLoadMoreListener = mOnPullDownLoadMoreListener;
    }

    public void setOnItemChildLongClickListener(OnItemChildLongClickListener mOnChatItemChildLongClickListener) {
        this.mOnChatItemChildLongClickListener = mOnChatItemChildLongClickListener;
    }
}
