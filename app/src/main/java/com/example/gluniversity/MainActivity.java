package com.example.gluniversity;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gluniversity.databinding.ActivityMainBinding;
import com.example.gluniversity.gl.animator.Animator;
import com.example.gluniversity.gl.animator.CubeAnimator;
import com.example.gluniversity.gl.polygon.Polygon;
import com.example.gluniversity.gl.polygon.Square;
import com.example.gluniversity.gl.polygon.Triangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    // Used to load the 'gluniversity' library on application startup.
    static {
        System.loadLibrary("gluniversity");
    }

    private ActivityMainBinding binding;
    private Animator animator;
    GLSurfaceView glSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        glSurfaceView = binding.sampleText;
        glSurfaceView.setEGLContextClientVersion(3);//should set before setRender
        glSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                if (true){
                    animator = new CubeAnimator(MainActivity.this);
                }else {
                    animator = new CubeAnimator(MainActivity.this);
                }
                animator.initGL();
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                animator.onViewportChange(width,height);
            }

            @Override
            public void onDrawFrame(GL10 gl) {
                animator.draw(Animator.ROTATE_X);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus){
            Log.d(TAG, "onWindowFocusChanged hasfocus: ");
            if (glSurfaceView.getWidth() != glSurfaceView.getHeight())
            glSurfaceView.setLayoutParams(new LinearLayout.LayoutParams(glSurfaceView.getWidth(),glSurfaceView.getWidth()));
        }
        super.onWindowFocusChanged(hasFocus);
    }

    /**
     * A native method that is implemented by the 'gluniversity' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}