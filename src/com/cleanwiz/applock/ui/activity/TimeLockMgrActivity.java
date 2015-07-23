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

import u.aly.bu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cleanwiz.applock.R;
import com.cleanwiz.applock.data.TimeManagerInfo;
import com.cleanwiz.applock.service.TimeLockInfoService;
import com.cleanwiz.applock.service.TimeManagerInfoService;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.ui.widget.SwitchButton;
import com.cleanwiz.applock.utils.LangUtils;
import com.cleanwiz.applock.utils.LogUtil;
import com.cleanwiz.applock.utils.StringUtils;

public class TimeLockMgrActivity extends BaseActivity {

	private TimeLockMgrActivity mContext;

	private ListView listView;
	private TimeAdapter timeAdapter;
	private View noneLayout;

	private TimeManagerInfoService timeMgr;
	private TimeLockInfoService timeService;
	
	private OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			SwitchButton switchButton = (SwitchButton)buttonView;
			if (switchButton != null) {
				TimeManagerInfo tManagerInfo = (TimeManagerInfo)switchButton.getTag();
				if (tManagerInfo != null) {
					if (tManagerInfo.getTimeIsOn()) {
						tManagerInfo.setTimeIsOn(false);
					} else {
						tManagerInfo.setTimeIsOn(true);
					}
					timeMgr.modifyManagerByTime(tManagerInfo);
					timeAdapter.notifyDataSetChanged();
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timelock_mgr);
		mContext = this;
		listView = (ListView) findViewById(R.id.listview);
		noneLayout = findViewById(R.id.layout_none);

	}

	@Override
	protected void onResume() {

		if (LangUtils.isChinese()) {
			((ImageView) findViewById(R.id.iv_tips))
					.setImageResource(R.drawable.time_pic_cn);
		}

		timeMgr = new TimeManagerInfoService(mContext);
		timeService = new TimeLockInfoService(mContext);
		List<TimeManagerInfo> tmInfos = timeMgr.getAllTimeManagerInfos();
		timeAdapter = new TimeAdapter(mContext, tmInfos);
		listView.setAdapter(timeAdapter);
		listView.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				timeAdapter.hideOptLayout();
				return false;
			}
		});
		if (timeAdapter.getCount() == 0) {
			noneLayout.setVisibility(View.VISIBLE);
		} else {
			noneLayout.setVisibility(View.INVISIBLE);
		}
		super.onResume();
	}

	private void addTimeLock() {
		startActivity(new Intent(mContext, TimeLockEditActivity.class));
	}

	@Override
	public void onClickEvent(View view) {
		switch (view.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_add_timelock:
			addTimeLock();
			break;
		case R.id.btn_none:
			addTimeLock();
			break;
		default:
			break;
		}
		super.onClickEvent(view);
	}

	class TimeAdapter extends BaseAdapter {

		static final float ALPHA_DISABLE = 0.6f;
		static final float ALPHA_ENABLE = 1.0f;

		List<TimeManagerInfo> tmInfos;

		LayoutInflater inflater;
		View tmpOptLayout;
		boolean longClickFlag = false;

		public TimeAdapter(Context context, List<TimeManagerInfo> tmInfos) {
			super();
			inflater = LayoutInflater.from(context);
			this.tmInfos = tmInfos;
		}

		@Override
		public int getCount() {
			return tmInfos.size();
		}

		@Override
		public TimeManagerInfo getItem(int position) {
			return tmInfos.get(position);
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
				convertView = inflater.inflate(R.layout.item_timelock, null);
				convertView.setTag(initViewHolder(convertView));
			}
			ViewHolder vh = (ViewHolder) convertView.getTag();
			TimeManagerInfo tmInfo = getItem(position);
			initItemView(vh, tmInfo);

			return convertView;
		}

		private ViewHolder initViewHolder(View view) {
			ViewHolder vh = new ViewHolder();
			vh.btnCheck = view.findViewById(R.id.btn_check);
			vh.ivCheck = (SwitchButton) view.findViewById(R.id.iv_check);
			vh.tvStart = (TextView) view.findViewById(R.id.tv_start);
			vh.tvEnd = (TextView) view.findViewById(R.id.tv_end);
			vh.tvName = (TextView) view.findViewById(R.id.tv_name);
			vh.layoutOpt = view.findViewById(R.id.layout_ed);
			vh.btnEdit = view.findViewById(R.id.btn_edit);
			vh.btnDel = view.findViewById(R.id.btn_del);
			vh.layoutItem = view.findViewById(R.id.layout_item);
			vh.layoutShow = view.findViewById(R.id.layout_show);
			vh.tvSee = (TextView) view.findViewById(R.id.tv_see);
			return vh;
		}

		private void initItemView(final ViewHolder vh, TimeManagerInfo tmInfo) {

			ItemClickListener icListener = new ItemClickListener(tmInfo);
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

			String start = StringUtils.dataToString24HM(tmInfo.getStartTime());
			String end = StringUtils.dataToString24HM(tmInfo.getEndTime());
			vh.tvStart.setText(start);
			vh.tvEnd.setText(" - " + end);

			int num = timeService.getAllLockAppByTimeManager(tmInfo).size();
			String format = getResources().getString(R.string.mgr_apps);
			String str = String.format(format, num);
			vh.tvName.setText(str);

			vh.tvSee.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

			if (tmInfo.getTimeIsOn()) {
				vh.ivCheck.setChecked(false);
				vh.layoutShow.setAlpha(ALPHA_ENABLE);
			} else {
				vh.ivCheck.setChecked(true);
				vh.layoutShow.setAlpha(ALPHA_DISABLE);
			}
			vh.ivCheck.setTag(tmInfo);
			vh.ivCheck.setOnCheckedChangeListener(mOnCheckedChangeListener);
			vh.layoutOpt.setVisibility(View.INVISIBLE);

		}

		class ViewHolder {

			SwitchButton ivCheck;
			View btnCheck;
			TextView tvStart;
			TextView tvEnd;
			TextView tvName;
			TextView tvSee;
			View layoutOpt;
			View btnEdit;
			View btnDel;
			View layoutItem;
			View layoutShow;

		}

		class ItemClickListener implements OnClickListener {

			TimeManagerInfo tmInfo;

			public ItemClickListener(TimeManagerInfo tmInfo) {
				super();
				this.tmInfo = tmInfo;
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

				if (tmInfo.getTimeIsOn()) {
					tmInfo.setTimeIsOn(false);
				} else {
					tmInfo.setTimeIsOn(true);
				}
				timeMgr.modifyManagerByTime(tmInfo);
				notifyDataSetChanged();
			}

			private void itemClick() {
				String name = StringUtils.dataToString24HM(tmInfo
						.getStartTime())
						+ "-"
						+ StringUtils.dataToString24HM(tmInfo.getEndTime());

				Intent intent = new Intent(mContext, ChooseAppsActivity.class);
				intent.putExtra(ChooseAppsActivity.APP_LIST_FLAG,
						ChooseAppsActivity.FLAG_TIME_LOCK);
				intent.putExtra(ChooseAppsActivity.EXT_TIME_ID, tmInfo.getId());
				intent.putExtra(ChooseAppsActivity.MODEL_NAME, name);
				startActivity(intent);
			}

			private void editClick() {
				Intent intent = new Intent(mContext, TimeLockEditActivity.class);
				intent.putExtra(TimeLockEditActivity.TIME_ID, tmInfo.getId());
				startActivity(intent);
			}

			private void deleteClick() {
				timeMgr.delateManagerByTimeId(tmInfo.getId());
				timeService.deleteAllLockAppByTimeManager(tmInfo);
				tmInfos.remove(tmInfo);
				notifyDataSetChanged();
			}

		}
	}

}
