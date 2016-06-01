package com.bill56.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.bill56.carlife.R;
import com.bill56.entity.About;

import java.util.List;

/**
 * Created by 何子洋 on 2016/5/10.
 */
public class AboutAdapter extends ArrayAdapter<About> {
    private int resourceId;

    public AboutAdapter(Context context, int textViewResourceId, List<About> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        About about = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.itemImage = (ImageView) view.findViewById(R.id.img_about_image);
            viewHolder.itemName = (TextView) view.findViewById(R.id.text_about_item_name);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.itemImage.setImageResource(about.getIamgeId());
        viewHolder.itemName.setText(about.getItem());
        return view;
    }

    class ViewHolder {
        ImageView itemImage;
        TextView itemName;
    }
}
