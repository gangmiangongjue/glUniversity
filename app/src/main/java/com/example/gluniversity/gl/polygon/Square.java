package com.example.gluniversity.gl.polygon;

import android.opengl.GLES30;
import android.util.Log;
import android.widget.Switch;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Square extends Polygon {
    private final static String TAG = "Square";
    int[] VERTEXBufferArrayID = new int[]{0};
    public Square(){
        float[] vertex = new float[]{
                -0.5f,0.5f,
                1.0f,0.0f,0.0f,1.0f,
                -0.5f,-0.5f,
                0.0f,1.0f,0.0f,1.0f,
                0.5f,0.5f,
                1.0f,1.0f,1.0f,1.0f,
                0.5f,-0.5f,
                0.0f,0.0f,1.0f,1.0f
        };
        ByteBuffer vBuffer = ByteBuffer.allocateDirect(vertex.length*4);//must allocate direct buffer
        vBuffer.order(ByteOrder.nativeOrder());
        vertexBuff = vBuffer.asFloatBuffer();
        vertexBuff.put(vertex);

        byte[] drawIndices = new byte[]{0,1,3,0,3,2};
        drawIndicesBuff = ByteBuffer.allocateDirect(drawIndices.length);
        drawIndicesBuff.order(ByteOrder.nativeOrder());
        drawIndicesBuff.put(drawIndices);
    }
    @Override
    public void draw(int  drawTech) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        if (program == 0) {
            Log.d(TAG, "draw program error: ");
            return;
        }
        GLES30.glUseProgram(program);
        int positionHandle = GLES30.glGetAttribLocation(program,"vPosition");
        int colorHandle = GLES30.glGetAttribLocation(program,"vColor");
        if (positionHandle == -1){
            Log.d(TAG, "draw program error: ");
            return;
        }
        if (colorHandle == -1){
            Log.d(TAG, "draw color error: ");
            return;
        }
        switch (drawTech){
            case NAIVE:{
                GLES30.glEnableVertexAttribArray(positionHandle);
                GLES30.glEnableVertexAttribArray(colorHandle);
                vertexBuff.position(0);//这里position必须设置
                GLES30.glVertexAttribPointer(positionHandle,VERTEX_DIMENSION,GLES30.GL_FLOAT,false,(VERTEX_DIMENSION+COLOR_DIMENSION)*4,vertexBuff);//无buffer版本
                vertexBuff.position(VERTEX_DIMENSION*4);//这里position没有毛用，所以最好还是把buffer分开存储
                GLES30.glVertexAttribPointer(colorHandle,COLOR_DIMENSION,GLES30.GL_FLOAT,false,(VERTEX_DIMENSION+COLOR_DIMENSION)*4,vertexBuff);
                GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,0,4);
                GLES30.glDisableVertexAttribArray(positionHandle);
                GLES30.glDisableVertexAttribArray(colorHandle);
            }break;
            case VBO:{
                GLES30.glEnableVertexAttribArray(positionHandle);
                GLES30.glEnableVertexAttribArray(colorHandle);
                if (bufferIds[0] == 0 && bufferIds[1] == 0 ){
                    Log.d(TAG, "draw no buffer before, create now: ");
                    drawIndicesBuff.position(0);
                    vertexBuff.position(0);//这里必须传入正确的位置，gles的jni会检查buffer位置

                    GLES30.glGenBuffers(bufferIds.length,bufferIds,0);
                    GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,bufferIds[0]);
                    GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,bufferIds[1]);//VBO
                    GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,vertexBuff.capacity()*4,vertexBuff,GLES30.GL_STATIC_DRAW);
                    GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,drawIndicesBuff.capacity(),drawIndicesBuff,GLES30.GL_STATIC_DRAW);

                    GLES30.glVertexAttribPointer(positionHandle,VERTEX_DIMENSION,GLES30.GL_FLOAT,false,(VERTEX_DIMENSION+COLOR_DIMENSION)*4,0);//有buffer版本
                    GLES30.glVertexAttribPointer(colorHandle,COLOR_DIMENSION,GLES30.GL_FLOAT,false,(VERTEX_DIMENSION+COLOR_DIMENSION)*4,VERTEX_DIMENSION*4);
                }
                GLES30.glDrawElements(GLES30.GL_TRIANGLES,drawIndicesBuff.capacity(),GLES30.GL_UNSIGNED_BYTE,0);
                GLES30.glDisableVertexAttribArray(positionHandle);
                GLES30.glDisableVertexAttribArray(colorHandle);
            }break;
            case VAO:{
                if (bufferIds[0] == 0 && bufferIds[1] == 0 ){
                    Log.d(TAG, "draw no buffer before, create now: ");
                    drawIndicesBuff.position(0);
                    vertexBuff.position(0);//这里必须传入正确的位置，gles的jni会检查buffer位置

                    GLES30.glGenBuffers(bufferIds.length,bufferIds,0);
                    GLES30.glGenVertexArrays(1,VERTEXBufferArrayID,0);//VAO
                    Log.d(TAG, "draw new VERTEX array id: " + VERTEXBufferArrayID[0]);
                    GLES30.glBindVertexArray(VERTEXBufferArrayID[0]);

                    GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,bufferIds[0]);//必须在glBindVertexArray之后
                    GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,bufferIds[1]);//VBO
                    GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,vertexBuff.capacity()*4,vertexBuff,GLES30.GL_STATIC_DRAW);
                    GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,drawIndicesBuff.capacity(),drawIndicesBuff,GLES30.GL_STATIC_DRAW);

                    GLES30.glEnableVertexAttribArray(positionHandle);//必须在glBindVertexArray之后
                    GLES30.glEnableVertexAttribArray(colorHandle);
                    GLES30.glVertexAttribPointer(positionHandle,VERTEX_DIMENSION,GLES30.GL_FLOAT,false,(VERTEX_DIMENSION+COLOR_DIMENSION)*4,0);
                    GLES30.glVertexAttribPointer(colorHandle,COLOR_DIMENSION,GLES30.GL_FLOAT,false,(VERTEX_DIMENSION+COLOR_DIMENSION)*4,VERTEX_DIMENSION*4);
                    GLES30.glBindVertexArray(0);
                    GLES30.glDisableVertexAttribArray(positionHandle);
                    GLES30.glDisableVertexAttribArray(colorHandle);
                }

                GLES30.glBindVertexArray(VERTEXBufferArrayID[0]);
                GLES30.glDrawElements(GLES30.GL_TRIANGLES,drawIndicesBuff.capacity(),GLES30.GL_UNSIGNED_BYTE,0);
                GLES30.glBindVertexArray(0);
                GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);
                GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,0);
            }break;
            case MAP:{
                GLES30.glEnableVertexAttribArray(positionHandle);
                GLES30.glEnableVertexAttribArray(colorHandle);
                if (bufferIds[0] == 0 && bufferIds[1] == 0 ){
                    Log.d(TAG, "draw no buffer before, create now: ");
                    drawIndicesBuff.position(0);
                    vertexBuff.position(0);//这里必须传入正确的位置，gles的jni会检查buffer位置

                    GLES30.glGenBuffers(bufferIds.length,bufferIds,0);
                    GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,bufferIds[0]);
                    GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,bufferIds[1]);//VBO
                    GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,vertexBuff.capacity()*4,null,GLES30.GL_STATIC_DRAW);
                    GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,drawIndicesBuff.capacity(),null,GLES30.GL_STATIC_DRAW);
                    Log.d(TAG, "draw error1: " + GLES30.glGetError() + " size :"+vertexBuff.capacity()*4);
                    ByteBuffer vertexMapBuffer = (ByteBuffer) GLES30.glMapBufferRange(GLES30.GL_ARRAY_BUFFER,0,vertexBuff.capacity()*4,GLES30.GL_MAP_WRITE_BIT);
                    Log.d(TAG, "draw error2: " + GLES30.glGetError());
                    Log.d(TAG, "draw type: " + vertexMapBuffer.getClass().getSimpleName());
                    vertexMapBuffer.order(ByteOrder.nativeOrder());

                    FloatBuffer vertexFloatBufferF = vertexMapBuffer.asFloatBuffer();
                    vertexFloatBufferF.put(vertexBuff).position(0);
                    if (!GLES30.glUnmapBuffer(GLES30.GL_ARRAY_BUFFER)){
                        Log.e(TAG, "draw glUnmapBuffer failed!");
                    }

                    ByteBuffer drawIndicesMapBuffer = (ByteBuffer) GLES30.glMapBufferRange(GLES30.GL_ELEMENT_ARRAY_BUFFER,0,drawIndicesBuff.capacity(),GLES30.GL_MAP_WRITE_BIT);
                    drawIndicesMapBuffer.order(ByteOrder.nativeOrder());//java默认是大端
                    drawIndicesMapBuffer.put(drawIndicesBuff).position(0);
                    if (!GLES30.glUnmapBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER)){
                        Log.e(TAG, "draw glUnmapBuffer failed!");
                    }

                    Log.d(TAG, "draw buffer type: "+vertexMapBuffer.getClass().getSimpleName());
                    GLES30.glVertexAttribPointer(positionHandle,VERTEX_DIMENSION,GLES30.GL_FLOAT,false,(VERTEX_DIMENSION+COLOR_DIMENSION)*4,0);//有buffer版本
                    GLES30.glVertexAttribPointer(colorHandle,COLOR_DIMENSION,GLES30.GL_FLOAT,false,(VERTEX_DIMENSION+COLOR_DIMENSION)*4,VERTEX_DIMENSION*4);

                }
                GLES30.glDrawElements(GLES30.GL_TRIANGLES,drawIndicesBuff.capacity(),GLES30.GL_UNSIGNED_BYTE,0);
                GLES30.glDisableVertexAttribArray(positionHandle);
                GLES30.glDisableVertexAttribArray(colorHandle);
            }break;
            default:
                Log.e(TAG, "draw: unknow type");
                break;

        }
        Log.d(TAG, "draw result: " + GLES30.glGetError());



    }
}
