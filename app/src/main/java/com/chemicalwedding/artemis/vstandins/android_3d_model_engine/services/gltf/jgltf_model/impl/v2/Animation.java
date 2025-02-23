/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package com.chemicalwedding.artemis.vstandins.android_3d_model_engine.services.gltf.jgltf_model.impl.v2;

import java.util.ArrayList;
import java.util.List;


/**
 * A keyframe animation. 
 * 
 * Auto-generated for animation.schema.json 
 * 
 */
public class Animation
    extends GlTFChildOfRootProperty
{

    /**
     * An array of channels, each of which targets an animation's sampler at 
     * a node's property. Different channels of the same animation can't have 
     * equal targets. (required)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;Targets an animation's sampler at a node's property. 
     * (optional) 
     * 
     */
    private List<AnimationChannel> channels;
    /**
     * An array of samplers that combines input and output accessors with an 
     * interpolation algorithm to define a keyframe graph (but not its 
     * target). (required)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;Combines input and output accessors with an interpolation 
     * algorithm to define a keyframe graph (but not its target). (optional) 
     * 
     */
    private List<AnimationSampler> samplers;

    /**
     * An array of channels, each of which targets an animation's sampler at 
     * a node's property. Different channels of the same animation can't have 
     * equal targets. (required)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;Targets an animation's sampler at a node's property. 
     * (optional) 
     * 
     * @param channels The channels to set
     * @throws NullPointerException If the given value is <code>null</code>
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setChannels(List<AnimationChannel> channels) {
        if (channels == null) {
            throw new NullPointerException((("Invalid value for channels: "+ channels)+", may not be null"));
        }
        if (channels.size()< 1) {
            throw new IllegalArgumentException("Number of channels elements is < 1");
        }
        this.channels = channels;
    }

    /**
     * An array of channels, each of which targets an animation's sampler at 
     * a node's property. Different channels of the same animation can't have 
     * equal targets. (required)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;Targets an animation's sampler at a node's property. 
     * (optional) 
     * 
     * @return The channels
     * 
     */
    public List<AnimationChannel> getChannels() {
        return this.channels;
    }

    /**
     * Add the given channels. The channels of this instance will be replaced 
     * with a list that contains all previous elements, and additionally the 
     * new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addChannels(AnimationChannel element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<AnimationChannel> oldList = this.channels;
        List<AnimationChannel> newList = new ArrayList<AnimationChannel>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.channels = newList;
    }

    /**
     * Remove the given channels. The channels of this instance will be 
     * replaced with a list that contains all previous elements, except for 
     * the removed one. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeChannels(AnimationChannel element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<AnimationChannel> oldList = this.channels;
        List<AnimationChannel> newList = new ArrayList<AnimationChannel>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        this.channels = newList;
    }

    /**
     * An array of samplers that combines input and output accessors with an 
     * interpolation algorithm to define a keyframe graph (but not its 
     * target). (required)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;Combines input and output accessors with an interpolation 
     * algorithm to define a keyframe graph (but not its target). (optional) 
     * 
     * @param samplers The samplers to set
     * @throws NullPointerException If the given value is <code>null</code>
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setSamplers(List<AnimationSampler> samplers) {
        if (samplers == null) {
            throw new NullPointerException((("Invalid value for samplers: "+ samplers)+", may not be null"));
        }
        if (samplers.size()< 1) {
            throw new IllegalArgumentException("Number of samplers elements is < 1");
        }
        this.samplers = samplers;
    }

    /**
     * An array of samplers that combines input and output accessors with an 
     * interpolation algorithm to define a keyframe graph (but not its 
     * target). (required)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;Combines input and output accessors with an interpolation 
     * algorithm to define a keyframe graph (but not its target). (optional) 
     * 
     * @return The samplers
     * 
     */
    public List<AnimationSampler> getSamplers() {
        return this.samplers;
    }

    /**
     * Add the given samplers. The samplers of this instance will be replaced 
     * with a list that contains all previous elements, and additionally the 
     * new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addSamplers(AnimationSampler element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<AnimationSampler> oldList = this.samplers;
        List<AnimationSampler> newList = new ArrayList<AnimationSampler>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.samplers = newList;
    }

    /**
     * Remove the given samplers. The samplers of this instance will be 
     * replaced with a list that contains all previous elements, except for 
     * the removed one. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeSamplers(AnimationSampler element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<AnimationSampler> oldList = this.samplers;
        List<AnimationSampler> newList = new ArrayList<AnimationSampler>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        this.samplers = newList;
    }

}
