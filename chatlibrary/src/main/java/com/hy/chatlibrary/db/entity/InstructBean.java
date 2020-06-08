package com.hy.chatlibrary.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.annotation.JSONField;
import com.hy.chatlibrary.bean.MessageHolder;
import com.hy.chatlibrary.db.converter.ConverterMessageHolders;

import java.io.Serializable;
import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/04/29 10:52
 * @desc:
 */
@Entity(indices = {@Index("id"), @Index("createMillis")}, tableName = "instructModel")
@TypeConverters(ConverterMessageHolders.class)
public class InstructBean implements Serializable {
    private String              title;      //指令标题
    private String              content;     //指令内容
    private List<MessageHolder> acceptors;  //接收人员信息
    private String              netFilePath;//网络地址
    private String              netThumbFilePath;//网络缩率图地址
    private long                duration;   //视频时长
    private float               fileSize;   //文件大小
    private String              fileName;   //文件名称
    private int                 replyStatus;//回复状态 0:未回复  1:已回复

    @NonNull
    @PrimaryKey
    @JSONField(serialize = false)
    private String id;
    @JSONField(serialize = false)
    private String createStr;//消息生成时间
    @JSONField(serialize = false)
    private long createMillis;//消息生成时间戳
    //    @JSONField(serialize = false)
    private String localFilePath;//本地地址
    @Ignore
    @JSONField(serialize = false)
    private boolean isOpenControl;//是否打开操作

    public String getNetThumbFilePath() {
        return netThumbFilePath;
    }

    public void setNetThumbFilePath(String netThumbFilePath) {
        this.netThumbFilePath = netThumbFilePath;
    }

    public int getReplyStatus() {
        return replyStatus;
    }

    public void setReplyStatus(int replyStatus) {
        this.replyStatus = replyStatus;
    }

    public boolean isOpenControl() {
        return isOpenControl;
    }

    public void setOpenControl(boolean openControl) {
        isOpenControl = openControl;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public float getFileSize() {
        return fileSize;
    }

    public void setFileSize(float fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getCreateStr() {
        return createStr;
    }

    public void setCreateStr(String createStr) {
        this.createStr = createStr;
    }

    public long getCreateMillis() {
        return createMillis;
    }

    public void setCreateMillis(long createMillis) {
        this.createMillis = createMillis;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<MessageHolder> getAcceptors() {
        return acceptors;
    }

    public void setAcceptors(List<MessageHolder> acceptors) {
        this.acceptors = acceptors;
    }

    public String getNetFilePath() {
        return netFilePath;
    }

    public void setNetFilePath(String netFilePath) {
        this.netFilePath = netFilePath;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }
}
