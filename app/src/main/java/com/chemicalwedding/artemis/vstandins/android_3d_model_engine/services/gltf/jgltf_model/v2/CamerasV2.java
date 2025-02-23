/*
 * www.javagl.de - JglTF
 *
 * Copyright 2015-2017 Marco Hutter - http://www.javagl.de
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
package com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.v2;

import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.MathUtils;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.Utils;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.impl.v2.Camera;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.impl.v2.CameraOrthographic;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.impl.v2.CameraPerspective;

import java.util.logging.Logger;

/**
 * Utility methods related to cameras in glTF 2.0
 */
class CamerasV2
{
    // TODO This is the same as CamerasV2, except for the package names
    // names of the classes that it operates on. 
    
    /**
     * The logger used in this class
     */
    private static final Logger logger =
        Logger.getLogger(CamerasV2.class.getName());
    
    /**
     * Compute the projection matrix for the given {@link Camera}. If the
     * {@link Camera#getType()} is neither <code>"perspective"</code> 
     * nor <code>"orthographic"</code>, then an error message will be 
     * printed, and the result will be the identity matrix.<br>
     * <br>
     * The result will be written to the given array, as a 4x4 matrix in 
     * column major order. If the given array is <code>null</code> or does
     * not have a length of 16, then a new array with length 16 will be 
     * created and returned. 
     * 
     * @param camera The {@link Camera}
     * @param aspectRatio An optional aspect ratio to use. If this is 
     * <code>null</code>, then the aspect ratio of the camera will be used.
     * @param result The array storing the result
     * @return The result array
     */
    static float[] computeProjectionMatrix(
            Camera camera, Float aspectRatio, float result[])
    {
        float localResult[] = Utils.validate(result, 16);
        String cameraType = camera.getType();
        if ("perspective".equals(cameraType))
        {
            CameraPerspective cameraPerspective = camera.getPerspective();
            float fovRad = cameraPerspective.getYfov();
            float fovDeg = (float) Math.toDegrees(fovRad);
            float localAspectRatio = 1.0f;
            if (aspectRatio != null)
            {
                localAspectRatio = aspectRatio;
            }
            else if (cameraPerspective.getAspectRatio() != null)
            {
                localAspectRatio = cameraPerspective.getAspectRatio();
            }
            float zNear = cameraPerspective.getZnear();
            Float zFar = cameraPerspective.getZfar();
            if (zFar == null)
            {
                MathUtils.infinitePerspective4x4(
                    fovDeg, localAspectRatio, zNear, localResult);
            }
            else
            {
                MathUtils.perspective4x4(
                    fovDeg, localAspectRatio, zNear, zFar, localResult);
            }
        }
        else if ("orthographic".equals(cameraType))
        {
            CameraOrthographic cameraOrthographic =
                camera.getOrthographic();
            float xMag = cameraOrthographic.getXmag();
            float yMag = cameraOrthographic.getYmag();
            float zNear = cameraOrthographic.getZnear();
            float zFar = cameraOrthographic.getZfar();
            MathUtils.setIdentity4x4(localResult);
            localResult[0] = 1.0f / xMag;
            localResult[5] = 1.0f / yMag;
            localResult[10] = 2.0f / (zNear - zFar);
            localResult[14] = (zFar + zNear) / (zNear - zFar);
        }
        else
        {
            logger.severe("Invalid camera type: " + cameraType);
            MathUtils.setIdentity4x4(localResult);
        }
        return localResult;
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private CamerasV2()
    {
        // Private constructor to prevent instantiation
    }
}
