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

import android.os.Environment;
import com.cleanwiz.applock.AppLockApplication;

import java.io.File;
import java.io.IOException;

public class FileUtil {
    public static File updateDir = null;
    public static File updateFile = null;

    public static void createFile(String name) {
        if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
            updateDir = new File(Environment.getExternalStorageDirectory() + "/" + "appLock");
            updateFile = new File(updateDir + "/" + name + ".apk");

            if (!updateDir.exists()) {
                updateDir.mkdirs();
            }
            if (!updateFile.exists()) {
                try {
                    updateFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 在/sdcard/或/data/data/pkg/files/创建文件夹
     */
    public static File mkDir(String dir) {
        String sdPath = getSDPath();
        if (sdPath == null) {
            sdPath = AppLockApplication.getInstance().getFilesDir().getPath();
        }
        String path = sdPath + File.separator + dir;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 缓存.photocat/cache
     */
    public static File getCacheFile() {
        return mkDir(".photocat/cache");
    }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSDPath() {
        //判断sd卡是否存在
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            //获取跟目录
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    /**
     * 通过文件名判断是否为隐藏文件
     *
     * @param name
     * @return
     */
    public static boolean isHideFile(String name) {
        if (name != null && name.length() > 0 && name.substring(0, 1).equals("."))
            return true;
        return false;
    }

    public static boolean isHideFile(File file) {
        return isHideFile(file.getName());
    }
}
