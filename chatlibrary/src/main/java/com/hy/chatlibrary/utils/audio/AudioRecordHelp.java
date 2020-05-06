package com.hy.chatlibrary.utils.audio;

import android.os.Environment;

/**
 * @author:MtBaby
 * @date:2020/04/03 14:33
 * @desc:
 */
public class AudioRecordHelp {
    private static AudioRecordHelp mAudioRecordUtil;
    private String mFileDir = Environment.getExternalStorageDirectory() + "/hy/audio/";
    private OnAudioRecordStatusUpdateListener mOnAudioRecordStatusUpdateListener;

    private AudioRecordHelp() {
    }

    public static AudioRecordHelp getInstance() {
        if (mAudioRecordUtil == null) {
            mAudioRecordUtil = new AudioRecordHelp();
        }
        return mAudioRecordUtil;
    }

    public AudioRecordHelp setFilePath(String path) {
        mFileDir = path + "/audio/";
        return mAudioRecordUtil;
    }

    public AudioRecordHelp setOnAudioRecordStatusUpdateListener(OnAudioRecordStatusUpdateListener mOnAudioRecordStatusUpdateListener) {
        this.mOnAudioRecordStatusUpdateListener = mOnAudioRecordStatusUpdateListener;
        return mAudioRecordUtil;
    }

    public AudioManager buildAudioManager() {
        return new AudioManager(mFileDir, mOnAudioRecordStatusUpdateListener);
    }


}
