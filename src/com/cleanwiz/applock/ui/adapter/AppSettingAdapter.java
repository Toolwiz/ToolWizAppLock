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

import java.util.List;
import java.util.Vector;

import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.data.UpdateVersionManafer;
import com.cleanwiz.applock.model.AppSettingItemInfo;
import com.cleanwiz.applock.service.UpdateService;
import com.cleanwiz.applock.service.UpdateVersionManagerService;
import com.cleanwiz.applock.ui.activity.AppSettingActivity;
import com.cleanwiz.applock.ui.activity.SplashActivity;
import com.cleanwiz.applock.ui.widget.SwitchButton;
import com.cleanwiz.applock.utils.LogUtil;
import com.cleanwiz.applock.utils.SharedPreferenceUtil;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AppSettingAdapter extends BaseAdapter {

	private Context context;
	private Vector<AppSettingItemInfo> itemInfos;
	private LayoutInflater mInflater;
	private OnCheckedChangeListener mCheckedChangeListener = null;
	private AppLockApplication application = null;
	public DevicePolicyManager devicePolicyManager;
	public ComponentName componentName;

	public AppSettingAdapter(Context context,
			OnCheckedChangeListener mCheckedChangeListener,
			AppLockApplication application) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		itemInfos = new Vector<AppSettingItemInfo>();
		this.mCheckedChangeListener = mCheckedChangeListener;
		this.application = application;
	}

	public DevicePolicyManager getDevicePolicyManager() {
		return devicePolicyManager;
	}

	public void setDevicePolicyManager(DevicePolicyManager devicePolicyManager) {
		this.devicePolicyManager = devicePolicyManager;
	}

	public ComponentName getComponentName() {
		return componentName;
	}

	public void setComponentName(ComponentName componentName) {
		this.componentName = componentName;
	}

	public void removeAll() {
		itemInfos.clear();
		this.notifyDataSetChanged();
	}

	public void addItem(AppSettingItemInfo item) {
		itemInfos.add(item);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return itemInfos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return itemInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHoder viewHoder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_appsetting, null);
			viewHoder = new ViewHoder();

			viewHoder.partLayout = (LinearLayout) convertView
					.findViewById(R.id.classGroupLayout);
			viewHoder.detailleftlinearlayout = (LinearLayout) convertView
					.findViewById(R.id.classleftlinearlayout);
			viewHoder.topTitleTextView = (TextView) convertView
					.findViewById(R.id.class_part_name);
			viewHoder.detailTitleTextView = (TextView) convertView
					.findViewById(R.id.appsetting_detailtitle);
			viewHoder.detailDescriptionTextView = (TextView) convertView
					.findViewById(R.id.appsetting_detaildescription);
			viewHoder.tipsTextView = (TextView) convertView
					.findViewById(R.id.appsetting_tips);
			viewHoder.switchImageView = (SwitchButton) convertView
					.findViewById(R.id.appsetting_switch);
			viewHoder.goAnotherImageView = (ImageView) convertView
					.findViewById(R.id.appsetting_tipsimage);

			convertView.setTag(viewHoder);

		} else {
			viewHoder = (ViewHoder) convertView.getTag();
		}

		AppSettingItemInfo appSettingItemInfo = itemInfos.get(position);
		if (appSettingItemInfo != null) {
			viewHoder.id = appSettingItemInfo.getClassID();
			if (appSettingItemInfo.isTopShow) {
				viewHoder.partLayout.setVisibility(View.VISIBLE);
				viewHoder.partLayout.setEnabled(false);
				viewHoder.detailleftlinearlayout.setVisibility(View.GONE);
				viewHoder.topTitleTextView.setText(appSettingItemInfo
						.getTopTitle());
			} else {
				viewHoder.partLayout.setVisibility(View.GONE);
				viewHoder.detailleftlinearlayout.setVisibility(View.VISIBLE);
				viewHoder.detailTitleTextView.setText(appSettingItemInfo
						.getDetailTitle());
				viewHoder.detailDescriptionTextView.setText(appSettingItemInfo
						.getDetailDescription());
				if (appSettingItemInfo.isShowGoAnother) {
					viewHoder.goAnotherImageView.setVisibility(View.VISIBLE);
					viewHoder.tipsTextView.setVisibility(View.VISIBLE);
					viewHoder.switchImageView.setVisibility(View.GONE);
					viewHoder.goAnotherImageView
							.setImageResource(R.drawable.setting_detail);
					viewHoder.tipsTextView
							.setText(appSettingItemInfo.getTips());
				}
				if (appSettingItemInfo.isShowSwitch) {
					viewHoder.goAnotherImageView.setVisibility(View.GONE);
					viewHoder.tipsTextView.setVisibility(View.GONE);
					viewHoder.switchImageView.setVisibility(View.VISIBLE);
					viewHoder.switchImageView
							.setOnCheckedChangeListener(mCheckedChangeListener);
					viewHoder.switchImageView.setId(appSettingItemInfo
							.getClassID());
					switch (appSettingItemInfo.getClassID()) {
					case 2: // 加锁
					{
						viewHoder.switchImageView.setChecked(!application
								.getAppLockState());
						if (!application.getAppLockState()) {
							viewHoder.detailTitleTextView
									.setText(R.string.server_unlock_title);
							viewHoder.detailDescriptionTextView
									.setText(R.string.server_unlock_detail);
						} else {
							viewHoder.detailTitleTextView
									.setText(R.string.server_startlock_title);
							viewHoder.detailDescriptionTextView
									.setText(R.string.server_startlock_detail);
						}
						break;
					}
					case 8: // 新应用加锁提示
					{
						break;
					}
					case 9: // 允许短暂退出
					{
						viewHoder.switchImageView.setChecked(!application
								.getAllowedLeaveAment());
						if (application.getAllowedLeaveAment()) {
							viewHoder.detailTitleTextView
									.setText(R.string.pwdsetting_advance_allowleave_title);
							viewHoder.detailDescriptionTextView
									.setText(R.string.pwdsetting_advance_allowleave_detail);
						} else {
							viewHoder.detailTitleTextView
									.setText(R.string.pwdsetting_advance_noallowleave_title);
							viewHoder.detailDescriptionTextView
									.setText(R.string.pwdsetting_advance_noallowleave_detail);
						}
						break;
					}
					case 11: // 锁锁图标的隐藏和显示
					{
						if (application.appIconIsHided) {
							viewHoder.detailTitleTextView
									.setText(R.string.pwdsetting_advance_showappicon_title);
							viewHoder.detailDescriptionTextView
									.setText(R.string.pwdsetting_advance_showappicon__detail);
						} else {
							viewHoder.detailTitleTextView
									.setText(R.string.pwdsetting_advance_hideappicon_title);
							viewHoder.detailDescriptionTextView
									.setText(R.string.pwdsetting_advance_hideappicon__detail);
						}
						break;
					}
					case 12: // 防止卸载
					{
						if (devicePolicyManager.isAdminActive(componentName)) {
							viewHoder.switchImageView.setChecked(false);
						} else {
							viewHoder.switchImageView.setChecked(true);
						}
						break;
					}
					case 16: // 版本更新
					{
						break;
					}
					case 18: // 自动拍照
					{
						viewHoder.switchImageView.setChecked(!application
								.getAutoRecordPic());
						if (application.getAutoRecordPic()) {
							viewHoder.detailTitleTextView
									.setText(R.string.pwdsetting_advance_aoturecordpic__title);
							viewHoder.detailDescriptionTextView
									.setText(R.string.pwdsetting_advance_aoturecordpic__detail);
						} else {
							viewHoder.detailTitleTextView
									.setText(R.string.pwdsetting_advance_noaoturecordpic__title);
							viewHoder.detailDescriptionTextView
									.setText(R.string.pwdsetting_advance_noaoturecordpic__detail);
						}
						break;
					}
					case 19: // 播放告警声音
					{
						viewHoder.switchImageView.setChecked(!application
								.getPlayWarringSoundState());
						if (application.getPlayWarringSoundState()) {
							viewHoder.detailTitleTextView
									.setText(R.string.pwdsetting_advance_playwarringsound__title);
							viewHoder.detailDescriptionTextView
									.setText(R.string.pwdsetting_advance_playwarringsound__detail);
						} else {
							viewHoder.detailTitleTextView
									.setText(R.string.pwdsetting_advance_noplaywarringsound__title);
							viewHoder.detailDescriptionTextView
									.setText(R.string.pwdsetting_advance_noplaywarringsound__detail);
						}
						break;
					}
					default:
						break;
					}
				}
				if (!appSettingItemInfo.isShowGoAnother()
						&& !appSettingItemInfo.isShowSwitch()) {
					viewHoder.goAnotherImageView.setVisibility(View.GONE);
					viewHoder.tipsTextView.setVisibility(View.GONE);
					viewHoder.switchImageView.setVisibility(View.GONE);
					
					if (appSettingItemInfo.getClassID() == 10) { // 短暂退出时间
						viewHoder.detailDescriptionTextView.setText(application
								.getAllowedLeaveTime());
					}
				} else {
					if (appSettingItemInfo.getClassID() == 16) {
						viewHoder.goAnotherImageView.setVisibility(View.GONE);
						viewHoder.goAnotherImageView.setVisibility(View.GONE);
						viewHoder.detailDescriptionTextView.setText(application.getApplicationVersion());
						if (application.hasNewVersion()) {
							String newVersionCodeString = getNewVersionString();
							if (newVersionCodeString != "") {
								viewHoder.tipsTextView
								.setText(context.getString(R.string.pwdsetting_aboutour_version_hasnew)+"("+newVersionCodeString+")");
							} else {
								viewHoder.tipsTextView
								.setText(R.string.pwdsetting_aboutour_version_hasnew);
							}
						} else {
							viewHoder.tipsTextView
									.setText(R.string.pwdsetting_aboutour_version_hasno);
						}
					} else if (appSettingItemInfo.getClassID() == 4) {
						if (SharedPreferenceUtil.readIsNumModel()) {
							viewHoder.tipsTextView
									.setText(R.string.pwdsetting_modify_number);
						} else {
							viewHoder.tipsTextView
									.setText(R.string.pwdsetting_modify_handler);
						}
					}
				}
			}
		}

		return convertView;
	}

	public class ViewHoder {
		public int id;
		LinearLayout partLayout;
		LinearLayout detailleftlinearlayout;
		TextView topTitleTextView;
		TextView detailTitleTextView;
		TextView detailDescriptionTextView;
		TextView tipsTextView;
		SwitchButton switchImageView;
		ImageView goAnotherImageView;
	}
	
	public String getNewVersionString() {
		UpdateVersionManagerService updateVersionManagerService = new UpdateVersionManagerService(context);
		List<UpdateVersionManafer> updateVersionManafers = updateVersionManagerService.getVersionManafers();
		for (UpdateVersionManafer updateVersionManafer : updateVersionManafers) {
			return String.valueOf(updateVersionManafer.getVersioncode());
		}
		return "";
	}

}
