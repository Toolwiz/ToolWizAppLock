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

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.MyConstants;
import com.cleanwiz.applock.data.UpdateVersionManafer;
import com.cleanwiz.applock.ui.activity.GestureUnlockActivity;
import com.cleanwiz.applock.ui.activity.NumberUnlockActivity;
import com.cleanwiz.applock.ui.activity.UserUnlockActivity;
import com.cleanwiz.applock.utils.AndroidUtil;
import com.cleanwiz.applock.utils.LogUtil;
import com.cleanwiz.applock.utils.SharedPreferenceUtil;

/**
 * 应用锁的锁定服务
 * 
 * @author btows
 * 
 */
public class LockService extends Service {

	public static boolean threadIsTerminate = false;
	public AppLockApplication application = AppLockApplication.getInstance();
	private AppLockBroadcastReceiver packageReceiver = new AppLockBroadcastReceiver();
	private AppLockBroadcastReceiver eventReceiver = new AppLockBroadcastReceiver();
	private RequestQueue requestQueue;
	private JsonObjectRequest jsonObjectRequest;
	private UpdateVersionManagerService updateVersionManagerService;
	final int START_TASK_TO_FRONT = 2;

	LockReceiver receiver = new LockReceiver();

	private static LockService lockService;

	public static LockService getService() {
		return lockService;
	}

	private Runnable checkVersionRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			updateVersionManagerService = new UpdateVersionManagerService(
					getApplicationContext());
			requestQueue = Volley.newRequestQueue(getApplicationContext());
			String url = "http://www.toolwiz.com/android/checkfiles.php";
			Locale locale = getResources().getConfiguration().locale;
			String language = locale.getLanguage();
			Uri.Builder builder = Uri.parse(url).buildUpon();
			builder.appendQueryParameter("uid",
					AndroidUtil.getUdid(getApplicationContext()));
			builder.appendQueryParameter("version", "1.0");
			builder.appendQueryParameter("action", "checkfile");
			builder.appendQueryParameter("app", "lockwiz");
			builder.appendQueryParameter("language", language);

			jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
					builder.toString(), null,
					new Response.Listener<JSONObject>() {

						@SuppressLint("NewApi")
						@Override
						public void onResponse(JSONObject arg0) {
							// TODO Auto-generated method stub
							LogUtil.e("colin", "success");
							if (arg0.has("status")) {
								try {
									String status = arg0.getString("status");
									if (Integer.valueOf(status) == 1) {
										JSONObject msgJsonObject = arg0
												.getJSONObject("msg");
										UpdateVersionManafer updateVersionManafer = new UpdateVersionManafer(
												null,
												msgJsonObject
														.getDouble("version"),
												msgJsonObject.getString("url"),
												msgJsonObject.getString("size"),
												msgJsonObject
														.getString("intro"),
												new Date().getTime(), 0L);
										updateVersionManagerService
												.addNewVersion(updateVersionManafer);

									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									LogUtil.e("colin",
											"JSONException" + e.getMessage());
									e.printStackTrace();
								}
							}
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError arg0) {
							// TODO Auto-generated method stub
							LogUtil.e("colin", "onErrorResponse" + arg0);
						}
					});
			requestQueue.add(jsonObjectRequest);
		}
	};

	private Runnable checkDataRunnable = new Runnable() {

		@Override
		public void run() {

			// TODO Auto-generated method stub
			LogUtil.e("colin", "run");
			ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			CommLockInfoService commLockInfoService = new CommLockInfoService(
					getApplicationContext());
			commLockInfoService.getCommLockInfoDaoInstance();
			String lastLoadPackageName = "";
			String packgeName = "";
			LogUtil.e("demo3", "run1");
			while (!threadIsTerminate) {
				try {

					if (Build.VERSION.SDK_INT >= 21) {

						List<ActivityManager.RunningAppProcessInfo> tasks = activityManager
								.getRunningAppProcesses();

						LogUtil.i("current_app", tasks.get(0).processName);
						if (tasks.get(0).processName
								.equals("com.android.documentsui")) {
							packgeName = "com.android.providers.downloads.ui";
						} else {
							packgeName = tasks.get(0).processName;
						}

					} else {
						List<RunningTaskInfo> runningTasks = activityManager
								.getRunningTasks(1);
						RunningTaskInfo runningTaskInfo = runningTasks.get(0);
						ComponentName topActivity = runningTaskInfo.topActivity;
						if (topActivity != null) {
							packgeName = topActivity.getPackageName();
						}
					}

					if (lockState && !inWhiteList(packgeName)
							&& !packgeName.equals(lastLoadPackageName)) {
						lastLoadPackageName = packgeName;
						if (allowedLeaveAment) {
							// 获取解锁界面的解锁结果是否正确
							if (lastUnlockTimeSeconds != 0
									&& new Date().getTime()
											- lastUnlockTimeSeconds < leaverTime) {
								LogUtil.e("colin", "允许暂停时间还没到，不开启保护");
								continue;
							}

						} else {
							LogUtil.e("demo3", "上次正确解锁时间："
									+ lastUnlockTimeSeconds + "当前时间："
									+ new Date().getTime());
							if (new Date().getTime() - lastUnlockTimeSeconds < 3000) {
								LogUtil.e("colin", "同款应用在3秒内被打开多次，时间还没到，不开启保护");
								continue;
							}
						}

						// 查找各种的锁，若存在则判断逻辑
						if (commLockInfoService.isLockedPackageName(packgeName)) {
							// 开启保护 不需要杀死启动起来的应用
							passwordLock(packgeName);
							continue;
						}
					}
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	private boolean inWhiteList(String packgeName) {
		return packgeName.equals("com.cleanwiz.applock");
	}

	public void registerApplicationReceiver() {
		IntentFilter packageIntentFilter = new IntentFilter();
		IntentFilter eventIntentFilter = new IntentFilter();
		eventIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		eventIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
		eventIntentFilter.addAction(Intent.ACTION_TIME_TICK);
		eventIntentFilter.addAction(Intent.ACTION_USER_PRESENT);
		eventIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		packageIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		packageIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		packageIntentFilter.addDataScheme("package");
		getApplicationContext().registerReceiver(packageReceiver,
				packageIntentFilter);
		getApplicationContext().registerReceiver(eventReceiver,
				eventIntentFilter);
		IntentFilter filter = new IntentFilter();
		filter.addAction(LOCK_SERVICE_LASTTIME);
		filter.addAction(LOCK_SERVICE_LEAVEAMENT);
		filter.addAction(LOCK_SERVICE_LEAVERTIME);
		filter.addAction(LOCK_SERVICE_LOCKSTATE);

		lastUnlockTimeSeconds = 0;
		allowedLeaveAment = application.getAllowedLeaveAment();
		leaverTime = application.getLeaverTime();
		lockState = application.getAppLockState();
		registerReceiver(new ServiceReceiver(), filter);
	}

	public class ServiceReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (LOCK_SERVICE_LASTTIME.equals(intent.getAction())) {
				Log.d("demo3", "LOCK_SERVICE_LASTTIME" + LOCK_SERVICE_LASTTIME);
				lastUnlockTimeSeconds = intent.getLongExtra(
						LOCK_SERVICE_LASTTIME, lastUnlockTimeSeconds);
			} else if (LOCK_SERVICE_LEAVEAMENT.equals(intent.getAction())) {
				Log.d("demo3", "LOCK_SERVICE_LEAVEAMENT"
						+ LOCK_SERVICE_LEAVEAMENT);
				allowedLeaveAment = intent.getBooleanExtra(
						LOCK_SERVICE_LEAVEAMENT, allowedLeaveAment);
			} else if (LOCK_SERVICE_LEAVERTIME.equals(intent.getAction())) {
				Log.d("demo3", "LOCK_SERVICE_LEAVERTIME"
						+ LOCK_SERVICE_LEAVERTIME);
				leaverTime = intent.getLongExtra(LOCK_SERVICE_LEAVERTIME,
						leaverTime);
			} else if (LOCK_SERVICE_LOCKSTATE.equals(intent.getAction())) {
				Log.d("demo3", "LOCK_SERVICE_LOCKSTATE"
						+ LOCK_SERVICE_LOCKSTATE);
				lockState = intent.getBooleanExtra(LOCK_SERVICE_LOCKSTATE,
						lockState);
			}

		}

	}

	public static final String LOCK_SERVICE_LASTTIME = "LOCK_SERVICE_LASTTIME";
	public static final String LOCK_SERVICE_LEAVERTIME = "LOCK_SERVICE_LEAVERTIME";
	public static final String LOCK_SERVICE_LEAVEAMENT = "LOCK_SERVICE_LEAVEAMENT";
	public static final String LOCK_SERVICE_LOCKSTATE = "LOCK_SERVICE_LOCKSTATE";

	private static long lastUnlockTimeSeconds;
	private static long leaverTime;
	private static boolean allowedLeaveAment;
	private static boolean lockState;

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		LogUtil.e("demo3", "onDestroy1");
		getApplicationContext().unregisterReceiver(eventReceiver);
		getApplication().unregisterReceiver(packageReceiver);
		LogUtil.e("demo3", "onDestroy2");
		startService(new Intent(this, LockService.class));
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		LogUtil.e("colin", "onBind");
		return null;
	}

	private Thread vThread;
	private Thread dThread;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		lockService = this;
		LogUtil.e("demo3", "onCreate");
		dThread = new Thread(checkDataRunnable);
		dThread.start();
		registerApplicationReceiver();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		LogUtil.e("demo3", "onStartCommand");
		vThread = new Thread(checkVersionRunnable);
		vThread.start();
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		LogUtil.e("colin", "onStart");
		super.onStart(intent, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		LogUtil.e("demo3", "onUnbind");
		return false;
	}

	public static final String IS_LOCK = "applock_islock";

	private void passwordLock(String pkgName) {
		AppLockApplication.getInstance().clearAllActivity();
		Intent intent;
		if (SharedPreferenceUtil.readIsNumModel()) {
			intent = new Intent(this, NumberUnlockActivity.class);
		} else {
			intent = new Intent(this, GestureUnlockActivity.class);
		}
		intent.putExtra(MyConstants.LOCK_PACKAGE_NAME, pkgName);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private void userLock(String pkgName) {
		AppLockApplication.getInstance().clearAllActivity();
		LogUtil.d("demo3", "user  lock");
		Intent intent = new Intent(AppLockApplication.getInstance(),
				UserUnlockActivity.class);
		intent.putExtra(MyConstants.LOCK_PACKAGE_NAME, pkgName);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

}
