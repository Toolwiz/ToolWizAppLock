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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cleanwiz.applock.utils.AndroidUtil;
import com.cleanwiz.applock.utils.LogUtil;

public class AppUpdateService {

	private Context context = null;
	private RequestQueue requestQueue = null;
	private JsonObjectRequest jsonObjectRequest = null;

	public AppUpdateService(Context context) {
		this.context = context;
	}

	public String getApplicationVersion() {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packageInfo;
		try {
			packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public void checkVersion() {
		requestQueue = Volley.newRequestQueue(context);
		String url = "http://www.toolwiz.com/android/checkfiles.php";
		final String oldVersionString = getApplicationVersion();
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("uid", AndroidUtil.getUdid(context));
		builder.appendQueryParameter("version", oldVersionString);
		builder.appendQueryParameter("action", "checkfile");
		builder.appendQueryParameter("app", "locklocker");

		jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
				builder.toString(), null, new Response.Listener<JSONObject>() {

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
									if (Double.valueOf(oldVersionString) < version) {
										// 发现新版本，提示用户更新
										String intro = msgJsonObject
												.getString("intro");
										AlertDialog.Builder alert = new AlertDialog.Builder(
												context);
										alert.setTitle("软件升级")
												.setMessage(intro)
												.setPositiveButton(
														"更新",
														new DialogInterface.OnClickListener() {
															public void onClick(
																	DialogInterface dialog,
																	int which) {
																// 开启下载服务
															}
														})
												.setNegativeButton(
														"取消",
														new DialogInterface.OnClickListener() {
															public void onClick(
																	DialogInterface dialog,
																	int which) {
																dialog.dismiss();
															}
														});
										alert.create().show();
									}
								} else {
									LogUtil.e("colin",
											"check update status is error");
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								LogUtil.e("colin", "JSONException" + e.getMessage());
								e.printStackTrace();
							}
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						// TODO Auto-generated method stub

					}
				});
		requestQueue.add(jsonObjectRequest);
	}

}
