package com.renuar.CustomizedViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.TextView;

import com.renuar.json.stores.InitInfoStore;

public class CustomFontTextView extends TextView {

	Pair<String, String> fonts;

	public CustomFontTextView(Context context) {

		super(context);
		fonts = InitInfoStore.getInstance().getRegularAndBoldFonts();

	}

	public CustomFontTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		fonts = InitInfoStore.getInstance().getRegularAndBoldFonts();
	}

	public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		fonts = InitInfoStore.getInstance().getRegularAndBoldFonts();
	}

	public void setTypefaceOfCustomFont(Typeface tf) {

		if (fonts != null)
			configureTypeFace(tf);
		else
			setTypeface(tf);
	}

	@Override
	protected void onFinishInflate() {

		super.onFinishInflate();
		if (fonts != null)
			configureTypeFace(null);

	}

	private void configureTypeFace(Typeface tf) {
		Typeface currentTypeFace = getTypeface();
		Typeface newTypeFace = null;

		if ((((tf != null) && tf.getStyle() == Typeface.BOLD))
				|| (currentTypeFace != null) && (currentTypeFace.isBold())) {

			newTypeFace = Typeface.createFromAsset(getContext().getAssets(),
					fonts.second);
			setTypeface(Typeface.DEFAULT);
		}

		else
			newTypeFace = Typeface.createFromAsset(getContext().getAssets(),
					fonts.first);
		super.setTypeface(newTypeFace);
	}
}
