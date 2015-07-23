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
package com.cleanwiz.applock.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.MyConstants;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.utils.SharedPreferenceUtil;

public class UserUnlockActivity extends BaseActivity {

	private String pkgName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_unlock);
		pkgName = getIntent().getStringExtra(MyConstants.LOCK_PACKAGE_NAME);

	}

	private void goToPassword() {
		Intent intent;
		if (SharedPreferenceUtil.readIsNumModel()) {
			intent = new Intent(this, NumberUnlockActivity.class);
		} else {
			intent = new Intent(this, GestureUnlockActivity.class);
		}
		intent.putExtra(MyConstants.LOCK_PACKAGE_NAME, pkgName);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onClickEvent(View view) {
		switch (view.getId()) {
		case R.id.btn_cancel:
			AppLockApplication.getInstance().goHome(this);
			break;
		case R.id.btn_unlock:
			goToPassword();
			break;

		default:
			break;
		}
		super.onClickEvent(view);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			AppLockApplication.getInstance().goHome(this);
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
}
