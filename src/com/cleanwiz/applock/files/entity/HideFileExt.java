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

import com.cleanwiz.applock.data.HideFile;
import com.cleanwiz.applock.files.adapter.BaseHideAdapter;
import com.cleanwiz.applock.model.FileModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev on 2015/4/29.
 */
public class HideFileExt extends HideFile implements BaseHideAdapter.IEnable {
    /**
     * 是否选中
     */
    private boolean enable;

    public HideFileExt(Long id, Integer beyondGroupId, String name, String oldPathUrl, String newPathUrl, Long moveDate) {
        super(id, beyondGroupId, name, oldPathUrl, newPathUrl, moveDate);
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public static HideFileExt copyVal(HideFile hideFile) {
        return new HideFileExt(
                hideFile.getId(),
                hideFile.getBeyondGroupId(),
                hideFile.getName(),
                hideFile.getOldPathUrl(),
                hideFile.getNewPathUrl(),
                hideFile.getMoveDate()
        );
    }

    public static List<HideFileExt> transList(List<?> list) {
        List<HideFileExt> listExt = new ArrayList();
        if (list != null)
            for (Object model : list) {
                listExt.add(HideFileExt.copyVal((HideFile) model));
            }
        return listExt;
    }

    public FileModel transientToModel() {
        FileModel fileModel = null;

        return fileModel;
    }
}
