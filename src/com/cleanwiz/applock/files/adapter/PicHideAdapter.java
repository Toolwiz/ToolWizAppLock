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
import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.data.GroupImage;
import com.cleanwiz.applock.data.HideImage;
import com.cleanwiz.applock.files.activity.PhotoPreViewActivity;
import com.cleanwiz.applock.files.entity.GroupImageExt;
import com.cleanwiz.applock.files.entity.HideImageExt;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片隐藏适配器
 * Created by dev on 2015/4/28.
 */
public class PicHideAdapter extends BaseHideAdapter {

    private static final String TAG = "PicHideAdapter";
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    DisplayImageOptions options;        // 显示图片的设置

    /**
     * @param context
     * @param onListern
     * @param itemSize  View大小
     */
    public PicHideAdapter(Context context, OnListener onListern, int itemSize) {
        super(context, onListern);
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.default_picture)          // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.default_picture)  // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.default_picture)       // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)                          // 设置下载的图片是否缓存在SD卡中
                .build();

        params = new AbsListView.LayoutParams(itemSize, itemSize);
    }

    /**
     * 普通宽高
     */
    private AbsListView.LayoutParams params;

    @Override
    public void clear() {
        super.clear();
        if (imageLoader != null) {
            imageLoader.stop();
            imageLoader = null;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            PicHolder picHolder = null;
            convertView = mInflater.inflate(R.layout.item_file_hide_pic, null);

            picHolder = new PicHolder();
            picHolder.mItem_file_pic = convertView.findViewById(R.id.item_file_pic);
            picHolder.mItem_file_ok = convertView.findViewById(R.id.item_file_ok);
            picHolder.mImg_pre_preview = (ImageView) convertView.findViewById(R.id.img_pre_preview);
            convertView.setTag(picHolder);
            convertView.setLayoutParams(params);
        }

        initView(convertView, position);
        return convertView;
    }

    protected void initView(View view, final int position) {
        final PicHolder fileHolder = (PicHolder) view.getTag();

        fileHolder.mImg_pre_preview.setImageBitmap(null);
        Object data = getItem(position);
        fileHolder.mData = data;

        if (data instanceof HideImageExt) {
            final HideImageExt hideImageView = (HideImageExt) data;

            imageLoader.displayImage(
                    ImageDownloader.Scheme.THUMBNAIL.wrap(hideImageView.getNewPathUrl()),
                    fileHolder.mImg_pre_preview,
                    options);

            if (edit) {
                // 编辑模式
                fileHolder.mItem_file_ok.setVisibility(hideImageView.isEnable() ? View.VISIBLE : View.GONE);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideImageView.setEnable(!hideImageView.isEnable());
                        fileHolder.mItem_file_ok.setVisibility(hideImageView.isEnable() ? View.VISIBLE : View.GONE);
                        updateSelect();
                    }
                });

                //长按
                view.setOnLongClickListener(null);

            } else {
                // 正常打开模式
                fileHolder.mItem_file_ok.setVisibility(View.GONE);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(context, PhotoPreViewActivity.class);
                        intent.putParcelableArrayListExtra("list", (ArrayList<? extends Parcelable>) mList_HideFile);
                        intent.putExtra("id", position);
                        context.startActivity(intent);
                    }
                });

                //长按监听
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        doVibrator(context);
                        mOnListern.onLongClick(hideImageView);
                        return false;
                    }
                });
            }

        } else if (data instanceof GroupImageExt) {
            final GroupImageExt groupImageView = (GroupImageExt) data;

            fileHolder.mItem_file_ok.setVisibility(View.GONE);
            fileHolder.mImg_pre_preview.setImageResource(R.drawable.folder);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (edit) {
                        // 编辑模式
                        boolean enable = groupImageView.isEnable();
                        PicHolder fileHolder = (PicHolder) v.getTag();
                        groupImageView.setEnable(!enable);
                    } else {
                        // 正常打开模式
                        if (mOnListern != null)
                            mOnListern.openHolder(groupImageView);
                    }
                }
            });
        }
    }

    @Override
    public void setHitFiles(List<?> listGroup, List<?> listFile, int groupID) {

        this.mList_Group = GroupImageExt.transList((List<GroupImage>) listGroup);
        this.mList_HideFile = HideImageExt.transList((List<HideImage>) listFile);

        setGroup(groupID);
        notifyDataSetChanged();
    }

    class PicHolder {
        View mItem_file_pic;
        View mItem_file_ok;//选中样式
        ImageView mImg_pre_preview;//图片容器
        Object mData;
    }
}
