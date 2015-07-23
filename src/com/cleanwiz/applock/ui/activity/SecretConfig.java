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

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.utils.StringUtils;
import com.cleanwiz.applock.utils.ToastUtils;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelClickedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

public class SecretConfig extends BaseActivity {

	public static final int[] SECRETQUESTIONIDS = {
			R.string.password_question_01, R.string.password_question_02,
			R.string.password_question_03, R.string.password_question_04,
			R.string.password_question_05, R.string.password_question_06,
			R.string.password_question_07, R.string.password_question_08,
			R.string.password_question_09 };

	private AppLockApplication application = AppLockApplication.getInstance();
	private EditText answerEditText = null;
	private WheelView wheelView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_secret_set);

		answerEditText = (EditText) findViewById(R.id.secretanswer);

		wheelView = (WheelView) findViewById(R.id.wv_question);
		AbstractWheelTextAdapter adapter = new AbstractWheelTextAdapter(
				getApplicationContext()) {

			@Override
			public int getItemsCount() {
				// TODO Auto-generated method stub
				return SECRETQUESTIONIDS.length;
			}

			@Override
			protected CharSequence getItemText(int index) {
				// TODO Auto-generated method stub
				return getString(SECRETQUESTIONIDS[index]);
			}

		};

		OnWheelClickedListener clickedListener = new OnWheelClickedListener() {

			@Override
			public void onItemClicked(WheelView wheel, int itemIndex) {
				wheel.setCurrentItem(itemIndex, true);
			}
		};

		OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				
			}
		};
		OnWheelScrollListener scrollListener = new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				// TODO Auto-generated method stub
			}
		};
		adapter.setItemResource(R.layout.item_wheel_setsecret);
		adapter.setItemTextResource(R.id.tv_text);
		wheelView.setViewAdapter(adapter);
		wheelView.setCyclic(true);
		wheelView.addChangingListener(wheelListener);
		wheelView.addScrollingListener(scrollListener);
		wheelView.addClickingListener(clickedListener);
	}

	@Override
	public void onClickEvent(View view) {
		switch (view.getId()) {
			case R.id.btn_menu:
			finish();
			break;
		case R.id.btn_save:
			saveSecret();
			break;
		default:
			break;
		}
		super.onClickEvent(view);
	}

	private void saveSecret() {

		String answer = null;
		try {
			answer = answerEditText.getText().toString();
		} catch (Exception e) {
			answer = null;
		}
		if (TextUtils.isEmpty(answer)) {
			ToastUtils.showToast(R.string.password_answer_null_toast);
		} else {
			application.setSecretQuestionString(wheelView.getCurrentItem());
			application.setSecretAnswerString(StringUtils.toMD5(answer));
			ToastUtils.showToast(R.string.password_answer_set_toast);
			finish();
		}

	}
}
