package com.parqueteam;

public class GlobalConstants {

	public static final String PAGE_FRAGMENTS_PACKAGE = App.getApp()
			.getPackageName() + ".pagefragments.";

	public static final String HEBREW = "iw";
	public static final String ENGLISH = "en";

	public final static String SPREE = "https://beta.mximo.com";
	public final static String STAGE = "https://stage.mximo.com";
	public static String PREFIX = SPREE;

	public final static String EMPTY_STRING = "";

	// Facebook
	public static final String FB_APP_ID = "224150304268173";
	public static final String[] FB_PERMISSIONS = new String[] {
			"manage_pages", "publish_stream" };

	public static final String TO_FACEBOOK_SHARE_PIC_URL = "TO_FACEBOOK_SHARE_PIC_URL";
	public static final String TO_FACEBOOK_SHARE_MSG = "TO_FACEBOOK_SHARE_MSG";

	public static final String IMAGE_THUMB_SIZE = "&size=phone_thumb";

	public static String ISRAEL_COUNTRY_ID = "97";

	// very explicit name given to differantiate between sliding menu given
	// from server.
	public static final boolean IS_ACTION_BAR_APP_CONFIGURED_FROM_MANIFEST;

	public static final String QA_FILE_PATH = "/sdcard/mximo_qa";
	
	static {
		int theme = App.getApp().getApplicationInfo().theme;
		IS_ACTION_BAR_APP_CONFIGURED_FROM_MANIFEST = ((theme == R.style.WithActionBar) ? true
				: false);
	}

}
