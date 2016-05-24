package com.bill56.util;

/**
 * Created by asus on 2016/5/10.
 */
public class SJNum {

    public static String num = "";

    public static void getNum() {

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int n = (int) (Math.random() * 10);
            String str = String.valueOf(n);
            builder.append(str);
        }

        num = builder.toString();
    }
}
