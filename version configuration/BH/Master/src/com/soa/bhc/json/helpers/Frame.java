package com.soa.bhc.json.helpers;

import com.soa.bhc.utils.UtilityFunctions;

public class Frame {

	private int x;
	private int y;
	private int width;
	private int height;
	private final String SPACE = " ";

	public Frame(String frame) {
		x = UtilityFunctions
				.NormalizeWidth(Integer.parseInt(frame.split(SPACE)[0]));
		y = UtilityFunctions.NormalizeHeight(Integer.parseInt(frame
				.split(SPACE)[1]));
		width = UtilityFunctions.NormalizeWidth(Integer.parseInt(frame
				.split(SPACE)[2]));
		height = UtilityFunctions.NormalizeHeight(Integer.parseInt(frame
				.split(SPACE)[3]));
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
