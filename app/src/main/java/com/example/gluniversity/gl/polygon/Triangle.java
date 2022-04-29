package com.example.gluniversity.gl.polygon;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle extends Polygon {
    private final static String TAG  = "Triangle";

    public Triangle(){
        float[] vertex = new float[]{
                0.0f,0.5f,
                1.0f,0.0f,0.0f,1.0f,
                -0.5f,-0.5f,
                0.0f,1.0f,0.0f,1.0f,
                0.5f,-0.5f,
                0.0f,0.0f,1.0f,1.0f
        };

        ByteBuffer vBuffer = ByteBuffer.allocateDirect(vertex.length*4);//must allocate direct buffer
        vBuffer.order(ByteOrder.nativeOrder());
        vertexBuff = vBuffer.asFloatBuffer();
        vertexBuff.put(vertex);

        byte[] drawIndices = new byte[]{0,1,2};

        ByteBuffer dBuffer = ByteBuffer.allocateDirect(drawIndices.length*4);
        dBuffer.order(ByteOrder.nativeOrder());
        drawIndicesBuff = dBuffer;
        drawIndicesBuff.put(drawIndices);

    }
    @Override
    public void draw(int  drawTech) {
        long startTime = System.currentTimeMillis();
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        if (program == 0){
            return;
        }
        GLES30.glUseProgram(program);

        int positionHandle = GLES30.glGetAttribLocation(program,"vPosition");
        int colorHandle = GLES30.glGetAttribLocation(program, "vColor");
//        Log.d(TAG, "draw position: " + positionHandle);
        if (positionHandle == -1 ){
            Log.d(TAG, "draw error no position attribute: ");
            return;
        }
        if(colorHandle == -1 ){
            Log.d(TAG, "draw error no color attribute: ");
            return;
        }
        GLES30.glEnableVertexAttribArray(positionHandle);//如果附变化值，则需要开启array，todo,查看官方文档
        GLES30.glEnableVertexAttribArray(colorHandle);
        if(drawTech == VBO){
            if (bufferIds[0] == 0 && bufferIds[1] == 0){
                Log.d(TAG, "draw bufferid no, create: ");
                GLES30.glGenBuffers(2,bufferIds,0);
                GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,bufferIds[0]);
                vertexBuff.position(0);
                GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,vertexBuff.capacity()*4,vertexBuff,GLES30.GL_STATIC_DRAW);
                GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,bufferIds[1]);
                drawIndicesBuff.position(0);
                GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,drawIndicesBuff.capacity(),drawIndicesBuff,GLES30.GL_STATIC_DRAW);
            }
//            Log.d(TAG, "draw bufferid " + bufferIds[0] + " 1:"+bufferIds[1] );
//            Log.d(TAG, "draw error1: " + GLES30.glGetError());
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,bufferIds[0]);
//            Log.d(TAG, "draw error2: " + GLES30.glGetError());
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,bufferIds[1]);

            GLES30.glVertexAttribPointer(positionHandle,2,GLES30.GL_FLOAT,false,(VERTEX_DIMENSION+COLOR_DIMENSION) * 4,0);

            GLES30.glVertexAttribPointer(colorHandle,4,GLES30.GL_FLOAT,false,(VERTEX_DIMENSION+COLOR_DIMENSION) * 4,VERTEX_DIMENSION*4);

//            drawIndicesBuff.position(0);
            GLES30.glDrawElements(GLES30.GL_TRIANGLES,3,GLES30.GL_UNSIGNED_BYTE,0);
//            Log.d(TAG, "draw error3: " + GLES30.glGetError());
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,0);

        }else {
            vertexBuff.position(0);//重要！！！
            GLES30.glVertexAttribPointer(positionHandle, VERTEX_DIMENSION, GLES30.GL_FLOAT, false, (VERTEX_DIMENSION+COLOR_DIMENSION) * 4, vertexBuff);//size是数据的宽，stride为0时候数据是pack好的，而且总是以
            vertexBuff.position(VERTEX_DIMENSION*4);//没有卵用，这里的position native不认，造成颜色有误差，可以把颜色和顶点分开解决
            GLES30.glVertexAttribPointer(colorHandle, COLOR_DIMENSION, GLES30.GL_FLOAT, false,(VERTEX_DIMENSION+COLOR_DIMENSION) * 4,vertexBuff);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);//first 和count按数组理解就好了
        }
        GLES30.glDisableVertexAttribArray(positionHandle);//默认状态，如果赋常量值，则在disable状态即可。
        GLES30.glDisableVertexAttribArray(colorHandle);
        Log.d(TAG, "draw time elapse: " + (System.currentTimeMillis() - startTime));
        /*GL_NO_ERROR = 0;
        GL_INVALID_ENUM = 0x0500(1280);
        GL_INVALID_VALUE = 0x0501;
        GL_INVALID_OPERATION = 0x0502;
        GL_OUT_OF_MEMORY = 0x0505;
         */
    }
}
