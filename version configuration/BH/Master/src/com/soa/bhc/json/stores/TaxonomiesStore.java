package com.soa.bhc.json.stores;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.soa.bhc.GlobalConstants;
import com.soa.bhc.json.Taxon;
import com.soa.bhc.json.helpers.CollapsibleCategoryProperties;
import com.soa.bhc.utils.ConfigurationFile;

public class TaxonomiesStore extends Store {

	@SerializedName("count")
	private int count;

	@SerializedName("taxonomies")
	private List<Taxon> taxonList;

	HashMap<Integer, Taxon> keyToNameMap;

	private static TaxonomiesStore taxonomiesStore = null;

	private TaxonomiesStore() {
		super();
	}

	@Override
	protected Store getStore() {
		return taxonomiesStore;
	}

	@Override
	protected void setStore(Store store) {
		this.taxonomiesStore = (TaxonomiesStore) store;

	}

	private void initializeMap() {
		keyToNameMap = new HashMap<Integer, Taxon>();
		for (Taxon taxon : taxonList)
			taxon.addLeafsToHashMap(keyToNameMap);
	}

	public String getNameByTaxonLeafID(Integer key) {
		return keyToNameMap.get(key).getName();
	}

	public String getNameOfLeafIfStarred(Integer key) {
		String description = keyToNameMap.get(key).getDescription();
		if (description == null)
			return null;
		if (!description.equals("SHOW_CATEGORY"))
			return null;

		return getNameByTaxonLeafID(key);
	}

	@Override
	protected Store createInstanceFromJson(String jsonString) {

		TaxonomiesStore taxonomiesStore = (TaxonomiesStore) super
				.createInstanceFromJson(jsonString);

		taxonomiesStore.initializeMap();
		return taxonomiesStore;
	}

	@Override
	protected String getUrl() {

		return GlobalConstants.PREFIX + "/apps/"
				+ ConfigurationFile.getInstance().getAppAddress()
				+ "/api/taxonomies";
	}

	public static TaxonomiesStore getInstance() {

		if (taxonomiesStore == null) {
			taxonomiesStore = new TaxonomiesStore();
			gson = new Gson();
		}

		return taxonomiesStore;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<Taxon> getTaxonomies() {
		return taxonList;
	}

	public void setTaxonomies(List<Taxon> taxonomies) {
		this.taxonList = taxonomies;
	}

	public List<String> getTaxonsTopLevelNames() {

		ArrayList<String> names = new ArrayList<String>();
		for (Taxon taxonomy : taxonList) {
			for (Taxon taxon : taxonomy.getTaxons()) {
				names.add(taxon.getName());
			}
		}

		return names;
	}

	public List<ArrayList<String>> getTaxonsTopLevelNamesSortedByIndex() {

		List<ArrayList<String>> topLevelNamesSortedByIndex = new ArrayList<ArrayList<String>>();

		for (Taxon taxonomy : taxonList) {

			ArrayList<String> names = new ArrayList<String>();

			for (Taxon taxon : taxonomy.getTaxons()) {
				names.add(taxon.getName());
			}
			topLevelNamesSortedByIndex.add(names);

		}

		return topLevelNamesSortedByIndex;
	}

	// This function will only work with collapsible!!
	public ArrayList<Integer> getPlaceHoldersByStringForCollapsibleCategory(
			ArrayList<String> strList) {
		ArrayList<Integer> sendArray = new ArrayList<Integer>();
		// right now the assumption is that its in the first cell in the array.
		sendArray.add(0);
		ArrayList<Integer> intList = getPlaceHoldersByStringForCollapsibleCategory(
				strList, sendArray, taxonList.get(0).getTaxons());
		if (intList.size() == 1) {
			sendArray.clear();
			sendArray.add(1);
			intList = getPlaceHoldersByStringForCollapsibleCategory(strList,
					sendArray, taxonList.get(1).getTaxons());

		}
		return intList;
	}

	// This function will only work with collapsible!!
	private ArrayList<Integer> getPlaceHoldersByStringForCollapsibleCategory(
			ArrayList<String> strList, ArrayList<Integer> retArray,
			List<Taxon> tempTaxonsList) {
		if (strList.size() > 0) {
			for (int i = 0; i < tempTaxonsList.size(); i++) {
				if (tempTaxonsList.get(i).getName().equals(strList.get(0))) {
					strList.remove(0);
					retArray.add(i);
					tempTaxonsList.get(i).getTaxons();
					return getPlaceHoldersByStringForCollapsibleCategory(
							strList, retArray, tempTaxonsList.get(i)
									.getTaxons());

				}
			}
		}
		return retArray;
	}

	public List<List<String>> getTaxonsTopLevelNamesByIndex() {

		ArrayList<List<String>> names = new ArrayList<List<String>>();
		for (Taxon taxonomy : taxonList) {
			ArrayList<String> curList = new ArrayList<String>();
			for (Taxon taxon : taxonomy.getTaxons()) {
				curList.add(taxon.getName());
			}
			names.add(curList);
		}

		return names;
	}

	public Taxon getTaxonsByPlaceHolder(int placeHolder) {
		return taxonList.get(placeHolder);
	}

	public Taxon getTaxonsByPlaceHolder(List<Integer> placeHolders) {

		List<Integer> tempPlaceHolders = new ArrayList<Integer>(placeHolders);

		ArrayList<Taxon> retTaxons = new ArrayList<Taxon>();

		if (tempPlaceHolders.size() == 1) {
			return taxonList.get(tempPlaceHolders.get(0));
		}

		int firstLocation = tempPlaceHolders.remove(0);

		return getTaxonsByPlaceHolderRecursive(taxonList.get(firstLocation),
				tempPlaceHolders);

	}

	private Taxon getTaxonsByPlaceHolderRecursive(Taxon taxons,
			List<Integer> placeHolders) {

		if (placeHolders.size() == 1) {
			return taxons.getTaxons().get(placeHolders.get(0));
		}

		int firstLocation = placeHolders.remove(0);

		return getTaxonsByPlaceHolderRecursive(
				taxons.getTaxons().get(firstLocation), placeHolders);
	}

	public boolean isTaxon(ArrayList<Integer> placeHolders) {
		return (taxonList.get(placeHolders.get(0)).getKind().equals("category"));
	}

	public boolean isLook(ArrayList<Integer> placeHolders) {
		return (taxonList.get(placeHolders.get(0)).getKind().equals("look"));
	}

	@Override
	protected REQUEST_TYPE getRequstType() {
		return REQUEST_TYPE.GET;
	}

	@Override
	protected List<NameValuePair> getPostParams() {
		return null;
	}

	public ArrayList<CollapsibleCategoryProperties> getCollapsibleCategoryPropertiesList(
			String categories) {

		ArrayList<String> categoryList = new ArrayList<String>(
				Arrays.asList(categories.split(",")));

		ArrayList<CollapsibleCategoryProperties> listCollapsableCategoryProps = new ArrayList<CollapsibleCategoryProperties>();
		for (int i = 0; i < categoryList.size(); i++) {

			ArrayList<String> subCategoriesList = new ArrayList<String>(
					Arrays.asList(categoryList.get(i).split("/")));

			int sizeOfSubCategoriesList = subCategoriesList.size();
			ArrayList<Integer> placeHolders = TaxonomiesStore.getInstance()
					.getPlaceHoldersByStringForCollapsibleCategory(
							subCategoriesList);
			Taxon taxon = TaxonomiesStore.getInstance().getTaxonsByPlaceHolder(
					placeHolders);
			boolean isLook = TaxonomiesStore.getInstance().isLook(placeHolders);
			boolean shouldOpenList = (((sizeOfSubCategoriesList == 1)
					&& (taxon.isExpandableTaxon())
					&& ((isLook)?(taxon.getDepth()>=2):(true))
					&& (taxon.getTaxons() != null) && (taxon.getTaxons().size() > 0)) ? true
					: false);

			listCollapsableCategoryProps.add(i,
					new CollapsibleCategoryProperties(placeHolders, taxon,
							shouldOpenList));

		}

		return listCollapsableCategoryProps;
	}

}
