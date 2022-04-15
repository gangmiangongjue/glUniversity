package com.example.gluniversity.gl;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

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
            Log.e(TAG, "linkShader error: ");
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
}
