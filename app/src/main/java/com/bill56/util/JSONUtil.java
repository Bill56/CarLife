package com.bill56.util;


import com.bill56.entity.User;
import com.bill56.entity.UserCar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bill56 on 2016/5/14.
 */
public class JSONUtil {

    /**
     * 创建用户登录和注册时候的Json数据
     *
     * @param userTel 用户手机
     * @param userPwd 用户密码
     * @return json数据
     */
    public static String createUserJSON(String userTel, String userPwd) {
        String jsonInfo = new String();
        // 创建json格式的数据对象，该对象是一个包含n个json数据对象的集合
        try {
            JSONArray jsonArray = new JSONArray();
            // 创建一个json类，对应User对象
            JSONObject jsonUser = new JSONObject();
            // 将user中每个字段的值放入jsonUser中
            jsonUser.put("userTel", userTel);
            jsonUser.put("userPwd", userPwd);
            // 将jsonUser放入jsonArray
            jsonArray.put(jsonUser);
            // 将jsonArray编程json字符串
            jsonInfo = jsonArray.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        LogUtil.d(LogUtil.TAG, jsonInfo);
        return jsonInfo;
    }

    /**
     * 解析响应的json数据
     *
     * @param jsonResponse 服务器响应的数据
     * @return 封装数据的User对象
     */
    public static User parseUserJSON(String jsonResponse) {
        // 根据解析的值创建一个User对象
        User user = new User();
        try {
            // 将json字符串转成jsonArray对象
            JSONArray jsonArray = new JSONArray(jsonResponse);
            // 获取一个json数据对象
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            // 先去userResult，如果为true说明登录成功，否则登录失败
            int userResult = jsonObject.getInt("userResult");
            LogUtil.d(LogUtil.TAG, "执行完了userResult,值为:" + String.valueOf(userResult));
            if (userResult > 0) {
                // 获取对应的值
                int userId = jsonObject.getInt("userId");
                String userNick = jsonObject.getString("userNick");
                String userSex = jsonObject.getString("userSex");
                String userEmail = jsonObject.getString("userEmail");
                String userCreateTime = jsonObject.getString("userCreateTime");
                // 将数据传递给User对象
                user.setUserId(userId);
                user.setUserNick(userNick);
                user.setUserSex(userSex);
                user.setUserEmail(userEmail);
                user.setUserCreateTime(userCreateTime);
                LogUtil.d(LogUtil.TAG, user.toString());
            } else {
                user = null;
            }
        } catch (JSONException e) {
            LogUtil.e(LogUtil.TAG, e.getMessage());
            user = null;
        }
        return user;
    }

    /**
     * 修改个人信息时候，访问服务器时候的Json数据
     *
     * @param userId      用户id
     * @param updateField 要修改的字段
     * @param userUpdate  要修改的值
     * @return json数据
     */
    public static String createUpdateJSON(int userId, String updateField, String userUpdate) {
        String jsonInfo = new String();
        // 创建json格式的数据对象，该对象是一个包含n个json数据对象的集合
        try {
            JSONArray jsonArray = new JSONArray();
            // 创建一个json类，对应User对象
            JSONObject jsonUser = new JSONObject();
            // 将user中每个字段的值放入jsonUser中
            jsonUser.put("userId", userId);
            // 根据传入的字段来创建数据
            switch (updateField) {
                case "userNick":
                    jsonUser.put("userNick", userUpdate);
                    break;
                case "userTel":
                    jsonUser.put("userTel", userUpdate);
                    break;
                case "userEmail":
                    jsonUser.put("userEmail", userUpdate);
                    break;
                case "userSex":
                    jsonUser.put("userSex", userUpdate);
                    break;
                case "userPwd":
                    jsonUser.put("userPwd", userUpdate);
                    break;
            }
            // 将jsonUser放入jsonArray
            jsonArray.put(jsonUser);
            // 将jsonArray编程json字符串
            jsonInfo = jsonArray.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        LogUtil.d(LogUtil.TAG, jsonInfo);
        return jsonInfo;
    }

    /**
     * 解析修改的信息，只接收一个参数，整数表示修改成功，负数表示修改失败
     *
     * @param jsonResponse 服务器响应的json数据
     * @return 修改结果
     */
    public static int parseUpdateJSON(String jsonResponse) {
        // 保存解析的数据
        int userResult = -1;
        try {
            // 将json字符串转成jsonArray对象
            JSONArray jsonArray = new JSONArray(jsonResponse);
            // 获取一个json数据对象
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            // 先去userResult，如果为正数说明修改成功，否则修改失败
            userResult = jsonObject.getInt("userResult");
            LogUtil.d(LogUtil.TAG, "执行完了userResult,值为:" + String.valueOf(userResult));
        } catch (JSONException e) {
            LogUtil.e(LogUtil.TAG, e.getMessage());
            userResult = -1;
        }
        return userResult;
    }

    /**
     * 从二维码数据中解析出车牌号
     *
     * @param jsonData 二维码json数据
     * @return 车牌号
     */
    public static String parseCarLicenceFromJSON(String jsonData) {
        // 保存解析的车牌
        String carLicence = null;
        try {
            // 将json字符串转成jsonArray对象
            JSONArray jsonArray = new JSONArray(jsonData);
            // 获取一个json数据对象
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            // 先去userResult，如果为正数说明修改成功，否则修改失败
            carLicence = jsonObject.getString("carLicence");
            LogUtil.d(LogUtil.TAG, "执行完了userResult,值为:" + carLicence);
        } catch (JSONException e) {
            carLicence = null;
        }
        return carLicence;
    }

    /**
     * 解析从二维码读出的车辆信息
     *
     * @param jsonData json数据
     * @return 封装了车辆信息的UserCar对象
     */
    public static UserCar parseUserCarJSONFromQRCode(String jsonData) {
        // 根据解析的值创建一个carInfo对象
        UserCar userCar = new UserCar();
        try {
            // 将json字符串转成jsonArray对象
            JSONArray jsonArray = new JSONArray(jsonData);
            // 获取一个json数据对象
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            // 获取对应的值
            String carBrand = jsonObject.getString("carBrand");
            String carVersion = jsonObject.getString("carVersion");
            String carLevel = jsonObject.getString("carLevel");
            String carEngineNo = jsonObject.getString("carEngineNo");
            String carLicence = jsonObject.getString("carLicence");
            String carEngineState = jsonObject.getString("carEngineState");
            String carLightState = jsonObject.getString("carLightState");
            String carTransState = jsonObject.getString("carTransState");
            int carOilRest = jsonObject.getInt("carOilRest");
            int carOilTotal = jsonObject.getInt("carOilTotal");
            int carMileage = jsonObject.getInt("carMileage");
            String carVim = jsonObject.getString("carVim");
            // 将数据传递给userCar对象
            userCar.setCarBrand(carBrand);
            userCar.setCarVersion(carVersion);
            userCar.setCarLevel(carLevel);
            userCar.setCarEngineNo(carEngineNo);
            userCar.setCarLicence(carLicence);
            userCar.setCarEngineState(carEngineState);
            userCar.setCarLightState(carLightState);
            userCar.setCarTransState(carTransState);
            userCar.setCarOilRest(carOilRest);
            userCar.setCarOilTotal(carOilTotal);
            userCar.setCarMileage(carMileage);
            userCar.setCarVim(carVim);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            userCar = null;
        }
        return userCar;
    }

    /**
     * 创建用于传输的userCar的json数据
     *
     * @param userCar 封装了用户车辆信息的对象
     * @return 传输的json数据
     */
    public static String createUserCarJSON(UserCar userCar) {
        String jsonInfo = new String();
        // 创建json格式的数据对象，该对象是一个包含n个json数据对象的集合
        try {
            JSONArray jsonArray = new JSONArray();
            // 创建一个json类，对应用户车辆对象
            JSONObject jsonUserCar = new JSONObject();
            // 保存用户和车辆信息
            jsonUserCar.put("userId", userCar.getUserId());
            jsonUserCar.put("carBrand", userCar.getCarBrand());
            jsonUserCar.put("carVersion", userCar.getCarVersion());
            jsonUserCar.put("carLevel", userCar.getCarLevel());
            jsonUserCar.put("carEngineNo", userCar.getCarEngineNo());
            jsonUserCar.put("carLicence", userCar.getCarLicence());
            jsonUserCar.put("carEngineState", userCar.getCarEngineState());
            jsonUserCar.put("carTransState", userCar.getCarTransState());
            jsonUserCar.put("carLightState", userCar.getCarLightState());
            jsonUserCar.put("carOilRest", userCar.getCarOilRest());
            jsonUserCar.put("carOilTotal", userCar.getCarOilTotal());
            jsonUserCar.put("carMileage", userCar.getCarMileage());
            jsonUserCar.put("carVim", userCar.getCarVim());
            // 将jsonUser放入jsonArray
            jsonArray.put(jsonUserCar);
            // 将jsonArray编程json字符串
            jsonInfo = jsonArray.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonInfo;
    }

    /**
     * 解析服务器发回来的json数据
     *
     * @param jsonResponse 服务器响应的数据
     * @return 封装了用户和车辆信息的对象
     */
    public static UserCar parseUserCarJSON(String jsonResponse) {
// 根据解析的值创建一个carInfo对象
        UserCar userCar = new UserCar();
        try {
            // 将json字符串转成jsonArray对象
            JSONArray jsonArray = new JSONArray(jsonResponse);
            // 获取一个json数据对象
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            // 先去userResult，如果为true说明登录成功，否则登录失败
            int bindResult = jsonObject.getInt("bindResult");
            LogUtil.d(LogUtil.TAG, "执行完了userResult,值为:" + String.valueOf(bindResult));
            if (bindResult > 0) {
                // 获取对应的值
                int carId = jsonObject.getInt("carId");
                int userId = jsonObject.getInt("userId");
                String carBrand = jsonObject.getString("carBrand");
                String carVersion = jsonObject.getString("carVersion");
                String carLevel = jsonObject.getString("carLevel");
                String carEngineNo = jsonObject.getString("carEngineNo");
                String carLicence = jsonObject.getString("carLicence");
                String carEngineState = jsonObject.getString("carEngineState");
                String carLightState = jsonObject.getString("carLightState");
                String carTransState = jsonObject.getString("carTransState");
                int carOilRest = jsonObject.getInt("carOilRest");
                int carOilTotal = jsonObject.getInt("carOilTotal");
                int carMileage = jsonObject.getInt("carMileage");
                String carVim = jsonObject.getString("carVim");
                // 将数据传递给userCar对象
                userCar.setCarId(carId);
                userCar.setUserId(userId);
                userCar.setCarBrand(carBrand);
                userCar.setCarVersion(carVersion);
                userCar.setCarLevel(carLevel);
                userCar.setCarEngineNo(carEngineNo);
                userCar.setCarLicence(carLicence);
                userCar.setCarEngineState(carEngineState);
                userCar.setCarLightState(carLightState);
                userCar.setCarTransState(carTransState);
                userCar.setCarOilRest(carOilRest);
                userCar.setCarOilTotal(carOilTotal);
                userCar.setCarMileage(carMileage);
                userCar.setCarVim(carVim);
            } else {
                userCar = null;
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            userCar = null;
        }
        return userCar;
    }

    /**
     * 根据用户id创建json数据
     *
     * @param userId 用户id
     * @return json数据
     */
    public static String createQueryUserCar(int userId) {
        String jsonInfo = new String();
        // 创建json格式的数据对象，该对象是一个包含n个json数据对象的集合
        try {
            JSONArray jsonArray = new JSONArray();
            // 创建一个json类，对应用户车辆对象
            JSONObject jsonUserCar = new JSONObject();
            // 保存用户和车辆信息
            jsonUserCar.put("userId", userId);
            // 将jsonUser放入jsonArray
            jsonArray.put(jsonUserCar);
            // 将jsonArray编程json字符串
            jsonInfo = jsonArray.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonInfo;
    }

    /**
     * 解析从服务器传递过来的json数据
     *
     * @param jsonResponse 用户绑定的车辆信息json数据
     * @return 封装了用户车辆信息的列表
     */
    public static List<UserCar> parseUserCarsJSON(String jsonResponse) {
        ArrayList<UserCar> userCars = new ArrayList<>();
        try {
            // 将json字符串转成jsonArray对象
            JSONArray jsonArray = new JSONArray(jsonResponse);
            // 循环获取json中的对象
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (i == 0) {
                    // 获取第一个对象中的queryResult，若为正数说明查询成功
                    int queryResult = jsonObject.getInt("queryResult");
                    if (queryResult <= 0) {
                        // 查询不成功，直接返回null
                        return null;
                    }
                }
                // 获取其他的数据
                UserCar userCar = new UserCar();
                userCar.setCarId(jsonObject.getInt("carId"));
                userCar.setUserId(jsonObject.getInt("userId"));
                userCar.setCarEngineNo(jsonObject.getString("carEngineNo"));
                userCar.setCarLicence(jsonObject.getString("carLicence"));
                userCar.setCarMileage(jsonObject.getInt("carMileage"));
                userCar.setCarOilTotal(jsonObject.getInt("carOilTotal"));
                userCar.setCarOilRest(jsonObject.getInt("carOilRest"));
                userCar.setCarEngineState(jsonObject.getString("carEngineState"));
                userCar.setCarTransState(jsonObject.getString("carTransState"));
                userCar.setCarLightState(jsonObject.getString("carLightState"));
                userCar.setCarVim(jsonObject.getString("carVim"));
                userCar.setCarBrand(jsonObject.getString("carBrand"));
                userCar.setCarVersion(jsonObject.getString("carVersion"));
                userCar.setCarLevel(jsonObject.getString("carLevel"));
                // 添加到列表
                userCars.add(userCar);
            }
            // 获取一个json数据对象
        } catch (JSONException e) {
            e.printStackTrace();
            userCars = null;
        }
        return userCars;
    }

    /**
     * 根据车辆识别代码生成对应的json数据
     *
     * @param carVim 车辆识别代码
     * @return json数据
     */
    public static String createDeleteCarJSON(String carVim) {
        String jsonInfo = new String();
        // 创建json格式的数据对象，该对象是一个包含n个json数据对象的集合
        try {
            JSONArray jsonArray = new JSONArray();
            // 创建一个json类，对应用户车辆对象
            JSONObject jsonUserCar = new JSONObject();
            // 保存用户和车辆信息
            jsonUserCar.put("carVim", carVim);
            // 将jsonUser放入jsonArray
            jsonArray.put(jsonUserCar);
            // 将jsonArray编程json字符串
            jsonInfo = jsonArray.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonInfo;
    }

    /**
     * 根据服务器返回的数据解析json数据，获取删除结果
     *
     * @param jsonResponse 服务器数据
     * @return 删除结果，大于0表示成功，否则失败
     */
    public static int parseDeleteCarJSON(String jsonResponse) {
        int deleteResult = 0;
        try {
            // 将json字符串转成jsonArray对象
            JSONArray jsonArray = new JSONArray(jsonResponse);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            deleteResult = jsonObject.getInt("deleteResult");
        } catch (JSONException e) {
            deleteResult = 0;
        }
        return deleteResult;
    }

}
