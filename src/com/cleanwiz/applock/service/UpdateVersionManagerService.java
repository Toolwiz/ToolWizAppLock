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

import com.cleanwiz.applock.data.UpdateVersionManafer;
import com.cleanwiz.applock.data.UpdateVersionManagerDao.DaoMaster;
import com.cleanwiz.applock.data.UpdateVersionManagerDao.DaoMaster.DevOpenHelper;
import com.cleanwiz.applock.data.UpdateVersionManagerDao.DaoSession;
import com.cleanwiz.applock.data.UpdateVersionManagerDao.UpdateVersionManaferDao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class UpdateVersionManagerService {

	private Context context = null;
	private DaoSession daoSession = null;
	private UpdateVersionManaferDao updateVersionManaferDao = null;
	
	public UpdateVersionManagerService(Context context) {
		this.context = context;
		instanceUpdateVersionManagerDao();
	}
	
	public void instanceUpdateVersionManagerDao() {
		if (updateVersionManaferDao == null) {
			DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, "updateversionmanager", null);
			SQLiteDatabase db = devOpenHelper.getWritableDatabase();
			DaoMaster daoMaster = new DaoMaster(db);
			daoSession = daoMaster.newSession();
			updateVersionManaferDao = daoSession.getUpdateVersionManaferDao();
		}
	}
	
	public List<UpdateVersionManafer> getVersionManafers() {
		List<UpdateVersionManafer> updateVersionManafers = new ArrayList<UpdateVersionManafer>();
		if (updateVersionManaferDao != null) {
			updateVersionManafers = updateVersionManaferDao.loadAll();
		}
		return updateVersionManafers;
	}
	
	public long modifyTipsDate(UpdateVersionManafer updateVersionManafer) {
		if (updateVersionManaferDao != null && updateVersionManafer != null) {
			return updateVersionManaferDao.insertOrReplace(updateVersionManafer);
		}
		return -1;
	}
	
	public long addNewVersion(UpdateVersionManafer updateVersionManafer) {
		if (updateVersionManaferDao != null && updateVersionManafer != null) {
			updateVersionManaferDao.deleteAll();
			return updateVersionManaferDao.insertOrReplace(updateVersionManafer);
		}
		return -1;
	}
	
}
