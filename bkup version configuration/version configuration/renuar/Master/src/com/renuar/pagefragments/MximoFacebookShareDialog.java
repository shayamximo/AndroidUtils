package com.renuar.pagefragments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.Facebook;
import com.renuar.GlobalConstants;
import com.renuar.R;
import com.renuar.utils.SharedPreferencesController;

public class MximoFacebookShareDialog extends MximoDialog {
	Button mButton;

	onSubmitListener mListener;
	public String text = "";
	String picUrl;
	String msgToPost;
	private byte[] imageInBytes = null;
	public static Facebook facebook;

	interface onSubmitListener {
		void setOnSubmisharefb_edt_texttListener(String arg);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();
		picUrl = args.getString(GlobalConstants.TO_FACEBOOK_SHARE_PIC_URL);
		msgToPost = args.getString(GlobalConstants.TO_FACEBOOK_SHARE_MSG);
		facebook = new Facebook(GlobalConstants.FB_APP_ID);
		SharedPreferencesController.restoreFacebookCredentials(facebook);

		return super.onCreateDialog(savedInstanceState);
	}

	private void postToWall() {
		try {
			Bundle msg = new Bundle();

			if (imageInBytes != null) {

				msg.putString("message", msgToPost);
				msg.putByteArray("picture", imageInBytes);
				facebook.request("photos", msg, "POST");
			} else {
				msg.putString("message", msgToPost);

				msg.putString("picture", picUrl);

				facebook.request("feed", msg, "POST");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class ShareFB extends AsyncTask<URL, Integer, Long> {
		private ProgressDialog mProgressDialog;

		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(getActivity(), "",
					"Sharing On Facebook...", true);

			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}

		protected Long doInBackground(URL... urls) {
			long result = 1;

			try {
				File root = new File(Environment.getExternalStorageDirectory()
						+ File.separator + "temp" + File.separator);
				if (!root.exists() || !root.isDirectory()) {
					root.mkdirs();
				}

				URL imageUrl = new URL(picUrl);
				HttpURLConnection conn = (HttpURLConnection) imageUrl
						.openConnection();
				conn.setConnectTimeout(30000);
				conn.setReadTimeout(30000);
				conn.setInstanceFollowRedirects(true);
				InputStream input_stream = conn.getInputStream();

				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				byte[] data = new byte[16384]; // 16K
				int bytes_read;
				while ((bytes_read = input_stream.read(data, 0, data.length)) != -1) {
					buffer.write(data, 0, bytes_read);
				}
				input_stream.close();
				imageInBytes = buffer.toByteArray();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			postToWall();

			return result;
		}

		protected void onPostExecute(Long result) {
			mProgressDialog.cancel();
			Toast.makeText(getActivity(), "Shared Successfully.",
					Toast.LENGTH_SHORT).show();

			finishDialogFragment();
		}
	}

	private void finishDialogFragment() {
		getActivity().getFragmentManager().beginTransaction()
				.remove(MximoFacebookShareDialog.this).commit();
	}

	@Override
	protected void doOnProceed() {
		ShareFB shareFB = new ShareFB();
		shareFB.execute();

	}

	@Override
	protected void doOnCancel() {
		finishDialogFragment();

	}

	protected int getColorOfBackground() {
		return R.color.facebook_background;
	}

	@Override
	protected int getStringOfCancelId() {
		return R.string.cancel;
	}

	@Override
	protected int getStringOfProceedId() {
		return R.string.share;
	}

	@Override
	protected String getTextForTitle() {
		return getResources().getString(R.string.facebook);
	}

	@Override
	protected String getTextForText() {
		return msgToPost;
	}
}
