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

import java.util.List;

import com.cleanwiz.applock.data.FaviterApps;
import com.cleanwiz.applock.data.FaviterAppsDao.DaoMaster;
import com.cleanwiz.applock.data.FaviterAppsDao.DaoMaster.DevOpenHelper;
import com.cleanwiz.applock.data.FaviterAppsDao.DaoSession;
import com.cleanwiz.applock.data.FaviterAppsDao.FaviterAppsDao;
import com.cleanwiz.applock.data.FaviterAppsDao.FaviterAppsDao.Properties;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class FaviterAppsService {

	private Context context = null;
	private DaoSession daoSession = null;
	private FaviterAppsDao faviterAppsDao = null;
	
	public FaviterAppsService(Context context) {
		this.context = context;
		instaceFaviterAppsDao();
	}
	
	public void instaceFaviterAppsDao() {
		if (faviterAppsDao == null) {
			DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, "faviterApps", null);
			SQLiteDatabase db = devOpenHelper.getWritableDatabase();
			DaoMaster daoMaster = new DaoMaster(db);
			daoSession = daoMaster.newSession();
			faviterAppsDao = daoSession.getFaviterAppsDao();
		}
	}
	
	public long addNewFaviterApp(String packageName) {
		if (faviterAppsDao != null) {
			return faviterAppsDao.insert(new FaviterApps(null, packageName));
		}
		return -1;
	}
	
	public boolean isFaviterApp(String packageName) {
		if (faviterAppsDao != null) {
			List<FaviterApps> faviterApps = faviterAppsDao.queryBuilder().where(Properties.PackageName.eq(packageName)).list();
			if (faviterApps.size() > 0) {
				return true;
			}
		}
		return false;
	}
	
}
