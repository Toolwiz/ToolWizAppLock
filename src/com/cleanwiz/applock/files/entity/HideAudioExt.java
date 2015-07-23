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

import android.util.Log;
import com.cleanwiz.applock.data.HideAudio;
import com.cleanwiz.applock.files.adapter.BaseHideAdapter;
import com.cleanwiz.applock.model.AudioModel;
import com.cleanwiz.applock.utils.NumUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev on 2015/4/29.
 */
public class HideAudioExt extends HideAudio implements BaseHideAdapter.IEnable {

    private String sizeStr;

    public HideAudioExt(Long id, Integer beyondGroupId, String title, String album, String artist, String oldPathUrl, String displayName, String mimeType, String duration, String newPathUrl, Long size, Long moveDate) {
        super(id, beyondGroupId, title, album, artist, oldPathUrl, displayName, mimeType, duration, newPathUrl, size, moveDate);
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

    public static HideAudioExt copyVal(HideAudio hideAudio) {
        return new HideAudioExt(
                hideAudio.getId(),
                hideAudio.getBeyondGroupId(),
                hideAudio.getTitle(),
                hideAudio.getAlbum(),
                hideAudio.getArtist(),
                hideAudio.getOldPathUrl(),
                hideAudio.getDisplayName(),
                hideAudio.getMimeType(),
                hideAudio.getDuration(),
                hideAudio.getNewPathUrl(),
                hideAudio.getSize(),
                hideAudio.getMoveDate()
        );
    }

    public static List<HideAudioExt> transList(List<?> list) {
        List<HideAudioExt> listExt = new ArrayList();
        if (list != null)
            for (Object model : list) {
                listExt.add(HideAudioExt.copyVal((HideAudio) model));
            }
        return listExt;
    }

    public String getSizeStr() {
        float size = (float) this.getSize() / 1024 / 1024;
        return NumUtil.transMoney(size) + "MB";
    }

    public AudioModel transientToModel() {
        AudioModel audioModel = null;
        try {
            audioModel = new AudioModel(
                    this.getId().intValue(),
                    this.getTitle(),
                    this.getAlbum(),
                    this.getArtist(),
                    this.getNewPathUrl(),
                    this.getDisplayName(),
                    this.getMimeType(),
                    Long.parseLong(this.getDuration()),
                    this.getSize()
            );
        } catch (NumberFormatException e) {
            Log.e("HideAudioExt", e.toString());
        }

        return audioModel;
    }

}
