package com.bill56.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bill56.carlife.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Bill56 on 2016/5/25.
 */
public class NotificationDetailActivity extends BaseActivity {

    // 显示通知的控件
    private TextView textNotifiTime;

    private TextView textNotifiContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);
        // 显示返回按钮
        ActionBar bar = getSupportActionBar();
        bar.setTitle(R.string.notification_detail_title);
        bar.setDisplayHomeAsUpEnabled(true);
        // 初始化控件
        initView();
    }

    private void initView() {
        textNotifiTime = (TextView) findViewById(R.id.text_notifi_time);
        textNotifiContent = (TextView) findViewById(R.id.text_notifi_content);
        Intent notiIntent = getIntent();
//        int notifiType = notiIntent.getIntExtra("notifiType", 0);
        String notifiContent = notiIntent.getStringExtra("notifiContent");
        long notifiTime = notiIntent.getLongExtra("notifiTime", -1);
        if (notifiContent != null && notifiTime > 0) {
            // 将时间格式化
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(notifiTime);
            textNotifiTime.setText(sdf.format(date));
            textNotifiContent.setText(notifiContent);
        }
    }

    /**
     * 添加选项菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back, menu);
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
            default:
                break;
        }
        return true;
    }

}
