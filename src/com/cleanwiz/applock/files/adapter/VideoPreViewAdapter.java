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
package com.cleanwiz.applock.files.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.files.entity.VideoModelExt;
import com.cleanwiz.applock.files.utils.BitmapUtil;
import com.cleanwiz.applock.service.VideoService;

import java.util.List;

/**
 * Created by dev on 2015/4/30.
 */
public class VideoPreViewAdapter extends BasePreViewAdapter {

    public VideoPreViewAdapter(Context context, OnListener onListern, List<?> fileList) {
        super(context, onListern, fileList);
    }

    protected void initView(View view, int position) {
        final FilePreViewHolder fileHolder = (FilePreViewHolder) view.getTag();

        final VideoModelExt videoModelExt = (VideoModelExt) mFileList.get(position);
        fileHolder.mObject = videoModelExt;

        fileHolder.mImgPreview.setImageBitmap(null);

        Bitmap bitmap = VideoService.getVideoThumbnail(videoModelExt.getPath(), 96, 96, MediaStore.Images.Thumbnails.MICRO_KIND);
        if (bitmap != null) {
            bitmap = BitmapUtil.toRoundBitmap(bitmap);
            fileHolder.mImgPreview.setImageBitmap(bitmap);
        } else
            fileHolder.mImgPreview.setImageResource(R.drawable.avi_1);

        fileHolder.mTextView.setText(videoModelExt.getDisplayName());
        fileHolder.mTV_detail.setText(videoModelExt.getSizeStr());

        fileHolder.mCheckBox.setChecked(videoModelExt.isEnable());
        fileHolder.mItem_file_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoModelExt.setEnable(!videoModelExt.isEnable());
                fileHolder.mCheckBox.setChecked(videoModelExt.isEnable());
                updateSelect();
            }
        });
    }
}
