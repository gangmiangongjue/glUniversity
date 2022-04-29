package com.example.gluniversity;

public class BitmapUtils {
    /**
     * 将bitmap 转换为RGB数组（三通道）
     * @param bitmap
     * @return
     */
    public static byte[] bitmap2ARGB(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] intValues = new int[width * height];
        bitmap.getPixels(intValues, 0, width, 0, 0, width,
                height);
        byte[] rgb = new byte[width * height * 3];
        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            argb[i * 3] = (byte) ((val >> 24) & 0xFF);//A
            argb[i * 3 +1] = (byte) ((val >> 16) & 0xFF);//R
            argb[i * 3 + 2] = (byte) ((val >> 8) & 0xFF);//G
            argb[i * 3 + 3] = (byte) (val & 0xFF);//B
        }
        return rgb;
    }

}
