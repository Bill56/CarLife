package com.bill56.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bill56.adapter.CarInfoAdapter;
import com.bill56.carlife.R;
import com.bill56.entity.UserCar;
import com.bill56.listener.HttpCallbackListener;
import com.bill56.util.ActivityUtil;
import com.bill56.util.HttpUtil;
import com.bill56.util.JSONUtil;
import com.bill56.util.LogUtil;
import com.bill56.util.ToastUtil;
import com.bill56.zxing.activity.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bill56 on 2016/5/22.
 */
public class CarInfoActivity extends BaseActivity {

    public static final int SCAN_REQUEST_CODE = 10000;

    // 这里是json格式的结果
    private String scanResult;
    private ProgressDialog progDialog;
    // 保存传递过来的userId
    private int userId;
    // 是否可查询
    private boolean isQueryWeiZhang;
    // 当点选中的条目的索引
    private int currentItemIndex;

    // 用户界面组件
    private ListView listCarInfo;
    // 适配器
    private CarInfoAdapter carInfoAdapter;
    // 存放数据的List
    private List<UserCar> carData;
    // 没有数据时候显示的布局
    private LinearLayout llCarInfoListEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carinfo);
        userId = getIntent().getIntExtra("userId", 0);
        isQueryWeiZhang = getIntent().getBooleanExtra("isQueryWeiZhang", false);
        // 显示返回按钮
        ActionBar bar = getSupportActionBar();
        bar.setTitle(R.string.carinfo_title);
        bar.setDisplayHomeAsUpEnabled(true);
        // 初始化控件和数据
        initView();
        // 注册上下文菜单
        registerForContextMenu(listCarInfo);
    }

    /**
     * 初始化ListView
     */
    private void initView() {
        // 绑定控件
        listCarInfo = (ListView) findViewById(R.id.list_car_info);
        llCarInfoListEmpty = (LinearLayout) findViewById(R.id.ll_car_info_list_empty);
        // 注册上下文菜单
        // 获取数据
        updateCars();
        // 当是从违章查询跳过来的时候才设置事件监听
        if (isQueryWeiZhang) {
            LogUtil.d(LogUtil.TAG, "可查询违章");
            getSupportActionBar().setTitle("选择车辆");
            // 设置listView的每一项点击事件监听
            listCarInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String carLicence = carData.get(position).getCarLicence();
                    String carEngineNo = carData.get(position).getCarEngineNo();
                    // 启动活动
                    ActivityUtil.startWeiZhangQueryActivity(CarInfoActivity.this, carLicence, carEngineNo);
                }
            });
        }
    }

    /**
     * 获取并更新cars
     */
    private void updateCars() {
        showProgressDialog("正在加载......");
        // 发起网络请求
        HttpUtil.sendHttpRequestToInner(HttpUtil.REQUEST_QUERY_CAR,
                JSONUtil.createQueryUserCar(userId), new HttpCallbackListener() {
                    Message message = new Message();

                    @Override
                    public void onFinish(String response) {
                        message.what = QUERY_SUCCESS;
                        message.obj = response;
                        bindCarHandler.sendMessage(message);
                    }

                    @Override
                    public void onError(Exception e) {
                        message.what = BIND_FAILURE;
                        message.obj = "服务器异常";
                        bindCarHandler.sendMessage(message);
                    }
                });
    }

    /**
     * 添加选项菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.carinfo_option, menu);
        return true;
    }

    /**
     * 选项菜单的事件处理
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_scan_by_scan:
                doScanCarInfo();
                break;
        }
        return true;
    }

    /**
     * 创建上下文菜单
     *
     * @param menu     加载的菜单
     * @param v        被注册的控件
     * @param menuInfo 列表项中被长按的那项的信息
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // 如果注册的控件是列表项
        if (v == listCarInfo) {
            // 加载菜单
            getMenuInflater().inflate(R.menu.carinfo_context, menu);
        }
    }

    /**
     * 当上下文菜单某一项被点击的方法
     *
     * @param item 被点击的菜单项
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // 获得被点击的菜单信息
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_delete_car:
                // 获得被点击的汽车的识别代码
                currentItemIndex = menuInfo.position;
                String carVim = carData.get(currentItemIndex).getCarVim();
                doDeleteUserCar(carVim);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 点击扫一扫后执行，获取车辆信息
     */
    private void doScanCarInfo() {
        // 启动一个可以返回结果Activity
        startActivityForResult(new Intent(this, CaptureActivity.class), SCAN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCAN_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    scanResult = bundle.getString("result");
                    // 处理收到的json结果
                    doScanResult(scanResult);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 处理扫描结果
     *
     * @param carJSON
     */
    private void doScanResult(final String carJSON) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CarInfoActivity.this);
        builder.setTitle("提示");
        builder.setCancelable(false);
        String carLicence = JSONUtil.parseCarLicenceFromJSON(carJSON);
        if (carLicence != null) {
            builder.setMessage("是否绑定车牌为[" + carLicence + "]的车辆");
            builder.setNegativeButton(R.string.perinfo_button_cancel, null);
            builder.setPositiveButton(R.string.perinfo_button_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showProgressDialog("正在绑定......");
                    UserCar userCar = JSONUtil.parseUserCarJSONFromQRCode(carJSON);
                    if (userCar == null) {
                        ToastUtil.show(CarInfoActivity.this, "绑定失败，请重试");
                        dissmissProgressDialog();
                    } else if (userId <= 0) {
                        ToastUtil.show(CarInfoActivity.this, "登录超时，请重新登录");
                    } else {
                        // 将用户信息存入
                        userCar.setUserId(userId);
                        // 创建需要传输的json数据
                        String requestData = JSONUtil.createUserCarJSON(userCar);
                        // 向服务器发送数据
                        HttpUtil.sendHttpRequestToInner(HttpUtil.REQUEST_BIND_CAR, requestData, new HttpCallbackListener() {
                            Message message = new Message();

                            @Override
                            public void onFinish(String response) {
                                message.what = BIND_SUCCESS;
                                message.obj = response;
                                bindCarHandler.sendMessage(message);
                            }

                            @Override
                            public void onError(Exception e) {
                                message.what = BIND_FAILURE;
                                message.obj = "服务器异常";
                                bindCarHandler.sendMessage(message);
                            }
                        });
                    }
                }
            });
        } else {
            builder.setMessage("扫描出错，请再试一次");
            builder.setNegativeButton(R.string.perinfo_button_ok, null);
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 根据车辆识别代码发送数据给服务器删除该车辆
     *
     * @param carVim 车辆识别代码
     */
    private void doDeleteUserCar(String carVim) {
        showProgressDialog("正在解绑......");
        HttpUtil.sendHttpRequestToInner(HttpUtil.REQUEST_DELETE_CAR,
                JSONUtil.createDeleteCarJSON(carVim), new HttpCallbackListener() {
                    Message message = new Message();

                    @Override
                    public void onFinish(String response) {
                        message.what = DELETE_SUCCESS;
                        message.obj = response;
                        bindCarHandler.sendMessage(message);
                    }

                    @Override
                    public void onError(Exception e) {
                        message.what = BIND_FAILURE;
                        message.obj = "服务器异常";
                        bindCarHandler.sendMessage(message);
                    }
                });
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String msg) {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage(msg);
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
     * msg的标志数字
     */
    private final int BIND_SUCCESS = 4001;
    private final int BIND_FAILURE = 4002;
    private final int QUERY_SUCCESS = 4003;
    private final int DELETE_SUCCESS = 4004;

    private Handler bindCarHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BIND_SUCCESS:
                    String response = (String) msg.obj;
                    doBindResponseSuccess(response);
                    dissmissProgressDialog();
                    break;
                case QUERY_SUCCESS:
                    String queryResponse = (String) msg.obj;
                    doQueryResponseSuccess(queryResponse);
                    break;
                case DELETE_SUCCESS:
                    String deleteResponse = (String) msg.obj;
                    doDeleteResponseSuccess(deleteResponse);
                    break;
                case BIND_FAILURE:
                    String failureText = (String) msg.obj;
                    ToastUtil.show(CarInfoActivity.this, failureText);
                    dissmissProgressDialog();
                    break;
            }
        }
    };

    /**
     * 处理服务器响应成功的查询数据
     *
     * @param queryResponse 服务器返回的数据
     */
    private void doQueryResponseSuccess(String queryResponse) {
        carData = JSONUtil.parseUserCarsJSON(queryResponse);
        if (carData == null) {
            carData = new ArrayList<>();
            ToastUtil.show(this, "没有绑定的车辆");
        }
        // 将数据交给适配器
        carInfoAdapter = new CarInfoAdapter(this, carData);
        // 将列表与适配器绑定
        listCarInfo.setAdapter(carInfoAdapter);
        // 列表为空的时候显示
        listCarInfo.setEmptyView(llCarInfoListEmpty);
        dissmissProgressDialog();
    }

    /**
     * 处理响应成功的服务器数据
     *
     * @param response 服务器返回的数据
     */
    private void doBindResponseSuccess(String response) {
        UserCar userCar = JSONUtil.parseUserCarJSON(response);
        // 当响应数据能正常解析的时候
        if (userCar != null) {
            ToastUtil.show(this, "绑定成功");
            // 将绑定车辆添加到绑定车辆列表
            carData.add(userCar);
            carInfoAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.show(this, "绑定失败，车辆信息错误或该车辆已被绑定");
        }
    }

    /**
     * 处理响应成功的服务器数据
     * @param response  服务器返回的数据
     */
    private void doDeleteResponseSuccess(String response) {
        int deleteResult = JSONUtil.parseDeleteCarJSON(response);
        // 表示删除成功
        if (deleteResult > 0) {
            ToastUtil.show(this,"解绑成功");
            carData.remove(currentItemIndex);
            carInfoAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.show(this,"解绑失败，该车辆已被解绑");
        }
        dissmissProgressDialog();
    }

}
