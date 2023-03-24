package com.example.gluniversity.gl.animator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import com.example.gluniversity.BitmapUtils;
import com.example.gluniversity.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class CubeAnimator extends Animator {

    private final static String TAG = "CubeAnimator";
    protected final static int VERTEX_LOCATION = 0;
    protected final static int COLOR_LOCATION = 1;
    protected final static int NORMAL_LOCATION = 2;

    private int degree = 0;
    private int[] VERTEXBufferArrayID = new int[]{0};

    private FloatBuffer vertexBuffLine;

    private int cubeTexId= 0;

    private Context context;

    public CubeAnimator(Context context) {
        vertexShaderCode =
                "#version 300 es \n"+
                        "layout(location = 0)in vec4 vPosition;\n" +  //写成out就报1281错误了
                        "layout(location = 1)in vec4 vColor;\n"+
                        "layout(location = 2)in vec3 a_normal;\n"+
                        "out vec4 tColor;\n"+
                        "uniform mat4 mvpMatrix;\n"+
                        "uniform lowp int isDrawCoor;\n"+
                        "out vec3 v_normal;\n"+
                        "void main() {\n" +
                        "if(isDrawCoor == 1){\n"+
                        "tColor = vColor;\n"+
                        "gl_Position = vPosition;}\n" +
                        "else{\n"+
                        "v_normal =  a_normal;\n" +
                        "gl_Position = mvpMatrix*vPosition;}\n" +
                        "}\n";
        fragmentShaderCode =
                "#version 300 es \n"+
                        "precision mediump float;\n" +
                        "in vec4 tColor;\n" +
                        "uniform lowp int isDrawCoor;\n"+
                        "out vec4 fragColor;\n"+
                        "in vec3 v_normal;\n"+
                        "uniform samplerCube sampler;\n" +
                        "void main() {\n" +
                        "if(isDrawCoor == 1){\n"+
                        "fragColor = tColor;}\n" +
                        "else{\n"+
                        "fragColor = texture(sampler,v_normal);}\n" +
//                        "fragColor = vec4(v_normal,1.0);}\n" +
                        "}\n";
        this.context = context;
        float[] vertex = new float[]{//x,y,z,w
                0.0f, 0.5f, 0.25f,
                -1.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 0.25f,
                -1.0f, -1.0f, 1.0f,
                0.5f, 0.0f, 0.25f,
                1.0f, -1.0f, 1.0f,
                0.5f, 0.5f, 0.25f,
                1.0f, 1.0f, 1.0f,
                0.5f, 0.5f, -0.25f,
                1.0f, 1.0f, -1.0f,
                0.5f, 0.0f, -0.25f,
                1.0f, -1.0f, -1.0f,
                0.0f, 0.0f, -0.25f,
                -1.0f, -1.0f, -1.0f,
                0.0f, 0.5f, -0.25f,
                -1.0f, 1.0f, -1.0f,
        };
        float[] vertexLine = new float[]{
                -1.0f,0.0f,0.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                1.0f,0.0f,0.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f,1.0f,0.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f,-1.0f,0.0f,
                0.0f, 1.0f, 0.0f, 1.0f
        };

        vertexBuffLine = ByteBuffer.allocateDirect(vertexLine.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffLine.put(vertexLine).position(0);

        byte[] drawIndices = new byte[]{
                0, 1, 2, 0, 2, 3,
                0, 3, 4, 0, 4, 7,
                1, 6, 5, 1, 5, 2,
                1, 0, 7, 7, 6, 1,
                3, 2, 5, 5, 4, 3,
                4, 5, 6, 4, 6, 7
        };
        vertexBuff = ByteBuffer.allocateDirect(vertex.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuff.put(vertex).position(0);

        drawIndicesBuff = ByteBuffer.allocateDirect(drawIndices.length).order(ByteOrder.nativeOrder());
        drawIndicesBuff.put(drawIndices).position(0);

        cubeTexId = genCubeTexture();
        Log.d(TAG, "CubeAnimator cubeTexId: " +cubeTexId);


    }

    float[] matrix = new float[]{//默认规则是列优先
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.25f, 0.25f, 0.0f, 1.0f
    };
    float[] matrix_tran = new float[]{//默认规则是列优先
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            -0.25f, -0.25f, 0.0f, 1.0f
    };

    float[] matrix_one = new float[]{//默认规则是列优先
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
    };


    float[] mvpMatrix = new float[16];

    @Override
    public void draw(int drawTech) {

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        if (program == 0) {
            Log.d(TAG, "draw program unavailable return: ");
        }
        GLES30.glUseProgram(program);

        GLES30.glEnable(GLES30.GL_CULL_FACE);
        GLES30.glEnable(GLES30.GL_BLEND);//必须设置，不然没有alpha
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);
        GLES30.glBlendEquation(GLES30.GL_FUNC_ADD);
//        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
//        GLES30.glDepthFunc(GLES30.GL_LESS);
        if (bufferIds[0] == 0 || bufferIds[1] == 0 || bufferIds[2] == 0) {
            Log.d(TAG, "first draw: create buffer");

            GLES30.glEnableVertexAttribArray(VERTEX_LOCATION);
            GLES30.glEnableVertexAttribArray(NORMAL_LOCATION);

            GLES30.glGenBuffers(3, bufferIds, 0);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, bufferIds[0]);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, 4 * vertexBuff.capacity(), vertexBuff, GLES30.GL_STATIC_DRAW);

            GLES30.glVertexAttribPointer(VERTEX_LOCATION, VERTEX_DIMENSION, GLES30.GL_FLOAT, false, (VERTEX_DIMENSION + VERTEX_DIMENSION) * 4, 0);//一组几个，一个多大，每隔多少取一组，偏移多少
            GLES30.glVertexAttribPointer(NORMAL_LOCATION, VERTEX_DIMENSION, GLES30.GL_FLOAT, false, (VERTEX_DIMENSION + VERTEX_DIMENSION) * 4, VERTEX_DIMENSION * 4);

            Log.d(TAG, "draw1: " + GLES30.glGetError());
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, bufferIds[1]);
            GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, drawIndicesBuff.capacity(), drawIndicesBuff, GLES30.GL_STATIC_DRAW);


            GLES30.glGenVertexArrays(1,VERTEXBufferArrayID,0);
            GLES30.glBindVertexArray(VERTEXBufferArrayID[0]);

            GLES30.glEnableVertexAttribArray(VERTEX_LOCATION);
            GLES30.glEnableVertexAttribArray(COLOR_LOCATION);//必须在bindVertexArray之后
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, bufferIds[2]);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, 4 * vertexBuffLine.capacity(), vertexBuffLine, GLES30.GL_STATIC_DRAW);
            GLES30.glVertexAttribPointer(VERTEX_LOCATION, VERTEX_DIMENSION, GLES30.GL_FLOAT, false, (VERTEX_DIMENSION + COLOR_DIMENSION) * 4, 0);//一组几个，一个多大，每隔多少取一组，偏移多少
            GLES30.glVertexAttribPointer(COLOR_LOCATION, COLOR_DIMENSION, GLES30.GL_FLOAT, false, (VERTEX_DIMENSION + COLOR_DIMENSION) * 4, VERTEX_DIMENSION * 4);

            Log.d(TAG, "draw111: " + GLES30.glGetError());
        }

        int matrixLocation = GLES30.glGetUniformLocation(program, "mvpMatrix");
        int drawLocation = GLES30.glGetUniformLocation(program, "isDrawCoor");
        Log.d(TAG, "draw11: " + GLES30.glGetError());

        Log.d(TAG, "draw matrixLocation: " + matrixLocation);
        if (matrixLocation == -1 || drawLocation == -1) {
            Log.d(TAG, "draw : get matrixLocation error");
            return;
        }

        degree++;
        double radian = degree*Math.PI/180;
        float cos = (float) Math.cos(radian);
        float sin = (float) Math.sin(radian);
        Log.e(TAG, "draw degree: " + degree + " cos:" + cos + " sin:" + sin);
        switch (drawTech) {
            case ROTATE_X:
                matrix[5] = cos;
                matrix[9] = -sin;//默认是列优先
                matrix[6] = sin;
                matrix[10] = cos;
                break;
            case ROTATE_Y:
                matrix[0] = cos;
                matrix[8] = sin;
                matrix[2] = -sin;
                matrix[10] = cos;
                break;
            case ROTATE_Z:
                matrix[0] = cos;
                matrix[4] = -sin;
                matrix[1] = sin;//默认是列优先
                matrix[5] = cos;
                break;
        }

        GLES30.glUniform1i(drawLocation,1);
        Log.d(TAG, "draw0.8: " + GLES30.glGetError());
        GLES30.glBindVertexArray(VERTEXBufferArrayID[0]);
        Log.d(TAG, "draw0.81: " + GLES30.glGetError());
        GLES30.glLineWidth(5);
        Log.d(TAG, "draw0.82: " + GLES30.glGetError());
        GLES30.glDrawArrays(GLES30.GL_LINES,0,4);
        Log.d(TAG, "draw0.9: " + GLES30.glGetError());


        GLES30.glUniform1i(drawLocation,2);
        Matrix.multiplyMM(mvpMatrix,0,matrix,0,matrix_tran,0);//右侧的最先实行变换，因为离点近嘛
        Log.d(TAG, "draw1.0: " + GLES30.glGetError());
        GLES30.glBindVertexArray(0);
        Log.d(TAG, "draw1.1: " + GLES30.glGetError());

        int samplerLocation = GLES30.glGetUniformLocation(program,"sampler");
        Log.d(TAG, "draw1.2: " + GLES30.glGetError());
        if (samplerLocation == -1) {
            Log.d(TAG, "draw : get matrixLocation error");
            return;
        }

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        Log.d(TAG, "draw2: " + GLES30.glGetError());
        GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP,cubeTexId);
        Log.d(TAG, "draw22: " + GLES30.glGetError());
        GLES30.glUniform1i(samplerLocation,0);
        Log.d(TAG, "draw222: " + GLES30.glGetError());

        GLES30.glUniformMatrix4fv(matrixLocation, 1, false, mvpMatrix, 0);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, drawIndicesBuff.capacity(), GLES30.GL_UNSIGNED_BYTE, 0);

//            Matrix.setIdentityM(matrix,0);


    }

    private int genCubeTexture(){
        int[] textureId= new int[1];
        GLES30.glGenTextures(1,textureId,0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP,textureId[0]);
        byte[][] imgBuffer = new byte[6][];
        int width =0;
        for (int i = 0 ; i< 6;++i){
            String tag = "n" + (i+1);
            int resourceId = context.getResources().getIdentifier(tag,"mipmap",context.getPackageName());
            Log.d(TAG, "genCubeTexture resource id : " + resourceId + " package name:" + context.getPackageName() + " img name:" + tag);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = 1;//这个参数设置为true才有效，
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),resourceId,options);
            width = bitmap.getWidth();
            imgBuffer[i] = BitmapUtils.bitmap2RGBA(bitmap);
            Log.d(TAG, "genCubeTexture imgbuffer size: "+ imgBuffer[i].length);
        }
        if (width == 0){
            Log.e(TAG, "genCubeTexture: "+ "gene bitmap error");
            return 0;
        }
        int height = width;
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_CUBE_MAP_POSITIVE_X,0,GLES30.GL_RGBA,width,height,0,GLES30.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,ByteBuffer.allocateDirect(width*height*4).order(ByteOrder.nativeOrder()).put(imgBuffer[0]).position(0));
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,0,GLES30.GL_RGBA,width,height,0,GLES30.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,ByteBuffer.allocateDirect(width*height*4).order(ByteOrder.nativeOrder()).put(imgBuffer[1]).position(0));
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,0,GLES30.GL_RGBA,width,height,0,GLES30.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,ByteBuffer.allocateDirect(width*height*4).order(ByteOrder.nativeOrder()).put(imgBuffer[2]).position(0));
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,0,GLES30.GL_RGBA,width,height,0,GLES30.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,ByteBuffer.allocateDirect(width*height*4).order(ByteOrder.nativeOrder()).put(imgBuffer[3]).position(0));
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,0,GLES30.GL_RGBA,width,height,0,GLES30.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,ByteBuffer.allocateDirect(width*height*4).order(ByteOrder.nativeOrder()).put(imgBuffer[4]).position(0));
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z,0,GLES30.GL_RGBA,width,height,0,GLES30.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,ByteBuffer.allocateDirect(width*height*4).order(ByteOrder.nativeOrder()).put(imgBuffer[5]).position(0));
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP,GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_NEAREST);

        return textureId[0];
    }
}
