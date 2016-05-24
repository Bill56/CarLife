package com.bill56.util;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

/**
 * Created by asus on 2016/5/11.
 */
public class Net {
    private static Net instance;
    private RequestQueue requestQueue;

    public static Net getInstance(Context context) {
        if (instance == null) {
            instance = new Net(context);
        }
        return instance;
    }

    public Net(Context context) {

        //定义内存缓存大小
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024 * 10);
        //定义网络
        Network network = new BasicNetwork(new HurlStack());
        //声明请求队列
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public void addRequestToQueue(Request request) {
        requestQueue.add(request);
    }
}
