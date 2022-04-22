package com.example.gluniversity;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.TextView;

import com.example.gluniversity.databinding.ActivityMainBinding;
import com.example.gluniversity.gl.polygon.Polygon;
import com.example.gluniversity.gl.polygon.Square;
import com.example.gluniversity.gl.polygon.Triangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'gluniversity' library on application startup.
    static {
        System.loadLibrary("gluniversity");
    }

    private ActivityMainBinding binding;
    private Polygon polygon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        GLSurfaceView glSurfaceView = binding.sampleText;
        glSurfaceView.setEGLContextClientVersion(3);//should set before setRender
        glSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                if (true){
                    polygon = new Square();
                }else {
                    polygon = new Triangle();
                }
                polygon.initGL();
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                polygon.onViewportChange(width,height);
            }

            @Override
            public void onDrawFrame(GL10 gl) {
                polygon.draw(Polygon.MAP);
            }
        });
    }

    /**
     * A native method that is implemented by the 'gluniversity' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}