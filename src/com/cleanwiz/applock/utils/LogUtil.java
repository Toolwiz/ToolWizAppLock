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

import android.util.Log;

import com.cleanwiz.applock.AppLockApplication;

/**
 * AppLock全局日志工具
 */
public class LogUtil {

	public static void i(String tag, String msg) {
		if (AppLockApplication.LOG_FLAG) {
			Log.i(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (AppLockApplication.LOG_FLAG) {
			Log.d(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (AppLockApplication.LOG_FLAG) {
			Log.e(tag, msg);
		}
	}
}
