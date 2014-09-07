package com.parqueteam.comm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public abstract class PutRequest extends Request {

	public void sendRequest(final String url,
			final List<NameValuePair> nameValuePairsList) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// Creating HTTP client
				HttpClient httpClient = new DefaultHttpClient();
				// Creating HTTP Post
				HttpPut httpPost = new HttpPut(url);

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
					HttpResponse httpResponse = httpClient.execute(httpPost);
					HttpEntity responseEntity = httpResponse.getEntity();
					if (responseEntity != null) {
						String response = EntityUtils.toString(responseEntity);
						onFinish(response);
					}

				} catch (ClientProtocolException e) {
					onError();
				} catch (IOException e) {
					onError();

				}

			}
		}).start();

	}
}
