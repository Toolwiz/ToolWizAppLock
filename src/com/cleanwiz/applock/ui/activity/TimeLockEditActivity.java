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
import java.util.Calendar;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelClickedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.data.CommLockInfo;
import com.cleanwiz.applock.data.TimeManagerInfo;
import com.cleanwiz.applock.service.TimeLockInfoService;
import com.cleanwiz.applock.service.TimeManagerInfoService;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.ui.widget.SwitchButton;
import com.cleanwiz.applock.utils.LogUtil;
import com.cleanwiz.applock.utils.StringUtils;
import com.cleanwiz.applock.utils.ToastUtils;

public class TimeLockEditActivity extends BaseActivity {

	public static final String TIME_ID = "lock_time_id";

	public static final String WEEK_CHECK_DEFAULT = "1111100";
	public static final String WEEK_CHECK_NONE = "0000000";

	private TimeLockEditActivity mContext;

	private WheelView wv_sh;
	private WheelView wv_sm;
	private WheelView wv_eh;
	private WheelView wv_em;
	private SwitchButton cb_repeat;
	private EditText et_name;
	private TextView tv_title;
	private TextView btn_done;
	private TextView tv_num;

	private List<TextView> weekBtns;
	private char[] weekStrs;

	private TimeManagerInfoService timeMgr;
	private TimeManagerInfo timeMgrInfo;
	private List<CommLockInfo> apps;

	private boolean isEdit = false;
	private int app_num = 0;
	
	private OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			SwitchButton switchButton = (SwitchButton)buttonView;
			if (switchButton != null) {
				if (!switchButton.isChecked()) {
					repeatClick();
					resetRepeat();
				} else {
					weekStrs = WEEK_CHECK_NONE.toCharArray();
					resetWeeks();
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timelock_edit);

		mContext = this;
		timeMgr = new TimeManagerInfoService(mContext);
		timeMgrInfo = new TimeManagerInfo();

		// for edit
		AppLockApplication.getInstance().setTmpLockInfos(null);
		Long timeId = (Long) getIntent().getSerializableExtra(TIME_ID);
		if (timeId != null) {
			timeMgrInfo = timeMgr.getTimeManagerInfoByTimeID(timeId);
			if (timeMgrInfo != null) {
				isEdit = true;
				apps = timeMgr.getAllTimeLockInfoByTimeManager(timeMgrInfo);
				AppLockApplication.getInstance().setTmpLockInfos(apps);
			}
		}

		et_name = (EditText) findViewById(R.id.et_lockname);
		tv_title = (TextView) findViewById(R.id.tv_title);
		btn_done = (TextView) findViewById(R.id.btn_done);
		tv_num = (TextView) findViewById(R.id.tv_app_num);

		if (isEdit) {
			tv_title.setText(R.string.time_lock_edit);

		} else {
			tv_title.setText(R.string.time_lock_add);
		}

		initTimeWheel();
		initWeekBtns();
	}

	@Override
	protected void onResume() {
		setAppNum();
		super.onResume();
	}

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

	private void initWeekBtns() {

		cb_repeat = (SwitchButton) findViewById(R.id.cb_repeat);
		cb_repeat.setOnCheckedChangeListener(mOnCheckedChangeListener);
		weekBtns = new ArrayList<TextView>();
		weekBtns.add((TextView) findViewById(R.id.week_check_1));
		weekBtns.add((TextView) findViewById(R.id.week_check_2));
		weekBtns.add((TextView) findViewById(R.id.week_check_3));
		weekBtns.add((TextView) findViewById(R.id.week_check_4));
		weekBtns.add((TextView) findViewById(R.id.week_check_5));
		weekBtns.add((TextView) findViewById(R.id.week_check_6));
		weekBtns.add((TextView) findViewById(R.id.week_check_7));

		if (isEdit) {
			weekStrs = timeMgrInfo.getRepeactDetail().toCharArray();
		} else {
			weekStrs = WEEK_CHECK_DEFAULT.toCharArray();
		}
		resetWeeks();
		resetRepeat();

	}

	private void resetWeeks() {
		int i = 0;
		for (TextView tv : weekBtns) {
			final int index = i++;
			char check = weekStrs[index];
			if (check == '1') {
				tv.setBackgroundResource(R.drawable.time_week_check);
			} else {
				tv.setBackgroundResource(R.drawable.time_week_bg);
			}
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (weekStrs[index] == '1') {
						weekStrs[index] = '0';
						v.setBackgroundResource(R.drawable.time_week_bg);
					} else {
						weekStrs[index] = '1';
						v.setBackgroundResource(R.drawable.time_week_check);
					}
					LogUtil.d("demo3", "weekStrs:" + new String(weekStrs));
					resetRepeat();
				}
			});
		}

	}

	private void resetRepeat() {
		boolean repeat = checkRepeat();
		if (repeat) {
			cb_repeat.setChecked(false);
		} else {
			cb_repeat.setChecked(true);
		}
	}

	private boolean checkRepeat() {

		for (char c : weekStrs) {
			if (c == '1') {
				return true;
			}
		}
		return false;

	}

	private void initTimeWheel() {
		wv_sh = (WheelView) findViewById(R.id.wv_start_h);
		wv_sm = (WheelView) findViewById(R.id.wv_start_m);
		wv_eh = (WheelView) findViewById(R.id.wv_end_h);
		wv_em = (WheelView) findViewById(R.id.wv_end_m);

		NumericWheelAdapter shAdapter = new NumericWheelAdapter(this, 0, 23,
				"%02d");
		shAdapter.setItemResource(R.layout.item_wheel_time);
		shAdapter.setItemTextResource(R.id.tv_text);
		NumericWheelAdapter smAdapter = new NumericWheelAdapter(this, 0, 59,
				"%02d");
		smAdapter.setItemResource(R.layout.item_wheel_time);
		smAdapter.setItemTextResource(R.id.tv_text);
		NumericWheelAdapter ehAdapter = new NumericWheelAdapter(this, 0, 23,
				"%02d");
		ehAdapter.setItemResource(R.layout.item_wheel_time);
		ehAdapter.setItemTextResource(R.id.tv_text);
		NumericWheelAdapter emAdapter = new NumericWheelAdapter(this, 0, 59,
				"%02d");
		emAdapter.setItemResource(R.layout.item_wheel_time);
		emAdapter.setItemTextResource(R.id.tv_text);

		wv_sh.setViewAdapter(shAdapter);
		wv_sh.setCyclic(true);
		wv_sm.setViewAdapter(smAdapter);
		wv_sm.setCyclic(true);
		wv_eh.setViewAdapter(ehAdapter);
		wv_eh.setCyclic(true);
		wv_em.setViewAdapter(emAdapter);
		wv_em.setCyclic(true);

		// add listeners
		addChangingListener(wv_sm, "min");
		addChangingListener(wv_sh, "hour");

		OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				LogUtil.d("demo3", "get:" + wheel.getCurrentItem());
			}
		};
		wv_sh.addChangingListener(wheelListener);
		wv_sm.addChangingListener(wheelListener);
		wv_eh.addChangingListener(wheelListener);
		wv_em.addChangingListener(wheelListener);

		OnWheelClickedListener click = new OnWheelClickedListener() {
			public void onItemClicked(WheelView wheel, int itemIndex) {
				wheel.setCurrentItem(itemIndex, true);
			}
		};
		wv_sh.addClickingListener(click);
		wv_sm.addClickingListener(click);
		wv_eh.addClickingListener(click);
		wv_em.addClickingListener(click);

		OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
			}

			public void onScrollingFinished(WheelView wheel) {
			}
		};

		wv_sh.addScrollingListener(scrollListener);
		wv_sm.addScrollingListener(scrollListener);
		wv_eh.addScrollingListener(scrollListener);
		wv_em.addScrollingListener(scrollListener);

		setDefaultTime();
	}

	private void setDefaultTime() {
		if (isEdit) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(timeMgrInfo.getStartTime());
			wv_sh.setCurrentItem(cal.get(Calendar.HOUR_OF_DAY));
			wv_sm.setCurrentItem(cal.get(Calendar.MINUTE));
			cal.setTimeInMillis(timeMgrInfo.getEndTime());
			wv_eh.setCurrentItem(cal.get(Calendar.HOUR_OF_DAY));
			wv_em.setCurrentItem(cal.get(Calendar.MINUTE));
		}
	}

	private void addChangingListener(final WheelView wheel, final String label) {
		wheel.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// wheel.setLabel(newValue != 1 ? label + "s" : label);
			}
		});
	}

	private void saveTimeLock() {

		if (timeMgr == null) {
			timeMgr = new TimeManagerInfoService(mContext);
		}

		Calendar start = Calendar.getInstance();
		start.set(Calendar.HOUR_OF_DAY, wv_sh.getCurrentItem());
		start.set(Calendar.MINUTE, wv_sm.getCurrentItem());
		Calendar end = Calendar.getInstance();
		end.set(Calendar.HOUR_OF_DAY, wv_eh.getCurrentItem());
		end.set(Calendar.MINUTE, wv_em.getCurrentItem());

		String name = et_name.getText() != null ? et_name.getText().toString()
				: "";
		timeMgrInfo.setStartTime(start.getTimeInMillis());
		timeMgrInfo.setEndTime(end.getTimeInMillis());
		timeMgrInfo.setIsRepeact(checkRepeat());
		timeMgrInfo.setTimeName(name);
		timeMgrInfo.setTimeIsOn(true);
		timeMgrInfo.setRepeactDetail(new String(weekStrs));

		TimeLockInfoService lockService = new TimeLockInfoService(mContext);

		if (isEdit) {
			timeMgr.modifyManagerByTime(timeMgrInfo);
			if (apps != null) {
				lockService.deleteAllLockAppByTimeManager(timeMgrInfo);
				for (CommLockInfo app : apps) {
					if (app.getIsLocked()) {
						lockService.lockAppByTimeManager(app.getPackageName(),
								timeMgrInfo);
					}
				}
			}
		} else {
			long id = timeMgr.inserManagerByTime(timeMgrInfo);
			if (id > 0 && apps != null) {
				timeMgrInfo.setId(id);
				lockService.deleteAllLockAppByTimeManager(timeMgrInfo);
				for (CommLockInfo app : apps) {
					if (app.getIsLocked()) {
						lockService.lockAppByTimeManager(app.getPackageName(),
								timeMgrInfo);
					}
				}
			}
		}

	}

	private void repeatClick() {
		if (checkRepeat()) {
			weekStrs = WEEK_CHECK_NONE.toCharArray();
		} else {
			weekStrs = WEEK_CHECK_DEFAULT.toCharArray();
		}
		resetWeeks();
	}

	@Override
	public void onClickEvent(View view) {
		switch (view.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_done:
			if (num != 0) {
				saveTimeLock();
				finish();
			} else {
				ToastUtils.showToast(R.string.lock_done_none);
			}
			break;
		case R.id.cb_repeat:
			repeatClick();
			break;
		case R.id.btn_enter_app:
			Calendar start = Calendar.getInstance();
			start.set(Calendar.HOUR_OF_DAY, wv_sh.getCurrentItem());
			start.set(Calendar.MINUTE, wv_sm.getCurrentItem());
			Calendar end = Calendar.getInstance();
			end.set(Calendar.HOUR_OF_DAY, wv_eh.getCurrentItem());
			end.set(Calendar.MINUTE, wv_em.getCurrentItem());
			String name = StringUtils.dataToString24HM(start.getTimeInMillis())
					+ "-" + StringUtils.dataToString24HM(end.getTimeInMillis());

			Intent intent = new Intent(mContext, ChooseAppsActivity.class);
			intent.putExtra(ChooseAppsActivity.APP_LIST_FLAG,
					ChooseAppsActivity.FLAG_TIME_LOCK);
			intent.putExtra(ChooseAppsActivity.MODEL_NAME, name);
			startActivity(intent);
			break;
		default:
			break;
		}
		super.onClickEvent(view);
	}
}
