package com.example.gluniversity.gl.light;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.example.gluniversity.BitmapUtils;
import com.example.gluniversity.R;
import com.example.gluniversity.gl.ESGLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class SpotLight extends Light {

    private final static String TAG = "CubeAnimator";
    protected final static int VERTEX_LOCATION = 0;
    protected final static int NORMAL_LOCATION = 1;
    protected final static int COLOR_LOCATION = 2;
    private int degree = 0;

    private FloatBuffer vertexBuffLine;

    private Context context;

    private int[] VERTEXBufferArrayID = new int[]{0};

    public SpotLight(Context context) {
        vertexShaderCode = ESGLUtils.loadCodeFromRawFile(context, R.raw.spot_light_vertext);
        fragmentShaderCode = ESGLUtils.loadCodeFromRawFile(context, R.raw.spot_light_fragment);
//        Log.d(TAG, "SpotLight fragmentcode: \n "+ fragmentShaderCode);
        this.context = context;
        float[] vertex = new float[]{//x,y,z,w
                0.0f, 0.5f, 0.25f,
                1.0f, 0.0f, 0.0f,1.0f,
                -1.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 0.25f,
                1.0f, 0.0f, 0.0f,1.0f,
                -1.0f, 1.0f, 1.0f,
                0.5f, 0.0f, 0.25f,
                1.0f, 0.0f, 0.0f,1.0f,
                -1.0f, 1.0f, 1.0f,
                0.5f, 0.5f, 0.25f,
                1.0f, 0.0f, 0.0f,1.0f,
                -1.0f, 1.0f, 1.0f,
                0.5f, 0.5f, -0.25f,
                1.0f, 0.0f, 0.0f,1.0f,
                -1.0f, 1.0f, 1.0f,
                0.5f, 0.0f, -0.25f,
                1.0f, 0.0f, 0.0f,1.0f,
                -1.0f, 1.0f, 1.0f,
                0.0f, 0.0f, -0.25f,
                1.0f, 0.0f, 0.0f,1.0f,
                -1.0f, 1.0f, 1.0f,
                0.0f, 0.5f, -0.25f,
                1.0f, 0.0f, 0.0f,1.0f,
                -1.0f, 1.0f, 1.0f,
        };
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

        float[] vertexLine = new float[]{
                0.0f, 0.5f, 0.25f,
                0.0f, 1.0f, 0.0f,1.0f,
                -1.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 0.25f,
                0.0f, 1.0f, 0.0f,1.0f,
                -1.0f, 1.0f, 1.0f,
                0.5f, 0.0f, 0.25f,
                0.0f, 1.0f, 0.0f,1.0f,
                -1.0f, 1.0f, 1.0f,
                0.5f, 0.5f, 0.25f,
                0.0f, 1.0f, 0.0f,1.0f,
                -1.0f, 1.0f, 1.0f,
                0.5f, 0.5f, -0.25f,
                0.0f, 1.0f, 0.0f,1.0f,
                -1.0f, 1.0f, 1.0f,
                0.5f, 0.0f, -0.25f,
                0.0f, 1.0f, 0.0f,1.0f,
                -1.0f, 1.0f, 1.0f,
                0.0f, 0.0f, -0.25f,
                0.0f, 1.0f, 0.0f,1.0f,
                -1.0f, 1.0f, 1.0f,
                0.0f, 0.5f, -0.25f,
                0.0f, 1.0f, 0.0f,1.0f,
                -1.0f, 1.0f, 1.0f,
        };

        vertexBuffLine = ByteBuffer.allocateDirect(vertexLine.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffLine.put(vertexLine).position(0);
        byte[] drawIndicesLine = new byte[]{
                0, 1, 0, 3, 1, 2, 2, 3,
                4, 5, 4, 7, 5, 6, 6, 7,
                3, 4, 2, 5, 1, 6, 0, 7
        };
        drawIndicesBuffLine = ByteBuffer.allocateDirect(drawIndicesLine.length).order(ByteOrder.nativeOrder());
        drawIndicesBuffLine.put(drawIndicesLine).position(0);

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
//        GLES30.glClearDepthf(0.0f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        if (program == 0) {
            Log.d(TAG, "draw program unavailable return: ");
        }
        GLES30.glUseProgram(program);

        GLES30.glEnable(GLES30.GL_CULL_FACE);
//        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
//        GLES30.glDepthFunc(GLES30.GL_LESS);
//        GLES30.glDepthMask(true);
//        GLES30.glEnable(GLES30.GL_POLYGON_OFFSET_FILL);
//        GLES30.glEnable(GLES30.GL_BLEND);//必须设置，不然没有alpha
//        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);
//        GLES30.glBlendEquation(GLES30.GL_FUNC_ADD);


        if (bufferIds[0] == 0 || bufferIds[1] == 0) {
            Log.d(TAG, "first draw: create buffer");

            GLES30.glEnableVertexAttribArray(VERTEX_LOCATION);
            GLES30.glEnableVertexAttribArray(COLOR_LOCATION);

            GLES30.glGenBuffers(4, bufferIds, 0);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, bufferIds[0]);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, 4 * vertexBuff.capacity(), vertexBuff, GLES30.GL_STATIC_DRAW);

            GLES30.glVertexAttribPointer(VERTEX_LOCATION, VERTEX_DIMENSION, GLES30.GL_FLOAT, false, (VERTEX_DIMENSION + COLOR_DIMENSION + NORMAL_DIMENSION) * 4, 0);//一组几个，一个多大，每隔多少取一组，偏移多少
            GLES30.glVertexAttribPointer(COLOR_LOCATION, COLOR_DIMENSION, GLES30.GL_FLOAT, false, (VERTEX_DIMENSION + COLOR_DIMENSION + NORMAL_DIMENSION) * 4, VERTEX_DIMENSION * 4);
            GLES30.glVertexAttribPointer(NORMAL_LOCATION, NORMAL_DIMENSION, GLES30.GL_FLOAT, false, (VERTEX_DIMENSION + COLOR_DIMENSION + NORMAL_DIMENSION) * 4, (VERTEX_DIMENSION+COLOR_DIMENSION) * 4);

            Log.d(TAG, "draw1: " + GLES30.glGetError());
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, bufferIds[1]);
            GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, drawIndicesBuff.capacity(), drawIndicesBuff, GLES30.GL_STATIC_DRAW);

            Log.d(TAG, "draw111: " + GLES30.glGetError());


            GLES30.glGenVertexArrays(1,VERTEXBufferArrayID,0);
            GLES30.glBindVertexArray(VERTEXBufferArrayID[0]);

            GLES30.glEnableVertexAttribArray(VERTEX_LOCATION);
            GLES30.glEnableVertexAttribArray(COLOR_LOCATION);//必须在bindVertexArray之后
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, bufferIds[2]);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, 4 * vertexBuffLine.capacity(), vertexBuffLine, GLES30.GL_STATIC_DRAW);
            GLES30.glVertexAttribPointer(VERTEX_LOCATION, VERTEX_DIMENSION, GLES30.GL_FLOAT, false, (VERTEX_DIMENSION + COLOR_DIMENSION + NORMAL_DIMENSION) * 4, 0);//一组几个，一个多大，每隔多少取一组，偏移多少
            GLES30.glVertexAttribPointer(COLOR_LOCATION, COLOR_DIMENSION, GLES30.GL_FLOAT, false, (VERTEX_DIMENSION + COLOR_DIMENSION + NORMAL_DIMENSION) * 4, VERTEX_DIMENSION * 4);
            GLES30.glVertexAttribPointer(NORMAL_LOCATION, NORMAL_DIMENSION, GLES30.GL_FLOAT, false, (VERTEX_DIMENSION + COLOR_DIMENSION + NORMAL_DIMENSION) * 4, (VERTEX_DIMENSION+COLOR_DIMENSION) * 4);

            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, bufferIds[3]);
            GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, drawIndicesBuffLine.capacity(), drawIndicesBuffLine, GLES30.GL_STATIC_DRAW);

            Log.d(TAG, "draw111: " + GLES30.glGetError());
        }

        int position = GLES30.glGetUniformLocation(program, "light.position");
        int ambient_color= GLES30.glGetUniformLocation(program, "light.ambient_color");
        int diffuse_color= GLES30.glGetUniformLocation(program, "light.diffuse_color");
        int specular_color= GLES30.glGetUniformLocation(program, "light.specular_color");
        int spot_direction= GLES30.glGetUniformLocation(program, "light.spot_direction");
        int attenuation_factors= GLES30.glGetUniformLocation(program, "light.attenuation_factors");
        int compute_distance_attenuation= GLES30.glGetUniformLocation(program, "light.compute_distance_attenuation");
        int spot_exponent= GLES30.glGetUniformLocation(program, "light.spot_exponent");
        int spot_cutoff_angle= GLES30.glGetUniformLocation(program, "light.spot_cutoff_angle");
        Log.d(TAG, "draw position: "+position+" ambient_color:"+ambient_color+" diffuse_color："+diffuse_color+
                " specular_color:"+specular_color+" spot_direction:"+spot_direction+" attenuation_factors:"+attenuation_factors);
        int matrixLocation = GLES30.glGetUniformLocation(program, "mvpMatrix");
        Log.d(TAG, "draw11: " + GLES30.glGetError());

        Log.d(TAG, "draw matrixLocation: " + matrixLocation);
        if (matrixLocation == -1 || position==-1 || ambient_color ==-1||diffuse_color==-1
            ||specular_color==-1||spot_direction==-1||attenuation_factors ==-1||compute_distance_attenuation==-1
                ||spot_exponent==-1||spot_cutoff_angle==-1) {
            Log.d(TAG, "draw : get matrixLocation error");
            return;
        }
        GLES30.glUniform4fv(position, 1, new float[]{0.25f,0.25f,1.0f,1.f}, 0);
        GLES30.glUniform4fv(ambient_color, 1, new float[]{0.9f,0.6f,0.5f,1.f}, 0);
        GLES30.glUniform4fv(diffuse_color, 1, new float[]{0.3f,0.4f,0.2f,1.f}, 0);
        GLES30.glUniform4fv(specular_color, 1, new float[]{0.9f,0.9f,0.9f,1.f}, 0);
        GLES30.glUniform3fv(spot_direction, 1, new float[]{0.0f,0.0f,-1.0f}, 0);
        GLES30.glUniform3fv(attenuation_factors, 1, new float[]{1.0f,1.0f,1.0f}, 0);
        GLES30.glUniform1i(compute_distance_attenuation, GLES30.GL_TRUE);
        GLES30.glUniform1f(spot_exponent, 0.1f);
        GLES30.glUniform1f(spot_cutoff_angle, 90.0f);


        int ambient_color1= GLES30.glGetUniformLocation(program, "material.ambient_color");
        int diffuse_color1= GLES30.glGetUniformLocation(program, "material.diffuse_color");
        int specular_color1= GLES30.glGetUniformLocation(program, "material.specular_color");
        int specular_exponent1= GLES30.glGetUniformLocation(program, "material.specular_exponent");
        if(ambient_color1 ==-1||diffuse_color1==-1 ||specular_color1==-1||specular_exponent1==-1){
            Log.d(TAG, "draw : get material_properties error");
            return;
        }
        GLES30.glUniform4fv(ambient_color1, 1, new float[]{0.3f,0.6f,0.3f,1.f}, 0);
        GLES30.glUniform4fv(diffuse_color1, 1, new float[]{0.1f,0.5f,0.1f,1.f}, 0);
        GLES30.glUniform4fv(specular_color1, 1, new float[]{0.1f,0.5f,0.1f,1.f}, 0);
        GLES30.glUniform1f(specular_exponent1,0.1f);

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


        Matrix.multiplyMM(mvpMatrix,0,matrix,0,matrix_tran,0);//右侧的最先实行变换，因为离点近嘛
        Log.d(TAG, "draw1.0: " + GLES30.glGetError());



        GLES30.glUniformMatrix4fv(matrixLocation, 1, false, mvpMatrix, 0);

        GLES30.glBindVertexArray(0);
//        GLES30.glPolygonOffset(-1,-1);
        Log.d(TAG, "draw1.3: " + GLES30.glGetError());
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, drawIndicesBuff.capacity(), GLES30.GL_UNSIGNED_BYTE, 0);
        Log.d(TAG, "draw1.4: " + GLES30.glGetError());//一定要先画面，再画线


//        GLES30.glBindVertexArray(VERTEXBufferArrayID[0]);
////        GLES30.glPolygonOffset(1,1);
//        Log.d(TAG, "draw1.1: " + GLES30.glGetError());
//        GLES30.glDrawElements(GLES30.GL_LINES,drawIndicesBuffLine.capacity(),GLES30.GL_UNSIGNED_BYTE,0);
//        Log.d(TAG, "draw1.2: " + GLES30.glGetError());//会有个问题，画的线depth较低的不会被挡住




    }

}
