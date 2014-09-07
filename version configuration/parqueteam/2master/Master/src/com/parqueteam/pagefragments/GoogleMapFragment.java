package com.parqueteam.pagefragments;

import android.os.Bundle;

public class GoogleMapFragment extends MximoWebViewFragment {

	public static final String URL_TO_PASS = "url_to_pass";
	private String urlOfPage = null;

	@Override
	protected String getUrlOfPage() {
		return urlOfPage;
	}

	@Override
	protected String getTitle() {

		return "Directions";
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		urlOfPage = getArguments().getString(URL_TO_PASS);
		super.onActivityCreated(savedInstanceState);
	}

}
