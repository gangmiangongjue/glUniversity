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
    public static byte[] bitmap2RGB(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.d(TAG, "bitmap2RGB width: " + width + " height:"+height);
        int[] intValues = new int[width * height];
        bitmap.getPixels(intValues, 0, width, 0, 0, width,
                height);
        byte[] rgb = new byte[width * height * 3];
        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            rgb[i * 3] = (byte) ((val >> 16) & 0xFF);//R
            rgb[i * 3 + 1] = (byte) ((val >> 8) & 0xFF);//G
            rgb[i * 3 + 2] = (byte) (val & 0xFF);//B
        }
        return rgb;
    }

    /**
     * 将bitmap 转换为RGBA数组（四通道）
     * @param bitmap
     * @return
     */
    public static byte[] bitmap2RGBA(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.d(TAG, "bitmap2RGB width: " + width + " height:"+height);
        int[] intValues = new int[width * height];
        bitmap.getPixels(intValues, 0, width, 0, 0, width,
                height);
        byte[] rgba = new byte[width * height * 4];
        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            rgba[i * 4] = (byte) ((val >> 16) & 0xFF);//R
            rgba[i * 4 + 1] = (byte) ((val >> 8) & 0xFF);//G
            rgba[i * 4 + 2] = (byte) (val & 0xFF);//B
            rgba[i * 4 + 3] = (byte) ((val >> 24) & 0xFF);//A
        }
        return rgba;
    }

    /**
     * 将bitmap 转换为RGBA int数组（四通道）
     * @param bitmap
     * @return
     */
    public static int[] bitmap2RGBACombine(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.d(TAG, "bitmap2RGB width: " + width + " height:"+height);
        int[] intValues = new int[width * height];
        bitmap.getPixels(intValues, 0, width, 0, 0, width,
                height);
        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            byte r = (byte) ((val >> 16) & 0xFF);//R
            byte g = (byte) ((val >> 8) & 0xFF);//G
            byte b = (byte) (val & 0xFF);//B
            byte a = (byte) ((val >> 24) & 0xFF);//A
            intValues[i] = ((r & 0xFF)<<24) | ((g&0xFF)<<16) | ((b&0xFF) << 8) | (a & 0xFF);
//            intValues[i] = ((r<<24) & 0xFF000000) | ((g<<16)&0xFF0000) | ((b<<8)&0xFF00) | 0;
        }
        return intValues;
    }


    public static void NV21ToARGB(byte[] input, int width, int height, int[] output)
    {
        int nvOff = width * height ;
        int  i, j, yIndex = 0;
        int y, u, v;
        int r, g, b, nvIndex = 0;
        for(i = 0; i < height; i++){
            for(j = 0; j < width; j ++,++yIndex){
                nvIndex = (i / 2)  * width + j - j % 2;
                y = input[yIndex] & 0xff;
                u = input[nvOff + nvIndex + 1] & 0xff;
                v = input[nvOff + nvIndex ] & 0xff;

                // yuv to rgb
                r = y + ((351 * (v-128))>>8);  //r
                g = y - ((179 * (v-128) + 86 * (u-128))>>8); //g
                b = y + ((443 * (u-128))>>8); //b

                r = ((r>255) ?255 : Math.max(r, 0));
                g = ((g>255) ?255 : Math.max(g, 0));
                b = ((b>255) ?255 : Math.max(b, 0));

                output[yIndex] = (0xff<<24) | ((r&0xff)<<16) | ((g&0xff)<<8) | (b&0xff);
            }
        }
    }

}
