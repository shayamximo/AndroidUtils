package com.renuar.pagefragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.renuar.R;
import com.renuar.json.Taxon;
import com.renuar.json.stores.TaxonomiesStore;

public class FilterFragment extends FragmentPageBase {

	private List<CellInfo> cellInfoList;

	@Override
	protected int getLayoutID() {
		return R.layout.filter_layout;
	}

	@Override
	protected void initializeActionBar() {
		actionBarContorller.textViewMainTitle.setText(getResources().getString(
				R.string.refine));

		actionBarContorller
				.setAcceptDrawable(actionBarContorller.imageViewOnRightStandalone);
		makeViewsVisible(actionBarContorller.imageViewOnRightStandalone);
		actionBarContorller.imageViewOnRightStandalone
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						ArrayList<Integer> chosenTaxonIds = new ArrayList<Integer>();
						for (CellInfo cellInfo : cellInfoList) {
							if (cellInfo.isChosen)
								chosenTaxonIds.add(cellInfo.taxonId);

						}

						fragmentRoot.goToPreviousFragment(null, null, null, 0,
								FragmentRoot.KEY_FILTERED_TAXONS,
								chosenTaxonIds);

					}
				});

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		ArrayList<Integer> placeHolders = getArguments().getIntegerArrayList(
				FragmentRoot.TAXON_ADDRESS_KEY);

		Taxon mainTaxon = TaxonomiesStore.getInstance().getTaxonsByPlaceHolder(
				placeHolders);

		ListView listview = (ListView) viewOfFragment
				.findViewById(R.id.filter_listview);

		cellInfoList = createCellInfoListFromTaxons(mainTaxon.getTaxons());

		FilterAdapter filterAdapter = new FilterAdapter(getActivity(),
				R.layout.item_in_filter_list, cellInfoList);
		listview.setAdapter(filterAdapter);

	}

	private List<CellInfo> createCellInfoListFromTaxons(List<Taxon> taxonsList) {

		List<CellInfo> cellInfoList = new ArrayList<CellInfo>();
		for (Taxon taxon : taxonsList) {

			if (taxon.getProducts() != null && taxon.getProducts().size() > 0) {
				CellInfo cellInfo = new CellInfo(taxon.getName(), taxon.getId());
				cellInfoList.add(cellInfo);
			}

		}

		return cellInfoList;

	}

	public class CellInfo {

		public CellInfo(String taxonName, int taxonId) {

			this.taxonName = taxonName;
			this.taxonId = taxonId;
			isChosen = false;
		}

		public String taxonName;
		public int taxonId;
		public boolean isChosen;

	}

	public class FilterAdapter extends ArrayAdapter<CellInfo> {

		private int resourceId;
		private List<CellInfo> cellInfoList;

		public FilterAdapter(Context context, int resourceId,
				List<CellInfo> cellInfoList) {
			super(context, resourceId, cellInfoList);
			this.resourceId = resourceId;
			this.cellInfoList = cellInfoList;
		}

		private void configureVisibilityOfCell(ImageView imageView,
				int position, boolean toggle) {

			Boolean isChosen = cellInfoList.get(position).isChosen;

			if (isChosen) {
				if (toggle)
					imageView.setVisibility(View.GONE);
				else
					imageView.setVisibility(View.VISIBLE);
			}

			else {
				if (toggle)
					imageView.setVisibility(View.VISIBLE);
				else
					imageView.setVisibility(View.GONE);
			}

			if (toggle)
				cellInfoList.get(position).isChosen = (!isChosen);

		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View row = convertView;
			final Holder holder;
			if (row == null) {
				row = inflator.inflate(resourceId, parent, false);
				holder = new Holder();
				holder.textView = (TextView) row
						.findViewById(R.id.filter_textview_title);
				holder.imageView = (ImageView) row
						.findViewById(R.id.filter_imageview_check);
				row.setTag(holder);
			} else {
				holder = (Holder) row.getTag();
			}

			holder.textView.setText(cellInfoList.get(position).taxonName);

			configureVisibilityOfCell(holder.imageView, position, false);

			row.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View cell) {

					configureVisibilityOfCell(holder.imageView, position, true);

				}
			});

			return row;
		}

		public class Holder {
			ImageView imageView;
			TextView textView;
		}

	}

}
