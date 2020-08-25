package com.chemicalwedding.artemis.vstandins.util.android;

import com.chemicalwedding.artemis.vstandins.util.android.assets.Handler;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class AndroidURLStreamHandlerFactory implements URLStreamHandlerFactory {

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("assets".equals(protocol)) {
            return new Handler();
        } else if ("content".equals(protocol)){
            return new com.chemicalwedding.artemis.vstandins.util.android.content.Handler();
        }
        return null;
    }
}
