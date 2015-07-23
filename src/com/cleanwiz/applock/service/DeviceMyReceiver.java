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

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.cleanwiz.applock.ui.activity.SettingActivity;
import com.cleanwiz.applock.utils.LogUtil;

public class DeviceMyReceiver extends DeviceAdminReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        LogUtil.e("autosetting", "receiver onReceive");
    }

    /**
     * 获取设备存储的数值
     * 
     * @param context
     * @return
     */
    public static SharedPreferences getDevicePreference(Context context) {
        return context.getSharedPreferences(DeviceMyReceiver.class.getName(), 0);
    }

    // 密码的特点
    public static String PREF_PASSWORD_QUALITY = "password_quality";
    // 密码的长度
    public static String PREF_PASSWORD_LENGTH = "password_length";

    public static String PREF_MAX_FAILED_PW = "max_failed_pw";

    void showToast(Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {

    }

    @Override
    public void onDisabled(Context context, Intent intent) {

    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        Intent intent2 = new Intent(context, SettingActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent2);
        context.stopService(intent);
        return "";
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {

    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {

    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {

    }

}
