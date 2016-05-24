package com.bill56.util;

import android.util.Log;


import com.bill56.listener.HttpCallbackListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Bill56 on 2016/5/11.
 */
public class HttpUtil {

    // 需要连接的ip地址
    private static final String ADDRESS_IP = "119.29.230.29";
    // 需要连接的端口
    private static final int ADDRESS_PORT = 8080;

    // 连接超时时间
    private static final int CONNECT_TIMEOUT = 8000;
    // 读取数据超时时间
    private static final int READ_TIMEOUT = 8000;

    public static final int REQUEST_REGISRER = 101;

    public static final int REQUEST_LOGIN = 102;

    public static final int REQUEST_UPDATE_NICK = 103;

    public static final int REQUEST_UPDATE_PASSWORD = 104;

    public static final int REQUEST_UPDATE_EMAIL = 105;

    public static final int REQUEST_UPDATE_TEL = 106;

    public static final int REQUEST_UPDATE_SEX = 107;

    public static final int REQUEST_BIND_CAR = 201;

    public static final int REQUEST_QUERY_CAR = 202;

    public static final int REQUEST_DELETE_CAR = 203;


    /**
     * 发送请求给指定网址并获得数据（访问内部）
     *
     * @param requestCode 请求代码
     * @param requestData 请求的json数据
     * @param listener    回调接口，用来处理网络返回的数据和异常的处理
     */
    public static void sendHttpRequestToInner(final int requestCode,
                                              final String requestData,
                                              final HttpCallbackListener listener) {
        // 开启线程访问网络
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    // 初始化访问地址
                    URL url = new URL("http://" + ADDRESS_IP + ":" + ADDRESS_PORT
                            + "/CarLifeServer/action/" + getActionFromRequestCode(requestCode));
                    // 开启连接，请求服务器
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(CONNECT_TIMEOUT);
                    connection.setReadTimeout(READ_TIMEOUT);
                    // 设置请求方式为Post
                    connection.setRequestMethod("POST");
                    // 将需要传递的数据进行转换
                    String requestParams = "requestData=" + requestData;
                    byte[] entity = requestParams.getBytes("utf-8");
                    // 设置请求的参数，包括请求代码和请求的json数据
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", String.valueOf(entity.length));
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    // 获取输出流，将数据传送给服务器
                    OutputStream out = connection.getOutputStream();
                    out.write(entity);
                    // 获取输入流，将数据读取到本地
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    String strResponse = response.toString();
                    strResponse = strResponse.trim();
                    // 将strResponse中的转义字符的数据进行转换
                    String desResponse = strResponse.replaceAll("&quot;", "\"");
                    LogUtil.d(LogUtil.TAG, String.valueOf(desResponse + "，长度为：" + desResponse.length()));
                    if (listener != null) {
                        listener.onFinish(desResponse);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                } finally {
                    // 关闭连接，释放资源
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * 发送请求给指定网址并获得数据（访问外部接口）
     */
    public static void sendHttpRequestToOut(final String address,
                                            final HttpCallbackListener listener) {
        // 开启线程访问网络
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    // 初始化访问地址
                    URL url = new URL(address);
                    // 开启连接，请求服务器
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(CONNECT_TIMEOUT);
                    connection.setReadTimeout(READ_TIMEOUT);
                    // 设置请求方式为Post
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    // 获取输入流，将数据读取到本地
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    String strResponse = response.toString();
                    strResponse = strResponse.trim();
                    // 将strResponse中的转义字符的数据进行转换
                    String desResponse = strResponse.replaceAll("&quot;", "\"");
                    LogUtil.d(LogUtil.TAG, String.valueOf(desResponse + "，长度为：" + desResponse.length()));
                    if (listener != null) {
                        listener.onFinish(desResponse);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                } finally {
                    // 关闭连接，释放资源
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private static String getActionFromRequestCode(int requestCode) {
        switch (requestCode) {
            case REQUEST_REGISRER:
                return "register";
            case REQUEST_LOGIN:
                return "login";
            case REQUEST_UPDATE_NICK:
                return "updateNick";
            case REQUEST_UPDATE_TEL:
                return "updateTel";
            case REQUEST_UPDATE_EMAIL:
                return "updateEmail";
            case REQUEST_UPDATE_SEX:
                return "updateSex";
            case REQUEST_UPDATE_PASSWORD:
                return "updatePassword";
            case REQUEST_BIND_CAR:
                return "addUserCar";
            case REQUEST_QUERY_CAR:
                return "queryUserCar";
            case REQUEST_DELETE_CAR:
                return "deleteUserCar";
            default:
                return "error";
        }

    }

}
