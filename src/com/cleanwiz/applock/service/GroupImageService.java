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
import com.cleanwiz.applock.data.GroupImage;
import com.cleanwiz.applock.data.GroupImageDao.DaoMaster;
import com.cleanwiz.applock.data.GroupImageDao.DaoMaster.DevOpenHelper;
import com.cleanwiz.applock.data.GroupImageDao.DaoSession;
import com.cleanwiz.applock.data.GroupImageDao.GroupImageDao;
import com.cleanwiz.applock.data.GroupImageDao.GroupImageDao.Properties;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class GroupImageService {

	private Context context;
	private DaoSession daoSession = null;
	private GroupImageDao groupImageDao = null;

	public GroupImageService(Context context) {
		super();
		this.context = context;
		instanceGroupImageDataBase();
	}

	public void instanceGroupImageDataBase() {
		if (groupImageDao == null) {
			DevOpenHelper helper = new DaoMaster.DevOpenHelper(context,
					MyConstants.getDatabasePath(context, "groupimage"), null);
			SQLiteDatabase db = helper.getWritableDatabase();
			DaoMaster daoMaster = new DaoMaster(db);
			daoSession = daoMaster.newSession();
			groupImageDao = daoSession.getGroupImageDao();
		}
	}

	/**
	 * 增加一个分组
	 */
	public long addGroup(GroupImage groupImage) {
		if (groupImageDao != null) {
			long id = groupImageDao.insert(groupImage);
			if (id >= 0) {
				return id;
			}
		}
		return -1;
	}

	/**
	 * 修改一个分组
	 */
	public boolean modifyGroup(GroupImage groupImage) {
		if (groupImageDao != null) {
			long id = groupImageDao.insertOrReplace(groupImage);
			if (id >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 删除一个分组
	 */
	public boolean deleteGroup(GroupImage groupImage) {
		if (groupImageDao != null) {
			groupImageDao.delete(groupImage);
			return true;
		}
		return false;
	}

	/**
	 * 获取所有分组
	 */
	public List<GroupImage> getGroupFiles(int beyondGroupId) {
		List<GroupImage> groupImages = new ArrayList<GroupImage>();
		if (groupImageDao != null) {
			groupImages = groupImageDao.queryBuilder().where(Properties.ParentId.eq(beyondGroupId)).list();
		}
		return groupImages;
	}

}
