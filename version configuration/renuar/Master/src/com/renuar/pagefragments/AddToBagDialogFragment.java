package com.renuar.pagefragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.renuar.R;
import com.renuar.json.stores.InitInfoStore;

public abstract class AddToBagDialogFragment extends DialogFragment {

	private Button btnCheckout;
	private Button btnContinue;
	private Button btnCancel;
	private TextView tvAppName;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = new Dialog(getActivity());
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.lyt_custom_dialog);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));

		btnCheckout = (Button) dialog
				.findViewById(R.id.cust_dialog_btnCheckout);
		btnCheckout.setBackgroundColor(Color.parseColor(InitInfoStore
				.getInstance().getBrandColor()));
		btnContinue = (Button) dialog
				.findViewById(R.id.cust_dialog_btnContinue);
		btnCancel = (Button) dialog.findViewById(R.id.cust_dialog_btnCancel);
		tvAppName = (TextView) dialog.findViewById(R.id.cust_dialog_tvAppName);

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		btnContinue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onButtonContinueClicked();

			}
		});

		btnCheckout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onButtonCheckoutClicked();

			}
		});

		tvAppName.setText(InitInfoStore.getInstance().getName());

		dialog.show();

		return dialog;
	}

	public abstract void onButtonContinueClicked();

	public abstract void onButtonCheckoutClicked();
}
