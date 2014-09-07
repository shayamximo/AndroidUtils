package com.soa.bhc.actionbar;

import com.soa.bhc.GlobalConstants;
import com.soa.bhc.pagefragments.FragmentPageBase;

public class ActionBarControllerFactory {

	public static ActionBarContorller getActionBarController(
			FragmentPageBase fragmentPageBase) {
		if (GlobalConstants.IS_ACTION_BAR_APP_CONFIGURED_FROM_MANIFEST)
			return new ActionBarContorllerWithSystemActionBar(fragmentPageBase);
		else
			return new ActionBarContorllerNoSystemActionBar(fragmentPageBase);
	}
}
