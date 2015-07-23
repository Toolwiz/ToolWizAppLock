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

import com.cleanwiz.applock.data.GroupVideo;
import com.cleanwiz.applock.files.adapter.BaseHideAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev on 2015/4/29.
 */
public class GroupVideoExt extends GroupVideo implements BaseHideAdapter.IEnable, BaseHideAdapter.IGroup {

    /**
     * 是否选中
     */
    private boolean enable;

    public GroupVideoExt(Long id, Integer parentId, String name, Long createDate) {
        super(id, parentId, name, createDate);
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public static GroupVideoExt copyVal(GroupVideo groupVideo) {
        return new GroupVideoExt(
                groupVideo.getId(),
                groupVideo.getParentId(),
                groupVideo.getName(),
                groupVideo.getCreateDate()
        );
    }

    public static List<GroupVideoExt> transList(List<?> list) {
        List<GroupVideoExt> listExt = new ArrayList();
        if (list != null)
            for (Object model : list) {
                listExt.add(GroupVideoExt.copyVal((GroupVideo) model));
            }
        return listExt;
    }
}
