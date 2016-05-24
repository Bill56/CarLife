package com.bill56.customui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bill56.carlife.R;

import java.util.jar.Attributes;

/**
 * Created by asus on 2016/5/13.
 */
public class ClearEdit extends AppCompatEditText {

    private Drawable removeIcon;

    /**
     * 构造方法【必须有一个】
     *
     * @param context
     */
    public ClearEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        //加载图标
        removeIcon = ContextCompat.getDrawable(context, R.drawable.ic_clear_24dp);
        //设置边框
        removeIcon.setBounds(0, 0, removeIcon.getIntrinsicWidth(), removeIcon.getIntrinsicHeight());

        //注册焦点改变监听器
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //有焦点设置图标在右边，没有焦点没有图标
                setCompoundDrawables(null, null, (hasFocus && (getText().length() > 0)) ? removeIcon : null, null);
            }
        });

        //注册点击View监听器
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    float x = event.getX();
                    if (x > getWidth() - removeIcon.getIntrinsicWidth() - getPaddingRight()) {
                        setText("");
                    }
                }
                return false;
            }
        });

        //添加文本改变监听器
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setCompoundDrawables(null, null, s.length() > 0 ? removeIcon : null, null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
}
