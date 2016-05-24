package com.bill56.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.bill56.adapter.GasStaAdapter;
import com.bill56.carlife.R;
import com.bill56.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrdRefActivity extends BaseActivity implements LocationSource,
        AMapLocationListener {

    private Spinner spinner_distance;
    private Spinner spinner_supplier;
    private ListView listView_gasstation;
    private List<HashMap<String, String>> list;
    private GasStaAdapter adapter;

    // 地图定位所需的组件
    private AMap aMap;
    private MapView mapView;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private TextView mLocationErrText;

    // 当前的经纬度
    private static double mLatitude;
    private static double mLangiitude;

    /**
     * 获得当前纬度
     *
     * @return 当前纬度
     */
    public static double getmLatitude() {
        return mLatitude;
    }

    /**
     * 获得当前经度
     *
     * @return 当前经度
     */
    public static double getmLangiitude() {
        return mLangiitude;
    }

    // 定位标记的选项
    private MarkerOptions markerOption;
    // 用于标记附近加油站的图片id数组
    int[] nearoilMarkers = new int[]{R.drawable.poi_marker_1, R.drawable.poi_marker_2,
            R.drawable.poi_marker_3, R.drawable.poi_marker_4, R.drawable.poi_marker_5,
            R.drawable.poi_marker_6, R.drawable.poi_marker_7, R.drawable.poi_marker_8,
            R.drawable.poi_marker_9, R.drawable.poi_marker_10};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ord_ref);
        // 修改actionBar的信息
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.ordRef_title);
        actionBar.setDisplayHomeAsUpEnabled(true);
        // 获取附近加油站的json信息
        Intent intent = getIntent();
        String response = intent.getStringExtra("response");
        // 绑定控件变量
        listView_gasstation = (ListView) findViewById(R.id.listView_gasstation);
        // 解析附近加油站的json信息
        list = analysis(response);
        // 绑定地图信息
        mapView = (MapView) findViewById(R.id.near_oil_map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        if (list != null) {
            adapter = new GasStaAdapter(this, list);
            listView_gasstation.setAdapter(adapter);
        }
        // 根据解析后的加油站信息的经纬度绘制地图坐标点，如果list为空，则只定位不做标记
        loadNearOilMap();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_ref_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    /**
     * 加载地图的定位和加油站标记
     */
    private void loadNearOilMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        mLocationErrText = (TextView) findViewById(R.id.location_errInfo_text);
        mLocationErrText.setVisibility(View.GONE);
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 设置定位监听
        aMap.setLocationSource(this);
        // // 设置默认定位按钮是否显示
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        //设置定位的缩放级别，高德地图的缩放级别是3-19之间
        aMap.moveCamera(CameraUpdateFactory.zoomTo(12f));
        // 当列表不为null的时候
        if (list != null) {
            // 往地图上添加marker
            addMarkersToMap();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
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


    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mLocationErrText.setVisibility(View.GONE);
                // 显示系统小蓝点
                mListener.onLocationChanged(amapLocation);
                // 将定位的经纬度进行保存
                mLangiitude = amapLocation.getLongitude();
                mLatitude = amapLocation.getLatitude();
//                LogUtil.d("MainActivity", "longitude=" + longitude + ",latitude=" + latitude);
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
                mLocationErrText.setVisibility(View.VISIBLE);
                mLocationErrText.setText(errText);
            }
        }
    }

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
     * 在地图上添加marker
     */
    private void addMarkersToMap() {
        for (int i = 0; i < nearoilMarkers.length && i < list.size(); i++) {
            markerOption = new MarkerOptions();
            DPoint baiduLatLng = new DPoint(
                    Double.parseDouble(list.get(i).get("lat")),
                    Double.parseDouble(list.get(i).get("lon")));
            // 将百度地图坐标转换成高德地图坐标
            CoordinateConverter converter = new CoordinateConverter(this);
            // CoordType.BAIDU 待转换坐标类型
            converter.from(CoordinateConverter.CoordType.BAIDU);
            // sourceLatLng待转换坐标点 DPoint类型
            // 目标坐标，默认为原来的坐标
            DPoint desLatLng = baiduLatLng;
            try {
                converter.coord(baiduLatLng);
                // 执行转换操作
                desLatLng = converter.convert();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 设置经纬度
            markerOption.position(new LatLng(desLatLng.getLatitude(), desLatLng.getLongitude()));
            LogUtil.d(LogUtil.TAG, "lat:" + desLatLng.getLatitude() + ",long:" + desLatLng.getLongitude());
            // 设置标记不能拖拽
            markerOption.draggable(false);
            // 设置标记的图片
            markerOption.icon(
                    BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(),
                                    nearoilMarkers[i])));
            // 将标记添加到地图
            aMap.addMarker(markerOption);
        }
//        markerOption = new MarkerOptions();
//        markerOption.position(new LatLng(28.138645, 112.94263));
//        markerOption.snippet("西安市：34.341568, 108.940174");
//
//        markerOption.draggable(false);
//        markerOption.icon(
//                BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                        .decodeResource(getResources(),
//                                R.drawable.poi_marker_1)));
//        // 将Marker设置为贴地显示，可以双指下拉看效果
//        markerOption.setFlat(true);
//        aMap.addMarker(markerOption);
//        aMap.addMarker(new MarkerOptions()
//                .position(new LatLng(28.1640719826, 112.9532539988))
//                .draggable(false)
//                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                        .decodeResource(getResources(),
//                                R.drawable.poi_marker_2))));
    }

    private List<HashMap<String, String>> analysis(String response) {

        list = new ArrayList<>();

        try {
            String str = "[" + response + "]";
            JSONArray array = new JSONArray(str);
            JSONObject object = array.getJSONObject(0);
            String result = object.getString("result");
            String str1 = "[" + result + "]";
            JSONArray array1 = new JSONArray(str1);
            JSONObject object1 = array1.getJSONObject(0);
            String data = object1.getString("data");
            JSONArray array2 = new JSONArray(data);
            int len = array2.length();
            for (int i = 0; i < len; i++) {
                JSONObject object2 = array2.getJSONObject(i);
                String id = object2.getString("id");
                String name = object2.getString("name");
                String area = object2.getString("area");
                String areaname = object2.getString("areaname");
                String address = object2.getString("address");
                String brandname = object2.getString("brandname");
                String type = object2.getString("type");
                String discount = object2.getString("distance");
                String exhaust = object2.getString("exhaust");
                String position = object2.getString("position");
                String lon = object2.getString("lon");
                String lat = object2.getString("lat");
                String price = object2.getString("price");
                String gastprice = object2.getString("gastprice");
                String fwlsmc = object2.getString("fwlsmc");
                String distance = object2.getString("distance");
                HashMap<String, String> map = new HashMap<>();
                map.put("num", String.valueOf(i + 1));
                map.put("id", id);
                map.put("name", name);
                map.put("area", area);
                map.put("areaname", areaname);
                map.put("address", address);
                map.put("brandname", brandname);
                map.put("type", type);
                map.put("discount", discount);
                map.put("exhaust", exhaust);
                map.put("position", position);
                map.put("lon", lon);
                map.put("lat", lat);
                map.put("price", price);
                map.put("gastprice", gastprice);
                map.put("fwlsmc", fwlsmc);
                map.put("distance", distance);
                list.add(map);
            }

//            OrderRefuelData refuelData = new OrderRefuelData(id, name, area, areaname,
//                    address, brandname, type, discount, exhaust, position,
//                    lon, lat, price, gastprice, fwlsmc, distance);
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
