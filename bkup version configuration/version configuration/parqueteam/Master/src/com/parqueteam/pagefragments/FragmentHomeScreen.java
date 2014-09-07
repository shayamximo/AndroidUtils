package com.parqueteam.pagefragments;

import java.util.ArrayList;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parqueteam.App;
import com.parqueteam.ChooseAppActivity;
import com.parqueteam.GlobalConstants;
import com.parqueteam.MainActivity;
import com.parqueteam.R;
import com.parqueteam.json.helpers.NavBarParams;
import com.parqueteam.json.stores.InitInfoStore;
import com.parqueteam.utils.ConfigurationFile;
import com.parqueteam.utils.HackyUtils;
import com.parqueteam.utils.MximoViewUtils;
import com.parqueteam.utils.SharedPreferencesController;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public abstract class FragmentHomeScreen extends FragmentPageBase {

	@Override
	protected void initializeActionBar() {
		if (GlobalConstants.IS_ACTION_BAR_APP_CONFIGURED_FROM_MANIFEST
				&& InitInfoStore.getInstance().showNavBar()) {
			ActionBar actionBar = getActivity().getActionBar();
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
			actionBar.setCustomView(R.layout.home_screen_action_bar);

			if (InitInfoStore.getInstance().isSlidingMenu()) {

				setTextOfSlidingMenu();
				configureBackgroundColor(actionBar, null);
			} else {

				loadBackgroundImageOfActionBar();
				

				if (HackyUtils.shouldMakeActionBarWhiteInHomeFragment())
					configureBackgroundColor(actionBar, "FFFFFF");
				else
					configureBackgroundColor(actionBar, null);
			}
			loadActionBarAction();

			configureSlidingMenuHomeParams(actionBar);

		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		if (ConfigurationFile.getInstance().isAppMximoView()) {
			ViewGroup viewGroupOfToolBar = (ViewGroup) viewOfFragment
					.findViewById(R.id.toolbar_layout);
			viewGroupOfToolBar.setVisibility(View.VISIBLE);

			View refresh = viewGroupOfToolBar.findViewById(R.id.image_refresh);
			refresh.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					MximoViewUtils.restartApp(ConfigurationFile.getInstance()
							.getAppAddress(), getActivity());
				}
			});

			View list = viewGroupOfToolBar.findViewById(R.id.image_list);
			list.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Bundle args = new Bundle();
					args.putParcelableArrayList(
							ChooseAppActivity.APPS_LIST_KEY, App.getApp()
									.getAppInfoList());

					Intent intent = new Intent(getActivity(),
							ChooseAppActivity.class);
					intent.putExtras(args);
					startActivity(intent);
				}
			});
		}

		super.onActivityCreated(savedInstanceState);
	}

	private void loadActionBarAction() {
		final String action = InitInfoStore.getInstance().getNavBarAction();
		if ((action != null) && ((action.equals("register"))||action.equals("join"))) {
			boolean isRegistered = SharedPreferencesController
					.isUserRegistered();
			if (isRegistered)
				return;
		}

		if (action != null) {
			ImageView imageViewRight = (ImageView) getActivity().findViewById(
					R.id.imageview_right_actionbar);
			imageViewRight.setImageResource(R.drawable.ic_action_join);
			imageViewRight.setVisibility(View.VISIBLE);

			imageViewRight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					fragmentRoot.openFragmentFromActionLabel(action, null);

				}
			});
		}
	}

	private void loadBackgroundImageOfActionBar() {

		ImageView imageViewOfLogoForNavBar = (ImageView) getActivity()
				.findViewById(R.id.imageview_actionbar);
		imageViewOfLogoForNavBar.setVisibility(View.VISIBLE);
		imageLoader.displayImage(
				InitInfoStore.getInstance().getNavBarLogoUrl(),
				imageViewOfLogoForNavBar, options, new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {

					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {

					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {

					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {

					}
				});

	}

	private void setTextOfSlidingMenu() {
		TextView title = (TextView) getActivity().findViewById(
				R.id.text_in_action_bar);
		title.setText(InitInfoStore.getInstance().getName());
		NavBarParams nbp = InitInfoStore.getInstance()
				.getConfigurableNavBarParams();
		if (nbp.textColor != null)
			title.setTextColor(Color.parseColor(nbp.textColor));

		title.setVisibility(View.VISIBLE);

		MainActivity mainActivity = (MainActivity) getActivity();
		boolean isBackFromSlidingMenu = mainActivity.isBackFromSlidingMenu();

		if (isBackFromSlidingMenu)
			title.setText(getResources().getString(R.string.choose_category));

		mainActivity.setTitleOfSlidingActionBarTextView(title);
		mainActivity.setBackFromSlidingMenu(false);
	}

	private void configureSlidingMenuHomeParams(ActionBar actionBar) {
		boolean isSlidingMenu = InitInfoStore.getInstance().isSlidingMenu();

		if (isSlidingMenu) {
			actionBar.setDisplayShowHomeEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		else {
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setDisplayHomeAsUpEnabled(false);
		}
	}

}
