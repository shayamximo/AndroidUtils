package com.parqueteam.json.stores;

import java.util.ArrayList;

import com.parqueteam.GlobalConstants;
import com.parqueteam.utils.ConfigurationFile;

public class TaxonsStore extends ProductsStore {

	String taxonIds;

	public TaxonsStore(Integer i) {

		taxonIds = i.toString();
	}

	public TaxonsStore(Integer parentId, ArrayList<Integer> arrTaxonIds) {

		if (arrTaxonIds == null)
			taxonIds = parentId.toString();
		else {
			taxonIds = arrTaxonIds.get(0).toString();

			for (int i = 1; i < arrTaxonIds.size(); i++) {
				taxonIds += "," + arrTaxonIds.get(i);
			}
			taxonIds += "," + parentId;
		}

	}

	protected String getUrl() {

		return GlobalConstants.PREFIX + "/apps/"
				+ ConfigurationFile.getInstance().getAppAddress()
				+ "/api/products?parent_id=" + taxonIds;
	}

}
