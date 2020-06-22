package com.hy.chatlibrary.page;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.hrw.chatlibrary.R;
import com.hy.chatlibrary.MiChatHelper;
import com.hy.chatlibrary.adapter.BaseAdapter;
import com.hy.chatlibrary.adapter.OnItemClickListener;
import com.hy.chatlibrary.base.SmartVH;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.db.DBHelper;
import com.hy.chatlibrary.db.dao.ChatMessageDAO;
import com.hy.chatlibrary.db.entity.ChatMessage;
import com.hy.chatlibrary.db.entity.InstructBean;
import com.hy.chatlibrary.utils.DateUtil;
import com.hy.chatlibrary.utils.SPHelper;
import com.hy.chatlibrary.utils.StatusBarUtil;
import com.hy.chatlibrary.utils.StringUtil;
import com.hy.chatlibrary.utils.glide.GlideHelper;
import com.hy.chatlibrary.widget.CornerTextView;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/05/12 16:47
 * @desc:
 */
public class ChatGroupHistoryActivity extends AppCompatActivity {
    private EditText mEditText;
    private RecyclerView mRecyclerView;
    private LinearLayout mQuickSearchContainer;
    private ChatMessageDAO mChatMessageDAO;
    private InputMethodManager inputMethodManager;
    private String searchLabel;
    private LinkedList<TextureMapView> textureMapViews = new LinkedList<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mChatMessageDAO = DBHelper.getInstance(this).getChatMessageDAO();
        String chatGroupId = getIntent().getStringExtra(MiChatHelper.CHAT_GROUP_ID);
        StatusBarUtil.setStatueColor(this, R.color.mi_chat_main_bg, true);
        setContentView(R.layout.mi_activity_chat_history);
        findViewById(R.id.mi_iv_back).setOnClickListener(v -> finish());
        mEditText = findViewById(R.id.mi_history_search_input);
        mRecyclerView = findViewById(R.id.mi_show_history);
        mQuickSearchContainer = findViewById(R.id.mi_quick_search_container);
        mRecyclerView.setHasFixedSize(true);

        findViewById(R.id.mi_cancel_search_history).setOnClickListener(v -> {
            closeSoftInput();
            mRecyclerView.setVisibility(View.GONE);
            mEditText.setText(null);
            mQuickSearchContainer.setVisibility(View.VISIBLE);
        });
        mRecyclerView.setOnTouchListener((v, event) -> {
            boolean active = inputMethodManager.isActive();
            if (active) {
                closeSoftInput();
            }
            return false;
        });
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    List<ChatMessage> chatMessages = mChatMessageDAO.queryLikeLabel(chatGroupId, s.toString());
                    searchLabel = s.toString();
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mQuickSearchContainer.setVisibility(View.GONE);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(ChatGroupHistoryActivity.this));
                    BaseAdapter<ChatMessage> messageBaseAdapter;
                    mRecyclerView.setAdapter(messageBaseAdapter = new BaseAdapter<ChatMessage>(chatMessages, R.layout.item_history_show) {
                        @Override
                        protected void onBindView(SmartVH holder, ChatMessage data, int position) {
                            int holderShowNameIndex = data.getMessageHolderShowName().indexOf(searchLabel);
                            int messageContentIndex = data.getMessageContent().indexOf(searchLabel);
                            SpannableStringBuilder messageHolderShowName = new SpannableStringBuilder(data.getMessageHolderShowName());
                            SpannableStringBuilder messageContent = new SpannableStringBuilder(data.getMessageContent());
                            if (holderShowNameIndex != -1)
                                messageHolderShowName.setSpan(new ForegroundColorSpan(Color.RED), holderShowNameIndex, (holderShowNameIndex + searchLabel.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            if (messageContentIndex != -1)
                                messageContent.setSpan(new ForegroundColorSpan(Color.RED), messageContentIndex, (messageContentIndex + searchLabel.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            holder.getText(R.id.mi_holder_name).setText(messageHolderShowName);
                            holder.getText(R.id.mi_message_content).setText(messageContent);
                            holder.getText(R.id.mi_message_date).setText(DateUtil.getStringTimeByMilli(data.getMessageSTMillis(), "MM月dd日"));
                            CornerTextView cornerTextView = holder.getViewById(R.id.mi_holder_pro);
                            cornerTextView.setText(messageHolderShowName);
                        }
                    });
                    messageBaseAdapter.setOnItemClickListener((OnItemClickListener<ChatMessage>) (view, data) -> gotoPosition(data));
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    mQuickSearchContainer.setVisibility(View.VISIBLE);
                }
            }
        });
        findViewById(R.id.mi_video_photo_search).setOnClickListener(v -> {// 视频/图片查询
            new Thread(() -> {
                List<ChatMessage> chatMessages = mChatMessageDAO.queryHistoryByGroupId(2, 3, chatGroupId);
                runOnUiThread(() -> {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mQuickSearchContainer.setVisibility(View.GONE);
                    mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
                    BaseAdapter<ChatMessage> messageBaseAdapter;
                    mRecyclerView.setAdapter(messageBaseAdapter = new BaseAdapter<ChatMessage>(chatMessages, R.layout.item_video_image_show) {
                        @Override
                        protected void onBindView(SmartVH holder, ChatMessage chatMessage, int position) {
                            ImageView imageView = holder.getViewById(R.id.iv_item_image);
                            String filePath = chatMessage.getMessageLocalPath();
                            File file = new File(StringUtil.isEmpty(filePath));
                            if (!file.exists()) {
                                filePath = chatMessage.getMessageNetPath();
                            }
                            GlideHelper.loadIntoUseNoCorner(holder.itemView.getContext(), filePath, imageView);
                            if (chatMessage.getItemType() == 2) {
                                TextView textView = findViewById(R.id.time);
                                textView.setText(DateUtil.long2String(chatMessage.getDuration()));
                            }
                        }
                    });
                    messageBaseAdapter.setOnItemClickListener((OnItemClickListener<ChatMessage>) (view, data) -> gotoPosition(data));
                });
            }).start();
        });
        findViewById(R.id.mi_local_search).setOnClickListener(v -> {//位置查询
            new Thread(() -> {
                List<ChatMessage> chatMessages = mChatMessageDAO.queryHistoryByGroupId(4, chatGroupId);
                runOnUiThread(() -> {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mQuickSearchContainer.setVisibility(View.GONE);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                    BaseAdapter<ChatMessage> messageBaseAdapter;
                    mRecyclerView.setAdapter(messageBaseAdapter = new BaseAdapter<ChatMessage>(chatMessages, R.layout.item_history_local_show) {
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
                        protected void onBindView(SmartVH holder, ChatMessage chatMessage, int position) {
                            TextureMapView textureMapView = holder.getViewById(R.id.tmv_show);
                            textureMapViews.add(textureMapView);
                            if (textureMapView != null) {
                                TextView localName = holder.getViewById(R.id.mi_local_name);
                                TextView localRoad = holder.getViewById(R.id.mi_local_road);
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
                                markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_location)));
                                // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                                markerOption.setFlat(true);//设置marker平贴地图效果
                                aMap.addMarker(markerOption);
                                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                            }
                        }
                    });
                    messageBaseAdapter.setOnItemClickListener((OnItemClickListener<ChatMessage>) (view, data) -> gotoPosition(data));
                });
            }).start();
        });
        findViewById(R.id.mi_instruct_search).setOnClickListener(v -> {//指令查询
            new Thread(() -> {
                List<ChatMessage> chatMessages = mChatMessageDAO.queryHistoryByGroupId(6, chatGroupId);
                runOnUiThread(() -> {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mQuickSearchContainer.setVisibility(View.GONE);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                    BaseAdapter<ChatMessage> messageBaseAdapter;
                    mRecyclerView.setAdapter(messageBaseAdapter = new BaseAdapter<ChatMessage>(chatMessages, R.layout.item_instruct_manager_show) {
                        @Override
                        protected void onBindView(SmartVH holder, ChatMessage chatMessage, int position) {
                            InstructBean instructBean = chatMessage.getInstructBean();
                            TextView instructTitle = holder.getText(R.id.mi_instruct_title);
                            TextView instructContent = holder.getText(R.id.mi_instruct_content);
                            TextView instructVideoTime = holder.getText(R.id.mi_instruct_video_time);
                            ImageView instructPic = holder.getImage(R.id.mi_instruct_pic);
                            ImageView instructVideoBG = holder.getImage(R.id.mi_instruct_video_bg);
                            RelativeLayout instructVideoContainer = holder.getViewById(R.id.mi_instruct_video_container);
                            RecyclerView mAcceptorShow = holder.getViewById(R.id.mi_acceptor_show);
                            FlexboxLayoutManager manager = new FlexboxLayoutManager(ChatGroupHistoryActivity.this, FlexDirection.ROW, FlexWrap.WRAP);
                            manager.setJustifyContent(JustifyContent.SPACE_BETWEEN);//交叉轴的起点对齐。
                            mAcceptorShow.setLayoutManager(manager);
                            mAcceptorShow.setHasFixedSize(true);
                            mAcceptorShow.setAdapter(new BaseAdapter<MessageHolder>(instructBean.getAcceptors(), R.layout.item_acceptor_tag) {
                                @Override
                                protected void onBindView(SmartVH holder, MessageHolder data, int position) {
                                    TextView miTagName = holder.getViewById(R.id.mi_tag_name);
                                    miTagName.setText(TextUtils.isEmpty(data.getGroupName()) ? data.getName() : data.getGroupName());
                                }
                            });
                            instructTitle.setText(StringUtil.isEmpty(instructBean.getTitle()));
                            instructContent.setText(StringUtil.isEmpty(instructBean.getContent()));

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
                        }
                    });
                    messageBaseAdapter.setOnItemClickListener((OnItemClickListener<ChatMessage>) (view, data) -> gotoPosition(data));
                });
            }).start();
        });
    }

    private void gotoPosition(ChatMessage data) {
        closeSoftInput();
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
        finish();
        SPHelper.getInstance(this).putString("scrollTo", data.getMessageId());
    }

    private void closeSoftInput() {
        inputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (TextureMapView textureMapView : textureMapViews) {
            if (textureMapView != null) textureMapView.onDestroy();
        }
    }
}
