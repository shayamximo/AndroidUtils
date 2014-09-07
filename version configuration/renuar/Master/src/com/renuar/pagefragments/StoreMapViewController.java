package com.renuar.pagefragments;

import java.util.List;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.renuar.App;
import com.renuar.R;
import com.renuar.json.Shop;
import com.renuar.json.stores.ShopStore;
import com.renuar.pagefragments.MximoGoogleMapsFragment.IonMximoGoogleMapsFragmentActivityCreated;

public class StoreMapViewController extends FragmentPageBase {

	public final static String KEY_OPEN_CLOSEST = "key_open_closest";
	MximoGoogleMapsFragment googleMapsFragment = null;

	Location currentLocation = null;

	GoogleMap googleMap = null;

	@Override
	protected int getLayoutID() {
		return R.layout.store_map;
	}

	@Override
	protected void initializeActionBar() {

		makeViewsVisible(actionBarContorller.imageViewOnRightStandalone,
				actionBarContorller.secondViewOnRightStandalone);

		actionBarContorller.textViewMainTitle.setText(getResources().getString(
				R.string.stores));

		ImageView getMyLocationButton = actionBarContorller.imageViewOnRightStandalone;

		getMyLocationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentLocation != null) {
					CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
							new LatLng(currentLocation.getLatitude(),
									currentLocation.getLongitude()), 15);
					if (googleMap != null) {
						googleMap.moveCamera(cameraUpdate);
						googleMap.animateCamera(CameraUpdateFactory.zoomTo(10),
								2000, null);
					}
				}
			}
		});

		getMyLocationButton
				.setImageResource(R.drawable.ic_action_location_found);

		actionBarContorller.secondViewOnRightStandalone
				.setImageResource(R.drawable.ic_action_view_as_list);

		actionBarContorller.secondViewOnRightStandalone
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						fragmentRoot.openShopListFragment(null);

					}
				});

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		FragmentManager fm = getChildFragmentManager();

		if (googleMap != null)
			return;

		try {
			googleMapsFragment = new MximoGoogleMapsFragment();
		} catch (Exception e) {
			e.printStackTrace();
		}

		googleMapsFragment
				.setIonMximoGoogleMapsFragmentActivityCreated(new IonMximoGoogleMapsFragmentActivityCreated() {

					@Override
					public void onFinish(final GoogleMap googleMap) {
						try {
							StoreMapViewController.this.googleMap = googleMap;
							// Changing map type
							googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

							// Showing / hiding your current location
							googleMap.setMyLocationEnabled(true);

							// Enable / Disable zooming controls
							googleMap.getUiSettings().setZoomControlsEnabled(
									false);

							// Enable / Disable my location button
							googleMap.getUiSettings()
									.setMyLocationButtonEnabled(false);

							// Enable / Disable Compass icon
							googleMap.getUiSettings().setCompassEnabled(true);

							// Enable / Disable Rotate gesture
							googleMap.getUiSettings().setRotateGesturesEnabled(
									true);

							// Enable / Disable zooming functionality
							googleMap.getUiSettings().setZoomGesturesEnabled(
									true);

							createLocationCoordsOfStores(googleMap);

							// This is for when an icon in clicked, set the
							// popup
							googleMap
									.setInfoWindowAdapter(new InfoWindowAdapter() {
										@Override
										public View getInfoWindow(Marker marker) {

											View v = inflator.inflate(
													R.layout.lyt_map_popup,
													null);
											TextView name = (TextView) v
													.findViewById(R.id.lyt_mappopup_txt_storename);
											TextView address = (TextView) v
													.findViewById(R.id.lyt_mappopup_txt_address);
											if (marker.getTitle()
													.equalsIgnoreCase(
															"I am Here.")) {
												ImageView arrow = (ImageView) v
														.findViewById(R.id.lyt_mappopup_btn_arrow);
												arrow.setVisibility(View.GONE);
											}
											name.setText(marker.getTitle());
											name.setGravity(Gravity.LEFT);
											address.setText(marker.getSnippet());
											return v;
										}

										@Override
										public View getInfoContents(Marker arg0) {
											return null;
										}
									});

							// For when a user clicks on the shops layout
							// after pushing icon, navigate to shop details
							// fragment

							googleMap
									.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

										@Override
										public void onInfoWindowClick(
												Marker marker) {
											// SHOP_DETAILS_POSITION_IN_STORE
											int position = App
													.getApp()
													.getShopStore()
													.getShopPositionByName(
															marker.getTitle());
											Bundle bundle = new Bundle();
											bundle.putInt(
													ShopDetailsFragment.SHOP_DETAILS_POSITION_IN_STORE,
													position);

											fragmentRoot
													.openShopDetailsFragment(
															bundle, false);

										}
									});

							googleMap
									.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {

										@Override
										public void onMyLocationChange(
												Location location) {
											googleMap
													.setOnMyLocationChangeListener(null);
											App.getApp()
													.setMyLocation(location);
											currentLocation = location;
											ShopStore shopStore = App.getApp()
													.getShopStore();
											List<Shop> shopList = shopStore
													.getShopsList();

											for (Shop shop : shopList) {
												float[] result = new float[3];
												Location.distanceBetween(
														location.getLatitude(),
														location.getLongitude(),
														shop.getLatitude(),
														shop.getLongitude(),
														result);
												shop.setDistanceFromCurrentLocation(Math
														.round(result[0] * 0.001));
											}
											App.getApp().getShopStore()
													.sortShopList();

											Bundle inputBundle = getArguments();
											boolean goToNearest = inputBundle
													.getBoolean(
															KEY_OPEN_CLOSEST,
															false);

											if (goToNearest) {
												Bundle bundle = new Bundle();
												bundle.putBoolean(
														ShopDetailsFragment.DISPLAY_CLOSEST_SHOP,
														true);

												fragmentRoot
														.openShopDetailsFragment(
																bundle, true);

											}

										}
									});

						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				});

		fm.beginTransaction().addToBackStack(null)
				.add(R.id.layout_with_google_maps_fragment, googleMapsFragment)
				.commit();

	}

	private void createLocationCoordsOfStores(final GoogleMap googleMap) {

		final ShopStore shopStore = App.getApp().getShopStore();

		final List<Shop> shopList = shopStore.getShopsList();

		boolean firstRun = true;
		for (final Shop shop : shopList) {
			final MarkerOptions marker = new MarkerOptions()
					.position(
							new LatLng(shop.getLatitude(), shop.getLongitude()))
					.title(shop.getName()).snippet(shop.getStreet());
			// marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.map));

			googleMap.addMarker(marker);
			if (firstRun == true) {
				firstRun = false;
				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(new LatLng(shop.getLatitude(), shop
								.getLongitude())).zoom(10).build();

				googleMap.animateCamera(CameraUpdateFactory
						.newCameraPosition(cameraPosition));
			}

		}

	}

}
