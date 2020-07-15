package com.chemicalwedding.artemis.database;

public class Look {
    private Integer pk;
    private Integer effectId;
    private String name;
    private Integer green, red, blue;

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public Integer getEffectId() {
        return effectId;
    }

    public void setEffectId(Integer effectId) {
        this.effectId = effectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGreen() {
        return green;
    }

    public void setGreen(Integer green) {
        this.green = green;
    }

    public Integer getRed() {
        return red;
    }

    public void setRed(Integer red) {
        this.red = red;
    }

    public Integer getBlue() {
        return blue;
    }

    public void setBlue(Integer blue) {
        this.blue = blue;
    }

}
