package com.parqueteam.pagefragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parqueteam.DeepLinkController;
import com.parqueteam.GlobalConstants;
import com.parqueteam.MainActivity;
import com.parqueteam.R;
import com.parqueteam.Splash;
import com.parqueteam.json.Page;
import com.parqueteam.json.Params;
import com.parqueteam.json.Product;
import com.parqueteam.json.Taxon;
import com.parqueteam.json.stores.InitInfoStore;
import com.parqueteam.json.stores.StoreLoadDataListener;
import com.parqueteam.json.stores.TaxonomiesStore;
import com.parqueteam.json.stores.TaxonsStore;
import com.parqueteam.utils.MximoLog;
import com.parqueteam.utils.ProgressDialogFragment;

public class FragmentRoot extends Fragment {

	boolean wasInit = false;
	public static String KEY_NAME_OF_CHILD = "key_name_of_child";
	public static String KEY_IS_FIRST_LEVEL_FRAGMENT = "key_is_first_level_fragment";
	public static final String KEY_FILTERED_TAXONS = "key_filtered_taxons";
	public static final String CATEGORY_TITLE_KEY = "category_title_key";
	public static final String TAXON_ADDRESS_KEY = "taxon_address_key";
	public static final String PRODUCT_IN_TAXON_ADDRESS_KEY = "product_in_taxon_address_key";
	public static final String SHOW_ONLY_CHOSEN_PRODUCT = "show_only_product";
	public static final String FROM_FAVORITES_PAGE = "from_favorites_page";
	public static final String FROM_SEARCH_PAGE = "from_search_page";
	public static final String SEARCH_PAGE_PRODUCT = "search_page_product";
	public static final String CART_ITEM_ADDRESS = "cart_item_address";
	private MainActivity mainActivity;
	private DeepLinkController dlc;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		return inflater.inflate(R.layout.tab_root, container, false);
	}

	public void hideTabs() {
		mainActivity.hideTabs();
	}

	public void showTabs() {
		mainActivity.showTabs();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState == null) {

			if (!wasInit) {
				wasInit = true;
				getChildFragmentManager().beginTransaction()
						.addToBackStack(null)
						.add(R.id.fragment_container, createInitialChild())
						.commit();
			}
		}
		mainActivity = (MainActivity) getActivity();
	}

	private void openCatagoryFragment(Bundle args, String title) {
		openFragment("category", args);
	}

	// function return if a bind was done or not
	private boolean bindTitle(String title, Bundle args) {
		if (title != null) {
			Page bindingPage = InitInfoStore.getInstance()
					.getBindingPageByName(title);
			if (bindingPage != null) {
				openFragment(bindingPage.getName(), args);
				return true;
			}
		} else {
			MximoLog.e("ERROR",
					"when calling a onCategoryChosen in FragmentRoot, a title must be attatched");
		}

		return false;
	}

	public void onCategoryChosen(List<Integer> prePlaceHolders,
			Integer positionChosen, final String title,
			boolean rootCalledFromHomePage) {
		Bundle args = new Bundle();
		final ArrayList<Integer> placeHoldersForNextFragment = new ArrayList<Integer>(
				prePlaceHolders);
		if (positionChosen != null)
			placeHoldersForNextFragment.add(positionChosen);

		args.putIntegerArrayList(TAXON_ADDRESS_KEY, placeHoldersForNextFragment);
		args.putString(CATEGORY_TITLE_KEY, title);
		args.putIntegerArrayList(FragmentPageBase.LIST_OF_PLACEHOLDERS_KEY,
				placeHoldersForNextFragment);

		boolean isBind = bindTitle(title, args);
		if (isBind)
			return;

		if (TaxonomiesStore.getInstance().isLook(placeHoldersForNextFragment)) {

			final Taxon currentTaxon = TaxonomiesStore.getInstance()
					.getTaxonsByPlaceHolder(placeHoldersForNextFragment);
			if (currentTaxon.getDepth() > 1) {

				openCatagoryFragment(args, title);
			} else {

				openLookFragment(args);
			}

			return;
		} else {

			final Taxon currentTaxon = TaxonomiesStore.getInstance()
					.getTaxonsByPlaceHolder(placeHoldersForNextFragment);

			if (currentTaxon.getDepth() == 1
					&& ((rootCalledFromHomePage) || InitInfoStore.getInstance()
							.isCategoryDrillDown())) {

				openCatagoryFragment(args, title);
				return;
			}

			if (currentTaxon.getDepth() > 1) {

				openCatagoryFragment(args, title);
			}

			// in this case depth =1,0, and
			// there will be grid with or without refine, accordingly
			else {

				final Taxon taxons = TaxonomiesStore.getInstance()
						.getTaxonsByPlaceHolder(placeHoldersForNextFragment);

				final List<Product> productsList = TaxonomiesStore
						.getInstance()
						.getTaxonsByPlaceHolder(placeHoldersForNextFragment)
						.getProducts();

				if (productsList == null) {
					final TaxonsStore taxonsStore = new TaxonsStore(
							taxons.getId());

					final ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
					progressDialogFragment.show(getFragmentManager(),
							"MyProgressDialog");
					progressDialogFragment.setCancelable(false);
					taxonsStore
							.loadDataIfNotInitialized(new StoreLoadDataListener() {

								@Override
								public void onFinish() {
									progressDialogFragment.dismiss();
									taxons.setProducts(taxonsStore
											.getProducts());

									openGridFragment(
											placeHoldersForNextFragment, title);
								}

								@Override
								public void onError() {
									// Do nothing

								}
							});
				}

				else {
					openGridFragment(placeHoldersForNextFragment, title);
				}

			}
		}

	}

	/**
	 * 
	 * @param placeHolders
	 * @param title
	 * @return if there are products defined for the grid
	 */
	private boolean openGridFragment(ArrayList<Integer> placeHolders,
			String title) {
		Bundle args = new Bundle();

		final List<Product> productsList = TaxonomiesStore.getInstance()
				.getTaxonsByPlaceHolder(placeHolders).getProducts();

		if (productsList == null)
			return false;

		if (productsList.size() > 1) {
			args.putIntegerArrayList(FragmentRoot.TAXON_ADDRESS_KEY,
					placeHolders);
			args.putString(FragmentRoot.CATEGORY_TITLE_KEY, title);
			openFragment("collection", args);
		}

		else {

			args.putInt(FragmentRoot.PRODUCT_IN_TAXON_ADDRESS_KEY, 0);
			args.putIntegerArrayList(FragmentRoot.TAXON_ADDRESS_KEY,
					placeHolders);
			args.putString(FragmentRoot.CATEGORY_TITLE_KEY, title);
			openItemFragment(args);
		}
		return true;

	}

	public void openChosenPromotionFragment(Bundle args,boolean popCurrentFragment) {
		if (popCurrentFragment) {
			FragmentManager fm = getChildFragmentManager();
			fm.popBackStack();
			args.putBoolean(KEY_IS_FIRST_LEVEL_FRAGMENT, true);
		}
		openFragmnet(new FragmentPromotionChosen(), args);
	}

	public void openPathFromChosenPromotionFragment(Bundle args) {
		openFragmnet(new FragmentPathChosenFromPromotions(), args);
	}

	public void openShopListFragment(Bundle args) {
		openFragmnet(new ShopListFragment(), args);
	}

	public void openShopDetailsFragment(Bundle args, boolean popCurrentFragment) {
		if (popCurrentFragment) {
			FragmentManager fm = getChildFragmentManager();
			fm.popBackStack();
		}

		openFragmnet(new ShopDetailsFragment(), args);
	}

	public void openGoogleMapFragment(Bundle args) {
		openFragmnet(new GoogleMapFragment(), args);
	}

	public void openFragment(String fragmentKey, Bundle args) {

		FragmentPageBase fpb = createFragmentFromKey(fragmentKey);
		openFragmnet(fpb, args);

	}

	public void openFragmentFromActionLabel(String fragmentKey, Bundle args) {

		Params pageParams = InitInfoStore.getInstance()
				.getPageByName(fragmentKey).getParamsByName("promote_first");
		if (pageParams != null) {
			Bundle argsForPromotion = new Bundle();
			argsForPromotion
					.putInt(FragmentPromotionChosen.PROMOTION_CHOSEN_ADDRESS_OF_PROMOTION,
							0);

			openChosenPromotionFragment(argsForPromotion,false);
		}

		else {
			openFragment(fragmentKey, args);
		}

	}

	public void openFragmnet(FragmentPageBase fpb, Bundle args) {
		if (args != null)
			fpb.setArguments(args);
		else
			fpb.setArguments(new Bundle());
		getChildFragmentManager().beginTransaction().addToBackStack(null)
				.replace(R.id.fragment_container, fpb).commit();
	}

	public FragmentPageBase getCurrentFragment() {
		FragmentManager fm = getChildFragmentManager();
		List<Fragment> listOfAllFragments = fm.getFragments();
		int sizeOfListOfAllFragments = getSizeOfFragmentList(listOfAllFragments);
		FragmentPageBase returnFragment = (FragmentPageBase) listOfAllFragments
				.get(sizeOfListOfAllFragments - 1);
		return returnFragment;
	}

	// this function is because when we get a list
	// of fragments using getFragments(), if in location i, there is null,
	// it would be counted as valid, and therefor it will be counted, when
	// calculating size.
	// therefor we define a helper function, which finds the first null, and
	// that will be
	// the real size
	private int getSizeOfFragmentList(List<Fragment> listOfAllFragments) {
		int sizeFromList = listOfAllFragments.size();
		for (int i = 0; i < sizeFromList; i++) {
			if (listOfAllFragments.get(i) == null)
				return i;
		}
		return sizeFromList;
	}

	public int getSizeOfFragmentList() {
		FragmentManager fm = getChildFragmentManager();
		List<Fragment> listOfAllFragments = fm.getFragments();
		return getSizeOfFragmentList(listOfAllFragments);
	}

	public int getNumberOfFragmentsInStack() {
		FragmentManager fm = getChildFragmentManager();
		return fm.getBackStackEntryCount();
	}

	public boolean onBackPressed() {
		FragmentManager fm = getChildFragmentManager();

		if (fm.getBackStackEntryCount() == 1) {
			return false;
		} else {
			fm.popBackStack();
			return true;
		}
	}

	// Function which acts as the equivelent of activity's
	// onActivityResult
	public void goToPreviousFragment(String keyString, String string,
			String keyInt, int i, String keyArrayInt,
			ArrayList<Integer> integerArrayList) {
		FragmentManager fm = getChildFragmentManager();

		if (fm.getBackStackEntryCount() == 1) {
			{
			}
		} else {

			int numberOfFragments = fm.getFragments().size();

			if (numberOfFragments > 1) {
				FragmentPageBase fpb = (FragmentPageBase) fm.getFragments()
						.get(numberOfFragments - 2);
				Bundle args = fpb.getArguments();
				if (keyString != null)
					args.putString(keyString, string);
				if (keyInt != null)
					args.putInt(keyInt, i);
				if (keyArrayInt != null)
					args.putIntegerArrayList(keyArrayInt, integerArrayList);
			}

			fm.popBackStack();
		}
	}

	public FragmentPageBase createFragmentFromKey(String fragmentKey) {
		FragmentPageBase fragmentPageBase = null;

		String className = InitInfoStore.getInstance().getFullClassNameByKey(
				fragmentKey);

		boolean addParamsToFragment = true;

		if (className == null) {
			className = GlobalConstants.PAGE_FRAGMENTS_PACKAGE + fragmentKey;
			addParamsToFragment = false;
		}

		try {
			fragmentPageBase = (FragmentPageBase) Class.forName(className)
					.newInstance();

			if (addParamsToFragment) {
				List<Params> paramsList = InitInfoStore.getInstance()
						.getPageParamsByName(fragmentKey);

				fragmentPageBase.setParamsListFromServer(fragmentKey,
						paramsList);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
		return fragmentPageBase;
	}

	Fragment createInitialChild() {

		Bundle args = getArguments();

		String className = args.getString(MainActivity.CLASS_NAME_KEY);
		dlc = (DeepLinkController) args.getSerializable(Splash.KEY_DEEP_LINK_CONTROLLER);
		FragmentPageBase fragmentPageBase = createFragmentFromKey(className);

		Bundle send = new Bundle();
		send.putString(KEY_NAME_OF_CHILD, this.getTag());
		send.putBoolean(KEY_IS_FIRST_LEVEL_FRAGMENT, true);
		fragmentPageBase.setArguments(send);
		return fragmentPageBase;
	}

	public MainActivity getMainActivity() {
		return mainActivity;
	}

	private void openLookFragment(Bundle args) {
		openFragment("look", args);
	}

	public void openItemFragment(Bundle args) {
		openFragment("item", args);
	}

	public void openUserInfoFragment(Bundle args) {
		openFragment("userinfo", args);
	}

	public void openSearchFragment(Bundle args) {
		openFragment("search", args);
	}

	public void openShippingFragment(Bundle bundle) {
		openFragment("shipinfo", bundle);
	}

	public void openFavoritesFragment() {
		openFragment("wishlist", null);
	}

	public void openTermsFragment() {
		openFragment("terms", null);
	}

	public void openStoresMapFragment() {
		openFragment("stores", null);
	}

	@Override
	public void onResume() {
		
		super.onResume();
		if (dlc!=null)
		{
			dlc.handleDeepLinkEvent(getMainActivity());
			//we only want to do this once.
			dlc = null;
		}
	}

}
