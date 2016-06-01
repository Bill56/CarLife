package com.bill56.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;


import com.bill56.adapter.SettingAdapter;
import com.bill56.carlife.R;
import com.bill56.entity.Setting;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置界面
 * Created by 何子洋 on 2016/5/10.
 */
public class SettingAcitivity extends BaseActivity {
    private List<Setting> settingList = new ArrayList<Setting>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        // 设置通用返回键
        getSupportActionBar().setTitle(R.string.activity_setting_tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 初始化关于列表
        initSettingItem();
        SettingAdapter adapter = new SettingAdapter(SettingAcitivity.this, R.layout.setting_item, settingList);
        ListView listView = (ListView) findViewById(R.id.setting_list_view);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back,menu);
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

    /**
     * 初始化设置列表的方法
     */
    private void initSettingItem() {
        Setting settingItem1 = new Setting("个人信息", R.drawable.csy_arr_right);
        settingList.add(settingItem1);
        Setting settingItem2 = new Setting("车辆信息", R.drawable.csy_arr_right);
        settingList.add(settingItem2);
        Setting settingItem3 = new Setting("付款信息", R.drawable.csy_arr_right);
        settingList.add(settingItem3);
        Setting settingItem4 = new Setting("背景音乐", R.drawable.csy_arr_right);
        settingList.add(settingItem4);
        Setting settingItem5 = new Setting("提醒", R.drawable.csy_arr_right);
        settingList.add(settingItem5);
    }

}
