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
package com.cleanwiz.applock.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.data.CommLockInfo;
import com.cleanwiz.applock.data.TimeManagerInfo;
import com.cleanwiz.applock.data.WIFILockInfo;
import com.cleanwiz.applock.data.WIFILockManager;
import com.cleanwiz.applock.service.CommLockInfoService;
import com.cleanwiz.applock.service.TimeLockInfoService;
import com.cleanwiz.applock.service.TimeManagerInfoService;
import com.cleanwiz.applock.service.VisitorModelService;
import com.cleanwiz.applock.service.WifiLockService;
import com.cleanwiz.applock.service.WifiManagerService;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.ui.adapter.AppPagerAdapter;
import com.cleanwiz.applock.utils.LogUtil;
import com.cleanwiz.applock.utils.ScreenUtil;
import com.cleanwiz.applock.utils.SharedPreferenceUtil;

public class AppLockActivity extends BaseActivity {

	public static final int FLAG_COMM_LOCK = 0;
	public static final int FLAG_TIME_LOCK = 1;
	public static final int FLAG_WIFI_LOCK = 2;
	public static final int FLAG_USER_LOCK = 3;
	public static final String APP_LIST_FLAG = "app_list_flag";
	public static final String EXT_TIME_ID = "ext_time_id";
	public static final String EXT_WIFI_ID = "ext_wifi_id";
	public static final String EXT_USER_ID = "ext_user_id";

	private AppLockActivity mContext;

	private List<CommLockInfo> lockInfos;

	private ViewPager appPager;
	private LinearLayout pLayout;
	private AppPagerAdapter pagerAdapter;
	private List<ImageView> points;

	private TextView tv_title;
	private AppPagerAdapter.AppLocker appLocker;

	private int itemHeight;
	private int itemWidth;

	// flags
	private int lockFlag;
	private boolean createFlag = true;

	private AppHandler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);
		mContext = this;
		handler = new AppHandler();
		appPager = (ViewPager) findViewById(R.id.vp_applock);
		pLayout = (LinearLayout) findViewById(R.id.ll_points);
		tv_title = (TextView) findViewById(R.id.tv_title);

		lockFlag = getIntent().getIntExtra(APP_LIST_FLAG, FLAG_COMM_LOCK);
		initData();
		startFirstServices();
		if (AppLockApplication.getInstance().isNeedSetSecret()) {
			startActivity(new Intent(this, SecretConfig.class));
			AppLockApplication.getInstance().setStartGuide(false);
		}
	}

	private static final int LINES = 5;

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (createFlag && hasFocus) {
			createFlag = false;
			itemHeight = appPager.getHeight() / LINES;
			itemWidth = appPager.getWidth() / AppPagerAdapter.APP_GRID_COLUMN;
			new AppThread().start();

		}
		super.onWindowFocusChanged(hasFocus);
	}

	private static final int HANDLER_ADAPTER_DONE = 0;

	@SuppressLint("HandlerLeak")
	class AppHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_ADAPTER_DONE:
				initPoints(pagerAdapter.getCount());
				appPager.setOnPageChangeListener(new PageListener());
				appPager.setAdapter(pagerAdapter);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}

	}

	class AppThread extends Thread {

		@Override
		public void run() {
			pagerAdapter = new AppPagerAdapter(mContext, lockInfos, appLocker,
					itemHeight, itemWidth, LINES);
			handler.sendEmptyMessage(HANDLER_ADAPTER_DONE);
			super.run();
		}

	}

	private void initData() {

		switch (lockFlag) {
		case FLAG_COMM_LOCK:
			initForComm();
			break;
		case FLAG_TIME_LOCK:
			initForTime();
			break;
		case FLAG_WIFI_LOCK:
			initForWifi();
			break;
		case FLAG_USER_LOCK:
			initForUser();
			break;

		default:
			break;
		}
		checkLockInfos();
	}

	public void startFirstServices() {
		if (!SharedPreferenceUtil.readEnterFlag()) {
			LogUtil.e("colin", "testService_start");
			startService(new Intent("com.cleanwiz.applock.service.LockService")
					.setPackage("com.cleanwiz.applock"));
			SharedPreferenceUtil.editEnterFlag(true);
		}
	}

	private void checkLockInfos() {
		PackageManager pkgMgr = getPackageManager();
		for (int i = lockInfos.size() - 1; i >= 0; i--) {
			CommLockInfo lock = lockInfos.get(i);
			ApplicationInfo appInfo = null;
			try {
				appInfo = pkgMgr.getApplicationInfo(lock.getPackageName(),
						PackageManager.GET_UNINSTALLED_PACKAGES);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				lockInfos.remove(lock);
				continue;
			}
			if (appInfo == null || pkgMgr.getApplicationIcon(appInfo) == null) {
				lockInfos.remove(lock);
				continue;
			}
		}

	}

	private void initForComm() {
		final CommLockInfoService lockService = new CommLockInfoService(
				mContext);
		lockService.getCommLockInfoDaoInstance();
		lockInfos = lockService.getAllCommLockInfos();
		appLocker = new AppPagerAdapter.AppLocker() {
			@Override
			public void unlockApp(String pkgName) {
				lockService.unlockCommApplication(pkgName);
			}

			@Override
			public void lockApp(String pkgName) {
				lockService.lockCommApplication(pkgName);
			}
		};

	}

	private void initForTime() {

		tv_title.setText(R.string.choose_app);

		TimeManagerInfoService mgr = new TimeManagerInfoService(mContext);
		long timeId = getIntent().getLongExtra(EXT_TIME_ID, 0);
		final TimeManagerInfo tmInfo = mgr.getTimeManagerInfoByTimeID(timeId);
		if (tmInfo == null) {
			finish();
		}
		lockInfos = mgr.getAllTimeLockInfoByTimeManager(tmInfo);

		final TimeLockInfoService lockService = new TimeLockInfoService(
				mContext);
		appLocker = new AppPagerAdapter.AppLocker() {
			@Override
			public void unlockApp(String pkgName) {
				lockService.deleteLockAppByTimeManager(pkgName, tmInfo);
			}

			@Override
			public void lockApp(String pkgName) {
				lockService.lockAppByTimeManager(pkgName, tmInfo);
			}
		};

	}

	private void initForWifi() {
		tv_title.setText(R.string.choose_app);

		WifiManagerService mgr = new WifiManagerService(mContext);

		long lockId = getIntent().getLongExtra(EXT_WIFI_ID, 0);
		final WIFILockManager lockInfo = mgr.getWifiLockManagerByID(lockId);
		if (lockInfo == null) {
			finish();
		}
		lockInfos = mgr.getAllWifiLockInfo(lockId);

		final WifiLockService lockService = new WifiLockService(mContext);
		appLocker = new AppPagerAdapter.AppLocker() {
			@Override
			public void unlockApp(String pkgName) {
				WIFILockInfo wLock = new WIFILockInfo(null, ""
						+ lockInfo.getId(), pkgName);
				lockService.unLockWifiLockInfo(wLock);
			}

			@Override
			public void lockApp(String pkgName) {
				WIFILockInfo wLock = new WIFILockInfo(null, ""
						+ lockInfo.getId(), pkgName);
				lockService.lockWifiLockInfo(wLock);
			}
		};

	}

	private void initForUser() {
		tv_title.setText(R.string.choose_app);

		final VisitorModelService mgr = new VisitorModelService(mContext);

		lockInfos = mgr.getAllVisitor();

		appLocker = new AppPagerAdapter.AppLocker() {
			@Override
			public void unlockApp(String pkgName) {
				mgr.deleteAppFromVisitor(pkgName);
			}

			@Override
			public void lockApp(String pkgName) {
				mgr.insertAppToVisitoer(pkgName);
			}
		};

	}

	@Override
	public void onClickEvent(View view) {
		switch (view.getId()) {
		case R.id.btn_back:
			finish();
			break;

		case R.id.btn_menu:
			if (lockFlag == FLAG_USER_LOCK) {
				AppLockApplication.getInstance().setVisitorState(true);
				finish();
			}
			break;

		case R.id.btn_setting:
			enterSeting();
			break;

		default:
			break;
		}
		super.onClickEvent(view);
	}

	private void enterSeting() {
		startActivity(new Intent(this, SettingActivity.class));
	}

	public static final int POINT_MAX_DP = 24;

	private void initPoints(int pagers) {
		pLayout.removeAllViews();
		points = new ArrayList<ImageView>();
		int maxW = ScreenUtil.dip2px(mContext, 24);
		int width = Math.min(maxW, pLayout.getWidth() / pagers);
		int height = ScreenUtil.dip2px(mContext, 16);
		for (int i = 0; i < pagers; i++) {
			ImageView iv = new ImageView(mContext);
			iv.setScaleType(ScaleType.CENTER_INSIDE);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,
					height);
			if (i == 0) {
				iv.setImageResource(R.drawable.pager_point_white);
			} else {
				iv.setImageResource(R.drawable.pager_point_green);
			}
			pLayout.addView(iv, lp);
			points.add(iv);
		}

	}

	class PageListener implements ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			for (ImageView iv : points) {
				iv.setImageResource(R.drawable.pager_point_green);
			}
			try {
				points.get(arg0).setImageResource(R.drawable.pager_point_white);
			} catch (Exception e) {
			}

		}

	}

}
