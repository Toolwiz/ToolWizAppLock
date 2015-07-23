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

import com.cleanwiz.applock.data.BabyModel;
import com.cleanwiz.applock.data.CommLockInfo;
import com.cleanwiz.applock.data.BabyModelDao.BabyModelDao;
import com.cleanwiz.applock.data.BabyModelDao.BabyModelDao.Properties;
import com.cleanwiz.applock.data.BabyModelDao.DaoMaster;
import com.cleanwiz.applock.data.BabyModelDao.DaoMaster.DevOpenHelper;
import com.cleanwiz.applock.data.BabyModelDao.DaoSession;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class BabyModelService {

	private Context context = null;
	private DaoSession daoSession = null;
	private BabyModelDao babyModelDao = null;
	
	public BabyModelService(Context context) {
		this.context = context;
		instanceBabyModelDao();
	}
	
	public void instanceBabyModelDao() {
		if (babyModelDao == null) {
			DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, "babyModel", null);
			SQLiteDatabase db = devOpenHelper.getWritableDatabase();
			DaoMaster daoMaster = new DaoMaster(db);
			daoSession = daoMaster.newSession();
			babyModelDao = daoSession.getBabyModelDao();
		}
	}
	
	//把应用增加进儿童锁模式
	public boolean insertAppToBabyModel(String packageName) {
		if (babyModelDao != null) {
			BabyModel babyModel = new BabyModel(null, packageName);
			babyModelDao.insert(babyModel);
			return true;
		}
		return false;
	}
	
	//删除掉一个应用
	public boolean deleteAppFromBabyModel(String packageName) {
		if (babyModelDao != null) {
			babyModelDao.queryBuilder().where(Properties.PackageName.eq(packageName)).buildDelete().executeDeleteWithoutDetachingEntities();
			return true;
		}
		return false;
	}
	
	//获取所有带状态的儿童锁应用列表
	public List<CommLockInfo> getAllBabyModels() {
		List<CommLockInfo> allBabyModels = new ArrayList<CommLockInfo>();
		if (babyModelDao != null) {
			List<BabyModel> babyModels = babyModelDao.loadAll();
			CommLockInfoService commLockInfoService = new CommLockInfoService(context);
			commLockInfoService.getCommLockInfoDaoInstance();
			List<CommLockInfo> commLockInfos = commLockInfoService.getAllCommLockInfos();
			for (CommLockInfo commLockInfo : commLockInfos) {
				boolean bIsLocked = false;
				for (BabyModel babyModel : babyModels) {
					if (babyModel.getPackageName().equals(commLockInfo.getPackageName())) {
						bIsLocked = true;
						break;
					}
				}
				commLockInfo.setIsLocked(bIsLocked);
			}
		}
		return allBabyModels;
	}
	
	public boolean isLockedPackageName(String packageName) {
		if (babyModelDao != null) {
			List<BabyModel> babyModels = babyModelDao.queryBuilder().where(Properties.PackageName.eq(packageName)).list();
			if (babyModels.size() > 0) {
				return true;
			}
		}
		return false;
	}
	
}
