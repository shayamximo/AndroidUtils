package com.renuar.pagefragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.renuar.App;
import com.renuar.GlobalConstants;
import com.renuar.R;
import com.renuar.actionbar.ActionBarContorller;
import com.renuar.actionbar.ActionBarControllerFactory;
import com.renuar.json.Params;
import com.renuar.json.Product;
import com.renuar.json.helpers.ConfigurableTabInfo;
import com.renuar.json.helpers.ItemPageParams;
import com.renuar.json.helpers.NavBarParams;
import com.renuar.json.stores.InitInfoStore;
import com.renuar.json.stores.SearchStore;
import com.renuar.json.stores.StoreLoadDataListener;
import com.renuar.utils.HackyUtils;
import com.renuar.utils.ProgressDialogFragment;
import com.renuar.utils.SharedPreferencesController;
import com.renuar.utils.UtilityFunctions;

public abstract class FragmentPageBase extends Fragment {

	public static final String LIST_OF_PLACEHOLDERS_KEY = "list_of_place_holders_key";
	private HashMap<String, ArrayList<String>> mapOfParams;

	FragmentRoot fragmentRoot;

	protected LayoutInflater inflator;
	protected View viewOfFragment;
	protected static final int BASIC_LAYOUT_ID = -1;

	protected abstract int getLayoutID();

	protected ActionBarContorller actionBarContorller = null;

	// This function is for initializing topbar
	// if a fragment doesn't have a top bar it would simply do nothing.
	protected abstract void initializeActionBar();

	protected DisplayImageOptions options;
	protected ImageLoader imageLoader;

	protected String keyOfFragment;
	private boolean firstLevelFragment;

	public boolean isFirstLevelFragment() {
		return firstLevelFragment;
	}

	@Override
	public final View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		int fragmentId = getLayoutID();

		options = ((App) getActivity().getApplicationContext()).getOptions();
		imageLoader = ((App) getActivity().getApplicationContext())
				.getImageLoader();

		if (fragmentId == BASIC_LAYOUT_ID)
			return createBasicView(inflater, container);

		firstLevelFragment = getArguments().getBoolean(
				FragmentRoot.KEY_IS_FIRST_LEVEL_FRAGMENT, false);
		viewOfFragment = inflater.inflate(fragmentId, container, false);
		this.inflator = inflater;
		return viewOfFragment;
	}

	private View createBasicView(LayoutInflater inflater, ViewGroup container) {
		View v = inflater.inflate(R.layout.fragment_layout, container, false);
		TextView tv = (TextView) v.findViewById(R.id.text);

		String title = null;
		Bundle send = getArguments();
		if (send != null)
			title = send.getString(FragmentRoot.KEY_NAME_OF_CHILD);

		if (title == null)
			title = "Base Class";
		tv.setText(title + " Content");
		return v;
	}

	private void configureOrderPage() {
		final TextView textViewFarRight = actionBarContorller.textViewFarRight;
		textViewFarRight.post(new Runnable() {

			@Override
			public void run() {
				if (textViewFarRight.getVisibility() == View.VISIBLE
						&& isOrderPage()) {
					int dpPadding = (int) TypedValue.applyDimension(
							TypedValue.COMPLEX_UNIT_DIP, 1, getResources()
									.getDisplayMetrics());
					actionBarContorller.rrTextViewFarRight.setPadding(
							dpPadding * 2, dpPadding * 2, dpPadding * 2,
							dpPadding * 2);
					actionBarContorller.rrTextViewFarRight
							.setBackgroundColor(getResources().getColor(
									R.color.black));
					actionBarContorller.textViewFarRight
							.setBackgroundResource(R.drawable.variantoption_mail_to);

				}

			}
		});

		ConfigurableTabInfo configurableTabInfo = InitInfoStore.getInstance()
				.getConfigurableTabInfo();
		if (configurableTabInfo.font != null) {
			Typeface font = Typeface.createFromAsset(App.getApp().getAssets(),
					configurableTabInfo.font + ".ttf");
			actionBarContorller.textViewFarRight.setTypeface(font);

		}
	}

	/**
	 * This is ugly hack, for mximo view, since in manifest I have to configure
	 * WithActionBar.
	 * 
	 * @return
	 */
	private boolean shouldPerformHideAnimationActionBar() {
		return ((GlobalConstants.IS_ACTION_BAR_APP_CONFIGURED_FROM_MANIFEST) && (!InitInfoStore
				.getInstance().showNavBar()));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		fragmentRoot = (FragmentRoot) getParentFragment();
		if (shouldInitializeActionBarController()) {

			actionBarContorller = ActionBarControllerFactory
					.getActionBarController(this);
			if (shouldPerformHideAnimationActionBar())
				HackyUtils.showActionBarNoAnimation(getActivity()
						.getActionBar());
		} else {
			if (shouldPerformHideAnimationActionBar())
				HackyUtils.hideActionBarNoAnimation(getActivity()
						.getActionBar());
			boolean isRegistered = SharedPreferencesController
					.isUserRegistered();

		}
		initializeActionBar();
		if (shouldInitializeActionBarController()) {
			configureOrderPage();
			initializeJoinInActionBar();
		}

		super.onActivityCreated(savedInstanceState);
	}

	private void initializeJoinInActionBar() {

		boolean isRegistered = SharedPreferencesController.isUserRegistered();
		if (isRegistered)
			return;

		String actionLabel = getValueOfParamFromName("action_label");
		if (actionLabel != null && actionLabel.equals("JOIN")) {

			ImageView imageViewRight = actionBarContorller.imageViewOnRightStandalone;
			imageViewRight.setImageResource(R.drawable.ic_action_join);
			imageViewRight.setVisibility(View.VISIBLE);
			imageViewRight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					String action = getValueOfParamFromName("action");
					fragmentRoot.openFragmentFromActionLabel(action, null);

				}
			});

		}
	}

	private boolean shouldInitializeActionBarController() {
		return (!(this instanceof FragmentHomeScreen));
	}

	public void setViewsToBrandColor(int... viewIds) {

		View fragmentView = getView();
		String brandColor = InitInfoStore.getInstance().getBrandColor();

		for (int viewID : viewIds) {
			View tempView = fragmentView.findViewById(viewID);
			tempView.setBackgroundColor(Color.parseColor(brandColor));
		}

	}

	protected void setViewToActionButtonColor(View view) {
		String actionButtonColor = InitInfoStore.getInstance()
				.getActionButtonColor();
		if (actionButtonColor != null)
			setViewToColor(view, "#" + actionButtonColor);
	}

	protected void setViewToColor(View view, int color) {
		view.setBackgroundColor(getResources().getColor(color));
	}

	protected void setViewToColor(View view, String color) {
		view.setBackgroundColor(Color.parseColor(color));
	}

	public void setViewToBrandColor(View view) {
		String brandColor = InitInfoStore.getInstance().getBrandColor();
		view.setBackgroundColor(Color.parseColor(brandColor));
	}

	public void makeViewsVisible(View... views) {
		for (View view : views) {
			view.setVisibility(View.VISIBLE);
		}
	}

	protected void startSearchResultsFragment(final SearchStore searchStore) {

		final ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
		progressDialogFragment.show(getFragmentManager(), "MyProgressDialog");
		progressDialogFragment.setCancelable(false);

		searchStore.loadDataIfNotInitialized(new StoreLoadDataListener() {

			@Override
			public void onFinish() {

				progressDialogFragment.dismiss();

				if (searchStore.getProducts() == null
						|| (searchStore.getProducts().size() == 0)) {

					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(
									getActivity(),
									getResources().getString(
											R.string.no_items_found_for_search),
									Toast.LENGTH_LONG).show();

						}
					});

				} else {

					Fragment f = getParentFragment();
					if (f instanceof FragmentRoot) {
						SearchResultsViewController searchResultsFragment = new SearchResultsViewController();

						Bundle args = new Bundle();
						ArrayList<Product> productsArrayList = new ArrayList<Product>(
								searchStore.getProducts());
						args.putParcelableArrayList(
								SearchResultsViewController.PRODUCT_LIST_FROM_SEARCH_RESULT,
								productsArrayList);
						args.putString(
								SearchResultsViewController.TITLE_OF_SEARCH_RESULTS,
								getTitleForSearchResultsFragment(searchStore));

						((FragmentRoot) f).openFragmnet(searchResultsFragment,
								args);
					}
				}

			}

			@Override
			public void onError() {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						progressDialogFragment.dismiss();
						Toast.makeText(
								getActivity(),
								getResources()
										.getString(
												R.string.incorrect_search_values_message),
								Toast.LENGTH_LONG).show();

					}
				});

			}
		});

	}

	private String getTitleForSearchResultsFragment(SearchStore searchStore) {
		String titleForSearchResultsFragment = null;

		switch (searchStore.getSearchType()) {
		case PRICE_RANGE:
			titleForSearchResultsFragment = getResources().getString(
					R.string.title_search_by_price);
			break;
		case QUERY:
			titleForSearchResultsFragment = getResources().getString(
					R.string.title_search_by_query);
			break;
		case RATING:
			titleForSearchResultsFragment = getResources().getString(
					R.string.title_search_by_rating);
			break;
		case SALE:
			titleForSearchResultsFragment = getResources().getString(
					R.string.title_search_by_sale);
			break;
		}

		return titleForSearchResultsFragment;
	}

	protected void setEditTextForSearch(final EditText editText) {
		editText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH
						|| actionId == EditorInfo.IME_NULL) {

					InputMethodManager mgr = (InputMethodManager) App.getApp()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);

					SearchStore searchStore = new SearchStore(v.getText()
							.toString());
					startSearchResultsFragment(searchStore);

					return true;
				} else {
					return false;
				}
			}
		});
	}

	protected boolean isOrderPage() {
		ItemPageParams itemPageParams = InitInfoStore.getInstance()
				.getItemPageParams();
		if (itemPageParams != null)
			return itemPageParams.isOrder();
		return false;

	}

	/**
	 * 
	 * most of the times, there is just one param, which is why the second
	 * method,getValueOfParamFromName which will just return the first one.
	 */
	protected ArrayList<String> getValueListOfParamFromName(String name) {
		return mapOfParams.get(name);
	}
	
	protected boolean getBooleanOfParamFromName(String name,boolean defaultValue)
	{
		String value = getValueOfParamFromName(name);
		if (value== null)
			return defaultValue;
		return Boolean.valueOf(value);
	}

	protected String getValueOfParamFromName(String name) {
		if (mapOfParams == null)
			return null;
		List<String> listNames = mapOfParams.get(name);
		if (listNames != null)
			return listNames.get(0);
		return null;
	}

	public void setParamsListFromServer(String keyOfFragment,
			List<Params> paramsListFromServer) {

		mapOfParams = new HashMap<String, ArrayList<String>>();
		this.keyOfFragment = keyOfFragment;
		for (Params params : paramsListFromServer) {
			ArrayList<String> stringArray = mapOfParams.get(params.getName());

			if ((stringArray == null) || (stringArray.isEmpty())) {
				stringArray = new ArrayList<String>();
			}
			stringArray.add(params.getValue());

			mapOfParams.put(params.getName(), stringArray);
		}

	}

	public void handleNonFragmentActions(String name) {
		if (name.equals("contact")) {

			String email = InitInfoStore.getInstance().getPageByName(name)
					.getParamsByName("app_url").getValue();
			email = email.replace("mailto:", "");
			String toMail[] = { email };
			Intent emailIntent = new Intent(
					android.content.Intent.ACTION_SEND_MULTIPLE);
			emailIntent.putExtra(Intent.EXTRA_EMAIL, toMail);
			emailIntent.setType("plain/html");

			getActivity().startActivity(
					android.content.Intent.createChooser(emailIntent,
							"Send mail..."));
		}

		if (name.equals("call")) {

			String phone = InitInfoStore.getInstance().getPageByName(name)
					.getParamsByName("app_url").getValue();

			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse(phone));
			startActivity(callIntent);

		}
	}

	protected boolean shouldCustomizeEmptyList(
			ICustomizedEmptyListFragment customizedEmptyListFragment) {
		if (customizedEmptyListFragment.isEmptyList()) {
			String staticBackground = getValueOfParamFromName("static_background");
			String staticImage = getValueOfParamFromName("static_image");

			if ((staticBackground != null) || (staticImage != null)) {
				return true;
			}
		}

		return false;
	}

	protected void loadEmptyListFragment(
			ICustomizedEmptyListFragment customizedEmptyListFragment) {

		String staticBackground = getValueOfParamFromName("static_background");
		String staticImage = getValueOfParamFromName("static_image");
		String caption = getValueOfParamFromName("static_caption");
		String fontString = getValueOfParamFromName("static_caption_font");

		final ImageView backgroundImageView = (ImageView) viewOfFragment
				.findViewById(R.id.main_background_image);

		final ImageView imageView = (ImageView) viewOfFragment
				.findViewById(R.id.image_empty_list_background);

		String textColorOfText = getValueOfParamFromName("static_caption_color");

		int textSize = Integer
				.parseInt(getValueOfParamFromName("static_caption_size"));

		TextView text = (TextView) viewOfFragment
				.findViewById(R.id.text_empty_list_background);
		text.setText(caption);
		text.setTextColor(Color.parseColor("#" + textColorOfText));
		text.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

		if (fontString != null)
			UtilityFunctions.setTextToFont(text, fontString);

		if (staticImage != null) {

			imageLoader.displayImage(staticImage, imageView, options, null);
		} else {
			imageView.setImageResource(customizedEmptyListFragment
					.getImageResourceIdIfNoneDefinedFromServer());
		}

		imageLoader.displayImage(staticBackground, backgroundImageView,
				options, null);

	}

	protected String generateProperStringForPrice(double price) {
		String currencySymbol = InitInfoStore.getInstance().getCurrencySymbol();
		String priceRepresentedInString = Double.toString(price);
		String[] dotSplit = priceRepresentedInString.split("\\.");

		if (dotSplit.length == 2) {
			String beforeDot = dotSplit[0];
			String afterDot = dotSplit[1];
			if (afterDot.length() == 1)
				priceRepresentedInString += "0";
			if (afterDot.length() > 2) {
				afterDot = afterDot.substring(0, 2);
				priceRepresentedInString = beforeDot + "." + afterDot;
			}

		}
		return (currencySymbol + priceRepresentedInString);
	}

	public FragmentRoot getFragmentRoot() {
		return fragmentRoot;
	}

	public void configureBackgroundColor(ActionBar actionBar, String forceColor) {
		NavBarParams navBarParams = InitInfoStore.getInstance()
				.getConfigurableNavBarParams();
		String navBarColor = navBarParams.navBarBackgroundColor;
		if (navBarColor == null) {
			navBarColor = InitInfoStore.getInstance().getBrandColor();

		}
		if (forceColor != null)
			navBarColor = forceColor;

		actionBar.setBackgroundDrawable(new ColorDrawable((int) Long.parseLong(
				"FF" + navBarColor.replace("#", ""), 16)));

	}

	protected void runOnUiThread(Runnable r) {
		getActivity().runOnUiThread(r);
	}

}
