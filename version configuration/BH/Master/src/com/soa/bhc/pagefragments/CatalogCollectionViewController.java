package com.soa.bhc.pagefragments;

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

import com.soa.bhc.R;
import com.soa.bhc.json.Product;
import com.soa.bhc.json.stores.TaxonomiesStore;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class CatalogCollectionViewController extends FragmentPageBase {

	private String title;
	private GridView gridView;

	@Override
	protected int getLayoutID() {

		return R.layout.ac_image_grid_three_columns;
	}

	@Override
	protected void initializeActionBar() {
		actionBarContorller.textViewMainTitle.setText(title);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		Bundle bundle = getArguments();
		title = bundle.getString(FragmentRoot.CATEGORY_TITLE_KEY);

		final ArrayList<Integer> placeHolders = bundle
				.getIntegerArrayList(FragmentRoot.TAXON_ADDRESS_KEY);

		final List<Product> productsList = TaxonomiesStore.getInstance()
				.getTaxonsByPlaceHolder(placeHolders).getProducts();

		gridView = (GridView) getView().findViewById(
				R.id.gridview_three_columns);
		gridView.setAdapter(new GridAdapter(getActivity(),
				R.layout.item_in_grid_catalog_collection_view_controller,
				productsList));
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				Bundle args = new Bundle();
				args.putString(FragmentRoot.CATEGORY_TITLE_KEY, title);
				args.putIntegerArrayList(FragmentRoot.TAXON_ADDRESS_KEY,
						placeHolders);
				args.putInt(FragmentRoot.PRODUCT_IN_TAXON_ADDRESS_KEY, position);
				fragmentRoot.openItemFragment(args);

			}
		});

		super.onActivityCreated(savedInstanceState);
	}

	public class GridAdapter extends ArrayAdapter<Product> {

		List<Product> productsList;
		int resourceID;

		public GridAdapter(Context context, int resourceID,
				List<Product> productsList) {
			super(context, resourceID, productsList);
			this.productsList = productsList;
			this.resourceID = resourceID;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Holder holder;
			View row = convertView;

			if (row == null) {
				row = inflator.inflate(resourceID, parent, false);
				holder = new Holder();
				holder.imageView = (ImageView) row
						.findViewById(R.id.image_in_grid_catalog_collection_view_controller);
				holder.textView = (TextView) row
						.findViewById(R.id.title_in_grid_catalog_collection_view_controller);

				holder.progressLayout = (RelativeLayout) row
						.findViewById(R.id.loadingPanel);
				row.setTag(holder);

			} else {

				holder = (Holder) row.getTag();
			}

			boolean showCaption = getBooleanOfParamFromName("show_captions",false);
			
			if (showCaption)
				holder.textView.setText(productsList.get(position).getName());
			else
				holder.textView.setVisibility(View.GONE);

			Product product = productsList.get(position);
			String imageUrl = product.getMainImageUrlThumbNailSize();

			holder.imageView.setVisibility(View.GONE);
			holder.progressLayout.setVisibility(View.VISIBLE);
			if ((imageUrl != null)) {
				imageLoader.displayImage(imageUrl, holder.imageView, options,
						new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								holder.imageView.setVisibility(View.VISIBLE);
								holder.progressLayout.setVisibility(View.GONE);

							}

							@Override
							public void onLoadingCancelled(String imageUri,
									View view) {
								// TODO Auto-generated method stub

							}
						});
			}

			return row;
		}

		public class Holder {
			ImageView imageView;
			TextView textView;
			RelativeLayout progressLayout;
		}

	}

}
