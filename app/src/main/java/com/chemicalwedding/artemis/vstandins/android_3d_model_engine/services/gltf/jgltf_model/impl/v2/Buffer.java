/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.impl.v2;



/**
 * A buffer points to binary geometry, animation, or skins. 
 * 
 * Auto-generated for buffer.schema.json 
 * 
 */
public class Buffer
    extends GlTFChildOfRootProperty
{

    /**
     * The uri of the buffer. (optional) 
     * 
     */
    private String uri;
    /**
     * The length of the buffer in bytes. (required)<br> 
     * Minimum: 1 (inclusive) 
     * 
     */
    private Integer byteLength;

    /**
     * The uri of the buffer. (optional) 
     * 
     * @param uri The uri to set
     * 
     */
    public void setUri(String uri) {
        if (uri == null) {
            this.uri = uri;
            return ;
        }
        this.uri = uri;
    }

    /**
     * The uri of the buffer. (optional) 
     * 
     * @return The uri
     * 
     */
    public String getUri() {
        return this.uri;
    }

    /**
     * The length of the buffer in bytes. (required)<br> 
     * Minimum: 1 (inclusive) 
     * 
     * @param byteLength The byteLength to set
     * @throws NullPointerException If the given value is <code>null</code>
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setByteLength(Integer byteLength) {
        if (byteLength == null) {
            throw new NullPointerException((("Invalid value for byteLength: "+ byteLength)+", may not be null"));
        }
        if (byteLength< 1) {
            throw new IllegalArgumentException("byteLength < 1");
        }
        this.byteLength = byteLength;
    }

    /**
     * The length of the buffer in bytes. (required)<br> 
     * Minimum: 1 (inclusive) 
     * 
     * @return The byteLength
     * 
     */
    public Integer getByteLength() {
        return this.byteLength;
    }

}
