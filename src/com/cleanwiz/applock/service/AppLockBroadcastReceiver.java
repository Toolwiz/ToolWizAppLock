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
import android.net.wifi.WifiManager;

import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.ui.activity.TipsLockActivity;
import com.cleanwiz.applock.utils.LogUtil;
import com.cleanwiz.applock.utils.SharedPreferenceUtil;

public class AppLockBroadcastReceiver extends BroadcastReceiver {

	private AppLockApplication application = AppLockApplication.getInstance();

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
			LogUtil.e("colin", "ACTION_SCREEN_OFF");
		} else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
			LogUtil.e("colin", "ACTION_SCREEN_ON");
		} else if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
			LogUtil.e("colin", "用户解锁");
		} else if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
			LogUtil.e("colin", "新增应用的广播");
			// 新增的时候只维护应用锁，不必再维护其他的锁。因其他的锁应用都从这个表里取数据
			CommLockInfoService commLockInfoService = new CommLockInfoService(
					context);
			commLockInfoService.getCommLockInfoDaoInstance();
			commLockInfoService
					.insertNewCommLockApplicationByPackageName(intent
							.getDataString().replace("package:", ""));
			// 新应用加锁提示
			if (SharedPreferenceUtil.readNewAppTips()) {
				// 若需要直接加锁在普通的应用锁里面
				Intent tipsIntent = new Intent();
				tipsIntent.putExtra("packagename", intent.getDataString()
						.replace("package:", ""));
				tipsIntent.setClass(context, TipsLockActivity.class);
				tipsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(tipsIntent);
			}
		} else if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
			LogUtil.e("colin",
					"应用删除的广播:" + intent.getDataString().replace("package:", ""));
			// 各种锁数据库信息的删除
			CommLockInfoService commLockInfoService = new CommLockInfoService(
					context);
			commLockInfoService.getCommLockInfoDaoInstance();
			commLockInfoService.deleteCommApplicationByPackageName(intent
					.getDataString().replace("package:", ""));
			// 删除时间锁中的信息
			TimeLockInfoService timeLockInfoService = new TimeLockInfoService(
					context);
			timeLockInfoService.deleteLockAppByPackageName(intent
					.getDataString().replace("package:", ""));
		} else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent
				.getAction())) {
			// wifi连接状态处理，此处只告诉LockService，由LockService处理
			if (intent.getExtras().getInt(WifiManager.EXTRA_WIFI_STATE) == WifiManager.WIFI_STATE_ENABLED) {
				application.wifiIsConnected = true;
				LogUtil.e("colin", "wifi state was connected");
			} else if (intent.getExtras().getInt(WifiManager.EXTRA_WIFI_STATE) == WifiManager.WIFI_STATE_DISABLED) {
				application.wifiIsConnected = false;
				LogUtil.e("colin", "wifi state was disconnected");
			}
		} else if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			context.startService(new Intent(
					"com.cleanwiz.applock.service.LockService")
					.setPackage("com.cleanwiz.applock"));
		} else if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			LogUtil.e("colin", "receiver onReceive" + "to start lookservice");
			Intent serIntent = new Intent(context,
					StartLookServiceReceiver.class);
			context.startService(serIntent.setPackage("com.cleanwiz.applock"));
		} else if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
			LogUtil.e("colin", "ACTION_TIME_TICK receiver onReceive"
					+ "to start lookservice");
			Intent serIntent = new Intent(context,
					StartLookServiceReceiver.class);
			context.startService(serIntent.setPackage("com.cleanwiz.applock"));
		}
	}

}
