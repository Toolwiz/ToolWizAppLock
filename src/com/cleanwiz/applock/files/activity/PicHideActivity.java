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
import com.cleanwiz.applock.data.GroupImage;
import com.cleanwiz.applock.data.HideImage;
import com.cleanwiz.applock.files.adapter.BaseHideAdapter;
import com.cleanwiz.applock.files.adapter.PicHideAdapter;
import com.cleanwiz.applock.files.entity.HideImageExt;
import com.cleanwiz.applock.files.widget.BGridView;
import com.cleanwiz.applock.service.GroupImageService;
import com.cleanwiz.applock.service.ImageService;

import java.util.List;

/**
 * 图片预览（私密图片）
 * Created by dev on 2015/4/27.
 */
public class PicHideActivity extends BaseHideActivity implements BaseHideAdapter.OnListener {

    protected static final String TAG = "PicHideActivity";

    protected ImageService mImageService;
    protected GroupImageService mGroupImageService;

    /**
     * item大小
     */
    private int itemSize;

    @Override
    protected void initUI() {
        setContentView(R.layout.activity_file_hide_group);
        setUI();
        setTitleRID(
                R.string.pic_preview_title,
                R.string.pic_preview_title_edit);
        setGridView();

        mFile_bottom_txt_tips.setText(R.string.file_hide_txt_add_pic);

        rid_string_type = R.string.pic_preview;
    }

    /**
     * 设置分组列表间距
     */
    private void setGridView() {

        BGridView gridView = (BGridView) findViewById(R.id.hide_view_list);
        itemSize = gridView.setGridView(getWindowManager(), 4, 4);
    }

    void initAdapter() {
        AdapterView adapterView = (AdapterView) findViewById(R.id.hide_view_list);

        mGroupImageService = new GroupImageService(this);
        mImageService = new ImageService(this);

        mBaseHideAdapter = new PicHideAdapter(this, this, itemSize);
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

    /**
     * 添加新内容
     */
    public void addFile() {
        // 隐藏图片
        Intent intent = new Intent(PicHideActivity.this, PicPreViewActivity.class);
        intent.putExtra("beyondGroupId", mBaseHideAdapter.getGruopID());
        startActivity(intent);
    }

    protected void recoveryFiles() {
        // 移除图片
        List<HideImageExt> list = (List<HideImageExt>) mBaseHideAdapter.getHitFiles();
        for (HideImageExt imageModelView : list) {
            mImageService.unHideImage(imageModelView);
        }
    }

    @Override
    protected void delFiles() {
        List<HideImageExt> list = (List<HideImageExt>) mBaseHideAdapter.getHitFiles();
        for (HideImageExt hideImageExt : list) {
            mImageService.deleteAudioByPath(hideImageExt);
        }
    }

    protected void openHolder(int groupID) {
        List<GroupImage> groupList = mGroupImageService.getGroupFiles(groupID);
        List<HideImage> list = mImageService.getHideImages(groupID);
        mBaseHideAdapter.setHitFiles(groupList, list, groupID);

        setHasData(groupList, list);
    }

    @Override
    public void openHolder(Object object) {

        GroupImage data = (GroupImage) object;

        int groupID = BaseHideAdapter.ROOT_FOLDER;
        if (data != null)
            groupID = data.getId().intValue();

        openHolder(groupID);
    }
}
