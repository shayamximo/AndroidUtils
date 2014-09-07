package com.parqueteam.comm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public abstract class PostRequest extends Request {

	public void sendRequest(final String url,
			final List<NameValuePair> nameValuePairsList) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// Creating HTTP client
				HttpClient httpClient = new DefaultHttpClient();
				// Creating HTTP Post
				HttpPost httpPost = new HttpPost(url);

				// Building post parameters
				// key and value pair

				// Url Encoding the POST parameters
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(
							nameValuePairsList, "utf-8"));
				} catch (UnsupportedEncodingException e) {
					// writing error to Log
					e.printStackTrace();
				}

				// Making HTTP Request
				try {
					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					String jsonResponse = null;

					jsonResponse = httpClient
							.execute(httpPost, responseHandler);

					onFinish(jsonResponse);

				} catch (ClientProtocolException e) {
					onError();
				} catch (IOException e) {
					onError();

				}

			}
		}).start();

	}
}
