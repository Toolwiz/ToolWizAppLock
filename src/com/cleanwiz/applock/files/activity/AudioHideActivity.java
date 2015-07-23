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
import com.cleanwiz.applock.data.GroupAudio;
import com.cleanwiz.applock.data.HideAudio;
import com.cleanwiz.applock.files.adapter.AudioHideAdapter;
import com.cleanwiz.applock.files.adapter.BaseHideAdapter;
import com.cleanwiz.applock.files.entity.HideAudioExt;
import com.cleanwiz.applock.service.AudioService;
import com.cleanwiz.applock.service.GroupAudioService;

import java.util.List;

/**
 * Created by dev on 2015/4/29.
 */
public class AudioHideActivity extends BaseHideActivity implements BaseHideAdapter.OnListener {
    protected static final String TAG = "AudioHideActivity";

    protected AudioService mAudioService;
    protected GroupAudioService mGroupAudioService;

    @Override
    protected void initUI() {
        super.initUI();
        setTitleRID(
                R.string.audio_preview_title,
                R.string.audio_preview_title_edit);

        mFile_bottom_txt_tips.setText(R.string.file_hide_txt_add_audio);

        rid_string_type = R.string.audio_preview;
    }

    void initAdapter() {
        AdapterView gridView = (AdapterView) findViewById(R.id.hide_view_list);

        mAudioService = new AudioService(this);
        mGroupAudioService = new GroupAudioService(this);

        mBaseHideAdapter = new AudioHideAdapter(this, this);
        gridView.setAdapter(mBaseHideAdapter);
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
        Intent intent = new Intent(AudioHideActivity.this, AudioPreViewActivity.class);
        intent.putExtra("beyondGroupId", mBaseHideAdapter.getGruopID());
        startActivity(intent);
    }

    @Override
    protected void delFiles() {
        List<HideAudioExt> list = (List<HideAudioExt>) mBaseHideAdapter.getHitFiles();
        for (HideAudioExt hideAudioExt : list) {
            mAudioService.deleteAudioByPath(hideAudioExt);
        }
    }

    protected void recoveryFiles() {
        List<HideAudioExt> list = (List<HideAudioExt>) mBaseHideAdapter.getHitFiles();
        for (HideAudioExt imageModelView : list) {
            mAudioService.unHideAudio(imageModelView);
        }
    }

    protected void openHolder(int groupID) {
        List<GroupAudio> groupList = mGroupAudioService.getGroupFiles(groupID);
        List<HideAudio> list = mAudioService.getHideAudios(groupID);

        mBaseHideAdapter.setHitFiles(groupList, list, groupID);
        setHasData(groupList, list);
    }

    @Override
    public void openHolder(Object object) {

        GroupAudio data = (GroupAudio) object;

        int groupID = BaseHideAdapter.ROOT_FOLDER;
        if (data != null)
            groupID = data.getId().intValue();

        openHolder(groupID);
    }
}
