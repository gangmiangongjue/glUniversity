package com.example.gluniversity.gl.polygon;

import android.opengl.GLES30;

import com.example.gluniversity.gl.ESGLUtils;

public abstract class polygon {

    protected String vertexShaderCode;
    protected String fragmentShaderCode;
    protected float[] vertex;

    polygon(){

    }

    void initGL(){
        int program = GLES30.glCreateProgram();
        int vertextShader = ESGLUtils.loadShader(vertexShaderCode,GLES30.GL_VERTEX_SHADER);
        int fragmentShader = ESGLUtils.loadShader(fragmentShaderCode,GLES30.GL_FRAGMENT_SHADER)；
        GLES30.glAttachShader(program,vertextShader);
        GLES30.glAttachShader(program,fragmentShader);
        GLES30.glLinkProgram(program);
        GLES30.glLink

    }

    protected abstract void draw();
}
