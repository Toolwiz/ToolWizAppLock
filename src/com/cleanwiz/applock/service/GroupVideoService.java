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
import com.cleanwiz.applock.data.GroupVideo;
import com.cleanwiz.applock.data.GroupVideoDao.DaoMaster;
import com.cleanwiz.applock.data.GroupVideoDao.DaoMaster.DevOpenHelper;
import com.cleanwiz.applock.data.GroupVideoDao.DaoSession;
import com.cleanwiz.applock.data.GroupVideoDao.GroupVideoDao;
import com.cleanwiz.applock.data.GroupVideoDao.GroupVideoDao.Properties;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class GroupVideoService {

	private Context context;
	private DaoSession daoSession = null;
	private GroupVideoDao groupVideoDao = null;

	public GroupVideoService(Context context) {
		super();
		this.context = context;
		instanceGroupFileDataBase();
	}

	public void instanceGroupFileDataBase() {
		if (groupVideoDao == null) {
			DevOpenHelper helper = new DaoMaster.DevOpenHelper(context,
					MyConstants.getDatabasePath(context, "groupvideo"), null);
			SQLiteDatabase db = helper.getWritableDatabase();
			DaoMaster daoMaster = new DaoMaster(db);
			daoSession = daoMaster.newSession();
			groupVideoDao = daoSession.getGroupVideoDao();
		}
	}

	/**
	 * 增加一个分组
	 */
	public boolean addGroup(GroupVideo groupVideo) {
		if (groupVideoDao != null) {
			long id = groupVideoDao.insert(groupVideo);
			if (id >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 修改一个分组
	 */
	public boolean modifyGroup(GroupVideo groupVideo) {
		if (groupVideoDao != null) {
			long id = groupVideoDao.insertOrReplace(groupVideo);
			if (id >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 删除一个分组
	 */
	public boolean deleteGroup(GroupVideo groupVideo) {
		if (groupVideoDao != null) {
			groupVideoDao.delete(groupVideo);
			return true;
		}
		return false;
	}

	/**
	 * 获取所有分组
	 */
	public List<GroupVideo> getGroupFiles(int beyondGroupId) {
		List<GroupVideo> groupVideos = new ArrayList<GroupVideo>();
		if (groupVideoDao != null) {
			groupVideos = groupVideoDao.queryBuilder().where(Properties.ParentId.eq(beyondGroupId)).list();
		}
		return groupVideos;
	}

}
