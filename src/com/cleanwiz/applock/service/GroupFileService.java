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

import com.cleanwiz.applock.MyConstants;
import com.cleanwiz.applock.data.GroupFile;
import com.cleanwiz.applock.data.GroupFileDao.DaoMaster;
import com.cleanwiz.applock.data.GroupFileDao.DaoMaster.DevOpenHelper;
import com.cleanwiz.applock.data.GroupFileDao.DaoSession;
import com.cleanwiz.applock.data.GroupFileDao.GroupFileDao;
import com.cleanwiz.applock.data.GroupFileDao.GroupFileDao.Properties;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class GroupFileService {

	private Context context;
	private DaoSession daoSession = null;
	private GroupFileDao groupFileDao = null;

	public GroupFileService(Context context) {
		super();
		this.context = context;
		instanceGroupFileDataBase();
	}

	public void instanceGroupFileDataBase() {
		if (groupFileDao == null) {
			DevOpenHelper helper = new DaoMaster.DevOpenHelper(context,
					MyConstants.getDatabasePath(context, "groupfile"), null);
			SQLiteDatabase db = helper.getWritableDatabase();
			DaoMaster daoMaster = new DaoMaster(db);
			daoSession = daoMaster.newSession();
			groupFileDao = daoSession.getGroupFileDao();
		}
	}

	/**
	 * 增加一个分组
	 */
	public boolean addGroup(GroupFile groupFile) {
		if (groupFileDao != null) {
			long id = groupFileDao.insert(groupFile);
			if (id >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 修改一个分组
	 */
	public boolean modifyGroup(GroupFile groupFile) {
		if (groupFileDao != null) {
			long id = groupFileDao.insertOrReplace(groupFile);
			if (id >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 删除一个分组
	 */
	public boolean deleteGroup(GroupFile groupFile) {
		if (groupFileDao != null) {
			groupFileDao.delete(groupFile);
			return true;
		}
		return false;
	}

	/**
	 * 获取所有分组
	 */
	public List<GroupFile> getGroupFiles(int beyondGroupId) {
		List<GroupFile> groupFiles = new ArrayList<GroupFile>();
		if (groupFileDao != null) {
			groupFiles = groupFileDao.queryBuilder().where(Properties.ParentId.eq(beyondGroupId)).list();
		}
		return groupFiles;
	}

}
