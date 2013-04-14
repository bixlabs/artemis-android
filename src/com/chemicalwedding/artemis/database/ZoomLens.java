package com.chemicalwedding.artemis.database;

import java.text.NumberFormat;

/**
 * Data place-holder for lens information.
 * 
 * Represents a row in the SQLite database table "lenses"
 * 
 */
public class ZoomLens {
	private Integer pk;
	private String name;
	private float minFL, maxFL;

	public ZoomLens() {

	}

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

	public float getMinFL() {
		return minFL;
	}

	public void setMinFL(float minFL) {
		this.minFL = minFL;
	}

	public float getMaxFL() {
		return maxFL;
	}

	public void setMaxFL(float maxFL) {
		this.maxFL = maxFL;
	}

	@Override
	public String toString() {
		return name == null ? "" : name
				+ (minFL > 0 ? " (" + nameFormat.format(minFL) + "-"
						+ nameFormat.format(maxFL) + " mm)" : "");
	}

	private static NumberFormat nameFormat;

	static {
		nameFormat = NumberFormat.getInstance();
		nameFormat.setMaximumFractionDigits(1);
	}
}
