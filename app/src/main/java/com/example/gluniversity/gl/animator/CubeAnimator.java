package com.example.gluniversity.gl.animator;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import com.example.gluniversity.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class CubeAnimator extends Animator {

    private final static String TAG = "CubeAnimator";
    private final static int VERTEX_LOCATION = 0;
    private final static int COLOR_LOCATION = 1;
    private final static int NORMAL_LOCATION = 2;
    private int degree = 0;
    private int[] VERTEXBufferArrayID = new int[]{0};

    private FloatBuffer vertexBuffLine;

    public CubeAnimator(Context context) {
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
                0.0f, 0.0f, 1.0f, 1.0f,
                1.0f,0.0f,0.0f,
                0.0f, 0.0f, 1.0f, 1.0f,
                0.0f,1.0f,0.0f,
                0.0f, 0.0f, 1.0f, 1.0f,
                0.0f,-1.0f,0.0f,
                0.0f, 0.0f, 1.0f, 1.0f
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

        BitmapDrawable n1 = (BitmapDrawable) context.getResources().getDrawable(R.drawable.n1);
        n1.getBitmap().
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
//        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
//        GLES30.glDepthFunc(GLES30.GL_LESS);
        if (bufferIds[0] == 0 || bufferIds[1] == 0 || bufferIds[2] == 0) {
            Log.d(TAG, "first draw: create buffer");

            GLES30.glEnableVertexAttribArray(VERTEX_LOCATION);
            GLES30.glEnableVertexAttribArray(COLOR_LOCATION);

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

//        degree++;
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
        GLES30.glUniform1ui(matrixLocation,1);
        GLES30.glBindVertexArray(VERTEXBufferArrayID[0]);
        GLES30.glLineWidth(5);
        GLES30.glDrawArrays(GLES30.GL_LINES,0,4);

        GLES30.glUniform1ui(matrixLocation,0);
        Matrix.multiplyMM(mvpMatrix,0,matrix,0,matrix_tran,0);//右侧的最先实行变换，因为离点近嘛
        GLES30.glBindVertexArray(0);
        GLES30.glUniformMatrix4fv(matrixLocation, 1, false, mvpMatrix, 0);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, drawIndicesBuff.capacity(), GLES30.GL_UNSIGNED_BYTE, 0);

//            Matrix.setIdentityM(matrix,0);


    }

    private int genCubeTexture(){
        int textureId[] = new int[1];
        GLES30.glGenTextures(1,textureId,0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP,textureId[0]);

    }
}
