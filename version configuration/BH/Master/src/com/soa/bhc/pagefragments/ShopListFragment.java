package com.soa.bhc.pagefragments;

import java.util.Collections;
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

import com.soa.bhc.App;
import com.soa.bhc.R;
import com.soa.bhc.json.Shop;

public class ShopListFragment extends FragmentPageBase {

	@Override
	protected int getLayoutID() {
		return R.layout.category_list_fragment;
	}

	@Override
	protected void initializeActionBar() {

		actionBarContorller.textViewMainTitle.setText(getResources().getString(
				R.string.stores));

		TextView bottomText = (TextView) viewOfFragment
				.findViewById(R.id.textview_bottom_of_topbar);
		makeViewsVisible(bottomText);
		bottomText.setText(getResources().getString(
				R.string.distance_from_current_location));
		setViewToColor(bottomText, R.color.white_smoke);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		ListView listViewOfShops = (ListView) viewOfFragment
				.findViewById(R.id.listview);

		List<Shop> shopList = App.getApp().getShopStore().getShopsList();
		Collections.sort(shopList);
		ShopListAdapter shopListAdapter = new ShopListAdapter(getActivity(),
				R.layout.row_in_stores_list, shopList);
		listViewOfShops.setAdapter(shopListAdapter);
	}

	public class ShopListAdapter extends ArrayAdapter<Shop> {

		private List<Shop> shopList;
		private int layoutResourceId;

		public ShopListAdapter(Context context, int layoutResourceId,
				List<Shop> shopList) {
			super(context, layoutResourceId, shopList);
			this.layoutResourceId = layoutResourceId;
			this.shopList = shopList;

		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View row = convertView;
			RecordHolder holder = null;

			if (row == null) {

				row = inflator.inflate(layoutResourceId, parent, false);

				holder = new RecordHolder();
				holder.txtShopName = (TextView) row
						.findViewById(R.id.text_shop_name);
				holder.txtShopStreet = (TextView) row
						.findViewById(R.id.text_shop_street);
				holder.txtDistanceFromMyLocation = (TextView) row
						.findViewById(R.id.text_distance);
				holder.imageItem = (ImageView) row
						.findViewById(R.id.image_view_arrow);

				row.setTag(holder);
			} else {
				holder = (RecordHolder) row.getTag();
			}

			Shop shop = shopList.get(position);

			holder.txtShopName.setText(shop.getName());
			holder.txtShopStreet.setText(shop.getStreet());
			holder.txtDistanceFromMyLocation.setText(shop
					.getDistanceFromCurrentLocation());
			row.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Bundle args = new Bundle();
					args.putInt(
							ShopDetailsFragment.SHOP_DETAILS_POSITION_IN_STORE,
							position);

					fragmentRoot.openShopDetailsFragment(args,false);

				}
			});

			return row;
		}

	}

	static class RecordHolder {
		TextView txtShopName;
		TextView txtShopStreet;
		TextView txtDistanceFromMyLocation;
		ImageView imageItem;

	}

}
