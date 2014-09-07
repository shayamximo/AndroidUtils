package com.parqueteam.pagefragments;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.parqueteam.R;
import com.parqueteam.category.CategoryUIItem;
import com.parqueteam.category.CategoryViewAdapter;
import com.parqueteam.json.Params;
import com.parqueteam.json.Taxon;
import com.parqueteam.json.stores.InitInfoStore;
import com.parqueteam.json.stores.TaxonomiesStore;
import com.parqueteam.utils.MximoFlurryAgent;

public class CategoryBrowserViewController extends FragmentPageBase {

	List<String> catagoryNamesMain = null;

	public CategoryBrowserViewController() {
	}

	@Override
	protected int getLayoutID() {
		// TODO Auto-generated method stub
		return R.layout.category_list_fragment;
	}

	private ArrayList<Integer> placeHolders = null;
	private List<String> catagoryNames = null;
	private Taxon taxons = null;

	@Override
	protected void initializeActionBar() {

		String title = getArguments()
				.getString(FragmentRoot.CATEGORY_TITLE_KEY);
		actionBarContorller.textViewMainTitle.setText(title);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		// ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
		// R.array.heroes, android.R.layout.simple_list_item_1);

		MximoFlurryAgent.logEvent(MximoFlurryAgent.CATEGORY_PAGE_VIEWED);

		Bundle args = getArguments();

		if (placeHolders == null) {
			placeHolders = args.getIntegerArrayList(LIST_OF_PLACEHOLDERS_KEY);
			taxons = TaxonomiesStore.getInstance().getTaxonsByPlaceHolder(
					placeHolders);
			catagoryNames = new ArrayList<String>();

			for (Taxon tax : taxons.getTaxons()) {
				catagoryNames.add(tax.getName());
			}

		}

		ArrayList<CategoryUIItem> data = new ArrayList<CategoryUIItem>();

		for (Taxon tax : taxons.getTaxons()) {
			data.add(new CategoryUIItem(tax.getName(), tax.getImageUrl()));
		}

		ListView listView = (ListView) getActivity()
				.findViewById(R.id.listview);

		// Customizations for the rows of the category

		List<Params> categoryParamsList = InitInfoStore.getInstance()
				.getCategoryRowParams();

		CategoryRowParams categoryRowParams = new CategoryRowParams();

		for (Params params : categoryParamsList) {
			String name = params.getName();

			// This line is to be set to a url, and not locally collect the
			// file.
			if (name.equals("background_image")
					&& params.getUi().equals("file_uploader")) {
				ViewGroup viewGroup = (ViewGroup) viewOfFragment
						.findViewById(R.id.main_layout_category_list_fragment);
				viewGroup.setBackgroundResource(R.drawable.tiling_bitmap);
			}

			if (name.equals("table_inset")) {
				int tableInset = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(params
								.getValue()), getResources()
								.getDisplayMetrics());
				listView.setDivider(new ColorDrawable(getResources().getColor(
						R.color.transparent)));
				listView.setDividerHeight(tableInset / 2);
				MarginLayoutParams mlp = (MarginLayoutParams) listView
						.getLayoutParams();
				mlp.topMargin = tableInset;
				mlp.bottomMargin = tableInset;
				mlp.leftMargin = tableInset;
				mlp.rightMargin = tableInset;
			}

			if (name.equals("text_color")) {
				categoryRowParams.textColor = "#" + params.getValue();
			}

			if (name.equals("cell_color")) {
				categoryRowParams.cellColor = "#" + params.getValue();

			}

			if (name.equals("cell_text_alignment")) {
				categoryRowParams.cellTextAlignment = params.getValue();

			}
		}

		CategoryViewAdapter customGridAdapter = new CategoryViewAdapter(
				getActivity(), R.layout.category_row_in_category_list, data,
				categoryRowParams, imageLoader, options);

		listView.setAdapter(customGridAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				fragmentRoot.onCategoryChosen(placeHolders, arg2,
						catagoryNames.get(arg2), false);

			}
		});

		String title = getArguments()
				.getString(FragmentRoot.CATEGORY_TITLE_KEY);

		if (title == null || title.equals("")) {

			title = taxons.getName();
			actionBarContorller.textViewMainTitle.setText(title);
		}

	}

	public class CategoryRowParams {
		public Integer tableInset;
		public String textColor;
		public String cellColor;
		public String cellTextAlignment;

	}
}
