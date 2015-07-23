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
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.cleanwiz.applock.R;

import java.util.ArrayList;
import java.util.List;

public class MainPagerAdapter extends PagerAdapter {
    private Activity mContext;
    private LayoutInflater mInflater;
    private PackageManager pkgMgr;

    private List<View> mListViews;

    public MainPagerAdapter(Activity context) {
        super();
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        pkgMgr = mContext.getPackageManager();
        initView();
    }

    private void initView() {

        mListViews = new ArrayList<View>();
        View boxView = buildBoxView();
        View lockView = buildLockView();
        mListViews.add(lockView);
        mListViews.add(boxView);

    }

    @SuppressLint("InflateParams")
    private View buildLockView() {

        View view = mInflater.inflate(R.layout.pager_applock, null);
        ListView listView = (ListView) view.findViewById(R.id.lv_apps);
        final LockAdapter adapter = new LockAdapter(mContext);

        View header = mInflater.inflate(R.layout.pager_applock_header, null);
        final TextView tvSortAlpha = (TextView) header.findViewById(R.id.sort_alpha);
        final TextView tvSortDefault = (TextView) header.findViewById(R.id.sort_default);

        final int colorRed = mContext.getResources().getColor(R.color.text_red);
        final int colorBlack = mContext.getResources().getColor(R.color.md_black_1);
        View.OnClickListener onSort = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sort_alpha:
                        tvSortAlpha.setTextColor(colorRed);
                        tvSortDefault.setTextColor(colorBlack);
                        adapter.onSort(LockAdapter.SORT_ALPHA);
                        break;
                    case R.id.sort_default:
                        tvSortAlpha.setTextColor(colorBlack);
                        tvSortDefault.setTextColor(colorRed);
                        adapter.onSort(LockAdapter.SORT_DEFAULT);
                        break;
                }
            }
        };


        tvSortAlpha.setOnClickListener(onSort);
        tvSortDefault.setOnClickListener(onSort);
        listView.addHeaderView(header);
        listView.setAdapter(adapter);
        return view;
    }

    private BoxAdapter boxAdapter;

    @SuppressLint("InflateParams")
    private View buildBoxView() {

        boxAdapter = new BoxAdapter(mContext);

        View view = mInflater.inflate(R.layout.pager_safebox, null);
        ListView listView = (ListView) view.findViewById(R.id.lv_boxes);
        listView.setAdapter(boxAdapter);
        return view;

    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView(mListViews.get(arg1));
    }

    @Override
    public void finishUpdate(View arg0) {
    }

    @Override
    public int getCount() {
        return mListViews.size();
    }

    @Override
    public Object instantiateItem(View arg0, int arg1) {
        ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
        return mListViews.get(arg1);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == (arg1);
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {
    }

    /**
     * 重置保险箱数据
     */
    public void resetSafeBox() {
        if (boxAdapter != null)
            boxAdapter.notifyDataSetChanged();
    }
}
