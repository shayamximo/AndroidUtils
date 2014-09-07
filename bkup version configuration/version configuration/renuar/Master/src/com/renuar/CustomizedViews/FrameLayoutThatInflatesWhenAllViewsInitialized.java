package com.renuar.CustomizedViews;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class FrameLayoutThatInflatesWhenAllViewsInitialized extends FrameLayout {

	private int numberOfViews;
	private ArrayList<View> viewList;
	private int numberOfElementsEntered = 0;

	public FrameLayoutThatInflatesWhenAllViewsInitialized(Context context,
			AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	public FrameLayoutThatInflatesWhenAllViewsInitialized(Context context,
			AttributeSet attrs) {
		super(context, attrs);

	}

	public FrameLayoutThatInflatesWhenAllViewsInitialized(Context context) {
		super(context);

	}

	private void addAllViews() {
		for (View view : viewList) {
			addView(view);
		}
	}

	public synchronized void addViewToSmartFrame(View child, int index) {

		viewList.set(index, child);
		numberOfElementsEntered++;
		if (numberOfElementsEntered == numberOfViews)
			addAllViews();
	}

	public int getNumberOfViews() {
		return numberOfViews;
	}

	public void setNumberOfViews(int numberOfViews) {
		viewList = new ArrayList<View>(numberOfViews);
		viewList.addAll(Arrays.asList(new View[numberOfViews]));
		this.numberOfViews = numberOfViews;
	}

}
