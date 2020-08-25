package com.chemicalwedding.artemis.vstandins;

import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.chemicalwedding.artemis.ArtemisActivity;

import java.io.IOException;

public class ModelGLSurfaceView extends GLSurfaceView {

    private ArtemisActivity parent;
    private ModelRenderer renderer;
    private TouchController touchHandler;

    public ModelGLSurfaceView(ArtemisActivity parent) throws IOException, IllegalAccessException {
        super(parent);

        this.parent = parent;
        setEGLContextClientVersion(2);

        renderer = new ModelRenderer(this, parent);
        this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.RGBA_8888);
        setRenderer(renderer);

        this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        touchHandler = new TouchController(this, renderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return touchHandler.onTouchEvent(event);
    }

    public ModelRenderer getModelRenderer() {
        return renderer;
    }

    public ArtemisActivity getModelActivity() {
        return parent;
    }
}
