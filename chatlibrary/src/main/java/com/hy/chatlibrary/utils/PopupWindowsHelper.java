package com.hy.chatlibrary.utils;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.hrw.chatlibrary.R;
import com.hy.chatlibrary.adapter.ItemControlAdapter;
import com.hy.chatlibrary.adapter.OnItemClickListener;
import com.hy.chatlibrary.bean.ControlTypeBean;
import com.hy.chatlibrary.db.entity.ChatMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/05/08 16:10
 * @desc:
 */
public class PopupWindowsHelper {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void showItemControlWindows(View view, ChatMessage chatMessage, OnItemClickListener<ControlTypeBean> mOnItemClickListener) {
        boolean openCopy = chatMessage.getItemType() == 0;
        boolean openTranslate = chatMessage.getItemType() == 1;
        boolean openInstruct = chatMessage.getItemType() == 6;
        boolean isCallBack = chatMessage.getMessageOwner()==0;

        List<ControlTypeBean> controlTypeBeans = new ArrayList<>();
        controlTypeBeans.add(new ControlTypeBean(0, "复制", openCopy));
        controlTypeBeans.add(new ControlTypeBean(1, "引用", true));
        controlTypeBeans.add(new ControlTypeBean(2, "回复", openInstruct));//用于指令
        controlTypeBeans.add(new ControlTypeBean(3, "撤回", isCallBack));//用于撤回，一定时间内发出的消息可以进行撤回
        controlTypeBeans.add(new ControlTypeBean(4, "翻译", openTranslate));//用于语音,对语音进行翻译
        View itemMenu = LayoutInflater.from(view.getContext()).inflate(R.layout.popup_item_control_menu, null);
        PopupWindow window = new PopupWindow(itemMenu, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RecyclerView recyclerView = itemMenu.findViewById(R.id.show_item_control_menu);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        ItemControlAdapter baseAdapter;
        recyclerView.setAdapter(baseAdapter = new ItemControlAdapter(controlTypeBeans));
        baseAdapter.setOnItemClickListener(((view1, data) -> {
            window.dismiss();
            mOnItemClickListener.onItemClick(view1, data);
        }));
        window.setBackgroundDrawable(new DrawerArrowDrawable(view.getContext()));
        window.setOutsideTouchable(true);
        window.setFocusable(true);
        if (chatMessage.getMessageOwner() == 0) {
            window.showAsDropDown(view, -3 * 160, 0, Gravity.RIGHT);
        } else {
            window.showAsDropDown(view, 0, 0, Gravity.LEFT);
        }
    }
}
