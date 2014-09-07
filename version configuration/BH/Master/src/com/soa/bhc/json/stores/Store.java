package com.soa.bhc.json.stores;

import java.util.List;

import org.apache.http.NameValuePair;

import android.util.Pair;

import com.google.gson.Gson;
import com.soa.bhc.comm.GetRequest;
import com.soa.bhc.comm.PostRequest;
import com.soa.bhc.comm.PutRequest;

public abstract class Store {

	public enum REQUEST_TYPE {

		POST, GET, PUT
	}

	protected Pair<String, String> getHeader() {
		return null;
	}

	protected abstract Store getStore();

	protected abstract void setStore(Store store);

	protected abstract String getUrl();

	protected abstract REQUEST_TYPE getRequstType();

	protected abstract List<NameValuePair> getPostParams();

	protected static Gson gson = null;

	protected Boolean initialized = false;

	protected Store() {
		gson = new Gson();
	}

	public void loadDataIfNotInitialized(
			final StoreLoadDataListener storeLoadDataListener) {
		if (initialized == false) {
			loadData(storeLoadDataListener);
		} else {
			if (getStore() != null)
				storeLoadDataListener.onFinish();
		}
	}

	// should improve this design, alot of code duplicate.
	private void loadData(
			final StoreLoadDataListener initInfoStoreFinishLoadingDataListener) {

		switch (getRequstType()) {
		case GET:

			GetRequest sgr = new GetRequest() {

				@Override
				public void onFinish(final String jsonResponse) {
					setStore(createInstanceFromJson(jsonResponse));
					if (initInfoStoreFinishLoadingDataListener != null)
						initInfoStoreFinishLoadingDataListener.onFinish();

				}

				@Override
				public void onError() {
					initInfoStoreFinishLoadingDataListener.onError();

				}
			};
			sgr.sendRequest(getUrl(), getHeader());

			break;
		case POST:

			PostRequest postRequest = new PostRequest() {

				@Override
				public void onFinish(String jsonResponse) {

					try {
						Store store = createInstanceFromJson(jsonResponse);
						setStore(store);
						if (initInfoStoreFinishLoadingDataListener != null)
							initInfoStoreFinishLoadingDataListener.onFinish();
					} catch (Exception e) {
						onError();
					}

				}

				@Override
				public void onError() {
					if (initInfoStoreFinishLoadingDataListener != null)
						initInfoStoreFinishLoadingDataListener.onError();

				}
			};
			postRequest.sendRequest(getUrl(), getPostParams());
			break;

		case PUT:

			PutRequest putRequest = new PutRequest() {

				@Override
				public void onFinish(String jsonResponse) {

					try {
						Store store = createInstanceFromJson(jsonResponse);
						setStore(store);
						if (initInfoStoreFinishLoadingDataListener != null)
							initInfoStoreFinishLoadingDataListener.onFinish();
					} catch (Exception e) {
						onError();
					}

				}

				@Override
				public void onError() {
					if (initInfoStoreFinishLoadingDataListener != null)
						initInfoStoreFinishLoadingDataListener.onError();

				}
			};
			putRequest.sendRequest(getUrl(), getPostParams());
			break;
		}

	}

	protected Store createInstanceFromJson(String jsonString) {

		Store retStore = (gson.fromJson(jsonString, getClass()));
		retStore.initialized = true;
		return retStore;
	}

	public void restartStore() {
		initialized = false;
	}

}
