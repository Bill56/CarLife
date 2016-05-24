package com.bill56.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.bill56.carlife.R;
import com.bill56.util.AMapUtil;
import com.bill56.util.LogUtil;
import com.bill56.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bill56 on 2016/5/13.
 */
public class MapSearchActivity extends BaseActivity implements TextWatcher,
        View.OnClickListener, Inputtips.InputtipsListener, LocationSource, AMap.OnMapClickListener, RouteSearch.OnRouteSearchListener, AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, AMapLocationListener, GeocodeSearch.OnGeocodeSearchListener {

    // 活动的控件
    private AutoCompleteTextView autoSeachFrom;
    private AutoCompleteTextView autoSeachTo;
    private ImageButton imgbtnSwap;

    // 当前位置的经度
    private double longtitude;
    // 当前位置的纬度
    private double latitude;
    // 目的地经度
    private double desLongtitude;
    // 目的地纬度
    private double desLatitude;
    // 目的地址地址
    private String desAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);
        // 设置actionBar的信息
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.map_search_title);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intentFromMain = getIntent();
        // 获取传递过来的经纬度
        longtitude = intentFromMain.getDoubleExtra("currentLong", 112.8);
        latitude = intentFromMain.getDoubleExtra("currentLat", 28.2);
        desLongtitude = intentFromMain.getDoubleExtra("desLong", -1.0);
        desLatitude = intentFromMain.getDoubleExtra("desLat", -1.0);
        desAddress = intentFromMain.getStringExtra("desAddress");
        // 将起始位置初始化
        mStartPoint = new LatLonPoint(latitude, longtitude);
        initView();
        mContext = this.getApplicationContext();
        mapView = (MapView) findViewById(R.id.route_map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        initMap();
        // 当从不是从主活动传递过来的时候
        if (desLatitude > 0 && desLongtitude > 0 && desAddress != null) {
            // 设置目的地经纬度
            mEndPoint = new LatLonPoint(desLatitude, desLongtitude);
            // 将目的地输入框设置为传入的地点
            autoSeachTo.setText(desAddress);
            // 将输入的控件变为不可编辑
            autoSeachFrom.setEnabled(false);
            autoSeachTo.setEnabled(false);
            doSearchQuery();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_search_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_route:
                searchButton();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 设置页面监听
     */
    private void initView() {
        // 绑定交换按钮
        imgbtnSwap = (ImageButton) findViewById(R.id.imgbtn_swap);
        imgbtnSwap.setOnClickListener(this);
        // 绑定出发地和目的地的输入框
        autoSeachFrom = (AutoCompleteTextView) findViewById(R.id.edit_seach_from);
        autoSeachTo = (AutoCompleteTextView) findViewById(R.id.edit_seach_to);
        // 为出发地和目的地输入框添加文本改动的事件监听
        autoSeachFrom.addTextChangedListener(this);
        autoSeachTo.addTextChangedListener(this);
    }

    /**
     * 将出发地和目的地进行互换
     */
    public void swapSrcToDes() {
        String src = AMapUtil.checkEditText(autoSeachFrom);
        String des = AMapUtil.checkEditText(autoSeachTo);
        if ("".equals(src)) {
            ToastUtil.show(MapSearchActivity.this, "请输入出发地");
            return;
        }
        if ("".equals(des)) {
            ToastUtil.show(MapSearchActivity.this, "请输入目的地");
            return;
        }
        // 把目的地和出发地也进行交换
        LatLonPoint temp = mStartPoint;
        mStartPoint = mEndPoint;
        mEndPoint = temp;
        autoSeachFrom.setFocusableInTouchMode(false);
        autoSeachFrom.clearFocus();
        autoSeachTo.setFocusableInTouchMode(false);
        autoSeachTo.clearFocus();
        // 交换显示
        autoSeachTo.setText(src);
        autoSeachFrom.setText(des);
        // 自动执行路径规划功能
        doSearchQuery();
    }

    /**
     * 点击搜索按钮
     */
    public void searchButton() {
        String src = AMapUtil.checkEditText(autoSeachFrom);
        String des = AMapUtil.checkEditText(autoSeachTo);
        if ("".equals(src)) {
            ToastUtil.show(MapSearchActivity.this, "请输入出发地");
            return;
        }
        if ("".equals(des)) {
            ToastUtil.show(MapSearchActivity.this, "请输入目的地");
            return;
        }
        doSearchQuery();
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在进行路径规划...");
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
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        // 隐藏输入法
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow((autoSeachFrom.isFocused()?autoSeachFrom:autoSeachTo).getWindowToken(),0);
        // 显示进度框
        showProgressDialog();
        // 进行路径规划
        setfromandtoMarker();
        searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
//        mapView.setVisibility(View.VISIBLE);
        //设置定位的缩放级别，高德地图的缩放级别是3-19之间
        aMap.moveCamera(CameraUpdateFactory.zoomTo(12f));
        autoSeachFrom.setFocusableInTouchMode(true);
        autoSeachFrom.requestFocus();
        autoSeachTo.setFocusableInTouchMode(true);
        autoSeachTo.requestFocus();
        // 关闭进度框
        dissmissProgressDialog();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * 点击交换图片按钮
             */
            case R.id.imgbtn_swap:
                swapSrcToDes();
                break;
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        if (!AMapUtil.IsEmptyOrNullString(newText)) {
            // 第一个参数表示要查询的关键字，第二个参数表示所在的城市，""表示全国
            // 当mCurrentCityName为空的时候，传""表示全国，否则传当前城市
            InputtipsQuery inputquery = new InputtipsQuery(newText, null==mCurrentCityName?"":mCurrentCityName);
            Inputtips inputTips = new Inputtips(MapSearchActivity.this, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        String newText = s.toString().trim();
        // 判断哪个文本框获得了焦点
        if (autoSeachFrom.isFocused()) {
            LogUtil.d(LogUtil.TAG, "from获得焦点：" + newText);
            // 如果出发点还是我的位置，则不改变传入的坐标值
            if (!"我的位置".equals(newText)) {
                // 进行编码查询
                // 进行编码查询
                isSearchFrom = true;
                // 进行编码查询
                getLatlon(newText);
            } else {
                mStartPoint = new LatLonPoint(latitude, longtitude);
            }
        } else if (autoSeachTo.isFocused()) {
            LogUtil.d(LogUtil.TAG, "to获得焦点：" + newText);
            if (!"我的位置".equals(newText)) {
                // 进行编码查询
                isSearchFrom = false;
                // 进行编码查询
                getLatlon(newText);
            } else {
                mEndPoint = new LatLonPoint(latitude, longtitude);
            }
        }
    }

    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        if (rCode == 1000) {// 正确返回
            List<String> listString = new ArrayList<String>();
            for (int i = 0; i < tipList.size(); i++) {
                listString.add(tipList.get(i).getName());
            }
            ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
                    getApplicationContext(),
                    R.layout.route_inputs, listString);
            // 判断哪个输入框获得了焦点
            if (autoSeachFrom.isFocused()) {
                autoSeachFrom.setAdapter(aAdapter);
            } else if (autoSeachTo.isFocused()) {
                autoSeachTo.setAdapter(aAdapter);
            }
            aAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showerror(this, rCode);
        }
    }

    // 路径规划需要的字段
    private AMap aMap;
    private MapView mapView;
    private Context mContext;
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private LatLonPoint mStartPoint = null;//起点，
    private LatLonPoint mEndPoint = null;//终点，
    private String mCurrentCityName = null;
    private final int ROUTE_TYPE_DRIVE = 2;

    private RelativeLayout mBottomLayout;
    private TextView mRotueTimeDes, mRouteDetailDes;
    private ProgressDialog progDialog = null;// 搜索时进度条

    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private TextView mLocationErrText;

    // 编码转换的字段
    // 进行位置转换的参数
    private GeocodeSearch geocoderSearch;
    // 表示当前转换的地址是否为出发地，true表示是，false表示当前转换的为目的地
    private boolean isSearchFrom = false;

    private void setfromandtoMarker() {
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(mStartPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(mEndPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));
    }

    /**
     * 初始化AMap对象
     */
    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        mLocationErrText = (TextView) findViewById(R.id.location_errInfo_text);
        mLocationErrText.setVisibility(View.GONE);
        registerListener();
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mRotueTimeDes = (TextView) findViewById(R.id.firstline);
        mRouteDetailDes = (TextView) findViewById(R.id.secondline);
        // 获得当前经纬度所在的城市
        getAddress(new LatLonPoint(latitude,longtitude));
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
    }


    /**
     * 注册监听
     */
    private void registerListener() {
        aMap.setOnMapClickListener(MapSearchActivity.this);
        aMap.setOnMarkerClickListener(MapSearchActivity.this);
        aMap.setOnInfoWindowClickListener(MapSearchActivity.this);
        aMap.setInfoWindowAdapter(MapSearchActivity.this);
    }

    @Override
    public View getInfoContents(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onMarkerClick(Marker arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onMapClick(LatLng arg0) {
        // TODO Auto-generated method stub

    }


    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode) {
        if (mStartPoint == null) {
            ToastUtil.show(mContext, "定位中，稍后再试...");
            return;
        }
        if (mEndPoint == null) {
            ToastUtil.show(mContext, "终点未设置");
        }
        showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null,
                    null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult result, int errorCode) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        dissmissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            this, aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos());
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    mBottomLayout.setVisibility(View.VISIBLE);
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";
                    mRotueTimeDes.setText(des);
                    mRouteDetailDes.setVisibility(View.VISIBLE);
                    int taxiCost = (int) mDriveRouteResult.getTaxiCost();
                    mRouteDetailDes.setText("打车约" + taxiCost + "元");
                    mBottomLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext,
                                    DriveRouteDetailActivity.class);
                            intent.putExtra("drive_path", drivePath);
                            intent.putExtra("drive_result",
                                    mDriveRouteResult);
                            startActivity(intent);
                        }
                    });
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(mContext, R.string.no_result);
                }

            } else {
                ToastUtil.show(mContext, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {

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
                longtitude = amapLocation.getLongitude();
                latitude = amapLocation.getLatitude();
                Log.d("MainActivity", "longitude=" + amapLocation.getLongitude() + ",latitude=" + amapLocation.getLatitude());
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
     * 响应地理编码
     */
    public void getLatlon(final String name) {
        GeocodeQuery query = new GeocodeQuery(name, null);// 第一个参，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求数表示地址
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                // 将反向编码获得的城市赋值给mCurrentCityName
                mCurrentCityName = result.getRegeocodeAddress().getCity();
            } else {
//                ToastUtil.show(MapSearchActivity.this, R.string.no_result);
            }
        } else {
//            ToastUtil.showerror(this, rCode);
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);
//                addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:"
//                        + address.getFormatAddress();
                // 表示当前应将获得的编码给出发地
                if (isSearchFrom) {
                    mStartPoint = address.getLatLonPoint();
                } else {
                    // 表示当前应将获得的编码给目的地
                    mEndPoint = address.getLatLonPoint();
                }
            } else {
//                ToastUtil.show(MapSearchActivity.this, R.string.no_result);
            }
        } else {
//            ToastUtil.showerror(this, rCode);
        }
    }

}
