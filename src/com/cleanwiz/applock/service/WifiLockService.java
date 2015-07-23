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

import com.cleanwiz.applock.data.CommLockInfo;
import com.cleanwiz.applock.data.WIFILockInfo;
import com.cleanwiz.applock.data.WIFILockManager;
import com.cleanwiz.applock.data.WIFILockInfoDao.DaoMaster;
import com.cleanwiz.applock.data.WIFILockInfoDao.DaoMaster.DevOpenHelper;
import com.cleanwiz.applock.data.WIFILockInfoDao.DaoSession;
import com.cleanwiz.applock.data.WIFILockInfoDao.WIFILockInfoDao;
import com.cleanwiz.applock.data.WIFILockInfoDao.WIFILockInfoDao.Properties;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class WifiLockService {

	private Context context = null;
	private WIFILockInfoDao wifiLockInfoDao = null;
	private DaoSession daoSession = null;

	public WifiLockService(Context context) {
		this.context = context;
		instanceWifiLockInfoDao();
	}

	public void instanceWifiLockInfoDao() {
		if (wifiLockInfoDao == null) {
			DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context,
					"wifiLockInfo", null);
			SQLiteDatabase db = devOpenHelper.getWritableDatabase();
			DaoMaster daoMaster = new DaoMaster(db);
			daoSession = daoMaster.newSession();
			wifiLockInfoDao = daoSession.getWIFILockInfoDao();
		}
	}

	// 解锁一个应用
	public boolean unLockWifiLockInfo(WIFILockInfo wifiLockInfo) {
		if (wifiLockInfoDao != null) {
			if (wifiLockInfo != null) {
				wifiLockInfoDao
						.queryBuilder()
						.where(Properties.PackageName.eq(wifiLockInfo
								.getPackageName()),
								Properties.BeyoundWifiManager.eq(wifiLockInfo
										.getBeyoundWifiManager()))
						.buildDelete().executeDeleteWithoutDetachingEntities();
				return true;
			}
		}
		return false;
	}

	// 锁住一个应用
	public boolean lockWifiLockInfo(WIFILockInfo wifiLockInfo) {
		if (wifiLockInfoDao != null && wifiLockInfo != null) {
			wifiLockInfoDao.insertOrReplace(wifiLockInfo);
			return true;
		}
		return false;
	}

	// 根据wifi锁，删除所有锁的应用列表
	public boolean deleteAllLockByWifiLockManager(
			WIFILockManager wifiLockManager) {
		if (wifiLockInfoDao != null && wifiLockManager != null) {
			wifiLockInfoDao
					.queryBuilder()
					.where(Properties.BeyoundWifiManager.eq(wifiLockManager
							.getId().intValue())).buildDelete()
					.executeDeleteWithoutDetachingEntities();
			return true;
		}
		return false;
	}

	// 获取所有锁与不锁的应用列表
	public List<CommLockInfo> getAllWifiLockInfo() {
		List<CommLockInfo> commLockInfos = new ArrayList<CommLockInfo>();
		if (wifiLockInfoDao != null) {
			CommLockInfoService commLockInfoService = new CommLockInfoService(
					context);
			commLockInfoService.getCommLockInfoDaoInstance();
			List<CommLockInfo> allCommLockInfos = commLockInfoService
					.getAllCommLockInfos();
			List<WIFILockInfo> wifiLockInfos = wifiLockInfoDao.loadAll();
			for (CommLockInfo commLockInfo : allCommLockInfos) {
				boolean bIsLocked = false;
				for (WIFILockInfo wifiLockInfo : wifiLockInfos) {
					if (commLockInfo.getPackageName().equals(
							wifiLockInfo.getPackageName())) {
						bIsLocked = true;
						break;
					}
				}
				commLockInfo.setIsLocked(bIsLocked);
			}
		}
		return commLockInfos;
	}

	// 根据WIFILockManager获取WIFILockInfo
	public List<WIFILockInfo> getLockInfosByMAnager(
			WIFILockManager wifiLockManager) {
		List<WIFILockInfo> wifiLockInfos = new ArrayList<WIFILockInfo>();
		if (wifiLockInfoDao != null) {
			wifiLockInfos = wifiLockInfoDao
					.queryBuilder()
					.where(Properties.BeyoundWifiManager.eq(String.valueOf(wifiLockManager.getId().intValue())))
					.list();
		}
		return wifiLockInfos;
	}

}
