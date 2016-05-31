package com.bill56.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.bill56.carlife.R;
import com.bill56.listener.HttpCallbackListener;
import com.bill56.util.HttpUtil;
import com.bill56.util.JSONUtil;
import com.bill56.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GasoinfoActivity extends BaseActivity {

    private String data;
    private int index;
    private TextView textView_gstation;
    private TextView textView_bname;
    private RadioGroup group;
    private EditText editText_l;
    private EditText editText_money;
    private RadioButton radioButton_1;
    private RadioButton radioButton_2;
    private RadioButton radioButton_3;
    private RadioButton radioButton_4;

    //预约加油数据
    private String gas_90;
    private String gas_93;
    private String gas_97;
    private String gas_0;
    private float prices;
    private String name;
    private String orderNo;

    //对话框信息
    private int paycount;
    private int result;
    private float orderOilTotal;
    private int orderOilMass;
    private AlertDialog dialog;
    private String orderOilType;

    private TextView textView_statename;
    private TextView textView_money;
    private TextView textView_mass;
    private TextView textView_type;
    private TextView textView_prices;
    // 显示进度对话框
    private ProgressDialog progDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gasoinfo);
        // 显示返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //绑定
        textView_gstation = (TextView) findViewById(R.id.textView_gstation);
        textView_bname = (TextView) findViewById(R.id.textView_bname);
        group = (RadioGroup) findViewById(R.id.group);
        editText_l = (EditText) findViewById(R.id.editText_l);
        editText_money = (EditText) findViewById(R.id.editText_money);
        radioButton_1 = (RadioButton) findViewById(R.id.radioButton);
        radioButton_2 = (RadioButton) findViewById(R.id.radioButton2);
        radioButton_3 = (RadioButton) findViewById(R.id.radioButton3);
        radioButton_4 = (RadioButton) findViewById(R.id.radioButton4);
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    private void initDialog() {

        View view = getLayoutInflater().inflate(R.layout.dialog_pay, null);
        textView_statename = (TextView) view.findViewById(R.id.textView_stateName);
        textView_money = (TextView) view.findViewById(R.id.textView_money);
        textView_mass = (TextView) view.findViewById(R.id.textView_mass);
        textView_type = (TextView) view.findViewById(R.id.textView_type);
        textView_prices = (TextView) view.findViewById(R.id.textView_prices);

        textView_statename.setText(name);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showProgressDialog("正在支付......");
                // 访问网络修改订单状态
                HttpUtil.sendHttpRequestToInner(HttpUtil.REQUEST_UPDATE_ORDER_STATE,
                        JSONUtil.createUpdateOrderStateJSON(orderNo, 2), new HttpCallbackListener() {
                            Message message = new Message();

                            @Override
                            public void onFinish(String response) {
                                message.what = UPDATE_SUCCESS;
                                message.obj = response;
                                addHandler.sendMessage(message);
                            }

                            @Override
                            public void onError(Exception e) {
                                message.what = ADD_FAILURE;
                                message.obj = "服务器异常";
                                addHandler.sendMessage(message);
                            }
                        });
            }
        });
        builder.setNegativeButton("取消", null);


        textView_money.setText(String.valueOf(orderOilTotal));
        textView_mass.setText(String.valueOf(orderOilMass));
        textView_type.setText(String.valueOf(orderOilType));
        textView_prices.setText(String.valueOf(prices));

        //显示对话框
        dialog = builder.create();
        dialog.show();
    }

    private void init() throws JSONException {
        //获取数据项
        Intent intent = getIntent();
        data = intent.getStringExtra("maps");
        index = intent.getIntExtra("index", -1);

        JSONArray array = new JSONArray(data);
        JSONObject object = array.getJSONObject(index);
        String id = object.getString("id");
        name = object.getString("name");
        String area = object.getString("area");
        String areaname = object.getString("areaname");
        String address = object.getString("address");
        String brandname = object.getString("brandname");
        String type = object.getString("type");
        String discount = object.getString("distance");
        String exhaust = object.getString("exhaust");
        String position = object.getString("position");
        String lon = object.getString("lon");
        String lat = object.getString("lat");

        //获取价格
        String price = object.getString("price");
        String n_prices = "[" + price + "]";
        JSONArray array2 = new JSONArray(n_prices);
        JSONObject object2 = array2.getJSONObject(0);
        gas_90 = object2.getString("E90");
        gas_93 = object2.getString("E93");
        gas_97 = object2.getString("E97");
        gas_0 = object2.getString("E0");

        String gastprice = object.getString("gastprice");
        String fwlsmc = object.getString("fwlsmc");
        String distance = object.getString("distance");

        textView_gstation.setText(name);
        textView_bname.setText(brandname);
        radioButton_1.setChecked(true);
        prices = Float.parseFloat(gas_90);
    }

    private void addListener() {
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton:
                        radioButton_1.setChecked(true);
                        prices = Float.parseFloat(gas_90);
                        break;
                    case R.id.radioButton2:
                        radioButton_2.setChecked(true);
                        prices = Float.parseFloat(gas_93);
                        break;
                    case R.id.radioButton3:
                        radioButton_3.setChecked(true);
                        prices = Float.parseFloat(gas_97);
                        break;
                    case R.id.radioButton4:
                        radioButton_4.setChecked(true);
                        prices = Float.parseFloat(gas_0);
                        break;
                }
            }
        });

        editText_l.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String str = editText_l.getText().toString();
                    int l;
                    if (str == null) {
                        l = 0;
                    } else {
                        l = Integer.parseInt(str);
                    }
                    editText_money.setText(conversionToMoney(l));
                }
            }
        });

        editText_money.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String str = editText_money.getText().toString();
                    if (str != null) {
                        float money = Float.parseFloat(str);
                        editText_l.setText(conversionToL(money));
                        editText_l.setEnabled(true);
                    }
                }
            }
        });
    }

    private String conversionToL(float money) {
        float f = money / prices;
        int l = (int) (f + 0.5);
        String s = String.valueOf(l);
        return s;
    }

    private String conversionToMoney(int l) {
        float f = ((float) l) * prices;
        String s = String.valueOf(f);
        return s;
    }

    public void Submit(View v) throws JSONException {
        showProgressDialog("正在下单，请稍等......");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        int useId = preferences.getInt("id", 0);
        if (useId > 0) {

            long stamp = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            orderNo = sdf.format(new Date(stamp));
            Log.d("Gasoinfo", orderNo);

            String orderOilstation = name;

            orderOilType = null;
            int type = group.getCheckedRadioButtonId();
            switch (type) {
                case R.id.radioButton:
                    orderOilType = "90#";
                    break;
                case R.id.radioButton2:
                    orderOilType = "93#";
                    break;
                case R.id.radioButton3:
                    orderOilType = "97#";
                    break;
                case R.id.radioButton4:
                    orderOilType = "0#";
                    break;
            }

            String str = editText_money.getText().toString();
            if (str != null) {
                orderOilTotal = Float.parseFloat(str);
            } else {
                return;
            }

            float f = orderOilTotal / prices;
            orderOilMass = (int) (f + 0.5);
            float orderOilPrice = prices;

            int orderStateNo = 1;

            // To JSON
            JSONArray array = new JSONArray();
            JSONObject object = new JSONObject();
            object.put("useId", useId);
            object.put("orderNo", orderNo);
            object.put("orderOilstation", orderOilstation);
            object.put("orderOilType", orderOilType);
            object.put("orderOilMass", orderOilMass);
            object.put("orderOilPrice", orderOilPrice);
            object.put("orderOilTotal", orderOilTotal);
            object.put("orderStateNo", orderStateNo);
            array.put(object);
            String json = array.toString();
            Log.d("Gasoinfo", json);
            // 发送数据给服务器
            HttpUtil.sendHttpRequestToInner(HttpUtil.REQUEST_ADD_ORDER,
                    json, new HttpCallbackListener() {
                        Message message = new Message();

                        @Override
                        public void onFinish(String response) {
                            message.what = ADD_SUCCESS;
                            message.obj = response;
                            addHandler.sendMessage(message);
                        }

                        @Override
                        public void onError(Exception e) {
                            message.what = ADD_FAILURE;
                            message.obj = "服务器异常";
                            addHandler.sendMessage(message);
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
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


    // 服务器响应类型标志
    private final int ADD_SUCCESS = 5001;
    private final int ADD_FAILURE = 5002;
    private final int UPDATE_SUCCESS = 5003;

    private Handler addHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ADD_SUCCESS:
                    String response = (String) msg.obj;
                    doAddResponseSuccess(response);
                    break;
                case UPDATE_SUCCESS:
                    String updateResponse = (String) msg.obj;
                    doUpdateResponseSuccess(updateResponse);
                    break;
                case ADD_FAILURE:
                    String failureText = (String) msg.obj;
                    ToastUtil.show(GasoinfoActivity.this, failureText);
                    dissmissProgressDialog();
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 解析服务器响应成功后的数据
     *
     * @param response 服务器响应数据
     */
    private void doAddResponseSuccess(String response) {
        int addResult = JSONUtil.parseAddOrderResultJSON(response);
        // 网络请求完成，关闭对话框
        dissmissProgressDialog();
        // 表示下单成功
        if (addResult > 0) {
            // 弹出支付对话框
            initDialog();
        } else {
            ToastUtil.show(this,"网络繁忙，请再试一次");
        }
    }

    /**
     * 解析服务器响应修改成功后的数据
      * @param response   服务器响应的数据
     */
    private void doUpdateResponseSuccess(String response) {
        int updateResult = JSONUtil.parseUpdateOrderResultJSON(response);
        // 网络请求完成，关闭对话框
        dissmissProgressDialog();
        // 表示支付成功
        if (updateResult > 0) {
            // 跳到支付成功页面
            Intent intent = new Intent(GasoinfoActivity.this, PayResultActivity.class);
            //是否预订成功
//                intent.putExtra("result", result);
            startActivity(intent);
        } else {
            ToastUtil.show(this,"网络繁忙，支付失败");
        }
    }


}
