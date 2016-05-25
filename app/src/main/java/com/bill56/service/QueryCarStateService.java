package com.bill56.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.bill56.entity.UserCar;
import com.bill56.listener.HttpCallbackListener;
import com.bill56.util.HttpUtil;
import com.bill56.util.JSONUtil;
import com.bill56.util.LogUtil;
import com.bill56.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bill56 on 2016/5/25.
 */
public class QueryCarStateService extends Service {

    // 用户id
    private int userId;
    // 服务器响应成功的标志
    private final int RESPONSE_SUCCESS = 9001;
    // 服务器响应失败的标志
    private final int RESPONSE_FAILURE = 9002;

    private Handler carStateHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RESPONSE_SUCCESS:
                    String queryResponse = (String) msg.obj;
                    doQueryResponseSuccess(queryResponse);
                    break;
                case RESPONSE_FAILURE:
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 处理服务器响应成功的查询数据
     *
     * @param queryResponse 服务器返回的数据
     */
    private void doQueryResponseSuccess(String queryResponse) {
        List<UserCar> userCars = JSONUtil.parseUserCarsJSON(queryResponse);
        // 说明解析数据成功
        if (userCars != null) {
            // 循环车辆列表，查看是否有不正常的状态
            for (UserCar car : userCars) {
                checkCarState(car);
            }
        }
    }

    /**
     * 检车车辆状态
     *
     * @param car 车辆对象
     */
    private void checkCarState(UserCar car) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(LogUtil.TAG, "Service onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(LogUtil.TAG, "Service onStartCommand");
        // 获得用户id
        userId = intent.getIntExtra("userId", 0);
        // 当用户id大于0的时候开启服务
        if (userId > 0) {
            HttpUtil.sendHttpRequestToInnerForCircle(this, HttpUtil.REQUEST_QUERY_CAR,
                    JSONUtil.createQueryUserCar(userId), new HttpCallbackListener() {
                        Message message = new Message();

                        @Override
                        public void onFinish(String response) {
                            message.what = RESPONSE_SUCCESS;
                            message.obj = response;
                            carStateHander.sendMessage(message);
                        }

                        @Override
                        public void onError(Exception e) {
                            message.what = RESPONSE_FAILURE;
                            message.obj = "服务器异常";
                            carStateHander.sendMessage(message);
                        }
                    });
        }
        return super.onStartCommand(intent, flags, startId);
    }

}
