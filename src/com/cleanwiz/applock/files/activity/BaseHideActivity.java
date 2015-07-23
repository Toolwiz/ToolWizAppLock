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
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.files.adapter.BaseHideAdapter;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.ui.activity.LockMainActivity;
import com.cleanwiz.applock.ui.widget.actionview.ActionView;
import com.cleanwiz.applock.ui.widget.actionview.BackAction;
import com.cleanwiz.applock.ui.widget.actionview.CloseAction;
import com.gc.materialdesign.views.CheckBox;
import com.gc.materialdesign.widgets.Dialog;

import java.util.List;

/**
 * Created by dev on 2015/4/30.
 */
public abstract class BaseHideActivity extends BaseActivity implements BaseHideAdapter.OnListener {

    protected static final String TAG = "BaseHideActivity";
    static final float ALPHA_DISABLE = 0.3f;
    static final float ALPHA_ENABLE = 1.0f;

    //按钮图层容器（切换状态）
    protected View mPic_hide_btn_preview;
    protected View mPic_hide_btn_edit;

    protected BaseHideAdapter mBaseHideAdapter;
    protected ActionView mBtn_back;
    protected CheckBox mCheckBox;//全选按钮
    protected View mHide_btn_add;

    protected View mPic_hide_img_recovery;//恢复选中的内容
    protected View mPic_hide_img_del;//彻底删除选中的内容

    protected TextView mFile_hide_txt_title;//标题头
    protected TextView mFile_bottom_txt_tips;//底部提示添加内容
    protected View mFile_bottom_layout_tips;//底部提示添加内容

    private int mRid_title_txt;
    private int mRid_title_txt_edit;
    /***/
    protected int rid_string_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initAdapter();
    }

    @Override
    protected void onDestroy() {
        if (mBaseHideAdapter != null) {
            mBaseHideAdapter.clear();
        }
        super.onDestroy();
    }

    abstract void initAdapter();

    /**
     * 初始化UI设置
     */
    protected void initUI() {
        setContentView(R.layout.activity_file_hide);
        setUI();
    }

    protected void setUI() {
        mPic_hide_btn_preview = findViewById(R.id.pic_hide_btn_preview);
        mPic_hide_btn_edit = findViewById(R.id.pic_hide_btn_edit);
        mBtn_back = (ActionView) findViewById(R.id.btn_back);
        mHide_btn_add = findViewById(R.id.hide_btn_add);

        mPic_hide_img_recovery = findViewById(R.id.pic_hide_img_recovery);
        mPic_hide_img_del = findViewById(R.id.pic_hide_img_del);
        mFile_hide_txt_title = (TextView) findViewById(R.id.file_hide_txt_title);
        mFile_bottom_txt_tips = (TextView) findViewById(R.id.file_bottom_txt_tips);
        mFile_bottom_layout_tips = findViewById(R.id.file_bottom_layout_tips);

        mCheckBox = (CheckBox) findViewById(R.id.item_file_checkbox);
        if (mCheckBox != null)
            mCheckBox.setOncheckListener(new CheckBox.OnCheckListener() {
                @Override
                public void onCheck(boolean check) {
                    CheckBox checkBox = (CheckBox) findViewById(R.id.item_file_checkbox);
                    selectAll(checkBox.isCheck());
                }
            });
    }

    /**
     * 设置资源id
     *
     * @param rid
     */
    protected void setTitleRID(int rid, int ridEdit) {
        mRid_title_txt = rid;
        mRid_title_txt_edit = ridEdit;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setEditState(false);
        mCheckBox.setCheckedNoAnim(false);
        setSelect(false);
        openHolder();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hide_btn_add://添加新文件
                addFile();
                break;

            case R.id.item_file_checkbox://全选/反选
                selectAll(mCheckBox.isCheck());
                break;

            case R.id.btn_back://返回
                if (!onBack()) {
                    finish();
                    onHome();
                }
                break;

            case R.id.pic_hide_img_recovery://恢复选中的内容
                if (v.getAlpha() == ALPHA_ENABLE) {
                    recoveryDialog();
                }
                break;

            case R.id.pic_hide_img_del://彻底删除选中的内容
                if (v.getAlpha() == ALPHA_ENABLE) {
                    delDialog();
                }
                break;

            case R.id.pic_hide_btn_preview://预览状态切换到编辑状态
                if (mPic_hide_btn_preview.getAlpha() == ALPHA_ENABLE) {
                    setEditState(true);
                }
                break;

            case R.id.pic_hide_btn_edit://切换编辑状态
                setEditState(false);
                break;
        }
    }

    protected void delDialog() {

        final Dialog dialog = new Dialog(
                this,
                ((getString(R.string.file_dialog_del)) + getString(rid_string_type)),
                getString(rid_string_type) + getString(R.string.file_dialog_del_missage)
        );

        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delFiles();
                setEditState(false);
                openHolder();
            }
        });

        dialog.addCancelButton(
                getString(R.string.lock_cancel),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

        dialog.show();
        dialog.getButtonAccept().setText(getString(R.string.lock_ok));
    }

    /**
     * 恢复选中内容
     */
    protected void recoveryDialog() {
        final Dialog dialog = new Dialog(
                this,
                ((getString(R.string.file_dialog_recovery)) + getString(rid_string_type)),
                getString(rid_string_type) + getString(R.string.file_dialog_recovery_missage)
        );

        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recoveryFiles();
                setEditState(false);
                openHolder();
            }
        });

        dialog.addCancelButton(
                getString(R.string.lock_cancel),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

        dialog.show();
        dialog.getButtonAccept().setText(getString(R.string.lock_ok));
    }

    /**
     * 彻底删除选中的文件
     */
    protected abstract void delFiles();

    /**
     * 全选切换
     *
     * @param enable
     */
    protected void selectAll(boolean enable) {
        mBaseHideAdapter.selectAll(enable);
    }

    /**
     * 返回主页面
     */
    private void onHome() {
        this.startActivity(new Intent(this, LockMainActivity.class));
    }

    /**
     * 添加文件夹
     */
    abstract void addFolder();

    /**
     * 删除文件夹（已选中的）同时恢复文件夹内的所有文件
     *
     * @return
     */
    abstract boolean delFolder();

    /**
     * 恢复隐藏的文件
     */
    abstract void recoveryFiles();

    /**
     * 添加新内容
     */
    abstract void addFile();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (onBack())
                return true;
        }

        onHome();
        return true;
    }

    /**
     * 返回/退出页面
     *
     * @return 是否停留
     */
    protected boolean onBack() {
        if (!mBaseHideAdapter.isRoot()) {
            openHolder(mBaseHideAdapter.getGruopParentID());
            return true;
        }

        // 切换为返回状态
        if (mBtn_back.getAction() instanceof CloseAction) {
            setEditState(false);
            return true;
        }
        return false;
    }

    /**
     * 重新初始化列表数据
     */
    protected void openHolder() {
        openHolder(mBaseHideAdapter.getGruopID());
    }

    /**
     * 打开指定分组内容
     *
     * @param groupID
     */
    abstract void openHolder(int groupID);

    /**
     * 是否编辑状态
     */
    private boolean mIsEdit;

    /**
     * 设置当前状态
     *
     * @param isEdit （编辑/预览）
     */
    protected void setEditState(boolean isEdit) {
        mIsEdit = isEdit;
        mBaseHideAdapter.setEditState(isEdit);
        if (isEdit) {
            mPic_hide_btn_preview.setVisibility(View.GONE);
            mPic_hide_btn_edit.setVisibility(View.VISIBLE);
            mBtn_back.setAction(new CloseAction(), ActionView.ROTATE_COUNTER_CLOCKWISE);

            mHide_btn_add.setVisibility(View.GONE);

            mFile_hide_txt_title.setText("");
        } else {
            mPic_hide_btn_preview.setVisibility(View.VISIBLE);
            mPic_hide_btn_edit.setVisibility(View.GONE);
            mBtn_back.setAction(new BackAction(), ActionView.ROTATE_CLOCKWISE);

            mHide_btn_add.setVisibility(View.VISIBLE);
            mFile_hide_txt_title.setText(mRid_title_txt);
        }
    }

    public boolean isEditState() {
        return mIsEdit;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // 恢复
        boolean save = savedInstanceState.getBoolean("save");
        if (save) {
            int gruopID = savedInstanceState.getInt("groupID");
            mBaseHideAdapter.clear();
            openHolder(gruopID);
        } else
            openHolder();
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // 保存信息
        boolean save = mBaseHideAdapter.isRoot();
        outState.putBoolean("save", !mBaseHideAdapter.isRoot());

        if (save) {
            outState.putInt("groupID", mBaseHideAdapter.getGruopID());
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLongClick(BaseHideAdapter.IEnable iEnable) {
        setEditState(true);
        mBaseHideAdapter.setSelect(iEnable);
        setSelect(true);
    }

    public void setSelect(boolean selected) {
        if (selected) {
            mPic_hide_img_recovery.setAlpha(ALPHA_ENABLE);
            mPic_hide_img_del.setAlpha(ALPHA_ENABLE);
        } else {
            mPic_hide_img_recovery.setAlpha(ALPHA_DISABLE);
            mPic_hide_img_del.setAlpha(ALPHA_DISABLE);
        }
    }

    /**
     * 设置是否有数据
     *
     * @param groupList
     * @param list
     */
    protected void setHasData(List<?> groupList, List<?> list) {
        boolean hasData = false;
        if (groupList != null && groupList.size() > 0 ||
                list != null && list.size() > 0)
            hasData = true;

        if (hasData) {
            mPic_hide_btn_preview.setAlpha(ALPHA_ENABLE);
            mFile_bottom_layout_tips.setVisibility(View.GONE);
        } else {
            mPic_hide_btn_preview.setAlpha(ALPHA_DISABLE);
            if (mIsEdit)
                mFile_bottom_layout_tips.setVisibility(View.GONE);
            else
                mFile_bottom_layout_tips.setVisibility(View.VISIBLE);
        }
    }
}
