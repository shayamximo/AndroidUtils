package com.soa.invertededge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;

public class MainActivity extends Activity {

	private UiLifecycleHelper uiHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		uiHelper = new UiLifecycleHelper(this, new StatusCallback() {

			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				// TODO Auto-generated method stub

			}
		});
		uiHelper.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.textView1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// FacebookDialog shareDialog = new
				// FacebookDialog.ShareDialogBuilder(
				// MainActivity.this)
				// .setLink("https://developers.facebook.com/android")
				// .build();
				

				if (FacebookDialog.canPresentShareDialog(MainActivity.this,
						FacebookDialog.ShareDialogFeature.SHARE_DIALOG))

				{
					FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(
							MainActivity.this)
							.setName("heavy metal rules!")
							.setLink(
									"https://play.google.com/store/apps/details?id=com.lamaloli")
							.setPicture(
									"https://scontent-a-fra.xx.fbcdn.net/hphotos-prn2/t1.0-9/969459_1377427689139011_1285323712_n.jpg")
							.build();
					uiHelper.trackPendingDialogCall(shareDialog.present());
				}

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		uiHelper.onActivityResult(requestCode, resultCode, data,
				new FacebookDialog.Callback() {
					@Override
					public void onError(FacebookDialog.PendingCall pendingCall,
							Exception error, Bundle data) {
						Log.e("Activity",
								String.format("Error: %s", error.toString()));
					}

					@Override
					public void onComplete(
							FacebookDialog.PendingCall pendingCall, Bundle data) {
						Log.i("Activity", "Success!");
					}
				});
	}

	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	public UiLifecycleHelper getUiHelper() {
		return uiHelper;
	}
}
