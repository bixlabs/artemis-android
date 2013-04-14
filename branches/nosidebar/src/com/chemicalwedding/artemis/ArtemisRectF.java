package com.chemicalwedding.artemis;

import android.graphics.RectF;

public class ArtemisRectF extends RectF {

	private int color;
	private boolean solid = false;
	private String focalLengthString;
	
	public ArtemisRectF(String focalLength, float left, float top, float right, float bottom) {
		super(left, top, right, bottom);
		this.focalLengthString = focalLength;
	}
	
	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	public void setSolid(boolean solid) {
		this.solid = solid;
	}

	public boolean isSolid() {
		return solid;
	}	
	
	public String getFocalLengthString() {
		return focalLengthString;
	}
	
	@Override
	public String toString() {
		return "(width: "+width()+" height: "+height()+" l: "+left+" r: "+right+" t: "+top+" b: "+bottom+" COLOR: "+color+")";
	}
}
