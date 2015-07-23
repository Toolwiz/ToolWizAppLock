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
import android.view.View;
import android.widget.ImageView;

import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.utils.LangUtils;
import com.cleanwiz.applock.utils.SharedPreferenceUtil;

public class UserHelpActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_help);

	}

	@Override
	protected void onResume() {
		if (LangUtils.isChinese()) {
			((ImageView) findViewById(R.id.iv_tips))
					.setImageResource(R.drawable.guest_pic_cn);
		}
		super.onResume();
	}

	@Override
	public void onClickEvent(View view) {
		switch (view.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_none:
			SharedPreferenceUtil.editFirstUserModel(false);
			Intent intent = new Intent(this, ChooseAppsActivity.class);
			intent.putExtra(ChooseAppsActivity.APP_LIST_FLAG,
					ChooseAppsActivity.FLAG_USER_LOCK);
			intent.putExtra(ChooseAppsActivity.MODEL_NAME,
					getString(R.string.lock_user));
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}
		super.onClickEvent(view);
	}

}
