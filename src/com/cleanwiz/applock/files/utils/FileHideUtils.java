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
package com.cleanwiz.applock.files.utils;

import com.cleanwiz.applock.data.HideAudio;
import com.cleanwiz.applock.data.HideFile;
import com.cleanwiz.applock.data.HideImage;
import com.cleanwiz.applock.data.HideVideo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件工具类
 * Created by dev on 2015/5/25.
 */
public class FileHideUtils {

    /**
     * 检测隐藏文件列表，如果有已经删除的文件，返回提交，更新数据库
     *
     * @param list
     * @return
     */
    public static List<HideFile> checkHideFile(List<HideFile> list) {
        List<HideFile> list1 = new ArrayList<>();
        for (int i = list.size() - 1; i > -1; i--) {
            HideFile hideFile = list.get(i);
            if (!new File(hideFile.getNewPathUrl()).exists()) {
                list1.add(hideFile);
                list.remove(i);
            }
        }
        return list1;
    }

    public static List<HideImage> checkHideImage(List<HideImage> list) {
        List<HideImage> list1 = new ArrayList<>();
        for (int i = list.size() - 1; i > -1; i--) {
            HideImage hideFile = list.get(i);
            if (!new File(hideFile.getNewPathUrl()).exists()) {
                list1.add(hideFile);
                list.remove(i);
            }
        }
        return list1;
    }

    public static List<HideAudio> checkHideAudio(List<HideAudio> list) {
        List<HideAudio> list1 = new ArrayList<>();
        for (int i = list.size() - 1; i > -1; i--) {
            HideAudio hideFile = list.get(i);
            if (!new File(hideFile.getNewPathUrl()).exists()) {
                list1.add(hideFile);
                list.remove(i);
            }
        }
        return list1;
    }

    public static List<HideVideo> checkHideVideo(List<HideVideo> list) {
        List<HideVideo> list1 = new ArrayList<>();
        for (int i = list.size() - 1; i > -1; i--) {
            HideVideo hideFile = list.get(i);
            if (!new File(hideFile.getNewPathUrl()).exists()) {
                list1.add(hideFile);
                list.remove(i);
            }
        }
        return list1;
    }
}
