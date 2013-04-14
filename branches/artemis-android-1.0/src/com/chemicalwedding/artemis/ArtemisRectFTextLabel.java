package com.chemicalwedding.artemis;

public class ArtemisRectFTextLabel {

	private String labelText;
	private int textColor;
	float positionX, positionY;

	public ArtemisRectFTextLabel(String labelText, int color, float positionX, float positionY) {
		this.labelText = labelText;
		this.textColor = color;
		this.positionX = positionX;
		this.positionY = positionY;
	}
	
	public String getLabelText() {
		return labelText;
	}

	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public float getPositionX() {
		return positionX;
	}

	public void setPositionX(float positionX) {
		this.positionX = positionX;
	}

	public float getPositionY() {
		return positionY;
	}

	public void setPositionY(float positionY) {
		this.positionY = positionY;
	}
}
