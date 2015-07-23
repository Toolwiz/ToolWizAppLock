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
import com.cleanwiz.applock.data.GroupFile;
import com.cleanwiz.applock.data.HideFile;
import com.cleanwiz.applock.files.entity.GroupFileExt;
import com.cleanwiz.applock.files.entity.HideFileExt;
import com.cleanwiz.applock.files.utils.OpenMIMEUtil;

import java.util.List;

/**
 * Created by dev on 2015/4/30.
 */
public class FileHideAdapter extends BaseHideAdapter {

    public FileHideAdapter(Context context, OnListener onListern) {
        super(context, onListern);
    }

    protected void initView(View view, int position) {
        final HideHolder fileHolder = (HideHolder) view.getTag();

        fileHolder.mImgPreview.setImageBitmap(null);
        fileHolder.mTextView.setText("");

        final Object data = getItem(position);

        if (data instanceof HideFileExt) {
            final HideFileExt hideFileView = (HideFileExt) data;

            fileHolder.mImgPreview.setImageResource(R.drawable.file_1);
            fileHolder.mTextView.setText(hideFileView.getName());

            if (edit) {
                // 编辑模式
                fileHolder.mCheckBox.setVisibility(View.VISIBLE);
                fileHolder.mCheckBox.setCheckedNoAnim(hideFileView.isEnable());
                fileHolder.mFileHideItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideFileView.setEnable(!hideFileView.isEnable());
                        fileHolder.mCheckBox.setChecked(hideFileView.isEnable());
                        updateSelect();
                    }
                });

                //长按监听
                fileHolder.mFileHideItem.setOnLongClickListener(null);

            } else {
                // 正常打开模式
                fileHolder.mCheckBox.setVisibility(View.GONE);
                fileHolder.mCheckBox.setCheckedNoAnim(false);

                fileHolder.mFileHideItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OpenMIMEUtil.getInstance().openFile(context, hideFileView.getNewPathUrl());
                    }
                });

                // 长按监听
                fileHolder.mFileHideItem.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        doVibrator(context);
                        mOnListern.onLongClick(hideFileView);
                        return false;
                    }
                });
            }

        } else if (data instanceof GroupFileExt) {
            final GroupFileExt groupFileView = (GroupFileExt) data;

            fileHolder.mImgPreview.setImageResource(R.drawable.folder);

            fileHolder.mTextView.setText(groupFileView.getName());

            fileHolder.mFileHideItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (edit) {
                        // 编辑模式
                        fileHolder.mCheckBox.setVisibility(View.VISIBLE);
                        boolean enable = groupFileView.isEnable();
                        HideHolder fileHolder = (HideHolder) v.getTag();
                        groupFileView.setEnable(!enable);
                    } else {
                        // 正常打开模式
                        fileHolder.mCheckBox.setVisibility(View.GONE);
                        if (mOnListern != null)
                            mOnListern.openHolder(groupFileView);
                    }
                }
            });
        }
    }

    @Override
    public void setHitFiles(List<?> listGroup, List<?> listFile, int groupID) {

        this.mList_Group = GroupFileExt.transList((List<GroupFile>) listGroup);
        this.mList_HideFile = HideFileExt.transList((List<HideFile>) listFile);

        setGroup(groupID);
        notifyDataSetChanged();
    }
}
