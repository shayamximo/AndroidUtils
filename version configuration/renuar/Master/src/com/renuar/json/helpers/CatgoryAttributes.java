package com.renuar.json.helpers;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;

import com.renuar.App;
import com.renuar.json.Params;
import com.renuar.utils.UtilityFunctions;

public class CatgoryAttributes {

	public enum Field {

		ALPHA, COLOR, ALIGNMENT, FONT, FONT_SIZE, FRAME, TEXT_ALIGNMENT
	}

	private int paddingLeft = 0;
	private int textAlignement = Gravity.CENTER;
	private double alpha = 0.5;
	private String color = "#888888";
	private boolean isHorizontal = false;
	private Typeface font = Typeface.DEFAULT;
	private int cellHeight;
	private int cellWidth;
	private int fontsize;
	private int numberofrows;

	private int numberofcolumns = 1;
	private int numberOfCatagories;
	Frame frame;

	public CatgoryAttributes(List<Params> categoriesParams,
			int numberOfCatagories, Display display, Context context) {
		this.numberOfCatagories = numberOfCatagories;
		numberofrows = (numberOfCatagories > 3) ? 2 : 1;
		// Point size = new Point();
		// display.getSize(size);

		constructItemByParamsList(categoriesParams, context);
		customizeAfterAllFieldsAssigned();
	}
	
	public CatgoryAttributes(List<Params> categoriesParams,Context context)
	{
		constructItemByParamsList(categoriesParams, context);
	}

	private void customizeAfterAllFieldsAssigned() {
		if (isHorizontal) {
			numberofcolumns = numberOfCatagories / numberofrows;
		}

		else
			numberofrows = numberOfCatagories;

		cellHeight = (frame.getHeight() / numberofrows);
		cellWidth = (frame.getWidth() / numberofcolumns);
	}

	private void constructItemByParamsList(List<Params> paramsList,
			Context context) {
		for (Params params : paramsList) {
			String fieldString = params.getName();
			String value = params.getValue();
			Field field = null;
			try {
				// a field may be added which we don't recognize or isn't
				// relevant, in
				// that case this exception will be thrown, and we just continue
				// to the next fiel
				field = Field.valueOf(fieldString.toUpperCase(Locale.ENGLISH));
			} catch (IllegalArgumentException e) {
				continue;
			}

			switch (field) {
			case ALPHA:
				alpha = Double.parseDouble(value);
				break;
			case COLOR:
				// this little thing is because the configuration in some apps
				// is like this
				color = "#" + value.replace("0x", "");
				break;
			case ALIGNMENT:
				if (value.equals("horizontal"))
					isHorizontal = true;
				else
					isHorizontal = false;
				break;
			case FONT:
				// in case font doesn't exist in assets
				try {
					font = Typeface.createFromAsset(App.getApp().getAssets(),
							value + ".ttf");
				} catch (Exception e) {
					font = Typeface.DEFAULT;
				}

				break;
			case FRAME:
				frame = new Frame(value);
				break;

			case FONT_SIZE:
				fontsize = Integer.parseInt(value);
				if (UtilityFunctions.isTablet(context))
					fontsize = (int) (fontsize *1.5);
				break;

			case TEXT_ALIGNMENT:
				String alignment = value;
				if (alignment.equals("left")) {
					textAlignement = Gravity.LEFT | Gravity.CENTER_VERTICAL;
					int dpPadding = (int) TypedValue.applyDimension(
							TypedValue.COMPLEX_UNIT_DIP, 1, context
									.getResources().getDisplayMetrics());

					paddingLeft = 8 * dpPadding;
				}

				break;

			}

		}
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean isHorizontal() {
		return isHorizontal;
	}

	public void setHorizontal(boolean isHorizontal) {
		this.isHorizontal = isHorizontal;
	}

	public Typeface getFont() {
		return font;
	}

	public void setFont(Typeface font) {
		this.font = font;
	}

	public int getFontsize() {
		return fontsize;
	}

	public int getTextAlignement() {
		return textAlignement;
	}

	public void setFontsize(int fontsize) {
		this.fontsize = fontsize;
	}

	public int getCellHeight() {
		return cellHeight;
	}

	public void setCellHeight(int cellHeight) {
		this.cellHeight = cellHeight;
	}

	public int getCellWidth() {
		return cellWidth;
	}

	public void setCellWidth(int cellWidth) {
		this.cellWidth = cellWidth;
	}

	public int getNumberofcolumns() {
		return numberofcolumns;
	}

	public void setNumberofcolumns(int numberofcolumns) {
		this.numberofcolumns = numberofcolumns;
	}

	public int getNumberofrows() {
		return numberofrows;
	}

	public Frame getFrame() {
		return frame;
	}

	public int getMarginLeft() {
		return paddingLeft;
	}
}
