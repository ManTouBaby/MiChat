package com.hy.chatlibrary.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hrw.chatlibrary.R;
import com.hy.chatlibrary.base.SmartVH;
import com.hy.chatlibrary.bean.ChatGroupMembers;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.widget.CornerTextView;

import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/05/25 10:03
 * @desc:
 */
public class ChatGroupMemberAdapter extends BaseAdapter<ChatGroupMembers> {


    public ChatGroupMemberAdapter(List<ChatGroupMembers> mDates) {
        super(mDates, R.layout.item_chat_group_member);
    }

    @Override
    public int getItemViewType(int position) {
        return mDates.get(position).getItemType();
    }

    public void addData(int position, ChatGroupMembers groupMembers) {
        mDates.add(position, groupMembers);
        notifyItemInserted(position);
    }

    @Override
    public SmartVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_group_member, null);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_group_add_member, null);
        }
        return new SmartVH(view);
    }

    @Override
    protected void onBindView(SmartVH holder, ChatGroupMembers data, int position) {
        MessageHolder messageHolder = data.getMessageHolder();
        if (data.getItemType() == 1) {
            CornerTextView cornerTextView = holder.getViewById(R.id.mi_tag_por);
            TextView textView = holder.getText(R.id.mi_member_name);
            cornerTextView.setText(messageHolder.getName());
            textView.setText(messageHolder.getGroupName());
            addChildViewClick(cornerTextView, data);
        } else {
            ImageView addMember = holder.getImage(R.id.mi_tag_add);
            addChildViewClick(addMember, data);
        }
    }
}
