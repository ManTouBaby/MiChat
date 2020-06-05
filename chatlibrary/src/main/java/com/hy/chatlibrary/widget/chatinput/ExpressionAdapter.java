package com.hy.chatlibrary.widget.chatinput;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hrw.chatlibrary.R;
import com.hy.chatlibrary.adapter.OnItemClickListener;
import com.hy.chatlibrary.base.SmartVH;
import com.hy.chatlibrary.widget.chatinput.expression.ExpressionBO;
import com.hy.chatlibrary.widget.chatinput.expression.ExpressionData;

import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/06/02 11:10
 * @desc:
 */
public class ExpressionAdapter extends RecyclerView.Adapter<SmartVH> {
    private List<ExpressionBO> expressionBOS;

    public ExpressionAdapter() {
        this.expressionBOS = ExpressionData.getExpressions();
    }

    @Override
    public SmartVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expression_show, null);
        return new SmartVH(view);
    }

    @Override
    public void onBindViewHolder(SmartVH holder, int position) {
        ExpressionBO expressionBO = expressionBOS.get(position);
        holder.getText(R.id.mi_expression_name).setText(expressionBO.getExpressionName());
        holder.getImage(R.id.iv_expression_tag).setImageResource(expressionBO.getExpressionRes());
        holder.itemView.setOnClickListener(v->{
            if (mOnItemClickListener!=null){
                mOnItemClickListener.onItemClick(holder.itemView,expressionBO);
            }
        });

    }

    @Override
    public int getItemCount() {
        return expressionBOS.size();
    }
    private OnItemClickListener<ExpressionBO> mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener<ExpressionBO> mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }


}
