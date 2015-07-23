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
import com.cleanwiz.applock.data.ParentModel;
import com.cleanwiz.applock.data.ParentModelDao.DaoMaster;
import com.cleanwiz.applock.data.ParentModelDao.DaoMaster.DevOpenHelper;
import com.cleanwiz.applock.data.ParentModelDao.DaoSession;
import com.cleanwiz.applock.data.ParentModelDao.ParentModelDao;
import com.cleanwiz.applock.data.ParentModelDao.ParentModelDao.Properties;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class ParentModelService {

	private Context context = null;
	private DaoSession daoSession = null;
	private ParentModelDao parentModelDao = null;
	
	public ParentModelService(Context context) {
		this.context = context;
	}
	
	public void instanceParentModelDao() {
		if (parentModelDao == null) {
			DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, "parentModel", null);
			SQLiteDatabase db = devOpenHelper.getWritableDatabase();
			DaoMaster daoMaster = new DaoMaster(db);
			daoSession = daoMaster.newSession();
			parentModelDao = daoSession.getParentModelDao();
		}
	}
	
	public boolean insertAppToParentModel(String packageName) {
		if (parentModelDao != null) {
			ParentModel parentModel = new ParentModel(null, packageName);
			parentModelDao.insert(parentModel);
			return true;
		}
		return false;
	}
	
	public boolean deleteAppFromParentModel(String packageName) {
		if (parentModelDao != null) {
			parentModelDao.queryBuilder().where(Properties.PackageName.eq(packageName)).buildDelete().executeDeleteWithoutDetachingEntities();
			return true;
		}
		return false;
	}
	
	public List<CommLockInfo> getAllParentModels() {
		List<CommLockInfo> allParentModels = new ArrayList<CommLockInfo>();
		if (parentModelDao != null) {
			List<ParentModel> parentModels = parentModelDao.loadAll();
			CommLockInfoService commLockInfoService = new CommLockInfoService(context);
			commLockInfoService.getCommLockInfoDaoInstance();
			List<CommLockInfo> allCommLockInfos = commLockInfoService.getAllCommLockInfos();
			for (CommLockInfo commLockInfo : allCommLockInfos) {
				boolean bIsLocked = false;
				for (ParentModel parentModel : parentModels) {
					if (parentModel.getPackageName().equals(commLockInfo.getPackageName())) {
						bIsLocked = true;
						break;
					}
				}
				commLockInfo.setIsLocked(bIsLocked);
			}
		}
		return allParentModels;
	}
	
	public boolean isLockedPackageName(String packageName) {
		if (parentModelDao != null) {
			List<ParentModel> parentModels = parentModelDao.queryBuilder().where(Properties.PackageName.eq(packageName)).list();
			if (parentModels.size() > 0) {
				return true;
			}
		}
		return false;
	}
	
}
