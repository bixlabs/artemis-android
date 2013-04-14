package com.chemicalwedding.artemis.database;

import com.chemicalwedding.artemis.ArtemisMath;

/**
 * Data place-holder for camera information.
 * 
 * Represents a row in the SQLite database table "cameras"
 * 
 */
public class Camera {
	private Integer rowid;
	private String format;
	private String sensor;
	private String ratio;
	private Float horiz;
	private Float vertical;
	private Float sqz;
	private String lenses;
	private String formatorder;
	private String medium;

	public Camera() {

	}

	public Camera(CustomCamera selectedCustomCamera) {
		this.setHoriz(selectedCustomCamera.getSensorwidth());
		this.setVertical(selectedCustomCamera.getSensorheight());
		this.setRatio("");
		this.setLenses("35mm");
		this.setSqz(selectedCustomCamera.getSqueeze());
		this.setRowid(-1); // to signify this is a custom
	}

	public void setRowid(Integer rowid) {
		this.rowid = rowid;
	}

	public Integer getRowid() {
		return rowid;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getSensor() {
		return sensor;
	}

	public void setSensor(String sensor) {
		this.sensor = sensor;
	}

	public String getRatio() {
		return ratio;
	}

	public void setRatio(String ratio) {
		this.ratio = ratio;
	}

	public Float getHoriz() {
		return horiz;
	}

	public void setHoriz(Float horiz) {
		this.horiz = horiz;
	}

	public Float getVertical() {
		return vertical;
	}

	public void setVertical(Float vertical) {
		this.vertical = vertical;
	}

	public Float getSqz() {
		return sqz;
	}

	public void setSqz(Float sqz) {
		this.sqz = sqz;
	}

	public String getLenses() {
		return lenses;
	}

	public void setLenses(String lenses) {
		this.lenses = lenses;
	}

	public String getFormatorder() {
		return formatorder;
	}

	public void setFormatorder(String formatorder) {
		this.formatorder = formatorder;
	}

	public String getMedium() {
		return medium;
	}

	public void setMedium(String medium) {
		this.medium = medium;
	}

	@Override
	public String toString() {
		return format;
	}
}
