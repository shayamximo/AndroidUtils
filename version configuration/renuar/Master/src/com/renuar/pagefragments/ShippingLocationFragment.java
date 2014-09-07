package com.renuar.pagefragments;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.renuar.App;
import com.renuar.R;
import com.renuar.json.ShippingLocation;

public class ShippingLocationFragment extends FragmentPageBase {

	public final static String SHIPPING_KEY_FOR_STATE = "country_key_for_state";

	@Override
	protected int getLayoutID() {
		return R.layout.category_list_fragment;
	}

	@Override
	protected void initializeActionBar() {
		actionBarContorller.textViewMainTitle.setText(getResources().getString(
				R.string.country));

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		Bundle args = getArguments();

		List<ShippingLocation> countryOrStateList = null;
		boolean isState = (args != null)
				&& (args.getInt(SHIPPING_KEY_FOR_STATE) != 0);

		if (isState) {

			Integer countryId = args.getInt(SHIPPING_KEY_FOR_STATE);

			countryOrStateList = App.getApp().getCountriesAndCitiesStore()
					.getStatesByCountryId(countryId);
		} else {
			countryOrStateList = App.getApp().getCountriesAndCitiesStore()
					.getCountryList();

		}

		ListView listView = (ListView) viewOfFragment
				.findViewById(R.id.listview);
		listView.setAdapter(new CountryAdapter(getActivity(),
				R.layout.category_row_in_category_list, countryOrStateList));

	}

	public class CountryAdapter extends ArrayAdapter<ShippingLocation> {
		List<ShippingLocation> shippingLocationList;
		private int resource;

		public CountryAdapter(Context context, int resource,
				List<ShippingLocation> countryList) {
			super(context, resource, resource, countryList);
			this.shippingLocationList = countryList;
			this.resource = resource;
		}

		@Override
		public int getCount() {
			return shippingLocationList.size();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			View row = convertView;
			Holder holder = null;
			if (row == null) {
				row = inflator.inflate(resource, parent, false);
				holder = new Holder();
				holder.countryText = (TextView) row.findViewById(R.id.home_txt);
				// This button is irrelevant
				row.findViewById(R.id.arrow).setVisibility(View.GONE);
				row.setTag(holder);
			} else {
				holder = (Holder) row.getTag();
			}

			holder.countryText.setText(shippingLocationList.get(position)
					.getName());
			row.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					fragmentRoot.goToPreviousFragment(
							UserInfoViewController.SHIPPING_LOCATION_NAME,
							shippingLocationList.get(position).getName(),
							UserInfoViewController.SHIPPING_LOCATION_ID,
							shippingLocationList.get(position).getId(), null,
							null);

				}
			});
			return row;

		}

	}

	public static class Holder {
		TextView countryText;
	}

}
