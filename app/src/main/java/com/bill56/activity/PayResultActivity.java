package com.bill56.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bill56.carlife.MainActivity;
import com.bill56.carlife.R;


public class PayResultActivity extends BaseActivity {

    private ImageView imageView_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);

        imageView_result = (ImageView) findViewById(R.id.imageView_result);
        if (false) {
            imageView_result.setImageResource(R.drawable.ic_gas_station);
        }
    }

    /**
     * To 历史订单
     *
     * @param v
     */
    public void ToList(View v) {
        Intent intent = new Intent(PayResultActivity.this, HistListActivity.class);
        startActivity(intent);
    }

    /**
     * To 主界面
     *
     * @param v
     */
    public void ToMain(View v) {
        Intent intent = new Intent(PayResultActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
