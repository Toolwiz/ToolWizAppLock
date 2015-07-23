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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.data.CommLockInfo;
import com.cleanwiz.applock.service.CommLockInfoService;
import com.cleanwiz.applock.utils.Cn2Spell;
import com.gc.materialdesign.views.CheckView;
import com.umeng.analytics.MobclickAgent;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class LockAdapter extends BaseAdapter {

    private Activity mContext;
    private CommLockInfoService lockService;
    private List<CommLockInfo> lockInfos;
    private LayoutInflater mInflater;
    private PackageManager pkgMgr;

    private AnimationDrawable animLock;
    private AnimationDrawable animUnlock;
    private Resources mResources;

    private AppLocker aLocker;

    public LockAdapter(Activity mContext) {
        super();
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        mResources = mContext.getResources();

        initData();
    }

    private void initData() {
        pkgMgr = mContext.getPackageManager();
        lockService = new CommLockInfoService(mContext);
        lockService.getCommLockInfoDaoInstance();
        lockInfos = lockService.getAllCommLockInfos();
        checkLockInfos();
        aLocker = new AppLocker() {

            @Override
            public void unlockApp(String pkgName) {
                lockService.unlockCommApplication(pkgName);
            }

            @Override
            public void lockApp(String pkgName) {
                lockService.lockCommApplication(pkgName);
            }
        };
    }

    @Override
    public int getCount() {
        return lockInfos.size();
    }

    @Override
    public CommLockInfo getItem(int position) {
        return lockInfos.get(position);
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
        CommLockInfo lock = getItem(position);
        ApplicationInfo appInfo = lock.getAppInfo();
        if (appInfo != null) {
            vh.ivIcon.setImageDrawable(pkgMgr.getApplicationIcon(appInfo));
            vh.tvTitle.setText(pkgMgr.getApplicationLabel(appInfo));
        }
        vh.ivLock.setCheckedNoAnim(lock.getIsLocked());
        vh.item.setTag(vh);
        vh.item.setOnClickListener(new ItemClick(lock));
        return convertView;
    }

    private void checkLockInfos() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                for (int i = 0; i < lockInfos.size(); i++) {
                    CommLockInfo lock = lockInfos.get(i);
                    ApplicationInfo appInfo = null;
                    try {
                        appInfo = pkgMgr.getApplicationInfo(lock.getPackageName(),
                                PackageManager.GET_UNINSTALLED_PACKAGES);
                    } catch (NameNotFoundException e) {
                        e.printStackTrace();
                        lockInfos.remove(lock);
                        continue;
                    }
                    if (appInfo == null || pkgMgr.getApplicationIcon(appInfo) == null) {
                        lockInfos.remove(lock);
                        continue;
                    } else {
                        lock.setAppInfo(appInfo);
                    }
                    if (i % 7 == 0) {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        });
                    }
                }
                if (sortType == SORT_DEFAULT)
                    Collections.sort(lockInfos, AppLockApplication.commLockInfoComparator);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (sortType == SORT_DEFAULT)
                    notifyDataSetChanged();
            }
        }.execute();
    }

    @SuppressLint("InflateParams")
    private View buildItemView() {
        View view = mInflater.inflate(R.layout.item_applock, null);
        ViewHolder vh = new ViewHolder();
        vh.item = view.findViewById(R.id.layout_item);
        vh.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
        vh.tvTitle = (TextView) view.findViewById(R.id.tv_title);
        vh.tvDetail = (TextView) view.findViewById(R.id.tv_detail);
        vh.ivLock = (CheckView) view.findViewById(R.id.iv_lock);
        view.setTag(vh);
        return view;
    }

    public static final int SORT_DEFAULT = 1;
    public static final int SORT_ALPHA = 2;
    public static final int SORT_INSTALL = 3;

    private Comparator<CommLockInfo> alphaSort = new Comparator<CommLockInfo>() {
        @Override
        public int compare(CommLockInfo lhs, CommLockInfo rhs) {
            if (lhs.getAppInfo() != null && rhs.getAppInfo() != null)
                return Cn2Spell.converterToSpell(String.valueOf(lhs.getAppInfo().loadLabel(pkgMgr)))
                        .compareToIgnoreCase(Cn2Spell.converterToSpell(String.valueOf(rhs.getAppInfo().loadLabel(pkgMgr))));
            return 0;
        }
    };
    private Comparator<CommLockInfo> installSort = new Comparator<CommLockInfo>() {
        @Override
        public int compare(CommLockInfo lhs, CommLockInfo rhs) {
            try {
                return (int) (pkgMgr.getPackageInfo(lhs.getPackageName(), 0).firstInstallTime - pkgMgr.getPackageInfo(rhs.getPackageName(), 0).firstInstallTime);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            return 0;
        }
    };

    private int sortType = SORT_DEFAULT;
    public void onSort(int type) {
        sortType = type;
        if (lockInfos != null) {
            switch (type) {
                case SORT_DEFAULT:
                    lockInfos = lockService.getAllCommLockInfos();
                    checkLockInfos();
                    break;
                case SORT_ALPHA:
                    Collections.sort(lockInfos, alphaSort);
                    break;
                case SORT_INSTALL:
                    Collections.sort(lockInfos, installSort);
                    break;
            }
            notifyDataSetChanged();
        }
    }

    class ViewHolder {

        View item;
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvDetail;
        CheckView ivLock;

    }

    class ItemClick implements OnClickListener {

        private CommLockInfo lockInfo;

        public ItemClick(CommLockInfo lockInfo) {
            super();
            this.lockInfo = lockInfo;
        }

        @Override
        public void onClick(View v) {
            ViewHolder vh = (ViewHolder) v.getTag();
            if (lockInfo.getIsLocked()) {
                vh.ivLock.setChecked(false);
                aLocker.unlockApp(lockInfo.getPackageName());
                reportEvent(UnlockEvent, lockInfo.getPackageName());
            } else {
                vh.ivLock.setChecked(true);
                aLocker.lockApp(lockInfo.getPackageName());
                reportEvent(LockEvent, lockInfo.getPackageName());
            }

        }

    }

    private static final String LockEvent = "lock";
    private static final String UnlockEvent = "unlock";

    public void reportEvent(String event, String packageName) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("pkg", packageName);
        MobclickAgent.onEvent(mContext, event, map);
    }

    public interface AppLocker {

        public void lockApp(String pkgName);

        public void unlockApp(String pkgName);

    }

}
