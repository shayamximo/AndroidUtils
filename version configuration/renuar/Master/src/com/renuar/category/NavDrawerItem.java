package com.renuar.category;

import java.util.ArrayList;

import com.renuar.json.Taxon;
import com.renuar.json.stores.InitInfoStore;
import com.renuar.json.stores.TaxonomiesStore;

public class NavDrawerItem {

	private String title;
	private boolean isOpen;
	private ArrayList<Integer> placeHolders;
	private boolean shouldOpenDrawer;
	private boolean firstLevelCategory;
	Taxon taxon;

	// for first level entries
	public NavDrawerItem(String title, int indexInTaxonStore,
			int locationInIndexedStore) {
		firstLevelCategory = true;
		this.title = title;
		isOpen = false;
		placeHolders = new ArrayList<Integer>();
		placeHolders.add(indexInTaxonStore);
		placeHolders.add(locationInIndexedStore);
		shouldOpenDrawer = false;
		taxon = TaxonomiesStore.getInstance().getTaxonsByPlaceHolder(
				placeHolders);
		// the isLook thing, oppening the look from sliding menu, is only good
		// for 2 levels
		// of tree, if in future want to add more, will need to imporve.
		if ((taxon.getTaxons() != null) && taxon.getTaxons().size() > 0
				&& (!TaxonomiesStore.getInstance().isLook(placeHolders)))
			shouldOpenDrawer = true;

	}

	// for second level entries
	public NavDrawerItem(String title, ArrayList<Integer> placeHolders) {
		firstLevelCategory = false;

		this.title = title;
		isOpen = false;
		this.placeHolders = placeHolders;
		shouldOpenDrawer = false;

	}

	public boolean isFirstLevelCategory() {
		return firstLevelCategory;
	}

	public boolean shouldOpenDrawer() {
		return shouldOpenDrawer;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public ArrayList<Integer> getLocationInTaxonsStore() {
		return placeHolders;
	}

	public boolean isExpandable() {
		return taxon.isExpandableTaxon();
	}

}
