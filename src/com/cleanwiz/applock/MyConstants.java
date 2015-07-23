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

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class MyConstants {

	public static final String LOCK_PACKAGE_NAME = "lock_package_name";

	public static final String SDPATH = Environment
			.getExternalStorageDirectory().getPath();

	public static String getDatabasePath(Context context, String oldName) {
		boolean sdExist = Environment.isExternalStorageEmulated();
		if (sdExist) {
			return Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/applock/" + oldName;
		} else {
			return oldName;
		}
	}

	public static String getHidePath(String pathString) {
		File pathFile = new File(pathString);
		if (pathFile != null) {
			return pathFile.getPath().substring(0, pathFile.getPath().lastIndexOf('/')) + "/.";
		}
		return "";
	}
	
	public static String getSuffix(){
		return ".lock"; 
	}
}
