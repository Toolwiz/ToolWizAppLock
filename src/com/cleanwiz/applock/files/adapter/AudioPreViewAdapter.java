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
import com.cleanwiz.applock.files.entity.AudioModelExt;

import java.util.List;

/**
 * Created by dev on 2015/4/30.
 */
public class AudioPreViewAdapter extends BasePreViewAdapter {

    public AudioPreViewAdapter(Context context, OnListener onListern, List<?> fileList) {
        super(context, onListern, fileList);
    }

    protected void initView(final View view, int position) {
        final FilePreViewHolder fileHolder = (FilePreViewHolder) view.getTag();

        final AudioModelExt audioModelExt = (AudioModelExt) mFileList.get(position);
        fileHolder.mObject = audioModelExt;
        fileHolder.mImgPreview.setImageBitmap(null);

        fileHolder.mImgPreview.setImageResource(R.drawable.audio_1);
        fileHolder.mTextView.setText(audioModelExt.getDisplayName());
        fileHolder.mTV_detail.setText(audioModelExt.getSizeStr());

        fileHolder.mCheckBox.setChecked(audioModelExt.isEnable());
        fileHolder.mItem_file_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                audioModelExt.setEnable(!audioModelExt.isEnable());
                fileHolder.mCheckBox.setChecked(audioModelExt.isEnable());
                updateSelect();
            }
        });
    }
}
