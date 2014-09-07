package com.soa.bhc.actionbar;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.soa.bhc.App;
import com.soa.bhc.MainActivity;
import com.soa.bhc.R;
import com.soa.bhc.json.helpers.NavBarParams;
import com.soa.bhc.json.stores.InitInfoStore;
import com.soa.bhc.pagefragments.FragmentPageBase;

public abstract class ActionBarContorller {

	protected FragmentPageBase fragmentPageBase;

	public RelativeLayout layoutOfActionBar;
	public ImageView imageViewOnRightOfTitle;
	public TextView textViewMainTitle;
	public TextView textViewFarRight;
	public RelativeLayout rrTextViewFarRight;
	public ImageView imageViewOnRightStandalone;
	public ImageView secondViewOnRightStandalone;
	private ImageView backButton;

	public ImageView imageViewOnLeft;
	public View viewUnderTitle;
	private boolean isLight;

	protected ActionBarContorller(final FragmentPageBase fragmentPageBase) {
		this.fragmentPageBase = fragmentPageBase;
		isLight = InitInfoStore.getInstance().getConfigurableNavBarParams()
				.isLightActionBar();
		initBeforeRender();
		layoutOfActionBar = (RelativeLayout) findViewById(R.id.layout_of_views_on_top);
		imageViewOnRightOfTitle = (ImageView) findViewById(R.id.image_on_right_of_text_main_title);
		textViewMainTitle = (TextView) findViewById(R.id.textiew_main_title);
		textViewFarRight = (TextView) findViewById(R.id.textview_far_right);
		rrTextViewFarRight = (RelativeLayout) findViewById(R.id.rr_of_textview_textview_far_right);
		imageViewOnRightStandalone = (ImageView) findViewById(R.id.imageview_on_right_standalone);
		secondViewOnRightStandalone = (ImageView) findViewById(R.id.second_imageview_on_right_standalone);
		imageViewOnLeft = (ImageView) findViewById(R.id.imageview_on_left);
		backButton = (ImageView) findViewById(R.id.back_button);
		if (isLight)
			backButton.setImageResource(R.drawable.ic_ab_back_holo_light_am);

		setViewsToBrandColor(R.id.textview_far_right,
				R.id.layout_of_views_on_top);

		initializeActionBarRemoteConfiguration();

		boolean isFirstLevelFragment = fragmentPageBase.isFirstLevelFragment();
		if (isFirstLevelFragment) {
			backButton.setVisibility(View.GONE);

			ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) textViewMainTitle
					.getLayoutParams();
			mlp.leftMargin = mlp.leftMargin * 2;
			textViewMainTitle.setLayoutParams(mlp);

		} else {
			setViewToBackButton(backButton);
			setViewToBackButton(textViewMainTitle);
		}
	}

	private void setViewToBackButton(View view) {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity mainActivity = (MainActivity) fragmentPageBase
						.getActivity();
				mainActivity.onBackPressed();

			}
		});
	}

	protected abstract void initBeforeRender();

	public abstract View findViewById(int id);

	public void setViewsToBrandColor(int... viewIds) {

		String brandColor = InitInfoStore.getInstance().getBrandColor();

		for (int viewID : viewIds) {
			View tempView = findViewById(viewID);
			tempView.setBackgroundColor(Color.parseColor(brandColor));
		}
	}

	private void initializeActionBarRemoteConfiguration() {

		NavBarParams navBarParams = InitInfoStore.getInstance()
				.getConfigurableNavBarParams();

		if (navBarParams.textColor != null) {
			textViewMainTitle.setTextColor(Color
					.parseColor(navBarParams.textColor));
		}

		if (navBarParams.titleLogoUrl != null) {
			imageViewOnRightOfTitle
					.setImageResource(R.drawable.navbar_icon_parquetim);
			imageViewOnRightOfTitle.setVisibility(View.VISIBLE);
		}

		if (navBarParams.navBarBackgroundColor != null) {
			layoutOfActionBar.setBackgroundColor(Color
					.parseColor(navBarParams.navBarBackgroundColor));
		}

		if (navBarParams.font != null) {

			Typeface font = Typeface.createFromAsset(App.getApp().getAssets(),
					navBarParams.font + ".ttf");
			textViewMainTitle.setTypeface(font);

		}

	}

	public void setEditDrawable(ImageView imageView) {
		imageView.setImageResource((isLight) ? R.drawable.ic_action_edit_light
				: R.drawable.ic_action_edit);
	}

	public void setAcceptDrawable(ImageView imageView) {
		imageView
				.setImageResource((isLight) ? R.drawable.ic_action_accept_light
						: R.drawable.ic_action_accept);
	}

	public void setSearchDrawable(ImageView imageView) {
		imageView
				.setImageResource((isLight) ? R.drawable.ic_action_search_light
						: R.drawable.ic_action_search);
	}

	public void setFilterDrawable(ImageView imageView) {
		imageView
				.setImageResource((isLight) ? R.drawable.ic_action_filter_light
						: R.drawable.ic_action_filter);
	}

	public void setShoppingBagDrawable(ImageView imageView) {
		imageView
				.setImageResource((isLight) ? R.drawable.ic_action_shopping_bag_light
						: R.drawable.ic_action_shopping_bag);
	}
}
