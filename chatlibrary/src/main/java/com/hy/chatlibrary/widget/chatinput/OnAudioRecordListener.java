package com.hy.chatlibrary.widget.chatinput;

/**
 * @author:MtBaby
 * @date:2020/04/07 11:28
 * @desc:
 */
public interface OnAudioRecordListener {
    void onAudioRecord(String filePath, long duration);

    void onAudioRecordFail(String failReason);
}
