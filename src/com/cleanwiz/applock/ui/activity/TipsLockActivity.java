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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleanwiz.applock.R;
import com.cleanwiz.applock.service.CommLockInfoService;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.utils.LogUtil;

public class TipsLockActivity extends BaseActivity {
	private String packageNameString;
	private AlertDialog tipsAlertDialog = null;
	private final int TIPSDIALOG_TIMEOUT = 400;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TIPSDIALOG_TIMEOUT: {
				if (tipsAlertDialog != null) {
				}
				break;
			}
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Intent intent = getIntent();
		packageNameString = intent.getStringExtra("packagename");
		LogUtil.e("colin", "TipsLockActivity:packagename:" + packageNameString);
		try {
			showTipsLockDialog();
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onCreate(savedInstanceState);
	}

	public String getApplicationName(String packageName) {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		String applicationName = "";
		try {
			packageManager = getApplicationContext().getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(packageName,
					PackageManager.GET_META_DATA
							| PackageManager.GET_ACTIVITIES
							| PackageManager.GET_GIDS
							| PackageManager.GET_CONFIGURATIONS
							| PackageManager.GET_INSTRUMENTATION
							| PackageManager.GET_PERMISSIONS
							| PackageManager.GET_PROVIDERS
							| PackageManager.GET_RECEIVERS
							| PackageManager.GET_SERVICES
							| PackageManager.GET_SIGNATURES
							| PackageManager.GET_UNINSTALLED_PACKAGES);
			applicationName = (String) packageManager
					.getApplicationLabel(applicationInfo);
		} catch (PackageManager.NameNotFoundException e) {
			LogUtil.e("colin", "NameNotFoundException:" + e.toString());
			applicationInfo = null;
		}

		return applicationName;
	}

	private void showTipsLockDialog() throws NameNotFoundException {
		final CommLockInfoService commLockInfoService = new CommLockInfoService(
				getApplicationContext());
		commLockInfoService.getCommLockInfoDaoInstance();
		final AlertDialog tipsDialogDlg = new AlertDialog.Builder(this)
				.create();
		this.tipsAlertDialog = tipsDialogDlg;
		tipsDialogDlg.show();
		Window win = tipsDialogDlg.getWindow();
		win.setContentView(R.layout.dialog_update);
		TextView titleTextView = (TextView) win.findViewById(R.id.update_title);
		TextView tvMsg = (TextView) win.findViewById(R.id.tvMsg);
		String text = getString(R.string.new_app_text);
		text = text.replaceAll("360", getApplicationName(packageNameString));
		tvMsg.setText(text);
		titleTextView.setText(R.string.new_app_title);
		Button btOk = (Button) win.findViewById(R.id.btOk);
		btOk.setText(R.string.new_app_btn);
		ImageView closeImageView = (ImageView) win
				.findViewById(R.id.updateclose);
		closeImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tipsAlertDialog = null;
				tipsDialogDlg.dismiss();
			}
		});

		btOk.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				commLockInfoService.lockCommApplication(packageNameString);
				tipsAlertDialog = null;
				mHandler.removeMessages(TIPSDIALOG_TIMEOUT);
				tipsDialogDlg.dismiss();
			}
		});

		tipsDialogDlg.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				tipsAlertDialog = null;
				mHandler.removeMessages(TIPSDIALOG_TIMEOUT);
				getApplicationContext().sendBroadcast(new Intent("finish"));
			}
		});
		Message message = new Message();
		message.what = TIPSDIALOG_TIMEOUT;
		mHandler.sendMessageDelayed(message, 15000);
	}
}
