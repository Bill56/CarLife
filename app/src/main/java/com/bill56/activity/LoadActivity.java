package com.bill56.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;



import com.bill56.carlife.MainActivity;
import com.bill56.carlife.R;
import com.bill56.entity.User;
import com.bill56.listener.HttpCallbackListener;
import com.bill56.util.ActivityUtil;
import com.bill56.util.HttpUtil;
import com.bill56.util.JSONUtil;
import com.bill56.util.LogUtil;
import com.bill56.util.ToastUtil;
import com.bill56.util.ViewUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoadActivity extends BaseActivity {

    private TextInputLayout textInputLayout_phone;
    private TextInputLayout textInputLayout_pwd;
    private CheckBox checkBox_pwd;
    private final int LOAD_SUCCESS = 1001;
    private final int LOAD_FAILURE = 1002;
    /**
     * 处理线程返回信息的处理对象
     */
    private Handler loadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:
                    String response = (String) msg.obj;
                    doLoadResponseSuccess(response);
                    break;
                case LOAD_FAILURE:
                    String error = (String) msg.obj;
                    ToastUtil.show(LoadActivity.this, "服务器错误");
                default:
                    break;
            }
            findViewById(R.id.button_load).setEnabled(true);
        }
    };

    private void doLoadResponseSuccess(String response) {
        // 解析服务器的数据
        User u = JSONUtil.parseUserJSON(response);
        if (u != null) {
            ToastUtil.show(this,"登录成功");
            //存储用户信息
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoadActivity.this);
            SharedPreferences.Editor editor = preferences.edit();
            LogUtil.d(LogUtil.TAG, "提交editor");
            editor.putInt("id",u.getUserId());
            editor.putString("name", u.getUserNick());
            editor.putString("phone", textInputLayout_phone.getEditText().getText().toString());
            editor.putString("pwd", textInputLayout_pwd.getEditText().getText().toString());
            editor.putString("sex", u.getUserSex());
            editor.putString("email", u.getUserEmail());
            editor.putString("date", u.getUserCreateTime());
            //editor提交
            editor.commit();
            Intent intent = new Intent(LoadActivity.this, MainActivity.class);
            ActivityUtil.finishExcept(this);
            startActivity(intent);
            finish();
        } else {
            ToastUtil.show(LoadActivity.this,"用户名或密码错误");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        // 修改actionBar的信息
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.load_title);
        actionBar.setDisplayHomeAsUpEnabled(true);
        textInputLayout_phone = (TextInputLayout) findViewById(R.id.textlayout_phone);
        textInputLayout_pwd = (TextInputLayout) findViewById(R.id.textlayout_pwd);
        checkBox_pwd = (CheckBox) findViewById(R.id.checkBox_pwd);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("person", MODE_PRIVATE);
        boolean isChecked = preferences.getBoolean("checked", false);
        Log.d("getPhone", "phone");
        textInputLayout_phone.getEditText().setText(preferences.getString("phone", ""));
        if (isChecked) {
            Log.d("getPwd", "pwd");
            checkBox_pwd.setChecked(true);
            textInputLayout_pwd.getEditText().setText(preferences.getString("pwd", ""));
        }
    }

    /**
     * 登陆操作
     *
     * @param v
     */
    public void Load(View v) {
        v.setEnabled(false);
        EditText editText_phone = textInputLayout_phone.getEditText();
        String phone = editText_phone.getText().toString();
        EditText editText_pwd = textInputLayout_pwd.getEditText();
        String pwd = editText_pwd.getText().toString();
        // 验证手机和密码格式是否正确
        if (VerifyPhone(phone)) {
            //出错信息清空
            textInputLayout_phone.setError("");
            if (VerifyPwd(pwd)) {
                textInputLayout_pwd.setError("");
                String requestData = JSONUtil.createUserJSON(phone,pwd);
                HttpUtil.sendHttpRequestToInner(HttpUtil.REQUEST_LOGIN, requestData, new HttpCallbackListener() {
                    Message message = new Message();
                    @Override
                    public void onFinish(String response) {
                        message.what = LOAD_SUCCESS;
                        message.obj = response;
                        loadHandler.sendMessage(message);
                    }
                    @Override
                    public void onError(Exception e) {
                        message.what = LOAD_FAILURE;
                        message.obj = "服务器异常";
                        loadHandler.sendMessage(message);
                    }
                });
                //存储用户名、密码
                SharedPreferences preferences1 = getSharedPreferences("person", MODE_PRIVATE);
                SharedPreferences.Editor editor1 = preferences1.edit();
                editor1.putString("phone", textInputLayout_phone.getEditText().getText().toString());
                editor1.putString("pwd", textInputLayout_pwd.getEditText().getText().toString());
                editor1.putBoolean("checked", checkBox_pwd.isChecked());
                editor1.commit();

                /*//存储用户信息
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                Log.d("load", "提交editor");
                editor.putString("name", "中国");
                editor.putString("phone", textInputLayout_phone.getEditText().getText().toString());
                editor.putString("pwd", textInputLayout_pwd.getEditText().getText().toString());
                editor.putString("sex", "男");
                editor.putString("email", "12345678@qq.com");
                editor.putString("date", "2016.5.13");
                //editor提交
                editor.commit();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();*/
            } else {
                textInputLayout_pwd.setError("密码6-16字母和数字");
                v.setEnabled(true);
            }
        } else {
            textInputLayout_phone.setError("手机号11位");
            v.setEnabled(true);
        }
    }

    /**
     * 跳转注册界面
     *
     * @param v
     */
    public void Zhuce(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    //检验手机号
    public boolean VerifyPhone(String phone) {
        Pattern p = Pattern.compile("1\\d{10}");
        Matcher m = p.matcher(phone);
        boolean b = m.matches();
        return b;
    }

    //检验密码
    public boolean VerifyPwd(String pwd) {
        Pattern p = Pattern.compile("(?![0-9]+$)(?![a-zA-Z]+$)[0-9a-zA-Z]{6,16}");
        Matcher m = p.matcher(pwd);
        boolean b = m.matches();
        return b;
    }
}
