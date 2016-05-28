package com.bill56.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


import com.bill56.activity.SeekActivity;
import com.bill56.carlife.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

public class BlueService extends Service {

    private static final String TAG = "BlueService";
    private BluetoothSocket socket;
    private boolean bluetoothFlag;
    private BluetoothDevice bluetoothDevice;
    private OutputStream outputStream;
    private InputStream inputStream;
    final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    public BlueService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public class LocalBinder extends Binder {
        public BlueService getService() {
            return BlueService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        bluetoothDevice = SeekActivity.getDevice();
        Log.d(TAG, "onBind");
        return new LocalBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.onUnbind(intent);
    }

    public void connectDevice() throws IOException {
        BluetoothAdapter adapter = MainActivity.getAdapter();
        if (adapter == null) {
            return;
        }
        Log.d(TAG, "尝试连接蓝牙设备");

        try {
//            socket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID));
            Method m = bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            socket = (BluetoothSocket) m.invoke(bluetoothDevice, 1);
            socket.connect();
        } catch (Exception e) {
            Log.d(TAG, "套接字创建失败");
            bluetoothFlag = false;
        }
        Log.d(TAG, "成功连接");
        adapter.cancelDiscovery();
        try {
            socket.connect();
            Log.d(TAG, "连接成功");
            bluetoothFlag = true;
        } catch (IOException e) {
            try {
                socket.close();
                bluetoothFlag = false;
            } catch (IOException e2) {
                Log.d(TAG, "连接未成功");
            }
        }

        if (bluetoothFlag) {
            try {
                inputStream = socket.getInputStream();
                Log.d(TAG, "inputStream");
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                outputStream = socket.getOutputStream();
                String str = "123";
                outputStream.write(str.getBytes());
                outputStream.flush();
                Log.d(TAG, "outputStream");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
