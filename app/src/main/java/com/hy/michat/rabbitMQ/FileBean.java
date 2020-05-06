package com.hy.michat.rabbitMQ;

/**
 * @author:MtBaby
 * @date:2020/04/22 16:43
 * @desc:
 */
public class FileBean {
    /**
     * id : d2f6fc38-0941-449c-9467-68f8a9871990
     * path : 2020/04/22/d2f6fc38-0941-449c-9467-68f8a9871990.JPG
     * fileName : IMG_20200302_163434.jpg
     * fileType : application/octet-stream
     * afterFileId : b43b39bc-99ea-44f4-a314-19887a79b887
     * fileSize : 3263849
     * longTime : null
     * fileBytes : null
     * bucketName : image
     * suffix : JPG
     */

    private String id;
    private String path;
    private String fileName;
    private String fileType;
    private String afterFileId;
    private String fileSize;
    private Object longTime;
    private Object fileBytes;
    private String bucketName;
    private String suffix;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getAfterFileId() {
        return afterFileId;
    }

    public void setAfterFileId(String afterFileId) {
        this.afterFileId = afterFileId;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public Object getLongTime() {
        return longTime;
    }

    public void setLongTime(Object longTime) {
        this.longTime = longTime;
    }

    public Object getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(Object fileBytes) {
        this.fileBytes = fileBytes;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
