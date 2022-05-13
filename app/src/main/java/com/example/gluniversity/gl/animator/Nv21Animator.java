package com.example.gluniversity.gl.animator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import com.example.gluniversity.BitmapUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Nv21Animator extends Animator {
    private final static String TAG = "PlaneAnimator";

    protected final static int VERTEX_LOCATION = 0;
    protected final static int TEXTURE_LOCATION = 1;
    private int degree = 0;
    private int[] VERTEXBufferArrayID = new int[]{0};

    private final static int TYPE_RGB = 0;
    private final static int TYPE_RGBA = 1;
    private final static int TYPE_RGBA_COMBINE = 2;

    private int[] texIds= new int[2];

    private Context context;

    public Nv21Animator(Context context) {
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
                        "uniform sampler2D samplerY;\n" +
                        "uniform sampler2D samplerUV;\n" +
                        "void main() {\n" +
                        "float y,u,v,r,g,b;\n"+
                        "y = texture(samplerY,v_texture).r;\n"+
                        "u = texture(samplerUV,v_texture).a-0.5;\n"+
                        "v = texture(samplerUV,v_texture).r-0.5;\n"+
                        "r = y+1.13983*v;\n"+
                        "g = y-0.39465*u-0.58060*v;\n"+
                        "b = y+2.03211*u;\n"+
                        "fragColor = vec4(r,g,b,1.0);\n"+
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

        gen2DTexture();



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

            int samplerYLocation = GLES30.glGetUniformLocation(program,"samplerY");
            int samplerUVLocation = GLES30.glGetUniformLocation(program,"samplerUV");
            Log.d(TAG, "draw1.2: " + GLES30.glGetError());
            if (samplerYLocation == -1 || samplerUVLocation == -1) {
                if (samplerYLocation == -1){
                    Log.d(TAG, "draw : get samplerYLocation error");
                }
                if (samplerYLocation == -1){
                    Log.d(TAG, "draw : get samplerUVLocation error");
                }
                return;
            }
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            Log.d(TAG, "draw1.3: " + GLES30.glGetError());
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,texIds[0]);
            Log.d(TAG, "draw1.4: " + GLES30.glGetError());
            GLES30.glUniform1i(samplerYLocation,0);
            Log.d(TAG, "draw1.5: " + GLES30.glGetError());

            GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
            Log.d(TAG, "draw1.3: " + GLES30.glGetError());
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,texIds[1]);
            Log.d(TAG, "draw1.4: " + GLES30.glGetError());
            GLES30.glUniform1i(samplerUVLocation,1);
            Log.d(TAG, "draw1.5: " + GLES30.glGetError());
            Log.d(TAG, "draw2.2: " + GLES30.glGetError());
        }



        GLES30.glDrawElements(GLES30.GL_TRIANGLES, drawIndicesBuff.capacity(), GLES30.GL_UNSIGNED_BYTE, 0);
        Log.d(TAG, "draw1.6: " + GLES30.glGetError());



    }

    private void gen2DTexture(){

        String tag = "n" + 8;
        int resourceId = context.getResources().getIdentifier(tag,"raw",context.getPackageName());
        Log.d(TAG, "gen2DTexture resource id : " + resourceId + " package name:" + context.getPackageName() + " img name:" + tag);
        int width = 600;
        int height = width;
        InputStream inputStream = context.getResources().openRawResource(resourceId);
        byte[] nv21 = new byte[width*height*3/2];
        try {
            int count = inputStream.read(nv21);
            Log.d(TAG, "gen2DTexture read count: " + count);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        GLES30.glGenTextures(2,texIds,0);
        Log.d(TAG, "planeAnimator texIds 0: " +texIds[0] + " 1:"+ texIds[1]);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,texIds[0]);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D,0,GLES30.GL_LUMINANCE,width,height,0,GLES30.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE,ByteBuffer.allocateDirect(width*height).order(ByteOrder.nativeOrder()).put(nv21,0,width*height).position(0));

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,texIds[1]);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D,0,GLES30.GL_LUMINANCE_ALPHA,width/2,height/2,0,GLES30.GL_LUMINANCE_ALPHA, GLES20.GL_UNSIGNED_BYTE,ByteBuffer.allocateDirect(width*height/2).order(ByteOrder.nativeOrder()).put(nv21,width*height,width*height/2).position(0));

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);

        Log.d(TAG, "draw0.1: " + GLES30.glGetError());
    }
}
