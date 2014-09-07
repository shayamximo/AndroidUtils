package com.renuar.pagefragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.renuar.GlobalConstants;
import com.renuar.R;
import com.renuar.json.Promotion;
import com.renuar.json.stores.InitInfoStore;
import com.renuar.json.stores.PromotionsStore;
import com.renuar.utils.SharedPreferencesController;
import com.renuar.utils.UtilityFunctions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class FragmentPromotionChosen extends FragmentPageBase {

	public static final String PROMOTION_CHOSEN_ADDRESS_OF_PROMOTION = "promotion_chosen_address_of_promotion";

	Promotion chosenPromotion;

	@Override
	protected int getLayoutID() {
		return R.layout.promotion_chosen_fragment;
	}

	@Override
	protected void initializeActionBar() {

		actionBarContorller.textViewMainTitle.setText(getResources().getString(
				R.string.bonuses));
		ImageView facebookButton = actionBarContorller.imageViewOnRightStandalone;
		makeViewsVisible(facebookButton);
		facebookButton.setImageResource(R.drawable.ic_action_facebook);

		facebookButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String textToPublish = new StringBuilder()
						.append(InitInfoStore.getInstance()
								.getPromotionShareMessage() + "\n\n")
						.append("\n" + chosenPromotion.getName()).append("\n")
						.append(chosenPromotion.getDescription() + "\n\n")
						.append("Download the App: \n")
						.append(InitInfoStore.getInstance().getGoogleQr())
						.toString();

				UtilityFunctions.shareToFacebook(getActivity(),
						chosenPromotion.getName(),
						chosenPromotion.getImageUrl());

			}
		});

		final String urlOfPath = chosenPromotion.getPath();
		final String sName = chosenPromotion.getName();
		if (urlOfPath != GlobalConstants.EMPTY_STRING) {

			actionBarContorller.secondViewOnRightStandalone
					.setImageResource(R.drawable.ic_action_arrow_up_right);
			makeViewsVisible(actionBarContorller.secondViewOnRightStandalone);

			actionBarContorller.secondViewOnRightStandalone
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							Bundle args = new Bundle();
							args.putString(
									FragmentPathChosenFromPromotions.URL_OF_CHOSEN_PATH,
									urlOfPath);

							args.putString(
									FragmentPathChosenFromPromotions.TITLE_OF_CHOSEN_PROMOTION,
									sName);

							fragmentRoot
									.openPathFromChosenPromotionFragment(args);

						}
					});
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		TextView name = (TextView) viewOfFragment
				.findViewById(R.id.promotion_chosen_name);
		TextView description = (TextView) viewOfFragment
				.findViewById(R.id.promotion_chosen_description);
		final ImageView imageOfPromotion = (ImageView) viewOfFragment
				.findViewById(R.id.promotion_chosen_image);
		final RelativeLayout loadingPanel = (RelativeLayout) viewOfFragment
				.findViewById(R.id.loadingPanel);

		Bundle args = getArguments();
		int addressOfPromotionInPromotionStore = args
				.getInt(PROMOTION_CHOSEN_ADDRESS_OF_PROMOTION);
		chosenPromotion = PromotionsStore.getInstance().getPromotionsList()
				.get(addressOfPromotionInPromotionStore);

		String finePrint = chosenPromotion.getFinePrint();
		if (finePrint != null) {
			TextView finePrintTextView = (TextView) viewOfFragment
					.findViewById(R.id.textview_fine_print);
			finePrintTextView.setVisibility(View.VISIBLE);
			finePrintTextView.setText(finePrint);
		}

		final String sName = chosenPromotion.getName();
		name.setText(sName);
		description.setText(chosenPromotion.getDescription());

		final String urlOfPath = chosenPromotion.getPath();

		if (urlOfPath != GlobalConstants.EMPTY_STRING) {
			imageOfPromotion.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Bundle args = new Bundle();
					args.putString(
							FragmentPathChosenFromPromotions.URL_OF_CHOSEN_PATH,
							urlOfPath);

					args.putString(
							FragmentPathChosenFromPromotions.TITLE_OF_CHOSEN_PROMOTION,
							sName);

					fragmentRoot.openPathFromChosenPromotionFragment(args);

				}
			});
		}

		imageLoader.displayImage(chosenPromotion.getImageUrl(),
				imageOfPromotion, options, new SimpleImageLoadingListener() {
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
						loadingPanel.setVisibility(View.GONE);
						imageOfPromotion.setVisibility(View.VISIBLE);

					}
				}, new ImageLoadingProgressListener() {
					@Override
					public void onProgressUpdate(String imageUri, View view,
							int current, int total) {

					}
				});

		String eventName = chosenPromotion.getEventName();
		View signUpButton = viewOfFragment
				.findViewById(R.id.sign_up_from_promotion_chosen);

		boolean isRegistered = SharedPreferencesController.isUserRegistered();

		if (eventName.equals("spree.user.new_user_registration")
				&& (!isRegistered)) {

			signUpButton.setVisibility(View.VISIBLE);
			setViewToBrandColor(signUpButton);
			signUpButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					fragmentRoot.openFragment("register", null);

				}
			});
		}

		super.onActivityCreated(savedInstanceState);
	}

}
