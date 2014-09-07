package com.renuar.pagefragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.renuar.App;
import com.renuar.GlobalConstants;
import com.renuar.MainActivity;
import com.renuar.R;
import com.renuar.json.OptionValues;
import com.renuar.json.Product;
import com.renuar.json.Variant;
import com.renuar.json.stores.InitInfoStore;
import com.renuar.userselection.UserSelectionItem;
import com.renuar.userselection.UserSelectionStore;
import com.renuar.utils.UtilityFunctions;

public class WishListViewController extends FragmentPageBase implements
		ICustomizedEmptyListFragment {

	private int imageWidth, imageHeight;
	private TextView total;
	private TextView totalTitle;
	private UserSelectionStore favorite;
	private ListProductAdaptor lpaAdaptor;

	protected int getLayoutID() {
		if (shouldCustomizeEmptyList(this)) {
			return R.layout.layout_empty_list_background_configured;
		}
		return R.layout.my_bag;
	}

	@Override
	protected void initializeActionBar() {

		actionBarContorller.textViewMainTitle.setText(getResources().getString(
				R.string.my_favorites));

		final ImageView edit = actionBarContorller.imageViewOnRightStandalone;
		actionBarContorller.setEditDrawable(edit);

		if (!shouldCustomizeEmptyList(this))
			makeViewsVisible(edit);

		edit.setTag(Boolean.valueOf(true));
		edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				Boolean isShowingEdit = (Boolean) view.getTag();

				if (isShowingEdit) {
					actionBarContorller.setAcceptDrawable(edit);
				} else {
					actionBarContorller.setEditDrawable(edit);
				}
				view.setTag(!isShowingEdit);

				lpaAdaptor.toggleDeleteButtonsVisibility();
			}
		});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		favorite = App.getApp().getFavorite();

		if (shouldCustomizeEmptyList(this)) {
			loadEmptyListFragment(this);
		} else {

			setViewsToBrandColor(R.id.my_bag_btn_checkout, R.id.share);
			getImagesSize();

			viewOfFragment.findViewById(R.id.layout_title).setVisibility(
					View.GONE);
			viewOfFragment.findViewById(R.id.layout_bottom_for_cart)
					.setVisibility(View.GONE);
			viewOfFragment.findViewById(R.id.layout_bottom_for_favorites)
					.setVisibility(View.VISIBLE);

			total = (TextView) viewOfFragment
					.findViewById(R.id.total_text_favorite_screen);

			totalTitle = (TextView) viewOfFragment
					.findViewById(R.id.total_title_favorite_screen);

			double totalPrice = favorite.getTotalAmountOfPrice();

			if (totalPrice > 0)
				total.setText(generateProperStringForPrice(totalPrice));

			else {
				totalTitle.setVisibility(View.GONE);
			}

			final List<UserSelectionItem> chosenCartItems = favorite
					.getAllCartItems();
			lpaAdaptor = new ListProductAdaptor(getActivity(),
					R.layout.lyt_mybag, chosenCartItems);
			((ListView) viewOfFragment.findViewById(R.id.cart_listview))
					.setAdapter(lpaAdaptor);

			Button shareButton = (Button) viewOfFragment
					.findViewById(R.id.share);

			String pageAction = getValueOfParamFromName("page_action");
			if (pageAction != null && pageAction.equals("nearest_store")) {

				shareButton
						.setText(getValueOfParamFromName("page_action_label"));
				shareButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						boolean isClosestPlaceSet = App.getApp().getShopStore()
								.isListInitialized();
						if (isClosestPlaceSet) {

							Bundle bundle = new Bundle();
							bundle.putBoolean(
									ShopDetailsFragment.DISPLAY_CLOSEST_SHOP,
									true);

							fragmentRoot.openShopDetailsFragment(bundle, false);

						} else {
							Bundle bundle = new Bundle();
							bundle.putBoolean(
									StoreMapViewController.KEY_OPEN_CLOSEST,
									true);
							fragmentRoot.openFragment("stores", bundle);
						}
					}
				});
			}

			else {

				shareButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						String textToShare = new String(
								GlobalConstants.EMPTY_STRING);
						String url = InitInfoStore.getInstance().getGoogleQr();
						ArrayList<String> urlsToAttatch = new ArrayList<String>();
						for (UserSelectionItem usi : chosenCartItems) {

							Product product = usi.getProduct();
							double priceToSendToEmail = product.getFinalPrice();

							String priceInMessage = "";

							if (priceToSendToEmail > 0) {
								priceInMessage = "Price :"
										+ product.getFinalPrice() + "<br/>";
							}

							String currentMessage = new StringBuilder()
									.append(product.getName()).append("<br/>")
									.append(priceInMessage).append("<br/>")
									.toString();

							// .append("<br/><br/>").append(url).toString();

							String currentUrl = product.getMainImageUrl();

							urlsToAttatch.add(currentUrl);
							textToShare += currentMessage;

						}

						textToShare += url;

						UtilityFunctions.sendEmail(getActivity(), textToShare,
								urlsToAttatch);
					}
				});
			}

		}
		super.onActivityCreated(savedInstanceState);
	}

	private void getImagesSize() {
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int w = dm.widthPixels;
		int h = dm.heightPixels;
		imageHeight = (int) (h * 0.18);
		imageWidth = (int) (imageHeight * 0.9);

	}

	public class ListProductAdaptor extends ArrayAdapter<UserSelectionItem> {

		private List<UserSelectionItem> listOfCartItems;
		int layoutResourceId;
		private ArrayList<ImageView> deleteButtons;
		private boolean areDeleteButtonsVisible;

		public ListProductAdaptor(Context context, int layoutResourceId,
				final List<UserSelectionItem> listOfCartItems) {
			super(context, layoutResourceId, listOfCartItems);
			this.layoutResourceId = layoutResourceId;
			this.listOfCartItems = listOfCartItems;
			deleteButtons = new ArrayList<ImageView>();
			areDeleteButtonsVisible = false;

		}

		@Override
		public int getCount() {
			return listOfCartItems.size();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			View rowView = convertView;

			if (rowView == null) {
				ViewHolder holder = new ViewHolder();

				rowView = inflator.inflate(layoutResourceId, null);
				holder.buyButton = (ViewGroup) rowView
						.findViewById(R.id.buy_button);
				setViewToBrandColor(holder.buyButton);
				holder.textName = (TextView) rowView
						.findViewById(R.id.lyt_mybag_txt_itemName);
				holder.textDescription = (TextView) rowView
						.findViewById(R.id.lyt_mybag_txt_itemDes);

				holder.textPrice = (TextView) rowView
						.findViewById(R.id.lyt_mybag_txt_itemprise);
				holder.buttonDelete = (ImageView) rowView
						.findViewById(R.id.lyt_mybag_btnDelete);
				deleteButtons.add(holder.buttonDelete);

				holder.imageItemPhoto = (ImageView) rowView
						.findViewById(R.id.lyt_mybag_img_item);
				holder.imageSale = (ImageView) rowView
						.findViewById(R.id.lyt_mybag_img_sale);
				// holder.imageSale.setVisibility(View.GONE);
				holder.imageItemPhoto
						.setLayoutParams(new RelativeLayout.LayoutParams(
								imageWidth, imageHeight));
				holder.textQuantity = (TextView) rowView
						.findViewById(R.id.lyt_mybag_txt_qty);
				holder.imageMinusOne = (ImageButton) rowView
						.findViewById(R.id.lyt_mybag_img_qtyminus);
				holder.imagePlusOne = (ImageButton) rowView
						.findViewById(R.id.lyt_mybag_img_qtyplus);

				holder.textVariants = (TextView) rowView
						.findViewById(R.id.lyt_mybag_txt_itemVariants);
				holder.textQuantityTitle = (TextView) rowView
						.findViewById(R.id.text_qty_title);

				boolean doesCartExist = InitInfoStore.getInstance()
						.doesApplicationHaveCart();
				if (doesCartExist) {
					holder.buyButton.setVisibility(View.VISIBLE);
					holder.textDescription.setVisibility(View.GONE);
				} else {
					holder.buyButton.setVisibility(View.GONE);
					holder.textDescription.setVisibility(View.VISIBLE);

				}

				rowView.setTag(holder);

			}

			final ViewHolder holder = (ViewHolder) rowView.getTag();
			final UserSelectionItem currentCartItem = listOfCartItems
					.get(position);
			final Product master = currentCartItem.getProduct();
			final Variant variant = currentCartItem.getVariant();

			rowView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Bundle args = new Bundle();
					args.putString(FragmentRoot.CATEGORY_TITLE_KEY,
							getResources().getString(R.string.mybag));
					args.putBoolean(FragmentRoot.SHOW_ONLY_CHOSEN_PRODUCT, true);

					args.putInt(FragmentRoot.CART_ITEM_ADDRESS, position);
					args.putBoolean(FragmentRoot.FROM_FAVORITES_PAGE, true);
					try {
						fragmentRoot.openItemFragment(args);
					} catch (Exception e) {
						// TODO: handle exception
					}

				}
			});

			holder.textName.setText(master.getName());
			holder.textDescription.setText(master.getDescription());

			double finalPrice = master.getFinalPrice();

			if (finalPrice > 0) {
				holder.textPrice.setText(generateProperStringForPrice(master
						.getFinalPrice()));

			}

			imageLoader.displayImage(master.getMainImageUrlThumbNailSize(),
					holder.imageItemPhoto, options, null);

			if (master.isOnSale())
				holder.imageSale.setVisibility(View.VISIBLE);
			else
				holder.imageSale.setVisibility(View.GONE);

			holder.textQuantity.setVisibility(View.GONE);

			if (variant != null) {
				holder.textVariants.setVisibility(View.VISIBLE);
				List<OptionValues> optionValuesList = variant.getOptionValues();

				holder.textVariants.setText("");
				for (OptionValues optionValues : optionValuesList) {
					String currentText = holder.textVariants.getText()
							.toString();
					holder.textVariants.setText(currentText
							+ optionValues.getOptionTypeValue() + ":"
							+ optionValues.getName() + "   ");
				}
			} else
				holder.textVariants.setText("");

			holder.buttonDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					listOfCartItems.remove(currentCartItem);
					notifyDataSetChanged();
					favorite.removeCartItem(currentCartItem);
					total.setText(generateProperStringForPrice(favorite
							.getTotalAmountOfPrice()));

				}
			});

			holder.imageMinusOne.setVisibility(View.GONE);

			holder.imagePlusOne.setVisibility(View.GONE);

			holder.textQuantityTitle.setVisibility(View.GONE);

			holder.buyButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					UserSelectionItem usi = listOfCartItems.get(position);
					App.getApp().getFavorite().removeCartItem(usi);
					App.getApp().getCart()
							.addCartItem(usi.getProduct(), usi.getVariant());
					((MainActivity) getActivity()).switchTab(2);

				}
			});

			return rowView;
		}

		public void toggleDeleteButtonsVisibility() {
			int visibility;
			if (areDeleteButtonsVisible)
				visibility = View.GONE;
			else
				visibility = View.VISIBLE;
			for (ImageView deleteButton : deleteButtons) {
				deleteButton.setVisibility(visibility);
			}
			areDeleteButtonsVisible = !areDeleteButtonsVisible;
		}

		private class ViewHolder {
			ImageView imageItemPhoto, imageSale;
			TextView textName, textDescription, textPrice, textQuantity,
					textVariants, textQuantityTitle;
			ImageButton imagePlusOne, imageMinusOne;
			ImageView buttonDelete;
			ViewGroup buyButton;

		}

	}

	@Override
	public boolean isEmptyList() {
		if (favorite == null)
			favorite = App.getApp().getFavorite();
		return (favorite == null || favorite.getAllCartItems().size() == 0);
	}

	@Override
	public int getImageResourceIdIfNoneDefinedFromServer() {

		return -1;
	}
}
