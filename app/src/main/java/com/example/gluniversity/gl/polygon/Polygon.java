package com.example.gluniversity.gl.polygon;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import com.example.gluniversity.gl.ESGLUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public abstract class Polygon {
    private final static String TAG ="Polygon";
    protected final static int VERTEX_DIMENSION = 2;
    protected final static int COLOR_DIMENSION = 4;

    public final static int NAIVE = 1;
    public final static int VBO = 2;//顶点缓冲对象
    public final static int VAO = 3;//顶点数组对象
    public final static int MAP = 4;
    protected String vertexShaderCode =
                    "#version 300 es \n"+
                    "layout(location = 0)in vec4 vPosition;\n" +  //写成out就报1281错误了
                    "layout(location = 1)in vec4 vColor;\n"+
                    "out vec4 tColor;\n"+
//                            "out vec4 gl_Position;\n"+
                    "void main() {\n" +
                    "tColor = vColor;\n"+
                    "  gl_Position = vPosition;\n" +
                    "}\n";
    protected String fragmentShaderCode =
                    "#version 300 es \n"+
                    "precision mediump float;\n" +
                    "in vec4 tColor;\n" +
                    "out vec4 fragColor;\n"+
                    "void main() {\n" +
                    "  fragColor = tColor;\n" +
                    "}\n";
    protected FloatBuffer vertexBuff;
    protected FloatBuffer colorBuff;
    protected ByteBuffer drawIndicesBuff;
    int program = 0;
    protected int bufferIds[] = {0,0};

    Polygon(){

    }

    public void initGL(){
        program = GLES30.glCreateProgram();
        int VERTEXShader = ESGLUtils.loadShader(vertexShaderCode,GLES30.GL_VERTEX_SHADER);
        int fragmentShader = ESGLUtils.loadShader(fragmentShaderCode,GLES30.GL_FRAGMENT_SHADER);
        GLES30.glAttachShader(program,VERTEXShader);
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
        GLES30.glClearColor(0.2f,0.2f,0.2f, 1.0f);

    }
    public void onViewportChange(int width ,int height){
        GLES30.glViewport(0,0,width,height);
    }
    public abstract void draw(int  drawTech);
}
