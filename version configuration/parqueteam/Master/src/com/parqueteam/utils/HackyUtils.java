package com.parqueteam.utils;

import android.app.ActionBar;

/**
 * 
 * @author Shaya The purpose of this class, are 1. things that are not
 *         configured server side yet, and this is the only solution for now
 * 
 *         2. UI issues that haven't been resolved(i.e application with and
 *         without action bar.)
 * 
 */
public class HackyUtils {

	public static void hideActionBarNoAnimation(ActionBar actionBar) {
		try {
			actionBar
					.getClass()
					.getDeclaredMethod("setShowHideAnimationEnabled",
							boolean.class).invoke(actionBar, false);
		} catch (Exception exception) {
			// Too bad, the animation will be run ;(
		}
		actionBar.hide();
	}

	public static void showActionBarNoAnimation(ActionBar actionBar) {
		try {
			actionBar
					.getClass()
					.getDeclaredMethod("setShowHideAnimationEnabled",
							boolean.class).invoke(actionBar, false);
		} catch (Exception exception) {
			// Too bad, the animation will be run ;(
		}
		actionBar.show();
	}

	public static boolean shouldLinkify() {
		return ConfigurationFile.getInstance().getAppAddress().equals(117);
	}

	public static boolean shouldMakeActionBarWhiteInHomeFragment() {
		Integer appAddress = ConfigurationFile.getInstance().getAppAddress();
		return (appAddress.equals(139) || appAddress.equals(172)
				|| appAddress.equals(171) || appAddress.equals(169));
	}

}
