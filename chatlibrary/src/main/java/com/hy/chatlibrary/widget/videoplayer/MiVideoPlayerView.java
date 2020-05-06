package com.hy.chatlibrary.widget.videoplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hrw.chatlibrary.R;

import java.io.IOException;

/**
 * @author:MtBaby
 * @date:2020/04/09 17:02
 * @desc:
 */
public class MiVideoPlayerView extends RelativeLayout {
    MediaPlayer mMediaPlayer;
    SurfaceView mSurfaceView;
    ImageView mFirstBitmap;
    ImageView mStartPlayer;
    ImageView mPlayControl;
    TextView mVideoTimeLong;
    SeekBar mSeekBar;

    private String mVideoPath;

    public MiVideoPlayerView(Context context) {
        this(context, null);
    }

    public MiVideoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.child_video_player_group, this, true);
        mStartPlayer = findViewById(R.id.mi_video_start_play);
        mPlayControl = findViewById(R.id.mi_video_play_control);
        mFirstBitmap = findViewById(R.id.mi_show_first_bitmap);
        mVideoTimeLong = findViewById(R.id.mi_video_time_show);
        mSeekBar = findViewById(R.id.mi_video_seekBar);
        mSurfaceView = findViewById(R.id.mi_sv_video_holder);
        SurfaceHolder mSurfaceViewHolder = mSurfaceView.getHolder();
        mSurfaceViewHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mMediaPlayer.setDisplay(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        mStartPlayer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlay();
            }
        });
        mPlayControl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer.isPlaying()) {
                    pausePlay();
                } else {

                }
            }
        });

    }

    private void setPlay() {
        try {
            mMediaPlayer = new MediaPlayer();
            mPlayControl.setImageResource(R.mipmap.icon_stop_play);
            mStartPlayer.setVisibility(GONE);
            mFirstBitmap.setVisibility(GONE);
            mMediaPlayer.setDataSource(mVideoPath);//设置播放视频文件
            mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);//缩放模式
//            mMediaPlayer.setLooping(true);//设置循环播放
            mMediaPlayer.prepareAsync();//异步准备
//            mMediaPlayer.prepare();//同步准备,因为是同步在一些性能较差的设备上会导致UI卡顿
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { //准备完成回调
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();//启动播放视频
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    onDestroy();
                }
            });
            mMediaPlayer.getCurrentPosition();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //暂停播放
    public void pausePlay() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void startPlay() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    public void stopPlay() {
        mStartPlayer.setVisibility(VISIBLE);
        mFirstBitmap.setVisibility(VISIBLE);
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();
    }

    public void setVideoPath(String mVideoPath) {
        this.mVideoPath = mVideoPath;
    }

    public void setFirstBitmap(Bitmap firstBitmap) {
        mFirstBitmap.setImageBitmap(firstBitmap);
    }

    public void onDestroy() {
        mStartPlayer.setVisibility(VISIBLE);
        mFirstBitmap.setVisibility(VISIBLE);
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
