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
package com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.v1.gl;

import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.MaterialModel;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.NodeModel;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.Optionals;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.gl.ProgramModel;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.gl.ShaderModel;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.gl.ShaderModel.ShaderType;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.gl.TechniqueModel;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.gl.TechniqueParametersModel;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.gl.TechniqueStatesFunctionsModel;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.gl.TechniqueStatesModel;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.gl.impl.DefaultProgramModel;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.gl.impl.DefaultShaderModel;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.gl.impl.DefaultTechniqueModel;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.gl.impl.DefaultTechniqueParametersModel;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.gl.impl.DefaultTechniqueStatesModel;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.gl.impl.v1.DefaultTechniqueStatesFunctionsModelV1;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.impl.DefaultMaterialModel;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.impl.v1.Material;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.impl.v1.Shader;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.impl.v1.Technique;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.impl.v1.TechniqueParameters;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.impl.v1.TechniqueStates;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.impl.v1.TechniqueStatesFunctions;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.io.Buffers;
import com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.io.IO;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * A class containing the default {@link TechniqueModel} and 
 * {@link MaterialModel} instances that correspond to the
 * default {@link Technique} and {@link Material} of glTF 1.0.
 */
public class DefaultModels
{
    /**
     * The logger used in this class
     */
    private static final Logger logger =
        Logger.getLogger(DefaultModels.class.getName());
    
    /**
     * The default vertex {@link ShaderModel}
     */
    private static final DefaultShaderModel DEFAULT_VERTEX_SHADER_MODEL;
    
    /**
     * The default fragment {@link ShaderModel}
     */
    private static final DefaultShaderModel DEFAULT_FRAGMENT_SHADER_MODEL;
    
    /**
     * The default {@link ProgramModel}
     */
    private static final DefaultProgramModel DEFAULT_PROGRAM_MODEL;
    
    /**
     * The default {@link TechniqueModel}
     */
    private static final DefaultTechniqueModel DEFAULT_TECHNIQUE_MODEL;
    
    /**
     * The default {@link MaterialModel}
     */
    private static final DefaultMaterialModel DEFAULT_MATERIAL_MODEL;
    
    static
    {
        // Create models for the default vertex- and fragment shader
        Shader vertexShader = GltfDefaults.getDefaultVertexShader();
        DEFAULT_VERTEX_SHADER_MODEL = 
            new DefaultShaderModel(vertexShader.getUri(),
                ShaderType.VERTEX_SHADER);
        initShaderData(DEFAULT_VERTEX_SHADER_MODEL);
        
        Shader fragmentShader = GltfDefaults.getDefaultFragmentShader();
        DEFAULT_FRAGMENT_SHADER_MODEL = 
            new DefaultShaderModel(fragmentShader.getUri(),
                ShaderType.FRAGMENT_SHADER);
        initShaderData(DEFAULT_FRAGMENT_SHADER_MODEL);
        
        // Create a model for the default program
        DEFAULT_PROGRAM_MODEL = new DefaultProgramModel();
        DEFAULT_PROGRAM_MODEL.setVertexShaderModel(
            DEFAULT_VERTEX_SHADER_MODEL);
        DEFAULT_PROGRAM_MODEL.setFragmentShaderModel(
            DEFAULT_FRAGMENT_SHADER_MODEL);
        
        // Create a model for the default technique
        Technique technique = GltfDefaults.getDefaultTechnique();
        DEFAULT_TECHNIQUE_MODEL = new DefaultTechniqueModel();
        DEFAULT_TECHNIQUE_MODEL.setProgramModel(DEFAULT_PROGRAM_MODEL);
        
        addParametersForDefaultTechnique(technique, DEFAULT_TECHNIQUE_MODEL);
        addAttributes(technique, DEFAULT_TECHNIQUE_MODEL);
        addUniforms(technique, DEFAULT_TECHNIQUE_MODEL);
        
        TechniqueStates states = technique.getStates();
        List<Integer> enable = Optionals.of(
            states.getEnable(), 
            states.defaultEnable());
        
        TechniqueStatesFunctions functions = states.getFunctions();
        TechniqueStatesFunctionsModel techniqueStatesFunctionsModel =
            new DefaultTechniqueStatesFunctionsModelV1(functions);
        TechniqueStatesModel techniqueStatesModel = 
            new DefaultTechniqueStatesModel(
                enable, techniqueStatesFunctionsModel);
        DEFAULT_TECHNIQUE_MODEL.setTechniqueStatesModel(techniqueStatesModel);

        // Create a model for the default material
        Material material = GltfDefaults.getDefaultMaterial();
        DEFAULT_MATERIAL_MODEL = new DefaultMaterialModel();
        DEFAULT_MATERIAL_MODEL.setValues(material.getValues());
        DEFAULT_MATERIAL_MODEL.setTechniqueModel(DEFAULT_TECHNIQUE_MODEL);
        
    }
    
    /**
     * Return the default {@link MaterialModel}
     * 
     * @return The default {@link MaterialModel}
     */
    public static MaterialModel getDefaultMaterialModel()
    {
        return DEFAULT_MATERIAL_MODEL;
    }
    
    /**
     * Initialize the {@link DefaultShaderModel#setShaderData(ByteBuffer)
     * shader data} for the given {@link ShaderModel}
     * 
     * @param shaderModel The {@link ShaderModel}
     */
    private static void initShaderData(DefaultShaderModel shaderModel)
    {
        try
        {
            URI uri = new URI(shaderModel.getUri());
            byte[] data = IO.read(uri);
            ByteBuffer shaderData = Buffers.create(data);
            shaderModel.setShaderData(shaderData);
        }
        catch (URISyntaxException | IOException e)
        {
            // This should never happen: The default shaders have valid
            // data URI that contain the shader data.
            logger.severe("Failed to initialize shader data");
        }
    }

    /**
     * Return the default {@link TechniqueModel}
     * 
     * @return The default {@link TechniqueModel}
     */
    public static TechniqueModel getDefaultTechniqueModel()
    {
        return DEFAULT_TECHNIQUE_MODEL;
    }
    
    /**
     * Add all {@link TechniqueParametersModel} instances for the 
     * attributes of the given {@link Technique} to the given
     * {@link TechniqueModel}
     * 
     * @param technique The {@link Technique}
     * @param techniqueModel The {@link TechniqueModel}
     */
    private static void addParametersForDefaultTechnique(
        Technique technique, DefaultTechniqueModel techniqueModel)
    {
        Map<String, TechniqueParameters> parameters =
            Optionals.of(technique.getParameters());
        for (Entry<String, TechniqueParameters> entry : parameters.entrySet())
        {
            String parameterName = entry.getKey();
            TechniqueParameters parameter = entry.getValue();
            
            int type = parameter.getType();
            int count = Optionals.of(parameter.getCount(), 1);
            String semantic = parameter.getSemantic();
            Object value = parameter.getValue();
            
            // The NodeModel is always null in the default technique
            NodeModel nodeModel = null;
            
            TechniqueParametersModel techniqueParametersModel =
                new DefaultTechniqueParametersModel(
                    type, count, semantic, value, nodeModel);
            techniqueModel.addParameter(
                parameterName, techniqueParametersModel);
        }
    }
    
    /**
     * Add all attribute entries of the given {@link Technique} to the given
     * {@link TechniqueModel}
     * 
     * @param technique The {@link Technique}
     * @param techniqueModel The {@link TechniqueModel}
     */
    private static void addAttributes(Technique technique,
        DefaultTechniqueModel techniqueModel)
    {
        Map<String, String> attributes =
            Optionals.of(technique.getAttributes());
        for (Entry<String, String> entry : attributes.entrySet())
        {
            String attributeName = entry.getKey();
            String parameterName = entry.getValue();
            techniqueModel.addAttribute(attributeName, parameterName);
        }
    }

    /**
     * Add all uniform entries of the given {@link Technique} to the given
     * {@link TechniqueModel}
     * 
     * @param technique The {@link Technique}
     * @param techniqueModel The {@link TechniqueModel}
     */
    private static void addUniforms(Technique technique,
        DefaultTechniqueModel techniqueModel)
    {
        Map<String, String> uniforms =
            Optionals.of(technique.getUniforms());
        for (Entry<String, String> entry : uniforms.entrySet())
        {
            String uniformName = entry.getKey();
            String parameterName = entry.getValue();
            techniqueModel.addUniform(uniformName, parameterName);
        }
    }
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private DefaultModels()
    {
        // Private constructor to prevent instantiation
    }
    
    
}
