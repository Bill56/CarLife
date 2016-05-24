package com.bill56.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bill56.adapter.ListAdapter;
import com.bill56.carlife.R;
import com.bill56.entity.ListModel;
import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.json.CityInfoJson;

public class CityListActivity extends BaseActivity {
	private ListView lv_list;
	private ListAdapter mAdapter;

	private String provinceName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.csy_activity_citys);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.csy_titlebar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("选择查询地-城市");

		Bundle bundle = getIntent().getExtras();
		provinceName = bundle.getString("province_name");
		final String provinceId = bundle.getString("province_id");


		lv_list = (ListView) findViewById(R.id.lv_1ist);

		mAdapter = new ListAdapter(this, getData(provinceId));
		lv_list.setAdapter(mAdapter);

		lv_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				TextView txt_name = (TextView) view.findViewById(R.id.txt_name);

				Intent intent = new Intent();
				// 设置cityName
				intent.putExtra("city_name", txt_name.getText());
				// 设置cityId
				intent.putExtra("city_id",
						txt_name.getTag().toString());
				setResult(20, intent);
				finish();
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


	/**
	 * title:获取数据
	 * @param provinceId
	 * @return
	 */
	private List<ListModel> getData(String provinceId) {
		List<ListModel> list = new ArrayList<ListModel>();

		List<CityInfoJson> cityList = WeizhangClient.getCitys(Integer
				.parseInt(provinceId));

		//开通数量提示
		TextView txtListTip = (TextView) findViewById(R.id.list_tip);
		txtListTip.setText(provinceName + "已开通"+cityList.size()+"个城市, 其它城市将陆续开放");

		for (CityInfoJson cityInfoJson : cityList) {
			String cityName = cityInfoJson.getCity_name();
			int cityId = cityInfoJson.getCity_id();

			ListModel model = new ListModel();
			model.setNameId(cityId);
			model.setTextName(cityName);
			list.add(model);
		}

		return list;
	}

}
