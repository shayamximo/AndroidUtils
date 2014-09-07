package com.renuar.json.helpers;

import java.util.ArrayList;

import com.renuar.json.Taxon;

public class CollapsibleCategoryProperties {

	private ArrayList<Integer> placeholders;
	private Taxon taxon;
	private boolean shouldOpenList;
	private boolean isShowingSubCategories;

	public CollapsibleCategoryProperties(ArrayList<Integer> placeholders,
			Taxon taxon, boolean shouldOpenList) {
		super();
		this.placeholders = placeholders;
		this.taxon = taxon;
		this.shouldOpenList = shouldOpenList;
		isShowingSubCategories = false;
	}

	public ArrayList<Integer> getPlaceholders() {
		return placeholders;
	}

	public void setPlaceholders(ArrayList<Integer> placeholders) {
		this.placeholders = placeholders;
	}

	public Taxon getTaxon() {
		return taxon;
	}

	public void setTaxon(Taxon taxon) {
		this.taxon = taxon;
	}

	public boolean isShouldOpenList() {
		return shouldOpenList;
	}

	public void setShouldOpenList(boolean shouldOpenList) {
		this.shouldOpenList = shouldOpenList;
	}

	public boolean isShowingSubCategories() {
		return isShowingSubCategories;
	}

	public void setShowingSubCategories(boolean isShowingSubCategories) {
		this.isShowingSubCategories = isShowingSubCategories;
	}

}
