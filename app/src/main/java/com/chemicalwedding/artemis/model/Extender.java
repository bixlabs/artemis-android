package com.chemicalwedding.artemis.model;

public class Extender {
    private String manufacturer;
    private float factor;

    public Extender(String manufacturer, float factor) {
        this.manufacturer = manufacturer;
        this.factor = factor;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public float getFactor() {
        return factor;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }

    public String toString() {
        return manufacturer + " " + String.format("%.1f", this.factor) + " Extender";
    }
}
