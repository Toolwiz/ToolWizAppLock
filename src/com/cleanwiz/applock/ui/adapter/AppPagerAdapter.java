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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cleanwiz.applock.R;
import com.cleanwiz.applock.data.CommLockInfo;
import com.cleanwiz.applock.utils.LogUtil;

public class AppPagerAdapter extends PagerAdapter {

	public static final int APP_GRID_COLUMN = 4;

	private int line_num = 5;
	private int app_num;

	private Context mContext;
	private LayoutInflater mInflater;
	private PackageManager pkgMgr;

	private List<View> mListViews;
	private List<CommLockInfo> lockInfos;
	private List<List<CommLockInfo>> data;

	private int itemHeight;
	private int itemWidth;
	private AppLocker appLocker;

	public AppPagerAdapter(Context context, List<CommLockInfo> lockInfos,
			AppLocker appLocker, int itemHeight, int itemWidth, int line_num) {
		super();
		this.mContext = context;
		this.lockInfos = lockInfos;
		this.appLocker = appLocker;
		this.itemHeight = itemHeight;
		this.itemWidth = itemWidth;
		this.line_num = line_num;
		app_num = line_num * APP_GRID_COLUMN;
		mInflater = LayoutInflater.from(mContext);
		pkgMgr = mContext.getPackageManager();
		initData();
		initView();
	}

	private void initData() {
		data = new ArrayList<List<CommLockInfo>>();
		int count = 0;
		List<CommLockInfo> pagerData = new ArrayList<CommLockInfo>();
		for (CommLockInfo lInfo : lockInfos) {

			if (count++ % app_num == 0) {
				pagerData = new ArrayList<CommLockInfo>();
				data.add(pagerData);
				LogUtil.d("demo3", "pager:" + data.size() + "|count:"
						+ pagerData.size());
			}
			pagerData.add(lInfo);

		}

	}

	private void initView() {

		mListViews = new ArrayList<View>();
		for (List<CommLockInfo> pData : data) {
			View pagerView = buildPagerView(pData);
			mListViews.add(pagerView);
		}

	}

	private View buildPagerView(final List<CommLockInfo> pData) {

		View pagerView = mInflater.inflate(R.layout.pager_applock_old, null);
		LinearLayout layout_lines = (LinearLayout) pagerView
				.findViewById(R.id.layout_lines);
		List<LinearLayout> lines = new ArrayList<LinearLayout>();

		for (int i = 0; i < line_num; i++) {
			LinearLayout line = new LinearLayout(mContext);
			line.setOrientation(LinearLayout.HORIZONTAL);
			lines.add(line);
			layout_lines.addView(line);

		}
		int num = 0;
		for (CommLockInfo lockInfo : pData) {
			View appView = buildAppView(lockInfo);
			lines.get(num++ / APP_GRID_COLUMN).addView(appView);
		}

		return pagerView;

	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(mListViews.get(arg1));
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public int getCount() {
		return mListViews.size();
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(mListViews.get(arg1), 0);
		return mListViews.get(arg1);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == (arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

	private View buildAppView(final CommLockInfo lockInfo) {

		View convertView = mInflater.inflate(R.layout.old_item_applock, null);
		ImageView ivLogo = (ImageView) convertView
				.findViewById(R.id.iv_app_logo);
		final ImageView ivTag = (ImageView) convertView
				.findViewById(R.id.iv_tag);
		View itemView = convertView.findViewById(R.id.rl_item);
		TextView tvName = (TextView) convertView.findViewById(R.id.tv_app_name);

		if (itemHeight > 0 && itemWidth > 0) {
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) itemView
					.getLayoutParams();
			lp.height = itemHeight;
			lp.width = itemWidth;
		}

		ApplicationInfo appInfo = null;
		try {
			appInfo = pkgMgr.getApplicationInfo(lockInfo.getPackageName(),
					PackageManager.GET_UNINSTALLED_PACKAGES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		if (appInfo != null) {
			ivLogo.setImageDrawable(pkgMgr.getApplicationIcon(appInfo));
			tvName.setText(pkgMgr.getApplicationLabel(appInfo));
		}
		ivLogo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (lockInfo.getIsLocked()) {
					appLocker.unlockApp(lockInfo.getPackageName());
					lockInfo.setIsLocked(false);
				} else {
					appLocker.lockApp(lockInfo.getPackageName());
					lockInfo.setIsLocked(true);
				}
				LogUtil.d("demo3", "lock:" + lockInfo.getIsLocked());
				if (lockInfo.getIsLocked()) {
					ivTag.setVisibility(View.VISIBLE);
				} else {
					ivTag.setVisibility(View.INVISIBLE);
				}
			}
		});

		if (lockInfo.getIsLocked()) {
			ivTag.setVisibility(View.VISIBLE);
		} else {
			ivTag.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	public interface AppLocker {

		public void lockApp(String pkgName);

		public void unlockApp(String pkgName);

	}

}
