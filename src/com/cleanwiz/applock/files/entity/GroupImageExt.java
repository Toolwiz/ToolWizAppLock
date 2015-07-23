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

import com.cleanwiz.applock.data.GroupImage;
import com.cleanwiz.applock.files.adapter.BaseHideAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片分组
 * Created by dev on 2015/4/29.
 */
public class GroupImageExt extends GroupImage implements BaseHideAdapter.IEnable, BaseHideAdapter.IGroup {

    public GroupImageExt(Long id, Integer parentId, String name, Long createDate) {
        super(id, parentId, name, createDate);
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    private boolean enable;

    public static GroupImageExt copyVal(GroupImage groupImage) {

        return new GroupImageExt(
                groupImage.getId(),
                groupImage.getParentId(),
                groupImage.getName(),
                groupImage.getCreateDate()
        );
    }

    public static List<GroupImageExt> transList(List<GroupImage> list) {
        List<GroupImageExt> listExt = new ArrayList<GroupImageExt>();
        if (list != null)
            for (Object model : list) {
                listExt.add(GroupImageExt.copyVal((GroupImage) model));
            }
        return listExt;
    }
}
