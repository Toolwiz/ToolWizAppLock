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
package com.cleanwiz.applock.service;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import com.cleanwiz.applock.MyConstants;
import com.cleanwiz.applock.data.HideImage;
import com.cleanwiz.applock.data.HideImageDao.DaoMaster;
import com.cleanwiz.applock.data.HideImageDao.DaoMaster.DevOpenHelper;
import com.cleanwiz.applock.data.HideImageDao.DaoSession;
import com.cleanwiz.applock.data.HideImageDao.HideImageDao;
import com.cleanwiz.applock.data.HideImageDao.HideImageDao.Properties;
import com.cleanwiz.applock.files.utils.FileHideUtils;
import com.cleanwiz.applock.files.utils.SdCardUtil;
import com.cleanwiz.applock.model.AbstructProvider;
import com.cleanwiz.applock.model.ImageModel;
import com.cleanwiz.applock.utils.FileUtil;
import com.cleanwiz.applock.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImageService implements AbstructProvider {

    private Context context;
    private String IMAGE_HIDE_URL = "";
    private HideImageDao hideImageDao = null;
    private DaoSession daoSession = null;

    public ImageService(Context context) {
        super();
        this.context = context;
        instanceHideImageDataBase();
        LogUtil.e("colin", IMAGE_HIDE_URL);
    }

    public void instanceHideImageDataBase() {
        if (hideImageDao == null) {
            DevOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                    MyConstants.getDatabasePath(context, "hideimage"), null);
            SQLiteDatabase db = helper.getWritableDatabase();
            DaoMaster daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            hideImageDao = daoSession.getHideImageDao();
        }
    }

    /**
     * 获取本机图片列表（如果是android4.4排除外置SD卡数据）
     *
     * @return
     */
    @Override
    public List<?> getList() {
        List<ImageModel> list = null;

        boolean needCheck = SdCardUtil.needCheckExtSDCard();
        String mPath = SdCardUtil.getExtSDCardPath();
        if (mPath == null)
            needCheck = false;

        if (context != null) {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null,
                    null, null);
            if (cursor != null) {
                list = new ArrayList<ImageModel>();
                while (cursor.moveToNext()) {
                    int id = cursor
                            .getInt(cursor
                                    .getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                    String title = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Images.Media.TITLE));
                    String path = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                    String displayName = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                    String mimeType = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
                    long size = cursor
                            .getLong(cursor
                                    .getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));

                    if (FileUtil.isHideFile(displayName))
                        continue;

                    // 判断是否需要添加
                    if (needCheck && path.contains(mPath)) {
                        continue;
                    }

                    ImageModel audio = new ImageModel(id, title, displayName,
                            mimeType, path, size);
                    LogUtil.e("colin", "dusplayname:" + displayName + " title:"
                            + title);
                    list.add(audio);
                }
                cursor.close();
            }
        }
        return list;
    }

    // 隐藏图片
    public boolean hideImage(ImageModel imageModel, int beyondGroupId) {
        File imageFile = new File(imageModel.getPath());
        if (!imageFile.exists()) {
            return false;
        }
        String toPathString = MyConstants.getHidePath(imageModel.getPath());
        if (toPathString.isEmpty()) {
            return false;
        }
        File toFile = new File(toPathString + imageModel.getDisplayName() + MyConstants.getSuffix());
        // 复制
        if (imageFile.renameTo(toFile)) {
            // 插入数据库
            if (hideImageDao != null) {
                long id = hideImageDao.insertOrReplace(new HideImage(null,
                        beyondGroupId, imageModel.getTitle(), imageModel
                        .getDisplayName(), imageModel.getMimeType(),
                        imageModel.getPath(), toFile.getPath(), imageModel
                        .getSize(), new Date().getTime()));
                if (id >= 0) {
                    delSysMedia(imageModel);
                    return true;
                }
            }
        }

        return false;
    }

    // 取消隐藏
    public boolean unHideImage(HideImage hideImage) {
        if (hideImage != null) {
            File imageFile = new File(hideImage.getNewPathUrl());
            File toFile = new File(hideImage.getOldPathUrl());

            // 插入数据库
            if (hideImageDao != null) {
                hideImageDao.delete(hideImage);
                insSysMedia(hideImage);
            }

            // 复制
            if (imageFile.renameTo(toFile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除系统媒体记录及其对应的缩略图记录
     */
    private void delSysMedia(ImageModel mi) {
        ContentResolver cr = context.getContentResolver();
        cr.delete(Images.Media.EXTERNAL_CONTENT_URI, Images.Media._ID + "=?",
                new String[]{String.valueOf(mi.getId())});
        cr.delete(Images.Thumbnails.EXTERNAL_CONTENT_URI,
                Images.Thumbnails.IMAGE_ID + "=?",
                new String[]{String.valueOf(mi.getId())});
    }

    /**
     * 插入系统媒体记录
     */
    private void insSysMedia(HideImage mi) {
        ContentResolver cr = context.getContentResolver();
        File oriFile = new File(mi.getOldPathUrl());
        ContentValues values = new ContentValues();
        values.put(
                Images.Media.TITLE,
                mi.getDisplayName().substring(0,
                        mi.getDisplayName().lastIndexOf(".")));
        values.put(Images.Media.DISPLAY_NAME, mi.getDisplayName());
        values.put(Images.Media.DATA, mi.getOldPathUrl());
        values.put(Images.Media.DATE_MODIFIED, oriFile.lastModified());
        values.put(Images.Media.SIZE, oriFile.length());
        values.put(Images.Media.MIME_TYPE, mi.getMimeType());
        Uri contentUri = cr.insert(Images.Media.EXTERNAL_CONTENT_URI, values);
        if (contentUri != null) {
            mi.setId(ContentUris.parseId(contentUri));
        }
    }

    // 获取已隐藏的图片列表
    public List<HideImage> getHideImages(int beyondGroupId) {
        List<HideImage> hideImages = new ArrayList<HideImage>();
        if (hideImageDao != null) {
            hideImages = hideImageDao.queryBuilder().where(Properties.BeyondGroupId.eq(beyondGroupId)).list();

            final List<HideImage> list = FileHideUtils.checkHideImage(hideImages);
            if (list.size() > 0) {
                new Thread() {
                    @Override
                    public void run() {
                        for (HideImage hideFile : list) {
                            deleteAudioByPath(hideFile);
                        }
                    }
                }.start();
            }
        }

        return hideImages;
    }

    public int getHideImageCount() {
        if (hideImageDao != null) {
            List<HideImage> hideImageList = hideImageDao.loadAll();
            return hideImageList.size();
        }
        return 0;
    }

    // 删除指定图像文件
    public boolean deleteAudioByPath(HideImage hideImage) {
        if (hideImage.getNewPathUrl() == null || hideImage.getNewPathUrl().isEmpty()) {
            return false;
        }
        File imageFile = new File(hideImage.getNewPathUrl());
        if (imageFile != null) {
            if (hideImageDao != null) {
                hideImageDao.delete(hideImage);
            }

            if (imageFile.delete()) {
                delSysMedia(new ImageModel(hideImage.getId().intValue(), hideImage.getTitle(), hideImage.getDisplayName(), hideImage.getMimeType(), hideImage.getNewPathUrl(), hideImage.getSize()));
                return true;
            }
        }
        return false;
    }

}
