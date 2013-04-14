package com.chemicalwedding.artemis.database;

/**
 * Data place-holder for custom camera information.
 * 
 * Represents a row in the SQLite database table "custom_cameras"
 * 
 */
public class CustomCamera {
	private Integer pk;
	private String name;
	private float sensorwidth, sensorheight;
	private float squeeze;

	public Integer getPk() {
		return pk;
	}

	public void setPk(Integer pk) {
		this.pk = pk;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getSensorwidth() {
		return sensorwidth;
	}

	public void setSensorwidth(float sensorwidth) {
		this.sensorwidth = sensorwidth;
	}

	public float getSensorheight() {
		return sensorheight;
	}

	public void setSensorheight(float sensorheight) {
		this.sensorheight = sensorheight;
	}

	public float getSqueeze() {
		return squeeze;
	}

	public void setSqueeze(float squeeze) {
		this.squeeze = squeeze;
	}

	@Override
	public String toString() {
		return name;
	}
}
