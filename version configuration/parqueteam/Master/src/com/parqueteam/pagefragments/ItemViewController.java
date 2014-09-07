package com.parqueteam.pagefragments;

import android.os.Bundle;
import android.widget.TextView;

import com.parqueteam.R;

/**
 * 
 * dumb class, it's needed because from server side, can get this name.
 * 
 */
public class ItemViewController extends CatalogViewController {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		TextView mainButton = (TextView) viewOfFragment
				.findViewById(R.id.order_now_button);
		mainButton.setText(getResources().getString(R.string.shop_now));
		String action = getValueOfParamFromName("action");
		if ((action!=null)&&action.equals("join"))
		{
			String displayString = getValueOfParamFromName("page_action_label");
			mainButton.setText(displayString);
		}
			

	}

	@Override
	protected boolean shouldShowMultiView() {
		return false;
	}
}
