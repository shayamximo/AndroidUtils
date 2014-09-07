package com.renuar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.renuar.json.AppInfo;
import com.renuar.utils.MximoViewUtils;
import com.renuar.utils.UtilityFunctions;

public class ChooseAppActivity extends Activity {

	public static final String APPS_LIST_KEY = "apps_list_key";

	Map<String, Integer> appInfoHashMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apps_list);

		ListView list = (ListView) findViewById(R.id.listview_apps_list);

		Bundle args = getIntent().getExtras();

		final ArrayList<AppInfo> appInfoList = args
				.getParcelableArrayList(APPS_LIST_KEY);

		AppNamesAdapter appNamesAdapter = new AppNamesAdapter(this,
				R.layout.app_name_row, appInfoList);

		list.setAdapter(appNamesAdapter);

		appInfoHashMap = new HashMap<String, Integer>();

		ArrayList<String> names = new ArrayList<String>();
		for (AppInfo ai : appInfoList) {
			appInfoHashMap.put(ai.getName(), ai.getId());
			names.add(ai.getName());
		}

		String item[] = names.toArray(new String[names.size()]);

		AutoCompleteTextView myAutoComplete = (AutoCompleteTextView) findViewById(R.id.myautocomplete);

		myAutoComplete.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, item));

		// from stackoverflow, to stop the keyboard from jumping
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		myAutoComplete.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				UtilityFunctions.hideKeyboard(ChooseAppActivity.this);
				TextView tv = ((TextView) arg1);
				App.getApp().setAppInfoList(appInfoList);
				int appID = appInfoHashMap.get(tv.getText().toString());
				MximoViewUtils.restartApp(appID, ChooseAppActivity.this);

			}
		});
	}

	public class AppNamesAdapter extends ArrayAdapter<AppInfo> {

		int resource;
		LayoutInflater inflator;
		final ArrayList<AppInfo> appInfoList;

		public AppNamesAdapter(Context context, int resource,
				ArrayList<AppInfo> appInfoList) {
			super(context, resource, appInfoList);

			this.resource = resource;
			this.inflator = getLayoutInflater();
			this.appInfoList = appInfoList;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View row = convertView;
			Holder holder;
			if (row == null) {

				row = inflator.inflate(resource, parent, false);
				holder = new Holder();
				holder.textView = (TextView) row
						.findViewById(R.id.text_view_app_name);
				row.setTag(holder);
			} else {
				holder = (Holder) row.getTag();
			}

			holder.textView.setText(appInfoList.get(position).getName());

			row.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					App.getApp().setAppInfoList(appInfoList);
					int appID = appInfoList.get(position).getId();
					MximoViewUtils.restartApp(appID, ChooseAppActivity.this);
				}
			});
			return row;
		}

		public class Holder {
			TextView textView;
		}

	}
}
