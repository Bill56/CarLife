package com.bill56.entity;

import java.sql.Timestamp;

/**
 * 用户类，保存用户的一系列属性
 *
 * @author Bill56
 */
public class User {

    // 用户id
    private int userId;
    // 用户手机号码
    private String userTel;
    // 用户密码
    private String userPwd;
    // 用户昵称
    private String userNick;
    // 用户性别
    private String userSex;
    // 用户邮箱
    private String userEmail;
    // 用户创建（注册）时间
    private String userCreateTime;

    // 对应的get与set方法，其中Id与创建时间不能set
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserTel() {
        return userTel;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserCreateTime() {
        return userCreateTime;
    }

    public void setUserCreateTime(String userCreateTime) {
        this.userCreateTime = userCreateTime;
    }

}