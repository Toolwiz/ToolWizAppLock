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
import com.cleanwiz.applock.data.GroupVideo;
import com.cleanwiz.applock.data.HideVideo;
import com.cleanwiz.applock.files.entity.GroupVideoExt;
import com.cleanwiz.applock.files.entity.HideVideoExt;
import com.cleanwiz.applock.files.utils.BitmapUtil;
import com.cleanwiz.applock.files.utils.OpenMIMEUtil;
import com.cleanwiz.applock.service.VideoService;

import java.util.List;

/**
 * Created by dev on 2015/4/30.
 */
public class VideoHideAdapter extends BaseHideAdapter {
    public VideoHideAdapter(Context context, OnListener onListern) {
        super(context, onListern);
    }

    protected void initView(View view, int position) {
        final HideHolder fileHolder = (HideHolder) view.getTag();

        fileHolder.mImgPreview.setImageBitmap(null);
        fileHolder.mTextView.setText("");

        final Object data = getItem(position);

        if (data instanceof HideVideoExt) {
            final HideVideoExt hideVideoExt = (HideVideoExt) data;

            Bitmap bitmap = VideoService.getVideoThumbnail(hideVideoExt.getNewPathUrl(), 96, 96, MediaStore.Images.Thumbnails.MICRO_KIND);
            if (bitmap != null) {
                bitmap = BitmapUtil.toRoundBitmap(bitmap);
                fileHolder.mImgPreview.setImageBitmap(bitmap);
            } else
                fileHolder.mImgPreview.setImageResource(R.drawable.avi_1);

            fileHolder.mCheckBox.setCheckedNoAnim(hideVideoExt.isEnable());
            fileHolder.mTextView.setText(hideVideoExt.getDisplayName());
            fileHolder.mTV_detail.setText(hideVideoExt.getSizeStr());

            if (edit) {
                // 编辑模式
                fileHolder.mCheckBox.setVisibility(View.VISIBLE);
                fileHolder.mFileHideItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideVideoExt.setEnable(!hideVideoExt.isEnable());
                        fileHolder.mCheckBox.setChecked(hideVideoExt.isEnable());
                        updateSelect();
                    }
                });

                // 长按监听
                fileHolder.mFileHideItem.setOnLongClickListener(null);

            } else {
                // 正常打开模式
                fileHolder.mCheckBox.setVisibility(View.GONE);
                fileHolder.mCheckBox.setCheckedNoAnim(false);

                fileHolder.mFileHideItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OpenMIMEUtil.getInstance().openFile(context, hideVideoExt.getNewPathUrl());
                    }
                });

                // 长按监听
                fileHolder.mFileHideItem.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        doVibrator(context);
                        mOnListern.onLongClick(hideVideoExt);
                        return false;
                    }
                });
            }

        } else if (data instanceof GroupVideoExt) {
            final GroupVideoExt groupVideoView = (GroupVideoExt) data;

            fileHolder.mImgPreview.setImageResource(R.drawable.folder);
            fileHolder.mTextView.setText(groupVideoView.getName());

            fileHolder.mFileHideItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (edit) {
                        // 编辑模式
                        boolean enable = groupVideoView.isEnable();
                        HideHolder fileHolder = (HideHolder) v.getTag();
                        groupVideoView.setEnable(!enable);
                    } else {
                        // 正常打开模式
                        if (mOnListern != null)
                            mOnListern.openHolder(groupVideoView);
                    }
                }
            });
        }
    }

    @Override
    public void setHitFiles(List<?> listGroup, List<?> listFile, int groupID) {

        this.mList_Group = GroupVideoExt.transList((List<GroupVideo>) listGroup);
        this.mList_HideFile = HideVideoExt.transList((List<HideVideo>) listFile);

        setGroup(groupID);
        notifyDataSetChanged();
    }
}
