package com.hy.chatlibrary.adapter;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.hrw.chatlibrary.R;
import com.hy.chatlibrary.base.SmartVH;
import com.hy.chatlibrary.db.ChatMessage;
import com.hy.chatlibrary.db.InstructBean;
import com.hy.chatlibrary.utils.DateUtil;
import com.hy.chatlibrary.utils.MediaPlayerUtil;
import com.hy.chatlibrary.utils.StringUtil;
import com.hy.chatlibrary.utils.glide.GlideHelper;

import java.io.File;
import java.util.LinkedList;

/**
 * @author:MtBaby
 * @date:2020/03/31 17:31
 * @desc:
 */
public class ChatAdapter extends BaseChatAdapter {
    private MediaPlayerUtil mediaPlayerUtil;

    private AnimationDrawable mDrawable;
    private ChatMessage mPlayingItem;
    private LinkedList<TextureMapView> textureMapViews = new LinkedList<>();

    @Override
    protected SparseIntArray getSparseIntArray() {
        mediaPlayerUtil = new MediaPlayerUtil();
        SparseIntArray sparseIntArray = new SparseIntArray();
        sparseIntArray.put(0, R.layout.mim_item_type_txt);
        sparseIntArray.put(1, R.layout.mim_item_type_voice);
        sparseIntArray.put(2, R.layout.mim_item_type_video);
        sparseIntArray.put(3, R.layout.mim_item_type_pic);
        sparseIntArray.put(4, R.layout.mim_item_type_local);
        sparseIntArray.put(5, R.layout.mim_item_type_file);
        sparseIntArray.put(6, R.layout.mim_item_type_instruct);

        sparseIntArray.put(20, R.layout.mio_item_type_txt);
        sparseIntArray.put(21, R.layout.mio_item_type_voice);
        sparseIntArray.put(22, R.layout.mio_item_type_video);
        sparseIntArray.put(23, R.layout.mio_item_type_pic);
        sparseIntArray.put(24, R.layout.mio_item_type_local);
        sparseIntArray.put(25, R.layout.mio_item_type_file);
        sparseIntArray.put(26, R.layout.mio_item_type_instruct);
        return sparseIntArray;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindView(final SmartVH holder, final int position) {
        final ChatMessage chatMessage = mChatMessages.get(position);
        TextView text = holder.getText(R.id.mi_message_time);
        View view = holder.getViewById(R.id.mi_content_container);   //item单击监听
        if (text != null) {
            text.setVisibility(TextUtils.isEmpty(chatMessage.getMessageST()) ? View.GONE : View.VISIBLE);
            text.setText(chatMessage.getMessageST());
        }
        holder.itemView.setTag(chatMessage);
        String filePath = chatMessage.getMessageLocalContent();
        File file = new File(StringUtil.isEmpty(filePath));
        if (!file.exists()) {
            filePath = chatMessage.getMessageContent();
        }
        addChildViewLongClick(view, chatMessage);
        switch (chatMessage.getItemType()) {
            case 0:
                String content = chatMessage.getMessageContent();
                holder.getText(R.id.mi_chat_item_text).setText(StringUtil.isEmpty(content));
                break;
            case 1:
                holder.getText(R.id.mi_voice_time).setText(chatMessage.getDuration() / 1000 + "``");
                AnimationDrawable drawable = (AnimationDrawable) holder.getImage(R.id.mi_voice_animate_holder).getDrawable();
                if (chatMessage.isPlayer()) {
                    drawable.start();
                } else {
                    drawable.selectDrawable(0);
                    drawable.stop();
                }
                //处理语音
                String finalFilePath = filePath;
                view.setOnClickListener(v -> {
                    //单击语音时不管所单击的Item是不是正在播放的语音，都进行停止
                    if (chatMessage.isPlayer()) {
                        stopVoicePlay();
                    } else {
                        stopVoicePlay();
                        mDrawable = (AnimationDrawable) holder.getImage(R.id.mi_voice_animate_holder).getDrawable();
                        if (mDrawable != null) mDrawable.start();
                        mPlayingItem = chatMessage;
                        chatMessage.setPlayer(true);
                        mediaPlayerUtil.playerMedia(finalFilePath, mp -> stopVoicePlay());
                    }
                });
                break;
            case 2:
                addChildViewClick(view, chatMessage);
                holder.getText(R.id.mi_item_video_time).setText(DateUtil.long2String(chatMessage.getDuration()));
                GlideHelper.loadIntoUseFitWidth(holder.itemView.getContext(), filePath, holder.getImage(R.id.mi_item_video_bg));
                break;
            case 3:
                addChildViewClick(view, chatMessage);
                GlideHelper.loadIntoUseFitWidth(holder.itemView.getContext(), filePath, holder.getImage(R.id.mi_item_pic));
                break;
            case 4://地图
                addChildViewClick(view, chatMessage);
                TextureMapView textureMapView = holder.getViewById(R.id.tmv_show);
                textureMapViews.add(textureMapView);
                if (textureMapView != null) {
                    TextView localName = view.findViewById(R.id.mi_local_name);
                    TextView localRoad = view.findViewById(R.id.mi_local_road);
                    localName.setText(chatMessage.getLocationAddress());
                    localRoad.setText(chatMessage.getLocationRoad());
                    LatLng latLng = new LatLng(chatMessage.getLatitude(), chatMessage.getLongitude());

                    AMap aMap = textureMapView.getMap();
                    aMap.clear();
                    UiSettings uiSettings = aMap.getUiSettings();
                    uiSettings.setZoomControlsEnabled(false);//隐藏放大缩小按钮
                    uiSettings.setAllGesturesEnabled(false);//禁止手势
                    MarkerOptions markerOption = new MarkerOptions();
                    markerOption.position(latLng);
                    markerOption.draggable(true);//设置Marker可拖动
                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(view.getResources(), R.mipmap.icon_location)));
                    // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                    markerOption.setFlat(true);//设置marker平贴地图效果
                    aMap.addMarker(markerOption);
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                }
                break;
            case 5://文件

                break;
            case 6://指令
                InstructBean instructBean = chatMessage.getInstructBean();
                TextView instructTitle = holder.getText(R.id.mi_instruct_title);
                TextView instructContent = holder.getText(R.id.mi_instruct_content);
                TextView instructHolder = holder.getText(R.id.mi_instruct_holder);
                TextView instructVideoTime = holder.getText(R.id.mi_instruct_video_time);
                ImageView instructPic = holder.getImage(R.id.mi_instruct_pic);
                ImageView instructVideoBG = holder.getImage(R.id.mi_instruct_video_bg);
                RelativeLayout instructVideoContainer = holder.getViewById(R.id.mi_instruct_video_container);
                instructTitle.setText(StringUtil.isEmpty(instructBean.getTitle()));
                instructContent.setText(StringUtil.isEmpty(instructBean.getContent()));
                instructHolder.setText(StringUtil.isEmpty(chatMessage.getMessageHolder().getName()));

                String netFilePath = instructBean.getNetFilePath();
                String localFilePath = instructBean.getLocalFilePath();
                long duration = instructBean.getDuration();
                instructVideoContainer.setVisibility(View.GONE);
                instructPic.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(localFilePath)) {
                    if (duration > 0) {//视屏文件
                        instructVideoContainer.setVisibility(View.VISIBLE);
                        instructVideoTime.setText(DateUtil.long2String(duration));
                        GlideHelper.loadIntoUseFitWidth(holder.itemView.getContext(), localFilePath, instructVideoBG);
                    } else {//图片文件
                        instructPic.setVisibility(View.VISIBLE);
                        GlideHelper.loadIntoUseFitWidth(holder.itemView.getContext(), localFilePath, instructPic);
                    }
                }
                break;
        }


        //自己的信息
        if (chatMessage.getMessageOwner() == 0) {
            //消息发送状态  0:发送成功 1：发送失败 2:发送中
            switch (chatMessage.getMessageStatus()) {
                case 0:
                    holder.getViewById(R.id.mi_message_status_container).setVisibility(View.INVISIBLE);
                    holder.getImage(R.id.mi_iv_send_fail).setVisibility(View.INVISIBLE);
                    holder.getViewById(R.id.chat_item_progress).setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    holder.getViewById(R.id.mi_message_status_container).setVisibility(View.VISIBLE);
                    holder.getImage(R.id.mi_iv_send_fail).setVisibility(View.VISIBLE);
                    holder.getViewById(R.id.chat_item_progress).setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    holder.getViewById(R.id.mi_message_status_container).setVisibility(View.VISIBLE);
                    holder.getImage(R.id.mi_iv_send_fail).setVisibility(View.INVISIBLE);
                    holder.getViewById(R.id.chat_item_progress).setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public SmartVH onCreateViewHolder(ViewGroup parent, int viewType) {
        SmartVH smartVH = super.onCreateViewHolder(parent, viewType);
        TextureMapView textureMapView = smartVH.getViewById(R.id.tmv_show);
        if (textureMapView != null) {
            textureMapView.onCreate(null);
            textureMapViews.add(textureMapView);
        }
        return smartVH;

    }

    @Override
    protected View getSendFailTagView(SmartVH holder, int position) {
        return holder.getViewById(R.id.mi_iv_send_fail);
    }


    private void stopVoicePlay() {
        if (mDrawable == null) return;
        if (mPlayingItem != null) mPlayingItem.setPlayer(false);
        mDrawable.selectDrawable(0);
        mDrawable.stop();
        mDrawable = null;
        mPlayingItem = null;
        mediaPlayerUtil.stopPlay();
    }


    @Override
    public void onDestroy() {
        stopVoicePlay();
        for (TextureMapView textureMapView : textureMapViews) {
            if (textureMapView != null) textureMapView.onDestroy();
        }
        super.onDestroy();
    }
}
