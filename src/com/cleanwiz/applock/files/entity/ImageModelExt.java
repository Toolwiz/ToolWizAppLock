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
import com.cleanwiz.applock.model.ImageModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统图片（预览选取）
 * Created by dev on 2015/4/28.
 */
public class ImageModelExt extends ImageModel implements BaseHideAdapter.IEnable {

    public ImageModelExt(int id, String title, String displayName, String mimeType, String path, long size) {
        super(id, title, displayName, mimeType, path, size);
    }

    /**
     * 是否选中
     */
    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public static ImageModelExt copyVal(ImageModel imageModel) {
        return new ImageModelExt(
                imageModel.getId(),
                imageModel.getTitle(),
                imageModel.getDisplayName(),
                imageModel.getMimeType(),
                imageModel.getPath(),
                imageModel.getSize());

    }

    public static List<ImageModelExt> transList(List<ImageModel> list) {
        List<ImageModelExt> listExt = new ArrayList();
        if (list != null) {
            for (Object model : list) {
                listExt.add(ImageModelExt.copyVal((ImageModel) model));
            }
        }
        return listExt;
    }
}
