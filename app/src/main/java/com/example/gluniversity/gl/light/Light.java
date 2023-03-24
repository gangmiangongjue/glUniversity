package com.example.gluniversity.gl.light;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import com.example.gluniversity.gl.ESGLUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public abstract class Light {
    private final static String TAG = "Animator";
    protected final static int NORMAL_DIMENSION = 3;
    protected final static int VERTEX_DIMENSION = 3;
    protected final static int COLOR_DIMENSION = 4;

    public final static int ROTATE_X = 0 ;
    public final static int ROTATE_Y = 1 ;
    public final static int ROTATE_Z = 2 ;

    protected String vertexShaderCode = "";
    protected String fragmentShaderCode = "";
    protected FloatBuffer vertexBuff;
    protected FloatBuffer colorBuff;
    protected ByteBuffer drawIndicesBuff,drawIndicesBuffLine;
    int program = 0;
    protected int[] bufferIds = new int[]{0,0,0,0};


    Light(){

    }

    public void initGL(){
        program = GLES30.glCreateProgram();
        int vertexShader = ESGLUtils.loadShader(vertexShaderCode,GLES30.GL_VERTEX_SHADER);
        int fragmentShader = ESGLUtils.loadShader(fragmentShaderCode,GLES30.GL_FRAGMENT_SHADER);
        GLES30.glAttachShader(program,vertexShader);
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
        GLES30.glClearColor(1.0f,1.0f,1.0f, 1.0f);

    }
    public void onViewportChange(int width ,int height){
        GLES30.glViewport(0,0,width,height);
    }
    public abstract void draw(int  drawTech);
}
