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
package com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.impl;

import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.CameraModel;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.NodeModel;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.Suppliers;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.impl.v1.Camera;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.impl.v1.Node;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Implementation of a {@link CameraModel}
 */
public final class DefaultCameraModel extends AbstractNamedModelElement
    implements CameraModel
{
    /**
     * The name of this camera model, suitable to be shown to a user 
     */
    private String instanceName;
    
    /**
     * The {@link NodeModel} of the {@link Node} that the {@link Camera}
     * is attached to 
     */
    private NodeModel nodeModel;
    
    /**
     * The function that computes the view matrix
     */
    private final Function<float[], float[]> viewMatrixComputer;
    
    /**
     * The function that computes the projection matrix
     */
    private final BiFunction<float[], Float, float[]> projectionMatrixComputer;

    /**
     * Creates a new instance
     * @param viewMatrixComputer The function that computes the view matrix
     * @param projectionMatrixComputer The function that computes the 
     * projection matrix
     */
    public DefaultCameraModel(
        Function<float[], float[]> viewMatrixComputer,
        BiFunction<float[], Float, float[]> projectionMatrixComputer)
    {
        this.viewMatrixComputer =
            Objects.requireNonNull(viewMatrixComputer,
                "The viewMatrixComputer may not be null");
        this.projectionMatrixComputer = 
            Objects.requireNonNull(projectionMatrixComputer,
                "The projectionMatrixComputer may not be null");
    }

    
    /**
     * Set the name of this camera model, suitable to be shown to a user
     * 
     * @param instanceName The instance name
     */
    public void setInstanceName(String instanceName)
    {
        this.instanceName = instanceName;
    }
    
    /**
     * Set the {@link NodeModel} that the camera is attached to
     * 
     * @param nodeModel The {@link NodeModel} that the camera is attached to
     */
    public void setNodeModel(NodeModel nodeModel)
    {
        this.nodeModel = nodeModel;
    }
    
    
    @Override
    public String getInstanceName()
    {
        return instanceName;
    }

    @Override
    public NodeModel getNodeModel()
    {
        return nodeModel;
    }

    @Override
    public float[] computeViewMatrix(float result[])
    {
        return viewMatrixComputer.apply(result);
    }
    
    @Override
    public float[] computeProjectionMatrix(float result[], Float aspectRatio)
    {
        return projectionMatrixComputer.apply(result, aspectRatio);
    }
    
    @Override
    public Supplier<float[]> createViewMatrixSupplier()
    {
        return Suppliers.createTransformSupplier(this, 
            CameraModel::computeViewMatrix);
    }
    
    @Override
    public Supplier<float[]> createProjectionMatrixSupplier(
        DoubleSupplier aspectRatioSupplier)
    {
        return Suppliers.createTransformSupplier(this, (c, t) -> 
        {
            Float aspectRatio = null;
            if (aspectRatioSupplier != null)
            {
                aspectRatio = (float)aspectRatioSupplier.getAsDouble();
            }
            computeProjectionMatrix(t, aspectRatio);
        });
    }
    
    
}
