package com.renuar.CustomizedViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.renuar.GlobalConstants;
import com.renuar.json.stores.InitInfoStore;

public class LocalizedRelativeLayout extends RelativeLayout {

	public LocalizedRelativeLayout(Context context) {
		super(context);
	}

	public LocalizedRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LocalizedRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {

		super.onFinishInflate();
		if (shouldLocalize())
			localizeLayout();
	}

	private boolean shouldLocalize() {
		return InitInfoStore.getInstance().getLanguage()
				.equals(GlobalConstants.HEBREW);
	}

	private boolean doesRullExist(RelativeLayout.LayoutParams lpRl, int rule) {

		int[] rules = lpRl.getRules();
		return (rules.length > (rule) ? (rules[rule] == -1) : false);

	}

	private int getIdOfRule(RelativeLayout.LayoutParams lpRl, int rule) {

		int[] rules = lpRl.getRules();
		return (rules.length > (rule) ? (rules[rule]) : 0);

	}

	private boolean doesContainRequestedGravity(TextView textView,
			int requestedGravity) {
		int gravity = textView.getGravity();

		return ((gravity == (requestedGravity | Gravity.TOP)) || (gravity == (Gravity.CENTER | requestedGravity)));
	}

	private void reverseParentAlignment(View view) {
		final RelativeLayout.LayoutParams lpRl = (RelativeLayout.LayoutParams) view
				.getLayoutParams();

		if (doesRullExist(lpRl, RelativeLayout.ALIGN_PARENT_RIGHT)) {

			lpRl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			lpRl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
			view.setLayoutParams(lpRl);
			return;

		}

		if (doesRullExist(lpRl, RelativeLayout.ALIGN_PARENT_LEFT)) {

			lpRl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			lpRl.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
			view.setLayoutParams(lpRl);
			return;

		}
	}

	private void reverseAlignment(View view) {
		final RelativeLayout.LayoutParams lpRl = (RelativeLayout.LayoutParams) view
				.getLayoutParams();
		int idLeft = getIdOfRule(lpRl, RelativeLayout.LEFT_OF);
		if (idLeft != 0) {
			lpRl.addRule(RelativeLayout.RIGHT_OF, idLeft);
			lpRl.addRule(RelativeLayout.LEFT_OF, 0);
			return;
		}

		int idRight = getIdOfRule(lpRl, RelativeLayout.RIGHT_OF);
		if (idRight != 0) {
			lpRl.addRule(RelativeLayout.LEFT_OF, idRight);
			lpRl.addRule(RelativeLayout.RIGHT_OF, 0);
		}
	}

	private void reverseTextViewGravity(TextView textView) {

		if (doesContainRequestedGravity(textView, Gravity.RIGHT)) {
			textView.setGravity(Gravity.LEFT);
			return;
		}

		if (doesContainRequestedGravity(textView, Gravity.LEFT)) {
			textView.setGravity(Gravity.RIGHT);
		}
	}

	private void reverseTextViewMargins(TextView textView) {

		ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) textView
				.getLayoutParams();

		int tempRight = mlp.rightMargin;
		mlp.rightMargin = mlp.leftMargin;
		mlp.leftMargin = tempRight;

	}

	private void localizeLayout() {

		for (int i = 0; i < getChildCount(); i++) {
			final View view = getChildAt(i);

			reverseParentAlignment(view);
			reverseAlignment(view);
			if (view instanceof TextView) {
				TextView textView = (TextView) view;

				reverseTextViewGravity(textView);
				reverseTextViewMargins(textView);

			}

		}

	}

}
