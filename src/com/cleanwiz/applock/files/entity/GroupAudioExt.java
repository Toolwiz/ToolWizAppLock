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

import com.cleanwiz.applock.data.GroupAudio;
import com.cleanwiz.applock.files.adapter.BaseHideAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev on 2015/4/29.
 */
public class GroupAudioExt extends GroupAudio implements BaseHideAdapter.IEnable, BaseHideAdapter.IGroup {

    public GroupAudioExt(Long id, Integer parentId, String name, Long createDate) {
        super(id, parentId, name, createDate);
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

    public static GroupAudioExt copyVal(GroupAudio groupAudio) {
        return new GroupAudioExt(
                groupAudio.getId(),
                groupAudio.getParentId(),
                groupAudio.getName(),
                groupAudio.getCreateDate()
        );
    }

    public static List<GroupAudioExt> transList(List<?> list) {
        List<GroupAudioExt> listExt = new ArrayList();
        if (list != null)
            for (Object model : list) {
                listExt.add(GroupAudioExt.copyVal((GroupAudio) model));
            }
        return listExt;
    }
}
