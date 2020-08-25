//package com.chemicalwedding.artemis.vstandins;
//
//import android.Manifest;
//import android.graphics.Bitmap;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.util.Log;
//import android.view.View;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//
//import com.chemicalwedding.artemis.vstandins.util.android.ContentUtils;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//public class ModelActivity extends AppCompatActivity implements ModelRenderer.BitmapGeneratedCallback {
//
//    private static final int REQUEST_CODE_LOAD_TEXTURE = 1000;
//    private static final int fullscreeen_delay = 10000;
//    private static final String TAG = ModelActivity.class.getSimpleName();
//
//    private int paramType;
//    private Uri paramUri;
//
//    private float[] backgroundColor = new float[] {0f, 0f, 0f, 1.0f };
//
////    private MyGLView glView;
//    private SceneLoader scene;
//    private Handler handler;
//    private ModelGLSurfaceView glView;
//    public static boolean takeScreenshot = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
//
//        try {
////            paramUri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.final_base_object);
////            paramUri = Uri.parse("file:///android_asset/models/spider.dae");
//            paramUri = Uri.parse("assets://" + getPackageName() + "/models/" + "FinalBaseMesh.obj");
//        } catch(Exception ex) {
//            ex.printStackTrace();
//        }
//
//        Log.i(TAG, "Params: uri: '" + paramUri + "'" );
//
//        handler = new Handler(getMainLooper());
//
////        scene = new MySceneLoader(this);
//        scene = new SceneLoader(this);
//        scene.init();
//
//        try{
//            glView = new ModelGLSurfaceView(this);
//            glView.setLayoutParams(new RelativeLayout.LayoutParams(
//                    android.app.ActionBar.LayoutParams.MATCH_PARENT,
//                    android.app.ActionBar.LayoutParams.MATCH_PARENT
//            ));
//            RelativeLayout relativeLayout = findViewById(R.id.openGLContainer);
//            relativeLayout.addView(glView);
//        } catch(Exception ex) {
//            ex.printStackTrace();
//            Toast.makeText(this, "Error loading OpenGL view:\n" +ex.getMessage(), Toast.LENGTH_LONG).show();
//        }
//
//        ContentUtils.printTouchCapabilities(getPackageManager());
//    }
//
//    public Uri getParamUri() {
//        return paramUri;
//    }
//
//    public int getParamType() {
//        return 0;
//    }
//
//    public ModelGLSurfaceView getGLView() {
//        return glView;
//    }
//
//    public SceneLoader getScene() {
//        return scene;
//    }
//
//    @Override
//    public void generated(Bitmap bitmap) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                getGLView().setVisibility(View.INVISIBLE);
//                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
//                //create a file to write bitmap data
//                String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//                File f = new File(absolutePath, "test.png");
//                ActivityCompat.requestPermissions(ModelActivity.this,
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        1);
//
//                try {
//                    f.createNewFile();
//                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
//                    byte[] bitmapdata = bos.toByteArray();
//                    FileOutputStream fos = new FileOutputStream(f);
//                    fos.write(bitmapdata);
//                    fos.flush();
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//}