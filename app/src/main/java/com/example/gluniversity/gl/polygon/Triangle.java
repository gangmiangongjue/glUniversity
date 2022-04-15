package com.example.gluniversity.gl.polygon;

import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle extends Polygon {


    Triangle(){
        float[] vertex = new float[]{
                0,-0.5f,0,
                -0.5f,0.5f,0,
                0.5f,0.5f,0
        };
        ByteBuffer buffer = ByteBuffer.allocate(vertex.length*4);
        buffer.order(ByteOrder.nativeOrder());
        vertexBuff = buffer.asFloatBuffer();
        vertexBuff.put(vertex);
    }
    @Override
    protected void draw() {
        if (program == 0){
            return;
        }
        int positionHandle = GLES30.glGetAttribLocation(program,"vPosition");
        GLES30.glEnableVertexAttribArray(positionHandle);
        GLES30.glVertexAttribPointer(positionHandle,3,GLES30.GL_FLOAT,false,4,vertexBuff);

        int colorHandle = GLES30.glGetUniformLocation(program,"vColor");
        GLES30.glUniform4fv(colorHandle,4,new float[]{0,255,0,255},0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES,0,9);

    }
}
