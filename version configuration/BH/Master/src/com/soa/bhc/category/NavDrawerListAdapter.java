package com.soa.bhc.category;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soa.bhc.App;
import com.soa.bhc.MainActivity;
import com.soa.bhc.R;
import com.soa.bhc.json.Product;
import com.soa.bhc.json.Taxon;
import com.soa.bhc.json.stores.InitInfoStore;
import com.soa.bhc.json.stores.SearchStore;
import com.soa.bhc.json.stores.StoreLoadDataListener;
import com.soa.bhc.json.stores.TaxonomiesStore;
import com.soa.bhc.json.stores.TaxonsStore;
import com.soa.bhc.utils.ProgressDialogFragment;

public class NavDrawerListAdapter extends BaseAdapter {

	private MainActivity mainActivity;
	private ArrayList<NavDrawerItem> navDrawerItems;

	private static final int ANIMATION_TIME_PER_CELL_APPEARENCE = 100;
	private static final int NUMBER_OF_CELLS_TO_ANIMATE_CLOSE = 3;
	private static final int NUMBER_OF_CELLS_TO_ANIMATE_OPEN = 3;
	private static final String SPACES_FOR_SONS = "       ";

	// variable to prevent bug of pushing an opening category twice fast.
	private boolean isInOppeningCategoryProgress = false;

	public NavDrawerListAdapter(Context context,
			ArrayList<NavDrawerItem> navDrawerItems) {
		this.mainActivity = (MainActivity) context;
		this.navDrawerItems = navDrawerItems;

	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private void handleFirstItemElementClosing(
			final NavDrawerItem navDrawerItem, final int position,
			final ImageView arrow, final ImageView arrowWorkaround) {

		final int numberOfElementsToRemove = getTaxonListForNavigationItem(
				navDrawerItem).size();

		int animationId = R.anim.rotate_btn_down;
		Animation anim = AnimationUtils
				.loadAnimation(mainActivity, animationId);
		// anim.setFillAfter(true);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {

				animateSecondLevelElementsRemoval(position + 1,
						numberOfElementsToRemove);

			}
		});

		arrowWorkaround.startAnimation(anim);

	}

	private void animateSecondLevelElementsRemoval(final int position,
			final int numberOfElementsToRemove) {

		for (int i = 0; i < numberOfElementsToRemove
				- NUMBER_OF_CELLS_TO_ANIMATE_CLOSE; i++)
			navDrawerItems.remove(position);
		notifyDataSetChanged();

		recursiveAnimationForRemovingListItems(
				0,
				position,
				((numberOfElementsToRemove < NUMBER_OF_CELLS_TO_ANIMATE_CLOSE) ? numberOfElementsToRemove
						: NUMBER_OF_CELLS_TO_ANIMATE_CLOSE));

	}

	private void recursiveAnimationForRemovingListItems(final int index,
			final int position, final int numberOfCellsToAnimate) {
		Handler handler = new Handler();

		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				int incrementedIndex = index + 1;
				navDrawerItems.remove(position);
				notifyDataSetChanged();
				if (incrementedIndex < numberOfCellsToAnimate)
					recursiveAnimationForRemovingListItems(incrementedIndex,
							position, numberOfCellsToAnimate);
				else
					isInOppeningCategoryProgress = false;

			}
		}, ANIMATION_TIME_PER_CELL_APPEARENCE);

	}

	private List<Taxon> getTaxonListForNavigationItem(
			NavDrawerItem navDrawerItem) {
		List<Integer> placeHolders = navDrawerItem.getLocationInTaxonsStore();

		Taxon taxon = TaxonomiesStore.getInstance().getTaxonsByPlaceHolder(
				placeHolders);
		return taxon.getTaxons();
	}

	private void handleFirstItemElementOppening(
			final NavDrawerItem navDrawerItem, final int position,
			final ImageView arrow, final ImageView arrowWorkaround) {

		final List<Taxon> taxonListOfChosenFirstElement = getTaxonListForNavigationItem(navDrawerItem);

		int animationId = R.anim.rotate_btn_down;
		Animation anim = AnimationUtils
				.loadAnimation(mainActivity, animationId);
		// //anim.setFillAfter(true);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {

				arrow.setVisibility(View.GONE);
				arrowWorkaround.setVisibility(View.VISIBLE);
				animateSecondLevelElementsAdding(position, 0,
						taxonListOfChosenFirstElement,
						navDrawerItem.getLocationInTaxonsStore());

			}
		});
		arrow.setVisibility(View.VISIBLE);
		arrowWorkaround.setVisibility(View.GONE);
		arrow.startAnimation(anim);

	}

	private void animateSecondLevelElementsAdding(final int positionInListView,
			final int indexInTaxamoniesStore,
			final List<Taxon> taxonListOfChosenFirstElement,
			List<Integer> placeHolders) {

		int numberOfTaxons = taxonListOfChosenFirstElement.size();
		int numberOfElementsToAnimate = ((numberOfTaxons < NUMBER_OF_CELLS_TO_ANIMATE_OPEN) ? numberOfTaxons
				: NUMBER_OF_CELLS_TO_ANIMATE_OPEN);
		recursiveAnimationForAddingListItems(indexInTaxamoniesStore,
				positionInListView, numberOfElementsToAnimate,
				taxonListOfChosenFirstElement, placeHolders);

	}

	private void recursiveAnimationForAddingListItems(
			final int indexInTaxamoniesStore, final int positionInListView,
			final int numberOfCellsToAnimate,
			final List<Taxon> taxonListOfChosenFirstElement,
			final List<Integer> placeHolders) {

		if (indexInTaxamoniesStore < numberOfCellsToAnimate) {
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					ArrayList<Integer> tempPlaceHolders = new ArrayList<Integer>(
							placeHolders);
					tempPlaceHolders.add(indexInTaxamoniesStore);
					navDrawerItems.add(
							positionInListView + 1,
							new NavDrawerItem(SPACES_FOR_SONS
									+ taxonListOfChosenFirstElement.get(
											indexInTaxamoniesStore).getName(),
									tempPlaceHolders));// -1 because it's
														// irrelevant,
					// for
					// now.

					int indexInTaxamoniesStorePlus1 = indexInTaxamoniesStore + 1;
					int positionInListViewPlus1 = positionInListView + 1;

					notifyDataSetChanged();

					recursiveAnimationForAddingListItems(
							indexInTaxamoniesStorePlus1,
							positionInListViewPlus1, numberOfCellsToAnimate,
							taxonListOfChosenFirstElement, placeHolders);

				}
			}, ANIMATION_TIME_PER_CELL_APPEARENCE);
		}

		else {
			int sizeOfListToAdd = taxonListOfChosenFirstElement.size();
			int currentPositionInListView = positionInListView;
			for (int i = indexInTaxamoniesStore; i < sizeOfListToAdd; i++) {
				ArrayList<Integer> tempPlaceHolders = new ArrayList<Integer>(
						placeHolders);
				tempPlaceHolders.add(i);

				navDrawerItems.add(currentPositionInListView + 1,
						new NavDrawerItem(SPACES_FOR_SONS
								+ taxonListOfChosenFirstElement.get(i)
										.getName(), tempPlaceHolders));// -1
																		// because
				// it's
				// irrelevant,
				// for
				// now.
				currentPositionInListView++;
			}
			notifyDataSetChanged();
			isInOppeningCategoryProgress = false;
		}

	}

	public class ViewHolder {
		public TextView text;
		public ImageView firstLevelImage;
		public ImageView firstLevelImageWorkaround;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater mInflater = (LayoutInflater) mainActivity
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.drawer_list_item, null);
			holder.text = (TextView) convertView.findViewById(R.id.title);
			holder.firstLevelImage = (ImageView) convertView
					.findViewById(R.id.arrow_first_level);

			holder.firstLevelImageWorkaround = (ImageView) convertView
					.findViewById(R.id.arrow_first_level_workaround_because_animation);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		TextView txtTitle = holder.text;

		final NavDrawerItem navDrawerItem = navDrawerItems.get(position);
		if (navDrawerItem.shouldOpenDrawer()) {

			holder.text.setTextColor(App.getApp().getResources()
					.getColor(R.color.black));
			if (navDrawerItem.isOpen()) {
				holder.firstLevelImageWorkaround.setVisibility(View.VISIBLE);
				holder.firstLevelImage.setVisibility(View.GONE);
			} else {
				holder.firstLevelImageWorkaround.setVisibility(View.GONE);
				holder.firstLevelImage.setVisibility(View.VISIBLE);
			}
		}

		else {

			if (navDrawerItem.isFirstLevelCategory()) {
				holder.text.setTextColor(App.getApp().getResources()
						.getColor(R.color.black));
			} else {
				holder.text.setTextColor(App.getApp().getResources()
						.getColor(R.color.gray_for_more));
			}

			holder.firstLevelImage.setVisibility(View.GONE);
			holder.firstLevelImageWorkaround.setVisibility(View.GONE);

		}

		txtTitle.setText(navDrawerItem.getTitle());

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (navDrawerItem.shouldOpenDrawer()
						&& (navDrawerItem.isExpandable())) {

					if (isInOppeningCategoryProgress == false) {
						isInOppeningCategoryProgress = true;
						if (navDrawerItem.isOpen()) {
							navDrawerItem.setOpen(false);
							handleFirstItemElementClosing(
									navDrawerItem,
									position,
									((ViewHolder) v.getTag()).firstLevelImage,
									((ViewHolder) v.getTag()).firstLevelImageWorkaround);
						} else {
							navDrawerItem.setOpen(true);
							handleFirstItemElementOppening(
									navDrawerItem,
									position,
									((ViewHolder) v.getTag()).firstLevelImage,
									((ViewHolder) v.getTag()).firstLevelImageWorkaround);

						}

					}

				} else {

					displayView(navDrawerItem.getLocationInTaxonsStore(),
							navDrawerItem.getTitle());

				}

			}
		});

		return convertView;
	}

	private void displayView(final ArrayList<Integer> placeHolders,
			final String title) {
		mainActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mainActivity.displayView(new ArrayList<Integer>(placeHolders),
						title.replace(SPACES_FOR_SONS, ""));
			}
		});
	}

}
