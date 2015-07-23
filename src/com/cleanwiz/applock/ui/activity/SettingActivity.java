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

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.cleanwiz.applock.R;
import com.cleanwiz.applock.model.SettingItem;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.ui.adapter.SettingAdapter;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends BaseActivity {

	private SettingActivity mContext;

	private ListView sListView;
	private List<SettingItem> settingList;
	private SettingAdapter settingAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appsetting);
		setStatusBarMargin(findViewById(R.id.layout_setting));
		mContext = this;
		initSetting();
		initView();
	}

	@Override
	public void onClickEvent(View view) {
		switch (view.getId()) {
		case R.id.btn_menu:
			finish();
			break;
		default:
			break;
		}
		super.onClickEvent(view);
	}

	private void initView() {
		sListView = (ListView) findViewById(R.id.appsettinglistview);
		settingAdapter = new SettingAdapter(mContext, settingList);
		sListView.setAdapter(settingAdapter);
	}

	private void initSetting() {

		settingList = new ArrayList<SettingItem>();

		// normal
		settingList.add(new SettingItem(0, -1, R.string.title_set_normal, -1,
				-1, SettingItem.SET_TYPE_TITLE));

		settingList.add(new SettingItem(1, R.drawable.setting_item_01,
				R.string.server_startlock_title,
				R.string.server_startlock_detail,
				R.string.server_unlock_detail, SettingItem.SET_TYPE_ONOFF));
		settingList.add(new SettingItem(3, R.drawable.setting_item_03,
				R.string.pwdsetting_modify_title,
				R.string.pwdsetting_modify_detail,
				R.string.pwdsetting_modify_detail, SettingItem.SET_TYPE_ENTER));
		settingList.add(new SettingItem(4, R.drawable.setting_item_04,
				R.string.pwdsetting_secret_title,
				R.string.pwdsetting_secret_detail,
				R.string.pwdsetting_secret_detail, SettingItem.SET_TYPE_ENTER));

		// advanced
		settingList.add(new SettingItem(20, -1, R.string.title_set_advanced,
				-1, -1, SettingItem.SET_TYPE_TITLE));

		settingList.add(new SettingItem(5, R.drawable.setting_item_05,
				R.string.pwdsetting_advance_uninstallapp_title,
				R.string.pwdsetting_advance_uninstallapp_title,
				R.string.pwdsetting_advance_uninstallapp_title,
				SettingItem.SET_TYPE_ENTER));
		settingList.add(new SettingItem(21, R.drawable.setting_item_21,
				R.string.pwdsetting_advance_aoturecordpic__title,
				R.string.pwdsetting_advance_aoturecordpic__detail,
				R.string.pwdsetting_advance_noaoturecordpic__detail,
				SettingItem.SET_TYPE_ONOFF));
		settingList.add(new SettingItem(22, R.drawable.setting_item_22,
				R.string.pwdsetting_advance_playwarringsound__title,
				R.string.pwdsetting_advance_playwarringsound__detail,
				R.string.pwdsetting_advance_noplaywarringsound__detail,
				SettingItem.SET_TYPE_ONOFF));
		settingList.add(new SettingItem(24, R.drawable.setting_item_24,
				R.string.pwdsetting_advance_allowleave_title,
				R.string.pwdsetting_advance_allowleave_detail,
				R.string.pwdsetting_advance_noallowleave_detail,
				SettingItem.SET_TYPE_ONOFF));
		settingList.add(new SettingItem(25, -1,
				R.string.pwdsetting_advance_allowleavetime_title,
				R.string.pwdsetting_advance_allowleavetime_detail_30second,
				R.string.pwdsetting_advance_allowleavetime_detail_30second,
				SettingItem.SET_TYPE_TEXT));

	}

}
