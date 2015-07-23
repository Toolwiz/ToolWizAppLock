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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.model.AppSettingItemInfo;
import com.cleanwiz.applock.service.DeviceMyReceiver;
import com.cleanwiz.applock.service.LockService;
import com.cleanwiz.applock.service.UpdateService;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.ui.adapter.AppSettingAdapter;
import com.cleanwiz.applock.ui.adapter.AppSettingAdapter.ViewHoder;
import com.cleanwiz.applock.ui.widget.SwitchButton;
import com.cleanwiz.applock.utils.FileService;
import com.cleanwiz.applock.utils.LogUtil;
import com.cleanwiz.applock.utils.SharedPreferenceUtil;

public class AppSettingActivity extends BaseActivity {

	private Context context = null;
	private ListView appSettingListView = null;
	private AppSettingAdapter appSettingAdapter = null;
	private AppLockApplication appLockApplication = AppLockApplication
			.getInstance();
	private Vector<AppSettingItemInfo> allItemInfos = new Vector<AppSettingItemInfo>();
	private List<String> leaveTimeStrings = new ArrayList<String>();
	private Vector<AppSettingItemInfo> dataAppSettingItemInfos = new Vector<AppSettingItemInfo>();

	private final int SETLEAVEDIALOG_DISS = 100;
	private final int UPDATEADAPTER = 101;

	private String downLoadFileUrl = "";
	private DevicePolicyManager devicePolicyManager;
	private ComponentName componentName;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SETLEAVEDIALOG_DISS: {
				appSettingAdapter.notifyDataSetChanged();
				break;
			}
			case SplashActivity.CHECKVERSION_CANCEL:
			case SplashActivity.CHECKVERSION_EOOR:
				break;
			case SplashActivity.CHECKVERSION_DOWN: {
				// 通知栏更新，下载文件
				Intent updateIntent = new Intent(AppSettingActivity.this,
						UpdateService.class);
				updateIntent.putExtra("appUrl", downLoadFileUrl);
				startService(updateIntent);
				break;
			}
			case UPDATEADAPTER: {
				appSettingAdapter.notifyDataSetChanged();
				break;
			}
			default:
				break;
			}
		}

	};

	private OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			SwitchButton switchButton = (SwitchButton) buttonView;
			LogUtil.e("colin", "switch is changed:" + switchButton.getId());
			switch (switchButton.getId()) {
			case 2: // 启用加锁
			{
				boolean bIsState = !switchButton.isChecked();
				appLockApplication.setAppLockState(bIsState);
				break;
			}
			case 8: // 新应用加锁提示
			{
				break;
			}
			case 9: // 允许短暂退出
			{
				boolean bIsState = !switchButton.isChecked();
				appLockApplication.setAllowedLeaveAment(bIsState);
				Intent intent = new Intent(LockService.LOCK_SERVICE_LEAVEAMENT);
				intent.putExtra(LockService.LOCK_SERVICE_LEAVEAMENT, bIsState);
				sendBroadcast(intent);
				LogUtil.d("demo3", "bIsState:" + bIsState);
				break;
			}
			case 11: // 锁锁图标的隐藏和显示
			{
				setHideAppIcon();
				break;
			}
			case 12: // 防卸载
			{
				if (!switchButton.isChecked()
						&& !devicePolicyManager.isAdminActive(componentName)) {
					enableDeviceManager();
				} else if (switchButton.isChecked()
						&& devicePolicyManager.isAdminActive(componentName)) {
					disableDeviceManager();
				}
				break;
			}
			case 18: // 自动拍照
			{
				appLockApplication.setAutoRecordPic(!switchButton.isChecked());
				if (appLockApplication.getAutoRecordPic()) {
					WarringDialog warringDialog = new WarringDialog();
					warringDialog.switchButton = switchButton;
					warringDialog.show();
				}
				break;
			}
			case 19: // 播放告警声音
			{
				appLockApplication.setPlayWarringSoundState(!switchButton
						.isChecked());
				break;
			}
			default:
				break;
			}
			appSettingAdapter.notifyDataSetChanged();
		}
	};

	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			ViewHoder appSettingItemInfo = (ViewHoder) view.getTag();
			LogUtil.e("colin", "ll:" + appSettingItemInfo.id);
			switch (appSettingItemInfo.id) {
			case 4: {
				changePassword();
				break;
			}
			case 6: // 密保设置
			{
				showSetSecretDialog();
				break;
			}
			case 10: // 短暂退出时间
			{
				LogUtil.e("colin", "短暂退出时间");
				if (appLockApplication.allowedLeaveAment) {
					showSetLeaveTimeDialog();
				}
				break;
			}
			case 14: // 用户反馈
			{
				setUpUmengFeedback();
				break;
			}
			case 15: // 常见问题
			{
				startActivity(new Intent(AppSettingActivity.this,
						NormalQAActivity.class));
				break;
			}
			case 16: // 版本更新
			{
				checkVersion();
				break;
			}
			case 17: // 错误解锁日志
			{
				startActivity(new Intent(AppSettingActivity.this,
						LookMyPrivateActivity.class));
				break;
			}
			case 22: // 精品推荐
			{
				startActivity(new Intent(AppSettingActivity.this,
						AppsLinkActivity.class));
				break;
			}
			case 20: // 分享
			{
				systemShare();
				break;
			}
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appsetting);
		context = this;
		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		componentName = new ComponentName(this, DeviceMyReceiver.class);
		appLockApplication.appIconIsHided = getHideAppIcon();

		appSettingListView = (ListView) findViewById(R.id.appsettinglistview);
		appSettingAdapter = new AppSettingAdapter(context,
				mCheckedChangeListener, appLockApplication);
		appSettingAdapter.setComponentName(componentName);
		appSettingAdapter.setDevicePolicyManager(devicePolicyManager);
		appSettingListView.setAdapter(appSettingAdapter);
		appSettingListView.setOnItemClickListener(itemClickListener);

		AppSettingItemInfo appSettingItemInfo = new AppSettingItemInfo(1, 0,
				true, false, false, getString(R.string.server_title), "", "",
				"");
		AppSettingItemInfo appSettingItemInfo1 = new AppSettingItemInfo(2, 1,
				false, true, false, "",
				getString(R.string.server_startlock_title),
				getString(R.string.server_startlock_detail), "");
		AppSettingItemInfo appSettingItemInfo2 = new AppSettingItemInfo(3,
				3000, true, false, false, getString(R.string.pwdsetting_title),
				"", "", "");
		AppSettingItemInfo appSettingItemInfo3 = new AppSettingItemInfo(4, 3,
				false, false, true, "",
				getString(R.string.pwdsetting_modify_title),
				getString(R.string.pwdsetting_modify_detail),
				getString(R.string.pwdsetting_modify_handler));
		AppSettingItemInfo appSettingItemInfo4 = new AppSettingItemInfo(5, 3,
				false, false, true, "",
				getString(R.string.pwdsetting_notrue_title),
				getString(R.string.pwdsetting_notrue_detail), "");
		AppSettingItemInfo appSettingItemInfo5 = new AppSettingItemInfo(6, 3,
				false, false, true, "",
				getString(R.string.pwdsetting_secret_title),
				getString(R.string.pwdsetting_secret_detail), "");
		AppSettingItemInfo appSettingItemInfo6 = new AppSettingItemInfo(7,
				3001, true, false, false,
				getString(R.string.pwdsetting_advance_title), "", "", "");
		AppSettingItemInfo appSettingItemInfo17 = new AppSettingItemInfo(18, 7,
				false, true, false, "",
				getString(R.string.pwdsetting_advance_aoturecordpic__title),
				getString(R.string.pwdsetting_advance_aoturecordpic__detail),
				"");
		AppSettingItemInfo appSettingItemInfo18 = new AppSettingItemInfo(
				19,
				7,
				false,
				true,
				false,
				"",
				getString(R.string.pwdsetting_advance_playwarringsound__title),
				getString(R.string.pwdsetting_advance_playwarringsound__detail),
				"");
		AppSettingItemInfo appSettingItemInfo7 = new AppSettingItemInfo(8, 7,
				false, true, false, "",
				getString(R.string.pwdsetting_advance_tipsnewapp_title),
				getString(R.string.pwdsetting_advance_tipsnewapp_detail), "");
		AppSettingItemInfo appSettingItemInfo8 = new AppSettingItemInfo(9, 7,
				false, true, false, "",
				getString(R.string.pwdsetting_advance_allowleave_title),
				getString(R.string.pwdsetting_advance_allowleave_detail), "");
		AppSettingItemInfo appSettingItemInfo9 = new AppSettingItemInfo(
				10,
				7,
				false,
				false,
				false,
				"",
				getString(R.string.pwdsetting_advance_allowleavetime_title),
				getString(R.string.pwdsetting_advance_allowleavetime_detail_30second),
				"");
		AppSettingItemInfo appSettingItemInfo10 = new AppSettingItemInfo(11, 7,
				false, true, false, "",
				getString(R.string.pwdsetting_advance_hideappicon_title),
				getString(R.string.pwdsetting_advance_hideappicon__detail), "");
		AppSettingItemInfo appSettingItemInfo11 = new AppSettingItemInfo(12, 7,
				false, true, false, "",
				getString(R.string.pwdsetting_advance_uninstallapp_title),
				getString(R.string.pwdsetting_advance_uninstallapp_detail), "");
		AppSettingItemInfo appSettingItemInfo12 = new AppSettingItemInfo(13,
				3002, true, false, false,
				getString(R.string.pwdsetting_aboutour_title), "", "", "");
		AppSettingItemInfo appSettingItemInfo21 = new AppSettingItemInfo(22,
				13, false, false, true, "",
				getString(R.string.pwdsetting_aboutour_apps_title),
				getString(R.string.pwdsetting_aboutour_apps_detail), "");
		AppSettingItemInfo appSettingItemInfo13 = new AppSettingItemInfo(14,
				13, false, false, true, "",
				getString(R.string.pwdsetting_aboutour_feedback_title),
				getString(R.string.pwdsetting_aboutour_feedback_detail), "");
		AppSettingItemInfo appSettingItemInfo14 = new AppSettingItemInfo(15,
				13, false, false, true, "",
				getString(R.string.pwdsetting_aboutour_qa_title),
				getString(R.string.pwdsetting_aboutour_qa_detail), "");
		AppSettingItemInfo appSettingItemInfo15 = new AppSettingItemInfo(16,
				13, false, false, true, "",
				getString(R.string.pwdsetting_aboutour_version_title),
				getString(R.string.pwdsetting_aboutour_version_detail),
				getString(R.string.pwdsetting_aboutour_version_hasnew));
		AppSettingItemInfo appSettingItemInfo16 = new AppSettingItemInfo(17,
				13, false, false, true, "",
				getString(R.string.pwdsetting_aboutour_lookmyprivate_title),
				getString(R.string.pwdsetting_aboutour_lookmyprivate_detail),
				"");

		AppSettingItemInfo appSettingItemInfo19 = new AppSettingItemInfo(20, 1,
				false, false, true, "",
				getString(R.string.pwdsetting_share_title),
				getString(R.string.pwdsetting_share_detail), "");

		dataAppSettingItemInfos.add(appSettingItemInfo);
		dataAppSettingItemInfos.add(appSettingItemInfo1);
		dataAppSettingItemInfos.add(appSettingItemInfo16);
		dataAppSettingItemInfos.add(appSettingItemInfo2);
		dataAppSettingItemInfos.add(appSettingItemInfo3);
		dataAppSettingItemInfos.add(appSettingItemInfo5);
		dataAppSettingItemInfos.add(appSettingItemInfo6);
		dataAppSettingItemInfos.add(appSettingItemInfo17);
		dataAppSettingItemInfos.add(appSettingItemInfo18);
		dataAppSettingItemInfos.add(appSettingItemInfo7);
		dataAppSettingItemInfos.add(appSettingItemInfo8);
		dataAppSettingItemInfos.add(appSettingItemInfo9);
		dataAppSettingItemInfos.add(appSettingItemInfo12);
		dataAppSettingItemInfos.add(appSettingItemInfo21);
		dataAppSettingItemInfos.add(appSettingItemInfo13);
		dataAppSettingItemInfos.add(appSettingItemInfo15);

		addAdapter(dataAppSettingItemInfos);

		leaveTimeStrings
				.add(getString(R.string.pwdsetting_advance_allowleavetime_detail_30second));
		leaveTimeStrings
				.add(getString(R.string.pwdsetting_advance_allowleavetime_detail_1minute));
		leaveTimeStrings
				.add(getString(R.string.pwdsetting_advance_allowleavetime_detail_2minute));
		leaveTimeStrings
				.add(getString(R.string.pwdsetting_advance_allowleavetime_detail_5minute));
	}

	private void addAdapter(Vector<AppSettingItemInfo> itemInfos) {
		Vector<AppSettingItemInfo> appSettingItemInfos = new Vector<AppSettingItemInfo>();
		appSettingItemInfos.removeAllElements();

		AppSettingItemInfo tmp = null;
		Set<Integer> set = new HashSet<Integer>();
		if (itemInfos != null && itemInfos.size() > 0) {
			for (int i = 0; i < itemInfos.size(); i++) {
				tmp = itemInfos.get(i);
				if (set.contains(tmp.parentID)) {
					appSettingItemInfos.add(tmp);
				} else {
					set.add(tmp.parentID);
					appSettingItemInfos.add(tmp);
				}
			}
			appSettingAdapter.removeAll();
			allItemInfos = appSettingItemInfos;
			for (AppSettingItemInfo appSettingItemInfo : appSettingItemInfos) {
				appSettingAdapter.addItem(appSettingItemInfo);
			}
		}
	}

	@Override
	public void onClickEvent(View view) {
		switch (view.getId()) {
		case R.id.btn_back:
			finish();
			break;

		default:
			break;
		}
		super.onClickEvent(view);
	}

	private void setUpUmengFeedback() {
		startActivity(new Intent(AppSettingActivity.this,
				FeedbackActivity.class));
	}

	private void showSetLeaveTimeDialog() {
		final AlertDialog updateDialogDlg = new AlertDialog.Builder(this)
				.create();
		updateDialogDlg.show();
		Window win = updateDialogDlg.getWindow();
		win.setContentView(R.layout.dialog_setleavetime);
		WheelView wheelView = (WheelView) win.findViewById(R.id.wv_leavetime);
		TextView titleTextView = (TextView) win.findViewById(R.id.update_title);
		titleTextView.setText(R.string.setleavetimetitle);
		AbstractWheelTextAdapter adapter = new AbstractWheelTextAdapter(
				getApplicationContext()) {

			@Override
			public int getItemsCount() {
				// TODO Auto-generated method stub
				return leaveTimeStrings.size();
			}

			@Override
			protected CharSequence getItemText(int index) {
				// TODO Auto-generated method stub
				return leaveTimeStrings.get(index);
			}
		};
		OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				String setTimeString = leaveTimeStrings.get(wheel
						.getCurrentItem());
				appLockApplication.setAllowedLeaveTime(setTimeString);
			}
		};
		adapter.setItemResource(R.layout.item_wheel_leavetime);
		adapter.setItemTextResource(R.id.tv_text);
		wheelView.setViewAdapter(adapter);
		wheelView.setCyclic(true);
		wheelView.addChangingListener(wheelListener);

		ImageView closeImageView = (ImageView) win
				.findViewById(R.id.updateclose);
		closeImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateDialogDlg.dismiss();
				Message msg = new Message();
				msg.what = SETLEAVEDIALOG_DISS;
				mHandler.sendMessage(msg);
			}
		});

		updateDialogDlg.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = SETLEAVEDIALOG_DISS;
				mHandler.sendMessage(msg);
			}
		});
	}

	private void showSetSecretDialog() {
		startActivity(new Intent(AppSettingActivity.this, SecretConfig.class));
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
		if (appLockApplication.hasNewVersion()) {
			downLoadFileUrl = appLockApplication.getUpdateVersionUrl();
			showUpdateDialog(appLockApplication.getUpdateVersionIntro());
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
				Message msg = new Message();
				msg.what = SplashActivity.CHECKVERSION_CANCEL;
				mHandler.sendMessage(msg);
			}
		});

		btOk.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				updateDialogDlg.dismiss();
				Message msg = new Message();
				msg.what = SplashActivity.CHECKVERSION_DOWN;
				mHandler.sendMessage(msg);
			}
		});

		updateDialogDlg.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = SplashActivity.CHECKVERSION_CANCEL;
				mHandler.sendMessage(msg);
			}
		});
	}

	public void setHideAppIcon() {
		PackageManager packageManager = getPackageManager();
		ComponentName componentName = new ComponentName(
				AppSettingActivity.this, SplashActivity.class);
		int res = packageManager.getComponentEnabledSetting(componentName);
		if (res == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
				|| res == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
			// 隐藏应用图标
			packageManager.setComponentEnabledSetting(componentName,
					PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
					PackageManager.DONT_KILL_APP);
			appLockApplication.appIconIsHided = true;
			LogUtil.e("colin", "隐藏应用图标");
		} else {
			// 显示应用图标
			packageManager.setComponentEnabledSetting(componentName,
					PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
					PackageManager.DONT_KILL_APP);
			appLockApplication.appIconIsHided = false;
			LogUtil.e("colin", "显示应用图标");
		}
	}

	public boolean getHideAppIcon() {
		PackageManager packageManager = getPackageManager();
		ComponentName componentName = new ComponentName(
				AppSettingActivity.this, SplashActivity.class);
		int res = packageManager.getComponentEnabledSetting(componentName);
		if (res == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
				|| res == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
			LogUtil.e("colin", "获取到应用图标显示在桌面上");
			return true;
		} else {
			LogUtil.e("colin", "获取到应用图标不显示在桌面上");
			return false;
		}
	}

	private void changePassword() {
		if (SharedPreferenceUtil.readIsNumModel()) {
			Intent intent = new Intent(context, NumberCheckActivity.class);
			intent.putExtra(NumberCheckActivity.CHANGE_PASSWORD, true);
			startActivity(intent);
		} else {
			Intent intent = new Intent(context, GestureCheckActivity.class);
			intent.putExtra(GestureCheckActivity.CHANGE_PASSWORD, true);
			startActivity(intent);
		}
	}

	private void hideLeaverTime() {
		for (AppSettingItemInfo appSettingItemInfo : dataAppSettingItemInfos) {
			if (appSettingItemInfo.getClassID() == 10) {
				dataAppSettingItemInfos.remove(appSettingItemInfo);
				appSettingAdapter.notifyDataSetChanged();
				break;
			}
		}
	}

	private void showLeaverTime() {
		AppSettingItemInfo appSettingItemInfo9 = new AppSettingItemInfo(
				10,
				7,
				false,
				false,
				false,
				"",
				getString(R.string.pwdsetting_advance_allowleavetime_title),
				getString(R.string.pwdsetting_advance_allowleavetime_detail_30second),
				"");
		dataAppSettingItemInfos.add(appSettingItemInfo9);
		appSettingAdapter.notifyDataSetChanged();
	}

	class WarringDialog extends Dialog implements View.OnClickListener {

		public SwitchButton switchButton;

		public WarringDialog() {
			super(context, R.style.MyDialog);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			setContentView(R.layout.dialog_warring);
			findViewById(R.id.btn_cancel).setOnClickListener(this);
			findViewById(R.id.btn_ok).setOnClickListener(this);
			super.onCreate(savedInstanceState);
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_cancel:
				switchButton.setChecked(true);
				appLockApplication.setAutoRecordPic(false);
				appSettingAdapter.notifyDataSetChanged();
				dismiss();
				break;
			case R.id.btn_ok:
				dismiss();
				break;

			default:
				break;
			}
		}
	}

	private void systemShare() {
		Bitmap bm = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		String imgPath = FileService.createShareImage(bm, context);
		String title = getString(R.string.pwdsetting_share_detail);
		String text = getString(R.string.pwdsetting_share_text);

		shareMsg(title, title, text, imgPath);
	}

	public void shareMsg(String activityTitle, String msgTitle, String msgText,
			String imgPath) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		if (imgPath == null || imgPath.equals("")) {
			intent.setType("text/plain"); // 纯文本
		} else {
			File f = new File(imgPath);
			if (f != null && f.exists() && f.isFile()) {
				intent.setType("image/jpg");
				Uri uri = Uri.fromFile(f);
				intent.putExtra(Intent.EXTRA_STREAM, uri);
			}
		}
		intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
		intent.putExtra(Intent.EXTRA_TEXT, msgText);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, activityTitle));
	}

	public void enableDeviceManager() {
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				getString(R.string.pwdsetting_advance_uninstallapp_detail));
		context.startActivity(intent);
	}

	public void disableDeviceManager() {
		devicePolicyManager.removeActiveAdmin(componentName);
		mHandler.sendEmptyMessageDelayed(UPDATEADAPTER, 1500);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		appSettingAdapter.notifyDataSetChanged();
		super.onResume();
	}
}
