package com.renuar.CustomizedViews;

import android.content.Context;
import android.support.v4.app.FragmentTabHost;
import android.util.AttributeSet;

public class ReclickableTabHost extends FragmentTabHost {

	private HomeTabClickedListener homeTabClickedListener;

	public void setOnHomeTabClickedListener(
			HomeTabClickedListener homeTabClickedListener) {
		this.homeTabClickedListener = homeTabClickedListener;
	}

	public interface HomeTabClickedListener {
		public void onHomeTabClicked();
	}

	public ReclickableTabHost(Context context) {
		super(context);
	}

	public ReclickableTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setCurrentTab(int index) {
		if ((0 == index) && (index == getCurrentTab())) {
			if (homeTabClickedListener != null)
				homeTabClickedListener.onHomeTabClicked();
		} else {
			super.setCurrentTab(index);
		}
	}
}