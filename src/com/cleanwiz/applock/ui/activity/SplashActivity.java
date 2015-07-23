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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.data.UpdateVersionManafer;
import com.cleanwiz.applock.service.UpdateService;
import com.cleanwiz.applock.service.UpdateVersionManagerService;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.utils.AndroidUtil;
import com.cleanwiz.applock.utils.LogUtil;
import com.cleanwiz.applock.utils.SharedPreferenceUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Locale;

//import com.testin.agent.TestinAgent;

public class SplashActivity extends BaseActivity {

	public static final int SPLASH_DONE = 0;
	public static final int CHECKVERSION_CANCEL = 1;
	public static final int CHECKVERSION_DOWN = 2;
	public static final int CHECKVERSION_EOOR = 3;

	private SplashHandler handler;

	private RequestQueue requestQueue = null;
	private JsonObjectRequest jsonObjectRequest = null;
	private String downLoadFileUrl = "";
	private String fileSize = "";
	private static UpdateVersionManagerService updateVersionManagerService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		updateVersionManagerService = new UpdateVersionManagerService(this);
		MobclickAgent.setDebugMode(true);
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);

		NewCheckVersion();

		handler = new SplashHandler();
		Message msg = new Message();
		msg.what = SPLASH_DONE;
		handler.sendMessage(msg);
		if (SharedPreferenceUtil.readEnterFlag()) {
			startService(new Intent("com.cleanwiz.applock.service.LockService")
					.setPackage("com.cleanwiz.applock"));
		}
	}

	private void goToPassword() {
		if (SharedPreferenceUtil.readIsFirst()) {
			startActivity(new Intent(this, StepActivity.class));
			finish();
		} else if (SharedPreferenceUtil.readIsNumModel()) {
			startActivity(new Intent(this, NumberCheckActivity.class));
			finish();
		} else {
			startActivity(new Intent(this, GestureCheckActivity.class));
			finish();
		}
	}

	@SuppressLint("HandlerLeak")
	class SplashHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CHECKVERSION_CANCEL:
			case CHECKVERSION_EOOR:
			case SPLASH_DONE: {
				goToPassword();
				break;
			}
			case CHECKVERSION_DOWN: {
				// 通知栏更新，下载文件
				Intent updateIntent = new Intent(SplashActivity.this,
						UpdateService.class);
				updateIntent.putExtra("appUrl", downLoadFileUrl);
				LogUtil.e("colin", "downLoadFileUrl:" + downLoadFileUrl);
				startService(updateIntent);
				goToPassword();
				break;
			}
			default:
				break;
			}
			super.handleMessage(msg);
		}

	}

	public String getApplicationVersion() {
		PackageManager packageManager = getPackageManager();
		PackageInfo packageInfo;
		try {
			packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public void checkVersion() {
		requestQueue = Volley.newRequestQueue(this);
		String url = "http://www.toolwiz.com/android/checkfiles.php";
		final String oldVersionString = getApplicationVersion();
		Locale locale = getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("uid", AndroidUtil.getUdid(this));
		builder.appendQueryParameter("version", oldVersionString);
		builder.appendQueryParameter("action", "checkfile");
		builder.appendQueryParameter("app", "locklocker");
		builder.appendQueryParameter("language", language);

		jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
				builder.toString(), null, new Response.Listener<JSONObject>() {

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
									double version = msgJsonObject
											.getDouble("version");
									downLoadFileUrl = msgJsonObject
											.getString("url");
									fileSize = msgJsonObject.getString("size");
									if (Double.valueOf(oldVersionString) < version
											&& !downLoadFileUrl.isEmpty()) {
										// 发现新版本，提示用户更新
										String intro = msgJsonObject
												.getString("intro");
										LogUtil.e("colin", "........" + intro);
										showUpdateDialog(intro);
									} else {
										LogUtil.e("colin",
												"check update status is same not to update");
										SplashHandler handler = new SplashHandler();
										Message msg = new Message();
										msg.what = CHECKVERSION_EOOR;
										handler.sendMessage(msg);
									}
								} else {
									LogUtil.e("colin",
											"check update status is error");
									SplashHandler handler = new SplashHandler();
									Message msg = new Message();
									msg.what = CHECKVERSION_EOOR;
									handler.sendMessage(msg);
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								SplashHandler handler = new SplashHandler();
								Message msg = new Message();
								msg.what = CHECKVERSION_EOOR;
								handler.sendMessage(msg);
								e.printStackTrace();
							}
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						// TODO Auto-generated method stub
						SplashHandler handler = new SplashHandler();
						Message msg = new Message();
						msg.what = CHECKVERSION_EOOR;
						handler.sendMessage(msg);
					}
				});
		requestQueue.add(jsonObjectRequest);
	}

	public void NewCheckVersion() {
		final String oldVersionString = getApplicationVersion();
		List<UpdateVersionManafer> versionManafers = updateVersionManagerService
				.getVersionManafers();
		boolean hasVersion = false;
		for (UpdateVersionManafer updateVersionManafer : versionManafers) {
			hasVersion = true;
			if (updateVersionManafer.getVersioncode() > Double
					.parseDouble(oldVersionString)) {
				if (SharedPreferenceUtil.readUpdateTipTime() == 0l) {
					downLoadFileUrl = updateVersionManafer.getUpdateurl();
					showUpdateDialog(updateVersionManafer.getIntro());
					updateVersionManafer.setLasttipdate(new Date().getTime());
				} else if (System.currentTimeMillis()
						- SharedPreferenceUtil.readUpdateTipTime() >= 1000 * 60 * 60 * 20) {
					downLoadFileUrl = updateVersionManafer.getUpdateurl();
					showUpdateDialog(updateVersionManafer.getIntro());
					updateVersionManafer.setLasttipdate(new Date().getTime());
				} else {
					SplashHandler handler = new SplashHandler();
					Message msg = new Message();
					msg.what = CHECKVERSION_EOOR;
					handler.sendMessageDelayed(msg, 1000);
				}
			} else {
				SplashHandler handler = new SplashHandler();
				Message msg = new Message();
				msg.what = CHECKVERSION_EOOR;
				handler.sendMessageDelayed(msg, 1000);
			}
			break;
		}
		if (!hasVersion) {
			SplashHandler handler = new SplashHandler();
			Message msg = new Message();
			msg.what = CHECKVERSION_EOOR;
			handler.sendMessageDelayed(msg, 1000);
		}
	}

	public void showUpdateDialog(String intro) {
		final AlertDialog updateDialogDlg = new AlertDialog.Builder(this)
				.create();
		updateDialogDlg.show();
		Window win = updateDialogDlg.getWindow();
		win.setContentView(R.layout.dialog_update);
		TextView tvMsg = (TextView) win.findViewById(R.id.tvMsg);
		tvMsg.setText(intro);
		Button btOk = (Button) win.findViewById(R.id.btOk);
		ImageView closeImageView = (ImageView) win
				.findViewById(R.id.updateclose);
		closeImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateDialogDlg.dismiss();
				SplashHandler handler = new SplashHandler();
				Message msg = new Message();
				msg.what = CHECKVERSION_CANCEL;
				handler.sendMessage(msg);
			}
		});

		btOk.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				SplashHandler handler = new SplashHandler();
				Message msg = new Message();
				msg.what = CHECKVERSION_DOWN;
				handler.sendMessage(msg);
			}
		});

		updateDialogDlg.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				List<UpdateVersionManafer> updateVersionManafers = updateVersionManagerService
						.getVersionManafers();
				for (UpdateVersionManafer updateVersionManafer : updateVersionManafers) {
					updateVersionManafer.setLasttipdate(new Date().getTime());
					updateVersionManagerService
							.modifyTipsDate(updateVersionManafer);
					break;
				}
				SplashHandler handler = new SplashHandler();
				Message msg = new Message();
				msg.what = CHECKVERSION_CANCEL;
				handler.sendMessage(msg);
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		MobclickAgent.onPause(this);
		super.onPause();
	}

	public void createDeskShortCut() {
		// 在配置文件中声明已经创建了快捷方式
		SharedPreferenceUtil.editShortCut(true);
		Intent shortcutIntent = new Intent();
		shortcutIntent.setClass(this, SplashActivity.class);
		shortcutIntent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		shortcutIntent.setAction("android.intent.action.MAIN");
		shortcutIntent.addCategory("android.intent.category.LAUNCHER");

		Intent resultIntent = new Intent();
		resultIntent.putExtra("duplicate", false);
		resultIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(this,
						R.drawable.ic_launcher));
		resultIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.app_name));
		resultIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);

		resultIntent
				.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
		sendBroadcast(resultIntent);
		resultIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		sendBroadcast(resultIntent);
	}

}
