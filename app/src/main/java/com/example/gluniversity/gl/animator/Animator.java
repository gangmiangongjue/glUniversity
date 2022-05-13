package com.example.gluniversity.gl.animator;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import com.example.gluniversity.gl.ESGLUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public abstract class Animator {
    private final static String TAG = "Animator";
    protected final static int TEXTURE_DIMENSION = 2;
    protected final static int VERTEX_DIMENSION = 3;
    protected final static int COLOR_DIMENSION = 4;

    protected final static int VERTEX_LOCATION = 0;
    protected final static int COLOR_LOCATION = 1;
    protected final static int NORMAL_LOCATION = 2;
    protected final static int TEXTURE_LOCATION = 2;

    public final static int ROTATE_X = 0 ;
    public final static int ROTATE_Y = 1 ;
    public final static int ROTATE_Z = 2 ;

    protected String vertexShaderCode =
            "#version 300 es \n"+
                    "layout(location = 0)in vec4 vPosition;\n" +  //写成out就报1281错误了
                    "layout(location = 1)in vec4 vColor;\n"+
                    "out vec4 tColor;\n"+
                    "uniform mat4 mvpMatrix;\n"+
                    "uniform lowp int isDrawCoor;\n"+
                    "layout(location =2)in vec3 a_normal;\n"+
                    "layout(location =3)in vec2 a_texture;\n"+
                    "out vec3 v_normal;\n"+
                    "out vec2 v_texture;\n"+
                    "void main() {\n" +
                    "if(isDrawCoor == 1){\n"+
                        "tColor = vColor;\n"+
                        "gl_Position = mvpMatrix*vPosition;}\n" +
                    "else if(isDrawCoor == 2){\n"+
                        "v_normal =  a_normal;\n" +
                        "gl_Position = mvpMatrix*vPosition;}\n" +
                    "else{\n" +
                        "gl_Position = mvpMatrix*vPosition;\n" +
                        "v_texture = a_texture;}\n"+
                    "}\n";
    protected String fragmentShaderCode =
            "#version 300 es \n"+
                    "precision mediump float;\n" +
                    "in vec4 tColor;\n" +
                    "uniform lowp int isDrawCoor;\n"+
                    "out vec4 fragColor;\n"+
                    "in vec3 v_normal;\n"+
                    "in vec2 v_texture;\n"+
                    "uniform samplerCube sampler;\n" +
                    "uniform sampler2D sampler2;\n" +
                    "void main() {\n" +
                    "if(isDrawCoor == 1){\n"+
                        "fragColor = tColor;}\n" +
                    "else if(isDrawCoor == 2){\n"+
                        "fragColor = texture(sampler,v_normal);}\n" +
                    "else{\n"+
                        "fragColor = texture(sampler2,v_texture);}\n"+
                    "}\n";
    protected FloatBuffer vertexBuff;
    protected FloatBuffer colorBuff;
    protected ByteBuffer drawIndicesBuff;
    int program = 0;
    protected int[] bufferIds = new int[]{0,0,0};

    Animator(){

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
        GLES30.glClearColor(0.2f,0.2f,0.2f, 1.0f);

    }
    public void onViewportChange(int width ,int height){
        GLES30.glViewport(0,0,width,height);
    }
    public abstract void draw(int  drawTech);
}
