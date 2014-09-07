package com.renuar.pagefragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.renuar.R;
import com.renuar.json.Params;
import com.renuar.json.Taxon;
import com.renuar.json.helpers.CatgoryAttributes;
import com.renuar.json.helpers.CollapsibleCategoryProperties;
import com.renuar.json.stores.InitInfoStore;
import com.renuar.json.stores.TaxonomiesStore;
import com.renuar.utils.UtilityFunctions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class FragmentHomeScreenCollapsibleCategory extends FragmentHomeScreen {

	private ArrayList<ViewGroup> viewGroupsOfTaxonList;

	@Override
	protected int getLayoutID() {

		return R.layout.collapsible_category_view_layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		viewGroupsOfTaxonList = new ArrayList<ViewGroup>();

		String collapsibleCategoryKey = InitInfoStore.getInstance()
				.getCollapsibleCategoriesStringKey();

		if (collapsibleCategoryKey == null) {
			List<String> topLevelStringsOfTaxamonies = TaxonomiesStore
					.getInstance().getTaxonsTopLevelNamesByIndex().get(0);

			List<String> tempIfFromLook = TaxonomiesStore.getInstance()
					.getTaxonsTopLevelNamesByIndex().get(1);
			if (tempIfFromLook != null)
				topLevelStringsOfTaxamonies.addAll(tempIfFromLook);
			collapsibleCategoryKey = "";

			for (String currentCategoryString : topLevelStringsOfTaxamonies) {
				collapsibleCategoryKey += currentCategoryString + ",";
			}
		}

		ArrayList<CollapsibleCategoryProperties> ccpList = TaxonomiesStore
				.getInstance().getCollapsibleCategoryPropertiesList(
						collapsibleCategoryKey);

		ListView listView = (ListView) viewOfFragment
				.findViewById(android.R.id.list);
		View header = inflator.inflate(R.layout.header_for_collapsible_layout,
				listView, false);

		View searchButton = header.findViewById(R.id.home_Btn_search);
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fragmentRoot.openSearchFragment(null);
			}

		});

		EditText searchEditText = (EditText) header
				.findViewById(R.id.home_edt_search);
		setEditTextForSearch(searchEditText);

		if (!InitInfoStore.getInstance().shouldHideSearchBarInCollapsibleView())
			listView.addHeaderView(header);
		listView.setAdapter(new CollapsibleRowsAdapter(getActivity(),
				R.layout.row_in_collapsible_layout, ccpList));
	}

	private class CollapsibleRowsAdapter extends
			ArrayAdapter<CollapsibleCategoryProperties> {

		List<CollapsibleCategoryProperties> ccpForCollapsibleView;
		int resourceId;

		public CollapsibleRowsAdapter(Context context, int resourceId,
				List<CollapsibleCategoryProperties> ccpForCollapsibleView) {
			super(context, resourceId, ccpForCollapsibleView);
			this.ccpForCollapsibleView = ccpForCollapsibleView;
			this.resourceId = resourceId;
		}

		@Override
		public View getView(final int position, View convertView,
				final ViewGroup parent) {
			View row = convertView;
			final ViewHolder viewHolder;
			if (row == null) {
				row = inflator.inflate(resourceId, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.imageView = (ImageView) row
						.findViewById(R.id.image_view_in_collapsible_row);
				viewHolder.linearLayout = (LinearLayout) row
						.findViewById(R.id.layout_of_sub_categories);
				viewHolder.linearLayout.setVisibility(View.GONE);
				viewHolder.textView = (TextView) row
						.findViewById(R.id.textview_in_row_collapsible);

				// RelativeLayout.LayoutParams lp =
				// (android.widget.RelativeLayout.LayoutParams)
				// viewHolder.imageView
				// .getLayoutParams();
				// lp.height = (int) TypedValue.applyDimension(
				// TypedValue.COMPLEX_UNIT_DIP, getRowHeight(),
				// getResources().getDisplayMetrics());

				row.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) row.getTag();
			}

			Taxon taxon = ccpForCollapsibleView.get(position).getTaxon();

			if (InitInfoStore.getInstance().shouldHideLabels()) {
				viewHolder.textView.setVisibility(View.GONE);
			} else {
				viewHolder.textView.setText(taxon.getName());

				final List<Params> categoriesParams = InitInfoStore
						.getInstance().getCategoryParams();
				if (categoriesParams != null) {
					CatgoryAttributes catgoryAttributes = new CatgoryAttributes(
							categoriesParams, getActivity());
					viewHolder.textView.setTypeface(catgoryAttributes
							.getFont());
				}

			}

			imageLoader.displayImage(taxon.getImageUrl(), viewHolder.imageView,
					options, new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String imageUri, View view) {

						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {

						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {

						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {

						}
					});

			final CollapsibleCategoryProperties ccp = ccpForCollapsibleView
					.get(position);
			if (ccp.isShouldOpenList()) {

				if (!(viewGroupsOfTaxonList.contains(viewHolder.linearLayout)))
					viewGroupsOfTaxonList.add(viewHolder.linearLayout);
				if (ccp.isShowingSubCategories())
					viewHolder.linearLayout.setVisibility(View.VISIBLE);
				else
					viewHolder.linearLayout.setVisibility(View.GONE);
			} else {
				viewHolder.linearLayout.setVisibility(View.GONE);
			}

			viewHolder.imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					final Taxon taxon = ccpForCollapsibleView.get(position)
							.getTaxon();
					if (ccp.isShouldOpenList()) {

						Boolean isListShowing = ccpForCollapsibleView.get(
								position).isShowingSubCategories();
						if (isListShowing) {
							ccp.setShowingSubCategories(false);
							viewHolder.linearLayout.setVisibility(View.GONE);
						} else {

							for (ViewGroup vg : viewGroupsOfTaxonList)
								vg.setVisibility(View.GONE);

							for (CollapsibleCategoryProperties tempCCP : ccpForCollapsibleView)
								tempCCP.setShowingSubCategories(false);

							ccp.setShowingSubCategories(true);

							viewHolder.linearLayout.setVisibility(View.VISIBLE);

							viewHolder.linearLayout.removeAllViews();
							List<Taxon> taxonList = ccp.getTaxon().getTaxons();
							int i = 0;
							for (Taxon currentTaxon : taxonList) {
								View subCategoryView = inflator
										.inflate(
												R.layout.sub_category_in_collapsible_row,
												viewHolder.linearLayout, false);
								((TextView) subCategoryView
										.findViewById(R.id.text_in_sub_category))
										.setText(currentTaxon.getName());

								viewHolder.linearLayout
										.addView(subCategoryView);

								subCategoryView.setTag(Integer.valueOf(i));
								subCategoryView
										.setOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View textView) {
												final ArrayList<Integer> placeHolders = new ArrayList<Integer>(
														ccpForCollapsibleView
																.get(position)
																.getPlaceholders());
												placeHolders
														.add((Integer) textView
																.getTag());
												final Taxon taxonSon = TaxonomiesStore
														.getInstance()
														.getTaxonsByPlaceHolder(
																placeHolders);

												fragmentRoot.onCategoryChosen(
														placeHolders, null,
														taxonSon.getName(),
														false);

											}
										});
								i++;

							}

							viewHolder.linearLayout.post(new Runnable() {

								@Override
								public void run() {
									((ListView) parent)
											.smoothScrollToPositionFromTop(
													position + 1, 0);

								}
							});

						}

					} else {

						fragmentRoot.onCategoryChosen(ccp.getPlaceholders(),
								null, taxon.getName(), false);

					}

				}
			});

			return row;
		}

		public class ViewHolder {
			ImageView imageView;
			LinearLayout linearLayout;
			TextView textView;
		}

		public int getRowHeight() {
			List<Params> paramsList = InitInfoStore.getInstance()
					.getCategoryParams();

			for (Params params : paramsList) {
				if (params.getName().equals("row_height")) {
					int rowHeight = Integer.parseInt(params.getValue());
					if (UtilityFunctions.isTablet(getActivity()))
						return (2 * rowHeight);
					else
						return rowHeight;
				}

			}
			return 140;
		}

	}

	@Override
	public void onStart() {
		enableDrawer();
		super.onStart();
	}

	@Override
	public void onPause() {
		disableDrawer();
		super.onPause();
	}

	public void disableDrawer() {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				fragmentRoot.getMainActivity().disableDrawer();

			}
		});

	}

	public void enableDrawer() {
		if (InitInfoStore.getInstance().isSlidingMenu()) {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					fragmentRoot.getMainActivity().enableDrawer();

				}
			});
		}

	}
}
