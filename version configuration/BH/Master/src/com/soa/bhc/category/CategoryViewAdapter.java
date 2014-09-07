package com.soa.bhc.category;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.soa.bhc.App;
import com.soa.bhc.R;
import com.soa.bhc.pagefragments.CategoryBrowserViewController.CategoryRowParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * 
 * @author manish.s
 * 
 */
public class CategoryViewAdapter extends ArrayAdapter<CategoryUIItem> {
	Context context;
	int layoutResourceId;
	ArrayList<CategoryUIItem> data = new ArrayList<CategoryUIItem>();
	CategoryRowParams categoryRowParams;
	ImageLoader imageLoader;
	DisplayImageOptions options;

	public CategoryViewAdapter(Context context, int layoutResourceId,
			ArrayList<CategoryUIItem> data,
			CategoryRowParams categoryRowParams, ImageLoader imageLoader,
			DisplayImageOptions options) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
		this.categoryRowParams = categoryRowParams;
		this.imageLoader = imageLoader;
		this.options = options;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RecordHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new RecordHolder();
			holder.txtTitle = (TextView) row.findViewById(R.id.home_txt);
			holder.imageItem = (ImageView) row
					.findViewById(R.id.image_in_category_row_in_category_list);
			if (categoryRowParams.cellColor != null) {
				row.setBackgroundColor(Color
						.parseColor(categoryRowParams.cellColor));
			}

			if (categoryRowParams.textColor != null) {
				holder.txtTitle.setTextColor(Color
						.parseColor(categoryRowParams.textColor));
				holder.txtTitle.setBackgroundColor(App.getApp().getResources()
						.getColor(R.color.transparent));
			}

			if ((categoryRowParams.cellTextAlignment != null)
					&& categoryRowParams.cellTextAlignment.equals("center")) {

				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.txtTitle
						.getLayoutParams();
				layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,
						RelativeLayout.TRUE);

			}

			row.setTag(holder);
		} else {
			holder = (RecordHolder) row.getTag();
		}

		CategoryUIItem item = data.get(position);

		holder.txtTitle.setText(item.getName());

		String url = item.getUrl();
		if (url != null) {

			imageLoader.displayImage(url, holder.imageItem, options,
					new SimpleImageLoadingListener() {
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
					}, new ImageLoadingProgressListener() {
						@Override
						public void onProgressUpdate(String imageUri,
								View view, int current, int total) {

						}
					});

		}

		// paramsLL.height = height;
		// paramsLL.width = width;

		return row;

	}

	static class RecordHolder {
		TextView txtTitle;
		ImageView imageItem;

	}

}