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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.files.entity.ImageModelExt;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.util.List;

/**
 * 图片预览适配器
 * Created by dev on 2015/4/28.
 */
public class PicPreViewAdapter extends BasePreViewAdapter {

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    DisplayImageOptions options;        // 显示图片的设置

    /**
     * @param context
     * @param onListern
     * @param fileList
     * @param itemSize
     */
    public PicPreViewAdapter(Context context, OnListener onListern, List<?> fileList, int itemSize) {
        super(context, onListern, fileList);

        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.default_picture)          // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.default_picture)  // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.default_picture)       // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)                          // 设置下载的图片是否缓存在SD卡中
                .build();                                   // 创建配置过得DisplayImageOption对象
        params = new AbsListView.LayoutParams(itemSize, itemSize);
    }

    /**
     * 普通宽高
     */
    private AbsListView.LayoutParams params;

    @Override
    public void clear() {
        if (imageLoader != null) {
            imageLoader.stop();
            imageLoader = null;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            PicHolder fileHolder = null;
            convertView = mInflater.inflate(R.layout.item_file_hide_pic, null);

            fileHolder = new PicHolder();
            fileHolder.mItem_file_ok = convertView.findViewById(R.id.item_file_ok);
            fileHolder.mImg_pre_preview = (ImageView) convertView.findViewById(R.id.img_pre_preview);
            fileHolder.mItem_file_pic = convertView.findViewById(R.id.item_file_pic);
            convertView.setTag(fileHolder);
            convertView.setLayoutParams(params);
        }

        initView(convertView, position);
        return convertView;
    }

    protected void initView(View view, int position) {
        final ImageModelExt imageModelExt;
        final PicHolder picHolder = (PicHolder) view.getTag();

        ImageModelExt imageModel = (ImageModelExt) getItem(position);
        picHolder.mImg_pre_preview.setImageBitmap(null);
        picHolder.mData = imageModelExt = (ImageModelExt) getItem(position);

        picHolder.mItem_file_ok.setVisibility(imageModelExt.isEnable() ? View.VISIBLE : View.GONE);

        imageLoader.displayImage(
                ImageDownloader.Scheme.THUMBNAIL.wrap(imageModel.getPath()),
                picHolder.mImg_pre_preview,
                options);

        picHolder.mItem_file_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageModelExt.setEnable(!imageModelExt.isEnable());
                picHolder.mItem_file_ok.setVisibility(imageModelExt.isEnable() ? View.VISIBLE : View.GONE);
                updateSelect();
            }
        });
    }

    class PicHolder {
        View mItem_file_pic;
        View mItem_file_ok;//选中样式
        ImageView mImg_pre_preview;//图片容器
        Object mData;
    }
}
