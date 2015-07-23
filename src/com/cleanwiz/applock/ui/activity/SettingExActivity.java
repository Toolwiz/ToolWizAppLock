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

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.cleanwiz.applock.R;
import com.cleanwiz.applock.model.SettingItem;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.ui.adapter.SettingAdapter;

public class SettingExActivity extends BaseActivity {

	private SettingExActivity mContext;

	private ListView sListView;
	private List<SettingItem> settingList;
	private SettingAdapter settingAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_appsetting);
		mContext = this;
		initSetting();
		initView();
		super.onCreate(savedInstanceState);
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

	private void initView() {

		findViewById(R.id.btn_setting).setVisibility(View.INVISIBLE);
		((TextView) findViewById(R.id.tv_title))
				.setText(R.string.pwdsetting_advance_title);

		sListView = (ListView) findViewById(R.id.appsettinglistview);
		settingAdapter = new SettingAdapter(mContext, settingList);
		sListView.setAdapter(settingAdapter);
	}

	private void initSetting() {

		settingList = new ArrayList<SettingItem>();
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
		settingList.add(new SettingItem(23, R.drawable.setting_item_23,
				R.string.pwdsetting_advance_tipsnewapp_title,
				R.string.pwdsetting_advance_tipsnewapp_detail,
				R.string.pwdsetting_advance_notipsnewapp_detail,
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
