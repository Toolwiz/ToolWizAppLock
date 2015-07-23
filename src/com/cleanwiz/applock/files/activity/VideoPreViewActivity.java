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
import com.cleanwiz.applock.files.adapter.VideoPreViewAdapter;
import com.cleanwiz.applock.files.entity.VideoModelExt;
import com.cleanwiz.applock.model.VideoModel;
import com.cleanwiz.applock.service.VideoService;
import com.gc.materialdesign.views.CheckBox;

import java.util.List;

/**
 * Created by dev on 2015/4/29.
 */
public class VideoPreViewActivity extends BasePreViewActivity {
    private VideoService mVideoService;
    private VideoPreViewAdapter mViewPreViewAdapter;

    @Override
    void initAdapter() {
        mVideoService = new VideoService(this);
        AdapterView adapterView = (AdapterView) findViewById(R.id.hide_view_list);

        mViewPreViewAdapter = new VideoPreViewAdapter(this, this, null);
        adapterView.setAdapter(mViewPreViewAdapter);
        mViewPreViewAdapter.setPreViewFiles(
                VideoModelExt.transList(
                        (List<VideoModel>) mVideoService.getList()
                ));
    }

    @Override
    void initUI() {
        setContentView(R.layout.activity_file_preview);
    }

    @Override
    protected void initListener() {
        super.initListener();
        setTitleRID(R.string.video_preview_title_add);

        CheckBox item_file_checkbox_all = (CheckBox) findViewById(R.id.item_file_checkbox_all);
        item_file_checkbox_all.setOncheckListener(new CheckBox.OnCheckListener() {
            @Override
            public void onCheck(boolean check) {
                mViewPreViewAdapter.selectAll(check);
            }
        });
    }

    @Override
    void hideFiles() {
        // 隐藏图片
        List<VideoModelExt> list = (List<VideoModelExt>) mViewPreViewAdapter.getEnablePreViewFiles();
        for (VideoModelExt imageModelView : list) {
            mVideoService.hideVideo(imageModelView, (int) mBeyondGroupId);
        }
    }
}
