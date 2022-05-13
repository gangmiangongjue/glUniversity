package com.example.gluniversity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.YuvImage;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gluniversity.databinding.ActivityMainBinding;
import com.example.gluniversity.gl.animator.Animator;
import com.example.gluniversity.gl.animator.CubeAnimator;
import com.example.gluniversity.gl.animator.Nv21Animator;
import com.example.gluniversity.gl.animator.PlaneAnimator;

import java.io.IOException;
import java.io.InputStream;

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
                if (false){
                    animator = new Nv21Animator(MainActivity.this);
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

        String tag = "n" + 8;
        int resourceId = getResources().getIdentifier(tag,"raw",getPackageName());
        Log.d(TAG, "onCreate resource id : " + resourceId + " package name:" + getPackageName() + " img name:" + tag);
        int width = 600;
        int height = width;
        InputStream inputStream = getResources().openRawResource(resourceId);
        byte[] nv21 = new byte[width*height*3/2];
        try {
            int count = inputStream.read(nv21);
            Log.d(TAG, "onCreate read count: " + count);
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
        int[] combine = new int[width*height];
        BitmapUtils.NV21ToARGB(nv21,width,height,combine);

        ImageView imageView = binding.sampleImg;
        imageView.setImageBitmap(Bitmap.createBitmap(combine,0,width,width,height,Bitmap.Config.ARGB_8888));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus){
            Log.d(TAG, "onWindowFocusChanged hasfocus: ");
            if (glSurfaceView.getWidth() != glSurfaceView.getHeight())
            glSurfaceView.setLayoutParams(new RelativeLayout.LayoutParams(glSurfaceView.getWidth(),glSurfaceView.getWidth()));
        }
        super.onWindowFocusChanged(hasFocus);
    }

    /**
     * A native method that is implemented by the 'gluniversity' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}