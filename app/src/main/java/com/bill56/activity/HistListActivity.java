package com.bill56.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;


import com.bill56.carlife.R;
import com.bill56.entity.OrderRefuOil;
import com.bill56.listener.HttpCallbackListener;
import com.bill56.util.HttpUtil;
import com.bill56.util.JSONUtil;
import com.bill56.util.QRCodeUtil;
import com.bill56.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 历史纪录活动
 */
public class HistListActivity extends BaseActivity {

    private SimpleAdapter adapter;
    private ArrayList<HashMap<String, Object>> data;
    private ListView listView_his;
    // 简单适配器所需的数组
    String[] from = {"orderId", "stname", "lmoney", "lprice", "lmass", "ltype", "ostart", "ostate"};
    int[] to = {R.id.textView_orderId, R.id.textView_stname, R.id.textView_lmoney,
            R.id.textView_lprice, R.id.textView_lmass, R.id.textView_ltype,
            R.id.textView_start_time, R.id.textView_orderState};
    // 显示进度对话框
    private ProgressDialog progDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hist_list);
        // 显示返回键
        getSupportActionBar().setTitle(R.string.activity_hist_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 初始化数据
        initListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    private void initListView() {
        listView_his = (ListView) findViewById(R.id.listView_his);
        // 列表数据初始化
        updateOrderOil();
        // 设置对ListView的点击事件监听
        listView_his.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 根据数据信息，生成二维码
                // 获得数据
                String orderNo = (String) data.get(position).get("orderId");
                String orderOilStation = (String) data.get(position).get("stname");
                float orderOilTotal = (float) data.get(position).get("lmoney");
                float orderOilPrice = (float) data.get(position).get("lprice");
                int orderOilMass = (int) data.get(position).get("lmass");
                String orderOilType = (String) data.get(position).get("ltype");
                String orderStartTime = (String) data.get(position).get("ostart");
                OrderRefuOil order = new OrderRefuOil();
                // 将数据封装
                order.setOrderNo(orderNo);
                order.setOrderOilStation(orderOilStation);
                order.setOrderOilTotal(orderOilTotal);
                order.setOrderOilPrice(orderOilPrice);
                order.setOrderOilMass(orderOilMass);
                order.setOrderOilType(orderOilType);
                order.setOrderStartTime(orderStartTime);
                // 弹出二维码对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(HistListActivity.this);
                View v = getLayoutInflater().inflate(R.layout.dialog_qrimg, null);
                ImageView imgQR = (ImageView) v.findViewById(R.id.imageView_qrcode);
                builder.setTitle(orderNo)
                .setView(v)
                .setPositiveButton(R.string.perinfo_button_ok,null);
                QRCodeUtil.createQRImage(order.toJsonString(), imgQR);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    /**
     * 从网络获取加油订单的所有记录
     */
    private void updateOrderOil() {
        showProgressDialog("正在加载......");
        // 获得用户id
        int userId = getIntent().getIntExtra("userId", 0);
        if ( userId > 0) {
            // 向服务器发送请求
            HttpUtil.sendHttpRequestToInner(HttpUtil.REQUEST_QUERY_ALL_ORDERS,
                    JSONUtil.createQueryOrderJSON(userId), new HttpCallbackListener() {
                        Message message = new Message();

                        @Override
                        public void onFinish(String response) {
                            message.what = QUERY_SUCCESS;
                            message.obj = response;
                            orderHandler.sendMessage(message);
                        }

                        @Override
                        public void onError(Exception e) {
                            message.what = QUERY_FAILURE;
                            message.obj = "服务器异常";
                            orderHandler.sendMessage(message);
                        }
                    });
        } else {
            ToastUtil.show(this,"加载失败...请先登录");
            dissmissProgressDialog();
        }
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
    private final int QUERY_FAILURE = 5005;
    private final int QUERY_SUCCESS = 4006;

    private Handler orderHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case QUERY_SUCCESS:
                    String queryResponse = (String) msg.obj;
                    doQueryResponseSuccess(queryResponse);
                    break;
                case QUERY_FAILURE:
                    String failureText = (String) msg.obj;
                    ToastUtil.show(HistListActivity.this, failureText);
                    dissmissProgressDialog();
                    break;
            }
        }
    };

    /**
     * 处理服务器响应成功的操作
     *
     * @param response 服务器返回的数据
     */
    private void doQueryResponseSuccess(String response) {
        List<OrderRefuOil> queryOrders = JSONUtil.parseQueryOrderJSON(response);
        data = new ArrayList<>();
        if (queryOrders == null) {
            data.add(new HashMap<String, Object>());
            ToastUtil.show(this, "没有任何订单");
        } else {
            // 遍历订单结果，将数据给适配器数据
            for (int i = 0;i<queryOrders.size();i++) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("orderId", "订单号:" + queryOrders.get(i).getOrderNo());
                map.put("stname", queryOrders.get(i).getOrderOilStation());
                map.put("lmoney", queryOrders.get(i).getOrderOilTotal());
                map.put("lprice", queryOrders.get(i).getOrderOilPrice());
                map.put("lmass", queryOrders.get(i).getOrderOilMass());
                map.put("ltype", queryOrders.get(i).getOrderOilType());
                map.put("ostart", queryOrders.get(i).getOrderStartTime());
                map.put("ostate", queryOrders.get(i).getOrderStateName());
                // 添加到map
                data.add(map);
            }
        }
        // 将数据给适配器
        adapter = new SimpleAdapter(this, data, R.layout.list_his, from, to);
        listView_his.setAdapter(adapter);
        dissmissProgressDialog();
    }


}
