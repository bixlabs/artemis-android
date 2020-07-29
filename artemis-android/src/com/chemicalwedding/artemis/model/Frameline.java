package com.chemicalwedding.artemis.model;

public class Frameline {
    private int id;
    private FramelineRate rate;

    private int scale;

    private double shading;

    private int verticalOffset;
    private int horizontalOffset;

    private int framelineType;


    private int color;
    private boolean isDotted;
    private int lineWidth;

    private int centerMarkerType;
    private int centerMarkerLineWidth;
    private boolean isApplied;

    public Frameline() {
    }

    public Frameline(int id, FramelineRate rate, int scale, double shading, int verticalOffset, int horizontalOffset, int framelineType, int color, boolean isDotted, int lineWidth, int centerMarkerType, int centerMarkerLineWidth, boolean isApplied) {
        this.id = id;
        this.rate = rate;
        this.scale = scale;
        this.shading = shading;
        this.verticalOffset = verticalOffset;
        this.horizontalOffset = horizontalOffset;
        this.framelineType = framelineType;
        this.color = color;
        this.isDotted = isDotted;
        this.lineWidth = lineWidth;
        this.centerMarkerType = centerMarkerType;
        this.centerMarkerLineWidth = centerMarkerLineWidth;
        this.isApplied = isApplied;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FramelineRate getRate() {
        return rate;
    }

    public void setRate(FramelineRate rate) {
        this.rate = rate;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        if(scale >= 1 && scale <= 100) {
            this.scale = scale;
        }
    }

    public double getShading() {
        return shading;
    }

    public void setShading(double shading) {
        this.shading = shading;
    }

    public int getVerticalOffset() {
        return verticalOffset;
    }

    public void setVerticalOffset(int verticalOffset) {
        if(verticalOffset < 100 && verticalOffset >= -100) {
            this.verticalOffset = verticalOffset;
        }
    }

    public int getHorizontalOffset() {
        return horizontalOffset;
    }

    public void setHorizontalOffset(int horizontalOffset) {
        this.horizontalOffset = horizontalOffset;
    }

    public int getFramelineType() {
        return framelineType;
    }

    public void setFramelineType(int framelineType) {
        this.framelineType = framelineType;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isDotted() {
        return isDotted;
    }

    public void setDotted(boolean dotted) {
        isDotted = dotted;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public int getCenterMarkerType() {
        return centerMarkerType;
    }

    public void setCenterMarkerType(int centerMarkerType) {
        this.centerMarkerType = centerMarkerType;
    }

    public int getCenterMarkerLineWidth() {
        return centerMarkerLineWidth;
    }

    public void setCenterMarkerLineWidth(int centerMarkerLineWidth) {
        this.centerMarkerLineWidth = centerMarkerLineWidth;
    }

    public boolean isApplied() {
        return isApplied;
    }

    public void setApplied(boolean applied) {
        isApplied = applied;
    }

    public String getTitle() {
        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(String.format("%.2f", rate.getRate()));
        stringBuilder.append(" SCALING: ");
        stringBuilder.append(String.format("%d", scale));
        stringBuilder.append(" SHADING: ");
        stringBuilder.append(this.shading * 25);
        stringBuilder.append("%");

        return stringBuilder.toString();
    }
}
