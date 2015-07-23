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
package com.cleanwiz.applock.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.cleanwiz.applock.AppLockApplication;

public class SharedPreferenceUtil {

	public static final String DEFAULT_SETTING_PREFERENCE = "drawwiz";
	public static final String KEY_OPEN_MIME = "open_mime";
	private static Context mContext = AppLockApplication.getInstance();

	public static SharedPreferences getDefaultSharedPreferences(
			Context paramContext) {
		return paramContext.getSharedPreferences(DEFAULT_SETTING_PREFERENCE, 0);
	}

	public static boolean getKeyBoolean(String paramString, boolean paramBoolean) {
		return mContext.getSharedPreferences(DEFAULT_SETTING_PREFERENCE,
				Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO ? 4 : 0)
				.getBoolean(paramString, paramBoolean);
	}

	public static int getKeyInt(String paramString, int paramInt) {
		return mContext.getSharedPreferences(DEFAULT_SETTING_PREFERENCE,
				Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO ? 4 : 0)
				.getInt(paramString, paramInt);
	}

	public static long getKeyLong(String paramString, long paramLong) {
		return mContext.getSharedPreferences(DEFAULT_SETTING_PREFERENCE,
				Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO ? 4 : 0)
				.getLong(paramString, paramLong);
	}

	public static String getKeyString(String paramString1, String paramString2) {
		return mContext.getSharedPreferences(DEFAULT_SETTING_PREFERENCE,
				Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO ? 4 : 0)
				.getString(paramString1, paramString2);
	}

	public static void putKeyBoolean(String paramString, boolean paramBoolean) {
		mContext.getSharedPreferences(DEFAULT_SETTING_PREFERENCE,
				Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO ? 4 : 0)
				.edit().putBoolean(paramString, paramBoolean).commit();
	}

	public static void putKeyInt(String paramString, int paramInt) {
		mContext.getSharedPreferences(DEFAULT_SETTING_PREFERENCE,
				Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO ? 4 : 0)
				.edit().putInt(paramString, paramInt).commit();
	}

	public static void putKeyLong(String paramString, long paramLong) {
		mContext.getSharedPreferences(DEFAULT_SETTING_PREFERENCE,
				Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO ? 4 : 0)
				.edit().putLong(paramString, paramLong).commit();
	}

	public static void putKeyString(String paramString1, String paramString2) {
		mContext.getSharedPreferences(DEFAULT_SETTING_PREFERENCE,
				Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO ? 4 : 0)
				.edit().putString(paramString1, paramString2).commit();
	}

	public static String readFeedMail() {
		return getKeyString("feedback_mail", "");
	}

	public static void editFeedmail(String s) {
		putKeyString("feedback_mail", s);
	}

	public static boolean readFirstUserModel() {
		return getKeyBoolean("FirstUserModel", true);
	}

	public static void editFirstUserModel(boolean isFirst) {
		putKeyBoolean("FirstUserModel", isFirst);
	}

	public static String readNumPassword() {
		return getKeyString("NumPassword", "");
	}

	public static void editNumPassword(String s) {
		putKeyString("NumPassword", s);
	}

	public static boolean readIsFirst() {
		return getKeyBoolean("IsFirst", true);
	}

	public static void editIsFirst(boolean flag) {
		putKeyBoolean("IsFirst", flag);
	}

	public static boolean readIsNumModel() {
		return getKeyBoolean("IsNumModel", false);
	}

	public static void editIsNumModel(boolean isNum) {
		putKeyBoolean("IsNumModel", isNum);
	}

	public static int readQuestionId() {
		return getKeyInt("secretQuestionId_1", -1);
	}

	public static void editQuestionId(int i) {
		putKeyInt("secretQuestionId_1", i);
	}

	public static boolean readUnlockUserByEnter() {
		return getKeyBoolean("UnlockUserByEnter", true);
	}

	public static void editUnlockUserByEnter(boolean flag) {
		putKeyBoolean("UnlockUserByEnter", flag);
	}

	public static boolean readShortCut() {
		return getKeyBoolean("ShortCut_1", false);
	}

	public static void editShortCut(boolean b) {
		putKeyBoolean("ShortCut_1", b);
	}

	public static boolean readEnterFlag() {
		return getKeyBoolean("EnterFlag", false);
	}

	public static void editEnterFlag(boolean b) {
		putKeyBoolean("EnterFlag", b);
	}

	public static long readUpdateTipTime() {
		return getKeyLong("UpdateTipTime", 0);
	}

	public static void editUpdateTipTime(long b) {
		putKeyLong("UpdateTipTime", b);
	}

	public static boolean readNewAppTips() {
		return getKeyBoolean("NewAppTips", false);
	}

	public static void editNewAppTips(boolean b) {
		putKeyBoolean("NewAppTips", b);
	}

	/**
	 * @param flag 如果是true，则下次打开的app 不加锁 如果是false，则加锁
	 */
	public static void setTag(boolean flag) {
		AppLockApplication
				.getInstance()
				.getSharedPreferences(AppLockApplication.getInstance().getPackageName(), Context.MODE_MULTI_PROCESS)
				.edit()
				.putBoolean(KEY_OPEN_MIME, flag)
				.commit();
	}

	public static boolean getTag() {
		return AppLockApplication
				.getInstance()
				.getSharedPreferences(AppLockApplication.getInstance().getPackageName(), Context.MODE_MULTI_PROCESS)
				.getBoolean(KEY_OPEN_MIME, false);
	}

}
