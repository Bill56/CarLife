package com.bill56.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.bill56.activity.SeekActivity;
import com.bill56.carlife.MainActivity;
import com.bill56.carlife.R;


/**
 * Created by asus on 2016/3/11.
 */
public class BlueTooth {

    private static final String TAG = "BlueTooth";
    private static BlueTooth blueTooth;
    private static Handler handler = new Handler();
    private static Context context;

    public BlueTooth(Context context) {
        this.context = context;
    }

    /**
     * 单例模式
     *
     * @return
     */
    public static BlueTooth getInstance(Context context) {
        if (blueTooth == null) {
            blueTooth = new BlueTooth(context);
        }
        return blueTooth;
    }

    /**
     * 获取蓝牙适配器
     *
     * @return
     */
    private BluetoothAdapter getBlueToothAdapter() {
        BluetoothAdapter adapter = MainActivity.getAdapter();
        return adapter;
    }

    /**
     * 蓝牙开始扫描
     *
     * @param enable
     */
    public void scanLeDevice(final boolean enable) {

        Log.d(TAG, "scanLeDevice");
        final BluetoothAdapter adapter = getBlueToothAdapter();
        if (enable) {
            //搜索十秒后中断搜索
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //判断进入路径
                    SeekActivity.isDrawable = true;
                    SeekActivity.button.setBackgroundDrawable(context.getDrawable(R.drawable.ic_play_circle_outline_24dp));
                    adapter.cancelDiscovery();
                }
            }, 10000);
            adapter.startDiscovery();
        } else {
            adapter.cancelDiscovery();
        }
    }
}
