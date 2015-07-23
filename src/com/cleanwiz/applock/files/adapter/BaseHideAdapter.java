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

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
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
 * 隐私文件列表 适配器 Created by dev on 2015/4/30.
 */
public abstract class BaseHideAdapter extends BaseAdapter {
    /**
     * 根目录文件夹默认ID值
     */
    public static final int ROOT_FOLDER = -1;

    protected OnListener mOnListern;

    protected LayoutInflater mInflater;
    protected Context context;

    /**
     * 父容器id
     */
    private GroupInfo groupInfo;

    protected List<?> mList_Group;
    protected List<?> mList_HideFile;

    protected boolean edit;

    public void selectAll(boolean enable) {
        if (mList_HideFile != null)
            for (Object object : mList_HideFile) {
                ((IEnable) object).setEnable(enable);
            }
        mOnListern.setSelect(enable);
        notifyDataSetChanged();
    }

    public interface OnListener {
        /**
         * 打开当前文件夹下所有内容
         *
         * @param groupImage （组相关数据包）
         */
        void openHolder(Object groupImage);

        /**
         * 设置选中内容状态切换
         *
         * @param selected
         */
        void setSelect(boolean selected);

        void onLongClick(BaseHideAdapter.IEnable iEnable);
    }

    protected void updateSelect() {
        for (Object object : mList_HideFile) {
            if (((IEnable) object).isEnable()) {
                mOnListern.setSelect(true);
                return;
            }
        }
        mOnListern.setSelect(false);
    }

    /**
     * 设置默认选中的项
     *
     * @param iEnable
     */
    public void setSelect(BaseHideAdapter.IEnable iEnable) {
        iEnable.setEnable(true);
        notifyDataSetChanged();
    }

    public BaseHideAdapter(Context context, OnListener onListern) {

        this.context = context;
        mInflater = LayoutInflater.from(this.context);
        this.mOnListern = onListern;

        clear();
    }

    public void clear() {
        mList_Group = null;
        mList_HideFile = null;
        groupInfo = null;
        edit = false;
    }

    /**
     * 是否在编辑状态
     *
     * @return
     */
    public boolean isEdit() {
        return edit;
    }

    public void setEditState(boolean edit) {
        this.edit = edit;
        selectAll(false);
        notifyDataSetChanged();
    }

    /**
     * 获取当前组父组ID（当前支持两级）
     *
     * @return
     */
    public int getGruopParentID() {
        if (groupInfo != null)
            return groupInfo.parentID;
        else
            return BaseHideAdapter.ROOT_FOLDER;
    }

    /**
     * 判断是否为根目录
     *
     * @return
     */
    public boolean isRoot() {
        if (groupInfo != null
                && groupInfo.groupID != BaseHideAdapter.ROOT_FOLDER)
            return false;
        return true;
    }

    /**
     * 获取当前组ID
     *
     * @return
     */
    public int getGruopID() {
        if (groupInfo != null)
            return groupInfo.groupID;
        return BaseHideAdapter.ROOT_FOLDER;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (mList_Group != null)
            count += mList_Group.size();
        if (mList_HideFile != null)
            count += mList_HideFile.size();
        return count;
    }

    @Override
    public Object getItem(int position) {
        if (mList_Group != null && mList_Group.size() > position) {
            return mList_Group.get(position);
        }

        if (mList_HideFile != null)
            return mList_HideFile.get(position - mList_Group.size());
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            HideHolder fileHolder = null;
            convertView = mInflater.inflate(R.layout.item_file_hide, null);

            fileHolder = new HideHolder();
            fileHolder.mImgPreview = (ImageView) convertView.findViewById(R.id.img_pre_preview);
            fileHolder.mTextView = (TextView) convertView.findViewById(R.id.pre_preView_txt);
            fileHolder.mTV_detail = (TextView) convertView.findViewById(R.id.tv_detail);
            fileHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.item_file_checkbox);
            fileHolder.mFileHideItem = convertView.findViewById(R.id.file_hide_layout_item);

            convertView.setTag(fileHolder);
        }

        initView(convertView, position);
        return convertView;
    }

    /**
     * 震动服务
     *
     * @param context
     */
    protected void doVibrator(Context context) {

        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(100);//只震动一秒，一次
    }

    abstract void initView(View view, int position);

    /**
     * 设置分组数据
     *
     * @param groupImageViews
     * @param fileList
     * @param groupID         当前组ID
     */
    public abstract void setHitFiles(List<?> groupImageViews, List<?> fileList,
                                     int groupID);


    /**
     * 新加入的组
     *
     * @param groupID
     */
    protected void setGroup(int groupID) {
        if (groupInfo == null) {
            groupInfo = new GroupInfo();
            groupInfo.parentID = BaseHideAdapter.ROOT_FOLDER;
        }
        groupInfo.groupID = groupID;
    }

    /**
     * 获取选中的隐私文件
     *
     * @return
     */
    public List<?> getHitFiles() {
        List<Object> list = new ArrayList<Object>();
        for (Object object : mList_HideFile) {
            if (((IEnable) object).isEnable())
                list.add(object);
        }
        return list;
    }

    /**
     * 获取选中的个数
     *
     * @return
     */
    private int getHitFilesCout() {
        int count = 0;
        if (mList_HideFile != null)
            for (Object object : mList_HideFile) {
                if (((IEnable) object).isEnable())
                    count++;
            }
        return count;
    }

    /**
     * 获取选中的文件夹
     *
     * @return
     */
    public List<?> getGroupFiles() {
        List<Object> list = new ArrayList<Object>();
        for (Object iGroup : mList_Group) {
            if (((IEnable) iGroup).isEnable())
                list.add(iGroup);
        }
        return list;
    }

    /**
     * Created by dev on 2015/4/29.
     */
    public interface IEnable {
        boolean isEnable();

        void setEnable(boolean enable);
    }

    /**
     * Created by dev on 2015/4/30.
     */
    public interface IGroup {

        Long getId();

        Integer getParentId();
    }

    class GroupInfo {
        public int parentID;
        public int groupID;
    }

    class HideHolder {
        View mFileHideItem;
        ImageView mImgPreview;
        TextView mTextView;// 主内容
        TextView mTV_detail;// 辅助内容（可能没有）
        CheckBox mCheckBox;//选中
    }
}
