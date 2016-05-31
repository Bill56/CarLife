package com.bill56.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bill56.carlife.MainActivity;
import com.bill56.carlife.R;
import com.bill56.util.ActivityUtil;

/**
 * 支付结果的活动
 */
public class PayResultActivity extends BaseActivity {

    private ImageView imageView_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);
        // 显示返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.activity_payresult_title);
        // 设置图标
        imageView_result = (ImageView) findViewById(R.id.imageView_result);
    }

    /**
     * 创建选项菜单
     *
     * @param menu 菜单
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back, menu);
        return true;
    }

    /**
     * 当选项菜单被选中的时候执行
     *
     * @param item 被选中的菜单
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

    /**
     * To 历史订单
     *
     * @param v
     */
    public void ToList(View v) {
        Intent intent = new Intent(PayResultActivity.this, HistListActivity.class);
        intent.putExtra("userId", getIntent().getIntExtra("userId", 0));
        startActivity(intent);
    }

    /**
     * To 主界面
     *
     * @param v
     */
    public void ToMain(View v) {
        ActivityUtil.finishExcept(this);
        Intent intent = new Intent(PayResultActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
