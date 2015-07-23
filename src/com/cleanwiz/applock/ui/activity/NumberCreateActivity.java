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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.ui.anim.PopListener;
import com.cleanwiz.applock.ui.widget.actionview.ActionView;
import com.cleanwiz.applock.ui.widget.actionview.CloseAction;
import com.cleanwiz.applock.ui.widget.actionview.MoreAction;
import com.cleanwiz.applock.utils.LangUtils;
import com.cleanwiz.applock.utils.LogUtil;
import com.cleanwiz.applock.utils.SharedPreferenceUtil;
import com.cleanwiz.applock.utils.StringUtils;
import com.cleanwiz.applock.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class NumberCreateActivity extends BaseActivity {

	public static final String CHANGE_FLAG = "change_flag";
	private boolean changeFlag;

	private String tmpPassword;
	private boolean checkInput = false;
	private TextView tv_text;

	private View popView;
	private ActionView actionView;
	private ScaleAnimation pop_in;
	private ScaleAnimation pop_out;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_num_create);
		tv_text = (TextView) findViewById(R.id.tv_text);
		initNumLayout();
		changeFlag = getIntent().getBooleanExtra(CHANGE_FLAG, false);
		if (changeFlag) {
			findViewById(R.id.tv_tips_01).setVisibility(View.INVISIBLE);
			findViewById(R.id.tv_tips_02).setVisibility(View.INVISIBLE);
		}
		super.onCreate(savedInstanceState);

		// pop
		popView = findViewById(R.id.layout_pop);
		actionView = (ActionView) findViewById(R.id.btn_more);

		initAnim();
	}

	private void initAnim() {

		long durationS = 160;
		AccelerateInterpolator accInterpolator = new AccelerateInterpolator();

		pop_in = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 1,
				Animation.RELATIVE_TO_SELF, 0);
		pop_out = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 1,
				Animation.RELATIVE_TO_SELF, 0);

		pop_in.setDuration(durationS);
		pop_in.setInterpolator(accInterpolator);
		pop_in.setAnimationListener(new PopListener(popView,
				PopListener.TYPE_IN));

		pop_out.setDuration(durationS);
		pop_out.setInterpolator(accInterpolator);
		pop_out.setAnimationListener(new PopListener(popView,
				PopListener.TYPE_OUT));

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void btnClickMore() {
		if (View.VISIBLE == popView.getVisibility()) {
			actionView.setAction(new MoreAction(),
					ActionView.ROTATE_COUNTER_CLOCKWISE);
			popView.clearAnimation();
			popView.startAnimation(pop_out);
		} else {
			actionView.setAction(new CloseAction(),
					ActionView.ROTATE_COUNTER_CLOCKWISE);
			popView.clearAnimation();
			popView.startAnimation(pop_in);
		}

	}

	@Override
	public void onClickEvent(View view) {
		switch (view.getId()) {
		case R.id.btn_change:
			changeToGes();
			break;
		case R.id.btn_more:
			btnClickMore();
			break;
		default:
			break;
		}
		super.onClickEvent(view);
	}

	private void changeToGes() {

		Intent intent = new Intent(this, GestureCreateActivity.class);
		intent.putExtra(GestureCreateActivity.CHANGE_FLAG, changeFlag);
		startActivity(intent);
		finish();

	}

	private static final int COUNT = 4;
	private List<String> numInput;
	private List<ImageView> pointList;

	private void initNumLayout() {
		numInput = new ArrayList<String>();
		pointList = new ArrayList<ImageView>(COUNT);
		pointList.add((ImageView) findViewById(R.id.num_point_1));
		pointList.add((ImageView) findViewById(R.id.num_point_2));
		pointList.add((ImageView) findViewById(R.id.num_point_3));
		pointList.add((ImageView) findViewById(R.id.num_point_4));
		for (ImageView iv : pointList) {
			iv.setImageResource(R.drawable.num_point);
		}

	}

	public void onNumClick(View view) {

		switch (view.getId()) {

		case R.id.number_1:
		case R.id.number_2:
		case R.id.number_3:
		case R.id.number_4:
		case R.id.number_5:
		case R.id.number_6:
		case R.id.number_7:
		case R.id.number_8:
		case R.id.number_9:
		case R.id.number_0:
			clickNumber((Button) view);
			break;

		case R.id.number_del:
			deleteNumber();
			break;

		default:
			break;
		}
		super.onClickEvent(view);
	}

	private void clickNumber(Button btn) {

		if (numInput.size() < COUNT) {
			numInput.add(btn.getText().toString());
		}
		int index = 0;
		for (ImageView iv : pointList) {
			if (index++ < numInput.size()) {
				iv.setImageResource(R.drawable.num_point_check);
			} else {
				iv.setImageResource(R.drawable.num_point);
			}
		}
		StringBuffer pBuffer = new StringBuffer();
		for (String s : numInput) {
			pBuffer.append(s);
		}
		doForResult(inputCheck(pBuffer.toString()));

	}

	enum InputResult {
		ONCE, SUCCESS, ERROR, CONTINUE
	}

	private void doForResult(InputResult result) {
		switch (result) {
		case CONTINUE:
			break;
		case ONCE:
			checkInput = true;
			numInput.clear();
			tv_text.setText(R.string.num_create_text_02);
			break;
		case SUCCESS:
			ToastUtils.showToast(R.string.password_set_success);
			AppLockApplication.getInstance().setStartGuide(true);
			startActivity(new Intent(this, LockMainActivity.class));
			String md5 = StringUtils.toMD5(tmpPassword);
			SharedPreferenceUtil.editIsNumModel(true);
			SharedPreferenceUtil.editNumPassword(md5);

			finish();
			break;
		case ERROR:
			checkInput = false;
			numInput.clear();
			for (ImageView iv : pointList) {
				iv.setImageResource(R.drawable.num_point);
			}
			tv_text.setText(R.string.num_create_text_03);
			break;

		default:
			break;
		}
	}

	private InputResult inputCheck(String password) {
		InputResult result;
		LogUtil.d("demo3", "input:" + password);
		if (numInput.size() == COUNT) {
			if (checkInput) {
				if (password.equals(tmpPassword)) {
					result = InputResult.SUCCESS;
				} else {
					result = InputResult.ERROR;
				}
			} else {
				tmpPassword = password;
				result = InputResult.ONCE;
			}
		} else {
			result = InputResult.CONTINUE;
		}
		return result;
	}

	private void deleteNumber() {

		if (numInput.size() == 0) {
			return;
		}
		pointList.get(numInput.size() - 1).setImageResource(
				R.drawable.num_point);
		numInput.remove(numInput.size() - 1);

	}
}
