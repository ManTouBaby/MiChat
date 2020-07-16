package com.hy.michat.retrofit;

public class GroupBo {

    /**
     * groupId : 123456
     * userId : test
     * addFlag : false
     * addErrorMsg : null
     */

    private String groupId;
    private String userId;
    private boolean addFlag;
    private Object addErrorMsg;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isAddFlag() {
        return addFlag;
    }

    public void setAddFlag(boolean addFlag) {
        this.addFlag = addFlag;
    }

    public Object getAddErrorMsg() {
        return addErrorMsg;
    }

    public void setAddErrorMsg(Object addErrorMsg) {
        this.addErrorMsg = addErrorMsg;
    }
}
