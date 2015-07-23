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
import com.cleanwiz.applock.data.GroupFile;
import com.cleanwiz.applock.data.HideFile;
import com.cleanwiz.applock.files.adapter.BaseHideAdapter;
import com.cleanwiz.applock.files.adapter.FileHideAdapter;
import com.cleanwiz.applock.files.entity.HideFileExt;
import com.cleanwiz.applock.service.FileService;
import com.cleanwiz.applock.service.GroupFileService;

import java.util.List;

/**
 * 隐私文件
 * Created by dev on 2015/4/29.
 */
public class FileHideActivity extends BaseHideActivity implements FileHideAdapter.OnListener {

    protected static final String TAG = "FileHideActivity";

    protected FileService mFileService;
    protected GroupFileService mGroupFileService;

    @Override
    protected void initUI() {
        super.initUI();
        setTitleRID(
                R.string.file_preview_title,
                R.string.file_preview_title_edit);

        mFile_bottom_txt_tips.setText(R.string.file_hide_txt_add_file);

        rid_string_type = R.string.file_preview;
    }

    void initAdapter() {
        AdapterView adapterView = (AdapterView) findViewById(R.id.hide_view_list);


        mGroupFileService = new GroupFileService(this);
        mFileService = new FileService(this);

        mBaseHideAdapter = new FileHideAdapter(this, this);
        adapterView.setAdapter(mBaseHideAdapter);
    }

    /**
     * 删除文件
     *
     * @return
     */
    boolean delFolder() {
        List<HideFileExt> list = (List<HideFileExt>) mBaseHideAdapter.getHitFiles();
        for (HideFileExt imageModelView : list) {
            mFileService.unHideFile(imageModelView);
            mFileService.deleteAudioByPath(imageModelView.getOldPathUrl());
        }
        return false;
    }

    /**
     * 添加文件夹
     */
    void addFolder() {

    }

    /**
     * 添加新内容
     */
    public void addFile() {
        // 隐藏图片
        Intent intent = new Intent(FileHideActivity.this, FilePreViewActivity.class);
        intent.putExtra("beyondGroupId", mBaseHideAdapter.getGruopID());
        startActivity(intent);
    }

    protected void recoveryFiles() {
        // 移除图片
        List<HideFileExt> list = (List<HideFileExt>) mBaseHideAdapter.getHitFiles();
        for (HideFileExt imageModelView : list) {
            mFileService.unHideFile(imageModelView);
        }
    }

    @Override
    protected void delFiles() {
        List<HideFileExt> list = (List<HideFileExt>) mBaseHideAdapter.getHitFiles();
        for (HideFileExt hideFileExt : list) {
            mFileService.unHideFile(hideFileExt);
            mFileService.deleteAudioByPath(hideFileExt.getOldPathUrl());
        }
    }

    protected void openHolder(int groupID) {
        List<GroupFile> groupList = mGroupFileService.getGroupFiles(groupID);
        List<HideFile> list = mFileService.getHideFiles(groupID);

        mBaseHideAdapter.setHitFiles(groupList, list, groupID);
        setHasData(groupList, list);
    }

    @Override
    public void openHolder(Object object) {

        GroupFile data = (GroupFile) object;

        int groupID = BaseHideAdapter.ROOT_FOLDER;
        if (data != null)
            groupID = data.getId().intValue();

        openHolder(groupID);
    }
}
