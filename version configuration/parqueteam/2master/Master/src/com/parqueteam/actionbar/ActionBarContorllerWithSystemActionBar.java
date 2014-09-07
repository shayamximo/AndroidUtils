package com.parqueteam.actionbar;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parqueteam.R;
import com.parqueteam.json.helpers.NavBarParams;
import com.parqueteam.json.stores.InitInfoStore;
import com.parqueteam.pagefragments.FragmentPageBase;

public class ActionBarContorllerWithSystemActionBar extends ActionBarContorller {

	private ViewGroup viewOfActionBar;

	protected ActionBarContorllerWithSystemActionBar(
			FragmentPageBase fragmentPageBase) {
		super(fragmentPageBase);
	}

	@Override
	public View findViewById(int id) {
		return viewOfActionBar.findViewById(id);
	}

	@Override
	protected void initBeforeRender() {
		fragmentPageBase.getView().findViewById(R.id.top_bar_top_layout)
				.setVisibility(View.GONE);

		ActionBar actionBar = fragmentPageBase.getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		viewOfActionBar = (ViewGroup) LayoutInflater.from(
				fragmentPageBase.getActivity()).inflate(R.layout.top_bar, null);

		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		fragmentPageBase.getActivity().getActionBar()
				.setCustomView(viewOfActionBar);
		fragmentPageBase.configureBackgroundColor(actionBar, null);

	}

}
