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

import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Paint;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cleanwiz.applock.R;
import com.cleanwiz.applock.data.TimeManagerInfo;
import com.cleanwiz.applock.data.WIFILockManager;
import com.cleanwiz.applock.service.WifiLockService;
import com.cleanwiz.applock.service.WifiManagerService;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.ui.activity.SplashActivity.SplashHandler;
import com.cleanwiz.applock.ui.widget.SwitchButton;
import com.cleanwiz.applock.utils.LangUtils;

public class WifiLockMgrActivity extends BaseActivity {

	private WifiLockMgrActivity mContext;

	private ListView listView;
	private WifiAdapter wifiAdapter;
	private View noneLayout;

	private WifiManagerService wifiMgr;
	private WifiLockService wifiService;

	private OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			SwitchButton switchButton = (SwitchButton) buttonView;
			if (switchButton != null) {
				WIFILockManager wifiLockManager = (WIFILockManager) switchButton
						.getTag();
				if (wifiLockManager != null) {
					if (isChecked) {
						wifiLockManager.setIsOn(false);
					} else {
						wifiLockManager.setIsOn(true);
					}
					wifiMgr.switchWifiLockManager(wifiLockManager);
					wifiAdapter.notifyDataSetChanged();
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifilock_mgr);
		mContext = this;
		listView = (ListView) findViewById(R.id.listview);
		noneLayout = findViewById(R.id.layout_none);

	}

	@Override
	protected void onResume() {

		if (LangUtils.isChinese()) {
			((ImageView) findViewById(R.id.iv_tips))
					.setImageResource(R.drawable.wifi_pic_cn);
		}

		wifiMgr = new WifiManagerService(mContext);
		wifiService = new WifiLockService(mContext);
		List<WIFILockManager> tmInfos = wifiMgr.getallWifiLockManaer();
		wifiAdapter = new WifiAdapter(mContext, tmInfos);
		listView.setAdapter(wifiAdapter);
		listView.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				wifiAdapter.hideOptLayout();
				return false;
			}
		});
		if (wifiAdapter.getCount() == 0) {
			noneLayout.setVisibility(View.VISIBLE);
		} else {
			noneLayout.setVisibility(View.INVISIBLE);
		}
		super.onResume();
	}

	private void addWifiLock() {
		if (wifiMgr != null && wifiMgr.wifiIsEnable()) {
			startActivity(new Intent(mContext, WifiLockEditActivity.class));
		} else {
			showUpdateDialog("您的WIFI开关没有打开哦");
		}
	}

	@Override
	public void onClickEvent(View view) {
		switch (view.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_add_timelock:
			addWifiLock();
			break;
		case R.id.btn_none:
			addWifiLock();
			break;

		default:
			break;
		}
		super.onClickEvent(view);
	}

	class WifiAdapter extends BaseAdapter {

		static final float ALPHA_DISABLE = 0.6f;
		static final float ALPHA_ENABLE = 1.0f;

		List<WIFILockManager> lockInfos;

		LayoutInflater inflater;
		View tmpOptLayout;
		boolean longClickFlag = false;

		public WifiAdapter(Context context, List<WIFILockManager> tmInfos) {
			super();
			inflater = LayoutInflater.from(context);
			this.lockInfos = tmInfos;
		}

		@Override
		public int getCount() {
			return lockInfos.size();
		}

		@Override
		public WIFILockManager getItem(int position) {
			return lockInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public void hideOptLayout() {
			if (tmpOptLayout != null) {
				tmpOptLayout.setVisibility(View.INVISIBLE);
				tmpOptLayout = null;
			}
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_wifilock, null);
				convertView.setTag(initViewHolder(convertView));
			}
			ViewHolder vh = (ViewHolder) convertView.getTag();
			WIFILockManager lockInfo = getItem(position);
			initItemView(vh, lockInfo);

			return convertView;
		}

		private ViewHolder initViewHolder(View view) {
			ViewHolder vh = new ViewHolder();
			vh.btnCheck = view.findViewById(R.id.btn_check);
			vh.ivCheck = (SwitchButton) view.findViewById(R.id.iv_check);
			vh.tvSSID = (TextView) view.findViewById(R.id.tv_ssid);
			vh.tvName = (TextView) view.findViewById(R.id.tv_name);
			vh.layoutOpt = view.findViewById(R.id.layout_ed);
			vh.btnEdit = view.findViewById(R.id.btn_edit);
			vh.btnDel = view.findViewById(R.id.btn_del);
			vh.layoutItem = view.findViewById(R.id.layout_item);
			vh.tvSee = (TextView) view.findViewById(R.id.tv_see);
			vh.layoutShow = view.findViewById(R.id.layout_show);
			return vh;
		}

		private void initItemView(final ViewHolder vh, WIFILockManager lockInfo) {

			ItemClickListener icListener = new ItemClickListener(lockInfo);
			vh.btnCheck.setOnClickListener(icListener);
			vh.btnEdit.setOnClickListener(icListener);
			vh.btnDel.setOnClickListener(icListener);
			vh.layoutItem.setOnClickListener(icListener);

			vh.layoutItem.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					hideOptLayout();
					vh.layoutOpt.setVisibility(View.VISIBLE);
					tmpOptLayout = vh.layoutOpt;
					longClickFlag = true;
					return false;
				}
			});

			vh.tvSSID.setText(lockInfo.getSsidName());

			int num = wifiService.getLockInfosByMAnager(lockInfo).size();
			String format = getResources().getString(R.string.mgr_apps);
			String str = String.format(format, num);
			vh.tvName.setText(str);

			vh.tvSee.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

			if (lockInfo.getIsOn()) {
				vh.ivCheck.setChecked(false);
				vh.layoutShow.setAlpha(ALPHA_ENABLE);
			} else {
				vh.ivCheck.setChecked(true);
				vh.layoutShow.setAlpha(ALPHA_DISABLE);
			}
			vh.ivCheck.setTag(lockInfo);
			vh.ivCheck.setOnCheckedChangeListener(mOnCheckedChangeListener);
			vh.layoutOpt.setVisibility(View.INVISIBLE);

		}

		class ViewHolder {

			SwitchButton ivCheck;
			View btnCheck;
			TextView tvSSID;
			TextView tvName;
			TextView tvSee;
			View layoutOpt;
			View btnEdit;
			View btnDel;
			View layoutItem;
			View layoutShow;

		}

		class ItemClickListener implements OnClickListener {

			WIFILockManager lockInfo;

			public ItemClickListener(WIFILockManager lockInfo) {
				super();
				this.lockInfo = lockInfo;
			}

			@Override
			public void onClick(View v) {

				if (longClickFlag) {
					longClickFlag = false;
					return;
				}
				hideOptLayout();

				switch (v.getId()) {
				case R.id.btn_check:
					checkClick();
					break;
				case R.id.btn_edit:
					editClick();
					break;
				case R.id.btn_del:
					deleteClick();
					break;
				case R.id.layout_item:
					itemClick();
					break;

				default:
					break;
				}
			}

			private void checkClick() {

				if (lockInfo.getIsOn()) {
					lockInfo.setIsOn(false);
				} else {
					lockInfo.setIsOn(true);
				}
				wifiMgr.switchWifiLockManager(lockInfo);
				notifyDataSetChanged();
			}

			private void itemClick() {
				Intent intent = new Intent(mContext, ChooseAppsActivity.class);
				intent.putExtra(ChooseAppsActivity.APP_LIST_FLAG,
						ChooseAppsActivity.FLAG_WIFI_LOCK);
				intent.putExtra(ChooseAppsActivity.EXT_WIFI_ID,
						lockInfo.getId());
				intent.putExtra(ChooseAppsActivity.MODEL_NAME,
						lockInfo.getSsidName());
				startActivity(intent);
			}

			private void editClick() {
				
			}

			private void deleteClick() {
				wifiMgr.deleteWifiLockManager(lockInfo);
				wifiService.deleteAllLockByWifiLockManager(lockInfo);
				lockInfos.remove(lockInfo);
				notifyDataSetChanged();
			}

		}
	}

	public void showUpdateDialog(String intro) {
		final AlertDialog updateDialogDlg = new AlertDialog.Builder(this)
				.create();
		updateDialogDlg.show();
		Window win = updateDialogDlg.getWindow();
		win.setContentView(R.layout.dialog_update);
		TextView titleTextView = (TextView) win.findViewById(R.id.update_title);
		titleTextView.setText("锁锁提示");
		TextView tvMsg = (TextView) win.findViewById(R.id.tvMsg);
		tvMsg.setText(intro);
		Button btOk = (Button) win.findViewById(R.id.btOk);
		btOk.setText("打开WIFI");
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
				WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				wifiManager.setWifiEnabled(true);
				updateDialogDlg.dismiss();
			}
		});

		updateDialogDlg.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub

			}
		});
	}

}
