package com.mximo.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomFontTextView extends TextView {

	public CustomFontTextView(Context context) {
		super(context);
	}

	public CustomFontTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setTypefaceOfCustomFont(Typeface tf) {

		configureTypeFace(tf);
	}

	@Override
	protected void onFinishInflate() {

		super.onFinishInflate();
		configureTypeFace(null);

	}

	private void configureTypeFace(Typeface tf) {
		Typeface currentTypeFace = getTypeface();
		Typeface newTypeFace = null;

		if ((((tf != null) && tf.getStyle() == Typeface.BOLD))
				|| (currentTypeFace != null) && (currentTypeFace.isBold())) {

			newTypeFace = Typeface.createFromAsset(getContext().getAssets(),
					"NarkisBlock-Bold.ttf");
			setTypeface(Typeface.DEFAULT);
		}

		else
			newTypeFace = Typeface.createFromAsset(getContext().getAssets(),
					"NarkisBlock-Regular.ttf");
		super.setTypeface(newTypeFace);
	}
}
