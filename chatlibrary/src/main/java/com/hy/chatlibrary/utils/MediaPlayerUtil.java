package com.hy.chatlibrary.utils;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * @author:MtBaby
 * @date:2020/04/07 15:31
 * @desc:
 */
public class MediaPlayerUtil {
    MediaPlayer mediaPlayer;

    public void playerMedia(String url, MediaPlayer.OnCompletionListener onCompletionListener) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
            mediaPlayer.setOnCompletionListener(onCompletionListener);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stopPlay() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public boolean isPlaying() {
        if (mediaPlayer == null) return false;
        return mediaPlayer.isPlaying();
    }

}
