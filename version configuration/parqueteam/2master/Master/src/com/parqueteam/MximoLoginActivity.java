package com.parqueteam;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.parqueteam.json.AppInfo;
import com.parqueteam.json.stores.AppsStore;
import com.parqueteam.json.stores.StoreLoadDataListener;
import com.parqueteam.utils.MximoViewUtils;
import com.parqueteam.utils.ProgressDialogFragment;
import com.parqueteam.utils.SharedPreferencesController;
import com.parqueteam.utils.UtilityFunctions;

public class MximoLoginActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);

		View buttonSignUp = findViewById(R.id.id_button_sign_up);
		View textSignUp = findViewById(R.id.id_text_view_powered_by);

		buttonSignUp.setOnClickListener(new RunnableForWebClick());
		textSignUp.setOnClickListener(new RunnableForWebClick());

		View signInButton = findViewById(R.id.login_btn_signin);

		final EditText userEditText = (EditText) findViewById(R.id.login_edit_username);
		final EditText passwordEditText = (EditText) findViewById(R.id.login_edit_password);

		Pair<String, String> savedCredentials = SharedPreferencesController
				.getUserCredentials();
		if (savedCredentials != null) {
			userEditText.setText(savedCredentials.first);
			passwordEditText.setText(savedCredentials.second);
		}

		if (MximoViewUtils.giveStagingOption()) {
			ViewGroup viewGroup = (ViewGroup) findViewById(R.id.layout_of_checkbox_staging);
			viewGroup.setVisibility(View.VISIBLE);
		}

		signInButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();

				progressDialogFragment.show(getSupportFragmentManager(), "");
				progressDialogFragment.setCancelable(false);

				UtilityFunctions.hideKeyboard(MximoLoginActivity.this);

				final String userString = userEditText.getText().toString();
				final String passwordString = passwordEditText.getText()
						.toString();

				final AppsStore appsStore = new AppsStore(userString,
						passwordString);
				appsStore.loadDataIfNotInitialized(new StoreLoadDataListener() {

					@Override
					public void onFinish() {
						progressDialogFragment.dismiss();

						if (MximoViewUtils.giveStagingOption()) {
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									CheckBox checkBoxIsStage = (CheckBox) findViewById(R.id.checkbox_is_stage);
									if (checkBoxIsStage.isChecked()) {
										GlobalConstants.PREFIX = GlobalConstants.STAGE;
									}

								}
							});
						}

						SharedPreferencesController.saveUserCredentials(
								userString, passwordString);

						ArrayList<AppInfo> appInfoList = new ArrayList<AppInfo>(
								appsStore.getAppInfoList());
						Collections.sort(appInfoList);

						Bundle args = new Bundle();
						args.putParcelableArrayList(
								ChooseAppActivity.APPS_LIST_KEY, appInfoList);

						Intent intent = new Intent(MximoLoginActivity.this,
								ChooseAppActivity.class);
						intent.putExtras(args);
						startActivity(intent);

					}

					@Override
					public void onError() {
						progressDialogFragment.dismiss();
						UtilityFunctions.makeToast(MximoLoginActivity.this,
								"Login Failed");
					}
				});
			}
		});
	}

	public class RunnableForWebClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			String url = "http://www.mximo.com";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
		}
	}

}
