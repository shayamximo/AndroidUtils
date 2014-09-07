package com.soa.bhc.pagefragments;

import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.soa.bhc.R;
import com.soa.bhc.json.Page;
import com.soa.bhc.json.Params;
import com.soa.bhc.json.stores.InitInfoStore;

public class SocialGridViewController extends FragmentPageBase {

	private static int NUMBER_OF_COLUMNS_IN_TABLE = 2;

	@Override
	protected int getLayoutID() {
		return R.layout.layout_social;
	}

	@Override
	protected void initializeActionBar() {
		actionBarContorller.textViewMainTitle.setText(getResources().getString(
				R.string.social));

	}

	private void socialNetworkClicked(String appUrl) {

		Intent intent = null;
		try {
			if (appUrl != null) {
				intent = new Intent(Intent.ACTION_VIEW, Uri.parse(appUrl));

				this.startActivity(intent);
			}

		} catch (Exception e) {

		}
	}

	private int getDrawableByIconImage(String iconImage) {
		if (iconImage.equals("social_FB"))
			return R.drawable.facebook;

		if (iconImage.equals("social_loveandpride"))
			return R.drawable.social_loveandpride;
		
		if (iconImage.equals("social_renuar"))
			return R.drawable.social_renuar;


		if (iconImage.equals("social_instagram"))
			return R.drawable.instragram;

		if (iconImage.equals("social_pinterest"))
			return R.drawable.piners;

		if (iconImage.equals("social_twitter"))
			return R.drawable.stwitter;

		if (iconImage.equals("social_youtube"))
			return R.drawable.youtube;

		return -1;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		ArrayList<String> socialMethodsList = getValueListOfParamFromName("page");

		TableLayout tableLayout = new TableLayout(getActivity());

		FrameLayout frame = (FrameLayout) viewOfFragment
				.findViewById(R.id.frame_social);

		tableLayout.setPadding(0, 0, 0, 0);

		int numberOfButtonsInsertedInRow = 0;
		TableRow tr = new TableRow(getActivity());
		int numberOfPages = socialMethodsList.size();
		int numberOfPagesInserted = 1;

		for (String socialMethod : socialMethodsList) {
			final Page page = InitInfoStore.getInstance().getPageByName(
					socialMethod);
			if (page == null) {
				numberOfPages--;
				continue;
			}

			String iconImage = page.getValueByName("icon_image");

			String appUrl = null;
			String className = null;

			Params appUrlParams = page.getParamsByName((!socialMethod
					.equals("instagram")) ? "app_url" : "app_url_android");
			if (appUrlParams != null)
				appUrl = appUrlParams.getValue();

			Params classNameParams = page.getParamsByName("class_name");
			if (classNameParams != null)
				className = classNameParams.getValue();

			Button button = addButton(tr, iconImage);

			final String classNameForButton = className;
			if (className != null) {
				button.setTag(page.getName());
				button.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {

						try {
							fragmentRoot.openFragment((String) view.getTag(),
									null);
						} catch (Exception e) {

							if (classNameForButton
									.equals("YouTubeViewController")) {
								String url = page.getParamsByName("url")
										.getValue();
								socialNetworkClicked(url);
							}
						}

					}
				});
			}
			if (appUrl != null && appUrl.length() != 0) {
				button.setTag(R.id.arrow, appUrl);

				button.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						socialNetworkClicked((String) view.getTag(R.id.arrow));

					}
				});
			}

			numberOfButtonsInsertedInRow++;
			if ((numberOfButtonsInsertedInRow == NUMBER_OF_COLUMNS_IN_TABLE)
					|| (numberOfPagesInserted == numberOfPages)) {

				tableLayout.addView(tr);
				tr = new TableRow(getActivity());
				numberOfButtonsInsertedInRow = 0;
			}
			numberOfPagesInserted++;

		}
		frame.addView(tableLayout);

		super.onActivityCreated(savedInstanceState);
	}

	private Button addButton(TableRow tr, String iconImage) {
		View view = inflator
				.inflate(R.layout.layout_in_social_table, tr, false);

		Button button = (Button) view.findViewById(R.id.social_button);
		button.setBackgroundResource(getDrawableByIconImage(iconImage));

		tr.addView(view);
		return button;
	}

}
