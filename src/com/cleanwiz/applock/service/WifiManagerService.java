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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.data.CommLockInfo;
import com.cleanwiz.applock.data.WIFILockInfo;
import com.cleanwiz.applock.data.WIFILockManager;
import com.cleanwiz.applock.data.WIFILockManagerDao.DaoMaster;
import com.cleanwiz.applock.data.WIFILockManagerDao.DaoMaster.DevOpenHelper;
import com.cleanwiz.applock.data.WIFILockManagerDao.DaoSession;
import com.cleanwiz.applock.data.WIFILockManagerDao.WIFILockManagerDao;
import com.cleanwiz.applock.data.WIFILockManagerDao.WIFILockManagerDao.Properties;
import com.cleanwiz.applock.utils.LogUtil;

import de.greenrobot.dao.query.DeleteQuery;

public class WifiManagerService {

	private WIFILockManagerDao wifiLockManagerDao = null;
	private Context context = null;
	private DaoSession daoSession = null;

	public WifiManagerService(Context context) {
		this.context = context;
		instanceWifiLockManagerDao(context);
	}

	public void instanceWifiLockManagerDao(Context context) {
		if (wifiLockManagerDao == null) {
			DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context,
					"wifiLockManager", null);
			SQLiteDatabase db = devOpenHelper.getWritableDatabase();
			DaoMaster daoMaster = new DaoMaster(db);
			daoSession = daoMaster.newSession();
			wifiLockManagerDao = daoSession.getWIFILockManagerDao();
		}
	}

	// 获取所有wifi锁
	public List<WIFILockManager> getallWifiLockManaer() {
		List<WIFILockManager> wifiLockManagers = new ArrayList<WIFILockManager>();
		if (wifiLockManagerDao != null) {
			wifiLockManagers = wifiLockManagerDao.loadAll();
		}
		return wifiLockManagers;
	}

	// 新增一个wifi锁
	public long insertNewWifiLockManager(WIFILockManager wifiLockManager) {
		if (wifiLockManagerDao != null) {
			return wifiLockManagerDao.insert(wifiLockManager);
		}
		return -1;
	}

	// 删除一个wifi锁
	public boolean deleteWifiLockManager(WIFILockManager wifiLockManager) {
		if (wifiLockManagerDao != null) {
			DeleteQuery<WIFILockManager> deleteQuery = wifiLockManagerDao
					.queryBuilder()
					.where(Properties.Id.eq(wifiLockManager.getId().intValue()))
					.buildDelete();
			deleteQuery.executeDeleteWithoutDetachingEntities();
			return true;
		}
		return false;
	}

	// 开关一个wifi锁
	public boolean switchWifiLockManager(WIFILockManager wifiLockManager) {
		if (wifiLockManagerDao != null) {
			wifiLockManagerDao.insertOrReplace(wifiLockManager);
		}
		return false;
	}

	// 根据SSID、应用名称，判断是否需要锁住
	public boolean isLockedWifiByPackageNameAndSSID(String sSID,
			String packageName) {
		if (wifiLockManagerDao != null) {
			List<WIFILockManager> wifiLockManagers = wifiLockManagerDao
					.queryBuilder().where(Properties.SsidName.eq(sSID)).list();
			WifiLockService wifiLockService = new WifiLockService(context);
			for (WIFILockManager wifiLockManager : wifiLockManagers) {
				List<WIFILockInfo> wifiLockInfos = wifiLockService
						.getLockInfosByMAnager(wifiLockManager);
				for (WIFILockInfo wifiLockInfo : wifiLockInfos) {
					if (wifiLockInfo.getPackageName().equals(packageName) && wifiLockManager.getIsOn()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// 获取所有已连接过的wifi
	public List<String> getallConnectedWifiSSID() {
		List<String> allSSID = new ArrayList<String>();
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		int wifiState = wifiManager.getWifiState();
		if (wifiState == WifiManager.WIFI_STATE_DISABLED || wifiState == WifiManager.WIFI_STATE_DISABLING || wifiState == WifiManager.WIFI_STATE_UNKNOWN) {
			wifiManager.setWifiEnabled(true);
			List<WifiConfiguration> configurations = wifiManager
					.getConfiguredNetworks();
			if (configurations != null && configurations.size() > 0) {
				for (WifiConfiguration wifiConfiguration : configurations) {
					String ssidString = wifiConfiguration.SSID.replace("\"", "");
					allSSID.add(ssidString);
					wifiManager.setWifiEnabled(false);
				}	
			}
		} else {
			List<WifiConfiguration> configurations = wifiManager
					.getConfiguredNetworks();
			if (configurations != null && configurations.size() > 0) {
				for (WifiConfiguration wifiConfiguration : configurations) {
					String ssidString = wifiConfiguration.SSID.replace("\"", "");
					allSSID.add(ssidString);
				}
			}
		}
		return allSSID;
	}

	//根据ID获取Manager
	public WIFILockManager getWifiLockManagerByID(long wifiManaferId) {
		if (wifiLockManagerDao != null) {
			List<WIFILockManager> inList = wifiLockManagerDao.queryBuilder().where(Properties.Id.eq(wifiManaferId)).list();
			for (WIFILockManager wifiLockManager : inList) {
				return wifiLockManager;
			}
		}
		return null;
	}
	
	//根据ID获取所有该wifi锁所有锁与不锁的列表
	//获取所有锁与不锁的应用列表
	public List<CommLockInfo> getAllWifiLockInfo(long wifiManagerId) {
		List<CommLockInfo> allCommLockInfos = new ArrayList<CommLockInfo>();
		if (wifiLockManagerDao != null) {
			CommLockInfoService commLockInfoService = new CommLockInfoService(context);
			commLockInfoService.getCommLockInfoDaoInstance();
			allCommLockInfos = commLockInfoService.getAllCommLockInfos();
			WifiLockService wifiLockService = new WifiLockService(context);
			List<WIFILockInfo> wifiLockInfos = wifiLockService.getLockInfosByMAnager(new WIFILockManager(wifiManagerId,"","",false));
			for (CommLockInfo commLockInfo : allCommLockInfos) {
				boolean bIsLocked = false;
				for (WIFILockInfo wifiLockInfo : wifiLockInfos) {
					if (commLockInfo.getPackageName().equals(wifiLockInfo.getPackageName())) {
						bIsLocked = true;
						break;
					}
				}
				commLockInfo.setIsLocked(bIsLocked);
			}
		}
		Collections.sort(allCommLockInfos, AppLockApplication.commLockInfoComparator);
		return allCommLockInfos;
	}
	
	//判断wifi是否可用
	public boolean wifiIsEnable() {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager != null && wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			return true;
		}
		return false;
	}
	
}
