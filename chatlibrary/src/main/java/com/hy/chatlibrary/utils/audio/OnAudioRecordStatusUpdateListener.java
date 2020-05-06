package com.hy.chatlibrary.utils.audio;

/**
 * @author:MtBaby
 * @date:2020/04/03 17:08
 * @desc:
 */
public interface OnAudioRecordStatusUpdateListener {
    void onRecordComplete(String filePath,long duration);

    void onRecording(double db, long duration);

    void onRecordFail(String failReason);
}
