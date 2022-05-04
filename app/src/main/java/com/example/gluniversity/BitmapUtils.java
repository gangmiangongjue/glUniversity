package com.example.gluniversity;

import android.graphics.Bitmap;
import android.util.Log;

public class BitmapUtils {
    private final static String TAG = "BitmapUtils";
    /**
     * 将bitmap 转换为RGB数组（三通道）
     * @param bitmap
     * @return
     */
    public static byte[] bitmap2ARGB(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.d(TAG, "bitmap2ARGB width: " + width + " height:"+height);
        int[] intValues = new int[width * height];
        bitmap.getPixels(intValues, 0, width, 0, 0, width,
                height);
        byte[] rgba = new byte[width * height * 4];
        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            rgba[i * 3] = (byte) ((val >> 16) & 0xFF);//R
            rgba[i * 3 + 1] = (byte) ((val >> 8) & 0xFF);//G
            rgba[i * 3 + 2] = (byte) (val & 0xFF);//B
            rgba[i * 3 + 3] = (byte) ((val >> 24) & 0xFF);//A
        }
        return rgba;
    }

}
