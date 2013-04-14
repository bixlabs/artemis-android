package com.chemicalwedding.artemis.database;

/**
 * Data place-holder for lens information. 
 * 
 * Represents a row in the SQLite database table "lenses"
 * 
 */
public class Lens {
	private Integer rowid;
	private String format;
	private String scale;
	private String lensMake;
	private String lensCode;
	private String lensSet;
	private float FL;
	private float FLReal;
	private String absorbT;
	private String minF;
	private String minT;
	private String maxT;
	private String mineFocus;
	private String squeeze;
	
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
	public String getScale() {
		return scale;
	}
	public void setScale(String scale) {
		this.scale = scale;
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
	public float getFLReal() {
		return FLReal;
	}
	public void setFLReal(float fLReal) {
		FLReal = fLReal;
	}
	public String getAbsorbT() {
		return absorbT;
	}
	public void setAbsorbT(String absorbT) {
		this.absorbT = absorbT;
	}
	public String getMinF() {
		return minF;
	}
	public void setMinF(String minF) {
		this.minF = minF;
	}
	public String getMinT() {
		return minT;
	}
	public void setMinT(String minT) {
		this.minT = minT;
	}
	public String getMaxT() {
		return maxT;
	}
	public void setMaxT(String maxT) {
		this.maxT = maxT;
	}
	public String getMineFocus() {
		return mineFocus;
	}
	public void setMineFocus(String mineFocus) {
		this.mineFocus = mineFocus;
	}
	public String getSqueeze() {
		return squeeze;
	}
	public void setSqueeze(String squeeze) {
		this.squeeze = squeeze;
	}
}
