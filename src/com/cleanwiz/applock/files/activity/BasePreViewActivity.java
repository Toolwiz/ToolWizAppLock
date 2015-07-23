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

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.files.adapter.BaseHideAdapter;
import com.cleanwiz.applock.files.adapter.BasePreViewAdapter;
import com.cleanwiz.applock.ui.BaseActivity;

/**
 * Created by dev on 2015/4/30.
 */
public abstract class BasePreViewActivity extends BaseActivity implements BasePreViewAdapter.OnListener {

    /**
     * 当前选中的文件夹ID标识
     */
    protected long mBeyondGroupId;
    private View mPreview_btn_hide;//确定按钮
    private TextView mFile_hide_txt_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initAdapter();
        initListener();
    }

    protected void initListener() {
        mPreview_btn_hide = findViewById(R.id.preview_btn_hide);
        mFile_hide_txt_title = (TextView) findViewById(R.id.file_hide_txt_title);
    }

    abstract void initAdapter();

    abstract void initUI();

    @Override
    protected void onStart() {
        super.onStart();
        mBeyondGroupId = getIntent().getIntExtra("beyondGroupId", BaseHideAdapter.ROOT_FOLDER);
        setSelect(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.preview_btn_hide:
                if (mPreview_btn_hide.getAlpha() == BaseHideActivity.ALPHA_ENABLE) {
                    hideFiles();

                    //延迟执行
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            finish();
                        }
                    }, 100);
                }
                break;
            case R.id.btn_back:
                onBack();
        }
        super.onClick(v);
    }

    /**
     * 按返回键
     */
    protected boolean onBack() {
        finish();
        return true;
    }

    /**
     * 隐藏当前选中项
     */
    abstract void hideFiles();

    protected void setTitleRID(int titleRID) {
        mFile_hide_txt_title.setText(titleRID);
    }

    @Override
    public void setSelect(boolean selected) {
        if (selected) {
            mPreview_btn_hide.setAlpha(BaseHideActivity.ALPHA_ENABLE);
        } else {
            mPreview_btn_hide.setAlpha(BaseHideActivity.ALPHA_DISABLE);
        }
    }
}
