package com.soa.bhc.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Pair;

import com.google.gson.annotations.SerializedName;
import com.soa.bhc.GlobalConstants;
import com.soa.bhc.json.stores.TaxonsStore;

public class Taxon {

	private int depth;

	@SerializedName("id")
	private int id;

	@SerializedName("kind")
	private String kind;

	@SerializedName("name")
	private String name;

	@SerializedName("description")
	private String description;

	@SerializedName("taxons")
	private List<Taxon> taxonList;

	@SerializedName("image_url")
	private String imageUrl;

	private List<Product> productsList;

	public int getId() {
		return id;
	}

	public void addProduct(Product product) {
		if (productsList == null)
			productsList = new ArrayList<Product>();
		productsList.add(product);
	}

	public ArrayList<Integer> getTaxonsIdsList() {
		if (taxonList == null || (taxonList.size() == 0))
			return null;
		ArrayList<Integer> taxIds = new ArrayList<Integer>();
		for (Taxon taxon : taxonList)
			taxIds.add(taxon.getId());
		return taxIds;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public List<Taxon> getTaxons() {
		return taxonList;
	}

	public void setTaxons(List<Taxon> taxons) {
		this.taxonList = taxons;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Product> getProducts() {
		return productsList;
	}

	// return place holder, and it's corresponding products list.
	public List<Pair<Integer, List<Product>>> getProductsByTaxonIds(
			ArrayList<Integer> chosenTaxonIds) {

		List<Pair<Integer, List<Product>>> pairTaxonIdToProducts = new ArrayList<Pair<Integer, List<Product>>>();

		List<Pair<Integer, Taxon>> taxonListWithPlaceHoldersFromChosenIds = getTaxonsByIds(chosenTaxonIds);

		for (Pair<Integer, Taxon> placeHolderToTaxon : taxonListWithPlaceHoldersFromChosenIds) {
			ArrayList<Product> tempProductList = new ArrayList<Product>();

			Taxon taxon = placeHolderToTaxon.second;

			for (Product product : taxon.getProducts()) {
				tempProductList.add(product);
			}
			Pair<Integer, List<Product>> newPair = new Pair<Integer, List<Product>>(
					placeHolderToTaxon.first, tempProductList);
			pairTaxonIdToProducts.add(newPair);

		}

		return pairTaxonIdToProducts;
	}

	private List<Pair<Integer, Taxon>> getTaxonsByIds(
			ArrayList<Integer> chosenIdList) {

		List<Pair<Integer, Taxon>> placeHolderToTaxonPairList = new ArrayList<Pair<Integer, Taxon>>();
		for (Integer id : chosenIdList) {
			for (int i = 0; i < taxonList.size(); i++) {
				Taxon testingTaxon = taxonList.get(i);
				if (testingTaxon.getId() == id) {
					Pair<Integer, Taxon> placeHolderToTaxon = new Pair<Integer, Taxon>(
							i, testingTaxon);
					placeHolderToTaxonPairList.add(placeHolderToTaxon);
					break;
				}
			}

		}

		return placeHolderToTaxonPairList;
	}

	public void setProducts(List<Product> productsList) {
		this.productsList = productsList;

		if (taxonList != null && taxonList.size() > 0) {
			for (Product product : productsList) {
				List<Integer> taxonsIdList = product.getTaxonIds();

				for (Taxon taxon : taxonList) {
					for (Integer id : taxonsIdList) {
						if (id.equals(taxon.getId())) {
							taxon.addProduct(product);
						}
					}
					// List<Taxon> grandTaxonsList = taxon.getTaxons();
					// if (grandTaxonsList != null && grandTaxonsList.size() >
					// 0) {
					// for (Taxon taxonGrand : grandTaxonsList) {
					// for (Integer id : taxonsIdList) {
					// if (id.equals(taxonGrand.getId())) {
					// taxonGrand.addProduct(product);
					// }
					// }
					// }
					//
					// }
				}
			}
		}
	}

	public String getImageUrl() {
		if (imageUrl != null && imageUrl.length() > 0
				&& (!imageUrl.equals("false")))
			return GlobalConstants.PREFIX + imageUrl;
		return null;
	}

	public int addLeafsToHashMap(HashMap<Integer, Taxon> map) {
		if (taxonList.size() == 0) {
			depth = 0;
			map.put(id, this);
			return 0;
		}

		int max = 0;
		for (Taxon taxon : taxonList) {
			map.put(id, this);
			int tempDepth = (taxon.addLeafsToHashMap(map)) + 1;
			if (max < tempDepth)
				max = tempDepth;

		}
		depth = max;
		return max;
	}

	public int getDepth() {
		return depth;
	}

	public String getDescription() {
		return description;
	}

	public boolean isExpandableTaxon() {
		if (description == null)
			return true;
		if (description.equals("DONT_EXPAND"))
			return false;
		return true;

	}

}
