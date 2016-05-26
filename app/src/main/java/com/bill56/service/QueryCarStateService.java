package com.bill56.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.bill56.activity.NotificationDetailActivity;
import com.bill56.carlife.MainActivity;
import com.bill56.carlife.R;
import com.bill56.entity.UserCar;
import com.bill56.listener.HttpCallbackListener;
import com.bill56.util.HttpUtil;
import com.bill56.util.JSONUtil;
import com.bill56.util.LogUtil;
import com.bill56.util.NotificationUtil;

import java.util.List;

/**
 * Created by Bill56 on 2016/5/25.
 */
public class QueryCarStateService extends Service {

    // 用户id
    private int userId;
    // 用户名
    private String userName;
    // 服务器响应成功的标志
    private final int RESPONSE_SUCCESS = 9001;
    // 服务器响应失败的标志
    private final int RESPONSE_FAILURE = 9002;
    // 表示是否发过车辆维护通知了
    private boolean isCarStateNotifi = false;
    // 表示是否发过油量低的通知了
    private boolean isCarOilNotifi = false;
    // 表示是否发过里程多的通知了
    private boolean isCarMileageNotifi = false;

    private Handler carStateHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RESPONSE_SUCCESS:
                    String queryResponse = (String) msg.obj;
                    doQueryResponseSuccess(queryResponse);
                    break;
                case RESPONSE_FAILURE:
                    String failureText = (String) msg.obj;
                    LogUtil.d(LogUtil.TAG, failureText);
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
            checkCarState(userCars);
            // 循环车辆列表，查看油量是否低于20%
            checkCarOilPercent(userCars);
            // 循环车辆列表，查看里程是否为15000的倍数
            checkCarMileage(userCars);
        }
    }

    /**
     * 检车车辆状态
     *
     * @param userCars 车辆列表对象
     */
    private void checkCarState(List<UserCar> userCars) {
        StringBuilder carStateText = new StringBuilder();
        // 正常的标志
        boolean isNormal = true;
        for (UserCar car : userCars) {
            // 当发动机，变速器和车灯有一项不正常，并且没有发过通知的时候执行
            if ((!"正常".equals(car.getCarEngineState())
                    || !"正常".equals(car.getCarTransState())
                    || !"正常".equals(car.getCarLightState()))
                    && !isCarStateNotifi) {
                // 判断哪个状态需要装入通知具体内容
                carStateText.append("检测到您车牌为【" + car.getCarLicence() + "】的爱车有如下问题：\n");
                if (!"正常".equals(car.getCarEngineState())) {
                    carStateText.append("发动机：" + car.getCarEngineState() + "\n");
                }
                if (!"正常".equals(car.getCarTransState())) {
                    carStateText.append("变速器：" + car.getCarTransState() + "\n");
                }
                if (!"正常".equals(car.getCarLightState())) {
                    carStateText.append("车灯：" + car.getCarLightState() + "\n");
                }
                // 修改标志
                isNormal = false;
            }
        }
        // 当车辆不正常的时候且没发过通知的时候发通知
        if (!isNormal && !isCarStateNotifi) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("亲爱的" + userName)
                            .setContentText("检测到您的爱车似乎有些问题，请点击查看!")
                            .setWhen(System.currentTimeMillis())
                            .setDefaults(NotificationCompat.DEFAULT_ALL);
            // 创建意图
            Intent resultIntent = new Intent(this, NotificationDetailActivity.class);
            resultIntent.putExtra("notifiContent", carStateText.toString());
            resultIntent.putExtra("notifiTime", System.currentTimeMillis());
            // 通过TaskStackBuilder创建PendingIntent对象
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(NotificationDetailActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            NotificationUtil.CAR_STATE,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(NotificationUtil.CAR_STATE, mBuilder.build());
            // 修改标志位，表示已发过通知
            isCarStateNotifi = true;
        }
    }

    /**
     * 检车车辆油耗是否低于20%
     *
     * @param userCars
     */
    private void checkCarOilPercent(List<UserCar> userCars) {
        StringBuilder carStateText = new StringBuilder();
        // 正常的标志
        boolean isAbove20 = true;
        for (UserCar car : userCars) {
            // 计算油耗比
            int carOilTotal = car.getCarOilTotal();
            int carOilRest = car.getCarOilRest();
            int percentInt = carOilRest * 100 / carOilTotal;
            // 油量是否低于20%
            if (percentInt <= 20) {
                // 判断哪个状态需要装入通知具体内容
                carStateText.append("检测到您车牌为【" + car.getCarLicence() + "】的爱车油量过低：\n");
                carStateText.append("剩余油量：" + carOilRest + "L\n");
                // 修改标志
                isAbove20 = false;
            }

        }
        // 当车辆不正常的时候且没发过通知的时候发通知
        if (!isAbove20 && !isCarOilNotifi) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("亲爱的" + userName)
                            .setContentText("检测到您爱车的油量过低，请点击查看!")
                            .setWhen(System.currentTimeMillis())
                            .setDefaults(NotificationCompat.DEFAULT_ALL);
            // 创建意图
            Intent resultIntent = new Intent(this, NotificationDetailActivity.class);
            resultIntent.putExtra("notifiContent", carStateText.toString());
            resultIntent.putExtra("notifiTime", System.currentTimeMillis());
            // 通过TaskStackBuilder创建PendingIntent对象
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(NotificationDetailActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            NotificationUtil.CAR_OIL,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(NotificationUtil.CAR_OIL, mBuilder.build());
            // 修改标志位，表示已发过通知
            isCarOilNotifi = true;
        }
    }

    /**
     * 检测车辆里程是否为15000的倍数
     *
     * @param userCars 车辆列表
     */
    private void checkCarMileage(List<UserCar> userCars) {
        StringBuilder carStateText = new StringBuilder();
        // 正常的标志
        boolean isMod = true;
        for (UserCar car : userCars) {
            // 计算里程
            int carMileage = car.getCarMileage();
            int carMileageMod = carMileage % 15000;
            // 里程是否为15000的倍数
            if (carMileageMod == 0) {
                // 判断哪个状态需要装入通知具体内容
                carStateText.append("检测到您车牌为【" + car.getCarLicence() + "】的爱车要保养啦：\n");
                carStateText.append("行驶里程：" + carMileage + "公里\n");
                // 修改标志
                isMod = false;
            }

        }
        // 当车辆不正常的时候且没发过通知的时候发通知
        if (!isMod && !isCarMileageNotifi) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("亲爱的" + userName)
                            .setContentText("检测到您爱车要保养了，请点击查看!")
                            .setWhen(System.currentTimeMillis())
                            .setDefaults(NotificationCompat.DEFAULT_ALL);
            // 创建意图
            Intent resultIntent = new Intent(this, NotificationDetailActivity.class);
            resultIntent.putExtra("notifiContent", carStateText.toString());
            resultIntent.putExtra("notifiTime", System.currentTimeMillis());
            // 通过TaskStackBuilder创建PendingIntent对象
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(NotificationDetailActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            NotificationUtil.CAR_MILE_AGE,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(NotificationUtil.CAR_MILE_AGE, mBuilder.build());
            // 修改标志位，表示已发过通知
            isCarMileageNotifi = true;
        }
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
        userName = intent.getStringExtra("userName");
        // 当用户id大于0，并且userName不为空的时候的时候开启服务
        if (userId > 0 && userName != null) {
            HttpUtil.sendHttpRequestToInnerForCircle(this, HttpUtil.REQUEST_QUERY_CAR,
                    JSONUtil.createQueryUserCar(userId), new HttpCallbackListener() {

                        @Override
                        public void onFinish(String response) {
                            Message message = new Message();
                            message.what = RESPONSE_SUCCESS;
                            message.obj = response;
                            carStateHander.sendMessage(message);
                        }

                        @Override
                        public void onError(Exception e) {
                            Message message = new Message();
                            message.what = RESPONSE_FAILURE;
                            message.obj = "服务器异常";
                            carStateHander.sendMessage(message);
                        }
                    });
        }
        return super.onStartCommand(intent, flags, startId);
    }

}
