package com.bill56.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.bill56.carlife.MainActivity;
import com.bill56.carlife.R;
import com.bill56.entity.User;
import com.bill56.listener.HttpCallbackListener;
import com.bill56.util.ActivityUtil;
import com.bill56.util.HttpUtil;
import com.bill56.util.JSONUtil;
import com.bill56.util.LogUtil;
import com.bill56.util.SJNum;
import com.bill56.util.ToastUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends BaseActivity {

    private EditText editText_phone;
    private EditText editText_num;
    private EditText editText_pwd;
    private Button button_getNum;
    private String sjNum;

    private final int REGISTER_SUCCESS = 2001;
    private final int REGISTER_FAILURE = 2002;
    /**
     * 处理线程返回信息的处理对象
     */
    private Handler loadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REGISTER_SUCCESS:
                    String response = (String) msg.obj;
                    doRegisterResponseSuccess(response);
                    break;
                case REGISTER_FAILURE:
                    String error = (String) msg.obj;
                    ToastUtil.show(RegisterActivity.this, "服务器错误");
                default:
                    break;
            }
            findViewById(R.id.button_zhuce).setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //添加左上角返回按钮
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        editText_phone = (EditText) findViewById(R.id.editText_phone);
        editText_num = (EditText) findViewById(R.id.editText_num);
        editText_pwd = (EditText) findViewById(R.id.editText_pwd);
        button_getNum = (Button) findViewById(R.id.button_getNum);

        button_getNum.setOnClickListener(onClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 验证按钮点击事件
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String phone = editText_phone.getText().toString();
            String pwd = editText_pwd.getText().toString();
//                判断手机号码和密码是否合法
            if (VerifyPhone(phone) && VerifyPwd(pwd)) {
                Asyn asyn = new Asyn();
                asyn.execute();
            } else {
                Toast.makeText(RegisterActivity.this, "手机号或密码不合法", Toast.LENGTH_SHORT).show();
            }
            button_getNum.setClickable(false);
        }
    };

    /**
     * 发送短信验证码
     *
     * @param phoneNumber
     * @param content
     */
    public String sendSmsCode(String phoneNumber, String content) {
        if (phoneNumber == null || content == null) {
            return null;
        }
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        String httpUrl = "http://apis.baidu.com/baidu_communication/sms_verification_code/smsverifycode";
        String httpArg = "phone=" + phoneNumber + "&content=" + content;
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // ????apikey??HTTP header
            connection.setRequestProperty("apikey",
                    "db642b2fac4fafe26849179ad8883592 ");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
            Log.i("result", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    class Asyn extends AsyncTask<Void, Void, Void> {

        String phone = String.valueOf(editText_phone.getText());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //获取随机验证码
            SJNum.getNum();
            sjNum = SJNum.num;
        }

        @Override
        protected Void doInBackground(Void... params) {
            sendSmsCode(phone, sjNum);
            return null;
        }
    }

    public boolean VerifyPhone(String phone) {
        Pattern p = Pattern.compile("1\\d{10}");
        Matcher m = p.matcher(phone);
        boolean b = m.matches();
        return b;
    }

    public boolean VerifyPwd(String pwd) {
        Pattern p = Pattern.compile("(?![0-9]+$)(?![a-zA-Z]+$)[0-9a-zA-Z]{6,16}");
        Matcher m = p.matcher(pwd);
        boolean b = m.matches();
        return b;
    }

    public boolean VerifyNum(String num) {
        Pattern p = Pattern.compile("\\d{6}");
        Matcher m = p.matcher(num);
        boolean b = m.matches();
        return b;
    }

    public void register(View v) {
        v.setEnabled(false);
        button_getNum.setClickable(true);
        String num = editText_num.getText().toString();
        if (VerifyNum(num) && sjNum.equals(num)) {
            // 发送注册信息给服务器
            // 获取手机号和密码
            String userTel = editText_phone.getText().toString();
            String userPwd = editText_pwd.getText().toString();
            String requstJSON = JSONUtil.createUserJSON(userTel,userPwd);
            HttpUtil.sendHttpRequestToInner(HttpUtil.REQUEST_REGISRER, requstJSON, new HttpCallbackListener() {
                Message message = new Message();
                @Override
                public void onFinish(String response) {
                    message.what = REGISTER_SUCCESS;
                    message.obj = response;
                    loadHandler.sendMessage(message);
                }

                @Override
                public void onError(Exception e) {
                    message.what = REGISTER_FAILURE;
                    message.obj = "服务器异常";
                    loadHandler.sendMessage(message);
                }
            });
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "验证码错误!请重新获取", Toast.LENGTH_SHORT).show();
            v.setEnabled(true);
        }
        sjNum = "0000000";
        editText_num.setText("");
    }

    private void doRegisterResponseSuccess(String response) {
        // 解析服务器的数据
        User u = JSONUtil.parseUserJSON(response);
        if (u != null) {
            ToastUtil.show(this,"注册成功");
            //存储用户信息
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            LogUtil.d(LogUtil.TAG, "提交editor");
            editor.putInt("id", u.getUserId());
            editor.putString("name", u.getUserNick());
            editor.putString("phone", editText_phone.getText().toString());
            editor.putString("pwd", editText_pwd.getText().toString());
            editor.putString("sex", u.getUserSex());
            editor.putString("email", u.getUserEmail());
            editor.putString("date", u.getUserCreateTime());
            //editor提交
            editor.commit();
            ActivityUtil.finishExcept(this);
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            ToastUtil.show(RegisterActivity.this, "用户名或密码错误");
        }
    }

//    /**
//     * 添加存储
//     */
//    private void addInSP() {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putInt("id",001);
//        editor.putString("name", "");
//        editor.putString("phone", editText_phone.getText().toString());
//        editor.putString("pwd", editText_pwd.getText().toString());
//        editor.putString("sex", "男");
//        editor.putString("email", "");
//        editor.putString("date", "");
//        editor.commit();
//    }

}
