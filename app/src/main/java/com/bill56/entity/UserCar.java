package com.bill56.entity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 何子洋 on 2016/5/15.
 */
public class UserCar {

    // 汽车id
    int carId;
    // 用户id
    int userId;
    // 汽车品牌
    String carBrand;
    // 汽车型号
    String carVersion;
    // 车牌好
    String carLicence;
    // 汽车发动机号码
    String carEngineNo;
    // 车身级别
    String carLevel;
    // 发动机状态
    String carEngineState;
    // 车灯状态
    String carLightState;
    // 变速器状态
    String carTransState;
    // 剩余油量
    int carOilRest;
    // 总油量
    int carOilTotal;
    // 车辆里程
    int carMileage;
    // 识别码
    String carVim;


    public UserCar() {

    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarVersion() {
        return carVersion;
    }

    public void setCarVersion(String carVersion) {
        this.carVersion = carVersion;
    }

    public String getCarLicence() {
        return carLicence;
    }

    public void setCarLicence(String carLicence) {
        this.carLicence = carLicence;
    }

    public String getCarEngineNo() {
        return carEngineNo;
    }

    public void setCarEngineNo(String carEngineNo) {
        this.carEngineNo = carEngineNo;
    }

    public String getCarLevel() {
        return carLevel;
    }

    public void setCarLevel(String carLevel) {
        this.carLevel = carLevel;
    }

    public String getCarEngineState() {
        return carEngineState;
    }

    public void setCarEngineState(String carEngineState) {
        this.carEngineState = carEngineState;
    }

    public String getCarLightState() {
        return carLightState;
    }

    public void setCarLightState(String carLightState) {
        this.carLightState = carLightState;
    }

    public String getCarTransState() {
        return carTransState;
    }

    public void setCarTransState(String carTransState) {
        this.carTransState = carTransState;
    }

    public int getCarOilRest() {
        return carOilRest;
    }

    public void setCarOilRest(int carOilRest) {
        this.carOilRest = carOilRest;
    }

    public int getCarOilTotal() {
        return carOilTotal;
    }

    public void setCarOilTotal(int carOilTotal) {
        this.carOilTotal = carOilTotal;
    }

    public int getCarMileage() {
        return carMileage;
    }

    public void setCarMileage(int carMileage) {
        this.carMileage = carMileage;
    }

    public String getCarVim() {
        return carVim;
    }

    public void setCarVim(String carVim) {
        this.carVim = carVim;
    }
}
