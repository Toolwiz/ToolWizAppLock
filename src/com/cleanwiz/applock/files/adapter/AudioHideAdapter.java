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
import android.view.View;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.files.entity.GroupAudioExt;
import com.cleanwiz.applock.files.entity.HideAudioExt;
import com.cleanwiz.applock.files.utils.OpenMIMEUtil;

import java.util.List;

/**
 * Created by dev on 2015/4/30.
 */
public class AudioHideAdapter extends BaseHideAdapter {

    public AudioHideAdapter(Context context, OnListener onListern) {
        super(context, onListern);
    }

    protected void initView(View view, int position) {
        final HideHolder fileHolder = (HideHolder) view.getTag();

        fileHolder.mImgPreview.setImageBitmap(null);
        fileHolder.mTextView.setText("");

        final Object data = getItem(position);

        if (data instanceof HideAudioExt) {
            final HideAudioExt hideAudioExt = (HideAudioExt) data;

            fileHolder.mImgPreview.setImageResource(R.drawable.audio_1);
            fileHolder.mCheckBox.setChecked(hideAudioExt.isEnable());
            fileHolder.mTextView.setText(hideAudioExt.getDisplayName());
            fileHolder.mTV_detail.setText(hideAudioExt.getSizeStr());

            if (edit) {
                // 编辑模式
                fileHolder.mCheckBox.setVisibility(View.VISIBLE);
                fileHolder.mFileHideItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideAudioExt.setEnable(!hideAudioExt.isEnable());
                        fileHolder.mCheckBox.setChecked(hideAudioExt.isEnable());
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
                        OpenMIMEUtil.getInstance().openFile(context, hideAudioExt.getNewPathUrl());
                    }
                });

                // 长按监听
                fileHolder.mFileHideItem.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        doVibrator(context);
                        mOnListern.onLongClick(hideAudioExt);
                        return false;
                    }
                });
            }

        } else if (data instanceof GroupAudioExt) {
            final GroupAudioExt groupAudioExt = (GroupAudioExt) data;

            fileHolder.mImgPreview.setImageResource(R.drawable.folder);
            fileHolder.mTextView.setText(groupAudioExt.getName());

            fileHolder.mFileHideItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (edit) {
                        // 编辑模式
                        groupAudioExt.setEnable(!groupAudioExt.isEnable());
                    } else {
                        // 正常打开模式
                        mOnListern.openHolder(groupAudioExt);
                    }
                }
            });
        }
    }

    @Override
    public void setHitFiles(List<?> listGroup, List<?> listFile, int groupID) {

        this.mList_Group = GroupAudioExt.transList(listGroup);
        this.mList_HideFile = HideAudioExt.transList(listFile);

        setGroup(groupID);
        notifyDataSetChanged();
    }
}
