package com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.collada;

import android.app.Activity;
import android.net.Uri;

import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.model.Object3DData;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.LoaderTask;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.collada.entities.AnimatedModelData;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.collada.loader.ColladaLoader;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ColladaLoaderTask extends LoaderTask {

    AnimatedModelData modelData;

    public ColladaLoaderTask(Activity parent, Uri uri, Callback callback) {
        super(parent, uri, callback);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<Object3DData> build() throws IOException {
        // Parse STL
        Object[] ret = ColladaLoader.buildAnimatedModel(uri);
        List<Object3DData> datas = (List<Object3DData>) ret[1];
        modelData = (AnimatedModelData) ret[0];
        return datas;
    }

    @Override
    protected void build(List<Object3DData> datas) throws Exception {
        ColladaLoader.populateAnimatedModel(uri, datas, modelData);
        if (datas.size() == 1) {
            datas.get(0).centerAndScale(5, new float[]{0, 0, 0});
        } else {
            Object3DData.centerAndScale(datas, 5, new float[]{0, 0, 0});
        }
    }

}
