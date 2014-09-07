package com.soa.bhc.CustomizedViews;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.soa.bhc.GlobalConstants;
import com.soa.bhc.json.stores.InitInfoStore;

public class LocalizedLinearLayout extends LinearLayout {

	private static final int GRAVITY_NOT_DEFINED_IN_VIEW = -1879;

	public LocalizedLinearLayout(Context context) {
		super(context);
	}

	public LocalizedLinearLayout(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	public LocalizedLinearLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	private void reverseLayouts() {
		ArrayList<View> viewList = new ArrayList<View>();
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			viewList.add(view);
		}
		removeAllViews();
		for (int i = (viewList.size() - 1); i >= 0; i--) {
			View view = viewList.get(i);
			addView(view);
		}
	}

	private boolean shouldreverse() {
		return ((getOrientation() == LinearLayout.HORIZONTAL));
	}

	private void reverseMarginsView(View view) {

		ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) view
				.getLayoutParams();

		int tempRight = mlp.rightMargin;
		mlp.rightMargin = mlp.leftMargin;
		mlp.leftMargin = tempRight;
		view.setLayoutParams(mlp);

	}

	private void reversePaddingsView(View view) {
		view.setPadding(view.getPaddingRight(), view.getPaddingTop(),
				view.getPaddingLeft(), view.getPaddingBottom());
	}

	private int doesContainRequestedGravity(TextView textView,
			int requestedGravity) {
		int gravity = textView.getGravity();

		if ((gravity == (Gravity.TOP | requestedGravity)))
			return Gravity.TOP;

		if ((gravity == (Gravity.CENTER_HORIZONTAL | requestedGravity)))
			return Gravity.CENTER_HORIZONTAL;
		if ((gravity == (Gravity.CENTER_VERTICAL | requestedGravity)))
			return Gravity.CENTER_VERTICAL;
		if ((gravity == (Gravity.CENTER | requestedGravity)))
			return Gravity.CENTER;
		if (gravity == requestedGravity)
			return Gravity.NO_GRAVITY;
		return GRAVITY_NOT_DEFINED_IN_VIEW;

	}

	private void reverseTextViewGravity(TextView textView) {
		int resultGravity;
		if ((resultGravity = doesContainRequestedGravity(textView,
				Gravity.RIGHT)) != GRAVITY_NOT_DEFINED_IN_VIEW) {
			textView.setGravity(Gravity.LEFT | resultGravity);
			return;
		}

		if ((resultGravity = doesContainRequestedGravity(textView, Gravity.LEFT)) != GRAVITY_NOT_DEFINED_IN_VIEW) {
			textView.setGravity(Gravity.RIGHT | resultGravity);

		}
	}

	private void reverseLayoutGravity(View view) {
		LinearLayout.LayoutParams params = (LayoutParams) view
				.getLayoutParams();

		if (params.gravity == Gravity.LEFT) {
			params.gravity = Gravity.RIGHT;
			view.setLayoutParams(params);
			return;
		}

		if (params.gravity == (Gravity.LEFT | Gravity.CENTER_VERTICAL)) {
			params.gravity = (Gravity.RIGHT | Gravity.CENTER_VERTICAL);
			view.setLayoutParams(params);
			return;
		}

		if (params.gravity == Gravity.RIGHT) {
			params.gravity = Gravity.LEFT;
			view.setLayoutParams(params);

		}
	}

	private void localizeLayout() {

		// reverse layouts
		if (shouldreverse())
			reverseLayouts();

		// customize text views
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			if (view instanceof TextView) {
				TextView textView = (TextView) view;
				reverseTextViewGravity(textView);
			}
			reverseLayoutGravity(view);
			reverseMarginsView(view);
			reversePaddingsView(view);
		}
	}

	private boolean shouldLocalize() {
		return InitInfoStore.getInstance().getLanguage()
				.equals(GlobalConstants.HEBREW);
	}

	@Override
	protected void onFinishInflate() {

		super.onFinishInflate();
		if (shouldLocalize())
			localizeLayout();

	}

}
