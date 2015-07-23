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

import u.aly.be;

import com.cleanwiz.applock.MyConstants;
import com.cleanwiz.applock.data.GroupAudio;
import com.cleanwiz.applock.data.GroupImage;
import com.cleanwiz.applock.data.GroupAudioDao.DaoMaster;
import com.cleanwiz.applock.data.GroupAudioDao.DaoMaster.DevOpenHelper;
import com.cleanwiz.applock.data.GroupAudioDao.DaoSession;
import com.cleanwiz.applock.data.GroupAudioDao.GroupAudioDao;
import com.cleanwiz.applock.data.GroupAudioDao.GroupAudioDao.Properties;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class GroupAudioService {

	private Context context;
	private DaoSession daoSession = null;
	private GroupAudioDao groupAudioDao = null;

	public GroupAudioService(Context context) {
		super();
		this.context = context;
		instanceGroupAudioDataBase();
	}

	public void instanceGroupAudioDataBase() {
		if (groupAudioDao == null) {
			DevOpenHelper helper = new DaoMaster.DevOpenHelper(context,
					MyConstants.getDatabasePath(context, "groupaudio"), null);
			SQLiteDatabase db = helper.getWritableDatabase();
			DaoMaster daoMaster = new DaoMaster(db);
			daoSession = daoMaster.newSession();
			groupAudioDao = daoSession.getGroupAudioDao();
		}
	}

	/**
	 * 增加一个分组
	 */
	public boolean addGroup(GroupAudio groupAudio) {
		if (groupAudioDao != null) {
			long id = groupAudioDao.insert(groupAudio);
			if (id >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 修改一个分组
	 */
	public boolean modifyGroup(GroupAudio groupAudio) {
		if (groupAudioDao != null) {
			long id = groupAudioDao.insertOrReplace(groupAudio);
			if (id >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 删除一个分组
	 */
	public boolean deleteGroup(GroupAudio groupAudio) {
		if (groupAudioDao != null) {
			groupAudioDao.delete(groupAudio);
			return true;
		}
		return false;
	}

	/**
	 * 获取所有分组
	 */
	public List<GroupAudio> getGroupFiles(int beyondGroupId) {
		List<GroupAudio> groupAudios = new ArrayList<GroupAudio>();
		if (groupAudioDao != null) {
			groupAudios = groupAudioDao.queryBuilder().where(Properties.ParentId.eq(beyondGroupId)).list();
		}
		return groupAudios;
	}

}
