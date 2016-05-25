package com.bill56.carlife;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;

import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bill56.activity.BaseActivity;
import com.bill56.activity.LoadActivity;
import com.bill56.activity.OrdRefActivity;
import com.bill56.activity.PerInfoActivity;
import com.bill56.activity.WeiZhangQueryActivity;
import com.bill56.service.QueryCarStateService;
import com.bill56.util.ActivityUtil;
import com.bill56.util.LogUtil;
import com.bill56.util.Net;


public class MainActivity extends BaseActivity implements LocationSource,
        AMapLocationListener, RadioGroup.OnCheckedChangeListener {

    // 记录按下back键后的毫秒数
    private long lastBackPressed;

    // 保存定位的精度
    private double longitude;
    // 保存定位的纬度
    private double latitude;

    // 地图定位所需的组件
    private AMap aMap;
    private MapView mapView;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private RadioGroup mGPSModeGroup;
    private TextView mLocationErrText;

    // 抽屉布局所需的组件
    private TextView textViewXingming;
    private ActionBar actionBar;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //抽屉关闭
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示程序的标题栏
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        // 初始化抽屉布局
        initDrawLayout();
        init();
        startQueryCarService();
    }

    /**
     * 启动后台服务
     */
    private void startQueryCarService() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // 获取用户Id
        int userId = preferences.getInt("id",0);
        String userName = preferences.getString("name",null);
        // 当id > 0的时候启动后台服务
        if (userId > 0 && userName != null) {
            LogUtil.d(LogUtil.TAG,"用户id为：" + userId);
            Intent serviceIntent = new Intent(this, QueryCarStateService.class);
            serviceIntent.putExtra("userId",userId);
            serviceIntent.putExtra("userName",userName);
            startService(serviceIntent);
        }
    }

    private void initDrawLayout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //getActionBar
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        textViewXingming = (TextView) findViewById(R.id.textView_xingming);
        //抽屉把手
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {
            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                //重新创建选项菜单
                invalidateOptionsMenu();
            }
        };
        //同步状态,更新抽屉图标
        toggle.syncState();
        drawerLayout.setDrawerListener(toggle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_option, menu);
        return true;
    }

    /**
     * 当选项菜单选中后执行
     *
     * @param item 被点击的选项菜单
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (id) {
            case R.id.action_search:
                ActivityUtil.startMapSearchActivity(this, longitude, latitude);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        mGPSModeGroup = (RadioGroup) findViewById(R.id.gps_radio_group);
        mGPSModeGroup.setOnCheckedChangeListener(this);
        mLocationErrText = (TextView) findViewById(R.id.location_errInfo_text);
        mLocationErrText.setVisibility(View.GONE);
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 设置定位监听
        aMap.setLocationSource(this);
        // 设置默认定位按钮是否显示
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        //设置定位的缩放级别，高德地图的缩放级别是3-19
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16f));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.gps_locate_button:
                // 设置定位的类型为定位模式
                aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
                break;
            case R.id.gps_follow_button:
                // 设置定位的类型为 跟随模式
                aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
                break;
            case R.id.gps_rotate_button:
                // 设置定位的类型为根据地图面向方向旋转
                aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
                break;
        }

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        initName();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mLocationErrText.setVisibility(View.GONE);
                // 显示系统小蓝点
                mListener.onLocationChanged(amapLocation);
                // 将定位的经纬度进行保存
                longitude = amapLocation.getLongitude();
                latitude = amapLocation.getLatitude();
                LogUtil.d("MainActivity", "longitude=" + longitude + ",latitude=" + latitude);
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
                mLocationErrText.setVisibility(View.VISIBLE);
                mLocationErrText.setText(errText);
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }


    /**
     * 初始化抽屉
     */
    private void initName() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        textViewXingming.setText(preferences.getString("name", "登录"));
    }

    /**
     * 下面是针对用户点击了左侧抽屉布局的选项后进行的方法绑定
     */

    /**
     * 当前登录显示个人信息，未登录进入登录界面
     *
     * @param v
     */
    public void LoadIn(View v) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("name", null);
        if (name == null) {
            //前往登录界面
            Intent intent = new Intent(this, LoadActivity.class);
            startActivity(intent);
        } else {
            //前往个人信息界面
            Intent intent = new Intent(this, PerInfoActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 预约加油项被点击后执行的方法
     *
     * @param v 被点击的事件源
     */
    public void OrderRefuel(View v) {
        //http://apis.juhe.cn/oil/local?key=您申请的APPKEY&lon=116.403119&lat=39.916042&format=2&r=3000
        showProgressDialog();
        String httpkey = "key=88cb2204cb62d2cb709c275ce1b4cb98";
        String lon = "&lon=" + longitude;
        String lat = "&lat=" + latitude;
        String r = "&r=5000";
        String httpUrl = "http://apis.juhe.cn/oil/local?" + httpkey + lon + lat + r;

        //抽屉关闭
        drawerLayout.closeDrawer(GravityCompat.START);
        Net.getInstance(getApplicationContext()).addRequestToQueue(new StringRequest(Request.Method.GET, httpUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //获取json对象
                LogUtil.d("MainActivity", response);
                //跳转到数据显示界面
                Intent intent = new Intent(MainActivity.this, OrdRefActivity.class);
                intent.putExtra("response", response);
                startActivity(intent);
                dissmissProgressDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "101", Toast.LENGTH_SHORT).show();
                dissmissProgressDialog();
            }
        }));
    }

    // 显示加载的对话框
    ProgressDialog progDialog;

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在加载，请稍等......");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 当用户点击违章查询后执行的方法
     *
     * @param v 被点击的事件源
     */
    public void WeiZhangQuery(View v) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("name", null);
        int userId = preferences.getInt("id", 0);
        // 没有登录
        if (name == null || userId <= 0) {
            startActivity(new Intent(this, WeiZhangQueryActivity.class));
        } else {
            ActivityUtil.startCarInfoActivity(this, userId, true);
        }
    }

    /**
     * 当用户点击车辆信息后执行的方法
     *
     * @param v 被点击的事件源
     */
    public void CarManage(View v) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("name", null);
        int userId = preferences.getInt("id", 0);
        if (name == null || userId <= 0) {
            // 弹出对话框，让其登录
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("您还未登录，请登录后查询");
            builder.setCancelable(false);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startActivity(new Intent(MainActivity.this, LoadActivity.class));
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            ActivityUtil.startCarInfoActivity(this, userId, false);
        }
    }

    @Override
    public void onBackPressed() {
        // 获取当前时间
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackPressed < 2000) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
        }
        lastBackPressed = currentTime;
    }
}
