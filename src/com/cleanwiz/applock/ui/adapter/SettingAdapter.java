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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.data.UpdateVersionManafer;
import com.cleanwiz.applock.model.SettingItem;
import com.cleanwiz.applock.service.DeviceMyReceiver;
import com.cleanwiz.applock.service.LockService;
import com.cleanwiz.applock.service.UpdateService;
import com.cleanwiz.applock.service.UpdateVersionManagerService;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.ui.activity.FeedbackActivity;
import com.cleanwiz.applock.ui.activity.GestureCheckActivity;
import com.cleanwiz.applock.ui.activity.NumberCheckActivity;
import com.cleanwiz.applock.ui.activity.SecretConfig;
import com.cleanwiz.applock.utils.FileService;
import com.cleanwiz.applock.utils.SharedPreferenceUtil;
import com.gc.materialdesign.views.Switch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelClickedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

public class SettingAdapter extends BaseAdapter {

	private BaseActivity context;
	private List<SettingItem> settingList;
	private LayoutInflater inflater;
	private AppLockApplication application;
	private List<String> leaveTimeStrings;

	public SettingAdapter(BaseActivity context, List<SettingItem> settingList) {
		super();
		this.context = context;
		this.settingList = settingList;
		inflater = LayoutInflater.from(this.context);
		application = AppLockApplication.getInstance();
		leaveTimeStrings = new ArrayList<String>();
		leaveTimeStrings
				.add(context
						.getString(R.string.pwdsetting_advance_allowleavetime_detail_10second));
		leaveTimeStrings
				.add(context
						.getString(R.string.pwdsetting_advance_allowleavetime_detail_30second));
		leaveTimeStrings
				.add(context
						.getString(R.string.pwdsetting_advance_allowleavetime_detail_1minute));
		leaveTimeStrings
				.add(context
						.getString(R.string.pwdsetting_advance_allowleavetime_detail_2minute));
		leaveTimeStrings
				.add(context
						.getString(R.string.pwdsetting_advance_allowleavetime_detail_5minute));
	}

	@Override
	public int getCount() {
		return settingList.size();
	}

	@Override
	public SettingItem getItem(int position) {
		return settingList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).getType() == SettingItem.SET_TYPE_ONOFF ? 0
				: 1;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_setting, null);
			ViewHolder vh = new ViewHolder();
			vh.layout_item = convertView.findViewById(R.id.layout_item);
			vh.tv_set_name = (TextView) convertView
					.findViewById(R.id.tv_set_name);
			vh.tv_set_detail = (TextView) convertView
					.findViewById(R.id.tv_set_detail);
			vh.iv_onoff = (Switch) convertView.findViewById(R.id.iv_onoff);
			vh.layout_title = convertView.findViewById(R.id.layout_title);
			vh.tv_set_title = (TextView) convertView
					.findViewById(R.id.tv_set_title);
			vh.title_line = convertView.findViewById(R.id.title_line);
			convertView.setTag(vh);
		}
		initView(convertView, position);
		return convertView;
	}

	private void initView(View view, int position) {
		ViewHolder vh = (ViewHolder) view.getTag();
		SettingItem sItem = settingList.get(position);
		vh.tv_set_name.setText(sItem.getTitleId());
		switch (sItem.getType()) {
		case SettingItem.SET_TYPE_SECTION:
			vh.iv_onoff.setVisibility(View.GONE);
			vh.tv_set_detail.setVisibility(View.GONE);
			vh.layout_title.setVisibility(View.GONE);
			break;
		case SettingItem.SET_TYPE_ENTER:
			vh.iv_onoff.setVisibility(View.GONE);
			vh.tv_set_detail.setVisibility(View.GONE);
			vh.layout_item.setOnClickListener(new ItemListener(sItem, vh));
			vh.layout_title.setVisibility(View.GONE);
			break;
		case SettingItem.SET_TYPE_ONOFF:
			vh.iv_onoff.setVisibility(View.VISIBLE);
			vh.tv_set_detail.setVisibility(View.GONE);
			vh.layout_item.setOnClickListener(new ItemListener(sItem, vh));
			setCheck(vh.iv_onoff, sItem);
			vh.layout_title.setVisibility(View.GONE);
			break;
		case SettingItem.SET_TYPE_TEXT:
			vh.iv_onoff.setVisibility(View.GONE);
			vh.tv_set_detail.setVisibility(View.VISIBLE);
			vh.layout_item.setOnClickListener(new ItemListener(sItem, vh));
			vh.layout_title.setVisibility(View.GONE);
			break;
		case SettingItem.SET_TYPE_TITLE:
			vh.layout_title.setVisibility(View.VISIBLE);
			vh.tv_set_title.setText(sItem.getTitleId());
			if (position == 0) {
				vh.title_line.setVisibility(View.INVISIBLE);
			} else {
				vh.title_line.setVisibility(View.VISIBLE);
			}
			break;
		default:
			break;
		}

		if (sItem.getKey() == 25) {
			vh.tv_set_detail.setText(application.getAllowedLeaveTime());
		}
	}

	class ViewHolder {
		View layout_item;
		TextView tv_set_name;
		TextView tv_set_detail;
		com.gc.materialdesign.views.Switch iv_onoff;
		View layout_title;
		View title_line;
		TextView tv_set_title;
	}

	private void setCheck(final Switch sw, final SettingItem sItem) {
		boolean check = false;
		switch (sItem.getKey()) {
		case 1:
			check = !application.getAppLockState();
			break;
		case 21:
			check = application.getAutoRecordPic();
			break;
		case 22:
			check = application.getPlayWarringSoundState();
			break;
		case 23:
			check = SharedPreferenceUtil.readNewAppTips();
			break;
		case 24:
			check = application.getAllowedLeaveAment();
			break;
		default:
			break;
		}
		sw.setCheckedNoAnim(check);
	}

	class ItemListener implements OnClickListener {

		private SettingItem sItem;
		private ViewHolder vh;

		public ItemListener(SettingItem sItem, ViewHolder vh) {
			super();
			this.sItem = sItem;
			this.vh = vh;
		}

		@Override
		public void onClick(View v) {
			boolean check = false;
			switch (sItem.getKey()) {
			case 3:
				changePassword();
				break;
			case 5:
				deviceMgr();
				break;
			case 4:
				showSetSecret();
				break;
			case 25:
				if (application.allowedLeaveAment) {
					showSetLeaveTimeDialog();
				}
				break;
			case 1:
				check = !application.getAppLockState();
				vh.iv_onoff.setChecked(!check);
				application.setAppLockState(check);
				Intent intent = new Intent(LockService.LOCK_SERVICE_LOCKSTATE);
				intent.putExtra(LockService.LOCK_SERVICE_LOCKSTATE, check);
				context.sendBroadcast(intent);
				break;
			case 21:
				check = application.getAutoRecordPic();
				vh.iv_onoff.setChecked(!check);
				application.setAutoRecordPic(!check);
				break;
			case 22:
				check = application.getPlayWarringSoundState();
				vh.iv_onoff.setChecked(!check);
				application.setPlayWarringSoundState(!check);
				break;
			case 23:
				check = SharedPreferenceUtil.readNewAppTips();
				vh.iv_onoff.setChecked(!check);
				SharedPreferenceUtil.editNewAppTips(!check);
				break;
			case 24:
				check = application.getAllowedLeaveAment();
				vh.iv_onoff.setChecked(!check);
				application.setAllowedLeaveAment(!check);
				Intent intent1 = new Intent(LockService.LOCK_SERVICE_LEAVEAMENT);
				intent1.putExtra(LockService.LOCK_SERVICE_LEAVEAMENT, !check);
				context.sendBroadcast(intent1);
				break;
			default:
				break;
			}

		}
	}

	public void deviceMgr() {
		enableDeviceManager();
	}

	public void enableDeviceManager() {
		ComponentName componentName = new ComponentName(context,
				DeviceMyReceiver.class);
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, context
				.getString(R.string.pwdsetting_advance_uninstallapp_detail));
		context.startActivity(intent);
	}

	public String getNewVersionString() {
		UpdateVersionManagerService updateVersionManagerService = new UpdateVersionManagerService(
				context);
		List<UpdateVersionManafer> updateVersionManafers = updateVersionManagerService
				.getVersionManafers();
		for (UpdateVersionManafer updateVersionManafer : updateVersionManafers) {
			return String.valueOf(updateVersionManafer.getVersioncode());
		}
		return "";
	}

	private void changePassword() {
		if (SharedPreferenceUtil.readIsNumModel()) {
			Intent intent = new Intent(context, NumberCheckActivity.class);
			intent.putExtra(NumberCheckActivity.CHANGE_PASSWORD, true);
			context.startActivity(intent);
		} else {
			Intent intent = new Intent(context, GestureCheckActivity.class);
			intent.putExtra(GestureCheckActivity.CHANGE_PASSWORD, true);
			context.startActivity(intent);
		}
	}

	private void showSetSecret() {
		context.startActivity(new Intent(context, SecretConfig.class));
	}

	private void setUpUmengFeedback() {
		context.startActivity(new Intent(context, FeedbackActivity.class));
	}

	private void systemShare() {
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_launcher);
		String imgPath = FileService.createShareImage(bm, context);
		String title = context.getString(R.string.pwdsetting_share_detail);
		String text = context.getString(R.string.pwdsetting_share_text);

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
		context.startActivity(Intent.createChooser(intent, activityTitle));
	}

	String downLoadFileUrl = "";

	public void checkVersion() {
		if (application.hasNewVersion()) {
			downLoadFileUrl = application.getUpdateVersionUrl();
			showUpdateDialog(application.getUpdateVersionIntro());
		}
	}

	public void showUpdateDialog(String intro) {
		final AlertDialog updateDialogDlg = new AlertDialog.Builder(context)
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
			}
		});

		btOk.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				updateDialogDlg.dismiss();
				Intent updateIntent = new Intent(context, UpdateService.class);
				updateIntent.putExtra("appUrl", downLoadFileUrl);
				context.startService(updateIntent);
			}
		});

		updateDialogDlg.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
			}
		});
	}

	private void showSetLeaveTimeDialog() {

		final AlertDialog updateDialogDlg = new AlertDialog.Builder(context)
				.create();
		updateDialogDlg.show();
		Window win = updateDialogDlg.getWindow();
		win.setContentView(R.layout.dialog_setleavetime);
		WheelView wheelView = (WheelView) win.findViewById(R.id.wv_leavetime);
		TextView titleTextView = (TextView) win.findViewById(R.id.update_title);
		titleTextView.setText(R.string.setleavetimetitle);
		String tStr = application.getAllowedLeaveTime();
		int current = 0;
		for (int i = 0; i < leaveTimeStrings.size(); i++) {
			if (tStr.equals(leaveTimeStrings.get(i))) {
				current = i;
				break;
			}
		}
		AbstractWheelTextAdapter adapter = new AbstractWheelTextAdapter(
				context.getApplicationContext()) {

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
		OnWheelClickedListener clickedListener = new OnWheelClickedListener() {

			@Override
			public void onItemClicked(WheelView wheel, int itemIndex) {
				wheel.setCurrentItem(itemIndex, true);
			}
		};
		OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				String setTimeString = leaveTimeStrings.get(wheel
						.getCurrentItem());
				application.setAllowedLeaveTime(setTimeString);
				Intent intent = new Intent(LockService.LOCK_SERVICE_LEAVERTIME);
				intent.putExtra(LockService.LOCK_SERVICE_LEAVERTIME,
						application.getLeaverTime());
				context.sendBroadcast(intent);
			}
		};
		adapter.setItemResource(R.layout.item_wheel_leavetime);
		adapter.setItemTextResource(R.id.tv_text);
		wheelView.setViewAdapter(adapter);
		wheelView.setCyclic(true);
		wheelView.addChangingListener(wheelListener);
		wheelView.addClickingListener(clickedListener);
		wheelView.setCurrentItem(current, false);

		ImageView closeImageView = (ImageView) win
				.findViewById(R.id.updateclose);
		closeImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateDialogDlg.dismiss();
				notifyDataSetChanged();
			}
		});

		updateDialogDlg.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				notifyDataSetChanged();
			}
		});
	}

}
