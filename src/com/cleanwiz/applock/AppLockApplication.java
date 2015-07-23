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
package com.cleanwiz.applock;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.cleanwiz.applock.data.CommLockInfo;
import com.cleanwiz.applock.data.UpdateVersionManafer;
import com.cleanwiz.applock.service.CommLockInfoService;
import com.cleanwiz.applock.service.FaviterAppsService;
import com.cleanwiz.applock.service.LockService;
import com.cleanwiz.applock.service.UpdateVersionManagerService;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.ui.activity.GestureUnlockActivity;
import com.cleanwiz.applock.ui.activity.NumberUnlockActivity;
import com.cleanwiz.applock.ui.activity.SecretConfig;
import com.cleanwiz.applock.ui.activity.UserUnlockActivity;
import com.cleanwiz.applock.ui.widget.LockPatternUtils;
import com.cleanwiz.applock.utils.*;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.ThumbnailImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.*;
import java.util.*;

public class AppLockApplication extends Application {

	/**
	 * 全局日志开关
	 */
	public static final boolean LOG_FLAG = false;

	private static AppLockApplication mContext;
	private LockPatternUtils mLockPatternUtils;

	public static SharedPreferences sharedPreferences;
	public static boolean appIsInstance = false;

	public boolean wifiIsConnected = false;
	public boolean appLockState = true;
	public boolean allowedLeaveAment = false;
	public String secretQuestionString = "";
	public String secretAnswerString = "";
	public boolean hasNewApp = false;
	public boolean appIconIsHided = false;
	public long lastUnlockTimeSeconds = 0;
	public boolean lastUserEnterCorrentPwd = false;
	public long lastUserEnterPwdLeaverDateMiliseconds = 0;
	public int lastUserEnterPwdErrorCount = 0;
	public int lastUserEnterPwdDelayTime = 0;
	public boolean lastAppEnterCorrentPwd = false;
	public long lastAppEnterPwdLeaverDateMiliseconds = 0;
	public int lastAppEnterPwdErrorCount = 0;
	public int lastAppEnterPwdDelayTime = 0;

	public static Comparator commLockInfoComparator = new Comparator() {

		@Override
		public int compare(Object lhs, Object rhs) {
			// TODO Auto-generated method stub
			CommLockInfo leftCommLockInfo = (CommLockInfo) lhs;
			CommLockInfo rightCommLockInfo = (CommLockInfo) rhs;
			if (leftCommLockInfo.getIsFaviterApp()
					&& !leftCommLockInfo.getIsLocked()
					&& !rightCommLockInfo.getIsFaviterApp()
					&& !rightCommLockInfo.getIsLocked()) {
				return -1;
			} else if (leftCommLockInfo.getIsFaviterApp()
					&& leftCommLockInfo.getIsLocked()
					&& !rightCommLockInfo.getIsFaviterApp()
					&& !rightCommLockInfo.getIsLocked()) {
				return -1;
			} else if (leftCommLockInfo.getIsFaviterApp()
					&& leftCommLockInfo.getIsLocked()
					&& rightCommLockInfo.getIsFaviterApp()
					&& !rightCommLockInfo.getIsLocked()) {
				if (leftCommLockInfo.getAppInfo() != null
						&& rightCommLockInfo.getAppInfo() != null)
					return Cn2Spell
							.converterToSpell(
									String.valueOf(leftCommLockInfo
											.getAppInfo()
											.loadLabel(
													mContext.getPackageManager())))
							.compareToIgnoreCase(
									Cn2Spell.converterToSpell(String.valueOf(leftCommLockInfo
											.getAppInfo()
											.loadLabel(
													mContext.getPackageManager()))));
				else
					return 0;
			} else if (!leftCommLockInfo.getIsFaviterApp()
					&& leftCommLockInfo.getIsLocked()
					&& !rightCommLockInfo.getIsFaviterApp()
					&& !rightCommLockInfo.getIsLocked()) {
				return -1;
			} else if (leftCommLockInfo.getIsFaviterApp()
					&& leftCommLockInfo.getIsLocked()
					&& !rightCommLockInfo.getIsFaviterApp()
					&& !rightCommLockInfo.getIsLocked()) {
				return -1;
			} else if (leftCommLockInfo.getIsFaviterApp()
					&& leftCommLockInfo.getIsLocked()
					&& rightCommLockInfo.getIsFaviterApp()
					&& !rightCommLockInfo.getIsLocked()) {
				if (leftCommLockInfo.getAppInfo() != null
						&& rightCommLockInfo.getAppInfo() != null)
					return Cn2Spell
							.converterToSpell(
									String.valueOf(leftCommLockInfo
											.getAppInfo()
											.loadLabel(
													mContext.getPackageManager())))
							.compareToIgnoreCase(
									Cn2Spell.converterToSpell(String.valueOf(leftCommLockInfo
											.getAppInfo()
											.loadLabel(
													mContext.getPackageManager()))));
				else
					return 0;
			} else if (!leftCommLockInfo.getIsFaviterApp()
					&& !leftCommLockInfo.getIsLocked()
					&& rightCommLockInfo.getIsFaviterApp()
					&& !rightCommLockInfo.getIsLocked()) {
				return 1;
			} else if (leftCommLockInfo.getIsFaviterApp()
					&& !leftCommLockInfo.getIsLocked()
					&& rightCommLockInfo.getIsFaviterApp()
					&& !rightCommLockInfo.getIsLocked()) {
				if (leftCommLockInfo.getAppInfo() != null
						&& rightCommLockInfo.getAppInfo() != null)
					return Cn2Spell
							.converterToSpell(
									String.valueOf(leftCommLockInfo
											.getAppInfo()
											.loadLabel(
													mContext.getPackageManager())))
							.compareToIgnoreCase(
									Cn2Spell.converterToSpell(String.valueOf(leftCommLockInfo
											.getAppInfo()
											.loadLabel(
													mContext.getPackageManager()))));
				else
					return 0;
			} else if (leftCommLockInfo.getIsFaviterApp()
					&& leftCommLockInfo.getIsLocked()
					&& rightCommLockInfo.getIsFaviterApp()
					&& !rightCommLockInfo.getIsLocked()) {
				if (leftCommLockInfo.getAppInfo() != null
						&& rightCommLockInfo.getAppInfo() != null)
					return Cn2Spell
							.converterToSpell(
									String.valueOf(leftCommLockInfo
											.getAppInfo()
											.loadLabel(
													mContext.getPackageManager())))
							.compareToIgnoreCase(
									Cn2Spell.converterToSpell(String.valueOf(leftCommLockInfo
											.getAppInfo()
											.loadLabel(
													mContext.getPackageManager()))));
				else
					return 0;
			} else if (!leftCommLockInfo.getIsFaviterApp()
					&& !leftCommLockInfo.getIsLocked()
					&& !rightCommLockInfo.getIsFaviterApp()
					&& rightCommLockInfo.getIsLocked()) {
				return 1;
			} else if (!leftCommLockInfo.getIsFaviterApp()
					&& !leftCommLockInfo.getIsLocked()
					&& rightCommLockInfo.getIsFaviterApp()
					&& rightCommLockInfo.getIsLocked()) {
				return 1;
			} else if (!leftCommLockInfo.getIsFaviterApp()
					&& leftCommLockInfo.getIsLocked()
					&& rightCommLockInfo.getIsFaviterApp()
					&& rightCommLockInfo.getIsLocked()) {
				return 1;
			} else if (!leftCommLockInfo.getIsFaviterApp()
					&& !leftCommLockInfo.getIsLocked()
					&& !rightCommLockInfo.getIsFaviterApp()
					&& !rightCommLockInfo.getIsLocked()) {
				if (leftCommLockInfo.getAppInfo() != null
						&& rightCommLockInfo.getAppInfo() != null)
					return Cn2Spell
							.converterToSpell(
									String.valueOf(leftCommLockInfo
											.getAppInfo()
											.loadLabel(
													mContext.getPackageManager())))
							.compareToIgnoreCase(
									Cn2Spell.converterToSpell(String.valueOf(leftCommLockInfo
											.getAppInfo()
											.loadLabel(
													mContext.getPackageManager()))));
				else
					return 0;
			} else if (leftCommLockInfo.getIsFaviterApp()
					&& leftCommLockInfo.getIsLocked()
					&& rightCommLockInfo.getIsFaviterApp()
					&& rightCommLockInfo.getIsLocked()) {
				if (leftCommLockInfo.getAppInfo() != null
						&& rightCommLockInfo.getAppInfo() != null)
					return Cn2Spell
							.converterToSpell(
									String.valueOf(leftCommLockInfo
											.getAppInfo()
											.loadLabel(
													mContext.getPackageManager())))
							.compareToIgnoreCase(
									Cn2Spell.converterToSpell(String.valueOf(leftCommLockInfo
											.getAppInfo()
											.loadLabel(
													mContext.getPackageManager()))));
				else
					return 0;
			} else if (!leftCommLockInfo.getIsFaviterApp()
					&& !leftCommLockInfo.getIsLocked()
					&& rightCommLockInfo.getIsFaviterApp()
					&& rightCommLockInfo.getIsLocked()) {
				return 1;
			} else if (!leftCommLockInfo.getIsFaviterApp()
					&& leftCommLockInfo.getIsLocked()
					&& !rightCommLockInfo.getIsFaviterApp()
					&& !rightCommLockInfo.getIsLocked()) {
				return -1;
			}
			return 0;
		}
	};

	@Override
	public void onCreate() {

		super.onCreate();

		mContext = this;
		activityList = new ArrayList<BaseActivity>();
		mLockPatternUtils = new LockPatternUtils(this);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (sharedPreferences.getBoolean("appIsInstance", false)) {
			appIsInstance = true;
		}
		secretAnswerString = getSecretAnswerString();
		appLockState = getAppLockState();
		allowedLeaveAment = getAllowedLeaveAment();
		if (!appIsInstance) {
			// 初始化常用app的数据库
			instaceFaviterApps();
			// 获取手机上的所有应用
			List<ResolveInfo> mAllResolveInfos;
			Intent intent = new Intent(Intent.ACTION_MAIN, null);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			PackageManager packageManager = getPackageManager();
			mAllResolveInfos = packageManager.queryIntentActivities(intent, 0);
			Collections.sort(mAllResolveInfos,
					new ResolveInfo.DisplayNameComparator(packageManager));
			// 初始化数据库
			CommLockInfoService commLockInfoService = new CommLockInfoService(
					getApplicationContext());
			commLockInfoService.getCommLockInfoDaoInstance();
			commLockInfoService.instanceCommLockInfoTable(mAllResolveInfos);

			// 重置appIsInstance值
			appIsInstance = true;
			sharedPreferences.edit().putBoolean("appIsInstance", true).commit();
			setAppLockState(true);
			setUnlockState(false);
			setAllowedLeaveTime(getString(R.string.pwdsetting_advance_allowleavetime_detail_30second));
			setLastAppEnterPwdState(true, 0, 0, 0);
			setLastUserEnterPwdState(true, 0, 0, 0);
			SharedPreferenceUtil.editFirstUserModel(true);
			setAutoRecordPic(false);
			setPlayWarringSoundState(true);
		}

		initLoadImage();
	}

	public static AppLockApplication getInstance() {
		return mContext;
	}

	public LockPatternUtils getLockPatternUtils() {
		return mLockPatternUtils;
	}

	// 获取全局解锁状态
	public boolean getUnlockState() {
		if (sharedPreferences != null) {
			return sharedPreferences.getBoolean("unlockState", false);
		}
		return false;
	}

	// 设置全局解锁状态
	public boolean setUnlockState(boolean state) {
		if (sharedPreferences != null) {
			return sharedPreferences.edit().putBoolean("unlockState", state)
					.commit();
		}
		return false;
	}

	// 获取访客模式状态
	public boolean getVisitorState() {
		if (sharedPreferences != null) {
			return sharedPreferences.getBoolean("visitorState", false);
		}
		return false;
	}

	// 设置访客模式状态
	public boolean setVisitorState(boolean state) {
		if (sharedPreferences != null) {
			ToastUtils.showToast(state ? R.string.toast_user_open
					: R.string.toast_user_close);
			return sharedPreferences.edit().putBoolean("visitorState", state)
					.commit();
		}
		return false;
	}

	// 获取儿童模式状态
	public boolean getBabyState() {
		if (sharedPreferences != null) {
			return sharedPreferences.getBoolean("babyState", false);
		}
		return false;
	}

	// 设置儿童模式状态
	public boolean setBabyState(boolean state) {
		if (sharedPreferences != null) {
			return sharedPreferences.edit().putBoolean("babyState", state)
					.commit();
		}
		return false;
	}

	// 获取老人模式状态
	public boolean getParentState() {
		if (sharedPreferences != null) {
			return sharedPreferences.getBoolean("parentState", false);
		}
		return false;
	}

	// 设置老人模式状态
	public boolean setParentState(boolean state) {
		if (sharedPreferences != null) {
			return sharedPreferences.edit().putBoolean("parentState", state)
					.commit();
		}
		return false;
	}

	// 设置中的启用
	public boolean getAppLockState() {
		if (sharedPreferences != null) {
			return sharedPreferences.getBoolean("appLockState", true);
		}
		return true;
	}

	public boolean setAppLockState(boolean state) {
		if (sharedPreferences != null) {
			appLockState = state;
			return sharedPreferences.edit().putBoolean("appLockState", state)
					.commit();
		}
		return false;
	}

	// 允许短暂退出
	public boolean getAllowedLeaveAment() {
		if (sharedPreferences != null) {
			return sharedPreferences.getBoolean("allowedLeaveAment_1", false);
		}
		return false;
	}

	public boolean setAllowedLeaveAment(boolean state) {
		if (sharedPreferences != null) {
			allowedLeaveAment = state;
			return sharedPreferences.edit()
					.putBoolean("allowedLeaveAment_1", state).commit();
		}
		return false;
	}

	// 允许短暂离开时间
	public String getAllowedLeaveTime() {
		if (sharedPreferences != null) {

			String str = sharedPreferences.getString("allowdLeaveTime", "");

			if (str.contains("10")) {
				return getString(R.string.pwdsetting_advance_allowleavetime_detail_10second);
			} else if (str.contains("30")) {
				return getString(R.string.pwdsetting_advance_allowleavetime_detail_30second);
			} else if (str.contains("1")) {
				return getString(R.string.pwdsetting_advance_allowleavetime_detail_1minute);
			} else if (str.contains("2")) {
				return getString(R.string.pwdsetting_advance_allowleavetime_detail_2minute);
			} else if (str.contains("5")) {
				return getString(R.string.pwdsetting_advance_allowleavetime_detail_5minute);
			}
		}
		return "";
	}

	public boolean setAllowedLeaveTime(String timeString) {
		boolean result = false;
		if (sharedPreferences != null) {
			result = sharedPreferences.edit()
					.putString("allowdLeaveTime", timeString).commit();
			
			return result;

		}
		return result;
	}

	// 密保问题
	public String getSecretQuestionString() {
		int id = SharedPreferenceUtil.readQuestionId();
		if (id == -1) {
			return null;
		} else {
			return getString(SecretConfig.SECRETQUESTIONIDS[id]);
		}
	}

	public void setSecretQuestionString(int strId) {
		SharedPreferenceUtil.editQuestionId(strId);
	}

	private boolean startGuide = false;

	public void setStartGuide(boolean flag) {
		startGuide = flag;
	}

	public boolean isNeedSetSecret() {
		return TextUtils.isEmpty(getSecretAnswerString()) && startGuide;
	}

	// 密保答案
	public String getSecretAnswerString() {
		if (sharedPreferences != null) {
			return sharedPreferences.getString("secretAnswer_1", null);
		}
		return null;
	}

	public boolean setSecretAnswerString(String answerString) {
		if (sharedPreferences != null) {
			return sharedPreferences.edit()
					.putString("secretAnswer_1", answerString).commit();
		}
		return false;
	}

	public boolean setLastUserEnterPwdState(boolean lastUserEnterCorrentPwd,
			long lastUserEnterPwdLeaverDateMiliseconds,
			int lastUserEnterPwdErrorCount, int lastUserEnterPwdDelayTime) {
		LogUtil.e(
				"colin",
				"被锁住的app现在的状态为，解锁结果:"
						+ lastUserEnterCorrentPwd
						+ "离开时间为："
						+ new Date(lastUserEnterPwdLeaverDateMiliseconds)
								.toGMTString() + "错误次数为："
						+ lastUserEnterPwdErrorCount + "剩余延迟时间为："
						+ lastUserEnterPwdDelayTime);
		if (sharedPreferences != null) {
			sharedPreferences
					.edit()
					.putBoolean("lastUserEnterCorrentPwd",
							lastUserEnterCorrentPwd).commit();
			sharedPreferences
					.edit()
					.putLong("lastUserEnterPwdLeaverDateMiliseconds",
							lastUserEnterPwdLeaverDateMiliseconds).commit();
			sharedPreferences
					.edit()
					.putInt("lastUserEnterPwdDelayTime",
							lastUserEnterPwdDelayTime).commit();
			return sharedPreferences
					.edit()
					.putInt("lastUserEnterPwdErrorCount",
							lastUserEnterPwdErrorCount).commit();
		}
		return false;
	}

	public boolean setLastUserEnterCorrentPwd(boolean state) {
		if (sharedPreferences != null) {
			return sharedPreferences.edit()
					.putBoolean("lastUserEnterCorrentPwd", state).commit();
		}
		return false;
	}

	public boolean getLastUserEnterCorrentPwd() {
		if (sharedPreferences != null) {
			return sharedPreferences
					.getBoolean("lastUserEnterCorrentPwd", true);
		}
		return true;
	}

	public long getLastUserEnterPwdLeaverDateMiliseconds() {
		if (sharedPreferences != null) {
			return sharedPreferences.getLong(
					"lastUserEnterPwdLeaverDateMiliseconds", 0);
		}
		return 0;
	}

	public int getLastUserEnterPwdErrorCount() {
		if (sharedPreferences != null) {
			return sharedPreferences.getInt("lastUserEnterPwdErrorCount", 0);
		}
		return 0;
	}

	public boolean setLastUserEnterPwdErrorCount(int errorCount) {
		LogUtil.e("colin", "被锁住的程序错误次数为：" + errorCount);
		if (sharedPreferences != null) {
			return sharedPreferences.edit()
					.putInt("lastUserEnterPwdErrorCount", errorCount).commit();
		}
		return false;
	}

	public int getLastUserEnterPwdDelayTime() {
		if (sharedPreferences != null) {
			return sharedPreferences.getInt("lastUserEnterPwdDelayTime", 0);
		}
		return 0;
	}

	public boolean setLastAppEnterPwdState(boolean lastAppEnterCorrentPwd,
			long lastAppEnterPwdLeaverDateMiliseconds,
			int lastAppEnterPwdErrorCount, int lastAppEnterPwdDelayTime) {
		LogUtil.e(
				"colin",
				"锁锁应用现在的状态,上次正确输入密码:"
						+ lastAppEnterCorrentPwd
						+ "离开时间为："
						+ new Date(lastAppEnterPwdLeaverDateMiliseconds)
								.toGMTString() + "错误的次数:"
						+ lastAppEnterPwdErrorCount + "还有延迟这么多："
						+ lastAppEnterPwdDelayTime);
		if (sharedPreferences != null) {
			sharedPreferences
					.edit()
					.putBoolean("lastAppEnterCorrentPwd",
							lastAppEnterCorrentPwd).commit();
			sharedPreferences
					.edit()
					.putLong("lastAppEnterPwdLeaverDateMiliseconds",
							lastAppEnterPwdLeaverDateMiliseconds).commit();
			sharedPreferences
					.edit()
					.putInt("lastAppEnterPwdDelayTime",
							lastAppEnterPwdDelayTime).commit();
			return sharedPreferences
					.edit()
					.putInt("lastAppEnterPwdErrorCount",
							lastAppEnterPwdErrorCount).commit();
		}
		return false;
	}

	public boolean getLastAppEnterCorrentPwd() {
		if (sharedPreferences != null) {
			return sharedPreferences.getBoolean("lastAppEnterCorrentPwd", true);
		}
		return true;
	}

	public long getLastAppEnterPwdLeaverDateMiliseconds() {
		if (sharedPreferences != null) {
			return sharedPreferences.getLong(
					"lastAppEnterPwdLeaverDateMiliseconds", 0);
		}
		return 0;
	}

	public int getLastAppEnterPwdErrorCount() {
		if (sharedPreferences != null) {
			return sharedPreferences.getInt("lastAppEnterPwdErrorCount", 0);
		}
		return 0;
	}

	public boolean setLastAppEnterPwdErrorCount(int errorCount) {
		LogUtil.e("colin", "打开锁锁应用已经错了：" + errorCount + "次");
		if (sharedPreferences != null) {
			return sharedPreferences.edit()
					.putInt("lastAppEnterPwdErrorCount", errorCount).commit();
		}
		return false;
	}

	public int getLastAppEnterPwdDelayTime() {
		if (sharedPreferences != null) {
			return sharedPreferences.getInt("lastAppEnterPwdDelayTime", 0);
		}
		return 0;
	}

	public boolean setAutoRecordPic(boolean state) {
		if (sharedPreferences != null) {
			return sharedPreferences.edit().putBoolean("AutoRecordPic", state)
					.commit();
		}
		return false;
	}

	public boolean getAutoRecordPic() {
		if (sharedPreferences != null) {
			return sharedPreferences.getBoolean("AutoRecordPic", false);
		}
		return false;
	}

	public boolean setPlayWarringSoundState(boolean state) {
		if (sharedPreferences != null) {
			return sharedPreferences.edit()
					.putBoolean("PlayWarringSoundState", state).commit();
		}
		return false;
	}

	public boolean getPlayWarringSoundState() {
		if (sharedPreferences != null) {
			return sharedPreferences.getBoolean("PlayWarringSoundState", true);
		}
		return false;
	}

	/**
	 * 设置未读取消息个数（原始未读取+新内容）
	 * 
	 * @param size
	 * @return
	 */
	public boolean setReplySize(int size) {
		if (sharedPreferences != null) {
			return sharedPreferences.edit().putInt("replySize", size).commit();
		}
		return false;
	}

	public int getReplySize() {
		if (sharedPreferences != null) {
			return sharedPreferences.getInt("replySize", 0);
		}
		return 0;
	}

	// activity manager
	private static List<BaseActivity> activityList;

	public void doForCreate(BaseActivity activity) {
		activityList.add(activity);
	}

	public void doForFinish(BaseActivity activity) {
		activityList.remove(activity);
	}

	public void goHome(BaseActivity activity) {
		Intent homeIntent = new Intent(Intent.ACTION_MAIN);
		homeIntent.addCategory(Intent.CATEGORY_HOME);
		activity.startActivity(homeIntent);
		activity.finish();
		clearAllActivity();
	}

	public void clearAllActivity() {
		try {
			for (BaseActivity activity : activityList) {
				if (activity != null && !clearAllWhiteList(activity))
					activity.clear();
			}
			activityList.clear();
		} catch (Exception e) {
			LogUtil.d("demo3", "catch:" + e.getMessage());
		}
	}

	private boolean clearAllWhiteList(BaseActivity activity) {
		return activity instanceof NumberUnlockActivity
				|| activity instanceof GestureUnlockActivity
				|| activity instanceof UserUnlockActivity;
	}

	public long getLeaverTime() {
		String leaverTimeString = getAllowedLeaveTime();
		if (leaverTimeString.contains("10")) {
			return 10000;
		} else if (leaverTimeString.contains("30")) {
			return 30000;
		} else if (leaverTimeString.contains("1")) {
			return 60000;
		} else if (leaverTimeString.contains("2")) {
			return 120000;
		} else if (leaverTimeString.contains("5")) {
			return 600000;
		}
		return 0;
	}

	private String lastPkg = "";

	public String getLastUnlockPkg() {
		return lastPkg;
	}

	public void setLastUnlockPkg(String lastPkg) {
		this.lastPkg = lastPkg;
	}

	private List<CommLockInfo> tmpLockInfos;

	public List<CommLockInfo> getTmpLockInfos() {
		return tmpLockInfos;
	}

	public void setTmpLockInfos(List<CommLockInfo> tmpLockInfos) {
		this.tmpLockInfos = tmpLockInfos;
	}

	public void instaceFaviterApps() {
		FaviterAppsService faviterAppsService = new FaviterAppsService(
				getApplicationContext());
		faviterAppsService.addNewFaviterApp("com.whatsapp");
		faviterAppsService.addNewFaviterApp("com.android.gallery3d");
		faviterAppsService.addNewFaviterApp("com.android.mms");
		faviterAppsService.addNewFaviterApp("com.tencent.mm");
		faviterAppsService.addNewFaviterApp("com.android.contacts");
		faviterAppsService.addNewFaviterApp("com.facebook.katana");
		faviterAppsService.addNewFaviterApp("com.mxtech.videoplayer.ad");
		faviterAppsService.addNewFaviterApp("com.facebook.orca");
		faviterAppsService.addNewFaviterApp("com.mediatek.filemanager");
		faviterAppsService.addNewFaviterApp("com.sec.android.gallery3d");
		faviterAppsService.addNewFaviterApp("com.android.email");
		faviterAppsService
				.addNewFaviterApp("com.android.providers.downloads.ui");
		faviterAppsService.addNewFaviterApp("com.sec.android.app.myfiles");
		faviterAppsService.addNewFaviterApp("com.android.vending");
		faviterAppsService.addNewFaviterApp("com.google.android.youtube");
		faviterAppsService.addNewFaviterApp("com.mediatek.videoplayer");
		faviterAppsService.addNewFaviterApp("com.android.calendar");
		faviterAppsService.addNewFaviterApp("com.google.android.talk");
		faviterAppsService.addNewFaviterApp("com.viber.voip");
		faviterAppsService.addNewFaviterApp("com.android.soundrecorder");
		faviterAppsService.addNewFaviterApp("com.sec.android.app.videoplayer");
		faviterAppsService.addNewFaviterApp("com.tencent.mobileqq");
		faviterAppsService.addNewFaviterApp("jp.naver.line.android");
		faviterAppsService.addNewFaviterApp("com.tencent.qq");
		faviterAppsService.addNewFaviterApp("com.google.plus");
		faviterAppsService.addNewFaviterApp("com.google.android.videos");
		faviterAppsService.addNewFaviterApp("com.android.dialer");
		faviterAppsService.addNewFaviterApp("com.samsung.everglades.video");
		faviterAppsService.addNewFaviterApp("com.appstar.callrecorder");
		faviterAppsService
				.addNewFaviterApp("com.sec.android.app.voicerecorder");
		faviterAppsService.addNewFaviterApp("com.htc.soundrecorder");
		faviterAppsService.addNewFaviterApp("com.twitter.android");
	}

	public boolean hasNewVersion() {
		final String oldVersionString = getApplicationVersion();
		UpdateVersionManagerService updateVersionManagerService = new UpdateVersionManagerService(
				getApplicationContext());
		List<UpdateVersionManafer> versionManafers = updateVersionManagerService
				.getVersionManafers();
		boolean hasVersion = false;
		for (UpdateVersionManafer updateVersionManafer : versionManafers) {
			LogUtil.e("colin", "oldversion:" + oldVersionString + "newVersion"
					+ updateVersionManafer.getVersioncode());
			if (updateVersionManafer.getVersioncode() > Double
					.parseDouble(oldVersionString)) {
				hasVersion = true;
			}
		}
		return hasVersion;
	}

	public String getApplicationVersion() {
		PackageManager packageManager = getPackageManager();
		PackageInfo packageInfo;
		try {
			packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public String getUpdateVersionIntro() {
		UpdateVersionManagerService updateVersionManagerService = new UpdateVersionManagerService(
				getApplicationContext());
		List<UpdateVersionManafer> versionManafers = updateVersionManagerService
				.getVersionManafers();
		if (versionManafers.size() > 0) {
			for (UpdateVersionManafer updateVersionManafer : versionManafers) {
				if (updateVersionManafer != null) {
					return updateVersionManafer.getIntro();
				}
			}
		}
		return "";
	}

	public String getUpdateVersionUrl() {
		if (hasNewVersion()) {
			UpdateVersionManagerService updateVersionManagerService = new UpdateVersionManagerService(
					getApplicationContext());
			List<UpdateVersionManafer> versionManafers = updateVersionManagerService
					.getVersionManafers();
			if (versionManafers.size() > 0) {
				for (UpdateVersionManafer updateVersionManafer : versionManafers) {
					if (updateVersionManafer != null) {
						return updateVersionManafer.getUpdateurl();
					}
				}
			}
		}
		return "";
	}

	private void CopyAssets(String assetDir, String dir) {
		String[] files;
		try {
			files = this.getResources().getAssets().list(assetDir);
		} catch (IOException e1) {
			return;
		}
		File mWorkingPath = new File(dir);
		// if this directory does not exists, make one.
		if (!mWorkingPath.exists()) {
			if (!mWorkingPath.mkdirs()) {
				LogUtil.e("--CopyAssets--", "cannot create directory.");
			}
		}
		for (int i = 0; i < files.length; i++) {
			try {
				String fileName = files[i];
				// we make sure file name not contains '.' to be a folder.
				if (!fileName.contains(".")) {
					if (0 == assetDir.length()) {
						CopyAssets(fileName, dir + fileName + "/");
					} else {
						CopyAssets(assetDir + "/" + fileName, dir + fileName
								+ "/");
					}
					continue;
				}
				File outFile = new File(mWorkingPath, fileName);
				if (outFile.exists())
					outFile.delete();
				InputStream in = null;
				if (0 != assetDir.length())
					in = getAssets().open(assetDir + "/" + fileName);
				else
					in = getAssets().open(fileName);
				OutputStream out = new FileOutputStream(outFile);
				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private CommLockInfoService commLockInfoService;

	public CommLockInfoService getLock() {
		if (commLockInfoService == null) {
			commLockInfoService = new CommLockInfoService(this);
			commLockInfoService.getCommLockInfoDaoInstance();
		}
		return commLockInfoService;
	}

	/**
	 * 初始化读取图片的工具
	 */
	private void initLoadImage() {
		File cacheDir = FileUtil.getCacheFile();
		if (cacheDir == null) {
			cacheDir = StorageUtils.getCacheDirectory(this);
		}

		int maxImageMemory = 0;
		int cachesize = 1024 * 1024 * 10;
		int cacheSize = 1024 * 1024 * 50;
		int count = 100;

		maxImageMemory = (int) (Runtime.getRuntime().maxMemory() / 3);

		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
				this)
				.threadPriority(Thread.NORM_PRIORITY - 1)
				.threadPoolSize(3)

				.denyCacheImageMultipleSizesInMemory()
				// 解码图像的大尺寸将在内存中缓存先前解码图像的小尺寸。
				.memoryCache(new LruMemoryCache(maxImageMemory))
				.memoryCacheSize(maxImageMemory)
				// 设置内存缓存的最大大小 默认为一个当前应用可用内存的1/8
				.memoryCacheSizePercentage(13)
				// 设置内存缓存最大大小占当前应用可用内存的百分比 默认为一个当前应用可用内存的1/8
				.memoryCacheExtraOptions(480, 800)
				// 内存缓存的设置选项 (最大图片宽度,最大图片高度) 默认当前屏幕分辨率
				.diskCache(new UnlimitedDiskCache(cacheDir))
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				// 设置磁盘缓存文件名称
				.diskCacheSize(cachesize).diskCacheFileCount(count)
				.discCacheSize(cacheSize).discCacheFileCount(count)
				.imageDownloader(new ThumbnailImageDownloader(this))
				.tasksProcessingOrder(QueueProcessingType.FIFO);// 设置加载显示图片队列进程
																// FIFO 先入先出/
																// LIFO 后入先出
		ImageLoader.getInstance().init(builder.build());
	}
}
