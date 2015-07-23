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
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;

import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.data.CommLockInfo;
import com.cleanwiz.applock.data.CommLockInfoDao.CommLockInfoDao;
import com.cleanwiz.applock.data.CommLockInfoDao.CommLockInfoDao.Properties;
import com.cleanwiz.applock.data.CommLockInfoDao.DaoMaster;
import com.cleanwiz.applock.data.CommLockInfoDao.DaoMaster.DevOpenHelper;
import com.cleanwiz.applock.data.CommLockInfoDao.DaoSession;

import de.greenrobot.dao.query.DeleteQuery;

public class CommLockInfoService {

	private CommLockInfoDao commLockInfoDao = null;
	private DaoSession daoSession = null;
	private Context context = null;
	private FaviterAppsService faviterAppsService = null;

	public CommLockInfoService(Context context) {
		this.context = context;
		faviterAppsService = new FaviterAppsService(context);
	}

	public CommLockInfoDao getCommLockInfoDaoInstance() {
		if (commLockInfoDao == null) {
			DevOpenHelper helper = new DaoMaster.DevOpenHelper(context,
					"commlockinfo", null);
			SQLiteDatabase db = helper.getWritableDatabase();
			DaoMaster daoMaster = new DaoMaster(db);
			daoSession = daoMaster.newSession();
			commLockInfoDao = daoSession.getCommLockInfoDao();
		}
		return commLockInfoDao;
	}

	public void instanceCommLockInfoTable(List<ResolveInfo> mAllAppInfos) {
		for (int i = 0; i < mAllAppInfos.size(); i++) {
			ResolveInfo resolveInfo = mAllAppInfos.get(i);
			boolean isfaviterApp = faviterAppsService
					.isFaviterApp(resolveInfo.activityInfo.packageName);
			CommLockInfo commLockInfo = new CommLockInfo(null,
					resolveInfo.activityInfo.packageName, false, isfaviterApp); // 后续需添加默认的开启保护
			if (commLockInfoDao != null) {
				if (!commLockInfo.getPackageName().equals(
						"com.cleanwiz.applock")
						&& !commLockInfo.getPackageName().equals(
								"com.qihoo360.mobilesafe.opti.powerctl")
						&& !commLockInfo.getPackageName().equals(
								"com.android.settings")
						&& !isExistPackage(commLockInfo.getPackageName())) {
					if (isfaviterApp) {
						commLockInfo.setIsLocked(true);
					} else {
						commLockInfo.setIsLocked(false);
					}
					commLockInfoDao.insert(commLockInfo);
				}
			}
		}
	}

	public boolean isExistPackage(String packageName) {
		if (commLockInfoDao != null) {
			List<CommLockInfo> commLockInfos = commLockInfoDao.loadAll();
			for (CommLockInfo commLockInfo : commLockInfos) {
				if (commLockInfo.getPackageName().equals(packageName)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean lockCommApplication(String packageName) {
		if (commLockInfoDao != null) {
			List<CommLockInfo> commLockInfos = commLockInfoDao.loadAll();
			for (CommLockInfo commLockInfo : commLockInfos) {
				if (commLockInfo.getPackageName().equals(packageName)) {
					commLockInfo.setIsLocked(true);
					commLockInfoDao.insertOrReplace(commLockInfo);
					return true;
				}
			}
		}
		return false;
	}

	public boolean deleteCommApplicationByPackageName(String packageName) {
		if (commLockInfoDao != null) {
			DeleteQuery<CommLockInfo> comDeleteQuery = commLockInfoDao
					.queryBuilder()
					.where(Properties.PackageName.eq(packageName))
					.buildDelete();
			comDeleteQuery.executeDeleteWithoutDetachingEntities();
			return true;
		}
		return false;
	}

	public boolean insertNewCommLockApplicationByPackageName(String packageName) {
		if (commLockInfoDao != null) {
			List<CommLockInfo> commLockInfos = commLockInfoDao.queryBuilder()
					.where(Properties.PackageName.eq(packageName)).list();
			if (commLockInfos.size() == 0) {
				boolean isFaviterApp = faviterAppsService
						.isFaviterApp(packageName);
				CommLockInfo commLockInfo = new CommLockInfo(null, packageName,
						false, isFaviterApp);
				commLockInfoDao.insert(commLockInfo);
				return true;
			} else {
				// 已存在需要修改数据
				return true;
			}
		}
		return false;
	}

	public boolean unlockCommApplication(String packageName) {
		if (commLockInfoDao != null) {
			List<CommLockInfo> commLockInfos = commLockInfoDao.loadAll();
			for (CommLockInfo commLockInfo : commLockInfos) {
				if (commLockInfo.getPackageName().equals(packageName)) {
					commLockInfo.setIsLocked(false);
					commLockInfoDao.insertOrReplace(commLockInfo);
					return true;
				}
			}
		}
		return false;
	}

	public List<CommLockInfo> getCommLockedInfos() {
		if (commLockInfoDao != null) {
			List<CommLockInfo> commLockInfos = new ArrayList<CommLockInfo>();
			commLockInfos = commLockInfoDao.queryBuilder()
					.where(Properties.IsLocked.eq(true)).list();
			return commLockInfos;
		}
		return null;
	}

	public List<CommLockInfo> getAllCommLockInfos() {
		List<CommLockInfo> commLockInfos = new ArrayList<CommLockInfo>();
		if (commLockInfoDao != null) {
			commLockInfos = commLockInfoDao.loadAll();
		}
		Collections.sort(commLockInfos,
				AppLockApplication.commLockInfoComparator);
		return commLockInfos;
	}

	public boolean isLockedPackageName(String packageName) {
		if (commLockInfoDao != null) {
			List<CommLockInfo> commLockInfos = commLockInfoDao.queryBuilder()
					.where(Properties.PackageName.eq(packageName)).list();
			for (CommLockInfo commLockInfo : commLockInfos) {
				if (commLockInfo.getIsLocked()) {
					return true;
				}
			}
		}
		return false;
	}
}
