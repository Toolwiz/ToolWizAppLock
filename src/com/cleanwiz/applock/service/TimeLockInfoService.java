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
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cleanwiz.applock.data.TimeLockInfo;
import com.cleanwiz.applock.data.TimeManagerInfo;
import com.cleanwiz.applock.data.TimeLockInfoDao.DaoMaster;
import com.cleanwiz.applock.data.TimeLockInfoDao.DaoMaster.DevOpenHelper;
import com.cleanwiz.applock.data.TimeLockInfoDao.DaoSession;
import com.cleanwiz.applock.data.TimeLockInfoDao.TimeLockInfoDao;
import com.cleanwiz.applock.data.TimeLockInfoDao.TimeLockInfoDao.Properties;

import de.greenrobot.dao.query.DeleteQuery;

public class TimeLockInfoService {

	private TimeLockInfoDao timeLockInfoDao = null;
	private DaoSession daoSession = null;
	private Context context = null;
	
	public TimeLockInfoService(Context context) {
		this.context = context;
		instanceTimeLockInfoDao(context);
	}
	
	public void instanceTimeLockInfoDao(Context context) {
		if (timeLockInfoDao == null) {
			DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, "timelockinfo", null);
			SQLiteDatabase sqLiteDatabase = devOpenHelper.getWritableDatabase();
			DaoMaster daoMaster = new DaoMaster(sqLiteDatabase);
			daoSession = daoMaster.newSession();
			timeLockInfoDao = daoSession.getTimeLockInfoDao();
		}
	}
	
	//根据一个时间，锁定一个应用
	public boolean lockAppByTimeManager(String packageName,TimeManagerInfo timeManagerInfo) {
		if (timeLockInfoDao != null) {
			TimeLockInfo timeLockInfo = new TimeLockInfo(null, timeManagerInfo.getId().intValue(), packageName);
			timeLockInfoDao.insert(timeLockInfo);
			return true;
		}
		return false;
	}
	
	//根据一个时间，删除一个应用
	public boolean deleteLockAppByTimeManager(String packageName,TimeManagerInfo timeManagerInfo) {
		if (timeLockInfoDao != null) {
			DeleteQuery<TimeLockInfo> deleteQuery = timeLockInfoDao.queryBuilder().where(Properties.BeyondTimeManager.eq(timeManagerInfo.getId().intValue()),Properties.PackageName.eq(packageName)).buildDelete();
			deleteQuery.executeDeleteWithoutDetachingEntities();
			return true;
		}
		return false;
	}
	
	//根据一个时间锁，删除所有关于该时间锁的应用
	public boolean deleteAllLockAppByTimeManager(TimeManagerInfo timeManagerInfo) {
		if (timeLockInfoDao != null) {
			DeleteQuery<TimeLockInfo> deleteQuery = timeLockInfoDao.queryBuilder().where(Properties.BeyondTimeManager.eq(timeManagerInfo.getId().intValue())).buildDelete();
			deleteQuery.executeDeleteWithoutDetachingEntities();
			return true;
		}
		return false;
	}
	
	//根据一个时间锁，获取该时间锁下的所有应用
	public List<TimeLockInfo> getAllLockAppByTimeManager(TimeManagerInfo timeManagerInfo) {
		List<TimeLockInfo> timeLockInfos = new ArrayList<TimeLockInfo>();
		if (timeLockInfoDao != null) {
			timeLockInfos = timeLockInfoDao.queryBuilder().where(Properties.BeyondTimeManager.eq(timeManagerInfo.getId().intValue())).list();
		}
		return timeLockInfos;
	}
	
	//根据应用名称删除锁
	public boolean deleteLockAppByPackageName(String packageName) {
		if (timeLockInfoDao != null) {
			DeleteQuery<TimeLockInfo> deleteQuery = timeLockInfoDao.queryBuilder().where(Properties.PackageName.eq(packageName)).buildDelete();
			deleteQuery.executeDeleteWithoutDetachingEntities();
			return true;
		}
		return false;
	}
	
}
