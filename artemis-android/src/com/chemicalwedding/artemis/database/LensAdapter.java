package com.chemicalwedding.artemis.database;

/**
 * Data place-holder for lens adapter information.
 *
 * Represents a row in the SQLite database table "lensadapters"
 *
 */
public class LensAdapter {
    private Integer pk;
    private Double magnificationFactor;
    private boolean isCustomAdapter;

    public LensAdapter() {
    }

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public Double getMagnificationFactor() {
        return magnificationFactor;
    }

    public void setMagnificationFactor(Double magnificationFactor) {
        this.magnificationFactor = magnificationFactor;
    }

    public boolean isCustomAdapter() {
        return isCustomAdapter;
    }

    public void setCustomAdapter(boolean customAdapter) {
        isCustomAdapter = customAdapter;
    }

    @Override
    public String toString() {
        return "LensAdapter{" +
                "pk=" + pk +
                ", magnificationFactor=" + magnificationFactor +
                '}';
    }
}
