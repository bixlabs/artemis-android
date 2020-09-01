package com.chemicalwedding.artemis.database;

import java.util.Date;

public class MediaFile {

    private String name;
    private String path;
    private Date date;
    private MediaType type;

    public MediaFile(String name, String path, Date date, MediaType type) {
        this.name = name;
        this.path = path;
        this.date = date;
        this.type = type;
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

    public MediaType getMediaType() {
        return type;
    }

    public void setMediaType(MediaType type) {
        this.type = type;
    }
}
