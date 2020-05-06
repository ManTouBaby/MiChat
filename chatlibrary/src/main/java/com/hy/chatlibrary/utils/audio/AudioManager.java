package com.hy.chatlibrary.utils.audio;

import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import com.hy.chatlibrary.utils.DateUtil;

import java.io.File;

/**
 * @author:MtBaby
 * @date:2020/04/03 17:34
 * @desc:
 */
public class AudioManager {
    private String mFileDir;
    private OnAudioRecordStatusUpdateListener mOnAudioRecordStatusUpdateListener;
    private MediaRecorder mMediaRecorder;
    private String filePath;//当前文件路径
    private long startTime = -1;
    private Handler mHandler = new Handler();

    public AudioManager(String mFileDir, OnAudioRecordStatusUpdateListener mOnAudioRecordStatusUpdateListener) {
        this.mFileDir = mFileDir;
        this.mOnAudioRecordStatusUpdateListener = mOnAudioRecordStatusUpdateListener;
    }

    public synchronized AudioManager startRecord() {
        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();
        try {

            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风

            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            String fileName = DateUtil.getSystemTime("yyyyMMdd_HHmmss") + ".m4a";
            File destDir = new File(mFileDir);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            filePath = mFileDir + fileName;
            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            startTime = System.currentTimeMillis();
            recordStatusUpdate();
        } catch (Exception e) {
            if (mOnAudioRecordStatusUpdateListener != null) {
                mOnAudioRecordStatusUpdateListener.onRecordFail(e.toString());
            }
            Log.i("failed!", e.toString());
        }
        return this;
    }

    private Runnable mRunnable = this::recordStatusUpdate;

    private void recordStatusUpdate() {
        if (mMediaRecorder != null) {
            double ratio = (double) mMediaRecorder.getMaxAmplitude();
//            System.out.println("语音大小:" + ratio);
            if (ratio > 1) {
//                db = ratio / 100;
                if (null != mOnAudioRecordStatusUpdateListener) {
                    mOnAudioRecordStatusUpdateListener.onRecording(ratio * 2, (System.currentTimeMillis() - startTime) + 1000);
                }
            }

            mHandler.postDelayed(mRunnable, 100);
        }
    }

    public void stopRecord(boolean isDeleteFile) {
        try {
            mHandler.removeCallbacks(mRunnable);
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
            if (null != mOnAudioRecordStatusUpdateListener && !isDeleteFile) {
                mOnAudioRecordStatusUpdateListener.onRecordComplete(filePath, (System.currentTimeMillis() - startTime) + 1000);
            }
            if (isDeleteFile) {
                File file = new File(filePath);
                if (file.exists()) file.delete();
            }
        } catch (RuntimeException e) {
            System.out.println(e.toString());
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            File file = new File(filePath);
            if (file.exists()) file.delete();
            if (mOnAudioRecordStatusUpdateListener != null) {
                mOnAudioRecordStatusUpdateListener.onRecordFail(e.toString());
            }
        }
    }
}
