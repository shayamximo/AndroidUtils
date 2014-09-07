package com.soa.bhc.actionbar;

import android.view.View;

import com.soa.bhc.pagefragments.FragmentPageBase;

public class ActionBarContorllerNoSystemActionBar extends ActionBarContorller {

	protected ActionBarContorllerNoSystemActionBar(
			FragmentPageBase fragmentPageBase) {
		super(fragmentPageBase);
		// This is added, mainly for parquetim.

	}

	@Override
	public View findViewById(int id) {
		return fragmentPageBase.getView().findViewById(id);
	}

	@Override
	protected void initBeforeRender() {
		// Do nothing
	}

}
