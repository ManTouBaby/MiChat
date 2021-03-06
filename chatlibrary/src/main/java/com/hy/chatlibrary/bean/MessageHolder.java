package com.hy.chatlibrary.bean;

import android.arch.persistence.room.Ignore;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * @author:MtBaby
 * @date:2020/04/19 10:50
 * @desc:
 */
public class MessageHolder implements Serializable {

    //信息持有者
    private String id;//消息发送人的唯一Id
    private String name;//消息发送人名称
    private String groupName;//消息发送人群聊名称
    private String departId;//消息发送人所在单位ID
    private String departName;//消息发送人所在单位名称
    private String duty;//消息发送人职务
    private String portrait;//消息发送人头像
    private String mobile;//移动电话
    private int    gender;//性别 男:0  女:1
    private int    role;//角色  1:领导  2:其他普通成员

    @Ignore
    @JSONField(serialize = false)
    private boolean isSelect;

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartId() {
        return departId;
    }

    public void setDepartId(String departId) {
        this.departId = departId;
    }

    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
