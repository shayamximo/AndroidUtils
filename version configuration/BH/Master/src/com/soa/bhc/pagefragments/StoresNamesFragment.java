package com.soa.bhc.pagefragments;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.soa.bhc.App;
import com.soa.bhc.R;
import com.soa.bhc.json.Shop;
import com.soa.bhc.json.stores.ShopStore;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class StoresNamesFragment extends FragmentPageBase {

	public static String SHOPS_KEY_TITLE = "shops_key_title";

	@Override
	protected int getLayoutID() {
		return R.layout.category_list_fragment;
	}

	@Override
	protected void initializeActionBar() {

		Bundle bundle = getArguments();
		String title = bundle.getString(SHOPS_KEY_TITLE);
		if (title != null) {
			actionBarContorller.textViewMainTitle.setText(title);
		} else {
			actionBarContorller.textViewMainTitle.setText(getResources()
					.getString(R.string.shipping_choose_branch));
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		ShopStore shopStore = App.getApp().getShopStore();
		ListView listView = (ListView) viewOfFragment
				.findViewById(R.id.listview);
		listView.setAdapter(new StoresAdapter(getActivity(),
				R.layout.donation_row_layout, shopStore.getShopsList()));

	}

	public class StoresAdapter extends ArrayAdapter<Shop> {
		List<Shop> shopsList;
		private int resource;

		public StoresAdapter(Context context, int resource, List<Shop> shopsList) {
			super(context, resource, resource, shopsList);
			this.shopsList = shopsList;
			this.resource = resource;
		}

		@Override
		public int getCount() {
			return shopsList.size();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			View row = convertView;
			final Holder holder;
			if (row == null) {
				row = inflator.inflate(resource, parent, false);
				holder = new Holder();
				holder.shopText = (TextView) row
						.findViewById(R.id.description_text_in_donation);
				holder.imageViewOfShop = (ImageView) row
						.findViewById(R.id.image_in_donation);

				// This button is irrelevant

				row.setTag(holder);
			} else {
				holder = (Holder) row.getTag();
			}

			Shop shop = shopsList.get(position);

			holder.shopText.setText(shop.getName());
			row.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					fragmentRoot.goToPreviousFragment(
							ShippingInfoViewController.CHOSEN_BRANCH, shopsList
									.get(position).getName(),
							ShippingInfoViewController.CHOSEN_BRANCH_ID,
							shopsList.get(position).getId(), null, null);

				}
			});

			holder.imageViewOfShop.setVisibility(View.GONE);

			String pinImage = shop.getPinImage();
			if ((pinImage != null) && (pinImage.length() > 0)) {
				if ((pinImage != null)) {
					imageLoader.displayImage(pinImage, holder.imageViewOfShop,
							options, new SimpleImageLoadingListener() {
								@Override
								public void onLoadingStarted(String imageUri,
										View view) {

								}

								@Override
								public void onLoadingFailed(String imageUri,
										View view, FailReason failReason) {

								}

								@Override
								public void onLoadingComplete(String imageUri,
										View view, Bitmap loadedImage) {
									holder.imageViewOfShop
											.setVisibility(View.VISIBLE);

								}
							}, new ImageLoadingProgressListener() {
								@Override
								public void onProgressUpdate(String imageUri,
										View view, int current, int total) {

								}
							});

				}

			}

			else {
				holder.imageViewOfShop.setVisibility(View.GONE);
			}

			return row;

		}

	}

	public static class Holder {
		TextView shopText;
		ImageView imageViewOfShop;
	}

}
