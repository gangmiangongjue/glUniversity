package com.example.gluniversity.gl.animator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import com.example.gluniversity.BitmapUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class PlaneAnimator extends Animator {
    private final static String TAG = "PlaneAnimator";

    protected final static int VERTEX_LOCATION = 0;
    protected final static int TEXTURE_LOCATION = 1;
    private int degree = 0;
    private int[] VERTEXBufferArrayID = new int[]{0};

    private final static int TYPE_RGB = 0;
    private final static int TYPE_RGBA = 1;
    private final static int TYPE_RGBA_COMBINE = 2;

    private int planeTexId= 0;

    private Context context;

    public PlaneAnimator(Context context) {
        this.context = context;
        vertexShaderCode =
                "#version 300 es \n"+
                        "layout(location = 0)in vec4 vPosition;\n" +  //写成out就报1281错误了
                        "layout(location = 1)in vec2 a_texture;\n"+
                        "out vec2 v_texture;\n"+
                        "void main() {\n" +
                        "v_texture = a_texture;\n"+
                        "gl_Position = vPosition;\n" +
                        "}\n";
        fragmentShaderCode =
                "#version 300 es \n"+
                        "precision mediump float;\n" +
                        "out vec4 fragColor;\n"+
                        "in vec2 v_texture;\n"+
                        "uniform sampler2D sampler2;\n" +
                        "void main() {\n" +
                        "fragColor = texture(sampler2,v_texture);\n"+
                        "}\n";
        float[] vertex = new float[]{//x,y,z,w
                -1.0f, 1.0f, 0.0f,
                0.0f, 0.0f,
                -1.0f, -1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, -1.0f, 0.0f,
                1.0f, 1.0f,
                1.0f, 1.0f, 0.0f,
                1.0f, 0.0f
        };


        byte[] drawIndices = new byte[]{
                0, 1, 2 , 0, 2, 3
        };
        vertexBuff = ByteBuffer.allocateDirect(vertex.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuff.put(vertex).position(0);

        drawIndicesBuff = ByteBuffer.allocateDirect(drawIndices.length).order(ByteOrder.nativeOrder());
        drawIndicesBuff.put(drawIndices).position(0);

        planeTexId = gen2DTexture(TYPE_RGBA);
        Log.d(TAG, "planeAnimator planeTexId: " +planeTexId);


    }


    @Override
    public void draw(int drawTech) {

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        if (program == 0) {
            Log.d(TAG, "draw program unavailable return: ");
        }
        GLES30.glUseProgram(program);

        GLES30.glEnable(GLES30.GL_CULL_FACE);
//        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
//        GLES30.glDepthFunc(GLES30.GL_LESS);
        if (bufferIds[0] == 0 || bufferIds[1] == 0 || bufferIds[2] == 0) {
            Log.d(TAG, "first draw: create buffer");

            GLES30.glEnableVertexAttribArray(VERTEX_LOCATION);
            GLES30.glEnableVertexAttribArray(TEXTURE_LOCATION);

            GLES30.glGenBuffers(2, bufferIds, 0);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, bufferIds[0]);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, 4 * vertexBuff.capacity(), vertexBuff, GLES30.GL_STATIC_DRAW);

            GLES30.glVertexAttribPointer(VERTEX_LOCATION, VERTEX_DIMENSION, GLES30.GL_FLOAT, false, (VERTEX_DIMENSION + TEXTURE_DIMENSION) * 4, 0);//一组几个，一个多大，每隔多少取一组，偏移多少

            GLES30.glVertexAttribPointer(TEXTURE_LOCATION, TEXTURE_DIMENSION, GLES30.GL_FLOAT, false, (VERTEX_DIMENSION + TEXTURE_DIMENSION) * 4, VERTEX_DIMENSION * 4);

            Log.d(TAG, "draw2.1: " + GLES30.glGetError());
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, bufferIds[1]);
            GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, drawIndicesBuff.capacity(), drawIndicesBuff, GLES30.GL_STATIC_DRAW);

            Log.d(TAG, "draw2.2: " + GLES30.glGetError());
        }

        int samplerLocation = GLES30.glGetUniformLocation(program,"sampler2");
        Log.d(TAG, "draw1.2: " + GLES30.glGetError());
        if (samplerLocation == -1) {
            Log.d(TAG, "draw : get matrixLocation error");
            return;
        }
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        Log.d(TAG, "draw1.3: " + GLES30.glGetError());
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,planeTexId);
        Log.d(TAG, "draw1.4: " + GLES30.glGetError());
        GLES30.glUniform1i(samplerLocation,0);
        Log.d(TAG, "draw1.5: " + GLES30.glGetError());

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, drawIndicesBuff.capacity(), GLES30.GL_UNSIGNED_BYTE, 0);
        Log.d(TAG, "draw1.6: " + GLES30.glGetError());



    }

    private int gen2DTexture(int type){
        int textureId[] = new int[1];
        GLES30.glPixelStorei(GLES30.GL_UNPACK_ALIGNMENT, 4);
        GLES30.glGenTextures(1,textureId,0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textureId[0]);

        String tag = "n" + 7;
        int resourceId = context.getResources().getIdentifier(tag,"mipmap",context.getPackageName());
        Log.d(TAG, "gen2DTexture resource id : " + resourceId + " package name:" + context.getPackageName() + " img name:" + tag);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),resourceId);

        switch (type){
            case TYPE_RGB:
                byte[] rgb = BitmapUtils.bitmap2RGB(bitmap);
                GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D,0,GLES30.GL_RGB,bitmap.getWidth(),bitmap.getHeight(),0,GLES30.GL_RGB, GLES20.GL_UNSIGNED_BYTE,ByteBuffer.allocateDirect(rgb.length).order(ByteOrder.nativeOrder()).put(rgb).position(0));
                break;
            case TYPE_RGBA:
                byte[] rgba = BitmapUtils.bitmap2RGBA(bitmap);
                ByteBuffer rgbaBuffer = ByteBuffer.allocateDirect(rgba.length).order(ByteOrder.nativeOrder()).put(rgba);
                rgbaBuffer.position(0);
                GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D,0,GLES30.GL_RGBA,bitmap.getWidth(),bitmap.getHeight(),0,GLES30.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,rgbaBuffer);
                break;
            case TYPE_RGBA_COMBINE:
                int[] rgbaCombine = BitmapUtils.bitmap2RGBACombine(bitmap);
                GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D,0,GLES30.GL_RGBA,bitmap.getWidth(),bitmap.getHeight(),0,GLES30.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,ByteBuffer.allocateDirect(rgbaCombine.length*4).order(ByteOrder.BIG_ENDIAN).asIntBuffer().put(rgbaCombine).position(0));
                break;
            default:
                break;
        }

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
        Log.d(TAG, "draw0.1: " + GLES30.glGetError());
        return textureId[0];
    }
}
