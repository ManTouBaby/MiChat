package com.hy.chatlibrary.widget.chatinput;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hrw.chatlibrary.R;
import com.hy.chatlibrary.base.SmartVH;
import com.hy.chatlibrary.bean.ChatContentType;

import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/04/01 16:36
 * @desc:
 */
public class ChatContentTypeAdapter extends RecyclerView.Adapter<SmartVH> {
    private List<ChatContentType> contentTypes;
    private OnContentTypeClickListener mOnContentTypeClickListener;

    ChatContentTypeAdapter(List<ChatContentType> contentTypes) {
        this.contentTypes = contentTypes;
    }

    @Override
    public SmartVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mi_item_chat_content_type, null);
        return new SmartVH(view);
    }

    @Override
    public void onBindViewHolder(SmartVH holder, int position) {
        final ChatContentType chatContentType = contentTypes.get(position);
        ImageView image = holder.getImage(R.id.mi_content_type_tag);
        TextView text = holder.getText(R.id.mi_content_type_name);
        image.setImageResource(chatContentType.getTypeIcon());
        text.setText(chatContentType.getTypeName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnContentTypeClickListener != null) {
                    mOnContentTypeClickListener.onContentTypeClick(chatContentType.getTypeId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contentTypes.size();
    }

    public void setmOnContentTypeClickListener(OnContentTypeClickListener onContentTypeClickListener) {
        this.mOnContentTypeClickListener = onContentTypeClickListener;
    }
}
