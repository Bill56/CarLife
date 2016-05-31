package com.bill56.util;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;

import com.bill56.entity.OrderRefuOil;
import com.cheshouye.api.client.json.CarInfo;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

/**
 * Created by 何子洋 on 2016/5/15.
 */
public class QRCodeUtil {
    private static int QR_WIDTH = 400;
    private static int QR_HEIGHT = 400;
    private TextView resultTextView;
    // 这里是json格式的结果
    private static String scanResult;

    /**
     * 生成二维码的方法
     * @param content 文本
     * @param imageQrcode 二维码ImageView
     */
    public static void createQRImage(String content, ImageView imageQrcode) {
        try {

            Hashtable<EncodeHintType, String> hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = null;
            try {
                bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            } catch (WriterException e) {
            }
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            //显示到一个ImageView上面
            imageQrcode.setImageBitmap(bitmap);
        } catch (Exception e) {
        }
    }
    /**
     * 获得json字符串
     * @return json字符串
     */
    public static String getJsonString(Object obj) {
        if(obj != null) {
            if(obj instanceof OrderRefuOil) {
                OrderRefuOil orderRefuOil = (OrderRefuOil) obj;
                return orderRefuOil.toJsonString();
            }
        }
        return null;
    }
}
