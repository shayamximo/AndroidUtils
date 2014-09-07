package com.parqueteam.pagefragments;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class MximoGoogleMapsFragment extends SupportMapFragment {

	IonMximoGoogleMapsFragmentActivityCreated ionMximoGoogleMapsFragmentActivityCreated;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		if (ionMximoGoogleMapsFragmentActivityCreated != null)
			ionMximoGoogleMapsFragmentActivityCreated.onFinish(getMap());

		// onActivityCreated is sometimes called twice, this is to make sure the
		// callback
		// won't be called again
		ionMximoGoogleMapsFragmentActivityCreated = null;
	}

	public interface IonMximoGoogleMapsFragmentActivityCreated {
		public void onFinish(GoogleMap googleMap);
	}

	public void setIonMximoGoogleMapsFragmentActivityCreated(
			IonMximoGoogleMapsFragmentActivityCreated ionMximoGoogleMapsFragmentActivityCreated) {
		this.ionMximoGoogleMapsFragmentActivityCreated = ionMximoGoogleMapsFragmentActivityCreated;
	}

}
