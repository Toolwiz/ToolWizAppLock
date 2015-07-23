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

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelClickedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.data.CommLockInfo;
import com.cleanwiz.applock.data.WIFILockInfo;
import com.cleanwiz.applock.data.WIFILockManager;
import com.cleanwiz.applock.service.WifiLockService;
import com.cleanwiz.applock.service.WifiManagerService;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.utils.LogUtil;
import com.cleanwiz.applock.utils.StringUtils;
import com.cleanwiz.applock.utils.ToastUtils;

public class WifiLockEditActivity extends BaseActivity {
	private WifiLockEditActivity mContext;

	private WheelView wv_wifi;
	private EditText et_name;
	private TextView tv_num;

	private WifiManagerService wifiMgr;
	private String[] wifiStrs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifilock_edit);

		mContext = this;
		wifiMgr = new WifiManagerService(mContext);

		et_name = (EditText) findViewById(R.id.et_lockname);
		tv_num = (TextView) findViewById(R.id.tv_app_num);

		initWifiWheel();
		AppLockApplication.getInstance().setTmpLockInfos(null);
	}

	@Override
	protected void onResume() {
		setAppNum();
		super.onResume();
	}

	private List<CommLockInfo> apps;
	int num;

	private void setAppNum() {

		num = 0;
		apps = AppLockApplication.getInstance().getTmpLockInfos();
		if (apps != null) {
			for (CommLockInfo app : apps) {
				if (app.getIsLocked()) {
					num++;
				}
			}
		}
		String format = getResources().getString(R.string.time_edit_apps);
		String str = String.format(format, num);
		tv_num.setText(str);
	}

	private List<String> getNoLockSSID(List<String> allSSID) {
		List<String> nlSSID = new ArrayList<String>();
		List<WIFILockManager> tmInfos = wifiMgr.getallWifiLockManaer();
		for (String ssid : allSSID) {
			boolean noLock = true;
			for (WIFILockManager mgr : tmInfos) {
				if (mgr.getSsidName().equals(ssid)) {
					noLock = false;
				}
			}
			if (noLock) {
				nlSSID.add(ssid);
			}

		}
		return nlSSID;
	}

	private void initWifiWheel() {
		List<String> allSSID = null;
		try {
			allSSID = getNoLockSSID(wifiMgr.getallConnectedWifiSSID());
		} catch (Exception e) {

		}

		if (allSSID == null | allSSID.size() == 0) {
			finish();
		}

		wifiStrs = new String[allSSID.size()];
		int index = 0;
		for (String ssid : allSSID) {
			wifiStrs[index++] = ssid;
		}

		wv_wifi = (WheelView) findViewById(R.id.wv_wifi);
		ArrayWheelAdapter<String> wifiAdapter = new ArrayWheelAdapter<String>(
				mContext, wifiStrs);
		wifiAdapter.setItemResource(R.layout.item_wheel_wifi);
		wifiAdapter.setItemTextResource(R.id.tv_text);

		wv_wifi.setViewAdapter(wifiAdapter);
		wv_wifi.setCyclic(true);

		// add listeners
		addChangingListener(wv_wifi, "hour");

		OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				LogUtil.d("demo3", "get:" + wheel.getCurrentItem());
			}
		};
		wv_wifi.addChangingListener(wheelListener);

		OnWheelClickedListener click = new OnWheelClickedListener() {
			public void onItemClicked(WheelView wheel, int itemIndex) {
				wheel.setCurrentItem(itemIndex, true);
			}
		};
		wv_wifi.addClickingListener(click);

		OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
			}

			public void onScrollingFinished(WheelView wheel) {
			}
		};

		wv_wifi.addScrollingListener(scrollListener);

	}

	private void addChangingListener(final WheelView wheel, final String label) {
		wheel.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// wheel.setLabel(newValue != 1 ? label + "s" : label);
			}
		});
	}

	private void saveWifiLock() {

		String name = et_name.getText() != null ? et_name.getText().toString()
				: "";
		String ssid = wifiStrs[wv_wifi.getCurrentItem()];

		WIFILockManager lock = new WIFILockManager();
		lock.setIsOn(true);
		lock.setLockName(name);
		lock.setSsidName(ssid);
		WifiLockService wfService = new WifiLockService(mContext);
		long id = wifiMgr.insertNewWifiLockManager(lock);
		if (id > 0 && apps != null) {
			lock.setId(id);
			wfService.deleteAllLockByWifiLockManager(lock);
			for (CommLockInfo app : apps) {
				if (app.getIsLocked()) {
					WIFILockInfo wLock = new WIFILockInfo(null, "" + id,
							app.getPackageName());
					wfService.lockWifiLockInfo(wLock);
				}
			}
		}

	}

	@Override
	public void onClickEvent(View view) {
		switch (view.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_done:
			if (num != 0) {
				saveWifiLock();
				finish();
			} else {
				ToastUtils.showToast(R.string.lock_done_none);
			}
			break;
		case R.id.btn_enter_app:
			Intent intent = new Intent(mContext, ChooseAppsActivity.class);
			intent.putExtra(ChooseAppsActivity.APP_LIST_FLAG,
					ChooseAppsActivity.FLAG_WIFI_LOCK);
			intent.putExtra(ChooseAppsActivity.MODEL_NAME,
					wifiStrs[wv_wifi.getCurrentItem()]);
			startActivity(intent);
			break;
		default:
			break;
		}
		super.onClickEvent(view);
	}

}
