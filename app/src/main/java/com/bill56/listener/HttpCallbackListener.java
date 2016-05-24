package com.bill56.listener;

/**
 * Created by Bill56 on 2016/5/11.
 */
public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);

}
