package com.parqueteam.pagefragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.parqueteam.R;
import com.parqueteam.json.stores.InitInfoStore;
import com.parqueteam.json.stores.RegisterStore;
import com.parqueteam.json.stores.StoreLoadDataListener;
import com.parqueteam.utils.ProgressDialogFragment;
import com.parqueteam.utils.SharedPreferencesController;
import com.parqueteam.utils.UtilityFunctions;
import com.urbanairship.push.PushManager;

public class UserRegisterViewController extends FragmentPageBase {

	@Override
	protected int getLayoutID() {

		return R.layout.register_layout;
	}

	@Override
	protected void initializeActionBar() {
		actionBarContorller.textViewMainTitle.setText(getResources().getString(
				R.string.sign_up));

	}
	
	@Override
	public void onResume() {

		super.onResume();
		fragmentRoot.hideTabs();
	}

	@Override
	public void onStop() {
		
		super.onStop();
		fragmentRoot.showTabs();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		List<String> stringList = getValueListOfParamFromName("row");

		final List<SingleRow> rowList = createRowList(stringList);

		Button signUpButton = (Button) viewOfFragment
				.findViewById(R.id.button_sign_up);
		setViewToBrandColor(signUpButton);

		signUpButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				UtilityFunctions.hideKeyboard(getActivity());

				ArrayList<EditText> emptyMandatoryEditTextList = new ArrayList<EditText>();

				for (SingleRow row : rowList) {
					if (row.isFieldEmptyAndMandatory())
						emptyMandatoryEditTextList.add(row.editText);
				}

				if (emptyMandatoryEditTextList.isEmpty()) {

					ArrayList<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
					for (SingleRow row : rowList) {
						String textFilledByUser = row.getSendText();

						String fieldName = row.textToPostParams;
						if (fieldName != null && (textFilledByUser != null)
								&& (textFilledByUser.length() > 0)) {
							fieldName = "user[" + fieldName + "]";

							NameValuePair nvp = new BasicNameValuePair(
									fieldName, textFilledByUser);
							nameValuePairList.add(nvp);
						}

					}

					// send promo info
					CheckBox sendPromoCheckBox = ((CheckBox) viewOfFragment
							.findViewById(R.id.checkbox_send_promo));
					nameValuePairList.add(new BasicNameValuePair(
							"user[get_promos]", Boolean
									.toString(sendPromoCheckBox.isChecked())));

					// send apid/device token
					String apid = PushManager.shared().getAPID();
					nameValuePairList.add(new BasicNameValuePair(
							"user[device_token]", apid));

					RegisterStore registerStore = new RegisterStore(
							nameValuePairList);

					final ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
					progressDialogFragment.show(getFragmentManager(),
							"MyProgressDialog");
					registerStore
							.loadDataIfNotInitialized(new StoreLoadDataListener() {

								@Override
								public void onFinish() {
									progressDialogFragment.dismiss();
									SharedPreferencesController
											.saveUserRegistered();
									String appname = InitInfoStore
											.getInstance().getName();
									final AlertDialog.Builder builder = new AlertDialog.Builder(
											getActivity());
									builder.setTitle(appname);
									builder.setMessage(
											getResources()
													.getString(
															R.string.registration_successful))
											.setCancelable(false)
											.setPositiveButton(
													"OK",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int id) {
															dialog.dismiss();

															fragmentRoot
																	.onBackPressed();

														}
													});

									getActivity().runOnUiThread(new Runnable() {

										@Override
										public void run() {
											AlertDialog alert = builder
													.create();
											alert.show();

										}
									});

								}

								@Override
								public void onError() {
									progressDialogFragment.dismiss();
									UtilityFunctions.makeToast(getActivity(),
											R.string.general_error);

								}
							});

				} else {

					for (EditText emptyEditText : emptyMandatoryEditTextList) {
						emptyEditText
								.setBackgroundResource(R.drawable.variantoption_red);
					}
				}

			}
		});

		super.onActivityCreated(savedInstanceState);
	}

	private List<SingleRow> createRowList(List<String> rowValueList) {

		LinearLayout linearLayoutOfEditText = (LinearLayout) viewOfFragment
				.findViewById(R.id.edit_text_layout_in_register);
		ArrayList<SingleRow> retRowList = new ArrayList<UserRegisterViewController.SingleRow>();
		for (String string : rowValueList) {
			retRowList.add(new SingleRow(string, linearLayoutOfEditText));
		}

		return retRowList;
	}

	public class SingleRow {
		public String textInEditText;
		public String textToPostParams;
		public EditText editText;
		public Boolean isFemale = null;
		public RelativeLayout layoutOfGender;
		private String dateOfBirth = null;

		public SingleRow(String rowName, LinearLayout linearLayoutOfEditText) {

			boolean isAppHebrew = InitInfoStore.getInstance()
					.isAppLanguageHebrew();
			RegisterRow rr = RegisterRow.valueOf(rowName.replace(" ", "_")
					.toUpperCase(Locale.getDefault()));

			LinearLayout layout = (LinearLayout) inflator.inflate(
					R.layout.item_in_register_list, linearLayoutOfEditText,
					false);

			editText = (EditText) layout
					.findViewById(R.id.edittext_in_register_list);

			layoutOfGender = (RelativeLayout) layout
					.findViewById(R.id.gender_layout);

			if (InitInfoStore.getInstance().isAppLanguageHebrew())
				editText.setGravity(Gravity.RIGHT);
			linearLayoutOfEditText.addView(layout);

			switch (rr) {
			case FIRST_NAME:
				if (isAppHebrew)
					textInEditText = "* "
							+ getResources().getString(
									R.string.register_first_name);
				textToPostParams = "firstname";
				break;

			case LAST_NAME:
				if (isAppHebrew)
					textInEditText = "* "
							+ getResources().getString(
									R.string.register_last_name);
				textToPostParams = "lastname";

				break;
			case EMAIL:
				editText.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
						| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
				if (isAppHebrew)
					textInEditText = "* "
							+ getResources().getString(R.string.register_email);
				textToPostParams = "email";

				break;
			case GENDER:

				setGenderLayout(layout);
				break;
			case PHONE_NUMBER:
				editText.setInputType(InputType.TYPE_CLASS_NUMBER);
				if (isAppHebrew)
					textInEditText = getResources().getString(
							R.string.register_phone_number);
				textToPostParams = "phone";

				break;
			case DATE_OF_BIRTH:
				editText.setFocusable(false);
				if (isAppHebrew)
					textInEditText = getResources().getString(
							R.string.register_date_of_birth);
				textToPostParams = "date_of_birth";
				setDateDialog();

				break;
			case STREET:
				if (isAppHebrew)
					textInEditText = getResources().getString(
							R.string.register_street);
				textToPostParams = null;

				break;
			case CITY:
				if (isAppHebrew)
					textInEditText = getResources().getString(
							R.string.register_city);
				textToPostParams = null;
				break;

			default:
				break;
			}
			
			if (!InitInfoStore.getInstance().isAppLanguageHebrew())
				textInEditText = rowName;
			
			editText.setHint(textInEditText);

		}

		private void setDateDialog() {

			editText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					final Calendar c = Calendar.getInstance();
					int mYear = c.get(Calendar.YEAR);
					int mMonth = c.get(Calendar.MONTH);
					int mDay = c.get(Calendar.DAY_OF_MONTH);

					DatePickerDialog dpd = new DatePickerDialog(getActivity(),
							new DatePickerDialog.OnDateSetListener() {

								@Override
								public void onDateSet(DatePicker view,
										int year, int monthOfYear,
										int dayOfMonth) {

									dateOfBirth = dayOfMonth + "/"
											+ (monthOfYear + 1) + "/" + year;
									editText.setText(dateOfBirth);
								}
							}, mYear, mMonth, mDay);
					dpd.show();

				}
			});

		}

		@SuppressLint("NewApi")
		private void setGenderLayout(LinearLayout layout) {

			layoutOfGender.setVisibility(View.VISIBLE);
			editText.setVisibility(View.GONE);

			final ImageButton female = (ImageButton) layoutOfGender
					.findViewById(R.id.female_button);
			final ImageButton male = (ImageButton) layoutOfGender
					.findViewById(R.id.male_button);

			isFemale = true;

			female.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					isFemale = true;
					if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {

						female.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.female_sel));
						male.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.male));
					} else {
						female.setBackground(getResources().getDrawable(
								R.drawable.female_sel));
						male.setBackground(getResources().getDrawable(
								R.drawable.male));
					}

				}
			});

			male.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					isFemale = false;
					if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {

						male.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.male_sel));
						female.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.female));
					} else {
						male.setBackground(getResources().getDrawable(
								R.drawable.male_sel));
						female.setBackground(getResources().getDrawable(
								R.drawable.female));
					}

				}
			});

			textToPostParams = "gender";
		}

		public String getSendText() {
			if (editText.getVisibility() == View.VISIBLE) {

				if (dateOfBirth != null)
					return dateOfBirth;
				return editText.getText().toString();
			}

			if (layoutOfGender.getVisibility() == View.VISIBLE) {
				if (isFemale)
					return "female";
				else
					return "male";

			}
			return null;
		}

		public boolean isFieldEmptyAndMandatory() {
			if (editText.getVisibility() == View.VISIBLE) {
				boolean isFieldMandatory = editText.getHint().toString()
						.contains("*");
				if ((textInEditText == null || textInEditText.equals(""))
						&& isFieldMandatory)
					return true;
			}
			return false;
		}
	}

	public enum RegisterRow {

		FIRST_NAME, LAST_NAME, EMAIL, GENDER, PHONE_NUMBER, DATE_OF_BIRTH, STREET, CITY
	}

}
