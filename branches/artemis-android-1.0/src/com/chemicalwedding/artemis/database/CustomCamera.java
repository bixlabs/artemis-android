package com.chemicalwedding.artemis.database;

/**
 * Data place-holder for custom camera information. 
 * 
 * Represents a row in the SQLite database table "custom_cameras"
 * 
 */
public class CustomCamera {
	private Integer rowid;
	private String label;
	private float FL;
	private float horiz;
	private float vertical;
	private float distance;
	private float width, height;
	
	public void setRowid(Integer rowid) {
		this.rowid = rowid;
	}
	public Integer getRowid() {
		return rowid;
	}
	public float getHoriz() {
		return horiz;
	}
	public void setHoriz(float horiz) {
		this.horiz = horiz;
	}
	public float getVertical() {
		return vertical;
	}
	public void setVertical(float vertical) {
		this.vertical = vertical;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public float getFL() {
		return FL;
	}
	public void setFL(float fL) {
		FL = fL;
	}
	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
	public float getWidth() {
		return width;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	public float getHeight() {
		return height;
	}
	public void setHeight(float height) {
		this.height = height;
	}
	@Override
	public String toString() {
		return label;
	}
}
