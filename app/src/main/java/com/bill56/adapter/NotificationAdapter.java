package com.bill56.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bill56.carlife.R;
import com.bill56.entity.CarNotification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Bill56 on 2016/5/26.
 */
public class NotificationAdapter extends BaseAdapter {

    // 上下文环境
    private Context mContext;
    // 通知的数据
    private List<CarNotification> mData;
    // 布局服务加载器
    private LayoutInflater layoutInflater;

    public NotificationAdapter(Context context, List<CarNotification> data) {
        this.mContext = context;
        mData = data;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        // 加载模板，创建视图项——将布局创建成一个View对象
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_notification, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CarNotification notification = mData.get(position);
        viewHolder.bindData(notification);
        return convertView;
    }

    /**
     * 保存布局控件实例的类
     */
    private class ViewHolder {

        // 字段
        TextView textNotifiTitle;
        TextView textNotifiContent;
        TextView textNotifiTime;

        /**
         * 构造方法，获得布局中的控件绑定对象
         *
         * @param v 布局参数
         */
        public ViewHolder(View v) {
            textNotifiTitle = (TextView) v.findViewById(R.id.text_notifi_title_simple);
            textNotifiContent = (TextView) v.findViewById(R.id.text_notifi_content_simple);
            textNotifiTime = (TextView) v.findViewById(R.id.text_notifi_time_simple);
        }

        /**
         * 绑定数据
         *
         * @param notifi 通知对象
         */
        public void bindData(CarNotification notifi) {
            // 设置其他参数
            textNotifiTitle.setText(notifi.getNotifiTitle());
            textNotifiContent.setText(notifi.getNotifiContent());
            // 改成字符串的格式
            // 将时间格式化
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(notifi.getNotifiTime());
            textNotifiTime.setText(sdf.format(date));
        }
    }

}
