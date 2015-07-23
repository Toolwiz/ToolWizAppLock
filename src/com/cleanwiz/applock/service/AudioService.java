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
import android.provider.MediaStore.Audio;
import com.cleanwiz.applock.MyConstants;
import com.cleanwiz.applock.data.HideAudio;
import com.cleanwiz.applock.data.HideAudioDao.DaoMaster;
import com.cleanwiz.applock.data.HideAudioDao.DaoMaster.DevOpenHelper;
import com.cleanwiz.applock.data.HideAudioDao.DaoSession;
import com.cleanwiz.applock.data.HideAudioDao.HideAudioDao;
import com.cleanwiz.applock.data.HideAudioDao.HideAudioDao.Properties;
import com.cleanwiz.applock.files.utils.FileHideUtils;
import com.cleanwiz.applock.files.utils.SdCardUtil;
import com.cleanwiz.applock.model.AbstructProvider;
import com.cleanwiz.applock.model.AudioModel;
import com.cleanwiz.applock.utils.FileUtil;
import com.cleanwiz.applock.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AudioService implements AbstructProvider {

    private Context context;
    private String IMAGE_HIDE_URL = "";
    private HideAudioDao hideAudioDao = null;
    private DaoSession daoSession = null;

    public AudioService(Context context) {
        super();
        this.context = context;
        instanceHideAudioDataBase();
        LogUtil.e("colin", IMAGE_HIDE_URL);
    }

    public void instanceHideAudioDataBase() {
        if (hideAudioDao == null) {
            DevOpenHelper helper = new DaoMaster.DevOpenHelper(context,
                    MyConstants.getDatabasePath(context, "hideiaudio"), null);
            SQLiteDatabase db = helper.getWritableDatabase();
            DaoMaster daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            hideAudioDao = daoSession.getHideAudioDao();
        }
    }

    // 获取所有音频文件列表（不包括已加密的）
    @Override
    public List<?> getList() {
        List<AudioModel> list = null;

        boolean needCheck = SdCardUtil.needCheckExtSDCard();
        String mPath = SdCardUtil.getExtSDCardPath();
        if (mPath == null)
            needCheck = false;

        if (context != null) {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                    null, null);
            if (cursor != null) {
                list = new ArrayList<AudioModel>();
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    String title = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String album = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                    String artist = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    String path = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    String displayName = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                    String mimeType = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));
                    long duration = cursor
                            .getInt(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    long size = cursor
                            .getLong(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

                    if (FileUtil.isHideFile(displayName))
                        continue;

                    // 判断是否需要添加
                    if (needCheck && path.contains(mPath)) {
                        continue;
                    }

                    AudioModel audio = new AudioModel(id, title, album, artist,
                            path, displayName, mimeType, duration, size);
                    list.add(audio);
                }
                cursor.close();
            }
        }
        return list;
    }

    // 隐藏音频文件
    public boolean hideAudio(AudioModel audioModel, int beyondGroupId) {
        File fromFile = new File(audioModel.getPath());
        if (!fromFile.exists()) {
            return false;
        }
        String toPathString = MyConstants.getHidePath(audioModel.getPath());
        if (toPathString.isEmpty()) {
            return false;
        }
        File toFile = new File(toPathString + audioModel.getDisplayName() + MyConstants.getSuffix());
        // 复制
        if (fromFile.renameTo(toFile)) {
            // 插入数据库
            if (hideAudioDao != null) {
                long id = hideAudioDao.insertOrReplace(new HideAudio(null,
                        beyondGroupId, audioModel.getTitle(), audioModel
                        .getAlbum(), audioModel.getArtist(), audioModel
                        .getPath(), audioModel.getDisplayName(),
                        audioModel.getMimeType(), String.valueOf(audioModel
                        .getDuration()), toFile.getPath(), audioModel
                        .getSize(), new Date().getTime()));
                if (id >= 0) {
                    delSysMedia(audioModel);
                    return true;
                }
            }
        }
        return false;
    }

    // 取消隐藏音频文件
    public boolean unHideAudio(HideAudio hideAudio) {
        if (hideAudio != null) {
            File fromFile = new File(hideAudio.getNewPathUrl());
            File toFile = new File(hideAudio.getOldPathUrl());

            // 插入数据库
            if (hideAudioDao != null) {
                hideAudioDao.delete(hideAudio);
                insSysMedia(hideAudio);
            }
            // 复制
            if (fromFile.renameTo(toFile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除系统媒体记录及其对应的缩略图记录
     */
    private void delSysMedia(AudioModel mi) {
        ContentResolver cr = context.getContentResolver();
        cr.delete(Audio.Media.EXTERNAL_CONTENT_URI, Audio.Media._ID + "=?",
                new String[]{String.valueOf(mi.getId())});
    }

    /**
     * 插入系统媒体记录
     */
    private void insSysMedia(HideAudio mi) {
        ContentResolver cr = context.getContentResolver();
        File oriFile = new File(mi.getOldPathUrl());
        ContentValues values = new ContentValues();
        values.put(
                Audio.Media.TITLE,
                mi.getDisplayName().substring(0,
                        mi.getDisplayName().lastIndexOf(".")));
        values.put(Audio.Media.DISPLAY_NAME, mi.getDisplayName());
        values.put(Audio.Media.DATA, mi.getOldPathUrl());
        values.put(Audio.Media.DATE_MODIFIED, oriFile.lastModified());
        values.put(Audio.Media.SIZE, oriFile.length());
        values.put(Audio.Media.MIME_TYPE, mi.getMimeType());
        Uri contentUri = cr.insert(Audio.Media.EXTERNAL_CONTENT_URI, values);
        if (contentUri != null) {
            mi.setId(ContentUris.parseId(contentUri));
        }
    }

    // 获取所有已加密的音频文件列表
    public List<HideAudio> getHideAudios(int beyondGroupId) {
        List<HideAudio> audios = new ArrayList<HideAudio>();
        if (hideAudioDao != null) {
            audios = hideAudioDao.queryBuilder()
                    .where(Properties.BeyondGroupId.eq(beyondGroupId)).list();

            final List<HideAudio> list = FileHideUtils.checkHideAudio(audios);
            if (list.size() > 0) {
                new Thread() {
                    @Override
                    public void run() {
                        for (HideAudio hideFile : list) {
                            deleteAudioByPath(hideFile);
                        }
                    }
                }.start();
            }
        }

        return audios;
    }


    // 获取已隐藏图片的数量不分组别
    public int getHideAudioCount() {
        if (hideAudioDao != null) {
            List<HideAudio> hideAudioList = hideAudioDao.loadAll();
            return hideAudioList.size();
        }
        return 0;
    }

    // 删除指定音频文件
    public boolean deleteAudioByPath(HideAudio hideAudio) {
        if (hideAudio.getNewPathUrl() == null || hideAudio.getNewPathUrl().isEmpty()) {
            return false;
        }
        File audioFile = new File(hideAudio.getNewPathUrl());
        if (audioFile != null) {

            if (hideAudioDao != null) {
                hideAudioDao.delete(hideAudio);
            }
            if (audioFile.delete()) {
                delSysMedia(new AudioModel(hideAudio.getId().intValue(), hideAudio.getTitle(), hideAudio.getAlbum(), hideAudio.getArtist(), hideAudio.getNewPathUrl(), hideAudio.getDisplayName(), hideAudio.getMimeType(), Long.valueOf(hideAudio.getDuration()), hideAudio.getSize()));
                return true;
            }
        }
        return false;
    }

}
