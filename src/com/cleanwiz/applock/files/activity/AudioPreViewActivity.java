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
package com.cleanwiz.applock.files.activity;

import android.widget.AdapterView;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.files.adapter.AudioPreViewAdapter;
import com.cleanwiz.applock.files.entity.AudioModelExt;
import com.cleanwiz.applock.model.AudioModel;
import com.cleanwiz.applock.service.AudioService;
import com.gc.materialdesign.views.CheckBox;

import java.util.List;

/**
 * Created by dev on 2015/4/29.
 */
public class AudioPreViewActivity extends BasePreViewActivity {

    private AudioService mAudioService;
    private AudioPreViewAdapter mAudioPreViewAdapter;

    @Override
    void initAdapter() {
        mAudioService = new AudioService(this);
        AdapterView adapterView = (AdapterView) findViewById(R.id.hide_view_list);

        mAudioPreViewAdapter = new AudioPreViewAdapter(this, this, null);
        adapterView.setAdapter(mAudioPreViewAdapter);
        mAudioPreViewAdapter.setPreViewFiles(
                AudioModelExt.transList(
                        (List<AudioModel>) mAudioService.getList()
                )
        );
    }

    @Override
    void initUI() {
        setContentView(R.layout.activity_file_preview);
    }

    @Override
    protected void initListener() {
        super.initListener();

        setTitleRID(R.string.audio_preview_title_add);

        CheckBox item_file_checkbox_all = (CheckBox) findViewById(R.id.item_file_checkbox_all);
        item_file_checkbox_all.setOncheckListener(new CheckBox.OnCheckListener() {
            @Override
            public void onCheck(boolean check) {
                mAudioPreViewAdapter.selectAll(check);
            }
        });
    }

    @Override
    void hideFiles() {
        // 隐藏图片
        List<AudioModelExt> list = (List<AudioModelExt>) mAudioPreViewAdapter.getEnablePreViewFiles();
        for (AudioModelExt imageModelView : list) {
            mAudioService.hideAudio(imageModelView, (int) mBeyondGroupId);
        }
    }
}
