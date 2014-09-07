package com.soa.bhc.pagefragments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.soa.bhc.App;
import com.soa.bhc.R;
import com.soa.bhc.json.Shop;
import com.soa.bhc.utils.MximoFlurryAgent;

public class ShopDetailsFragment extends FragmentPageBase {

	public static final String SHOP_DETAILS_POSITION_IN_STORE = "shop_details_position_in_store";
	public static final String DISPLAY_CLOSEST_SHOP = "display_closest_shop";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";

	private Shop currentShop;

	@Override
	protected int getLayoutID() {

		return R.layout.shop_details;
	}

	@Override
	protected void initializeActionBar() {
		actionBarContorller.textViewMainTitle.setText(currentShop.getName());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		List<Shop> shopList = App.getApp().getShopStore().getShopsList();

		Bundle bundle = getArguments();

		boolean showClosest = bundle.getBoolean(DISPLAY_CLOSEST_SHOP, false);
		int positionInCartStore = 0;
		if (showClosest)
			positionInCartStore = 0;
		else
			positionInCartStore = bundle.getInt(SHOP_DETAILS_POSITION_IN_STORE);
		currentShop = shopList.get(positionInCartStore);

		TextView nameOfShopTextView = (TextView) viewOfFragment
				.findViewById(R.id.storeinfo_tvNamevalue);
		nameOfShopTextView.setText(currentShop.getName());

		TextView cityTextView = (TextView) viewOfFragment
				.findViewById(R.id.storeinfo_tvCityValue);
		cityTextView.setText(currentShop.getCity());

		TextView streetTextView = (TextView) viewOfFragment
				.findViewById(R.id.storeinfo_tvStreetValue);
		streetTextView.setText(currentShop.getStreet());

		TextView phoneTextView = (TextView) viewOfFragment
				.findViewById(R.id.storeinfo_tvPhoneValue);
		phoneTextView.setText(currentShop.getPhone());

		TextView hoursTextView = (TextView) viewOfFragment
				.findViewById(R.id.storeinfo_tvHourValue);
		hoursTextView.setText(currentShop.getHours());

		View callStore = (View) viewOfFragment
				.findViewById(R.id.storeinfo_tvCallStore);
		callStore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MximoFlurryAgent.logEvent(MximoFlurryAgent.DIAL_CLICKED);
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:" + currentShop.getPhone()));
				startActivity(callIntent);
			}
		});

		View navigateToStore = (View) viewOfFragment
				.findViewById(R.id.storeinfo_tvNavigate);
		navigateToStore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MximoFlurryAgent
						.logEvent(MximoFlurryAgent.USER_NAVIGATED_TO_STORE);
				if (App.getApp().getMyLocation() == null)
					return;

				Map<String, String> share = new HashMap<String, String>();
				share.put("Store Name", currentShop.getName());
				share.put("Store Address", currentShop.getCity());

				String strURL = "http://maps.google.com/maps?saddr=%s&daddr=%s";
				String source = App.getApp().getMyLocation().getLatitude()
						+ "," + App.getApp().getMyLocation().getLongitude();
				String destination = currentShop.getLatitude() + ","
						+ currentShop.getLongitude();
				String url1 = String.format(strURL, source, destination);
				String url = url1.replace(" ", "%20");

				Bundle bundle = new Bundle();
				bundle.putString(GoogleMapFragment.URL_TO_PASS, url);

				fragmentRoot.openGoogleMapFragment(bundle);

			}
		});
		super.onActivityCreated(savedInstanceState);
	}

}
