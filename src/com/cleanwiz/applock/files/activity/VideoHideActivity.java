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

import android.content.Intent;
import android.widget.AdapterView;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.data.GroupVideo;
import com.cleanwiz.applock.data.HideVideo;
import com.cleanwiz.applock.files.adapter.BaseHideAdapter;
import com.cleanwiz.applock.files.adapter.VideoHideAdapter;
import com.cleanwiz.applock.files.entity.HideVideoExt;
import com.cleanwiz.applock.service.GroupVideoService;
import com.cleanwiz.applock.service.VideoService;

import java.util.List;

/**
 * Created by dev on 2015/4/29.
 */
public class VideoHideActivity extends BaseHideActivity implements BaseHideAdapter.OnListener {
    protected static final String TAG = "VideoHideActivity";

    protected VideoService mVideoService;
    protected GroupVideoService mGroupVideoService;

    @Override
    protected void initUI() {
        super.initUI();
        setTitleRID(
                R.string.video_preview_title,
                R.string.video_preview_title_edit);

        mFile_bottom_txt_tips.setText(R.string.file_hide_txt_add_video);
        rid_string_type = R.string.video_preview;
    }

    void initAdapter() {
        AdapterView adapterView = (AdapterView) findViewById(R.id.hide_view_list);

        mGroupVideoService = new GroupVideoService(this);
        mVideoService = new VideoService(this);

        mBaseHideAdapter = new VideoHideAdapter(this, this);
        adapterView.setAdapter(mBaseHideAdapter);
    }

    boolean delFolder() {
        return false;
    }

    /**
     * 添加文件夹
     */
    void addFolder() {

    }

    @Override
    public void addFile() {
        // 隐藏图片
        Intent intent = new Intent(VideoHideActivity.this, VideoPreViewActivity.class);
        intent.putExtra("beyondGroupId", mBaseHideAdapter.getGruopID());
        startActivity(intent);
    }

    @Override
    protected void recoveryFiles() {
        // 移除图片
        List<HideVideoExt> list = (List<HideVideoExt>) mBaseHideAdapter.getHitFiles();
        for (HideVideoExt imageModelView : list) {
            mVideoService.unHideVideo(imageModelView);
        }
    }

    @Override
    protected void delFiles() {
        List<HideVideoExt> list = (List<HideVideoExt>) mBaseHideAdapter.getHitFiles();
        for (HideVideoExt imageModelView : list) {
            mVideoService.deleteAudioByPath(imageModelView);
        }
    }

    protected void openHolder(int groupID) {
        List<GroupVideo> groupVideoList = mGroupVideoService.getGroupFiles(groupID);
        List<HideVideo> list = mVideoService.getHideVideos(groupID);

        mBaseHideAdapter.setHitFiles(groupVideoList, list, groupID);
        setHasData(groupVideoList, list);
    }

    @Override
    public void openHolder(Object object) {
        GroupVideo data = (GroupVideo) object;

        int groupID = BaseHideAdapter.ROOT_FOLDER;
        if (data != null)
            groupID = data.getId().intValue();

        openHolder(groupID);
    }
}
