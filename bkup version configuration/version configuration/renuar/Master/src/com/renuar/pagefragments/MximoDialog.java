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

public abstract class MximoDialog extends DialogFragment {

	public interface IOnUserChooseOption {
		public void onCancel();

		public void onProceed();
	}

	protected IOnUserChooseOption iOnUserChooseOption = null;

	public void setiOnUserChooseOption(IOnUserChooseOption iOnUserChooseOption) {
		this.iOnUserChooseOption = iOnUserChooseOption;
	}

	protected TextView titleToDisplay;
	protected TextView textToDisplay;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		final Dialog dialog = new Dialog(getActivity());
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.mximo_dialog);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		dialog.show();

		textToDisplay = (TextView) dialog.findViewById(R.id.text_of_dialog);
		textToDisplay.setText(getTextForText());

		titleToDisplay = (TextView) dialog.findViewById(R.id.title_of_dialog);
		titleToDisplay.setText(getTextForTitle());

		Button cancel = (Button) dialog.findViewById(R.id.cancel);
		cancel.setText(getResources().getString(getStringOfCancelId()));

		Button proceed = (Button) dialog.findViewById(R.id.proceed);
		proceed.setText(getResources().getString(getStringOfProceedId()));

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doOnCancel();
				if (iOnUserChooseOption != null)
					iOnUserChooseOption.onCancel();
			}
		});

		proceed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doOnProceed();
				if (iOnUserChooseOption != null)
					iOnUserChooseOption.onProceed();
			}
		});

		dialog.findViewById(R.id.layout_of_entire_dialog).setBackgroundColor(
				getResources().getColor(getColorOfBackground()));

		return dialog;
	}

	protected abstract String getTextForTitle();

	protected abstract String getTextForText();

	protected abstract void doOnProceed();

	protected abstract void doOnCancel();

	protected abstract int getColorOfBackground();

	protected abstract int getStringOfCancelId();

	protected abstract int getStringOfProceedId();

}
