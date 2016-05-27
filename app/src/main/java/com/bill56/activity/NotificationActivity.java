package com.bill56.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bill56.adapter.NotificationAdapter;
import com.bill56.carlife.R;
import com.bill56.entity.CarNotification;
import com.bill56.listener.HttpCallbackListener;
import com.bill56.util.HttpUtil;
import com.bill56.util.JSONUtil;
import com.bill56.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bill56 on 2016/5/26.
 */
public class NotificationActivity extends BaseActivity {

    // 用户界面组件
    private ListView listNotification;
    // 适配器
    private NotificationAdapter notifiAdapter;
    // 存放数据的List
    private List<CarNotification> notifiData;
    // 没有数据时候显示的布局
    private LinearLayout llNotifiListEmpty;
    // 显示的加载框
    private ProgressDialog progDialog;

    // 保存传递过来的userId
    private int userId;
    // 当点选中的条目的索引
    private int currentItemIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        userId = getIntent().getIntExtra("userId", 0);
        // 显示返回按钮
        ActionBar bar = getSupportActionBar();
        bar.setTitle(R.string.notification_title);
        bar.setDisplayHomeAsUpEnabled(true);
        // 初始化控件和数据
        initView();
        // 注册上下文菜单
        registerForContextMenu(listNotification);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        // 绑定控件
        listNotification = (ListView) findViewById(R.id.list_notification);
        llNotifiListEmpty = (LinearLayout) findViewById(R.id.ll_notification_list_empty);
        updateNotification();
        // 为通知列表注册点击监听器
        listNotification.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 获取选中的时间和内容
                long notifiTime = notifiData.get(position).getNotifiTime();
                String notifiContent = notifiData.get(position).getNotifiContent();
                // 启动活动
                Intent notifiDetailIntent = new Intent(NotificationActivity.this, NotificationDetailActivity.class);
                notifiDetailIntent.putExtra("notifiTime", notifiTime);
                notifiDetailIntent.putExtra("notifiContent", notifiContent);
                startActivity(notifiDetailIntent);
            }
        });
    }

    /**
     * 从服务器更新通知
     */
    private void updateNotification() {
        showProgressDialog("正在加载......");
        // 发起网络请求
        HttpUtil.sendHttpRequestToInner(HttpUtil.REQUEST_QUERY_NOTIFICATION,
                JSONUtil.createQueryNotificationJSON(userId), new HttpCallbackListener() {
                    Message message = new Message();

                    @Override
                    public void onFinish(String response) {
                        message.what = QUERY_SUCCESS;
                        message.obj = response;
                        notifiHandler.sendMessage(message);
                    }

                    @Override
                    public void onError(Exception e) {
                        message.what = QUERY_FAILURE;
                        message.obj = "服务器异常";
                        notifiHandler.sendMessage(message);
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
        getMenuInflater().inflate(R.menu.notification_option, menu);
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
            case R.id.action_clear_notification:
                doClearNotification();
                break;
            default:
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
        if (v == listNotification) {
            // 加载菜单
            getMenuInflater().inflate(R.menu.notification_context, menu);
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
            case R.id.action_delete_notifi:
                // 获得被点击的通知时间
                currentItemIndex = menuInfo.position;
                long notifiTime = notifiData.get(currentItemIndex).getNotifiTime();
                doDeleteNotification(notifiTime);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 根据通知时间删除一条通知
     *
     * @param notifiTime 通知时间
     */
    private void doDeleteNotification(long notifiTime) {
        showProgressDialog("正在删除......");
        HttpUtil.sendHttpRequestToInner(HttpUtil.REQUEST_DELETE_NOTIFICATION,
                JSONUtil.createDeleteNotifiJSON(notifiTime), new HttpCallbackListener() {
                    Message message = new Message();

                    @Override
                    public void onFinish(String response) {
                        message.what = DELETE_SUCCESS;
                        message.obj = response;
                        notifiHandler.sendMessage(message);
                    }

                    @Override
                    public void onError(Exception e) {
                        message.what = QUERY_FAILURE;
                        message.obj = "服务器异常";
                        notifiHandler.sendMessage(message);
                    }
                });
    }

    /**
     * 删除用户的所有通知
     */
    private void doClearNotification() {
        showProgressDialog("正在清空......");
        HttpUtil.sendHttpRequestToInner(HttpUtil.REQUEST_CLEAR_NOTIFICATION,
                JSONUtil.createClearNotifiJSON(userId), new HttpCallbackListener() {
                    Message message = new Message();
                    @Override
                    public void onFinish(String response) {
                        message.what = CLEAR_SUCCESS;
                        message.obj = response;
                        notifiHandler.sendMessage(message);
                    }

                    @Override
                    public void onError(Exception e) {
                        message.what = QUERY_FAILURE;
                        message.obj = "服务器异常";
                        notifiHandler.sendMessage(message);
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
    private final int QUERY_FAILURE = 4002;
    private final int QUERY_SUCCESS = 4003;
    private final int DELETE_SUCCESS = 4004;
    private final int CLEAR_SUCCESS = 4005;

    private Handler notifiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case QUERY_SUCCESS:
                    String queryResponse = (String) msg.obj;
                    doQueryResponseSuccess(queryResponse);
                    break;
                case DELETE_SUCCESS:
                    String deleteResponse = (String) msg.obj;
                    doDeleteResponseSuccess(deleteResponse);
                    break;
                case CLEAR_SUCCESS:
                    String clearResponse = (String) msg.obj;
                    doClearResponseSuccess(clearResponse);
                    break;
                case QUERY_FAILURE:
                    String failureText = (String) msg.obj;
                    ToastUtil.show(NotificationActivity.this, failureText);
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
        notifiData = JSONUtil.parseQueryNotificationJSON(queryResponse);
        if (notifiData == null) {
            notifiData = new ArrayList<>();
            ToastUtil.show(this, "没有任何通知");
        }
        // 将数据交给适配器
        notifiAdapter = new NotificationAdapter(this, notifiData);
        // 将列表与适配器绑定
        listNotification.setAdapter(notifiAdapter);
        // 列表为空的时候显示
        listNotification.setEmptyView(llNotifiListEmpty);
        dissmissProgressDialog();
    }

    /**
     * 处理响应成功的服务器数据
     *
     * @param response 服务器返回的数据
     */
    private void doDeleteResponseSuccess(String response) {
        int deleteResult = JSONUtil.parseDeleteCarJSON(response);
        // 表示删除成功
        if (deleteResult > 0) {
            ToastUtil.show(this, "删除成功");
            notifiData.remove(currentItemIndex);
            notifiAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.show(this, "删除失败，该通知已被删除");
        }
        dissmissProgressDialog();
    }

    /**
     * 处理清空成功的服务器数据
     *
     * @param response 服务器返回的数据
     */
    private void doClearResponseSuccess(String response) {
        int deleteResult = JSONUtil.parseDeleteCarJSON(response);
        // 表示删除成功
        if (deleteResult > 0) {
            ToastUtil.show(this, "清除成功");
            notifiData.clear();
            notifiAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.show(this, "通知已被清楚");
        }
        dissmissProgressDialog();
    }

}
