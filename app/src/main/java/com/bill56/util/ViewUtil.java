package com.bill56.util;

import android.graphics.Color;
import android.widget.Button;

/**
 * Created by Bill56 on 2016/5/16.
 */
public class ViewUtil {

    /**
     * 按下按钮后更改按钮样式的方法
     * @param button    被按下的按钮
     * @param btnColor  按下后的按钮颜色
     * @param textColor 按下后的字体颜色
     */
    public static void changeButton(Button button, int btnColor, int textColor,boolean isEditable) {
        button.setBackgroundColor(btnColor);
        button.setTextColor(textColor);
        button.setEnabled(isEditable);
    }

}
