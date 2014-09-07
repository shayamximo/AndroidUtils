package com.renuar.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.widget.FacebookDialog;
import com.renuar.App;
import com.renuar.GlobalConstants;
import com.renuar.MainActivity;
import com.renuar.R;
import com.renuar.json.stores.InitInfoStore;
import com.renuar.pagefragments.MximoFacebookShareDialog;

public class UtilityFunctions {

	public static final int NORMALIZE_WIDTH = 320;
	public static final int NORMALIZE_HEIGHT = 480;

	public static final int WIDTH_OF_SCREEN;
	public static final int HEIGHT_OF_SCREEN;

	private static final Point size;

	private static final HashMap<String, String> currencyStringToSymbolMap;

	static {

		WindowManager wm = (WindowManager) App.getApp().getSystemService(
				Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		size = initializeScreenDimensions(display);

		WIDTH_OF_SCREEN = size.x;
		HEIGHT_OF_SCREEN = size.y;
		currencyStringToSymbolMap = new HashMap<String, String>();
		currencyStringToSymbolMap.put("ILS", App.getApp().getResources()
				.getString(R.string.israeli_currency));
		currencyStringToSymbolMap.put("EUR", App.getApp().getResources()
				.getString(R.string.euro_currency));
		currencyStringToSymbolMap.put("GBP", App.getApp().getResources()
				.getString(R.string.gbp_currency));
		currencyStringToSymbolMap.put("USD", App.getApp().getResources()
				.getString(R.string.us_currency));

	}

	@SuppressLint("NewApi")
	private static Point initializeScreenDimensions(Display display) {

		Point tempSize = new Point();
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
			size.set(display.getWidth(), display.getHeight());
		} else {
			display.getSize(tempSize);
		}

		return tempSize;
	}

	public static Point getScreenDimensions() {
		return size;
	}

	public static int NormalizeHeight(int num) {

		return Normalize(num, HEIGHT_OF_SCREEN, NORMALIZE_HEIGHT);
	}

	public static int NormalizeWidth(int num) {

		return Normalize(num, WIDTH_OF_SCREEN, NORMALIZE_WIDTH);
	}

	private static int Normalize(int num, int screenInputParam,
			int normalizationNumber) {
		double temp = ((double) num / normalizationNumber);
		double normalized = temp * screenInputParam;

		return (int) normalized;
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public static Boolean checkFbInstalled() {

		PackageManager pm = App.getApp().getPackageManager();
		boolean flag = false;
		try {
			pm.getPackageInfo("com.facebook.katana",
					PackageManager.GET_ACTIVITIES);
			flag = true;
		} catch (PackageManager.NameNotFoundException e) {
			flag = false;
		}
		return flag;
	}

	public static void shareToFacebook(final Activity activity,
			final String postString, final String imageUrl) {

		if (checkFbInstalled()) {
			if (FacebookDialog.canPresentShareDialog(activity,
					FacebookDialog.ShareDialogFeature.SHARE_DIALOG))

			{
				FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(
						activity)
						.setName(InitInfoStore.getInstance().getName())
						.setLink(InitInfoStore.getInstance().getGoogleQr())
						.setPicture(imageUrl)
						.setDescription(postString)
						.setCaption(InitInfoStore.getInstance().getShareTitle())
						.setRequestCode(MainActivity.FACEBOOK_SHARE_ACTIVITY_ID)
						.build();
				MainActivity ma = (MainActivity) activity;
				ma.getUiHelper().trackPendingDialogCall(shareDialog.present());

			}
		} else {
			final Facebook facebook = new Facebook(GlobalConstants.FB_APP_ID);
			SharedPreferencesController.restoreFacebookCredentials(facebook);

			if (!facebook.isSessionValid()) {
				facebook.authorize(activity, GlobalConstants.FB_PERMISSIONS,
						new DialogListener() {

							public void onComplete(Bundle values) {
								SharedPreferencesController
										.saveFacebookCredentials(facebook);
								postOnFbAuthorised(activity, postString,
										imageUrl);
							}

							public void onCancel() {
								Toast.makeText(activity, "Share Cancelled",
										Toast.LENGTH_SHORT).show();

							}

							public void onFacebookError(FacebookError e) {
								Toast.makeText(activity, "Facebook Error",
										Toast.LENGTH_SHORT).show();
							}

							public void onError(DialogError e) {
								Toast.makeText(activity, "Facebook Error",
										Toast.LENGTH_SHORT).show();
							}
						});
			} else {
				postOnFbAuthorised(activity, InitInfoStore.getInstance()
						.getShareTitle()
						+ "\n"
						+ postString
						+ "\n"
						+ InitInfoStore.getInstance().getGoogleQr(), imageUrl);
			}
		}

	}

	private static void postOnFbAuthorised(Activity activity,
			String postString, String imageUrl) {

		MximoFacebookShareDialog mximoFacebookShareDialog = new MximoFacebookShareDialog();
		mximoFacebookShareDialog.setCancelable(false);
		mximoFacebookShareDialog.show(activity.getFragmentManager(),
				GlobalConstants.EMPTY_STRING);

		Bundle args = new Bundle();
		args.putString(GlobalConstants.TO_FACEBOOK_SHARE_PIC_URL, imageUrl);

		args.putString(GlobalConstants.TO_FACEBOOK_SHARE_MSG, postString);

		mximoFacebookShareDialog.setArguments(args);
	}

	public static void sendEmail(Activity activity, String message,
			ArrayList<String> urls, String subject, String sendTo) {
		ShareEmail shareEmail = new ShareEmail(activity, message, urls,
				subject, sendTo);
		shareEmail.execute();
	}

	public static void sendEmail(Activity activity, String message,
			ArrayList<String> urls) {
		ShareEmail shareEmail = new ShareEmail(activity, message, urls);
		shareEmail.execute();
	}

	private static class ShareEmail extends AsyncTask<URL, Integer, Long> {
		private ProgressDialog mProgressDialog;

		private Activity activity;
		private String message;
		private ArrayList<String> urlList;
		private ArrayList<Uri> uriList = new ArrayList<Uri>();
		private String subject;
		private String sendTo;

		public ShareEmail(final Activity activity, final String message,
				final ArrayList<String> urlList) {
			this.message = message;
			this.urlList = urlList;
			this.activity = activity;
			sendTo = "";
		}

		public ShareEmail(final Activity activity, final String message,
				final ArrayList<String> urlList, String subject, String sendTo) {
			this(activity, message, urlList);
			this.subject = subject;
			this.sendTo = sendTo;

		}

		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(activity, "",
					"Initializing...", true);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}

		protected Long doInBackground(URL... urls) {
			long result = 1;
			int padAtEndOfFileName = 1;

			if (urlList == null)
				return result;
			for (String url : urlList) {
				try {
					File root = new File(
							Environment.getExternalStorageDirectory()
									+ File.separator + "temp" + File.separator);
					if (!root.exists() || !root.isDirectory()) {
						root.mkdirs();
					}
					File file = new File(root, "item" + padAtEndOfFileName
							+ ".jpg");
					padAtEndOfFileName++;
					file.createNewFile();
					URL imageUrl = new URL(url);
					HttpURLConnection conn = (HttpURLConnection) imageUrl
							.openConnection();
					conn.setConnectTimeout(30000);
					conn.setReadTimeout(30000);
					conn.setInstanceFollowRedirects(true);
					InputStream is = conn.getInputStream();
					OutputStream os = new FileOutputStream(file);
					UtilityFunctions.CopyStream(is, os);
					os.close();
					Uri u = Uri.fromFile(file);
					uriList.add(u);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			return result;
		}

		protected void onPostExecute(Long result) {
			mProgressDialog.dismiss();

			if (subject == null)
				subject = InitInfoStore.getInstance().getShareTitle();

			String toMail[] = { sendTo };

			Intent emailIntent = new Intent(
					android.content.Intent.ACTION_SEND_MULTIPLE);
			emailIntent.setType("plain/html");
			emailIntent.putExtra(Intent.EXTRA_EMAIL, toMail);
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
			emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(message));
			emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
					uriList);
			activity.startActivity(android.content.Intent.createChooser(
					emailIntent, "Send mail..."));

		}
	}

	public static void setHighLightAnimation(final View view) {
		Animation glowBorder = new AlphaAnimation(0.3f, 1.0f);
		glowBorder.setDuration(700);
		glowBorder.setRepeatCount(1);
		glowBorder.setFillAfter(true);
		glowBorder.setRepeatMode(Animation.ABSOLUTE);

		glowBorder.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

				// do nothing
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

				// do nothing
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				view.setBackgroundResource(R.drawable.variantoption);

			}
		});

		view.setBackgroundResource(R.drawable.variantoption_red);
		view.startAnimation(glowBorder);

	}

	public static double formatDouble(double d) {

		NumberFormat formatter = new DecimalFormat("#0.00");
		String s = formatter.format(d);
		s = s.replace(",", ".");
		return Double.valueOf(s);

	}

	public static String getCurrencySymbol(String currencyString) {
		String returnValueFromMap = currencyStringToSymbolMap
				.get(currencyString);
		if (returnValueFromMap == null)
			return currencyString;
		return returnValueFromMap;
	}

	public static void makeToast(final Activity activity, final String message) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(activity, message, Toast.LENGTH_LONG).show();

			}
		});
	}

	public static void makeToast(final Activity activity, final int id) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(activity, activity.getResources().getString(id),
						Toast.LENGTH_LONG).show();

			}
		});
	}

	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	public static void hideKeyboard(Activity activity) {
		InputMethodManager inputManager = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		// check if no view has focus:
		View viewTest = activity.getCurrentFocus();
		if (viewTest == null)
			return;

		inputManager.hideSoftInputFromWindow(viewTest.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static void setTextToFont(TextView textView, String fontName) {
		Typeface font;
		font = Typeface.createFromAsset(App.getApp().getAssets(), fontName
				+ ".ttf");
		textView.setTypeface(font);
	}

}
