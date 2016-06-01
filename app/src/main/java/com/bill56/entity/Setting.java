package com.bill56.entity;

/**
 * 设置界面的项目封装类
 * Created by 何子洋 on 2016/5/10.
 */
public class Setting {
    private String item;
    private int iamgeId;

    public Setting(String item, int iamgeId) {
        this.item = item;
        this.iamgeId = iamgeId;
    }

    public String getItem() {
        return item;
    }

    public int getIamgeId() {
        return iamgeId;
    }
}
