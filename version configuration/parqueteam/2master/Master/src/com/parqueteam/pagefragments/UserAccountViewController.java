package com.parqueteam.pagefragments;

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

import com.parqueteam.R;
import com.parqueteam.json.Page;
import com.parqueteam.json.Params;
import com.parqueteam.json.stores.InitInfoStore;

public class UserAccountViewController extends FragmentPageBase {

	protected int getLayoutID() {
		return R.layout.user_account;
	}

	@Override
	protected void initializeActionBar() {
		actionBarContorller.textViewMainTitle.setText(getResources().getString(
				R.string.more));
	}

	private boolean isClass(String name) {
		List<Params> paramsList = InitInfoStore.getInstance()
				.getPageParamsByName(name);
		for (Params params : paramsList) {
			if (params.getName().equals("class_name"))
				return true;
		}
		return false;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		ListView listView = (ListView) viewOfFragment
				.findViewById(R.id.listview_user);
		List<String> keysOfActions = getValueListOfParamFromName("row");

		AccountAdapter accountAdapter = new AccountAdapter(getActivity(),
				R.layout.row_in_account, keysOfActions);
		listView.setAdapter(accountAdapter);
		super.onActivityCreated(savedInstanceState);
	}

	public class AccountAdapter extends ArrayAdapter<String> {

		private int resource;
		private List<String> keysOfActions;

		public AccountAdapter(Context context, int resource,
				List<String> keysOfActions) {
			super(context, resource, keysOfActions);
			this.resource = resource;
			this.keysOfActions = keysOfActions;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = convertView;
			final Holder holder;
			if (row == null) {

				row = inflator.inflate(resource, parent, false);

				holder = new Holder();
				holder.imageView = (ImageView) row
						.findViewById(R.id.imageview_row_in_account);
				holder.textView = (TextView) row
						.findViewById(R.id.textview_row_in_account);

				row.setTag(holder);
			} else {
				holder = (Holder) row.getTag();

			}

			final String keyOfCurrentFragment = keysOfActions.get(position);

			Page page = InitInfoStore.getInstance().getPageByName(
					keyOfCurrentFragment);

			int iconId = getIconByString(keyOfCurrentFragment);
			if (iconId != -1)
				holder.imageView.setBackgroundResource(iconId);

			Params params = page.getParamsByName("title");
			if (params == null)
				holder.textView.setText("");
			else
				holder.textView.setText(params.getValue());

			row.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (isClass(keyOfCurrentFragment)) {
						fragmentRoot.openFragment(keyOfCurrentFragment, null);
					} else {
						handleNonFragmentActions(keyOfCurrentFragment);
					}

				}
			});
			return row;
		}

		public class Holder {
			public ImageView imageView;
			public TextView textView;
		}
	}

	public int getIconByString(String iconString) {
		if (iconString.endsWith("wishlist"))
			return R.drawable.heart;
		if (iconString.endsWith("terms"))
			return R.drawable.document;
		if (iconString.endsWith("stores"))
			return R.drawable.location;
		if (iconString.endsWith("about"))
			return R.drawable.about;
		if (iconString.endsWith("contact"))
			return R.drawable.mail;
		if (iconString.endsWith("call"))
			return R.drawable.phone;
		if (iconString.endsWith("register"))
			return R.drawable.about;

		return -1;
	}

	public class NonFragmentActions {
		public NonFragmentActions(String action, String data) {

		}
	}

}
