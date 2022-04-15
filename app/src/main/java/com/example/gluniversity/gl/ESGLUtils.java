package com.example.gluniversity.gl;
import android.opengl.GLES30;
public class ESGLUtils {
    public static int loadShader(String shaderCode,int shaderType){
        int shader = GLES30.glCreateShader(shaderType);//创建shader id
        GLES30.glShaderSource(shader,shaderCode);//id与code链接
        GLES30.glCompileShader(shader);//编译shader code
        return shader;
    }
}
