package com.bill56.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.bill56.activity.GasoinfoActivity;
import com.bill56.activity.OrdRefActivity;
import com.bill56.carlife.R;
import com.bill56.customui.ImageLayout;
import com.bill56.util.ActivityUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by asus on 2016/5/11.
 */
public class GasStaAdapter extends BaseAdapter {

    private List<HashMap<String, String>> list;
    private Context context;
    private LayoutInflater layoutInflater;

    /**
     * 重写构造方法
     *
     * @param context 上下文
     * @param list    需要显示的数据
     */
    public GasStaAdapter(Context context, List<HashMap<String, String>> list) {
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    /**
     * 获取数据总数
     *
     * @return
     */
    @Override
    public int getCount() {
        return 10;
    }

    /**
     * 获取指定位置的数据
     *
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * 获取特定位置加载了数据的视图
     *
     * @param position    位置
     * @param convertView 可重用视图
     * @param parent      父元素
     * @return 列表项
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        // 获取当前项
        final int i = position;
        convertView = layoutInflater.inflate(R.layout.list_gasstation, parent, false);
        viewHolder = new ViewHolder();
        viewHolder.textView_num = (TextView) convertView.findViewById(R.id.textView_num);
        viewHolder.textView_GSname = (TextView) convertView.findViewById(R.id.textView_GSname);
        viewHolder.textView_distance = (TextView) convertView.findViewById(R.id.textView_distance);
        viewHolder.textView_brandname = (TextView) convertView.findViewById(R.id.textView_brandname);
        viewHolder.textView_93 = (TextView) convertView.findViewById(R.id.textView_93);
        viewHolder.textView_90 = (TextView) convertView.findViewById(R.id.textView_90);
        viewHolder.textView_97 = (TextView) convertView.findViewById(R.id.textView_97);
        viewHolder.textView_0 = (TextView) convertView.findViewById(R.id.textView_0);
        viewHolder.textView_price0 = (TextView) convertView.findViewById(R.id.textView_price0);
        viewHolder.textView_price90 = (TextView) convertView.findViewById(R.id.textView_price90);
        viewHolder.textView_price93 = (TextView) convertView.findViewById(R.id.textView_price93);
        viewHolder.textView_price97 = (TextView) convertView.findViewById(R.id.textView_price97);
        viewHolder.textView_address = (TextView) convertView.findViewById(R.id.textView_address);
        viewHolder.image_layout1 = (ImageLayout) convertView.findViewById(R.id.layout_roate);
        viewHolder.image_layout1.setImageResource(R.drawable.ic_navigation_24dp);
        viewHolder.image_layout1.setTextViewText("路径规划");
        viewHolder.image_layout2 = (ImageLayout) convertView.findViewById(R.id.layout_ordRef);
        viewHolder.image_layout2.setImageResource(R.drawable.ic_gas_station);
        viewHolder.image_layout2.setTextViewText("预约加油");
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.layout_ordRef) {
                    //预约加油
                    Log.d("adapter", "预约加油");
                    doRefuOil();
                } else if (id == R.id.layout_roate) {
                    //路径规划
                    Log.d("adapter", "路径规划");
                    doRoute();
                }
            }

            // 执行路径导航的方法
            private void doRoute() {
                double startLat = OrdRefActivity.getmLatitude();
                double startLang = OrdRefActivity.getmLangiitude();
                // 获得目的地经纬度
                DPoint baiduLatLng = new DPoint(
                        Double.parseDouble(list.get(i).get("lat")),
                        Double.parseDouble(list.get(i).get("lon")));
                // 将百度地图坐标转换成高德地图坐标
                CoordinateConverter converter = new CoordinateConverter(context);
                // CoordType.BAIDU 待转换坐标类型
                converter.from(CoordinateConverter.CoordType.BAIDU);
                // sourceLatLng待转换坐标点 DPoint类型
                // 目标坐标，默认为原来的坐标
                DPoint desLatLng = baiduLatLng;
                try {
                    converter.coord(baiduLatLng);
                    // 执行转换操作
                    desLatLng = converter.convert();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 获取目的地的经纬度参数
                double endLat = desLatLng.getLatitude();
                double endLang = desLatLng.getLongitude();
                String endAddress = viewHolder.textView_GSname.getText().toString();
                ActivityUtil.startMapSearchActivity(context, startLang, startLat, endLang, endLat, endAddress);
            }

            /**
             * 预约加油的方法
             */
            private void doRefuOil() {
                HashMap<String, String> map = list.get(i);
                String data = map.get("getArray");
                Intent intent = new Intent(context, GasoinfoActivity.class);
                intent.putExtra("maps", data);
                intent.putExtra("index", i);
                context.startActivity(intent);
            }
        };
        // 为按钮注册点击的事件监听
        viewHolder.image_layout1.setOnClickListener(listener);
        viewHolder.image_layout2.setOnClickListener(listener);


        //加载position中的数据
        HashMap<String, String> map = list.get(position);

        //编号
        String num = map.get("num");
        viewHolder.textView_num.setText(num + ".");
        //加油站名字
        String name = map.get("name");
        viewHolder.textView_GSname.setText(name);
        //距离
        String distance = map.get("distance");
        viewHolder.textView_distance.setText(distance + "m");
        //品牌名称
        String brandname = map.get("brandname");
        viewHolder.textView_brandname.setText(brandname);

        //不同油的油价
        String prices = map.get("price");
        Log.d("start get prices", prices);
        String n_prices = "[" + prices + "]";
        try {
            JSONArray array = new JSONArray(n_prices);
            JSONObject object = array.getJSONObject(0);
            String price_90 = object.getString("E90");
            String price_93 = object.getString("E93");
            String price_97 = object.getString("E97");
            String price_0 = object.getString("E0");
            viewHolder.textView_price90.setText(price_90 + "元每升");
            viewHolder.textView_price93.setText(price_93 + "元每升");
            viewHolder.textView_price97.setText(price_97 + "元每升");
            viewHolder.textView_price0.setText(price_0 + "元每升");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String address = map.get("address");
        viewHolder.textView_address.setText(address);

        return convertView;
    }

    /**
     * 可重用视图
     */
    private static class ViewHolder {
        TextView textView_num;
        TextView textView_GSname;
        TextView textView_distance;
        TextView textView_brandname;
        TextView textView_93;
        TextView textView_0;
        TextView textView_90;
        TextView textView_97;
        TextView textView_price90;
        TextView textView_price93;
        TextView textView_price97;
        TextView textView_price0;
        TextView textView_address;
        ImageLayout image_layout1;
        ImageLayout image_layout2;
    }


}
