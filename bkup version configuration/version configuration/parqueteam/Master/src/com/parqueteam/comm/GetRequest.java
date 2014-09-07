package com.parqueteam.comm;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Pair;

public abstract class GetRequest extends Request {

	public void sendRequest(final String url, final Pair<String, String> header) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					HttpClient client = new DefaultHttpClient();
					HttpGet httpget = new HttpGet(url);

					if (header != null)
						httpget.setHeader(header.first, header.second);

					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					String jsonResponse = client.execute(httpget,
							responseHandler);
					onFinish(jsonResponse);

				} catch (Exception e) {

					onError();
				}

			}
		}).start();

	}

}
