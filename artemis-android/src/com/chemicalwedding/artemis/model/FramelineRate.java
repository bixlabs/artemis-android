package com.chemicalwedding.artemis.model;

public class FramelineRate {
    private double id;
    private double rate;
    private boolean custom;

    public FramelineRate(double rate, boolean custom) {
        this.rate = rate;
        this.custom = custom;
    }

    public FramelineRate() {
    }

    public double getId() {
        return id;
    }

    public void setId(double id) {
        this.id = id;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }
}
