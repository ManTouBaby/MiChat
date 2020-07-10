package com.hy.chatlibrary.utils.retrofit.down;

public class DownInfo {
    /*存储位置*/
    private String savePath;
    /*下载url*/
    private String url;
    /*基础url*/
    private String baseUrl;
    /*文件总长度*/
    private long countLength;
    /*下载长度*/
    private long readLength;
    /*下载唯一的HttpService*/
    private HttpService service;
    /*回调监听*/
    private HttpProgressOnNextListener listener;
    /*超时设置*/
    private int DEFAULT_TIMEOUT = 6;
    /*下载状态*/
    private DownState state;

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public long getCountLength() {
        return countLength;
    }

    public void setCountLength(long countLength) {
        this.countLength = countLength;
    }

    public long getReadLength() {
        return readLength;
    }

    public void setReadLength(long readLength) {
        this.readLength = readLength;
    }

    public HttpService getService() {
        return service;
    }

    public void setService(HttpService service) {
        this.service = service;
    }

    public HttpProgressOnNextListener getListener() {
        return listener;
    }

    public void setListener(HttpProgressOnNextListener listener) {
        this.listener = listener;
    }

    public int getDEFAULT_TIMEOUT() {
        return DEFAULT_TIMEOUT;
    }

    public void setDEFAULT_TIMEOUT(int DEFAULT_TIMEOUT) {
        this.DEFAULT_TIMEOUT = DEFAULT_TIMEOUT;
    }

    public DownState getState() {
        return state;
    }

    public void setState(DownState state) {
        this.state = state;
    }
}
