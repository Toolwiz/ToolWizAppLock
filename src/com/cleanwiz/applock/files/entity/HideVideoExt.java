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

import com.cleanwiz.applock.data.HideVideo;
import com.cleanwiz.applock.files.adapter.BaseHideAdapter;
import com.cleanwiz.applock.model.VideoModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev on 2015/4/29.
 */
public class HideVideoExt extends HideVideo implements BaseHideAdapter.IEnable {

    public HideVideoExt(Long id, Integer beyondGroupId, String title, String album, String artist, String oldPathUrl, String displayName, String mimeType, Long duration, String newPathUrl, Long size, Long moveDate) {
        super(id, beyondGroupId, title, album, artist, oldPathUrl, displayName, mimeType, duration, newPathUrl, size, moveDate);
    }

    public static HideVideoExt copyVal(HideVideo hideVideo) {

        return new HideVideoExt(
                hideVideo.getId(),
                hideVideo.getBeyondGroupId(),
                hideVideo.getTitle(),
                hideVideo.getAlbum(),
                hideVideo.getArtist(),
                hideVideo.getOldPathUrl(),
                hideVideo.getDisplayName(),
                hideVideo.getMimeType(),
                hideVideo.getDuration(),
                hideVideo.getNewPathUrl(),
                hideVideo.getSize(),
                hideVideo.getMoveDate()
        );
    }

    public static List<HideVideoExt> transList(List<HideVideo> list) {
        List<HideVideoExt> listImageView = new ArrayList<HideVideoExt>();
        if (list != null)
            for (Object imageModel : list) {
                listImageView.add(HideVideoExt.copyVal((HideVideo) imageModel));
            }
        return listImageView;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    private boolean enable;

    /**
     * 获取文件大小 取整(单位MB)
     *
     * @return
     */
    public String getSizeStr() {
        Long size = this.getSize() / 1024 / 1024;
        return size + "MB";
    }

    public VideoModel transientToModel() {

        VideoModel videoModel = null;
        videoModel = new VideoModel(
                this.getId().intValue(),
                this.getTitle(),
                this.getAlbum(),
                this.getArtist(),
                this.getDisplayName(),
                this.getMimeType(),
                this.getNewPathUrl(),
                this.getSize(),
                this.getDuration()
        );

        return videoModel;
    }
}
