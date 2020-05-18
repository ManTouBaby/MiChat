package com.hy.michat.retrofit;

/**
 * @author:MtBaby
 * @date:2020/04/22 16:42
 * @desc:
 */
public class BaseResult {

    /**
     * flag : true
     * code : 200
     * msg : 上传成功
     * costTime : 2
     * data : {"id":"d2f6fc38-0941-449c-9467-68f8a9871990","path":"2020/04/22/d2f6fc38-0941-449c-9467-68f8a9871990.JPG","fileName":"IMG_20200302_163434.jpg","fileType":"application/octet-stream","afterFileId":"b43b39bc-99ea-44f4-a314-19887a79b887","fileSize":"3263849","longTime":null,"fileBytes":null,"bucketName":"image","suffix":"JPG"}
     * allSuccess : true
     */

    private boolean flag;
    private int code;
    private String msg;
    private String costTime;
    private FileBean data;
    private boolean allSuccess;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCostTime() {
        return costTime;
    }

    public void setCostTime(String costTime) {
        this.costTime = costTime;
    }

    public FileBean getData() {
        return data;
    }

    public void setData(FileBean data) {
        this.data = data;
    }

    public boolean isAllSuccess() {
        return allSuccess;
    }

    public void setAllSuccess(boolean allSuccess) {
        this.allSuccess = allSuccess;
    }


}
