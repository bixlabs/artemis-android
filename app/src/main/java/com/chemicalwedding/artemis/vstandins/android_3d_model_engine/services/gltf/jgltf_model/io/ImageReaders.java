package com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.io;///*
// * www.javagl.de - JglTF
// *
// * Copyright 2015-2016 Marco Hutter - http://www.javagl.de
// *
// * Permission is hereby granted, free of charge, to any person
// * obtaining a copy of this software and associated documentation
// * files (the "Software"), to deal in the Software without
// * restriction, including without limitation the rights to use,
// * copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the
// * Software is furnished to do so, subject to the following
// * conditions:
// *
// * The above copyright notice and this permission notice shall be
// * included in all copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
// * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
// * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
// * OTHER DEALINGS IN THE SOFTWARE.
// */
//package org.andresoviedo.android_3d_model_engine.services.gltf.jgltf_model.io;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.ByteBuffer;
//import java.util.Iterator;
//
//import android.graphics.BitmapFactory;
//import android.graphics.Bitmap;
//
//import javax.imageio.ImageIO;
//import javax.imageio.ImageReader;
//import javax.imageio.stream.ImageInputStream;
//
///**
// * Utility methods to find <code>ImageReader</code> instances for given
// * image data.<br>
// * <br>
// * This class should not be considered as part of the public API. It may
// * change or be omitted in the future.
// */
//public class ImageReaders
//{
//    /**
//     * Tries to find an <code>ImageReader</code> that is capable of reading
//     * the given image data. The returned image reader will be initialized
//     * by passing an ImageInputStream that is created from the given data
//     * to its <code>setInput</code> method. The caller is responsible for
//     * disposing the returned image reader.
//     *
//     * @param imageData The image data
//     * @return The image reader
//     * @throws IOException If no matching image reader can be found
//     */
//    @SuppressWarnings("resource")
//    public static BitmapFactory findBitMap(ByteBuffer imageData)
//        throws IOException
//    {
//
//        byte[] imageBytes= new byte[imageData.remaining()];
//        imageData.get(imageBytes);
//        final Bitmap bmp=BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length, outM);
//
//        if (bmp != null){
//            return bmp;
//        }
//
////        InputStream inputStream =
////            Buffers.createByteBufferInputStream(imageData.slice());
////        ImageInputStream imageInputStream =
////            ImageIO.createImageInputStream(inputStream);
////        Iterator<ImageReader> imageReaders =
////            ImageIO.getImageReaders(imageInputStream);
////        if (imageReaders.hasNext())
////        {
////            ImageReader imageReader = imageReaders.arrow_next();
////            imageReader.setInput(imageInputStream);
////            return imageReader;
////        }
//        throw new IOException("Could not find Bitmap for image data");
//    }
//
//    /**
//     * Private constructor to prevent instantiation
//     */
//    private ImageReaders()
//    {
//        // Private constructor to prevent instantiation
//    }
//}
