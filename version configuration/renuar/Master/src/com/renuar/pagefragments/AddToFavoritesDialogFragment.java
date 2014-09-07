package com.renuar.pagefragments;

import com.renuar.R;
import com.renuar.json.stores.InitInfoStore;

public class AddToFavoritesDialogFragment extends MximoDialog {

	private void finishDialogFragment() {
		getActivity().getFragmentManager().beginTransaction()
				.remove(AddToFavoritesDialogFragment.this).commit();
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
		return R.string.close;
	}

	@Override
	protected int getStringOfProceedId() {
		return R.string.go_to_favorites;
	}

	@Override
	protected String getTextForTitle() {
		return InitInfoStore.getInstance().getName();
	}

	@Override
	protected String getTextForText() {
		return getResources().getString(R.string.add_to_favorites_message);
	}

}
