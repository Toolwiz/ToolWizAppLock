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
package com.cleanwiz.applock.files.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.cleanwiz.applock.R;
import com.gc.materialdesign.views.CheckBox;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件预览适配器 Created by dev on 2015/4/30.
 */
public abstract class BasePreViewAdapter extends BaseAdapter {

    protected LayoutInflater mInflater;
    protected Context context;
    protected List<?> mFileList;

    public BasePreViewAdapter(Context context, OnListener onListern, List<?> fileList) {
        this.context = context;
        this.mOnListern = onListern;
        this.mFileList = fileList;
        mInflater = LayoutInflater.from(this.context);
    }

    public void clear() {
    }

    protected void updateSelect() {
        for (Object object : mFileList) {
            if (((BaseHideAdapter.IEnable) object).isEnable()) {
                mOnListern.setSelect(true);
                return;
            }
        }

        mOnListern.setSelect(false);
    }

    /**
     * 获取选中的项
     *
     * @return
     */
    public List<?> getEnablePreViewFiles() {
        List<BaseHideAdapter.IEnable> list = new ArrayList<BaseHideAdapter.IEnable>();
        for (Object object : mFileList) {
            if (((BaseHideAdapter.IEnable) object).isEnable())
                list.add((BaseHideAdapter.IEnable) object);
        }
        return list;
    }

    /**
     * 重新设置数据
     *
     * @param list
     */
    public void setPreViewFiles(List<?> list) {
        mFileList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mFileList != null) {
            return mFileList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (mFileList != null) {
            return mFileList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            FilePreViewHolder fileHolder = null;
            convertView = mInflater.inflate(getLayoutID(), null);

            fileHolder = new FilePreViewHolder();
            fileHolder.mImgPreview = (ImageView) convertView.findViewById(R.id.img_pre_preview);
            fileHolder.mTextView = (TextView) convertView.findViewById(R.id.pre_preView_txt);
            fileHolder.mTV_detail = (TextView) convertView.findViewById(R.id.tv_detail);
            fileHolder.mItem_file_linear = convertView.findViewById(R.id.file_hide_layout_item);
            fileHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.item_file_checkbox);
            convertView.setTag(fileHolder);
        }

        initView(convertView, position);
        return convertView;
    }

    /**
     * 获取布局容器
     *
     * @return
     */
    protected int getLayoutID() {
        return R.layout.item_file_hide;
    }

    /**
     * 设置具体UI
     *
     * @param view
     * @param position
     */
    abstract protected void initView(View view, int position);

    /**
     * 切换全选状态
     *
     * @param selected
     */
    public void selectAll(boolean selected) {
        for (Object object : mFileList) {
            ((BaseHideAdapter.IEnable) object).setEnable(selected);
        }
        mOnListern.setSelect(selected);
        notifyDataSetChanged();
    }

    class FilePreViewHolder {
        ImageView mImgPreview;
        TextView mTextView;
        TextView mTV_detail;// 辅助内容（可能没有）
        CheckBox mCheckBox;//选中按钮
        View mItem_file_linear;
        Object mObject;
    }

    protected OnListener mOnListern;

    public interface OnListener {

        /**
         * 设置选中内容状态切换
         *
         * @param selected
         */
        void setSelect(boolean selected);
    }
}
