package com.renuar.pagefragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.renuar.R;
import com.renuar.json.Taxon;
import com.renuar.json.stores.TaxonomiesStore;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class CategoryGridViewController extends FragmentPageBase {

	private ArrayList<Integer> placeHolders = null;

	private GridView gridView = null;

	@Override
	protected int getLayoutID() {
		return R.layout.ac_image_grid;
	}

	@Override
	protected void initializeActionBar() {
		String title = getArguments()
				.getString(FragmentRoot.CATEGORY_TITLE_KEY);
		actionBarContorller.textViewMainTitle.setText(title);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Bundle args = getArguments();

		placeHolders = args.getIntegerArrayList(LIST_OF_PLACEHOLDERS_KEY);
		final Taxon taxon = TaxonomiesStore.getInstance()
				.getTaxonsByPlaceHolder(placeHolders);

		gridView = (GridView) getView().findViewById(R.id.gridview);
		gridView.setNumColumns(2);
		gridView.setBackgroundColor(getResources().getColor(R.color.white));
		gridView.setAdapter(new GridAdapter(getActivity(),
				R.layout.item_category_grid_view_controller, taxon.getTaxons()));
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Integer localizedPosition = getLocalizedPositionInGrid(
						taxon.getTaxons(), position);
				fragmentRoot.onCategoryChosen(placeHolders, localizedPosition,
						taxon.getTaxons().get(localizedPosition).getName(),
						false);

			}
		});

		super.onActivityCreated(savedInstanceState);
	}

	boolean isEven(double num) {
		return ((num % 2) == 0);
	}

	// function for "localization" for parquetim, if this
	// cotroller will ever be used for english language app, will need to not do
	// this for english.

	private int getLocalizedPositionInGrid(List<Taxon> taxonList, int position) {

		int sizeOfTaxonList = taxonList.size();
		if ((position == (sizeOfTaxonList - 1)) && ((isEven(position))))
			return position;

		if (isEven(position)) {
			return position + 1;
		}

		else {
			return position - 1;
		}

	}

	public class GridAdapter extends ArrayAdapter<Taxon> {

		private List<Taxon> taxonList;
		int resourceId;

		public GridAdapter(Context context, int resourceId,
				List<Taxon> taxonList) {
			super(context, resourceId, taxonList);
			this.taxonList = taxonList;
			this.resourceId = resourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			if (view == null) {
				view = inflator.inflate(resourceId, parent, false);
				holder = new ViewHolder();
				holder.imageView = (ImageView) view
						.findViewById(R.id.image_in_category_grid);
				holder.progressLayout = (RelativeLayout) view
						.findViewById(R.id.loadingPanel);
				holder.textView = (TextView) view
						.findViewById(R.id.text_in_category_grid);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			final Integer localizedPosition = getLocalizedPositionInGrid(
					taxonList, position);
			String title = taxonList.get(localizedPosition).getName();
			holder.textView.setText(title);

			String imageUrl = taxonList.get(localizedPosition).getImageUrl();

			if ((imageUrl != null)) {
				imageLoader.displayImage(imageUrl, holder.imageView, options,
						new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
								holder.progressLayout
										.setVisibility(View.VISIBLE);

							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {
								holder.progressLayout.setVisibility(View.GONE);

							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								holder.progressLayout.setVisibility(View.GONE);

							}
						}, new ImageLoadingProgressListener() {
							@Override
							public void onProgressUpdate(String imageUri,
									View view, int current, int total) {

							}
						});

			}
			return view;

		}

		class ViewHolder {
			public RelativeLayout progressLayout;
			public ImageView imageView;
			public TextView textView;
		}
	}
}
