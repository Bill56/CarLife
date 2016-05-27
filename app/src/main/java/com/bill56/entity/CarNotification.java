package com.bill56.entity;

/**
 * Created by Bill56 on 2016/5/26.
 */
public class CarNotification {

    // 通知时间，作为主键
    private long notifiTime;
    // 通知标题
    private String notifiTitle;
    // 通知内容
    private String notifiContent;
    // 通知的用户
    private int userId;

    public CarNotification() {

    }

    public CarNotification(long notifiTime, String notifiTitle, String notifiContent) {
        this.notifiTime = notifiTime;
        this.notifiTitle = notifiTitle;
        this.notifiContent = notifiContent;
    }

    public long getNotifiTime() {
        return notifiTime;
    }

    public void setNotifiTime(long notifiTime) {
        this.notifiTime = notifiTime;
    }

    public String getNotifiTitle() {
        return notifiTitle;
    }

    public void setNotifiTitle(String notifiTitle) {
        this.notifiTitle = notifiTitle;
    }

    public String getNotifiContent() {
        return notifiContent;
    }

    public void setNotifiContent(String notifiContent) {
        this.notifiContent = notifiContent;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
