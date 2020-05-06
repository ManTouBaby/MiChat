package com.hy.chatlibrary.bean;

/**
 * @author:MtBaby
 * @date:2020/05/06 12:04
 * @desc:
 */
public class ControlTypeBean {
    private int controlTypeIndex;
    private String controlTypeName;
    private boolean isShow;

    public ControlTypeBean(int controlTypeIndex, String controlTypeName, boolean isShow) {
        this.controlTypeIndex = controlTypeIndex;
        this.controlTypeName = controlTypeName;
        this.isShow = isShow;
    }

    public int getControlTypeIndex() {
        return controlTypeIndex;
    }

    public String getControlTypeName() {
        return controlTypeName;
    }

    public boolean isShow() {
        return isShow;
    }
}
