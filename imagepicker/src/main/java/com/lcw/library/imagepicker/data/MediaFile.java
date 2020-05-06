package com.lcw.library.imagepicker.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 媒体实体类
 * Create by: chenWei.li
 * Date: 2018/8/22
 * Time: 上午12:36
 * Email: lichenwei.me@foxmail.com
 */
public class MediaFile implements Parcelable {

    private String path;
    private String mime;
    private Integer folderId;
    private String folderName;
    private long duration;
    private long dateToken;

    protected MediaFile(Parcel in) {
        path = in.readString();
        mime = in.readString();
        if (in.readByte() == 0) {
            folderId = null;
        } else {
            folderId = in.readInt();
        }
        folderName = in.readString();
        duration = in.readLong();
        dateToken = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(mime);
        if (folderId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(folderId);
        }
        dest.writeString(folderName);
        dest.writeLong(duration);
        dest.writeLong(dateToken);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MediaFile> CREATOR = new Creator<MediaFile>() {
        @Override
        public MediaFile createFromParcel(Parcel in) {
            return new MediaFile(in);
        }

        @Override
        public MediaFile[] newArray(int size) {
            return new MediaFile[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public Integer getFolderId() {
        return folderId;
    }

    public void setFolderId(Integer folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDateToken() {
        return dateToken;
    }

    public void setDateToken(long dateToken) {
        this.dateToken = dateToken;
    }
}

