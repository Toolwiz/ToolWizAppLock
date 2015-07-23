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
import com.cleanwiz.applock.files.adapter.PicPreViewAdapter;
import com.cleanwiz.applock.files.entity.ImageModelExt;
import com.cleanwiz.applock.files.widget.BGridView;
import com.cleanwiz.applock.model.ImageModel;
import com.cleanwiz.applock.service.ImageService;
import com.gc.materialdesign.views.CheckBox;

import java.util.List;

/**
 * 图片文件夹预览（寻找系统图片插入）
 * Created by dev on 2015/4/28.
 */
public class PicPreViewActivity extends BasePreViewActivity {

    private ImageService mImageService;
    private PicPreViewAdapter mPicPreViewAdapter;

    /**
     * item大小
     */
    private int itemSize;

    @Override
    void initAdapter() {
        mImageService = new ImageService(this);
        AdapterView adapterView = (AdapterView) findViewById(R.id.hide_view_list);

        mPicPreViewAdapter = new PicPreViewAdapter(this, this, null, itemSize);
        adapterView.setAdapter(mPicPreViewAdapter);
        mPicPreViewAdapter.setPreViewFiles(
                ImageModelExt.transList(
                        (List<ImageModel>) mImageService.getList()
                ));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPicPreViewAdapter.clear();
    }

    @Override
    void initUI() {
        setContentView(R.layout.activity_file_preview_group);
        setGridView();
    }

    /**
     * 设置分组列表间距
     */
    private void setGridView() {
//        layout_marginRight
        BGridView gridView = (BGridView) findViewById(R.id.hide_view_list);
        itemSize = gridView.setGridView(getWindowManager(), 4, 4);
    }

    @Override
    protected void initListener() {
        super.initListener();
        setTitleRID(R.string.pic_preview_title_add);

        CheckBox item_file_checkbox_all = (CheckBox) findViewById(R.id.item_file_checkbox_all);
        item_file_checkbox_all.setOncheckListener(new CheckBox.OnCheckListener() {
            @Override
            public void onCheck(boolean check) {
                mPicPreViewAdapter.selectAll(check);
            }
        });
    }

    @Override
    void hideFiles() {
        // 隐藏图片
        List<ImageModelExt> list = (List<ImageModelExt>) mPicPreViewAdapter.getEnablePreViewFiles();
        for (ImageModelExt imageModelView : list) {
            mImageService.hideImage(imageModelView, (int) mBeyondGroupId);
        }
    }
}
