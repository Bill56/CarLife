package com.bill56.util;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.bill56.activity.BaseActivity;
import com.bill56.activity.CarInfoActivity;
import com.bill56.activity.MapSearchActivity;
import com.bill56.activity.WeiZhangQueryActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bill56 on 2016/5/14.
 */
public class ActivityUtil {

    // 当前程序的所有活动
    public static List<BaseActivity> activities = new ArrayList<>();

    /**
     * 添加活动
     *
     * @param activity 当前添加的活动
     */
    public static void addActivity(BaseActivity activity) {
        activities.add(activity);
    }

    /**
     * 移除活动
     *
     * @param activity 当前移除的活动
     */
    public static void removeActivity(BaseActivity activity) {
        activities.remove(activity);
    }

    /**
     * 销毁所有活动
     */
    public static void finishAll() {
        for (BaseActivity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 销毁除参数外的所有活动
     *
     * @param activity 不希望被销毁的活动
     */
    public static void finishExcept(BaseActivity activity) {
        for (BaseActivity ba : activities) {
            if (!ba.isFinishing() && ba != activity) {
                ba.finish();
            }
        }
    }

    /**
     * 启动MapSearchActivity的方法
     *
     * @param context    上下文环境
     * @param longtitude 经度
     * @param latitude   纬度
     */
    public static void startMapSearchActivity(Context context, double longtitude, double latitude) {
        // 创建Intent对象
        Intent mapSearchIntent = new Intent(context, MapSearchActivity.class);
        // 存放参数
        mapSearchIntent.putExtra("currentLong", longtitude);
        mapSearchIntent.putExtra("currentLat", latitude);
        // 启动活动
        context.startActivity(mapSearchIntent);
    }

    /**
     * 启动MapSearchActivity的方法
     *
     * @param context       上下文环境
     * @param longtitude    经度
     * @param latitude      纬度
     * @param desLongtitude 目的地精度
     * @param desLatitude   目的地纬度
     * @param desAddress    目的地点名称
     */
    public static void startMapSearchActivity(Context context,
                                              double longtitude, double latitude,
                                              double desLongtitude, double desLatitude,
                                              String desAddress) {
        // 创建Intent对象
        Intent mapSearchIntent = new Intent(context, MapSearchActivity.class);
        // 存放参数
        mapSearchIntent.putExtra("currentLong", longtitude);
        mapSearchIntent.putExtra("currentLat", latitude);
        mapSearchIntent.putExtra("desLong", desLongtitude);
        mapSearchIntent.putExtra("desLat", desLatitude);
        mapSearchIntent.putExtra("desAddress", desAddress);
        // 启动活动
        context.startActivity(mapSearchIntent);
    }

    /**
     * 启动WeiZhangQueryActivity的方法
     *
     * @param context     上下文环境
     * @param carLicence  车牌号
     * @param carEngineNo 发动机号
     */
    public static void startWeiZhangQueryActivity(Context context,
                                                  String carLicence,
                                                  String carEngineNo) {
        // 创建Intent对象
        Intent weiZhangQueryIntent = new Intent(context, WeiZhangQueryActivity.class);
        carLicence = subEndString(carLicence,6);
        carEngineNo = subEndString(carEngineNo,6);
        // 存放参数
        weiZhangQueryIntent.putExtra("carLicence", carLicence);
        weiZhangQueryIntent.putExtra("carEngineNo", carEngineNo);
        // 启动活动
        context.startActivity(weiZhangQueryIntent);
    }

    /**
     * 启动车辆信息CarInfoActivity的方法
     *
     * @param context         上下文环境
     * @param userId          用户id
     * @param isQueryWeiZhang 是否可以进行违章查询
     */
    public static void startCarInfoActivity(Context context, int userId, boolean isQueryWeiZhang) {
        Intent carInfoIntent = new Intent(context, CarInfoActivity.class);
        carInfoIntent.putExtra("userId", userId);
        carInfoIntent.putExtra("isQueryWeiZhang", isQueryWeiZhang);
        context.startActivity(carInfoIntent);
    }

    /**
     * 获得字符串后面number位子串
     *
     * @param number 子串个数
     * @return 截取后的子串
     */
    private static String subEndString(String str, int number) {
        int length = str.length();
        String newStr;
        if (length >= number) {
            newStr = str.substring(length - number);
        } else {
            newStr = str;
        }
        return newStr;
    }

}
