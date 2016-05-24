package com.bill56.customui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bill56.carlife.R;


/**
 * Created by asus on 2016/5/16.
 */
public class ImageLayout extends LinearLayout {

    private ImageView imageView;
    private TextView textView;

    public ImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_button, this);
        imageView = (ImageView) findViewById(R.id.imageView_layout);
        textView = (TextView) findViewById(R.id.textView_layout);
    }

    /**
     * 设置图片资源
     * @param resId
     */
    public void setImageResource(int resId) {
        imageView.setImageResource(resId);
    }

    /**
     * 设置文本资源
     * @param text
     */
    public void setTextViewText(String text) {
        textView.setText(text);
    }
}
