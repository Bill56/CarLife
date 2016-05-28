package com.bill56.activity;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import com.bill56.carlife.MainActivity;
import com.bill56.carlife.R;
import com.bill56.service.BlueService;
import com.bill56.util.BlueTooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SeekActivity extends AppCompatActivity {

    private static final String TAG = "SeekActivity";
    ListView listView;
    public static FloatingActionButton button;
    private static SimpleAdapter adapter;
    public static List<BluetoothDevice> bdList;
    private static List<HashMap<String, String>> data;
    private BlueTooth blueTooth;
    public static boolean isDrawable = true;    //判断按钮点击效果
    public static BluetoothDevice device;
    private BlueService blueService;

    //获取Service对象
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(TAG, "onServiceConnected");
            blueService = ((BlueService.LocalBinder) binder).getService();
            //建立连接
            try {
                blueService.connectDevice();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            blueService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seek);

        listView = (ListView) findViewById(R.id.listView);
        button = (FloatingActionButton) findViewById(R.id.button);
        SeekBlueTooch listener = new SeekBlueTooch();
        button.setBackgroundDrawable(getDrawable(R.drawable.ic_play_circle_outline_24dp));
        button.setOnClickListener(listener);
        ItemListener itemListener = new ItemListener();
        listView.setOnItemClickListener(itemListener);

        //注册广播
        IntentFilter filter = new IntentFilter();
        //蓝牙查找监听
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //蓝牙搜索完成
//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //蓝牙设备低级别连接
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        //蓝牙设备低级别连接断开
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        //蓝牙设备连接状态改变
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(receiver, filter);

        //初始化ListView
        bdList = new ArrayList<>();
        data = new ArrayList<>();
        String[] from = {"address", "name"};
        int[] to = {R.id.textView_address, R.id.textView_name};
        adapter = new SimpleAdapter(this, data, R.layout.array_layout, from, to);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        blueTooth = BlueTooth.getInstance(this);
        super.onStart();
    }

    /**
     * 按钮点击监听器
     */
    class SeekBlueTooch implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            //判断蓝牙功能是否开启
            if (!MainActivity.getAdapter().isEnabled()) {
                //提示信息
                Toast.makeText(SeekActivity.this, "蓝牙可能未开启", Toast.LENGTH_SHORT).show();
                //若蓝牙未开启则跳转到主界面
                startActivity(new Intent(SeekActivity.this, MainActivity.class));
            }

            if (isDrawable) {
                isDrawable = false;
                button.setBackgroundDrawable(getDrawable(R.drawable.ic_pause_circle_outline_24dp));
                blueTooth.scanLeDevice(true);
            } else {
                isDrawable = true;
                button.setBackgroundDrawable(getDrawable(R.drawable.ic_play_circle_outline_24dp));
                blueTooth.scanLeDevice(false);
            }
        }
    }

    public static BluetoothDevice getDevice() {
        return device;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    //广播接收器
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            //找到设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "find device" + device.getName() + device.getAddress());
                //避免重复设备显示
                if (data.size() == 0) {
                    bdList.add(device);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("address", device.getAddress());
                    map.put("name", device.getName());
                    data.add(map);
                } else {
                    for (int i = 0; i < data.size(); i++) {
                        if (data.get(i).get("address").equals(device.getAddress()))
                            break;
                        if (i == data.size() - 1) {
                            bdList.add(device);
                            HashMap<String, String> map = new HashMap<>();
                            map.put("address", device.getAddress());
                            map.put("name", device.getName());
                            data.add(map);
                        }
                    }
                }
            }
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Log.d(TAG, "ACTOIN_ACL_CONNECTION");
                Toast.makeText(SeekActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                finish();
            }
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Log.d(TAG, "ACTION_ACL_DISCONNECTED");
                Toast.makeText(SeekActivity.this, "断开连接", Toast.LENGTH_SHORT).show();
            }
            adapter.notifyDataSetChanged();
        }
    };

    /**
     * ListView 的 Item Listener
     */
    class ItemListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            device = bdList.get(position);
            String name = device.getName();
            String address = device.getAddress();
            Log.d(TAG, "选中" + name + "," + address);
            //绑定服务
            bindService(new Intent(SeekActivity.this, BlueService.class), conn, BIND_AUTO_CREATE);
        }
    }

}
