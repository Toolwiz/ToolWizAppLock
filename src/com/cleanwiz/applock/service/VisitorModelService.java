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

import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.data.CommLockInfo;
import com.cleanwiz.applock.data.VisitorModel;
import com.cleanwiz.applock.data.VisitorModelDao.DaoMaster;
import com.cleanwiz.applock.data.VisitorModelDao.DaoMaster.DevOpenHelper;
import com.cleanwiz.applock.data.VisitorModelDao.DaoSession;
import com.cleanwiz.applock.data.VisitorModelDao.VisitorModelDao;
import com.cleanwiz.applock.data.VisitorModelDao.VisitorModelDao.Properties;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class VisitorModelService {

	private Context context = null;
	private DaoSession daoSession = null;
	private VisitorModelDao visitorModelDao = null;
	
	public VisitorModelService(Context context) {
		this.context = context;
		instanceVisitorModelDao();
	}
	
	public void instanceVisitorModelDao() {
		DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, "visitorModel", null);
		SQLiteDatabase db = devOpenHelper.getWritableDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		visitorModelDao = daoSession.getVisitorModelDao();
	}
	
	//把应用新增到访客锁
	public boolean insertAppToVisitoer(String packageName) {
		if (visitorModelDao != null) {
			VisitorModel visitorModel = new VisitorModel(null, packageName);
			visitorModelDao.insert(visitorModel);
			return true;
		}
		return false;
	}
	
	//在访客锁中删掉一个应用
	public boolean deleteAppFromVisitor(String packageName) {
		if (visitorModelDao != null) {
			visitorModelDao.queryBuilder().where(Properties.PackageName.eq(packageName)).buildDelete().executeDeleteWithoutDetachingEntities();
			return true;
		}
		return false;
	}
	
	//获取所有应用包括锁定状态
	public List<CommLockInfo> getAllVisitor() {
		List<CommLockInfo> commLockInfos = new ArrayList<CommLockInfo>();
		if (visitorModelDao != null) {
			List<VisitorModel> visitorModels = visitorModelDao.loadAll();
			CommLockInfoService commLockInfoService = new CommLockInfoService(context);
			commLockInfoService.getCommLockInfoDaoInstance();
			commLockInfos = commLockInfoService.getAllCommLockInfos();
			for (CommLockInfo commLockInfo : commLockInfos) {
				boolean bIsLocked = false;
				for (VisitorModel visitorModel : visitorModels) {
					if (visitorModel.getPackageName().equals(commLockInfo.getPackageName())) {
						bIsLocked = true;
						break;
					}
				}
				commLockInfo.setIsLocked(bIsLocked);
			}
		}
		Collections.sort(commLockInfos, AppLockApplication.commLockInfoComparator);
		return commLockInfos;
	}
	
	public boolean isLockedPackageName(String packageName) {
		if (visitorModelDao != null) {
			List<VisitorModel> visitorModels = visitorModelDao.queryBuilder().where(Properties.PackageName.eq(packageName)).list();
			if (visitorModels.size() > 0) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasLockedPackage() {
		if (visitorModelDao != null) {
			List<VisitorModel> visitorModels = visitorModelDao.loadAll();
			if (visitorModels.size() > 0) {
				return true;
			}
		}
		return false;
	}
	
}
