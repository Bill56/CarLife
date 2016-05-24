package com.bill56.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bill56.adapter.WeizhangResponseAdapter;
import com.bill56.carlife.R;
import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.json.CarInfo;
import com.cheshouye.api.client.json.CityInfoJson;
import com.cheshouye.api.client.json.WeizhangResponseHistoryJson;
import com.cheshouye.api.client.json.WeizhangResponseJson;

/**
 * title：查询违章信息
 *
 * @author paul
 *
 */
public class WeizhangResult extends BaseActivity {
	final Handler cwjHandler = new Handler();
	WeizhangResponseJson info = null;

	private View popLoader;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 循环添加列表项来显示 自定义adapter
		setContentView(R.layout.csy_activity_result);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.csy_titlebar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("违章查询结果");

		popLoader = (View) findViewById(R.id.popLoader);
		popLoader.setVisibility(View.VISIBLE);

		Intent intent = this.getIntent();

		CarInfo car = (CarInfo) intent.getSerializableExtra("carInfo");

		step4(car);

		// 查询内容: 车牌+查询地
		TextView query_chepai = (TextView) findViewById(R.id.query_chepai);
		TextView query_city = (TextView) findViewById(R.id.query_city);
		query_chepai.setText(car.getChepai_no());
		CityInfoJson citys = WeizhangClient.getCity(car.getCity_id());
		query_city.setText(citys.getCity_name());
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

	public void step4(final CarInfo car) {
		// 声明一个子线程
		new Thread() {
			@Override
			public void run() {
				try {
					// 这里写入子线程需要做的工作
					info = WeizhangClient.getWeizhang(car);
					cwjHandler.post(mUpdateResults); // 高速UI线程可以更新结果了
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

	}

	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			updateUI();
		}
	};

	private void updateUI() {
		TextView result_null = (TextView) findViewById(R.id.result_null);
		TextView result_title = (TextView) findViewById(R.id.result_title);
		ListView result_list = (ListView) findViewById(R.id.result_list);

		popLoader.setVisibility(View.GONE);

		Log.d("返回数据", info.toJson());

		// 直接将信息限制在 Activity中
		if (info.getStatus() == 2001) {
			result_null.setVisibility(View.GONE);
			result_title.setVisibility(View.VISIBLE);
			result_list.setVisibility(View.VISIBLE);

			result_title.setText("共违章" + info.getCount() + "次, 计"
					+ info.getTotal_score() + "分, 罚款 " + info.getTotal_money()
					+ "元");

			WeizhangResponseAdapter mAdapter = new WeizhangResponseAdapter(
					this, getData());
			result_list.setAdapter(mAdapter);

		} else {
			// 没有查到为章记录

			if (info.getStatus() == 5000) {
				result_null.setText("请求超时，请稍后重试");
			} else if (info.getStatus() == 5001) {
				result_null.setText("交管局系统连线忙碌中，请稍后再试");
			} else if (info.getStatus() == 5002) {
				result_null.setText("恭喜，当前城市交管局暂无您的违章记录");
			} else if (info.getStatus() == 5003) {
				result_null.setText("数据异常，请重新查询");
			} else if (info.getStatus() == 5004) {
				result_null.setText("系统错误，请稍后重试");
			} else if (info.getStatus() == 5005) {
				result_null.setText("车辆查询数量超过限制");
			} else if (info.getStatus() == 5006) {
				result_null.setText("你访问的速度过快, 请后再试");
			} else if (info.getStatus() == 5008) {
				result_null.setText("输入的车辆信息有误，请查证后重新输入");
			} else {
				result_null.setText("恭喜, 没有查到违章记录！");
			}

			result_title.setVisibility(View.GONE);
			result_list.setVisibility(View.GONE);
			result_null.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * title:填值
	 *
	 * @return
	 */
	private List getData() {
		List<WeizhangResponseHistoryJson> list = new ArrayList();

		for (WeizhangResponseHistoryJson weizhangResponseHistoryJson : info
				.getHistorys()) {
			WeizhangResponseHistoryJson json = new WeizhangResponseHistoryJson();
			json.setFen(weizhangResponseHistoryJson.getFen());
			json.setMoney(weizhangResponseHistoryJson.getMoney());
			json.setOccur_date(weizhangResponseHistoryJson.getOccur_date());
			json.setOccur_area(weizhangResponseHistoryJson.getOccur_area());
			json.setInfo(weizhangResponseHistoryJson.getInfo());
			list.add(json);
		}

		return list;
	}

}
