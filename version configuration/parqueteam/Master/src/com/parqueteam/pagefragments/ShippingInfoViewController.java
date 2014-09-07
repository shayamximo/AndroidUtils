package com.parqueteam.pagefragments;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.parqueteam.App;
import com.parqueteam.MainActivity;
import com.parqueteam.R;
import com.parqueteam.json.LineItem;
import com.parqueteam.json.ShipAddress;
import com.parqueteam.json.ShippingMethod;
import com.parqueteam.json.stores.InitInfoStore;
import com.parqueteam.json.stores.OrderDetailsStore;
import com.parqueteam.json.stores.StoreLoadDataListener;
import com.parqueteam.utils.ConfigurationFile;
import com.parqueteam.utils.MximoFlurryAgent;
import com.parqueteam.utils.ProgressDialogFragment;
import com.parqueteam.utils.UtilityFunctions;
import com.zooz.android.lib.CheckoutActivity;
import com.zooz.android.lib.model.ZooZInvoice;

public class ShippingInfoViewController extends FragmentPageBase {

	public static String ORDER_DETAILS_STORE_KEY = "order_details_store_key";
	public static String CHOSEN_BRANCH = "chosen_branch";
	public static String CHOSEN_BRANCH_ID = "chosen_branch_id";
	private double totalCostOfAllProducts;
	private double currentShippingFee;
	private String branchName = null;
	private int idOfChosenBranchName;
	private TextView shippingCost;

	private double sumCost;
	private TextView textViewOfSumCost;

	private TextView currentTextViewBranch;
	private ScrollView scrollViewOfEntireView;
	private int currentPosition;
	private boolean exitToPay = false;
	private String orderNumber;
	private String zoozId;
	private String zoozMasterId;
	private boolean isSandBox;

	private void updateFinalPrice(double price) {
		sumCost = price;
		textViewOfSumCost.setText(generateProperStringForPrice(price));

	}

	@Override
	protected int getLayoutID() {
		return R.layout.shipping_methods;
	}

	@Override
	public void onResume() {
		if (exitToPay) {
			fragmentRoot.goToPreviousFragment(null, null, null, 0, null, null);
			fragmentRoot.goToPreviousFragment(null, null, null, 0, null, null);
			((MainActivity) getActivity()).switchTab(0);

		}
		exitToPay = false;
		super.onResume();
	}

	@Override
	protected void initializeActionBar() {

		actionBarContorller.textViewMainTitle.setText(getResources().getString(
				R.string.order_details));

	}

	private void adjustListViewHeightAccordingToNumberOfShippingMethods(
			ListView listView, int numberOfElements) {
		LinearLayout.LayoutParams params = (LayoutParams) listView
				.getLayoutParams();
		int listSize = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, (numberOfElements * 50) + 10,
				getResources().getDisplayMetrics());
		params.height = listSize;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		zoozId = ConfigurationFile.getInstance().getZoozId();
		zoozMasterId = ConfigurationFile.getInstance().getZoozMasterId();
		isSandBox = ConfigurationFile.getInstance().getIsSandbox();

		MximoFlurryAgent.logEvent(MximoFlurryAgent.SHIPPING_INFO_PAGE_VIEWED);

		Bundle args = getArguments();

		branchName = args.getString(CHOSEN_BRANCH);

		if (branchName != null)
			idOfChosenBranchName = args.getInt(CHOSEN_BRANCH_ID);

		OrderDetailsStore orderDetailsStore = (OrderDetailsStore) args
				.getSerializable(ORDER_DETAILS_STORE_KEY);
		setViewsToBrandColor(R.id.brand_color_bottom, R.id.button_pay);

		configureCheckboxEditText(R.id.checkbox_add_shipping_comments,
				R.id.edittext_add_shipping_comments);
		configureCheckboxEditText(R.id.checkbox_gift_wrapping,
				R.id.edittext_gift_wrapping);

		totalCostOfAllProducts = orderDetailsStore.getTotal();

		// The initial shipping fee is always the first one, and it will be
		// selected
		List<ShippingMethod> shippingMethodList = orderDetailsStore
				.getShippingMethodsList();
		if (shippingMethodList != null && shippingMethodList.size() > 0)
			currentShippingFee = shippingMethodList.get(0).getCost();

		((TextView) viewOfFragment
				.findViewById(R.id.textview_price_total_from_server))
				.setText(generateProperStringForPrice(totalCostOfAllProducts));

		shippingCost = (TextView) viewOfFragment
				.findViewById(R.id.textview_shipping_cost);
		textViewOfSumCost = (TextView) viewOfFragment
				.findViewById(R.id.textview_sum);

		shippingCost.setText(generateProperStringForPrice(currentShippingFee));

		updateFinalPrice(totalCostOfAllProducts + currentShippingFee);

		View payView = viewOfFragment.findViewById(R.id.button_pay);

		ListView listView = (ListView) viewOfFragment
				.findViewById(R.id.payment_listview);

		if (shippingMethodList != null && shippingMethodList.size() > 0) {
			listView.setAdapter(new ShippingMethodsAdapter(getActivity(),
					R.layout.cell_in_shipping_method_screen, orderDetailsStore
							.getShippingMethodsList(), inflator));

			adjustListViewHeightAccordingToNumberOfShippingMethods(listView,
					orderDetailsStore.getShippingMethodsList().size());
		} else {
			viewOfFragment.findViewById(R.id.payment_listview).setVisibility(
					View.GONE);
			viewOfFragment.findViewById(R.id.textview_shipping_method_id)
					.setVisibility(View.GONE);

		}

		payView.setTag(orderDetailsStore);

		payView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendPrequisitesToPayment((OrderDetailsStore) v.getTag());
			}
		});
		scrollViewOfEntireView = (ScrollView) viewOfFragment
				.findViewById(R.id.scroll_view_shipping_method_screen);
		configureCoupons(orderDetailsStore);
		configureDonations();

	}

	private void configureDonations() {

		ViewGroup layoutOfDonation = (ViewGroup) viewOfFragment
				.findViewById(R.id.layout_of_donation);
		if (shouldDonate()) {
			TextView textViewAmount = (TextView) layoutOfDonation
					.findViewById(R.id.donation_amount);
			TextView textViewExplanation = (TextView) layoutOfDonation
					.findViewById(R.id.donation_explanation);

			TextView orginizationSelection = (TextView) layoutOfDonation
					.findViewById(R.id.organization_selection);

			String textDonationExplanation1 = getResources().getString(
					R.string.donation_explanation_text_1);
			String nameOfApp = InitInfoStore.getInstance().getName();
			String textDonationExplanation2 = getResources().getString(
					R.string.donation_explanation_text_2);

			String textForExplanationTextView = textDonationExplanation1 + " "
					+ nameOfApp + " " + textDonationExplanation2;

			textViewExplanation.setText(textForExplanationTextView);

			double textAmount = (sumCost / 20);
			textViewAmount.setText(generateProperStringForPrice(textAmount));

			orginizationSelection.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Bundle bundle = new Bundle();
					bundle.putString(
							StoresNamesFragment.SHOPS_KEY_TITLE,
							getResources().getString(
									R.string.donation_select_orginization));
					fragmentRoot
							.openFragmnet(new StoresNamesFragment(), bundle);
				}
			});

			currentTextViewBranch = orginizationSelection;

			if (branchName != null)
				orginizationSelection.setText(branchName);

		} else {
			layoutOfDonation.setVisibility(View.GONE);
		}
	}

	private void configureCoupons(final OrderDetailsStore orderDetailsStore) {

		LinearLayout layoutOfCoupons = (LinearLayout) viewOfFragment
				.findViewById(R.id.layout_of_coupon);
		if (InitInfoStore.getInstance().shouldShowPromotionsOnShipInfo()) {

			final EditText editTextCoupon = (EditText) layoutOfCoupons
					.findViewById(R.id.edit_text_coupon);
			final Button buttonCoupon = (Button) layoutOfCoupons
					.findViewById(R.id.button_apply);

			setViewToBrandColor(buttonCoupon);
			setViewToActionButtonColor(buttonCoupon);
			buttonCoupon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					InputMethodManager mgr = (InputMethodManager) App.getApp()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr.hideSoftInputFromWindow(
							editTextCoupon.getWindowToken(), 0);

					String textInEditText = editTextCoupon.getText().toString();
					if (textInEditText.length() > 0) {

						List<NameValuePair> nameValuePairsList = new ArrayList<NameValuePair>();

						nameValuePairsList.add(new BasicNameValuePair(
								"order[coupon_code]", textInEditText));

						final ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
						progressDialogFragment.show(getFragmentManager(),
								"MyProgressDialog");
						progressDialogFragment.setCancelable(false);

						final OrderDetailsStore discountDetailsStore = new OrderDetailsStore(
								nameValuePairsList, orderDetailsStore
										.getNumber());
						// This dumb variables is simply so i can pass the
						// button
						// to the next thread, to set it to null
						// once the discount succesffully arrived.
						final Button buttonCouponSecond = buttonCoupon;
						final EditText editTextCouponSecond = editTextCoupon;
						discountDetailsStore
								.loadDataIfNotInitialized(new StoreLoadDataListener() {

									@Override
									public void onFinish() {
										progressDialogFragment.dismiss();

										String exception = discountDetailsStore
												.getException();
										if ((exception == null)
												|| (exception.length()) == 0) {
											final double discount = discountDetailsStore
													.getAdjustmentTotal();

											buttonCouponSecond
													.setOnClickListener(null);
											getActivity().runOnUiThread(
													new Runnable() {

														@Override
														public void run() {
															editTextCouponSecond
																	.setEnabled(false);
															editTextCoupon
																	.setText("");
															editTextCoupon
																	.setHint("");
															updateCostsAfterPromotionSucceeded(discount);
															UtilityFunctions
																	.makeToast(
																			getActivity(),
																			R.string.coupon_code_successfully_retrived);

														}

													});
										}

										else {
											UtilityFunctions.makeToast(
													getActivity(), exception);
										}

									}

									@Override
									public void onError() {
										progressDialogFragment.dismiss();
										UtilityFunctions.makeToast(
												getActivity(),
												R.string.general_error);

									}
								});

					}

					else {
						UtilityFunctions.makeToast(getActivity(),
								R.string.coupon_code_empty);
					}

				}
			});
		}

		else {
			layoutOfCoupons.setVisibility(View.GONE);
		}

	}

	private boolean areShippingMethodsDefined(
			OrderDetailsStore orderDetailsStore) {
		List<ShippingMethod> shippingMethodsList = orderDetailsStore
				.getShippingMethodsList();
		return (shippingMethodsList != null && shippingMethodsList.size() > 0);
	}

	private void sendPrequisitesToPayment(
			final OrderDetailsStore orderDetailsStore) {

		boolean isBranchValidated = validateAndInitializeBranchDetails();

		if (!isBranchValidated)
			return;
		if (!areShippingMethodsDefined(orderDetailsStore)) {
			UtilityFunctions.makeToast(getActivity(),
					R.string.no_shipping_methods_define_error_message);
			return;
		}

		EditText addCommentsEditText = (EditText) viewOfFragment
				.findViewById(R.id.edittext_add_shipping_comments);
		EditText addBlessingComments = (EditText) viewOfFragment
				.findViewById(R.id.edittext_gift_wrapping);

		String comment = addCommentsEditText.getText().toString();
		String blessing = addBlessingComments.getText().toString();

		List<NameValuePair> nameValuePairsList = new ArrayList<NameValuePair>();

		if ((addCommentsEditText.getVisibility() == View.VISIBLE)
				&& (comment != null) && comment.length() > 0)
			nameValuePairsList.add(new BasicNameValuePair(
					"order[instructions]", comment));

		if ((addBlessingComments.getVisibility() == View.VISIBLE)
				&& (blessing != null)) {

			if (blessing.length() > 0)
				nameValuePairsList.add(new BasicNameValuePair(
						"order[gift_message]", blessing));
			else
				nameValuePairsList.add(new BasicNameValuePair(
						"order[gift_message]", getResources().getString(
								R.string.wrap_as_gift)));

		}
		if (isBranchRelevant())
			nameValuePairsList.add(new BasicNameValuePair("order[store_id]", ""
					+ idOfChosenBranchName));

		List<ShippingMethod> shippingMethodsList = orderDetailsStore
				.getShippingMethodsList();

		if (shippingMethodsList != null && shippingMethodsList.size() > 0) {
			ShippingMethod currentShippingMethodChosen = shippingMethodsList
					.get(currentPosition);
			int shipId = currentShippingMethodChosen.getId();
			nameValuePairsList.add(new BasicNameValuePair(
					"order[shipping_method_id]", "" + shipId));
		}

		if (nameValuePairsList.size() > 0) {
			final ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
			progressDialogFragment.show(getFragmentManager(),
					"MyProgressDialog");
			progressDialogFragment.setCancelable(false);

			OrderDetailsStore metadataDetails = new OrderDetailsStore(
					nameValuePairsList, orderDetailsStore.getNumber());
			metadataDetails
					.loadDataIfNotInitialized(new StoreLoadDataListener() {

						@Override
						public void onFinish() {
							progressDialogFragment.dismiss();
							performPayment(orderDetailsStore);

						}

						@Override
						public void onError() {
							progressDialogFragment.dismiss();
							UtilityFunctions.makeToast(getActivity(),
									R.string.general_error);

						}
					});
		} else {
			performPayment(orderDetailsStore);
		}

	}

	private void performPayment(OrderDetailsStore orderDetailsStore) {

		ZooZInvoice invoice = new ZooZInvoice();
		invoice.setInvoiceNumber(orderDetailsStore.getNumber());

		List<ShippingMethod> shippingMethodsList = orderDetailsStore
				.getShippingMethodsList();

		String invoiceAdditionalDetails = "";

		if (ConfigurationFile.getInstance().isLanguageConfigurable() == true) {
			invoiceAdditionalDetails += "-"
					+ ConfigurationFile.getInstance().getAppAddress();
		}
		if (shippingMethodsList != null && shippingMethodsList.size() > 0) {

			ShippingMethod currentShippingMethodChosen = shippingMethodsList
					.get(currentPosition);
			int shipId = currentShippingMethodChosen.getId();

			invoiceAdditionalDetails += "," + shipId + ",";
			if (isBranchRelevant())
				invoiceAdditionalDetails +=  + idOfChosenBranchName ;
			invoiceAdditionalDetails += "," +  ConfigurationFile.getInstance().getAppAddress();
		}

		invoice.setInvoiceAdditionalDetails(invoiceAdditionalDetails);

		List<LineItem> lineItemsList = orderDetailsStore.getLineItemsList();

		for (LineItem lineItem : lineItemsList) {
			String variantName = lineItem.getVariant().getName();
			int quantity = lineItem.getQuantity();
			double finalPrice = lineItem.getVariant().getPrice();
			int orderId = lineItem.getId();
			invoice.addItem(variantName, quantity, finalPrice, 0.0,
					Integer.toString(orderId), null);
		}

		Intent intent = new Intent(getActivity(), CheckoutActivity.class);
		// send merchant credential, app_key as given in the
		// registration
		intent.putExtra(CheckoutActivity.ZOOZ_APP_KEY, zoozId);

		intent.putExtra(CheckoutActivity.ZOOZ_AMOUNT, sumCost);

		intent.putExtra(CheckoutActivity.ZOOZ_CURRENCY_CODE, InitInfoStore
				.getInstance().getCurrency());// "USD"
		intent.putExtra(CheckoutActivity.ZOOZ_IS_SANDBOX, isSandBox);
		intent.putExtra(CheckoutActivity.ZOOZ_REQUIRE_ZIP_CODE, ((InitInfoStore
				.getInstance().isAppLanguageHebrew()) ? false : true));

		ShipAddress shipAddress = orderDetailsStore.getShipAddress();
		intent.putExtra(CheckoutActivity.ZOOZ_USER_FIRST_NAME,
				shipAddress.getFirstName());

		intent.putExtra(CheckoutActivity.ZOOZ_USER_LAST_NAME,
				shipAddress.getLastName());
		intent.putExtra(CheckoutActivity.ZOOZ_USER_EMAIL,
				orderDetailsStore.getEmail());
		intent.putExtra(CheckoutActivity.ZOOZ_USER_PHONE_NUMBER,
				shipAddress.getPhone());

		intent.putExtra(CheckoutActivity.ZOOZ_ADDRESS_BILLING_STREET,
				shipAddress.getAddress());
		intent.putExtra(CheckoutActivity.ZOOZ_ADDRESS_BILLING_CITY,
				shipAddress.getCity());
		// intent.putExtra(
		// CheckoutActivity.ZOOZ_ADDRESS_BILLING_STATE,
		// "ZOOZ_ADDRESS_BILLING_STATE");
		intent.putExtra(CheckoutActivity.ZOOZ_ADDRESS_BILLING_COUNTRY,
				shipAddress.getCountryId());

		String stateId = shipAddress.getStateId();
		if (stateId != null) {
			intent.putExtra(CheckoutActivity.ZOOZ_ADDRESS_BILLING_STATE,
					stateId);
		}

		intent.putExtra(CheckoutActivity.ZOOZ_ADDRESS_BILLING_ZIP_CODE,
				shipAddress.getZipcode());
		intent.putExtra(CheckoutActivity.ZOOZ_INVOICE, invoice);

		// start ZooZCheckoutActivity and wait to the activity
		// result.
		exitToPay = true;
		MximoFlurryAgent.logEvent(MximoFlurryAgent.ZOOZ_PAGE_VIEWED);

		getActivity().startActivityForResult(intent,
				MainActivity.ZOOZ_ACTIVITY_ID);

	}

	private boolean isBranchRelevant() {
		return ((shouldDonate()) || ((currentTextViewBranch) != null)
				&& (currentTextViewBranch.getVisibility() == View.VISIBLE));
	}

	private boolean shouldDonate() {
		boolean shouldDonate = Boolean
				.parseBoolean(getValueOfParamFromName("show_donations"));

		return shouldDonate;
	}

	/*
	 * Main purpose of this function is to make sure, that if a shipping method
	 * was chosen that has a branch, that a branch is actually chosen
	 */
	private boolean validateAndInitializeBranchDetails() {

		if (shouldDonate()) {
			if (branchName == null) {
				currentTextViewBranch
						.setBackgroundResource(R.drawable.variantoption_red);
				int[] location = new int[2];
				currentTextViewBranch.getLocationOnScreen(location);
				scrollViewOfEntireView.smoothScrollTo(0, location[1]);

				return false;
			}
		} else {
			if ((currentTextViewBranch != null)
					&& (currentTextViewBranch.getVisibility() == View.VISIBLE)
					&& (branchName == null)) {
				currentTextViewBranch
						.setBackgroundResource(R.drawable.variantoption_red);
				int[] location = new int[2];
				currentTextViewBranch.getLocationOnScreen(location);
				scrollViewOfEntireView.smoothScrollTo(0, location[1]);

				return false;
			}

		}

		return true;
	}

	private void updateShippingMethod(double newShippingFee) {

		currentShippingFee = newShippingFee;

		shippingCost.setText(generateProperStringForPrice(currentShippingFee));

		updateFinalPrice(totalCostOfAllProducts + currentShippingFee);

	}

	private void updateCostsAfterPromotionSucceeded(double discount) {

		// discount will always be a negative number

		totalCostOfAllProducts = totalCostOfAllProducts + discount;

		updateFinalPrice(totalCostOfAllProducts + currentShippingFee);

		TextView textViewPromotion = (TextView) viewOfFragment
				.findViewById(R.id.textview_promotion);

		textViewPromotion.setText(generateProperStringForPrice(discount));

	}

	private void configureCheckboxEditText(int idOfCheckBox, int IdOfEditText) {
		final CheckBox checkBox = (CheckBox) viewOfFragment
				.findViewById(idOfCheckBox);
		final EditText editText = (EditText) viewOfFragment
				.findViewById(IdOfEditText);

		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked)
					editText.setVisibility(View.VISIBLE);
				else
					editText.setVisibility(View.GONE);

			}
		});
	};

	public class ShippingMethodsAdapter extends ArrayAdapter<ShippingMethod> {

		List<ShippingMethod> shippingMethodsList;
		LayoutInflater inflator;
		int resource;
		ArrayList<RadioButton> radioButtonList;

		public ShippingMethodsAdapter(Context context, int resource,

		List<ShippingMethod> shippingMethodsList, LayoutInflater inflator) {
			super(context, resource, shippingMethodsList);
			this.shippingMethodsList = shippingMethodsList;
			this.inflator = inflator;
			this.resource = resource;
			radioButtonList = new ArrayList<RadioButton>();

		}

		@Override
		public int getCount() {
			return shippingMethodsList.size();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			View row = convertView;
			RecordHolder holder = null;

			if (row == null) {

				row = inflator.inflate(resource, parent, false);

				holder = new RecordHolder();
				holder.textTitle = (TextView) row
						.findViewById(R.id.shipping_method_title);
				holder.textPrice = (TextView) row
						.findViewById(R.id.shipping_method_price);
				holder.radioButton = (RadioButton) row
						.findViewById(R.id.checkbox_shipment);
				holder.textBranch = (TextView) row
						.findViewById(R.id.shipping_choose_branch);
				radioButtonList.add(holder.radioButton);
				if (position == 0) {
					currentPosition = 0;
					holder.radioButton.setChecked(true);

					if (!shouldDonate())
						currentTextViewBranch = holder.textBranch;
				}

				row.setTag(holder);
			} else {
				holder = (RecordHolder) row.getTag();
			}

			if (shippingMethodsList.get(position).getSelectBranch()) {
				holder.textBranch.setVisibility(View.VISIBLE);

				holder.textBranch.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						fragmentRoot.openFragmnet(new StoresNamesFragment(),
								null);

					}
				});
				if (branchName != null) {
					currentTextViewBranch = holder.textBranch;
					holder.textBranch.setText(branchName);
					updateDataFromRadioButton(holder.radioButton, position);

				}

			}

			holder.textTitle.setText(shippingMethodsList.get(position)
					.getName());
			holder.textPrice
					.setText(generateProperStringForPrice(shippingMethodsList
							.get(position).getCost()));

			holder.radioButton.setTag(holder.textBranch);
			holder.radioButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (!shouldDonate())
						currentTextViewBranch = (TextView) v.getTag();
					updateDataFromRadioButton(v, position);

				}
			});
			return row;
		}

		private void updateDataFromRadioButton(View v, int position) {
			currentPosition = position;
			for (RadioButton radioButton : radioButtonList)
				radioButton.setChecked(false);
			((RadioButton) v).setChecked(true);
			updateShippingMethod(shippingMethodsList.get(position).getCost());
		}
	}

	static class RecordHolder {
		TextView textTitle;
		TextView textPrice;
		TextView textBranch;
		RadioButton radioButton;

	}

}
