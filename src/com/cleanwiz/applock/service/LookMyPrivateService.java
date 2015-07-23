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
import android.os.Handler;
import android.os.Message;

import com.cleanwiz.applock.data.LookMyPrivate;
import com.cleanwiz.applock.data.LookMyPrivateDao.DaoMaster;
import com.cleanwiz.applock.data.LookMyPrivateDao.DaoMaster.DevOpenHelper;
import com.cleanwiz.applock.data.LookMyPrivateDao.DaoSession;
import com.cleanwiz.applock.data.LookMyPrivateDao.LookMyPrivateDao;
import com.cleanwiz.applock.utils.LogUtil;

public class LookMyPrivateService {

	private Context context = null;
	private DaoSession daoSession = null;
	private LookMyPrivateDao lookMyPrivateDao = null;
	
	public static int LOOKMYPRIVATE_PICOK = 1;
	public String picPath;
	public LookMyPrivate lookMyPrivate = null;

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1: {
				if (lookMyPrivate != null) {
					lookMyPrivate.setPicPath(picPath);
					LogUtil.e("colin", "插入一条新数据:"+lookMyPrivate.getResolver());
					replaceLookMyPrivate(lookMyPrivate);
				}
				break;
			}
			default:
				break;
			}
		};
	};
	
	public LookMyPrivateService(Context context) {
		this.context = context;
		instanceLookMyPrivateDao();
	}
	
	public void instanceLookMyPrivateDao() {
		if (lookMyPrivateDao == null) {
			DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, "lookmyprivate", null);
			SQLiteDatabase db = devOpenHelper.getWritableDatabase();
			DaoMaster daoMaster = new DaoMaster(db);
			daoSession = daoMaster.newSession();
			lookMyPrivateDao = daoSession.getLookMyPrivateDao();
		}
	}
	
	public long addNewLookMyPrivate(LookMyPrivate lookMyPrivate) {
		if (lookMyPrivateDao != null && lookMyPrivate != null) {
			return lookMyPrivateDao.insert(lookMyPrivate);
		}
		return -1;
	}
	
	public boolean replaceLookMyPrivate(LookMyPrivate lookMyPrivate) {
		if (lookMyPrivateDao != null) {
			lookMyPrivateDao.insertOrReplace(lookMyPrivate);
			return true;
		}
		return false;
	}
	
	public boolean setLookMyPrivateWasReaded(LookMyPrivate lookMyPrivate) {
		if (lookMyPrivateDao != null && lookMyPrivate != null) {
			lookMyPrivate.setIsReaded(true);
			lookMyPrivateDao.insertOrReplace(lookMyPrivate);
			return true;
		}
		return false;
	}
	
	public List<LookMyPrivate> getAllLookMyPrivates() {
		List<LookMyPrivate> lookMyPrivates = new ArrayList<LookMyPrivate>();
		if (lookMyPrivateDao != null) {
			lookMyPrivates = lookMyPrivateDao.loadAll();
		}
		return lookMyPrivates;
	}
	
	public void clearLookMyPrivate() {
		if (lookMyPrivateDao != null) {
			lookMyPrivateDao.deleteAll();
		}
	}
	
}
