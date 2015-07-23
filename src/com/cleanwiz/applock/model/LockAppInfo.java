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
package com.cleanwiz.applock.model;


public class LockAppInfo {

	public static int APP_LOCK_TYPE_COMM = 1;	//普通锁
	public static int APP_LOCK_TYPE_TIME = 2;	//时间锁
	public static int APP_LOCK_TYPE_WIFI = 3;	//wifi锁
	public static int APP_LOCK_TYPE_MODEL = 4;	//情景锁
	
	int lockType;
}
