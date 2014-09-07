package com.parqueteam.pagefragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.parqueteam.R;

public abstract class MximoWebViewFragment extends FragmentPageBase {

	private WebView browser;
	public final static String SHOW_TOOLBAR_KEY = "show_toolbar_key";

	@Override
	protected int getLayoutID() {
		return R.layout.mximo_web_view_fragment;
	}

	@Override
	protected void initializeActionBar() {
		actionBarContorller.textViewMainTitle.setText(getTitle());
	}

	private void initWebPage() {

		String url = getUrlOfPage();
		browser = (WebView) viewOfFragment.findViewById(R.id.mximo_webview);
		browser.setWebViewClient(new MximoWebViewBrowser());
		browser.getSettings().setLoadsImagesAutomatically(true);
		browser.getSettings().setJavaScriptEnabled(true);
		
		if (getTitle().equals("MAGAZINE"))
		{
			WebSettings webSettings = browser.getSettings();
			webSettings.setLoadWithOverviewMode(true);
			webSettings.setUseWideViewPort(true);
		}
		
		browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		browser.loadUrl(url);

		RelativeLayout progressLayout = (RelativeLayout) viewOfFragment
				.findViewById(R.id.loadingPanel);
		progressLayout.setVisibility(View.VISIBLE);
		browser.setVisibility(View.GONE);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		initWebPage();
		initToolBar();

	}

	private boolean showToolBar() {
		Bundle args = getArguments();
		Boolean showArgs = args.getBoolean(SHOW_TOOLBAR_KEY);
		if (showArgs == true)
			return true;

		// another option to show toolbar, if defined in page in initinfostore
		String showToolBarString = getValueOfParamFromName("show_toolbar");
		Boolean showToolBarBoolean = null;
		if (showToolBarString != null)
			showToolBarBoolean = Boolean.parseBoolean(showToolBarString);
		else
			showToolBarBoolean = false;
		return showToolBarBoolean;

	}

	private void initToolBar() {
		View layoutOfWebContollers = viewOfFragment
				.findViewById(R.id.layout_of_web_controllers);

		if (showToolBar()) {
			layoutOfWebContollers.setVisibility(View.VISIBLE);
			View backButton = viewOfFragment.findViewById(R.id.web_back);
			View forwardButton = viewOfFragment.findViewById(R.id.web_forward);
			View refreshButton = viewOfFragment.findViewById(R.id.web_refresh);
			backButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (browser.canGoBack())
						browser.goBack();

				}
			});

			forwardButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (browser.canGoForward())
						browser.goForward();

				}
			});

			refreshButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					initWebPage();

				}
			});

		} else {
			layoutOfWebContollers.setVisibility(View.GONE);
		}

	}

	protected abstract String getUrlOfPage();

	protected abstract String getTitle();

	private class MximoWebViewBrowser extends WebViewClient {

		private int webViewPreviousState;
		private final int PAGE_STARTED = 0x1;
		private final int PAGE_REDIRECTED = 0x2;

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			webViewPreviousState = PAGE_REDIRECTED;
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			webViewPreviousState = PAGE_STARTED;
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {

			if (webViewPreviousState == PAGE_STARTED) {
				RelativeLayout progressLayout = (RelativeLayout) viewOfFragment
						.findViewById(R.id.loadingPanel);
				progressLayout.setVisibility(View.GONE);
				browser.setVisibility(View.VISIBLE);
			}

		}
	}

}
