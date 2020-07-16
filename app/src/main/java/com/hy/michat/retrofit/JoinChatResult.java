package com.hy.michat.retrofit;

public class JoinChatResult {

    /**
     * userInfo : {"groupId":"0","userId":"test0","addFlag":true,"addErrorMsg":null}
     * addGroupMsg : 发送加群通知成功
     */

    private UserInfoBean userInfo;
    private String addGroupMsg;

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    public String getAddGroupMsg() {
        return addGroupMsg;
    }

    public void setAddGroupMsg(String addGroupMsg) {
        this.addGroupMsg = addGroupMsg;
    }

    public static class UserInfoBean {
        /**
         * groupId : 0
         * userId : test0
         * addFlag : true
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
}
