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
import com.cleanwiz.applock.model.AudioModel;
import com.cleanwiz.applock.utils.NumUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev on 2015/4/29.
 */
public class AudioModelExt extends AudioModel implements BaseHideAdapter.IEnable {

    public AudioModelExt(int id, String title, String album, String artist, String path, String displayName, String mimeType, long duration, long size) {
        super(id, title, album, artist, path, displayName, mimeType, duration, size);
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

    public static AudioModelExt copyVal(AudioModel audioModel) {
        return new AudioModelExt(
                audioModel.getId(),
                audioModel.getTitle(),
                audioModel.getAlbum(),
                audioModel.getArtist(),
                audioModel.getPath(),
                audioModel.getDisplayName(),
                audioModel.getMimeType(),
                audioModel.getDuration(),
                audioModel.getSize()
        );
    }

    public static List<AudioModelExt> transList(List<AudioModel> list) {
        List<AudioModelExt> listExt = new ArrayList();
        if (list != null)
            for (Object model : list) {
                listExt.add(AudioModelExt.copyVal((AudioModel) model));
            }
        return listExt;
    }

    public String getSizeStr() {
        float size = (float) this.getSize() / 1024 / 1024;
        return NumUtil.transMoney(size) + "MB";
    }
}
