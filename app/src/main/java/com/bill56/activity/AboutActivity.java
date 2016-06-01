package com.bill56.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.bill56.adapter.AboutAdapter;
import com.bill56.carlife.R;
import com.bill56.entity.About;

import java.util.ArrayList;
import java.util.List;

/**
 * 关于的活动页面
 * Created by Bill56 on 2016/6/1.
 */
public class AboutActivity extends BaseActivity {
    private List<About> aboutList = new ArrayList<About>();

    private TextView textViewAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        // 设置通用返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.activity_about_title);
        // 初始化关于列表
        initAboutItem();
        AboutAdapter adapter = new AboutAdapter(AboutActivity.this, R.layout.about_item, aboutList);
        ListView listView = (ListView) findViewById(R.id.about_list_view);
        listView.setAdapter(adapter);
        textViewAbout = (TextView) findViewById(R.id.textView_about);
        initVersion();
    }

    /**
     * 获取app的版本号
     */
    private void initVersion() {
        PackageManager manager = getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(),0);
            textViewAbout.setText("V " + info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            textViewAbout.setText("V 1.3.3.2755");
        }

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
     * 初始化关于列表的方法
     */
    private void initAboutItem() {
        About developTeam1 = new About("版本更新", R.drawable.csy_arr_right);
        aboutList.add(developTeam1);
        About developTeam2 = new About("开发团队", R.drawable.csy_arr_right);
        aboutList.add(developTeam2);
    }

}
