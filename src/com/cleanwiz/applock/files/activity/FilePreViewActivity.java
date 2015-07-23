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

import android.util.Log;
import android.view.KeyEvent;
import android.widget.AdapterView;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.files.adapter.FilePreViewAdapter;
import com.cleanwiz.applock.files.entity.FileModelExt;
import com.cleanwiz.applock.model.FileModel;
import com.cleanwiz.applock.service.FileService;
import com.cleanwiz.applock.utils.FileUtil;
import com.gc.materialdesign.views.CheckBox;

import java.io.File;
import java.util.List;

/**
 * Created by dev on 2015/4/29.
 */
public class FilePreViewActivity extends BasePreViewActivity implements FilePreViewAdapter.OnFolder {
    private static final String TAG = "FilePreViewActivity";

    private FileService mFileService;
    private FilePreViewAdapter mFilePreViewAdapter;

    /**
     * SD卡根路径
     */
    private static String SD_URL = FileUtil.getSDPath();

    @Override
    void initAdapter() {
        mFileService = new FileService(this);
        AdapterView adapterView = (AdapterView) findViewById(R.id.hide_view_list);

        mFilePreViewAdapter = new FilePreViewAdapter(this, this, null);
        adapterView.setAdapter(mFilePreViewAdapter);

        openFolder();
    }

    @Override
    void initUI() {
        setContentView(R.layout.activity_file_preview);
    }

    @Override
    protected void initListener() {
        super.initListener();

        setTitleRID(R.string.file_preview_title_add);

        CheckBox item_file_checkbox_all = (CheckBox) findViewById(R.id.item_file_checkbox_all);
        item_file_checkbox_all.setOncheckListener(new CheckBox.OnCheckListener() {
            @Override
            public void onCheck(boolean check) {
                mFilePreViewAdapter.selectAll(check);
            }
        });
    }

    @Override
    void hideFiles() {
        // 隐藏图片
        List<FileModelExt> list = (List<FileModelExt>) mFilePreViewAdapter.getEnablePreViewFiles();
        for (FileModelExt imageModelView : list) {
            mFileService.hideFile(imageModelView, (int) mBeyondGroupId);
        }
    }

    /**
     * 当前所在的文件夹信息（没人默认为空）
     */
    private File mFile;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (onBack())
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 返回/退出页面
     *
     * @return 是否停留
     */
    @Override
    protected boolean onBack() {
        if (mFile != null && !mFile.getPath().equals(FilePreViewActivity.SD_URL)) {
            openFolder(mFile.getParent());
            return true;
        }
        finish();
        return false;
    }

    @Override
    public void openFolder(FileModel fileModel) {
        if (fileModel != null)
            openFolder(fileModel.getPath());
        else
            openFolder(FilePreViewActivity.SD_URL);
    }

    public void openFolder() {
        openFolder(FilePreViewActivity.SD_URL);
    }

    /**
     * 打开某个文件夹
     *
     * @param url
     */
    public void openFolder(String url) {
        Log.i(TAG, "url: " + url);
        openFolder(new File(url));
    }

    public void openFolder(File file) {
        mFile = file;

        List<FileModel> list = mFileService.getFilesByDir(file.getPath());
        if (list != null && list.size() > 0) {
            List<FileModelExt> list2 = FileModelExt.transList(list);
            if (list2.size() > 0)
                mFilePreViewAdapter.setPreViewFiles(list2);
        }
    }
}
