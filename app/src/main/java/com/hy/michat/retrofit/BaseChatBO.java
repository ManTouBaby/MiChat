package com.hy.michat.retrofit;

/**
 * @author:MtBaby
 * @date:2020/05/13 16:28
 * @desc:
 */
public class BaseChatBO<T> {

    /**
     * msg : 操作成功
     * success : true
     * data : [{"groupId":"0001","userId":"test"},{"groupId":"0001","userId":"testtest"}]
     */

    private String msg;
    private boolean success;
    private T data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


}
