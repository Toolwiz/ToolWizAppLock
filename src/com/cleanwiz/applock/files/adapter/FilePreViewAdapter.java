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
import com.cleanwiz.applock.files.entity.FileModelExt;
import com.cleanwiz.applock.model.FileModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 文件预览适配器
 * Created by dev on 2015/4/30.
 */
public class FilePreViewAdapter extends BasePreViewAdapter {

    private OnFolder onFolder;

    public FilePreViewAdapter(Context context, OnFolder onFolder, List<?> fileList) {
        super(context, onFolder, fileList);
        this.onFolder = onFolder;
    }

    public interface OnFolder extends OnListener {

        /**
         * 文件夹选中，打开文件夹
         *
         * @param fileModel
         */
        void openFolder(FileModel fileModel);
    }

    @Override
    public void setPreViewFiles(List<?> list) {
        Collections.sort((List<FileModelExt>) list);
        super.setPreViewFiles(list);
    }

    @Override
    protected void initView(final View view, int position) {
        final FilePreViewHolder fileHolder = (FilePreViewHolder) view.getTag();

        final FileModelExt fileModelExt = (FileModelExt) mFileList.get(position);
        fileHolder.mObject = getItem(position);
        FileModel fileModel = (FileModel) fileHolder.mObject;
        fileHolder.mObject = fileModelExt;

        if (fileModel.getFileType() == FileModel.FILE_FILE) {
            fileHolder.mImgPreview.setImageResource(R.drawable.file_1);
        } else
            fileHolder.mImgPreview.setImageResource(R.drawable.folder);

        fileHolder.mTextView.setText(fileModel.getName());

        //直接设置无动画效果
        fileHolder.mCheckBox.setCheckedNoAnim(fileModelExt.isEnable());


        if (fileModelExt.getFileType() == FileModel.FILE_DIR) {
            //文件夹
            fileHolder.mCheckBox.setVisibility(View.GONE);
            fileHolder.mItem_file_linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 打开文件夹
                    FilePreViewHolder fileHolder2 = (FilePreViewHolder) view.getTag();
                    onFolder.openFolder((FileModel) fileHolder2.mObject);
                }
            });

        } else {
            //文件
            fileHolder.mCheckBox.setVisibility(View.VISIBLE);
            fileHolder.mItem_file_linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    fileModelExt.setEnable(!fileModelExt.isEnable());
                    fileHolder.mCheckBox.setChecked(fileModelExt.isEnable());
                    updateSelect();
                }
            });
        }
    }

    @Override
    public void selectAll(boolean selected) {
        List<BaseHideAdapter.IEnable> list = new ArrayList<BaseHideAdapter.IEnable>();
        for (Object object : mFileList) {
            if (((FileModelExt) object).getFileType() == FileModel.FILE_FILE)
                ((BaseHideAdapter.IEnable) object).setEnable(selected);
        }
        mOnListern.setSelect(selected);
        notifyDataSetChanged();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.item_file_hide2;
    }
}
