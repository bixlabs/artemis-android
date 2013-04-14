package com.chemicalwedding.artemis.database;

/**
 * Data place-holder for lens information.
 * 
 * Represents a row in the SQLite database table "lenses"
 * 
 */
public class Lens {
	private Integer pk;
	private String format;
	private String zoom, zoomRange;
	private String lensMake;
	private String lensCode;
	private String lensSet;
	private float FL;
	private float squeeze;
	private boolean isCustomLens;

	public Lens() {
		
	}
	
	public Lens(Lens l) {
		format = l.format;
		lensMake = l.lensMake;
		lensCode = l.lensCode;
		lensSet = l.lensSet;
		FL = l.FL;
		zoom = l.zoom;
		zoomRange = l.zoomRange;
		squeeze = l.squeeze;
		isCustomLens = l.isCustomLens;
	}

	public Integer getPk() {
		return pk;
	}

	public void setPk(Integer pk) {
		this.pk = pk;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getZoom() {
		return zoom;
	}

	public void setZoom(String zoom) {
		this.zoom = zoom;
	}

	public String getZoomRange() {
		return zoomRange;
	}

	public void setZoomRange(String zoomRange) {
		this.zoomRange = zoomRange;
	}

	public String getLensMake() {
		return lensMake;
	}

	public void setLensMake(String lensMake) {
		this.lensMake = lensMake;
	}

	public String getLensCode() {
		return lensCode;
	}

	public void setLensCode(String lensCode) {
		this.lensCode = lensCode;
	}

	public String getLensSet() {
		return lensSet;
	}

	public void setLensSet(String lensSet) {
		this.lensSet = lensSet;
	}

	public float getFL() {
		return FL;
	}

	public void setFL(float fL) {
		FL = fL;
	}

	public float getSqueeze() {
		return squeeze;
	}

	public void setSqueeze(float squeeze) {
		this.squeeze = squeeze;
	}

	public boolean isCustomLens() {
		return isCustomLens;
	}

	public void setCustomLens(boolean isCustomLens) {
		this.isCustomLens = isCustomLens;
	}

	@Override
	public String toString() {
		return "Lens [pk=" + pk + ", format=" + format + ", zoom=" + zoom
				+ ", zoomRange=" + zoomRange + ", lensMake=" + lensMake
				+ ", lensCode=" + lensCode + ", lensSet=" + lensSet + ", FL="
				+ FL + ", squeeze=" + squeeze + ", isCustomLens="
				+ isCustomLens + "]";
	}
}
