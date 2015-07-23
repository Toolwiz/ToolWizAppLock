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
package com.cleanwiz.applock.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.MyConstants;
import com.cleanwiz.applock.ui.activity.GestureUnlockActivity;
import com.cleanwiz.applock.ui.activity.NumberUnlockActivity;
import com.cleanwiz.applock.ui.activity.UserUnlockActivity;
import com.cleanwiz.applock.utils.LogUtil;
import com.cleanwiz.applock.utils.SharedPreferenceUtil;

import java.util.Date;

public class LockReceiver extends BroadcastReceiver {

	public static final String CHECK_ACTION = "com.cleanwiz.applock.CHECK_LOCK";
	public static final String EX_PKG_NAME = "EX_PKG_NAME";
	private AppLockApplication application;
	private CommLockInfoService commLockInfoService;
	private String packgeName;

	public LockReceiver() {
		super();
		application = AppLockApplication.getInstance();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (application.getUnlockState() || !application.getAppLockState()) {
			return;
		}

		packgeName = intent.getStringExtra(EX_PKG_NAME);
		if (TextUtils.isEmpty(packgeName)) {
			return;
		}
		commLockInfoService = application.getLock();
		if (commLockInfoService.isLockedPackageName(packgeName)) {
			passwordLock(context, packgeName);
			return;
		}
	}

	public static final String IS_LOCK = "applock_islock";

	private void passwordLock(Context context, String pkgName) {
		if (SharedPreferenceUtil.getTag()) {
			SharedPreferenceUtil.setTag(false);
			return;
		}
		AppLockApplication.getInstance().clearAllActivity();
		Intent intent;
		if (SharedPreferenceUtil.readIsNumModel()) {
			intent = new Intent(context, NumberUnlockActivity.class);
		} else {
			intent = new Intent(context, GestureUnlockActivity.class);
		}
		intent.putExtra(MyConstants.LOCK_PACKAGE_NAME, pkgName);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);

	}

	private void userLock(Context context, String pkgName) {
		AppLockApplication.getInstance().clearAllActivity();
		LogUtil.d("demo3", "user  lock");
		Intent intent = new Intent(AppLockApplication.getInstance(),
				UserUnlockActivity.class);
		intent.putExtra(MyConstants.LOCK_PACKAGE_NAME, pkgName);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

}
