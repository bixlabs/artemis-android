package com.chemicalwedding.artemis.model;

public class Shotplan {
    private long id;
    private String path;
    private String cameraName;
    public String lens;
    public String title;
    public String notes;
    public double latitude;
    public double longitude;

    public Shotplan(long id, String path, String cameraName, String lens, String title, String notes, double latitude, double longitude) {
        this.id = id;
        this.path = path;
        this.cameraName = cameraName;
        this.lens = lens;
        this.title = title;
        this.notes = notes;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public String getLens() {
        return lens;
    }

    public void setLens(String lens) {
        this.lens = lens;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
