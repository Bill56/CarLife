package com.bill56.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;


import com.bill56.carlife.R;

import java.util.ArrayList;
import java.util.HashMap;

public class HistListActivity extends BaseActivity {

    private SimpleAdapter adapter;
    private ArrayList<HashMap<String,Object>> data;
    private ListView listView_his;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hist_list);

        listView_his = (ListView) findViewById(R.id.listView_his);

        // 列表数据初始化
        data = new ArrayList<>();
        String[] from = {"orderId","stname","lmoney","lprice","lmass","ltype"};
        int[] to = {R.id.textView_orderId,R.id.textView_stname,R.id.textView_lmoney,R.id.textView_lprice,R.id.textView_lmass,R.id.textView_ltype};

        initListView();

        adapter = new SimpleAdapter(this, data, R.layout.list_his, from, to);
        listView_his.setAdapter(adapter);
    }

    private void initListView() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("orderId", 61);
        map.put("stname", "stname");
        map.put("lmoney", "lmoney");
        map.put("lprice", "lprice");
        map.put("lmass", "lmass");
        map.put("ltype", "ltype");
        data.add(map);
    }

}
