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
import com.cleanwiz.applock.model.VideoModel;
import com.cleanwiz.applock.utils.NumUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev on 2015/4/29.
 */
public class VideoModelExt extends VideoModel implements BaseHideAdapter.IEnable {

    public VideoModelExt(int id, String title, String album, String artist, String displayName, String mimeType, String path, long size, long duration) {
        super(id, title, album, artist, displayName, mimeType, path, size, duration);
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

    public static VideoModelExt copyVal(VideoModel videoModel) {
        return new VideoModelExt(
                videoModel.getId(),
                videoModel.getTitle(),
                videoModel.getAlbum(),
                videoModel.getArtist(),
                videoModel.getDisplayName(),
                videoModel.getMimeType(),
                videoModel.getPath(),
                videoModel.getSize(),
                videoModel.getDuration()
        );
    }

    public static List<VideoModelExt> transList(List<VideoModel> list) {
        List<VideoModelExt> listExt = new ArrayList();
        if (list != null) {
            for (Object model : list) {
                listExt.add(VideoModelExt.copyVal((VideoModel) model));
            }
        }
        return listExt;
    }

    public String getSizeStr() {
        float size = (float) this.getSize() / 1024 / 1024;
        return NumUtil.transMoney(size) + "MB";
    }
}
