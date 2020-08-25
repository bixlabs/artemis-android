/*
 * www.javagl.de - JglTF
 *
 * Copyright 2015-2016 Marco Hutter - http://www.javagl.de
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.io.v2;

import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.impl.v2.GlTF;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.io.GltfAssetWriter;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.io.GltfModelWriter;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.io.GltfWriter;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.v2.GltfModelV2;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A class for writing a {@link GltfModelV2}. This class contains
 * implementations for the methods of the {@link GltfModelWriter},
 * for glTF 2.0 assets. Clients should not use this class directly,
 * but only the {@link GltfModelWriter}.
 */
public final class GltfModelWriterV2
{
    /**
     * Default constructor
     */
    public GltfModelWriterV2()
    {
        // Default constructor
    }
    
    /**
     * Write the given {@link GltfModelV2} to the given file. External
     * references of buffers and images that are given via the respective 
     * URI string will be resolved against the parent directory of the 
     * given file, and the corresponding data will be written into 
     * the corresponding files. 
     * 
     * @param gltfModel The {@link GltfModelV2}
     * @param file The file
     * @throws IOException If an IO error occurs
     */
    public void write(GltfModelV2 gltfModel, File file)
        throws IOException
    {
        DefaultAssetCreatorV2 assetCreator = new DefaultAssetCreatorV2();
        GltfAssetV2 gltfAsset = assetCreator.create(gltfModel);
        GltfAssetWriter gltfAssetWriter = new GltfAssetWriter();
        gltfAssetWriter.write(gltfAsset, file);
    }
    
    /**
     * Write the given {@link GltfModelV2} as a binary glTF asset to the
     * given output stream. The caller is responsible for closing the 
     * given stream.
     * 
     * @param gltfModel The {@link GltfModelV2}
     * @param outputStream The output stream
     * @throws IOException If an IO error occurs
     */
    public void writeBinary(GltfModelV2 gltfModel, OutputStream outputStream)
        throws IOException
    {
        BinaryAssetCreatorV2 assetCreator = new BinaryAssetCreatorV2();
        GltfAssetV2 gltfAsset = assetCreator.create(gltfModel);
        GltfAssetWriterV2 gltfAssetWriter = new GltfAssetWriterV2();
        gltfAssetWriter.writeBinary(gltfAsset, outputStream);
    }
    
    /**
     * Write the given {@link GltfModelV2} as an embedded glTF asset to the
     * given output stream. The caller is responsible for closing the 
     * given stream.
     * 
     * @param gltfModel The {@link GltfModelV2}
     * @param outputStream The output stream
     * @throws IOException If an IO error occurs
     */
    public void writeEmbedded(GltfModelV2 gltfModel, OutputStream outputStream)
        throws IOException
    {
        EmbeddedAssetCreatorV2 assetCreator = new EmbeddedAssetCreatorV2();
        GltfAssetV2 gltfAsset = assetCreator.create(gltfModel);
        GltfWriter gltfWriter = new GltfWriter();
        GlTF gltf = gltfAsset.getGltf();
        gltfWriter.write(gltf, outputStream);
    }
}
