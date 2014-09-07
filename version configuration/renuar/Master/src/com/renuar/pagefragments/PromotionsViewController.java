package com.renuar.pagefragments;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.renuar.R;
import com.renuar.json.Promotion;
import com.renuar.json.stores.PromotionsStore;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class PromotionsViewController extends FragmentPageBase implements
		ICustomizedEmptyListFragment {

	List<Promotion> promotionList;

	private int getLayoutForEmptyList() {

		if (shouldCustomizeEmptyList(this)) {
			return R.layout.layout_empty_list_background_configured;
		}
		return R.layout.empty_promotions_layout;
	}

	@Override
	public boolean isEmptyList() {
		if (promotionList == null)
			promotionList = PromotionsStore.getInstance().getPromotionsList();

		return (promotionList == null || (promotionList.size() == 0));
	}

	@Override
	protected int getLayoutID() {

		if (isEmptyList()) {
			return getLayoutForEmptyList();
		} else
			return R.layout.category_list_fragment;
	}

	@Override
	protected void initializeActionBar() {
		// TopBarParams topBarParams = new TopBarParams();
		actionBarContorller.textViewMainTitle.setText(getResources().getString(
				R.string.offers));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		promotionList = PromotionsStore.getInstance().getPromotionsList();

		if (!(isEmptyList())) {

			PromotionsAdapter promotionsAdapter = new PromotionsAdapter(
					getActivity(), R.layout.promoitions_row, promotionList,
					inflator);
			((ListView) viewOfFragment.findViewById(R.id.listview))
					.setAdapter(promotionsAdapter);
		}

		else {
			if (shouldCustomizeEmptyList(this))
				loadEmptyListFragment(this);
		}

		super.onActivityCreated(savedInstanceState);

	}

	public class PromotionsAdapter extends ArrayAdapter<Promotion> {

		LayoutInflater inflator;
		int resource;
		List<Promotion> promotions;

		public PromotionsAdapter(Context context, int resource,
				List<Promotion> promotions, LayoutInflater inflator) {
			super(context, resource, promotions);

			this.inflator = inflator;
			this.resource = resource;
			this.promotions = promotions;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			View row = convertView;
			final Holder holder;
			if (row == null) {

				row = inflator.inflate(resource, parent, false);

				holder = new Holder();
				holder.imageView = (ImageView) row.findViewById(R.id.image);
				holder.name = (TextView) row.findViewById(R.id.name);
				holder.description = (TextView) row
						.findViewById(R.id.description);
				holder.progressLayout = (RelativeLayout) row
						.findViewById(R.id.loadingPanel);

				row.setTag(holder);
			} else {
				holder = (Holder) row.getTag();

			}

			row.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Bundle args = new Bundle();
					args.putInt(
							FragmentPromotionChosen.PROMOTION_CHOSEN_ADDRESS_OF_PROMOTION,
							position);

					fragmentRoot.openChosenPromotionFragment(args);

				}
			});

			Promotion currentPromotion = promotions.get(position);
			holder.name.setText(currentPromotion.getName());
			holder.description.setText(currentPromotion.getDescription());

			imageLoader.displayImage(currentPromotion.getImageUrl(),
					holder.imageView, options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							holder.progressLayout.setVisibility(View.VISIBLE);

						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							holder.progressLayout.setVisibility(View.GONE);

						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							holder.progressLayout.setVisibility(View.GONE);

						}
					}, new ImageLoadingProgressListener() {
						@Override
						public void onProgressUpdate(String imageUri,
								View view, int current, int total) {

						}
					});

			return row;
		}

		public class Holder {
			public TextView name;
			public TextView description;
			public ImageView imageView;
			RelativeLayout progressLayout;
		}

	}

	@Override
	public int getImageResourceIdIfNoneDefinedFromServer() {
		return R.drawable.offer_icon;
	}

}
