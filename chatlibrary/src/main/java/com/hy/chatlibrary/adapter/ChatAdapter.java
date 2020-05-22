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
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.db.entity.ChatMessage;
import com.hy.chatlibrary.db.entity.InstructBean;
import com.hy.chatlibrary.utils.DateUtil;
import com.hy.chatlibrary.utils.MediaPlayerUtil;
import com.hy.chatlibrary.utils.SPHelper;
import com.hy.chatlibrary.utils.StringUtil;
import com.hy.chatlibrary.utils.glide.GlideHelper;
import com.hy.chatlibrary.widget.CornerTextView;

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
        sparseIntArray.put(7, R.layout.mim_item_type_quote_reply);
        sparseIntArray.put(8, R.layout.mim_item_type_quote_reply);
        sparseIntArray.put(9, R.layout.mim_item_type_txt);
        sparseIntArray.put(10, R.layout.mimo_item_common_notify);

        sparseIntArray.put(100, R.layout.mio_item_type_txt);
        sparseIntArray.put(101, R.layout.mio_item_type_voice);
        sparseIntArray.put(102, R.layout.mio_item_type_video);
        sparseIntArray.put(103, R.layout.mio_item_type_pic);
        sparseIntArray.put(104, R.layout.mio_item_type_local);
        sparseIntArray.put(105, R.layout.mio_item_type_file);
        sparseIntArray.put(106, R.layout.mio_item_type_instruct);
        sparseIntArray.put(107, R.layout.mio_item_type_quote_reply);
        sparseIntArray.put(108, R.layout.mio_item_type_quote_reply);
        sparseIntArray.put(109, R.layout.mio_item_type_txt);
        sparseIntArray.put(110, R.layout.mimo_item_common_notify);
        return sparseIntArray;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindView(final SmartVH holder, final int position) {
        final ChatMessage chatMessage = mChatMessages.get(position);
        TextView msgTime = holder.getText(R.id.mi_message_time);
        View view = holder.getViewById(R.id.mi_content_container);   //item单击监听
        CornerTextView holderPro = holder.getViewById(R.id.mi_holder_pro);
        ImageView leaderTag = holder.getViewById(R.id.iv_leader_tag);
        MessageHolder messageHolder = chatMessage.getMessageHolder();
        String messageHolderShowName = chatMessage.getMessageHolderShowName();
        String messageHolderName = chatMessage.getMessageHolderName();


        if (holderPro != null) {
            addChildViewClick(holderPro, chatMessage);
            if (messageHolder!=null){
                holderPro.setAutoBackGroundText(messageHolderShowName);
                leaderTag.setVisibility(messageHolder.getRole() == 1 ? View.VISIBLE : View.GONE);
            }
        }
        if (chatMessage.getMessageOwner() == 1 && holderPro != null)
            addChildViewLongClick(holderPro, chatMessage);
        if (view != null) addChildViewLongClick(view, chatMessage);
        if (msgTime != null) {
            if (position > 0) {
                ChatMessage message = mChatMessages.get(position - 1);
                long spaceTime = chatMessage.getMessageSTMillis() - message.getMessageSTMillis();
                if (spaceTime > 2 * 60 * 1000) {
                    msgTime.setVisibility(View.VISIBLE);
                    msgTime.setText(chatMessage.getMessageST());
                } else {
                    msgTime.setVisibility(View.GONE);
                }
            } else {
                msgTime.setText(chatMessage.getMessageST());
            }
        }

        String filePath = chatMessage.getMessageLocalPath();
        File file = new File(StringUtil.isEmpty(filePath));
        if (!file.exists()) {
            filePath = chatMessage.getMessageNetPath();
        }

        switch (chatMessage.getItemType()) {
            case 10:
                if (msgTime != null) msgTime.setVisibility(View.GONE);
                holder.getText(R.id.mt_notify_content).setText(messageHolderShowName + chatMessage.getMessageContent());
                break;
            case 0:
            case 9:
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
                if (view != null) view.setOnClickListener(v -> {
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
                if (view != null) addChildViewClick(view, chatMessage);
                holder.getText(R.id.mi_item_video_time).setText(DateUtil.long2String(chatMessage.getDuration()));
                GlideHelper.loadIntoUseNoCorner(holder.itemView.getContext(), filePath, holder.getImage(R.id.mi_item_video_bg));
                break;
            case 3:
                if (view != null) addChildViewClick(view, chatMessage);
                GlideHelper.loadIntoUseNoCorner(holder.itemView.getContext(), filePath, holder.getImage(R.id.mi_item_pic));
                break;
            case 4://地图
                if (view != null) addChildViewClick(view, chatMessage);
                TextureMapView textureMapView = holder.getViewById(R.id.tmv_show);
                textureMapViews.add(textureMapView);
                if (textureMapView != null && view != null) {
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
                instructHolder.setText(StringUtil.isEmpty(messageHolderShowName));

                addChildViewClick(instructVideoContainer, chatMessage);
                addChildViewClick(instructPic, chatMessage);

                String localFilePath = instructBean.getLocalFilePath();
                File instructFile = new File(StringUtil.isEmpty(localFilePath));
                if (!instructFile.exists()) {
                    localFilePath = instructBean.getNetFilePath();
                }
                long duration = instructBean.getDuration();
                instructVideoContainer.setVisibility(View.GONE);
                instructPic.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(localFilePath)) {
                    if (duration > 0) {//视屏文件
                        instructVideoContainer.setVisibility(View.VISIBLE);
                        instructVideoTime.setText(DateUtil.long2String(duration));
                        GlideHelper.loadIntoUseNoCorner(holder.itemView.getContext(), localFilePath, instructVideoBG);
                    } else {//图片文件
                        instructPic.setVisibility(View.VISIBLE);
                        GlideHelper.loadIntoUseNoCorner(holder.itemView.getContext(), localFilePath, instructPic);
                    }
                }
                break;
            case 7:
            case 8:
                ChatMessage quoteMessage = chatMessage.getChatMessage();
                holder.getText(R.id.mi_chat_item_text).setText(StringUtil.isEmpty(chatMessage.getMessageContent()));
                TextView quoteShow = holder.getText(R.id.mi_quote_reply_notify);
                quoteShow.setText(quoteMessage.getMessageHolderShowName() + "：" + quoteMessage.getMessageContent());
                addChildViewClick(quoteShow, chatMessage);
                break;
        }

        if (chatMessage.getMessageOwner() == 1 && chatMessage.getItemType() != 10) {
            TextView groupShowName = holder.getText(R.id.mi_group_show_name);
            boolean isOpenGroupName = SPHelper.getInstance(holder.getItemView().getContext()).getBoolean(SPHelper.IS_OPEN_GROUP_NAME);
            groupShowName.setVisibility(isOpenGroupName ? View.VISIBLE : View.GONE);
           groupShowName.setText(TextUtils.isEmpty(messageHolderShowName) ? messageHolderName : messageHolderShowName);
        }

        //自己的信息
        if (chatMessage.getMessageOwner() == 0 && chatMessage.getItemType() != 10) {
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
