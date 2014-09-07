package com.parqueteam.pagefragments;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.parqueteam.App;
import com.parqueteam.GlobalConstants;
import com.parqueteam.R;
import com.parqueteam.json.Product;
import com.parqueteam.json.stores.SearchStore;
import com.parqueteam.json.stores.StoreLoadDataListener;
import com.parqueteam.pagefragments.SearchByPriceDialogFragment.IOnUserChooseRange;
import com.parqueteam.utils.MximoFlurryAgent;
import com.parqueteam.utils.ProgressDialogFragment;

public class SearchViewController extends FragmentPageBase implements
		OnClickListener {

	@Override
	protected int getLayoutID() {
		// TODO Auto-generated method stub
		return R.layout.search_layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		MximoFlurryAgent.logEvent(MximoFlurryAgent.USER_PERFORMED_SEARCH);
		viewOfFragment.findViewById(R.id.search_top_rated).setOnClickListener(
				this);
		viewOfFragment.findViewById(R.id.search_sale).setOnClickListener(this);
		viewOfFragment.findViewById(R.id.search_budget)
				.setOnClickListener(this);

		final EditText searchByKeyWords = (EditText) viewOfFragment
				.findViewById(R.id.search_keywords);

		setEditTextForSearch(searchByKeyWords);

	}

	@Override
	protected void initializeActionBar() {

		actionBarContorller.textViewMainTitle.setText(getResources().getString(
				R.string.search));
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		SearchStore searchStore = null;
		switch (id) {
		case R.id.search_top_rated:
			searchStore = new SearchStore(SearchStore.SEARCH_TYPE.RATING);
			startSearchResultsFragment(searchStore);
			break;
		case R.id.search_sale:
			searchStore = new SearchStore(SearchStore.SEARCH_TYPE.SALE);
			startSearchResultsFragment(searchStore);
			break;
		case R.id.search_budget:
			SearchByPriceDialogFragment searchByPriceDialogFragment = new SearchByPriceDialogFragment();
			searchByPriceDialogFragment
					.setiOnUserChooseRange(new IOnUserChooseRange() {

						@Override
						public void onUserChoose(int from, int to) {
							SearchStore searchStore = new SearchStore(from, to);
							startSearchResultsFragment(searchStore);

						}
					});
			searchByPriceDialogFragment.setCancelable(false);
			searchByPriceDialogFragment.show(
					getActivity().getFragmentManager(),
					GlobalConstants.EMPTY_STRING);
			break;

		default:
			break;
		}

	}

}
