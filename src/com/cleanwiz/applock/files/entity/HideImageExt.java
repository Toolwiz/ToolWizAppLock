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

import android.os.Parcel;
import android.os.Parcelable;
import com.cleanwiz.applock.data.HideImage;
import com.cleanwiz.applock.files.adapter.BaseHideAdapter;
import com.cleanwiz.applock.model.ImageModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 当前隐藏的图片
 * Created by dev on 2015/4/28.
 */
public class HideImageExt extends HideImage implements BaseHideAdapter.IEnable, Parcelable {

    public static HideImageExt copyVal(HideImage hideImage) {

        return new HideImageExt(
                hideImage.getId(),
                hideImage.getBeyondGroupId(),
                hideImage.getTitle(),
                hideImage.getDisplayName(),
                hideImage.getMimeType(),
                hideImage.getOldPathUrl(),
                hideImage.getNewPathUrl(),
                hideImage.getSize(),
                hideImage.getMoveDate()
        );
    }

    public static List<HideImageExt> transList(List<HideImage> list) {
        List<HideImageExt> listImageView = new ArrayList<HideImageExt>();
        if (list != null)
            for (Object imageModel : list) {
                listImageView.add(HideImageExt.copyVal((HideImage) imageModel));
            }
        return listImageView;
    }

    public HideImageExt(Long id, Integer beyondGroupId, String title, String displayName, String mimeType, String oldPathUrl, String newPathUrl, Long size, Long moveDate) {
        super(id, beyondGroupId, title, displayName, mimeType, oldPathUrl, newPathUrl, size, moveDate);
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    private boolean enable;

    public ImageModel transientToModel() {

        ImageModel imageModel = null;
        imageModel = new ImageModel(
                this.getId().intValue(),
                this.getTitle(),
                this.getDisplayName(),
                this.getMimeType(),
                this.getNewPathUrl(),
                this.getSize()
        );

        return imageModel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeInt(getBeyondGroupId());
        dest.writeString(getTitle());
        dest.writeString(getDisplayName());
        dest.writeString(getMimeType());
        dest.writeString(getOldPathUrl());
        dest.writeString(getNewPathUrl());
        dest.writeLong(getSize());
        dest.writeLong(getMoveDate());
        dest.writeInt(enable ? 1 : 0);
    }

    private HideImageExt(Parcel in) {
        setId(in.readLong());
        setBeyondGroupId(in.readInt());
        setTitle(in.readString());
        setDisplayName(in.readString());
        setMimeType(in.readString());
        setOldPathUrl(in.readString());
        setNewPathUrl(in.readString());
        setSize(in.readLong());
        setMoveDate(in.readLong());
        setEnable(in.readInt() == 0 ? false : true);
    }

    public static final Parcelable.Creator<HideImageExt> CREATOR = new Parcelable.Creator<HideImageExt>() {
        public HideImageExt createFromParcel(Parcel in) {
            return new HideImageExt(in);
        }

        public HideImageExt[] newArray(int size) {
            return new HideImageExt[size];
        }
    };
}
