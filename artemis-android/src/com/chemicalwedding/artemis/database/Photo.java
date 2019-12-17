package com.chemicalwedding.artemis.database;

import java.util.Date;

public class Photo {

    private String name;
    private String path;
    private Date date;

    public Photo(String name, String path, Date date) {
        this.name = name;
        this.path = path;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(String path) {
        this.date = date;
    }
}

