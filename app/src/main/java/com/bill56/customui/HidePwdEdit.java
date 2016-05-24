package com.bill56.customui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bill56.carlife.R;

/**
 * Created by asus on 2016/5/13.
 */
public class HidePwdEdit extends AppCompatEditText {

    private Drawable showIcon;
    private Drawable hideIcon;

    /**
     * 必须有一个构造方法
     *
     * @param context
     * @param attrs
     */
    public HidePwdEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

        //加载图标
        showIcon = ContextCompat.getDrawable(context, R.drawable.ic_visibility_24dp);
        hideIcon = ContextCompat.getDrawable(context, R.drawable.ic_visibility_off_24dp);
        //设置边框
        hideIcon.setBounds(0, 0, hideIcon.getIntrinsicWidth(), hideIcon.getIntrinsicHeight());
        showIcon.setBounds(0, 0, showIcon.getIntrinsicWidth(), showIcon.getIntrinsicHeight());

        setCompoundDrawables(null, null, hideIcon, null);

        //注册点击View监听器
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                if (x > getWidth() - showIcon.getIntrinsicWidth() - getPaddingRight()) {
                    int type = getInputType();
                    if (type == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        setCompoundDrawables(null, null, hideIcon, null);
                    } else {
                        setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        setCompoundDrawables(null, null, showIcon, null);
                    }
                }
                return false;
            }
        });

    }
}
