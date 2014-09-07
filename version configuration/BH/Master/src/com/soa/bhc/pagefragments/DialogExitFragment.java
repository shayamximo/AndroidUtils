package com.soa.bhc.pagefragments;

import com.soa.bhc.R;
import com.soa.bhc.json.stores.InitInfoStore;

public class DialogExitFragment extends MximoDialog {

	private void finishDialogFragment() {
		getActivity().getFragmentManager().beginTransaction()
				.remove(DialogExitFragment.this).commit();
	}

	@Override
	protected String getTextForTitle() {
		return InitInfoStore.getInstance().getName();

	}

	@Override
	protected String getTextForText() {
		return getResources().getString(R.string.exit_text);
	}

	@Override
	protected void doOnProceed() {
		finishDialogFragment();

	}

	@Override
	protected void doOnCancel() {
		finishDialogFragment();

	}

	@Override
	protected int getColorOfBackground() {
		return R.color.white;
	}

	@Override
	protected int getStringOfCancelId() {

		return R.string.cancel;
	}

	@Override
	protected int getStringOfProceedId() {
		return R.string.exit;
	}

}
