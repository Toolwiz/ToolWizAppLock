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
package com.cleanwiz.applock.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import u.aly.ca;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.data.CommLockInfo;
import com.cleanwiz.applock.data.TimeLockInfo;
import com.cleanwiz.applock.data.TimeManagerInfo;
import com.cleanwiz.applock.data.TimeManagerInfoDao.DaoMaster;
import com.cleanwiz.applock.data.TimeManagerInfoDao.DaoMaster.DevOpenHelper;
import com.cleanwiz.applock.data.TimeManagerInfoDao.DaoSession;
import com.cleanwiz.applock.data.TimeManagerInfoDao.TimeManagerInfoDao;
import com.cleanwiz.applock.data.TimeManagerInfoDao.TimeManagerInfoDao.Properties;
import com.cleanwiz.applock.utils.LogUtil;

import de.greenrobot.dao.query.DeleteQuery;

public class TimeManagerInfoService {

	private TimeManagerInfoDao timeManagerInfoDao = null;
	private DaoSession daoSession = null;
	private Context context = null;

	public TimeManagerInfoService(Context context) {
		this.context = context;
		instanceTimeManagerInfoDao(context);
	}

	public void instanceTimeManagerInfoDao(Context context) {
		if (timeManagerInfoDao == null) {
			DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context,
					"timemanagerinfo", null);
			SQLiteDatabase sqLiteDatabase = devOpenHelper.getWritableDatabase();
			DaoMaster daoMaster = new DaoMaster(sqLiteDatabase);
			daoSession = daoMaster.newSession();
			timeManagerInfoDao = daoSession.getTimeManagerInfoDao();
		}
	}

	// 获取所有时间锁
	public List<TimeManagerInfo> getAllTimeManagerInfos() {
		List<TimeManagerInfo> allInfos = new ArrayList<TimeManagerInfo>();
		if (timeManagerInfoDao != null) {
			allInfos = timeManagerInfoDao.loadAll();
		}
		return allInfos;
	}

	// 新增一个时间锁
	public long inserManagerByTime(TimeManagerInfo info) {
		if (timeManagerInfoDao != null) {
			return timeManagerInfoDao.insert(info);
		}
		return -1;
	}

	// 删除一个时间锁
	public boolean delateManagerByTimeId(long timeId) {
		if (timeManagerInfoDao != null) {
			DeleteQuery<TimeManagerInfo> deleteQuery = timeManagerInfoDao
					.queryBuilder().where(Properties.Id.eq(timeId))
					.buildDelete();
			deleteQuery.executeDeleteWithoutDetachingEntities();
			return true;
		}
		return false;
	}

	// 根据timeID获取单条时间锁
	public TimeManagerInfo getTimeManagerInfoByTimeID(long timeID) {
		if (timeManagerInfoDao != null) {
			List<TimeManagerInfo> infos = timeManagerInfoDao.queryBuilder()
					.where(Properties.Id.eq(timeID)).list();
			for (TimeManagerInfo timeManagerInfo : infos) {
				return timeManagerInfo;
			}
		}
		return null;
	}

	// 修改一个时间锁
	public boolean modifyManagerByTime(TimeManagerInfo info) {
		if (timeManagerInfoDao != null) {
			timeManagerInfoDao.insertOrReplace(info);
		}
		return false;
	}

	// 根据给定时间，获取所有的应用（包括锁定的和没锁定的)
	public List<CommLockInfo> getAllTimeLockInfoByTimeManager(
			TimeManagerInfo timeManagerInfo) {
		List<CommLockInfo> allCommLockInfos = new ArrayList<CommLockInfo>();
		List<TimeLockInfo> timeLockInfos = new ArrayList<TimeLockInfo>();

		CommLockInfoService commLockInfoService = new CommLockInfoService(
				context);
		commLockInfoService.getCommLockInfoDaoInstance();
		allCommLockInfos = commLockInfoService.getAllCommLockInfos();

		TimeLockInfoService timeLockInfoService = new TimeLockInfoService(
				context);
		timeLockInfos = timeLockInfoService
				.getAllLockAppByTimeManager(timeManagerInfo);

		for (CommLockInfo commLockInfo : allCommLockInfos) {
			boolean bIsLockInTime = false;
			for (TimeLockInfo timeLockInfo : timeLockInfos) {
				if (timeLockInfo.getPackageName().equals(
						commLockInfo.getPackageName())) {
					bIsLockInTime = true;
					break;
				}
			}
			commLockInfo.setIsLocked(bIsLockInTime);
		}
		Collections.sort(allCommLockInfos, AppLockApplication.commLockInfoComparator);
		return allCommLockInfos;
	}

	// 根据给定的时间（开始计算出星期几）、指定的包名，判断是否是锁定的应用
	@SuppressWarnings("deprecation")
	public boolean isLockTheAppByTimeManager(TimeManagerInfo timeManagerInfo,
			String packageName) {
		// 查找有无开始结束的时间段
		if (timeManagerInfoDao != null) {
			TimeLockInfoService timeLockInfoService = new TimeLockInfoService(
					context);
			List<TimeManagerInfo> managerInfos = new ArrayList<TimeManagerInfo>();
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(timeManagerInfo.getStartTime());
			managerInfos = timeManagerInfoDao.loadAll();
			for (TimeManagerInfo timeManagerInfoTmp : managerInfos) {
				// 是否开启
				if (!timeManagerInfoTmp.getTimeIsOn()) {
					LogUtil.e("colin", "时间锁："+timeManagerInfoTmp.getTimeName()+":没开");
					continue;
				}
				// 该时间锁是否重复
				Date endDate = new Date(timeManagerInfoTmp.getEndTime());
				endDate.setSeconds(59);
				timeManagerInfoTmp.setEndTime(endDate.getTime());
				if (timeManagerInfoTmp.getIsRepeact()) {
					Calendar startCalendar = Calendar.getInstance();
					Calendar endCalendar = Calendar.getInstance();
					startCalendar.setTimeInMillis(timeManagerInfoTmp.getStartTime());
					endCalendar.setTimeInMillis(timeManagerInfoTmp.getEndTime());
					if (calendar.after(startCalendar) && calendar.before(endCalendar)) {
						// 查找该时间锁下面的应用，有无指定的包名
						List<TimeLockInfo> timeLockInfos = timeLockInfoService
								.getAllLockAppByTimeManager(timeManagerInfoTmp);
						for (TimeLockInfo timeLockInfo : timeLockInfos) {
							if (timeLockInfo.getPackageName().equals(
									packageName)) {
								return true;
							}
						}
					} else {
						// 判定重复时间
						String repeactString = timeManagerInfoTmp
								.getRepeactDetail();
						if (repeactString.length() == 7) {
							boolean bIsTheDay = false;
							switch (calendar.get(Calendar.DAY_OF_WEEK)) {
							case 1: {
								// 星期天
								if (Integer.valueOf(repeactString.substring(6,
										7)) == 1) {
									bIsTheDay = true;
								}
								break;
							}
							case 2: {
								// 星期一
								if (Integer.valueOf(repeactString.substring(5,
										6)) == 1) {
									bIsTheDay = true;
								}
								break;
							}
							case 3: {
								// 星期二
								if (Integer.valueOf(repeactString.substring(4,
										5)) == 1) {
									bIsTheDay = true;
								}
								break;
							}
							case 4: {
								// 星期三
								if (Integer.valueOf(repeactString.substring(3,
										4)) == 1) {
									bIsTheDay = true;
								}
								break;
							}
							case 5: {
								// 星期四
								if (Integer.valueOf(repeactString.substring(2,
										3)) == 1) {
									bIsTheDay = true;
								}
								break;
							}
							case 6: {
								// 星期五
								if (Integer.valueOf(repeactString.substring(1,
										2)) == 1) {
									bIsTheDay = true;
								}
								break;
							}
							case 7: {
								// 星期六
								if (Integer.valueOf(repeactString.substring(0,
										1)) == 1) {
									bIsTheDay = true;
								}
								break;
							}
							default: {
								LogUtil.e("cleanwiz",
										"repeact string out off week!");
								break;
							}
							}
							if (bIsTheDay) {
								// 把时间的日期，秒调成统一进行比较
								startCalendar.setTimeInMillis(timeManagerInfoTmp.getStartTime());
								endCalendar.setTimeInMillis(timeManagerInfoTmp.getEndTime());
								calendar.set(Calendar.YEAR,2015);
								calendar.set(Calendar.MONTH,2);
								calendar.set(Calendar.DATE,4);
								calendar.set(Calendar.SECOND,0);
								startCalendar.set(Calendar.YEAR,2015);
								startCalendar.set(Calendar.MONTH,2);
								startCalendar.set(Calendar.DATE,4);
								startCalendar.set(Calendar.SECOND,0);
								endCalendar.set(Calendar.YEAR,2015);
								endCalendar.set(Calendar.MONTH,2);
								endCalendar.set(Calendar.DATE,4);
								endCalendar.set(Calendar.SECOND,59);
								LogUtil.e("colin", "getStartTime:"+timeManagerInfoTmp.getStartTime());
								LogUtil.e("colin", "getEndTime:"+timeManagerInfoTmp.getEndTime());
								LogUtil.e("colin", "calendar:"+calendar.getTimeInMillis());
								LogUtil.e("colin", "calstartCalendarendar:"+startCalendar.getTimeInMillis());
								LogUtil.e("colin", "endCalendar:"+endCalendar.getTimeInMillis());
								if (calendar.after(startCalendar) && calendar.before(endCalendar)) {
									// 查找该时间锁下面的应用，有无指定的包名
									List<TimeLockInfo> timeLockInfos = timeLockInfoService
											.getAllLockAppByTimeManager(timeManagerInfoTmp);
									for (TimeLockInfo timeLockInfo : timeLockInfos) {
										if (timeLockInfo.getPackageName()
												.equals(packageName)) {
											return true;
										}
									}
								}
							}

						} else {
							return false;
						}
					}
				} else {
					Calendar startCalendar = Calendar.getInstance();
					Calendar endCalendar = Calendar.getInstance();
					startCalendar.setTimeInMillis(timeManagerInfoTmp.getStartTime());
					endCalendar.setTimeInMillis(timeManagerInfoTmp.getEndTime());
					if (calendar.after(startCalendar) && calendar.before(endCalendar)) {
						// 查找该时间锁下面的应用，有无指定的包名
						List<TimeLockInfo> timeLockInfos = timeLockInfoService
								.getAllLockAppByTimeManager(timeManagerInfoTmp);
						for (TimeLockInfo timeLockInfo : timeLockInfos) {
							if (timeLockInfo.getPackageName().equals(
									packageName)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

}
