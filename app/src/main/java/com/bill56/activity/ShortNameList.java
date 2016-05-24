package com.bill56.activity;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.bill56.carlife.R;

public class ShortNameList extends BaseActivity {

	private GridView gv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//获取传过来的城市
		Bundle bundle = getIntent().getExtras();
		final String short_name = bundle.getString("select_short_name");
		Log.d("select_short_name...", short_name);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.csy_activity_shortname);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.csy_titlebar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("选择车牌所在地");

		//省份简称列表
		gv = (GridView) findViewById(R.id.gv_1);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.csy_listitem_shortname, getDate());
		gv.setAdapter(adapter);
		gv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				String txt = adapter.getItem(position);
				if(txt.length()>0){
					Toast.makeText(ShortNameList.this, txt, 0).show();

					// 选择之后再打开一个 显示城市的 Activity；
					Intent intent = new Intent();
					intent.putExtra("short_name", txt);
					setResult(0, intent);
					finish();
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.back,menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return true;
	}

	private String[] getDate() {
		return new String[] { "京", "津", "沪", "川", "鄂", "甘", "赣", "桂", "贵", "黑",
				"吉", "翼", "晋", "辽", "鲁", "蒙", "闽", "宁", "青", "琼", "陕", "苏",
				"皖", "湘", "新", "渝", "豫", "粤", "云", "藏", "浙", ""};
	}
}
