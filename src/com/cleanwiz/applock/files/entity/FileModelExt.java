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
package com.cleanwiz.applock.files.entity;

import com.cleanwiz.applock.files.adapter.BaseHideAdapter;
import com.cleanwiz.applock.model.FileModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev on 2015/4/29.
 */
public class FileModelExt extends FileModel implements BaseHideAdapter.IEnable, Comparable<FileModelExt> {

    public FileModelExt(String name, String path, int fileType) {
        super(name, path, fileType);
    }

    /**
     * 是否选中
     */
    private boolean enable;

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public static FileModelExt copyVal(FileModel fileModel) {
        return new FileModelExt(
                fileModel.getName(),
                fileModel.getPath(),
                fileModel.getFileType()
        );
    }

    /**
     * 转换数据，并屏蔽隐藏文件（以.开始的文件）
     *
     * @param list
     * @return
     */
    public static List<FileModelExt> transList(List<FileModel> list) {
        List<FileModelExt> listExt = new ArrayList();
        char dian = '.';
        if (list != null)
            for (Object model : list) {
                FileModel fileModel = (FileModel) model;
                if (fileModel.getName().charAt(0) != dian)
                    listExt.add(FileModelExt.copyVal((FileModel) model));
            }
        return listExt;
    }

    @Override
    public int compareTo(FileModelExt another) {
        if (this.getFileType() == another.getFileType()) {
            return this.getName().compareToIgnoreCase(another.getName());
        } else if (this.getFileType() > another.getFileType()) {
            return 1;
        } else
            return -1;
    }
}
