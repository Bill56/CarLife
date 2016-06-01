package com.bill56.entity;

/**
 * 关于类的实体
 * Created by 何子洋 on 2016/5/10.
 */
public class About {
    private String item;
    private int iamgeId;

    public About(String item, int iamgeId) {
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
