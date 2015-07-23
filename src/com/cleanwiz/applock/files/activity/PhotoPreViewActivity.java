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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.data.HideImage;
import com.cleanwiz.applock.files.entity.HideImageExt;
import com.cleanwiz.applock.files.widget.HackyViewPager;
import com.cleanwiz.applock.service.ImageService;
import com.cleanwiz.applock.ui.BaseActivity;
import com.gc.materialdesign.widgets.Dialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.util.List;

/**
 * 照片浏览
 * Created by dev on 2015/5/7.
 */
public class PhotoPreViewActivity extends BaseActivity {

    private static final String TAG = "PhotoPreViewActivity";
    private ViewPager mViewPager;
    private SamplePagerAdapter mSamplePagerAdapter;

    protected ImageService mImageService;
    private View mViewpage_title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_preview_viewpager);

        mViewPager = (HackyViewPager) findViewById(R.id.file_preview_viewpager);
        mViewpage_title = findViewById(R.id.viewpage_title);

        mImageService = new ImageService(this);
        mSamplePagerAdapter = new SamplePagerAdapter(null);
        mViewPager.setAdapter(mSamplePagerAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;

            case R.id.pic_hide_img_recovery://恢复内容
                recoveryDialog();
                break;

            case R.id.pic_hide_img_del://删除内容

                delDialog();
                break;
        }
    }

    private void recoveryDialog() {

        final Dialog dialog = new Dialog(
                this,
                ((getString(R.string.file_dialog_recovery)) + getString(R.string.pic_preview)),
                getString(R.string.pic_preview) + getString(R.string.file_dialog_recovery_missage)
        );

        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoveryFiles();
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

    protected void delDialog() {

        final Dialog dialog = new Dialog(
                this,
                ((getString(R.string.file_dialog_del)) + getString(R.string.pic_preview)),
                getString(R.string.pic_preview) + getString(R.string.file_dialog_del_missage)
        );

        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delFiles();
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

    protected void recoveryFiles() {
        // 移除图片
        List<HideImageExt> list = mSamplePagerAdapter.getList();
        if (list != null) {
            int index = mViewPager.getCurrentItem();
            HideImage hideImageExt = list.remove(index);
            mSamplePagerAdapter.setList(list);
            mViewPager.setCurrentItem(index);
            mImageService.unHideImage(hideImageExt);
        }
    }

    protected void delFiles() {

        List<HideImageExt> list = mSamplePagerAdapter.getList();
        if (list != null) {
            int index = mViewPager.getCurrentItem();
            HideImage hideImageExt = list.remove(index);
            mSamplePagerAdapter.setList(list);
            mViewPager.setCurrentItem(index);
            mImageService.deleteAudioByPath(hideImageExt);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        List<HideImageExt> hideImageExt = intent.getParcelableArrayListExtra("list");
        int index = intent.getIntExtra("id", -1);
        mSamplePagerAdapter.setList(hideImageExt);

        if (index != -1)
            mViewPager.setCurrentItem(index);

        mViewpage_title.setVisibility(View.VISIBLE);
    }

    class SamplePagerAdapter extends PagerAdapter {

        public SamplePagerAdapter(List<HideImageExt> list) {
            this.list = list;

            // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
                    .cacheOnDisc(false)                          // 设置下载的图片是否缓存在SD卡中
                    .build();                                   // 创建配置过得DisplayImageOption对象
        }

        private List<HideImageExt> list;

        protected final ImageLoader imageLoader = ImageLoader.getInstance();

        DisplayImageOptions options;        // 显示图片的设置

        @Override
        public int getCount() {
            if (list != null)
                return list.size();
            return 0;
        }

        public void clear() {
            if (imageLoader != null)
                imageLoader.stop();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            if (list != null) {
                imageLoader.displayImage(
                        ImageDownloader.Scheme.THUMBNAIL.wrap(list.get(position).getNewPathUrl()),
                        photoView,
                        options);
            }
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewpage_title.setVisibility(mViewpage_title.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                }
            });

            photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    mViewpage_title.setVisibility(mViewpage_title.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                }
            });

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public void setList(List<HideImageExt> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        /**
         * 获取当前列表
         *
         * @return
         */
        public List<HideImageExt> getList() {
            return list;
        }
    }
}
