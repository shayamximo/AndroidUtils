package com.renuar.category;

import java.util.ArrayList;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.renuar.MainActivity;
import com.renuar.R;
import com.renuar.json.helpers.TabInfo;
import com.renuar.pagefragments.FragmentHomeScreenCollapsibleCategory;
import com.renuar.utils.MximoLog;

public class NavDrawerListAdapterTabReplacement extends BaseAdapter {

	private ArrayList<NavDrawerItemTabReplacement> navDrawerItemTabReplacement;
	private MainActivity mainActivity;
	private DrawerLayout mDrawerLayout;
	ListView mDrawerList;

	public NavDrawerListAdapterTabReplacement(
			ArrayList<NavDrawerItemTabReplacement> navDrawerItemTabReplacement,
			MainActivity mainActivity, DrawerLayout mDrawerLayout,
			ListView mDrawerList) {
		this.navDrawerItemTabReplacement = navDrawerItemTabReplacement;
		this.mainActivity = mainActivity;
		this.mDrawerLayout = mDrawerLayout;
		this.mDrawerList = mDrawerList;
	}

	@Override
	public int getCount() {
		return navDrawerItemTabReplacement.size();
	}

	@Override
	public Object getItem(int position) {
		return navDrawerItemTabReplacement.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater mInflater = (LayoutInflater) mainActivity
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(
					R.layout.drawer_list_item_tab_replacement, null);
			holder.text = (TextView) convertView
					.findViewById(R.id.title_in_drawer);
			holder.image = (ImageView) convertView
					.findViewById(R.id.image_in_drawer);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final TabInfo tabInfo = navDrawerItemTabReplacement.get(position)
				.getTabInfo();
		holder.text.setText(tabInfo.tabTitle);
		holder.image.setImageResource(tabInfo.imageResourceId);

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (tabInfo.className
						.equals("FragmentHomeScreenCollapsibleCategory")) {
					mDrawerLayout.closeDrawer(mDrawerList);
				} else {
					mainActivity.getFragmentRoot().openFragment(
							tabInfo.className, null);
					mDrawerLayout
							.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
				}

			}
		});

		return convertView;
	}

	public class ViewHolder {
		public TextView text;
		public ImageView image;

	}

}
