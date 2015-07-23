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

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.data.CommLockInfo;
import com.cleanwiz.applock.data.TimeManagerInfo;
import com.cleanwiz.applock.data.WIFILockManager;
import com.cleanwiz.applock.service.CommLockInfoService;
import com.cleanwiz.applock.service.DeviceMyReceiver;
import com.cleanwiz.applock.service.TimeManagerInfoService;
import com.cleanwiz.applock.service.VisitorModelService;
import com.cleanwiz.applock.service.WifiManagerService;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.utils.LogUtil;
import com.cleanwiz.applock.utils.SharedPreferenceUtil;
import com.cleanwiz.applock.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;

public class GuideActivity extends BaseActivity {

	private TextView guide_num;
	private TextView tv_tips_time;
	private TextView tv_tips_wifi;
	private TextView tv_tips_user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		guide_num = (TextView) findViewById(R.id.guide_num);
		tv_tips_time = (TextView) findViewById(R.id.tv_tips_time);
		tv_tips_wifi = (TextView) findViewById(R.id.tv_tips_wifi);
		tv_tips_user = (TextView) findViewById(R.id.tv_tips_user);
		testServices();
		testBroadcast(this);
	}

	@Override
	protected void onResume() {
		if (AppLockApplication.getInstance().isNeedSetSecret()) {
			startActivity(new Intent(this, SecretConfig.class));
			AppLockApplication.getInstance().setStartGuide(false);
		}
		setTips();
		setNumber();
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}

	private void setTips() {
		TimeManagerInfoService timeMgr = new TimeManagerInfoService(this);
		WifiManagerService wifiMgr = new WifiManagerService(this);
		int tNum = 0;
		int wNum = 0;
		for (TimeManagerInfo tInfo : timeMgr.getAllTimeManagerInfos()) {
			if (tInfo.getTimeIsOn()) {
				tNum++;
			}
		}
		for (WIFILockManager wInfo : wifiMgr.getallWifiLockManaer()) {
			if (wInfo.getIsOn()) {
				wNum++;
			}
		}
		if (tNum > 0) {
			tv_tips_time.setVisibility(View.VISIBLE);
			String timeTips = tNum < 100 ? String.valueOf(tNum) : "99+";
			tv_tips_time.setText(timeTips);
		} else {
			tv_tips_time.setVisibility(View.INVISIBLE);
		}
		if (wNum > 0) {
			tv_tips_wifi.setVisibility(View.VISIBLE);
			String wifiTips = wNum < 100 ? String.valueOf(wNum) : "99+";
			tv_tips_wifi.setText(wifiTips);
		} else {
			tv_tips_wifi.setVisibility(View.INVISIBLE);
		}
		boolean isUser = AppLockApplication.getInstance().getVisitorState();
		String userTips = isUser ? "ON" : "OFF";
		tv_tips_user.setText(userTips);

	}

	private void setNumber() {

		CommLockInfoService lockService = new CommLockInfoService(this);
		lockService.getCommLockInfoDaoInstance();
		List<CommLockInfo> lockList = lockService.getCommLockedInfos();
		int num = lockList == null ? 0 : lockList.size();
		guide_num.setText("" + num);
	}

	@Override
	public void onClickEvent(View view) {
		switch (view.getId()) {
		case R.id.btn_setting:
			enterSeeting();
			break;
		case R.id.btn_app_lock:
			enterAppLock();
			break;
		case R.id.btn_time_lock:
			enterTimeLock();
			break;
		case R.id.btn_wifi_lock:
			enterWifiLock();
			break;
		case R.id.btn_user_lock:
			enterUserLock();
			break;
		case R.id.tv_tips_user:
			updateUserModel();

		default:
			break;
		}
		super.onClickEvent(view);
	}

	private void updateUserModel() {
		if (SharedPreferenceUtil.readFirstUserModel()) {
			Intent intent = new Intent(this, UserHelpActivity.class);
			startActivity(intent);
		} else {
			VisitorModelService visitorModelService = new VisitorModelService(
					getApplicationContext());
			if (!visitorModelService.hasLockedPackage()) {
				ToastUtils.showToast(R.string.lock_done_none);
				return;
			}
			boolean isUser = AppLockApplication.getInstance().getVisitorState();
			String userTips = !isUser ? "ON" : "OFF";
			tv_tips_user.setText(userTips);
			AppLockApplication.getInstance().setVisitorState(!isUser);
		}
	}

	private void enterSeeting() {
		startActivity(new Intent(this, SettingActivity.class));
	}

	private void enterAppLock() {
		startActivity(new Intent(this, AppLockActivity.class));
	}

	private void enterTimeLock() {
		startActivity(new Intent(this, TimeLockMgrActivity.class));
	}

	private void enterUserLock() {
		if (SharedPreferenceUtil.readFirstUserModel()) {
			Intent intent = new Intent(this, UserHelpActivity.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(this, ChooseAppsActivity.class);
			intent.putExtra(ChooseAppsActivity.APP_LIST_FLAG,
					ChooseAppsActivity.FLAG_USER_LOCK);
			intent.putExtra(ChooseAppsActivity.MODEL_NAME,
					getString(R.string.lock_user));
			startActivity(intent);
		}
	}

	private void enterWifiLock() {
		startActivity(new Intent(this, WifiLockMgrActivity.class));
	}

	public void testServices() {
		if (!SharedPreferenceUtil.readEnterFlag()) {
			LogUtil.e("colin", "testService_start");
			startService(new Intent("com.cleanwiz.applock.service.LockService")
					.setPackage("com.cleanwiz.applock"));
			SharedPreferenceUtil.editEnterFlag(true);
		}
	}

	public static void testBroadcast(Context context) {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		intentFilter.addAction(Intent.ACTION_USER_PRESENT);
		intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		context.registerReceiver(new DeviceMyReceiver(), new IntentFilter());
	}

}
