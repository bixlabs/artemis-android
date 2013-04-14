package com.chemicalwedding.artemis;

import android.graphics.RectF;

public class ArtemisRectF extends RectF {

	private int color;
	private boolean solid = false;
	
	public ArtemisRectF(float left, float top, float right, float bottom) {
		super(left, top, right, bottom);
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
	
	@Override
	public String toString() {
		return "(width: "+width()+" height: "+height()+" l: "+left+" r: "+right+" t: "+top+" b: "+bottom+" COLOR: "+color+")";
	}
}
