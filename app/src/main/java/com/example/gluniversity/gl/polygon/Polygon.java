package com.example.gluniversity.gl.polygon;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import com.example.gluniversity.gl.ESGLUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public abstract class Polygon {
    private final static String TAG ="Polygon";
    protected String vertexShaderCode =
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";
    protected String fragmentShaderCode =
                    "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    protected FloatBuffer vertexBuff;
    protected ByteBuffer drawOrderBuff;
    int program = 0;

    Polygon(){

    }

    void initGL(){
        program = GLES30.glCreateProgram();
        int vertextShader = ESGLUtils.loadShader(vertexShaderCode,GLES30.GL_VERTEX_SHADER);
        int fragmentShader = ESGLUtils.loadShader(fragmentShaderCode,GLES30.GL_FRAGMENT_SHADER);
        GLES30.glAttachShader(program,vertextShader);
        GLES30.glAttachShader(program,fragmentShader);
        GLES30.glLinkProgram(program);
        int[] linkResult = new int[]{0};
        GLES30.glGetProgramiv(program,GLES30.GL_LINK_STATUS,linkResult,0);
        if (linkResult[0] == 0){
            Log.e(TAG, "initGL error ");
            GLES30.glGetProgramiv(program, GLES20.GL_INFO_LOG_LENGTH,linkResult,0);
            if (linkResult[0] > 1){
                Log.e(TAG, "error is: " +GLES30.glGetProgramInfoLog(program));
            }
            return;
        }
        GLES30.glClearColor(200,0,0,255);

    }

    protected abstract void draw();
}
