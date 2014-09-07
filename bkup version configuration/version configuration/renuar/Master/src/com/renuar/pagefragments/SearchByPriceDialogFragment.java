package com.renuar.pagefragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;

import com.renuar.R;

public class SearchByPriceDialogFragment extends DialogFragment implements
		NumberPicker.OnValueChangeListener {
	final String[] numsFrom = new String[50];
	final String[] numsTo = new String[50];

	private IOnUserChooseRange iOnUserChooseRange;

	public void setiOnUserChooseRange(IOnUserChooseRange iOnUserChooseRange) {
		this.iOnUserChooseRange = iOnUserChooseRange;
	}

	public interface IOnUserChooseRange {
		public void onUserChoose(int from, int to);
	}

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = new Dialog(getActivity());
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		dialog.setContentView(R.layout.budget_dialog);

		Button btnSearch = (Button) dialog.findViewById(R.id.btnSearch);
		Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

		final NumberPicker npFrom = configureNumberPicker(numsFrom,
				R.id.numberPicker1, 0, dialog);

		final NumberPicker npTo = configureNumberPicker(numsTo,
				R.id.numberPicker2, 1, dialog);

		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				finishDialogFragment();
				int from = Integer.valueOf(numsFrom[npFrom.getValue()]);
				int to = Integer.valueOf(numsTo[npTo.getValue()]);
				if (iOnUserChooseRange != null)
					iOnUserChooseRange.onUserChoose(from, to);
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finishDialogFragment();
			}
		});

		dialog.show();
		return dialog;
	}

	private NumberPicker configureNumberPicker(String[] nums, int id,
			int minimumValue, Dialog dialog) {
		// Create the array of numbers that will populate the numberpicker from

		for (int i = 0; i < nums.length; i++) {
			nums[i] = Integer.toString((i + minimumValue) * 50);
		}

		// Set the max and min values of the numberpicker, and give it the
		// array of numbers created above to be the displayed numbers
		final NumberPicker np = (NumberPicker) dialog.findViewById(id);

		np.setMaxValue(50);
		np.setMinValue(0);
		np.setWrapSelectorWheel(false);
		np.setDisplayedValues(nums);
		np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		// np.setOnValueChangedListener(this);
		return np;
	}

	private void finishDialogFragment() {
		getActivity().getFragmentManager().beginTransaction()
				.remove(SearchByPriceDialogFragment.this).commit();
	}
}
