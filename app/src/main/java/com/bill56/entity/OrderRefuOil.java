package com.bill56.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

/**
 * 预约加油的订单数据封装类
 * 
 * @author Bill56
 *
 */
public class OrderRefuOil {

	// 订单编号
	private String orderNo;
	// 加油站
	private String orderOilStation;
	// 加油类型
	private String orderOilType;
	// 加油升数
	private int orderOilMass;
	// 加油单价
	private float orderOilPrice;
	// 加油总价
	private float orderOilTotal;
	// 订单状态编号
	private int orderStateNo;
	// 订单状态
	private String orderStateName;
	// 开始时间
	private String orderStartTime;
	// 结束时间
	private String orderEndTime;
	// 用户id
	private int userId;
	// 用户昵称
	private String userNick;
	// 用户电话
	private String userTel;

	/**
	 * 构造方法
	 */
	public OrderRefuOil() {
		super();
	}

	// getter与setter方法
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderOilStation() {
		return orderOilStation;
	}

	public void setOrderOilStation(String orderOilStation) {
		this.orderOilStation = orderOilStation;
	}

	public String getOrderOilType() {
		return orderOilType;
	}

	public void setOrderOilType(String orderOilType) {
		this.orderOilType = orderOilType;
	}

	public int getOrderOilMass() {
		return orderOilMass;
	}

	public void setOrderOilMass(int orderOilMass) {
		this.orderOilMass = orderOilMass;
	}

	public float getOrderOilPrice() {
		return orderOilPrice;
	}

	public void setOrderOilPrice(float orderOilPrice) {
		this.orderOilPrice = orderOilPrice;
	}

	public float getOrderOilTotal() {
		return orderOilTotal;
	}

	public void setOrderOilTotal(float orderOilTotal) {
		this.orderOilTotal = orderOilTotal;
	}

	public int getOrderStateNo() {
		return orderStateNo;
	}

	public void setOrderStateNo(int orderStateNo) {
		this.orderStateNo = orderStateNo;
	}

	public String getOrderStateName() {
		return orderStateName;
	}

	public void setOrderStateName(String orderStateName) {
		this.orderStateName = orderStateName;
	}

	public String getOrderStartTime() {
		return orderStartTime;
	}

	public void setOrderStartTime(String orderStartTime) {
		this.orderStartTime = orderStartTime;
	}

	public String getOrderEndTime() {
		return orderEndTime;
	}

	public void setOrderEndTime(String orderEndTime) {
		this.orderEndTime = orderEndTime;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserNick() {
		return userNick;
	}

	public void setUserNick(String userNick) {
		this.userNick = userNick;
	}

	public String getUserTel() {
		return userTel;
	}

	public void setUserTel(String userTel) {
		this.userTel = userTel;
	}

	/**
	 * 转化为json格式的String
	 *
	 * @return json格式的String信息
	 */
	public String toJsonString() {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		// 存放key
		String[] key = {"orderNo", "orderOilStation", "orderOilType", "orderOilMass",
				"orderOilPrice", "orderOilTotal", "orderStartTime"};
		// 存放value
		Object[] value = {orderNo, orderOilStation, orderOilType, orderOilMass,
				orderOilPrice, orderOilTotal, orderStartTime};
		for (int i = 0; i < key.length; i++) {
			try {
				jsonObject.put(key[i], value[i]);
			} catch (JSONException e) {
			}
		}
		jsonArray.put(jsonObject);
		return jsonArray.toString();
	}

}
