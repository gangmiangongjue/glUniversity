package com.example.gluniversity.gl;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ESGLUtils {
    private final static String TAG  = "ESGLUtils";
    public static int loadShader(String shaderCode,int shaderType){
        int shader = GLES30.glCreateShader(shaderType);//创建shader id
        if(shader == 0){
            Log.e(TAG, "loadShader error!");
            return 0;
        }
        GLES30.glShaderSource(shader,shaderCode);//id与code链接
        GLES30.glCompileShader(shader);//编译shader code
        int[] compileResult = new int[]{0};
        GLES30.glGetShaderiv(shader,GLES30.GL_COMPILE_STATUS,compileResult,0);
        if (compileResult[0] == 0){
            Log.e(TAG, "linkShader error shaderType: " + shaderType);
            GLES30.glGetShaderiv(shader, GLES20.GL_INFO_LOG_LENGTH,compileResult,0);
            if(compileResult[0]>1){
                String log = GLES30.glGetShaderInfoLog(shader);
                Log.e(TAG, "error info: "+ log);
            }
            GLES30.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }
    public static String loadCodeFromRawFile(Context context, int rawId) {
        String result = null;
        try {
            InputStream in = context.getResources().openRawResource(rawId);;
            int ch = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((ch = in.read()) != -1) {
                baos.write(ch);
            }
            byte[] buff = baos.toByteArray();
            baos.close();
            in.close();
            result = new String(buff, "UTF-8");
//            result = result.replaceAll("\\r\\n", "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
