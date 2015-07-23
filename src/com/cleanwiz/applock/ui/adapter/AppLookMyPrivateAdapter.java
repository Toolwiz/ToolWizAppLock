/*******************************************************************************
 * Copyright (c) 2015 btows.com.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.cleanwiz.applock.ui.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cleanwiz.applock.R;
import com.cleanwiz.applock.data.LookMyPrivate;
import com.cleanwiz.applock.utils.LogUtil;
import com.gc.materialdesign.views.LayoutRipple;

import java.text.SimpleDateFormat;
import java.util.List;

public class AppLookMyPrivateAdapter extends BaseAdapter {

	public List<LookMyPrivate> looMyPrivates;
	private Context context;
	private LayoutInflater mInflater;
	private PackageManager packageManager;
	private SimpleDateFormat dFormat = new SimpleDateFormat(
			"yyyy.MM.dd HH:mm:ss");

	public AppLookMyPrivateAdapter(List<LookMyPrivate> looMyPrivates,
			Context context) {
		this.looMyPrivates = looMyPrivates;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		packageManager = context.getPackageManager();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		LogUtil.e("colin", "count:" + looMyPrivates.size());
		return looMyPrivates.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return looMyPrivates.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_lookmyprivate, null);
			viewHolder.headerImageView = (ImageView) convertView
					.findViewById(R.id.lookmyprivate_headerimage);
			viewHolder.dateTextView = (TextView) convertView
					.findViewById(R.id.lookmyprivate_datestring);
			viewHolder.detailTextView = (TextView) convertView
					.findViewById(R.id.lookmyprivate_detailstring);
			viewHolder.ripple = (RelativeLayout) convertView
					.findViewById(R.id.layout_item);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (viewHolder != null) {
			LookMyPrivate lookMyPrivate = looMyPrivates.get(position);
			if (lookMyPrivate != null) {
				if (TextUtils.isEmpty(lookMyPrivate.getPicPath())) {
					viewHolder.headerImageView
							.setImageResource(R.drawable.default_avatar);
					viewHolder.headerImageView.setOnClickListener(null);
				} else {
					final Bitmap bm = BitmapFactory.decodeFile(lookMyPrivate
							.getPicPath());
					viewHolder.headerImageView.setImageBitmap(bm);
					viewHolder.ripple
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									ImageView imageView = new ImageView(context);
									imageView.setImageBitmap(bm);
									final PopupWindow popupWindow = new PopupWindow(
											imageView,
											LayoutParams.WRAP_CONTENT,
											LayoutParams.WRAP_CONTENT, true);
									popupWindow.setOutsideTouchable(true);
									imageView
											.setOnClickListener(new View.OnClickListener() {
												@Override
												public void onClick(View v) {
													popupWindow.dismiss();
												}
											});
									popupWindow.showAtLocation(parent,
											Gravity.CENTER, 0, 0);
								}
							});
				}
				viewHolder.dateTextView.setText(dFormat.format(lookMyPrivate
						.getLookDate()));
				viewHolder.detailTextView.setText(context
						.getString(R.string.try_to_open)
						+ " "
						+ getLabelBypackageName(lookMyPrivate.getResolver()));
			}
		}
		return convertView;
	}

	class ViewHolder {
		ImageView headerImageView;
		TextView dateTextView;
		TextView detailTextView;
		RelativeLayout ripple;
	}

	public String getLabelBypackageName(String packageName) {

		Log.d("demo3", "packageName:" + packageName);
		if (packageManager != null) {
			try {
				ApplicationInfo info = packageManager.getApplicationInfo(
						packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
				return (String) packageManager.getApplicationLabel(info);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}

}
