package com.bill56.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.bill56.carlife.R;
import com.bill56.entity.Setting;

import java.util.List;

/**
 * Created by 何子洋 on 2016/5/10.
 */
public class SettingAdapter extends ArrayAdapter<Setting> {
    private int resourceId;

    public SettingAdapter(Context context, int textViewResourceId, List<Setting> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Setting setting = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.itemImage = (ImageView) view.findViewById(R.id.setting_image);
            viewHolder.itemName = (TextView) view.findViewById(R.id.setting_item_name);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.itemImage.setImageResource(setting.getIamgeId());
        viewHolder.itemName.setText(setting.getItem());
        return view;
    }

    class ViewHolder {
        ImageView itemImage;
        TextView itemName;
    }
}
