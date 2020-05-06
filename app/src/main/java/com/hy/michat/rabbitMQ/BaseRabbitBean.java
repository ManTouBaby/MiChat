package com.hy.michat.rabbitMQ;

import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/04/21 18:32
 * @desc:
 */
public class BaseRabbitBean {

    /**
     * branchProcessStep : [{"processId":"msg-send-process"}]
     * creater : firstStep
     * currServer : 未命名
     * currentServerId : testtest
     * currentServerName : 消息群组分发服务
     * currentStep : 2
     * errorProcess : []
     * ifOffline : 0
     * logProcess : []
     * msgCategory : default
     * msgId : 1121119534
     * msgSend : {"data":{"duration":0,"fileSize":0,"holderDepartId":"111111","holderDepartName":"黑猫典狱","holderDuty":"典狱长","holderGender":0,"holderId":"test","holderMobile":"13829793334","holderName":"test","holderPortrait":"http://i0.hdslb.com/bfs/article/8e87829cde9559c8407892aa6110f83a4631c6b3.jpg","itemType":0,"latitude":0,"longitude":0,"messageCT":"2020-04-21 18:31:20","messageCTMillis":1587465080000,"messageContent":"旅途愉快","messageGroupId":"0001","messageGroupName":"测试聊天群","messageHolder":0,"messageId":"1121119534","messageLatitude":0,"messageLongitude":0,"messageST":"2020-04-21 18:31:20","messageSTMillis":1587465080000,"messageStatus":2,"player":false},"dataType":"jstx","groupId":"0001","ifRead":"1","msgFrom":"test","type":"0"}
     * process : [{"createTime":1587365064000,"creator":"firstStep","executeOrder":1,"listBranchProcess":[],"processId":"msg-send-process","serverId":"filter","serverName":"过滤服务","updateTime":1587365064000,"updater":"firstStep"},{"createTime":1587365064000,"creator":"firstStep","executeOrder":2,"listBranchProcess":[],"processId":"msg-send-process","serverId":"send","serverName":"消息群组分发服务","updateTime":1587365064000,"updater":"firstStep"}]
     * processId : msg-send-process
     * sendTime : 1587465080000
     */

    private String creater;
    private String currServer;
    private String currentServerId;
    private String currentServerName;
    private int currentStep;
    private String ifOffline;
    private String msgCategory;
    private String msgId;
    private MsgBean msgSend;
    private String processId;
    private long sendTime;
    private List<BranchProcessStepBean> branchProcessStep;
    private List<?> errorProcess;
    private List<?> logProcess;
    private List<ProcessBean> process;

    public MsgBean getMsgSend() {
        return msgSend;
    }

    public void setMsgSend(MsgBean msgSend) {
        this.msgSend = msgSend;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getCurrServer() {
        return currServer;
    }

    public void setCurrServer(String currServer) {
        this.currServer = currServer;
    }

    public String getCurrentServerId() {
        return currentServerId;
    }

    public void setCurrentServerId(String currentServerId) {
        this.currentServerId = currentServerId;
    }

    public String getCurrentServerName() {
        return currentServerName;
    }

    public void setCurrentServerName(String currentServerName) {
        this.currentServerName = currentServerName;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public String getIfOffline() {
        return ifOffline;
    }

    public void setIfOffline(String ifOffline) {
        this.ifOffline = ifOffline;
    }

    public String getMsgCategory() {
        return msgCategory;
    }

    public void setMsgCategory(String msgCategory) {
        this.msgCategory = msgCategory;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }



    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public List<BranchProcessStepBean> getBranchProcessStep() {
        return branchProcessStep;
    }

    public void setBranchProcessStep(List<BranchProcessStepBean> branchProcessStep) {
        this.branchProcessStep = branchProcessStep;
    }

    public List<?> getErrorProcess() {
        return errorProcess;
    }

    public void setErrorProcess(List<?> errorProcess) {
        this.errorProcess = errorProcess;
    }

    public List<?> getLogProcess() {
        return logProcess;
    }

    public void setLogProcess(List<?> logProcess) {
        this.logProcess = logProcess;
    }

    public List<ProcessBean> getProcess() {
        return process;
    }

    public void setProcess(List<ProcessBean> process) {
        this.process = process;
    }



    public static class BranchProcessStepBean {
        /**
         * processId : msg-send-process
         */

        private String processId;

        public String getProcessId() {
            return processId;
        }

        public void setProcessId(String processId) {
            this.processId = processId;
        }
    }

    public static class ProcessBean {
        /**
         * createTime : 1587365064000
         * creator : firstStep
         * executeOrder : 1
         * listBranchProcess : []
         * processId : msg-send-process
         * serverId : filter
         * serverName : 过滤服务
         * updateTime : 1587365064000
         * updater : firstStep
         */

        private long createTime;
        private String creator;
        private int executeOrder;
        private String processId;
        private String serverId;
        private String serverName;
        private long updateTime;
        private String updater;
        private List<?> listBranchProcess;

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public String getCreator() {
            return creator;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }

        public int getExecuteOrder() {
            return executeOrder;
        }

        public void setExecuteOrder(int executeOrder) {
            this.executeOrder = executeOrder;
        }

        public String getProcessId() {
            return processId;
        }

        public void setProcessId(String processId) {
            this.processId = processId;
        }

        public String getServerId() {
            return serverId;
        }

        public void setServerId(String serverId) {
            this.serverId = serverId;
        }

        public String getServerName() {
            return serverName;
        }

        public void setServerName(String serverName) {
            this.serverName = serverName;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public String getUpdater() {
            return updater;
        }

        public void setUpdater(String updater) {
            this.updater = updater;
        }

        public List<?> getListBranchProcess() {
            return listBranchProcess;
        }

        public void setListBranchProcess(List<?> listBranchProcess) {
            this.listBranchProcess = listBranchProcess;
        }
    }
}
