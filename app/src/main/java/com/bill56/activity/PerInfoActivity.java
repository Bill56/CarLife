package com.bill56.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.bill56.carlife.R;
import com.bill56.listener.HttpCallbackListener;
import com.bill56.util.HttpUtil;
import com.bill56.util.JSONUtil;
import com.bill56.util.ToastUtil;

import java.util.Iterator;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PerInfoActivity extends BaseActivity {

    // 存放用户id的变量
    private int userId;
    // 用户信息的头像
    private ImageView imageView_touxiang;
    // 用户昵称
    private TextView textView_name;
    // 用户手机
    private TextView textView_phone;
    // 用户性别图片
    private ImageView imageView_sex;
    // 用户性别
    private TextView textView_sex;
    // 用户邮箱
    private TextView textView_email;
    // 用户创建日期
    private TextView textView_date;
    // 用户修改的按钮
    private LinearLayout llPerinfoUpdateNick;
    private LinearLayout llPerinfoUpdateSex;
    private LinearLayout llPerinfoUpdateEmail;
    private LinearLayout llPerinfoUpdateTel;
    private LinearLayout llPerinfoUpdatePassword;
    // 保存当前正在修改的信息
    private String currentUpdateInfo;
    // 保存键值对的对象
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_per_info);
        init();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        readSharedPreference();
    }


    private void readSharedPreference() {
        // 从键值对文件读取保存的用户信息
        userId = preferences.getInt("id", 0);
        textView_name.setText(preferences.getString("name", "Name"));
        textView_phone.setText(preferences.getString("phone", "Phone"));
        String sex = preferences.getString("sex", "Sex");
        if (sex.equals("男")) {
            imageView_sex.setImageDrawable(getDrawable(R.drawable.man));
        } else if (sex.equals("女")) {
            imageView_sex.setImageDrawable(getDrawable(R.drawable.woman));
        } else {
            imageView_sex.setImageDrawable(getDrawable(R.drawable.ic_people_outline_24dp));
        }
        textView_sex.setText(sex);
        textView_email.setText(preferences.getString("email", "Email"));
        textView_date.setText(preferences.getString("date", "Date"));
    }

    /**
     * 初始化界面
     */
    private void init() {
        // 修改actionBar的信息
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.per_info_title);
        actionBar.setDisplayHomeAsUpEnabled(true);
        // 绑定控件
        imageView_touxiang = (ImageView) findViewById(R.id.imageView_Gtouxiang);
        textView_name = (TextView) findViewById(R.id.textView_name);
        textView_phone = (TextView) findViewById(R.id.textView_phone);
        textView_sex = (TextView) findViewById(R.id.textView_sex);
        imageView_sex = (ImageView) findViewById(R.id.imageView_sex);
        textView_email = (TextView) findViewById(R.id.textView_email);
        textView_date = (TextView) findViewById(R.id.textView_date);
        // 绑定修改控件
        llPerinfoUpdateNick = (LinearLayout) findViewById(R.id.ll_perinfo_update_nick);
        llPerinfoUpdateTel = (LinearLayout) findViewById(R.id.ll_perinfo_update_tel);
        llPerinfoUpdateEmail = (LinearLayout) findViewById(R.id.ll_perinfo_update_email);
        llPerinfoUpdatePassword = (LinearLayout) findViewById(R.id.ll_perinfo_update_password);
        llPerinfoUpdateSex = (LinearLayout) findViewById(R.id.ll_perinfo_update_sex);
        // 为修改控件注册监听器
        PerinfoUpdateClickListener listener = new PerinfoUpdateClickListener();
        llPerinfoUpdateNick.setOnClickListener(listener);
        llPerinfoUpdateTel.setOnClickListener(listener);
        llPerinfoUpdateEmail.setOnClickListener(listener);
        llPerinfoUpdatePassword.setOnClickListener(listener);
        llPerinfoUpdateSex.setOnClickListener(listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back, menu);
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
     * 退出登陆，清空SharedPreferences
     *
     * @param v
     */
    public void Quit(View v) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
//        editor.remove("name");
//        editor.remove("phone");
//        editor.remove("person");
//        editor.remove("sex");
//        editor.remove("email");
//        editor.remove("date");
        editor.commit();
        Intent intent = new Intent(this, LoadActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 修改按钮的点击事件监听器
     */
    private class PerinfoUpdateClickListener implements View.OnClickListener {

        // 点击按钮后要弹出的对话框
        private AlertDialog dialog;
        // 构造对话框的构造器
        private AlertDialog.Builder builder = new AlertDialog.Builder(PerInfoActivity.this);

        // 初始化构造器的方法
        private void initDialog() {
            builder.setCancelable(false);
            builder.setNegativeButton(R.string.perinfo_button_cancel, null);
        }

        // 初始化完毕要弹出时候调用
        private void showDialog() {
            dialog = builder.create();
            dialog.show();
        }

        @Override
        public void onClick(View v) {
            initDialog();
            // 根据点击的视图的id来执行不同的方法
            switch (v.getId()) {
                case R.id.ll_perinfo_update_nick:
                    doUpdateBase(builder, "修改昵称", "昵称", HttpUtil.REQUEST_UPDATE_NICK, "userNick", "name", "修改失败");
                    break;
                case R.id.ll_perinfo_update_tel:
                    doUpdateBase(builder, "修改手机", "手机号码", HttpUtil.REQUEST_UPDATE_TEL, "userTel", "phone", "该手机号已被注册");
                    break;
                case R.id.ll_perinfo_update_email:
                    doUpdateBase(builder, "修改邮箱", "邮箱地址", HttpUtil.REQUEST_UPDATE_EMAIL, "userEmail", "email", "该邮箱地址已被注册");
                    break;
                case R.id.ll_perinfo_update_sex:
                    doUpdateSex(builder, "修改性别");
                    break;
                case R.id.ll_perinfo_update_password:
                    doUpdatePassword(builder, "修改密码");
                    break;
                default:
                    break;
            }
            showDialog();
        }
    }

    /**
     * 点击修改昵称、手机、邮箱时候执行的操作
     *
     * @param build       需要打开的对话框的构造器对象
     * @param title       对话框的标题
     * @param hint        对话框中输入框的提示信息
     * @param requstCode  请求服务器的请求代码
     * @param updateField 修改的字段
     * @param sharedName  存放在键值对文件的键名
     * @param failureText 修改失败的提示文本
     */
    private void doUpdateBase(AlertDialog.Builder build, String title, String hint,
                              final int requstCode, final String updateField, final String sharedName,
                              final String failureText) {
        build.setTitle(title);
        View v = getLayoutInflater().inflate(R.layout.dialog_update_edit, null);
        // 获取编辑文本器
        final EditText editUpdateBase = (EditText) v.findViewById(R.id.edit_update_base);
        editUpdateBase.setHint(hint);
        // 将编辑器装入构造器
        build.setView(v);
        // 设置确认按钮的点击事件
        build.setPositiveButton(R.string.perinfo_button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 获取输入
                String userInput = editUpdateBase.getText().toString();
                if (TextUtils.isEmpty(userInput)) {
                    ToastUtil.show(PerInfoActivity.this, "输入不能为空");
                    return;
                }
                showProgressDialog();
                // 将获取到的值赋值给当前输入的信息变量
                currentUpdateInfo = userInput;
                HttpUtil.sendHttpRequestToInner(
                        requstCode,
                        JSONUtil.createUpdateJSON(userId, updateField, userInput),
                        new HttpCallbackListener() {
                            Message message = new Message();

                            @Override
                            public void onFinish(String response) {
                                int updateResult = JSONUtil.parseUpdateJSON(response);
                                if (updateResult > 0) {
                                    message.what = UPDATE_SUCCESS;
                                    // 存放需要修改的键值对中的键名
                                    message.obj = sharedName;
                                } else {
                                    message.what = UPDATE_FAILURE;
                                    message.obj = failureText;
                                }
                                updateHandler.sendMessage(message);
                            }

                            @Override
                            public void onError(Exception e) {
                                message.what = UPDATE_FAILURE;
                                message.obj = e.getMessage();
                                updateHandler.sendMessage(message);
                            }
                        });
            }
        });
    }

    /**
     * 点击修改性别后执行的代码
     *
     * @param builder 创建对话框的构造器对象
     * @param title   对话框的标题
     */
    private void doUpdateSex(AlertDialog.Builder builder, String title) {
        builder.setTitle(title);
        final View v = getLayoutInflater().inflate(R.layout.dialog_update_sex, null);
        // 获取单选按钮组
        final RadioGroup sexGroup = (RadioGroup) v.findViewById(R.id.radio_group_sex);
        // 将单选按钮组装入对话框
        builder.setView(v);
        // 设置对话框的点击事件
        builder.setPositiveButton(R.string.perinfo_button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 获取选择的文本
                int checkedId = sexGroup.getCheckedRadioButtonId();
                RadioButton checkedRadioButton = (RadioButton) v.findViewById(checkedId);
                String userSex = checkedRadioButton.getText().toString();
                if (TextUtils.isEmpty(userSex)) {
                    ToastUtil.show(PerInfoActivity.this, "请选择性别");
                    return;
                }
                showProgressDialog();
                currentUpdateInfo = userSex;
                // 发送数据给服务器
                HttpUtil.sendHttpRequestToInner(
                        HttpUtil.REQUEST_UPDATE_SEX,
                        JSONUtil.createUpdateJSON(userId, "userSex", userSex),
                        new HttpCallbackListener() {
                            Message message = new Message();

                            @Override
                            public void onFinish(String response) {
                                int updateResult = JSONUtil.parseUpdateJSON(response);
                                if (updateResult > 0) {
                                    message.what = UPDATE_SUCCESS;
                                    // 存放需要修改的键值对中的键名
                                    message.obj = "sex";
                                } else {
                                    message.what = UPDATE_FAILURE;
                                    message.obj = "连接超时";
                                }
                                updateHandler.sendMessage(message);
                            }

                            @Override
                            public void onError(Exception e) {
                                message.what = UPDATE_FAILURE;
                                message.obj = e.getMessage();
                                updateHandler.sendMessage(message);
                            }
                        });
            }
        });
    }

    /**
     * 点击修改密码后执行的代码
     *
     * @param builder 创建对话框的构造器对象
     * @param title   对话框的标题
     */
    private void doUpdatePassword(AlertDialog.Builder builder, String title) {
        builder.setTitle(title);
        final View v = getLayoutInflater().inflate(R.layout.dialog_update_editpwd, null);
        // 获取两个编辑器
        final EditText editPassword = (EditText) v.findViewById(R.id.edit_update_password);
        final EditText editSurePassword = (EditText) v.findViewById(R.id.edit_sure_password);
        // 将单选按钮组装入对话框
        builder.setView(v);
        // 设置对话框的点击事件
        builder.setPositiveButton(R.string.perinfo_button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userPwd = editPassword.getText().toString();
                String surePwd = editSurePassword.getText().toString();
                if (TextUtils.isEmpty(userPwd)) {
                    ToastUtil.show(PerInfoActivity.this, "密码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(surePwd)) {
                    ToastUtil.show(PerInfoActivity.this, "确认密码不能为空");
                    return;
                }
                if (!userPwd.equals(surePwd)) {
                    ToastUtil.show(PerInfoActivity.this, "两次输入的密码不一致");
                    return;
                }
                // 当密码不符合规定的时候
                if (!VerifyPwd(userPwd)) {
                    ToastUtil.show(PerInfoActivity.this, "密码必须由16位内的字母和数字的组合");
                    return;
                }
                showProgressDialog();
                currentUpdateInfo = userPwd;
                // 发送数据给服务器
                HttpUtil.sendHttpRequestToInner(
                        HttpUtil.REQUEST_UPDATE_PASSWORD,
                        JSONUtil.createUpdateJSON(userId, "userPwd", userPwd),
                        new HttpCallbackListener() {
                            Message message = new Message();

                            @Override
                            public void onFinish(String response) {
                                int updateResult = JSONUtil.parseUpdateJSON(response);
                                if (updateResult > 0) {
                                    message.what = UPDATE_SUCCESS;
                                    // 存放需要修改的键值对中的键名
                                    message.obj = "pwd";
                                } else {
                                    message.what = UPDATE_FAILURE;
                                    message.obj = "连接超时";
                                }
                                updateHandler.sendMessage(message);
                            }

                            @Override
                            public void onError(Exception e) {
                                message.what = UPDATE_FAILURE;
                                message.obj = e.getMessage();
                                updateHandler.sendMessage(message);
                            }
                        });
            }
        });
    }

    /**
     * msg的标志数字
     */
    private final int UPDATE_SUCCESS = 3001;
    private final int UPDATE_FAILURE = 3002;
    /**
     * 处理网络线程返回响应的处理类对象
     */
    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_SUCCESS:
                    SharedPreferences.Editor editor = preferences.edit();
                    String sharedName = (String) msg.obj;
                    editor.putString(sharedName, currentUpdateInfo);
                    editor.commit();
                    readSharedPreference();
                    ToastUtil.show(PerInfoActivity.this, "信息修改成功");
                    dissmissProgressDialog();
                    if ("pwd".equals(sharedName)) {
                        showForceQuitDialog();
                    }
                    break;
                case UPDATE_FAILURE:
                    String failureText = (String) msg.obj;
                    ToastUtil.show(PerInfoActivity.this, failureText);
                    dissmissProgressDialog();
                    break;
            }

        }
    };

    private ProgressDialog progDialog = null;

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在修改......");
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
     * 强制退出时弹出的对话框
     */
    private void showForceQuitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("您已修改密码，请重新登录");
        builder.setCancelable(false);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Quit(findViewById(R.id.button_quit));
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean VerifyPwd(String pwd) {
        Pattern p = Pattern.compile("(?![0-9]+$)(?![a-zA-Z]+$)[0-9a-zA-Z]{6,16}");
        Matcher m = p.matcher(pwd);
        boolean b = m.matches();
        return b;
    }

}