package com.hy.chatlibrary.widget.chatinput;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hrw.chatlibrary.R;
import com.hy.chatlibrary.adapter.BaseChatAdapter;
import com.hy.chatlibrary.adapter.OnPullDownLoadMoreListener;
import com.hy.chatlibrary.bean.ChatContentType;
import com.hy.chatlibrary.db.entity.ChatMessage;
import com.hy.chatlibrary.utils.DateUtil;
import com.hy.chatlibrary.utils.audio.AudioManager;
import com.hy.chatlibrary.utils.audio.AudioRecordHelp;
import com.hy.chatlibrary.utils.audio.OnAudioRecordStatusUpdateListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/03/30 17:04
 * @desc:
 */
public class MiChatInputGroup extends RelativeLayout implements View.OnClickListener, OnAudioRecordStatusUpdateListener, ViewTreeObserver.OnGlobalLayoutListener {
    private final static String SOFT_HEIGHT = "soft_height";
    private final float density;
    private String filePathDir;

    ImageView ivVoice;//语音输入切换
    ImageView ivSmile;//表情
    ImageView ivAdd;//打开菜单可选项
    Button btSend;//文字发送按钮
    EditText editText;//文字编辑控件
    TextView tvVoiceClick;//长按录音控件
    RecyclerView rvSendContent;//发送内容类型列表
    TextView rvSmileContent;//发送表情
    RecyclerView rvChatList;//聊天列表
    RelativeLayout emotionContainer;//其他
    RelativeLayout mQuoteContainer;
    TextView mQuoteLabel;
    ImageView mCloseQuote;
    LinearLayout mLabelMsgContainer;

    BaseChatAdapter mBaseChatAdapter;
    ChatContentTypeAdapter typeAdapter;
    InputMethodManager inputMethodManager;//输入键盘控制


    boolean isShowTakeVoiceBtn = false;//语音录制按钮是否显示
    boolean isShowContent = false;//菜单内容框是否开启
    boolean isOpenSoftKeyBoard = false;//输入键盘是否开启


    public RecyclerView getRvChatList() {
        return rvChatList;
    }

    OnTextSubmitListener mOnTextSubmitListener;
    OnAudioRecordListener mOnAudioRecordListener;
    OnCloseQuoteListener mOnCloseQuoteListener;
    private LinearLayoutManager linearLayoutManager;
    private PopupWindow window;
    private ImageView ivRecordTag;
    private TextView tvRecordTime;
    private TextView tvRecordNotice;

    AudioManager mAudioManager;
    private SharedPreferences preferences;


    public MiChatInputGroup(Context context) {
        this(context, null);
    }

    public MiChatInputGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.child_chat_input_group, this, true);
        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        density = context.getResources().getDisplayMetrics().density;
        initView();
    }

    private void initView() {
        preferences = getContext().getSharedPreferences("WindowsSoftHeight", 0);
//        int height = preferences.getInt(SOFT_HEIGHT, 0);
//        System.out.println("之前是否测量出高度:" + height);
        getViewTreeObserver().addOnGlobalLayoutListener(this);

        ivVoice = findViewById(R.id.emotion_voice);
        ivSmile = findViewById(R.id.emotion_smile);
        ivAdd = findViewById(R.id.emotion_add);
        btSend = findViewById(R.id.emotion_send);
        mQuoteContainer = findViewById(R.id.mi_quote_container);
        mLabelMsgContainer = findViewById(R.id.mi_label_msg_container);
        mCloseQuote = findViewById(R.id.mi_close_quote);
        mQuoteLabel = findViewById(R.id.item_quote_tag);
        setOnClickListener(v -> closeSoftInputGroup());

//        mPullDownLoadMore = findViewById(R.id.mi_pull_down_load_more);
        ivSmile.setOnClickListener(this);
        ivVoice.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
        btSend.setOnClickListener(this);
        mCloseQuote.setOnClickListener(this);

        initRecordBtn();
        initChatList();
        initContentTypeList();
        initEditText();
        initEmotionLayout();

    }

    public void showQuoteEdit(String label) {
        mQuoteContainer.setVisibility(VISIBLE);
        mQuoteLabel.setText(label);
    }

    //添加@引用
    public void addATQuote(String label) {
        if (editText != null && editText.getText() != null) {
            editText.requestFocus();
            SpannableStringBuilder spannableString = new SpannableStringBuilder(editText.getText());
            label = "@" + label + " ";
            spannableString.append(label);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#6f6f6f"));
            spannableString.setSpan(colorSpan, spannableString.length() - label.length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            editText.setText(spannableString);
            editText.setSelection(spannableString.length());
        }
    }


    public void setFilePathDir(String filePathDir) {
        this.filePathDir = filePathDir;
    }

    //初始化语音录制按钮
    @SuppressLint("ClickableViewAccessibility")
    private void initRecordBtn() {
        tvVoiceClick = findViewById(R.id.voice_text);
        tvVoiceClick.setOnTouchListener((v, event) -> {
            int x = (int) event.getX();
            int y = (int) event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    showRecordVoiceAnimate();
                    tvVoiceClick.setText("松开结束");
                    tvRecordNotice.setText("手指上滑，取消发送");
                    tvVoiceClick.setTag("1");
                    mAudioManager = AudioRecordHelp.getInstance()
                            .setOnAudioRecordStatusUpdateListener(MiChatInputGroup.this)
                            .setFilePath(this.filePathDir)
                            .buildAudioManager()
                            .startRecord();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (wantToCancel(x, y)) {
                        tvVoiceClick.setText("松开结束");
                        tvRecordNotice.setText("松开手指，取消发送");
                        tvVoiceClick.setTag("2");
                    } else {
                        tvVoiceClick.setText("松开结束");
                        tvRecordNotice.setText("手指上滑，取消发送");
                        tvVoiceClick.setTag("1");
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    window.dismiss();
                    if ("2".equals(tvVoiceClick.getTag())) {
                        //TODO 取消发送操作，删除所录制的语音
                        mAudioManager.stopRecord(true);
                    } else {
                        //TODO 停止语音录制操作，进行语音发送
                        mAudioManager.stopRecord(false);
                    }
                    tvVoiceClick.setText("按住说话");
                    tvVoiceClick.setTag("3");
                    break;
            }
            return false;
        });
    }

    private boolean wantToCancel(int x, int y) {
        // 超过按钮的宽度
        if (x < 0 || x > tvVoiceClick.getWidth()) {
            return true;
        }
        // 超过按钮的高度
        return y < -50 || y > tvVoiceClick.getHeight() + 50;
    }

    //初始化文字编辑控件
    @SuppressLint("ClickableViewAccessibility")
    private void initEditText() {
        editText = findViewById(R.id.edit_text);
        editText.setOnTouchListener((v, event) -> {
            openSoftInput();
            hideContent();
            scrollToBottom();
            return false;
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && btSend.getLayoutParams().width == 0) {
                    showSendBtn();
                }
                if (s.length() == 0) {
                    hideSendBtn();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                final int selectionStart = Selection.getSelectionStart(editText.getText());
                final int selectionEnd = Selection.getSelectionEnd(editText.getText());

                String string = editText.getText().toString();
                String[] split = string.split(" @");

                final ForegroundColorSpan spans[] = editText.getText().getSpans(selectionStart, selectionEnd, ForegroundColorSpan.class);
                for (ForegroundColorSpan span : spans) {
                    if (span == null) continue;
                    if (editText.getText().getSpanEnd(span) == selectionStart) {
                        final int spanStart = editText.getText().getSpanStart(span);
                        final int spanEnd = editText.getText().getSpanEnd(span);
                        System.out.println("要删除的对象:" + editText.getText().subSequence(spanStart, spanEnd));
                        Selection.setSelection(editText.getText(), spanStart, spanEnd);//选中要删除的字段
                        return true;
                    }
                }
            }
            return false;
        });
    }

    //初始化输入框底部布局
    private void initEmotionLayout() {
        emotionContainer = findViewById(R.id.emotion_layout);
    }

    //初始化底部布局二
    private void initContentTypeList() {
        List<ChatContentType> contentTypes = new ArrayList<>();
        contentTypes.add(new ChatContentType(ContentType.PIC, "相册", R.mipmap.icon_type_photo));
        contentTypes.add(new ChatContentType(ContentType.TAKE_PHOTO, "拍照", R.mipmap.icon_type_takephoto));
        contentTypes.add(new ChatContentType(ContentType.TAKE_VIDEO, "录像", R.mipmap.icon_type_takevideo));
        contentTypes.add(new ChatContentType(ContentType.LOCAL, "位置", R.mipmap.icon_type_local));
//        contentTypes.add(new ChatContentType(ContentType.FILE, "文件", R.mipmap.icon_type_file));
        contentTypes.add(new ChatContentType(ContentType.INSTRUCT, "指令", R.mipmap.icon_type_command));

        rvSendContent = findViewById(R.id.mi_other_send_content);
        rvSmileContent = findViewById(R.id.mi_smile_container);
        rvSendContent.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvSendContent.setAdapter(typeAdapter = new ChatContentTypeAdapter(contentTypes));
    }


    //初始化聊天列表
    @SuppressLint("ClickableViewAccessibility")
    private void initChatList() {
        rvChatList = findViewById(R.id.mi_chat_list);
        rvChatList.setItemViewCacheSize(20);
        rvChatList.setDrawingCacheEnabled(true);
        rvChatList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        ((SimpleItemAnimator) rvChatList.getItemAnimator()).setSupportsChangeAnimations(false);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvChatList.setLayoutManager(linearLayoutManager);
//        rvChatList.setHasFixedSize(true);
//        rvChatList.setItemViewCacheSize(12);
        rvChatList.setOnTouchListener((v, event) -> {
            closeSoftInputGroup();
            return false;
        });
    }


    public void setStackFromEnd(boolean stackFromEnd) {
        linearLayoutManager.setStackFromEnd(stackFromEnd);
    }

    //滚动聊天列表到底部
    public void scrollToBottom() {
        linearLayoutManager.scrollToPositionWithOffset(rvChatList.getAdapter().getItemCount() - 1, Integer.MIN_VALUE);
    }

    public void scrollToPosition(ChatMessage message) {
        int position = -1;
        for (int i = 0; i < mBaseChatAdapter.getItemCount(); i++) {
            List<ChatMessage> chatMessages = mBaseChatAdapter.getChatMessages();
            if (message.getMessageId().equals(chatMessages.get(i).getMessageId())) {
                position = i;
                break;
            }
        }
        linearLayoutManager.scrollToPosition(position);
    }


    public void setChatListAdapter(BaseChatAdapter chatListAdapter) {
        mBaseChatAdapter = chatListAdapter;
        rvChatList.setAdapter(mBaseChatAdapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        //录音按钮
        if (id == R.id.emotion_voice) {
            isShowTakeVoiceBtn = !isShowTakeVoiceBtn;
            if (isShowTakeVoiceBtn) {
                showTakeVoiceBtn();
            } else {
                showEditText();
            }

        }
        //打开表情列表按钮
        if (id == R.id.emotion_smile) {
            if (isShowContent){
                if (rvSmileContent.getVisibility()==VISIBLE){
                    openSoftInput();
                    hideContent();
                }else {
                    rvSmileContent.setVisibility(VISIBLE);
                    rvSendContent.setVisibility(GONE);
                }
            }else {
                isShowTakeVoiceBtn = false;
                ivVoice.setImageResource(R.mipmap.icon_chat_voice);
                tvVoiceClick.setVisibility(GONE);
                mLabelMsgContainer.setVisibility(VISIBLE);
                showContent();
                rvSmileContent.setVisibility(VISIBLE);
                rvSendContent.setVisibility(GONE);
                closeSoftInput();
            }
        }
        //打开其他可发送内容按钮
        if (id == R.id.emotion_add) {
            if (isShowContent) {
                if (rvSendContent.getVisibility()==VISIBLE){
                    openSoftInput();
                    hideContent();
                }else {
                    rvSmileContent.setVisibility(GONE);
                    rvSendContent.setVisibility(VISIBLE);
                }
            } else {
//                showEditText();
                isShowTakeVoiceBtn = false;
                ivVoice.setImageResource(R.mipmap.icon_chat_voice);
                tvVoiceClick.setVisibility(GONE);
                mLabelMsgContainer.setVisibility(VISIBLE);
                showContent();
                rvSmileContent.setVisibility(GONE);
                rvSendContent.setVisibility(VISIBLE);
                closeSoftInput();
            }
        }
        //文字发送按钮
        if (id == R.id.emotion_send) {
            if (mOnTextSubmitListener != null) {
                mOnTextSubmitListener.onTextSubmit(editText.getText().toString());
                editText.setText(null);
            }
        }
        if (id == R.id.mi_close_quote) {
            closeQuote();
        }
    }

    private void showTakeVoiceBtn() {
        isShowTakeVoiceBtn = true;
        ivVoice.setImageResource(R.mipmap.icon_keyboad);
        tvVoiceClick.setVisibility(VISIBLE);
        mLabelMsgContainer.setVisibility(GONE);
        closeSoftInput();
        hideContent();
        hideSendBtn();
    }

    private void showEditText() {
        isShowTakeVoiceBtn = false;
        ivVoice.setImageResource(R.mipmap.icon_chat_voice);
        tvVoiceClick.setVisibility(GONE);
        mLabelMsgContainer.setVisibility(VISIBLE);
        scrollToBottom();
        openSoftInput();
        if (editText.getText().length() > 0) {
            showSendBtn();
        } else {
            hideSendBtn();
        }
    }

    public void closeQuote() {
        mQuoteContainer.setVisibility(GONE);
        mQuoteLabel.setText("");
        if (mOnCloseQuoteListener != null) {
            mOnCloseQuoteListener.onCloseQuoteEdite();
        }
    }


    //输入组件是否开启
    public boolean isSoftInputGroupShow() {
        return isOpenSoftKeyBoard || isShowContent;
    }

    //关闭输入组件
    public void closeSoftInputGroup() {
        hideContent();
        closeSoftInput();
    }

    private void hideSendBtn() {
        ViewGroup.LayoutParams layoutParams = btSend.getLayoutParams();
        layoutParams.width = 0;
        ivAdd.setVisibility(VISIBLE);
    }

    private void showSendBtn() {
        ViewGroup.LayoutParams layoutParams = btSend.getLayoutParams();
        layoutParams.width = (int) (60 * density);
        ivAdd.setVisibility(GONE);
    }

    private void hideContent() {
        isShowContent = false;
        ViewGroup.LayoutParams layoutParams = emotionContainer.getLayoutParams();
        layoutParams.height = 0;
        //判断输入键盘是否开启，没有打开的话，选择菜单则需要重绘
        if (!isOpenSoftKeyBoard) emotionContainer.requestLayout();
    }


    private void showContent() {
        isShowContent = true;
        ViewGroup.LayoutParams layoutParams = emotionContainer.getLayoutParams();
        layoutParams.height = preferences.getInt(SOFT_HEIGHT, 787);
        scrollToBottom();
        //判断输入键盘是否开启，没有打开的话，选择菜单则需要重绘
        if (!isOpenSoftKeyBoard) emotionContainer.requestLayout();
    }

    public void openSoftInput() {
        isOpenSoftKeyBoard = true;
        editText.requestFocus();
        inputMethodManager.showSoftInput(editText, 0);
    }

    public void openSoftInput(String label) {
        isOpenSoftKeyBoard = true;
        editText.requestFocus();
        editText.setText(label);
        editText.setSelection(label.length());
        inputMethodManager.showSoftInput(editText, 0);
//        int softHeight = getSupportSoftInputHeight();
    }

    private void closeSoftInput() {
        isOpenSoftKeyBoard = false;
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showRecordVoiceAnimate() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_microphone, null);
        ivRecordTag = view.findViewById(R.id.iv_recording_icon);
        tvRecordTime = view.findViewById(R.id.tv_recording_time);
        tvRecordNotice = view.findViewById(R.id.tv_recording_text);
        window = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.showAtLocation(tvVoiceClick, Gravity.CENTER, 0, 0);
    }


    public void setOnTextSubmitListener(OnTextSubmitListener onTextSubmitListener) {
        this.mOnTextSubmitListener = onTextSubmitListener;
    }

    public void setOnContentTypeClickListener(OnContentTypeClickListener onContentTypeClickListener) {
        typeAdapter.setmOnContentTypeClickListener(onContentTypeClickListener);
    }

    public void setOnAudioRecordListener(OnAudioRecordListener mOnAudioRecordListener) {
        this.mOnAudioRecordListener = mOnAudioRecordListener;
    }

    public void setOnPullDownLoadMoreListener(OnPullDownLoadMoreListener mOnPullDownLoadMoreListener) {
        mBaseChatAdapter.setOnPullDownLoadMoreListener(mOnPullDownLoadMoreListener);
    }

    public void setOnCloseQuoteListener(OnCloseQuoteListener mOnCloseQuoteListener) {
        this.mOnCloseQuoteListener = mOnCloseQuoteListener;
    }

    int allHeight = 0;
    int afterShowSoftWindowsHeight = 0;

    @Override
    public void onGlobalLayout() {
        Rect rect = new Rect();
        //1、获取main在窗体的可视区域
        getWindowVisibleDisplayFrame(rect);
        if (allHeight == 0) allHeight = rect.bottom;
        afterShowSoftWindowsHeight = rect.bottom;
        if (allHeight - afterShowSoftWindowsHeight > 300) {
//            System.out.println("底部高度:" + (allHeight - afterShowSoftWindowsHeight));
            preferences.edit().putInt(SOFT_HEIGHT, allHeight - afterShowSoftWindowsHeight).apply();
        }
    }

    @Override
    public void onRecordComplete(String filePath, long duration) {
        if (mOnAudioRecordListener != null) {
            mOnAudioRecordListener.onAudioRecord(filePath, duration);
        }
    }

    @Override
    public void onRecording(double db, long duration) {
        ivRecordTag.getDrawable().setLevel((int) db);
        tvRecordTime.setText(DateUtil.long2String(duration));
    }

    @Override
    public void onRecordFail(String failReason) {
        if (mOnAudioRecordListener != null) {
            mOnAudioRecordListener.onAudioRecordFail(failReason);
        }
    }
}
