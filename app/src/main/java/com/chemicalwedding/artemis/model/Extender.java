package com.chemicalwedding.artemis.model;

public class Extender {
    private int id;
    private String manufacturer;
    private String model;
    private float magnification;
    private float squeeze;
    private int order;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public float getMagnification() {
        return magnification;
    }

    public void setMagnification(float magnification) {
        this.magnification = magnification;
    }

    public float getSqueeze() {
        return squeeze;
    }

    public void setSqueeze(float squeeze) {
        this.squeeze = squeeze;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
