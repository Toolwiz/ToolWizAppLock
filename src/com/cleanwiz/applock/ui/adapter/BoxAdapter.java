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
package com.cleanwiz.applock.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.files.activity.AudioHideActivity;
import com.cleanwiz.applock.files.activity.FileHideActivity;
import com.cleanwiz.applock.files.activity.PicHideActivity;
import com.cleanwiz.applock.files.activity.VideoHideActivity;
import com.cleanwiz.applock.model.SafeBoxMgr;
import com.cleanwiz.applock.service.AudioService;
import com.cleanwiz.applock.service.FileService;
import com.cleanwiz.applock.service.ImageService;
import com.cleanwiz.applock.service.VideoService;

import java.util.List;

public class BoxAdapter extends BaseAdapter {

    private Context mContext;
    private List<SafeBoxMgr.SafeBox> boxList;
    private LayoutInflater mInflater;

    public BoxAdapter(Context mContext) {
        super();
        this.mContext = mContext;
        boxList = new SafeBoxMgr().getSafeBoxList();
        mInflater = LayoutInflater.from(mContext);

        mAudioService = new AudioService(mContext);
        mFileService = new FileService(mContext);
        mImageService = new ImageService(mContext);
        mVideoService = new VideoService(mContext);
    }

    private AudioService mAudioService;
    private FileService mFileService;
    private ImageService mImageService;
    private VideoService mVideoService;

    @Override
    public int getCount() {
        return boxList.size();
    }

    @Override
    public SafeBoxMgr.SafeBox getItem(int position) {
        return boxList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = buildItemView();
        }
        ViewHolder vh = (ViewHolder) convertView.getTag();
        SafeBoxMgr.SafeBox box = getItem(position);
        vh.ivIcon.setImageResource(box.getIconId());
        vh.tvTitle.setText(box.getTitleId());

        int count = 0;
        switch (box.getId()) {
            case SafeBoxMgr.BOX_ID_PIC:
                count = mImageService.getHideImageCount();
                break;

            case SafeBoxMgr.BOX_ID_VIDEO:
                count = mVideoService.getHideVideoCount();
                break;

            case SafeBoxMgr.BOX_ID_AUDIO:
                count = mAudioService.getHideAudioCount();
                break;

            case SafeBoxMgr.BOX_ID_FILE:
                count = mFileService.getHideFileCount();
                break;
            default:
                count = 0;
        }

        String detail = mContext.getString(box.getDetailId(), count);
        vh.tvDetail.setText(detail);

        vh.item.setOnClickListener(new ItemClick(box));
        return convertView;
    }

    class ItemClick implements View.OnClickListener {

        private SafeBoxMgr.SafeBox box;

        public ItemClick(SafeBoxMgr.SafeBox box) {
            super();
            this.box = box;
        }

        @Override
        public void onClick(View v) {
            switch (box.getId()) {
                case SafeBoxMgr.BOX_ID_PIC:
                    mContext.startActivity(new Intent(mContext, PicHideActivity.class));
                    break;

                case SafeBoxMgr.BOX_ID_VIDEO:
                    mContext.startActivity(new Intent(mContext, VideoHideActivity.class));
                    break;

                case SafeBoxMgr.BOX_ID_AUDIO:
                    mContext.startActivity(new Intent(mContext, AudioHideActivity.class));
                    break;

                case SafeBoxMgr.BOX_ID_FILE:
                    mContext.startActivity(new Intent(mContext, FileHideActivity.class));
                    break;
            }

        }

    }

    @SuppressLint("InflateParams")
    private View buildItemView() {
        View view = mInflater.inflate(R.layout.item_safebox, null);
        ViewHolder vh = new ViewHolder();
        vh.item = view.findViewById(R.id.layout_item);
        vh.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
        vh.tvTitle = (TextView) view.findViewById(R.id.tv_title);
        vh.tvDetail = (TextView) view.findViewById(R.id.tv_detail);
        view.setTag(vh);
        return view;
    }

    class ViewHolder {

        View item;
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvDetail;

    }

}
