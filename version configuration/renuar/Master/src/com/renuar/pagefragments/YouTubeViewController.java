package com.renuar.pagefragments;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore.Video;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.renuar.R;
import com.renuar.YoutubePlayerActivity;
import com.renuar.json.stores.InitInfoStore;
import com.renuar.utils.ProgressDialogFragment;
import com.renuar.utils.StreamUtils;

public class YouTubeViewController extends FragmentPageBase {

	@Override
	protected int getLayoutID() {
		return R.layout.youtube_layout;
	}

	@Override
	protected void initializeActionBar() {
		String title = getValueOfParamFromName("title");
		if (title != null)
			actionBarContorller.textViewMainTitle.setText(title);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		getYoutubeInfoArray();
		super.onActivityCreated(savedInstanceState);
	}

	private void getYoutubeInfoArray() {
		final ListView list = (ListView) viewOfFragment
				.findViewById(android.R.id.list);

		ArrayList<YoutubeRowInfo> youtubeRowInfoListFromInitInfo = InitInfoStore
				.getInstance().getYoutubeRowInfoList();
		if (youtubeRowInfoListFromInitInfo != null) {
			list.setAdapter(new YouTubeAdapter(getActivity(),
					R.layout.row_in_youtube_controller,
					youtubeRowInfoListFromInitInfo));
			return;
		}

		final ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
		progressDialogFragment.show(getFragmentManager(), "MyProgressDialog");
		progressDialogFragment.setCancelable(false);
		new Thread(new Runnable() {

			@Override
			public void run() {

				final ArrayList<YoutubeRowInfo> youtubeRowInfoList = new ArrayList<YoutubeRowInfo>();
				try {

					String urlOfPlaylist = getValueOfParamFromName("url");
					String playListID = urlOfPlaylist.replace(
							"http://www.youtube.com/playlist?list=", "");

					if (playListID.length() == urlOfPlaylist.length()) {
						playListID = urlOfPlaylist.replace(
								"https://www.youtube.com/playlist?list=", "");
					}

					String urlForGettingVideoInformation = "https://gdata.youtube.com/feeds/api/playlists/"
							+ playListID + "?alt=jsonc&v=2";

					// Get a httpclient to talk to the internet
					HttpClient client = new DefaultHttpClient();
					// Perform a GET request to YouTube for a JSON list of all
					// the
					// videos by
					// a specific user
					HttpUriRequest request = new HttpGet(
							urlForGettingVideoInformation);
					// Get the response that YouTube sends back
					HttpResponse response = client.execute(request);
					// Convert this response into a readable string
					String jsonString = StreamUtils.convertToString(response
							.getEntity().getContent());
					// Create a JSON object that we can use from the String
					JSONObject json = new JSONObject(jsonString);

					// For further information about the syntax of this request
					// and
					// JSON-C
					// see the documentation on YouTube
					// http://code.google.com/apis/youtube/2.0/developers_guide_jsonc.html

					// Get are search result items
					JSONArray jsonArray = json.getJSONObject("data")
							.getJSONArray("items");

					// Create a list to store are videos in
					List<Video> videos = new ArrayList<Video>();
					// Loop round our JSON list of videos creating Video objects
					// to use
					// within our app
					for (int i = 0; i < jsonArray.length(); i++) {

						try {
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							String id = jsonObject.getJSONObject("video")
									.getString("id");
							String thumbnail = jsonObject
									.getJSONObject("video")
									.getJSONObject("thumbnail")
									.getString("hqDefault");

							YoutubeRowInfo yri = new YoutubeRowInfo(id,
									thumbnail);
							youtubeRowInfoList.add(yri);

						} catch (Exception e) {
							// TODO: handle exception
						}

					}
					InitInfoStore.getInstance().setYoutubeRowInfoList(
							youtubeRowInfoList);
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							progressDialogFragment.dismiss();
							list.setAdapter(new YouTubeAdapter(getActivity(),
									R.layout.row_in_youtube_controller,
									youtubeRowInfoList));

						}
					});

				}

				catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();

	}

	public class YoutubeRowInfo {
		public String videoId;
		public String thumbnail;

		public YoutubeRowInfo(String videoId, String thumbnail) {
			this.videoId = videoId;
			this.thumbnail = thumbnail;
		}

	}

	class YouTubeAdapter extends ArrayAdapter<YoutubeRowInfo> {

		int resourceId;
		List<YoutubeRowInfo> youtubeRowInfoList;

		public YouTubeAdapter(Context context, int resourceId,
				List<YoutubeRowInfo> youtubeRowInfoList) {
			super(context, resourceId, youtubeRowInfoList);
			this.youtubeRowInfoList = youtubeRowInfoList;
			this.resourceId = resourceId;

		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			View row = convertView;
			final ViewHolder viewHolder;
			if (row == null) {
				row = inflator.inflate(resourceId, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.image = (ImageView) row
						.findViewById(R.id.image_view_in_youtube_row);

				row.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) row.getTag();
			}

			row.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = null;
					try {
						if (youtubeRowInfoList.get(position).videoId != null) {
							// intent = new Intent(
							// Intent.ACTION_VIEW,
							// Uri.parse("vnd.youtube://"
							// + youtubeRowInfoList.get(position).videoId));
							// getContext().startActivity(intent);

							intent = new Intent(getActivity(),
									YoutubePlayerActivity.class);

							intent.putExtra(
									YoutubePlayerActivity.YOUTUBE_VIDEO_URL,
									youtubeRowInfoList.get(position).videoId);
							getContext().startActivity(intent);

						}

					} catch (Exception e) {

					}

				}
			});

			imageLoader.displayImage(
					youtubeRowInfoList.get(position).thumbnail,
					viewHolder.image, options, new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String imageUri, View view) {

						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {

						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {

						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {

						}
					});

			return row;
		}

		class ViewHolder {
			public ImageView image;
		}

	}

}
