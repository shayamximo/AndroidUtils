package com.renuar.pagefragments;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.renuar.App;
import com.renuar.GlobalConstants;
import com.renuar.R;
import com.renuar.json.ShippingLocation;
import com.renuar.json.stores.InitInfoStore;
import com.renuar.json.stores.OrderDetailsStore;
import com.renuar.json.stores.StoreLoadDataListener;
import com.renuar.userselection.UserSelectionItem;
import com.renuar.userselection.UserSelectionStore;
import com.renuar.utils.MximoFlurryAgent;
import com.renuar.utils.ProgressDialogFragment;
import com.renuar.utils.SharedPreferencesController;
import com.renuar.utils.UtilityFunctions;

public class UserInfoViewController extends FragmentPageBase {

	public static final String QUALIFIER_IN_EDIT_TEXT_FOR_MANDATORY_FIELD = "*";
	public static final String SHIPPING_LOCATION_NAME = "shipping_location_name";
	public static final String SHIPPING_LOCATION_ID = "shipping_location_id";

	// This can be a country or a state
	private int idOfExcpectedShippingLocation;
	private boolean exitFragmentForShippingLocation;
	private boolean exitFragmentForCountry;

	public void onCreate(Bundle savedInstanceState) {
		exitFragmentForShippingLocation = false;
		exitFragmentForCountry = false;
		idOfExcpectedShippingLocation = -1;
		super.onCreate(savedInstanceState);
	};

	ScrollView scrollViewOfFragment;
	private TextView firstEmptyMandatoryTextViewToScrollTo = null;

	@Override
	protected int getLayoutID() {
		return R.layout.check_out_screen;
	}

	@Override
	public void onResume() {
		super.onResume();
		fragmentRoot.hideTabs();
		setAllEditTextFromPersistentStorage();
	}

	@Override
	public void onPause() {
		super.onPause();
		saveAllDataToPersistentMemory();
	}

	@Override
	protected void initializeActionBar() {

		actionBarContorller.textViewMainTitle.setText(getResources().getString(
				R.string.order_details));

		TextView bottomTextView = (TextView) viewOfFragment
				.findViewById(R.id.textview_bottom_of_topbar);
		makeViewsVisible(bottomTextView);

		UserSelectionStore cart = App.getApp().getCart();

		bottomTextView.setText(getResources().getString(R.string.sum_of_order)
				+ " "
				+ generateProperStringForPrice(cart.getTotalAmountOfPrice()));

	}

	private void setTextViewToDefaultCountry(TextView textView) {
		String defaultCountry = getValueOfParamFromName("default_country");
		if (defaultCountry != null)
			textView.setText(defaultCountry);
	}

	private void setShippingLocationButtons() {
		ViewGroup layoutUser = (ViewGroup) viewOfFragment
				.findViewById(R.id.layout_user);
		ViewGroup layoutDelivery = (ViewGroup) viewOfFragment
				.findViewById(R.id.layout_address_for_delivery);
		ViewGroup layoutPayment = (ViewGroup) viewOfFragment
				.findViewById(R.id.layout_address_for_payment);

		TextView countryTextViewUser = (TextView) layoutUser
				.findViewById(R.id.country);
		TextView countryTextViewDelivery = (TextView) layoutDelivery
				.findViewById(R.id.country);
		TextView countryTextViewPayment = (TextView) layoutPayment
				.findViewById(R.id.country);

		TextView stateTextViewUser = (TextView) layoutUser
				.findViewById(R.id.state);
		TextView stateTextViewDelivery = (TextView) layoutDelivery
				.findViewById(R.id.state);
		TextView stateTextViewPayment = (TextView) layoutPayment
				.findViewById(R.id.state);

		setTextViewToDefaultCountry(countryTextViewUser);

		if (!InitInfoStore.getInstance().isAppLanguageHebrew()) {
			setListenersToOpenShippingLocationFragment(countryTextViewUser,
					R.id.layout_user, true);
			setListenersToOpenShippingLocationFragment(countryTextViewDelivery,
					R.id.layout_address_for_delivery, true);
			setListenersToOpenShippingLocationFragment(countryTextViewPayment,
					R.id.layout_address_for_payment, true);

			setListenersToOpenShippingLocationFragment(stateTextViewUser,
					R.id.layout_user, false);
			setListenersToOpenShippingLocationFragment(stateTextViewDelivery,
					R.id.layout_address_for_delivery, false);
			setListenersToOpenShippingLocationFragment(stateTextViewPayment,
					R.id.layout_address_for_payment, false);
		} else {
			layoutUser.removeView(countryTextViewUser);
			layoutDelivery.removeView(countryTextViewDelivery);
			layoutPayment.removeView(countryTextViewPayment);

			layoutUser.removeView(stateTextViewUser);
			layoutDelivery.removeView(stateTextViewDelivery);
			layoutPayment.removeView(stateTextViewPayment);
		}

	}

	private void setListenersToOpenShippingLocationFragment(
			final TextView textView, final int parentId, final boolean isCountry) {
		textView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				Bundle bundle = new Bundle();
				if (isCountry) {
					exitFragmentForCountry = true;

				} else {
					ViewGroup currentviewGroup = (ViewGroup) viewOfFragment
							.findViewById(parentId);
					String countryName = ((TextView) currentviewGroup
							.findViewById(R.id.country)).getText().toString();

					if (countryName == null || countryName.length() == 0) {
						UtilityFunctions.makeToast(getActivity(),
								R.string.please_fill_country_text);
						return;
					}

					int countryId = App.getApp().getCountriesAndCitiesStore()
							.getCountryIdByName(countryName);

					List<ShippingLocation> countryOrStateList = App.getApp()
							.getCountriesAndCitiesStore()
							.getStatesByCountryId(countryId);

					if (countryOrStateList == null
							|| countryOrStateList.size() == 0) {
						UtilityFunctions.makeToast(getActivity(),
								R.string.state_not_relevant);
						UtilityFunctions.hideKeyboard(getActivity());
						return;
					}

					bundle.putInt(
							ShippingLocationFragment.SHIPPING_KEY_FOR_STATE,
							countryId);
					exitFragmentForCountry = false;

				}

				idOfExcpectedShippingLocation = parentId;
				exitFragmentForShippingLocation = true;
				fragmentRoot.openFragmnet(new ShippingLocationFragment(),
						bundle);

				InputMethodManager inputManager = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);

				inputManager.hideSoftInputFromWindow(view.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);

			}
		});

	}

	private void configureShippingLocationInfo() {
		Bundle args = getArguments();

		if (args != null) {
			String shippingLocationName = args
					.getString(SHIPPING_LOCATION_NAME);
			args.putString(SHIPPING_LOCATION_NAME, null);
			if (shippingLocationName != null) {

				LinearLayout layoutUser = (LinearLayout) getActivity()
						.findViewById(idOfExcpectedShippingLocation);
				TextView shippingLocationTextView = (TextView) layoutUser
						.findViewById((exitFragmentForCountry) ? (R.id.country)
								: (R.id.state));

				exitFragmentForCountry = false;
				shippingLocationTextView.setText(shippingLocationName);
				saveAllDataToPersistentMemory();

			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		MximoFlurryAgent.logEvent(MximoFlurryAgent.USER_INFO_PAGE_VIEWED);
		super.onActivityCreated(savedInstanceState);

		if (exitFragmentForShippingLocation) {
			exitFragmentForShippingLocation = false;
			configureShippingLocationInfo();
		}

		setViewsToBrandColor(R.id.button_continue);

		scrollViewOfFragment = (ScrollView) viewOfFragment
				.findViewById(R.id.scroll_view_checkout_screen);

		setCheckboxWithCorrespondingLayout(
				R.id.checkbox_show_different_address_for_delivery,
				R.id.layout_address_for_delivery, scrollViewOfFragment);

		setCheckboxWithCorrespondingLayout(
				R.id.checkbox_show_different_address_for_payment,
				R.id.layout_address_for_payment, scrollViewOfFragment);

		viewOfFragment.findViewById(R.id.button_continue).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						InputMethodManager inputManager = (InputMethodManager) getActivity()
								.getSystemService(Context.INPUT_METHOD_SERVICE);

						// check if no view has focus:
						View v = getActivity().getCurrentFocus();
						if (v == null)
							return;

						inputManager.hideSoftInputFromWindow(
								v.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
						VerifyUserDetailsAndGoToShippingScreen();
					}
				});
		setShippingLocationButtons();

	}

	private boolean emailAddressesVerified() {

		ViewGroup layoutUser = (ViewGroup) viewOfFragment
				.findViewById(R.id.layout_user);
		ViewGroup layoutDelivery = (ViewGroup) viewOfFragment
				.findViewById(R.id.layout_address_for_delivery);
		ViewGroup layoutPayment = (ViewGroup) viewOfFragment
				.findViewById(R.id.layout_address_for_payment);

		EditText etEmailUser = ((EditText) layoutUser.findViewById(R.id.email));
		EditText etEmailDelivery = ((EditText) layoutDelivery
				.findViewById(R.id.email));
		EditText etEmailPayment = ((EditText) layoutPayment
				.findViewById(R.id.email));

		String emailUser = etEmailUser.getText().toString();
		String emailDelivery = etEmailDelivery.getText().toString();
		String emailPayment = etEmailPayment.getText().toString();

		if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailUser).matches())
			return false;

		boolean isEmailDeliveryEnabled = ((CheckBox) viewOfFragment
				.findViewById(R.id.checkbox_show_different_address_for_delivery))
				.isChecked();
		if (isEmailDeliveryEnabled) {
			if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailDelivery)
					.matches())
				return false;

		}

		boolean isEmailPaymentEnabled = ((CheckBox) viewOfFragment
				.findViewById(R.id.checkbox_show_different_address_for_payment))
				.isChecked();
		if (isEmailPaymentEnabled) {
			if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailPayment)
					.matches())
				return false;

		}

		return true;
	}

	private void VerifyUserDetailsAndGoToShippingScreen() {
		saveAllDataToPersistentMemory();
		boolean validate = validateAllMandatoryFieldsFilled();

		if (!validate) {
			final ScrollView scrollView = (ScrollView) viewOfFragment
					.findViewById(R.id.scroll_view_checkout_screen);
			scrollView.post(new Runnable() {
				@Override
				public void run() {
					int[] location = new int[2];
					firstEmptyMandatoryTextViewToScrollTo
							.getLocationOnScreen(location);
					scrollView.smoothScrollTo(0, location[1]);
					firstEmptyMandatoryTextViewToScrollTo = null;

				}
			});
			return;
		}

		boolean isEmailValidated = emailAddressesVerified();

		if (!isEmailValidated) {
			Toast.makeText(getActivity(),
					getResources().getString(R.string.email_incorrect),
					Toast.LENGTH_LONG).show();
		}

		if (validate && isEmailValidated) {
			final List<NameValuePair> nameValuePairList = createPostParams();

			final OrderDetailsStore orderDetailsStore = new OrderDetailsStore(
					nameValuePairList);

			final ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
			progressDialogFragment.show(getFragmentManager(),
					"MyProgressDialog");
			orderDetailsStore
					.loadDataIfNotInitialized(new StoreLoadDataListener() {

						@Override
						public void onFinish() {
							progressDialogFragment.dismiss();
							Bundle args = new Bundle();
							args.putSerializable(
									ShippingInfoViewController.ORDER_DETAILS_STORE_KEY,
									orderDetailsStore);
							fragmentRoot.openShippingFragment(args);

						}

						@Override
						public void onError() {
							progressDialogFragment.dismiss();
							getActivity().runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(
											getActivity(),
											getResources().getString(
													R.string.general_error),
											Toast.LENGTH_LONG).show();

								}
							});

						}
					});

		}

	}

	private List<NameValuePair> createPostParams() {

		List<NameValuePair> nameValuePairsListUser = createSingleNameValuePairListForLayout(
				R.id.layout_user, R.id.layout_user, "user", true);

		CheckBox checkBoxDelivery = (CheckBox) viewOfFragment
				.findViewById(R.id.checkbox_show_different_address_for_delivery);
		CheckBox checkBoxPayment = (CheckBox) viewOfFragment
				.findViewById(R.id.checkbox_show_different_address_for_payment);

		List<NameValuePair> nameValuePairsListOrder = createSingleNameValuePairListForLayout(
				R.id.layout_address_for_delivery, R.id.layout_user,
				"order[ship_address]", checkBoxDelivery.isChecked());
		List<NameValuePair> nameValuePairsListBilling = createSingleNameValuePairListForLayout(
				R.id.layout_address_for_payment, R.id.layout_user,
				"order[bill_address]", checkBoxPayment.isChecked());

		// user/order/shippin
		List<NameValuePair> nameValuePairsList = new ArrayList<NameValuePair>();
		nameValuePairsList.addAll(nameValuePairsListUser);
		nameValuePairsList.addAll(nameValuePairsListOrder);
		nameValuePairsList.addAll(nameValuePairsListBilling);

		// send promo info
		CheckBox sendPromoCheckBox = ((CheckBox) viewOfFragment
				.findViewById(R.id.checkbox_send_promo));
		nameValuePairsList.add(new BasicNameValuePair("user[get_promos]",
				Boolean.toString(sendPromoCheckBox.isChecked())));

		// variants

		UserSelectionStore cart = App.getApp().getCart();

		List<UserSelectionItem> allUsersItemsList = cart.getAllCartItems();

		for (UserSelectionItem userSelectionItem : allUsersItemsList) {
			int variantId = userSelectionItem.getVariantAndReturnMasterIfNone()
					.getId();
			nameValuePairsList.add(new BasicNameValuePair(
					"order[line_items][][variant_id]", Integer
							.toString(variantId)));

			nameValuePairsList.add(new BasicNameValuePair(
					"order[line_items][][quantity]", Integer
							.toString(userSelectionItem.getQuantity())));
		}

		return nameValuePairsList;
	}

	private List<NameValuePair> createSingleNameValuePairListForLayout(
			int layoutId, int defaultLayoutID, String prefix, boolean isChecked) {

		boolean isUserIteration = (layoutId == defaultLayoutID);

		ViewGroup viewGroup = (ViewGroup) viewOfFragment.findViewById(layoutId);
		ViewGroup defaultViewGroup = (ViewGroup) viewOfFragment
				.findViewById(defaultLayoutID);
		String firstName = createStringFromIdOfTextView(viewGroup,
				defaultViewGroup, R.id.first_name, isChecked);
		String lastName = createStringFromIdOfTextView(viewGroup,
				defaultViewGroup, R.id.last_name, isChecked);
		String email = createStringFromIdOfTextView(viewGroup,
				defaultViewGroup, R.id.email, isChecked);

		String country = "";
		String state = "";
		if (!InitInfoStore.getInstance().isAppLanguageHebrew()) {
			country = createStringForCountry(viewGroup, defaultViewGroup,
					isChecked);
			state = createStringForState(viewGroup, defaultViewGroup, isChecked);
		}

		String street = createStringFromIdOfTextView(viewGroup,
				defaultViewGroup, R.id.street, isChecked);
		String city = createStringFromIdOfTextView(viewGroup, defaultViewGroup,
				R.id.city, isChecked);
		String phone = createStringFromIdOfTextView(viewGroup,
				defaultViewGroup, R.id.phone, isChecked);
		String zip = createStringFromIdOfTextView(viewGroup, defaultViewGroup,
				R.id.zip, isChecked);

		List<NameValuePair> nameValuePairsList = new ArrayList<NameValuePair>();

		nameValuePairsList.add(new BasicNameValuePair(prefix + "[firstname]",
				firstName));
		nameValuePairsList.add(new BasicNameValuePair(prefix + "[lastname]",
				lastName));
		nameValuePairsList
				.add(new BasicNameValuePair(prefix + "[phone]", phone));

		if (isUserIteration) {

			nameValuePairsList.add(new BasicNameValuePair(prefix + "[email]",
					email));
		}

		else {
			nameValuePairsList.add(new BasicNameValuePair(
					prefix + "[address1]", street));
			nameValuePairsList.add(new BasicNameValuePair(prefix + "[city]",
					city));

			if (zip != null && zip.length() > 0)
				nameValuePairsList.add(new BasicNameValuePair(prefix
						+ "[zipcode]", zip));
			else {
				nameValuePairsList.add(new BasicNameValuePair(prefix
						+ "[zipcode]", "00000"));
			}

			if (InitInfoStore.getInstance().isAppLanguageHebrew()) {
				nameValuePairsList.add(new BasicNameValuePair(prefix
						+ "[country_id]", GlobalConstants.ISRAEL_COUNTRY_ID));
			} else {
				nameValuePairsList.add(new BasicNameValuePair(prefix
						+ "[country_id]", country));

				if (state != null)
					nameValuePairsList.add(new BasicNameValuePair(prefix
							+ "[state_id]", state));
			}

		}

		return nameValuePairsList;
	}

	// Important: VITAL precondition to this function, is that all layout id's
	// have the same name!
	private String createStringFromIdOfTextView(ViewGroup currentviewGroup,
			ViewGroup defaultviewGroup, int editTextId, boolean isChecked) {

		if (!isChecked)
			currentviewGroup = defaultviewGroup;

		String returnString = "";
		try {
			returnString = ((EditText) currentviewGroup
					.findViewById(editTextId)).getText().toString();
		} catch (Exception NullPointereException) {

		}

		if ((returnString == null || returnString.equals("")) && (isChecked)) {
			returnString = ((TextView) defaultviewGroup
					.findViewById(editTextId)).getText().toString();
		}

		return returnString;
	}

	private String createStringForCountry(ViewGroup currentviewGroup,
			ViewGroup defaultviewGroup, boolean isChecked) {
		if (!isChecked)
			currentviewGroup = defaultviewGroup;
		String returnString = "";
		try {
			Integer countryId;
			String countryName = ((TextView) currentviewGroup
					.findViewById(R.id.country)).getText().toString();
			countryId = App.getApp().getCountriesAndCitiesStore()
					.getCountryIdByName(countryName);
			returnString = countryId.toString();
		} catch (Exception NullPointereException) {

		}

		return returnString;

	}

	private String createStringForState(ViewGroup currentviewGroup,
			ViewGroup defaultviewGroup, boolean isChecked) {
		if (!isChecked)
			currentviewGroup = defaultviewGroup;
		String returnString = null;
		try {
			Integer countryId;
			String countryName = ((TextView) currentviewGroup
					.findViewById(R.id.country)).getText().toString();
			String stateName = ((TextView) currentviewGroup
					.findViewById(R.id.state)).getText().toString();
			countryId = App.getApp().getCountriesAndCitiesStore()
					.getCountryIdByName(countryName);
			List<ShippingLocation> countryOrStateList = App.getApp()
					.getCountriesAndCitiesStore()
					.getStatesByCountryId(countryId);
			if (countryOrStateList.size() > 0) {

				for (ShippingLocation sl : countryOrStateList) {
					if (sl.getName().equals(stateName)) {
						returnString = sl.getId().toString();
						break;
					}

				}
			}

		} catch (Exception NullPointereException) {

		}

		return returnString;

	}

	private boolean validateAllMandatoryFieldsFilled() {

		ViewGroup userGroup = (ViewGroup) viewOfFragment
				.findViewById(R.id.layout_user);
		ViewGroup deliveryGroup = (ViewGroup) viewOfFragment
				.findViewById(R.id.layout_address_for_delivery);
		ViewGroup paymentGroup = (ViewGroup) viewOfFragment
				.findViewById(R.id.layout_address_for_payment);

		CheckBox checkBoxDelivery = (CheckBox) viewOfFragment
				.findViewById(R.id.checkbox_show_different_address_for_delivery);
		CheckBox checkBoxPayment = (CheckBox) viewOfFragment
				.findViewById(R.id.checkbox_show_different_address_for_payment);

		// we put & and not && because we want the views
		// to be drawn red if neccacry, and that's done in the function
		boolean validate = (validateAllMandatoryFieldsFilled(userGroup, true))
				& (validateAllMandatoryFieldsFilled(deliveryGroup,
						checkBoxDelivery.isChecked()) & validateAllMandatoryFieldsFilled(
						paymentGroup, checkBoxPayment.isChecked()));

		return validate;
	}

	private boolean validateAllMandatoryFieldsFilled(ViewGroup viewGroup,
			boolean isChecked) {

		boolean wholeViewGroupValidated = true;
		if (isChecked == false)
			return true;

		List<TextView> mandatoryEditTextsInViewGroup = getAllEditTextsForLayout(viewGroup);
		for (TextView et : mandatoryEditTextsInViewGroup) {

			boolean isEditTextValidated = !((et.getText().toString().length() == 0) && (et
					.getHint().toString()
					.contains(QUALIFIER_IN_EDIT_TEXT_FOR_MANDATORY_FIELD)));

			// special case, since even though its star, and mandatory,
			// it's only isEditTextValidated if states are defined for the
			// country.
			if ((!isEditTextValidated)
					&& et.getHint()
							.toString()
							.equals(getResources().getString(
									R.string.state_with_star))) {
				TextView country = (TextView) viewGroup
						.findViewById(R.id.country);
				String countryName = country.getText().toString();
				if (country != null && country.length() > 0) {
					int countryId = App.getApp().getCountriesAndCitiesStore()
							.getCountryIdByName(countryName);
					List<ShippingLocation> countryOrStateList = App.getApp()
							.getCountriesAndCitiesStore()
							.getStatesByCountryId(countryId);
					if (countryOrStateList.size() == 0)
						isEditTextValidated = true;

				}

			}

			if (isEditTextValidated == false) {
				wholeViewGroupValidated = false;
				et.setBackgroundResource(R.drawable.variantoption_red);
				if (firstEmptyMandatoryTextViewToScrollTo == null)
					firstEmptyMandatoryTextViewToScrollTo = et;

			} else {
				et.setBackgroundResource(R.drawable.variantoption);
			}

		}

		return wholeViewGroupValidated;
	}

	private void setCheckboxWithCorrespondingLayout(int checkBoxID,
			final int viewToToggleID, final ScrollView scrollView) {

		try {
			CheckBox checkBox = (CheckBox) viewOfFragment
					.findViewById(checkBoxID);
			final View viewToToggle = viewOfFragment
					.findViewById(viewToToggleID);

			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {

					if (isChecked) {
						viewToToggle.setVisibility(View.VISIBLE);
						scrollView.post(new Runnable() {
							@Override
							public void run() {
								scrollView.fullScroll(View.FOCUS_DOWN);
							}
						});
					} else {
						viewToToggle.setVisibility(View.GONE);
					}
				}
			});
		} catch (ClassCastException e) {
			// For now, Do nothing
		}
	}

	private void setAllEditTextFromPersistentStorage() {
		List<TextView> allEditTextsInScreen = getAllEditTextsInEntireScreen();
		int i = 0;
		for (TextView editText : allEditTextsInScreen) {
			String previousStringInEditText = SharedPreferencesController
					.getUserDetail(getActivity(), i);
			if ((previousStringInEditText != null)
					&& (!previousStringInEditText.equals(""))) {
				editText.setText(previousStringInEditText);
			}
			i++;
		}
	}

	private List<TextView> getAllEditTextsInEntireScreen() {
		ViewGroup userGroup = (ViewGroup) viewOfFragment
				.findViewById(R.id.layout_user);
		ViewGroup deliveryGroup = (ViewGroup) viewOfFragment
				.findViewById(R.id.layout_address_for_delivery);
		ViewGroup paymentGroup = (ViewGroup) viewOfFragment
				.findViewById(R.id.layout_address_for_payment);

		List<TextView> editTextsUserGroup = getAllEditTextsForLayout(userGroup);
		List<TextView> editTextsDeliveryGroup = getAllEditTextsForLayout(deliveryGroup);
		List<TextView> editTextsPaymentGroup = getAllEditTextsForLayout(paymentGroup);

		List<TextView> allEditTextsInLayout = new ArrayList<TextView>();
		allEditTextsInLayout.addAll(editTextsUserGroup);
		allEditTextsInLayout.addAll(editTextsDeliveryGroup);
		allEditTextsInLayout.addAll(editTextsPaymentGroup);

		return allEditTextsInLayout;

	}

	private List<TextView> getAllEditTextsForLayout(ViewGroup viewGroup) {
		List<TextView> editTextsInViewGroup = new ArrayList<TextView>();
		int numberOfChildrenInLayout = viewGroup.getChildCount();
		for (int i = 0; i < numberOfChildrenInLayout; i++) {
			View view = viewGroup.getChildAt(i);
			if ((view instanceof TextView) && (!(view instanceof Button))) {
				TextView editTextField = (TextView) view;
				editTextsInViewGroup.add(editTextField);
			}
		}
		return editTextsInViewGroup;
	}

	private void saveAllDataToPersistentMemory() {
		List<TextView> allEditTextsInScreen = getAllEditTextsInEntireScreen();

		int i = 0;
		for (TextView editText : allEditTextsInScreen) {
			String currentStringInEditText = editText.getText().toString();
			if ((currentStringInEditText != null)
					&& (!currentStringInEditText.equals(""))) {
				SharedPreferencesController.saveUserDetail(getActivity(), i,
						currentStringInEditText);
			}
			i++;
		}
	}

}
