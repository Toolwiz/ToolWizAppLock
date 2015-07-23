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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import com.cleanwiz.applock.MyConstants;
import com.cleanwiz.applock.data.HideFile;
import com.cleanwiz.applock.data.HideFileDao.DaoMaster;
import com.cleanwiz.applock.data.HideFileDao.DaoMaster.DevOpenHelper;
import com.cleanwiz.applock.data.HideFileDao.DaoSession;
import com.cleanwiz.applock.data.HideFileDao.HideFileDao;
import com.cleanwiz.applock.data.HideFileDao.HideFileDao.Properties;
import com.cleanwiz.applock.files.utils.FileHideUtils;
import com.cleanwiz.applock.model.FileModel;
import com.cleanwiz.applock.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileService {

    private Context context;
    private String IMAGE_HIDE_URL = "";
    private DaoSession daoSession = null;
    private HideFileDao hideFileDao = null;

    public FileService(Context context) {
        super();
        this.context = context;
        instanceHideFileDataBase();
        LogUtil.e("colin", IMAGE_HIDE_URL);
    }

    public void instanceHideFileDataBase() {
        if (hideFileDao == null) {
            DevOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                    MyConstants.getDatabasePath(context, "hidefile"), null);
            SQLiteDatabase db = helper.getWritableDatabase();
            DaoMaster daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            hideFileDao = daoSession.getHideFileDao();
        }
    }

    // 获取指定目录下的文件与子目录
    public List<FileModel> getFilesByDir(String dirString) {
        File parentFile;
        List<FileModel> fileModels = new ArrayList<FileModel>();
        if (dirString == null || dirString.isEmpty()) {
            parentFile = Environment.getRootDirectory();
        } else {
            parentFile = new File(dirString);
        }

        File fileList[] = parentFile.listFiles();
        if (fileList == null || fileList.length == 0) {
            return null;
        }
        for (File file : fileList) {
            FileModel fileModel = new FileModel();
            if (file.isDirectory()) {
                fileModel.setFileType(fileModel.FILE_DIR);
            } else {
                fileModel.setFileType(fileModel.FILE_FILE);
            }
            fileModel.setName(file.getName());
            fileModel.setPath(file.getPath());
            fileModels.add(fileModel);
        }
        return fileModels;
    }

    // 隐藏文件
    public boolean hideFile(FileModel fileModel, int beyondGroupId) {
        File fromFile = new File(fileModel.getPath());
        if (!fromFile.exists()) {
            return false;
        }
        String toPathString = MyConstants.getHidePath(fileModel.getPath());
        if (toPathString.isEmpty()) {
            return false;
        }
        File toFile = new File(toPathString + fileModel.getName() + MyConstants.getSuffix());
        // 复制
        if (fromFile.renameTo(toFile)) {
            // 插入数据库
            if (hideFileDao != null) {
                long id = hideFileDao.insertOrReplace(new HideFile(null,
                        beyondGroupId, fileModel.getName(),
                        fileModel.getPath(), toFile.getPath(), new Date()
                        .getTime()));
                if (id >= 0) {
                    return true;
                }
            }
        }

        return false;
    }

    // 取消文件隐藏
    public boolean unHideFile(HideFile hideFile) {
        if (hideFile != null) {
            File fromFile = new File(hideFile.getNewPathUrl());
            File toFile = new File(hideFile.getOldPathUrl());
            // 插入数据库
            if (hideFileDao != null) {
                hideFileDao.delete(hideFile);
            }

            // 复制
            if (fromFile.renameTo(toFile)) {
                return true;
            }
        }
        return false;
    }

    // 获取所有加密文件列表
    public List<HideFile> getHideFiles(int beyondGroupId) {
        List<HideFile> hideFiles = new ArrayList<HideFile>();
        if (hideFileDao != null) {
            hideFiles = hideFileDao.queryBuilder()
                    .where(Properties.BeyondGroupId.eq(beyondGroupId)).list();

            final List<HideFile> list = FileHideUtils.checkHideFile(hideFiles);
            if (list.size() > 0) {
                new Thread() {
                    @Override
                    public void run() {
                        for (HideFile hideFile : list) {
                            unHideFile(hideFile);
                            deleteAudioByPath(hideFile.getOldPathUrl());
                        }
                    }
                }.start();
            }
        }

        return hideFiles;
    }

    public int getHideFileCount() {
        if (hideFileDao != null) {
            List<HideFile> hideFileList = hideFileDao.loadAll();
            return hideFileList.size();
        }
        return 0;
    }

    // 删除指定文件
    public boolean deleteAudioByPath(String pathString) {
        if (pathString == null || pathString.isEmpty()) {
            return false;
        }
        File fileFile = new File(pathString);
        if (fileFile != null) {
            return fileFile.delete();
        }
        return false;
    }

}
