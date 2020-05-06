package com.hy.chatlibrary.utils.retrofit;

/**
 * @author:MtBaby
 * @date:2020/04/24 17:18
 * @desc:
 */
public class DownloadInfo {
    public String message;
    public int progress;

    public DownloadInfo(String message, int progress) {
        this.message = message;
        this.progress = progress;
    }

    public DownloadInfo() {
    }
}
