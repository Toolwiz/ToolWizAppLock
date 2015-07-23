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
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.data.LookMyPrivate;
import com.cleanwiz.applock.service.CameraFuncation;
import com.cleanwiz.applock.service.LookMyPrivateService;
import com.cleanwiz.applock.service.PlayWarringSoundService;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.ui.anim.PopListener;
import com.cleanwiz.applock.ui.widget.LockPatternUtils;
import com.cleanwiz.applock.ui.widget.LockPatternView;
import com.cleanwiz.applock.ui.widget.LockPatternView.Cell;
import com.cleanwiz.applock.ui.widget.actionview.ActionView;
import com.cleanwiz.applock.ui.widget.actionview.CloseAction;
import com.cleanwiz.applock.ui.widget.actionview.MoreAction;
import com.cleanwiz.applock.utils.LogUtil;
import com.cleanwiz.applock.utils.SharedPreferenceUtil;
import com.cleanwiz.applock.utils.ToastUtils;

import java.util.Date;
import java.util.List;

public class GestureCheckActivity extends BaseActivity {

	public static final String CHANGE_PASSWORD = "change_password";

	private LockPatternView mLockPatternView;
	private int mFailedPatternAttemptsSinceLastTimeout = 0;
	private CountDownTimer mCountdownTimer = null;
	private Handler mHandler = new Handler();
	private TextView mHeadTextView;
	private Animation mShakeAnim;
	private CameraFuncation cameraFuncation;
	private SurfaceView surfaceView;
	private LookMyPrivateService pService;
	private PlayWarringSoundService playWarringSoundService;

	private boolean changeFlag;

	private ImageView iv_user_check;

	private int[] delayTime = { 60000, 120000, 180000, 600000, 1800000 };
	private int errorCount = 0;
	private boolean bPwdIsCorrent = true;
	private boolean bIsFalseStart = false;
	private int lastDelayTime = 0;
	private AppLockApplication appLockApplication = AppLockApplication
			.getInstance();

	private View popView;
	private ActionView actionView;
	private ScaleAnimation pop_in;
	private ScaleAnimation pop_out;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture_check);

		pService = new LookMyPrivateService(getApplicationContext());
		playWarringSoundService = new PlayWarringSoundService(
				getApplicationContext());
		surfaceView = (SurfaceView) findViewById(R.id.picSurfaceView);
		cameraFuncation = new CameraFuncation(getApplicationContext(),
				surfaceView, pService);
		mLockPatternView = (LockPatternView) findViewById(R.id.gesturepwd_unlock_lockview);
		mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
		mLockPatternView.setTactileFeedbackEnabled(true);
		mHeadTextView = (TextView) findViewById(R.id.gesturepwd_unlock_text);
		mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);

		iv_user_check = (ImageView) findViewById(R.id.iv_user_check);

		String secretQuestion = AppLockApplication.getInstance()
				.getSecretQuestionString();

		changeFlag = getIntent().getBooleanExtra(CHANGE_PASSWORD, false);
		unGoHome = changeFlag;

		bPwdIsCorrent = appLockApplication.getLastAppEnterCorrentPwd();
		errorCount = appLockApplication.getLastAppEnterPwdErrorCount();
		if (!bPwdIsCorrent) {
			bIsFalseStart = true;
			long defauleTime = new Date().getTime()
					- appLockApplication
							.getLastAppEnterPwdLeaverDateMiliseconds();
			LogUtil.e("colin", "上次解锁密码错误，到现在的时间为:" + defauleTime + "上次时间为:"
					+ appLockApplication.getLastAppEnterPwdDelayTime());
			if (defauleTime < appLockApplication.getLastAppEnterPwdDelayTime() * 1000) {
				LogUtil.e("colin", "上次解锁密码错误，时间孙艳");
				mHandler.postDelayed(attemptLockout, 100);
			} else {
				LogUtil.e("colin", "上次解锁密码错误，时间不孙艳");
				bIsFalseStart = false;
				errorCount += 1;
				if (errorCount > 4) {
					errorCount = 0;
				}
				appLockApplication.setLastAppEnterPwdErrorCount(errorCount);
			}
		}

		// pop
		popView = findViewById(R.id.layout_pop);
		actionView = (ActionView) findViewById(R.id.btn_more);

		initAnim();
		if (!TextUtils.isEmpty(secretQuestion)) {
			actionView.setVisibility(View.VISIBLE);
		} else {
			actionView.setVisibility(View.INVISIBLE);
		}

	}

	private void closePopView() {
		if (View.VISIBLE == popView.getVisibility()) {
			actionView.setAction(new MoreAction(),
					ActionView.ROTATE_COUNTER_CLOCKWISE);
			popView.clearAnimation();
			popView.startAnimation(pop_out);
		}
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
	protected void onResume() {
		super.onResume();
		try {
			if (!AppLockApplication.getInstance().getLockPatternUtils()
					.savedPatternExists()) {
				unGoHome = true;
				startActivity(new Intent(this, GestureCreateActivity.class));
				finish();
			}
		} catch (Exception e) {
			LogUtil.d("demo3", e.toString());
		}
	}

	@Override
	public void onClickEvent(View view) {
		switch (view.getId()) {
		case R.id.btn_more:
			btnClickMore();
			break;
		case R.id.btn_user_model:
			AppLockApplication.getInstance().setVisitorState(true);
			finish();
			break;
		case R.id.gesturepwd_unlock_forget:
			unGoHome = true;
			Intent intent = new Intent(this, SecretCheckActivity.class);
			startActivity(intent);
			closePopView();
			break;
		case R.id.btn_user_check:
			boolean flag = SharedPreferenceUtil.readUnlockUserByEnter();
			SharedPreferenceUtil.editUnlockUserByEnter(!flag);
			if (SharedPreferenceUtil.readUnlockUserByEnter()) {
				iv_user_check.setImageResource(R.drawable.checkbox_select);
			} else {
				iv_user_check.setImageResource(R.drawable.checkbox_unselect);
			}
			break;
		default:
			break;
		}
		super.onClickEvent(view);
	}

	boolean unGoHome = false;

	@Override
	protected void onStop() {
		cameraFuncation.clearCamera();
		appLockApplication.setLastAppEnterPwdState(bPwdIsCorrent,
				new Date().getTime(), errorCount, lastDelayTime);
		if (!unGoHome) {
			AppLockApplication.getInstance().goHome(this);
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cameraFuncation.clearCamera();
		appLockApplication.setLastAppEnterPwdState(bPwdIsCorrent,
				new Date().getTime(), errorCount, lastDelayTime);
		if (mCountdownTimer != null)
			mCountdownTimer.cancel();
	}

	private Runnable mClearPatternRunnable = new Runnable() {
		public void run() {
			mLockPatternView.clearPattern();
		}
	};

	protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {

		public void onPatternStart() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
			patternInProgress();
		}

		public void onPatternCleared() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
		}

		public void onPatternDetected(List<LockPatternView.Cell> pattern) {
			if (pattern == null)
				return;
			if (AppLockApplication.getInstance().getLockPatternUtils()
					.checkPattern(pattern)) {
				mLockPatternView
						.setDisplayMode(LockPatternView.DisplayMode.Correct);
				unGoHome = true;
				bPwdIsCorrent = true;
				if (changeFlag) {
					Intent intent = new Intent(GestureCheckActivity.this,
							GestureCreateActivity.class);
					intent.putExtra(GestureCreateActivity.CHANGE_FLAG, true);
					startActivity(intent);
				} else {
					if (AppLockApplication.getInstance().getVisitorState()
							&& SharedPreferenceUtil.readUnlockUserByEnter()) {
						AppLockApplication.getInstance().setVisitorState(false);
					}
					Intent intent = new Intent(GestureCheckActivity.this,
							LockMainActivity.class);
					AppLockApplication.getInstance().setStartGuide(true);
					startActivity(intent);
				}
				finish();
			} else {
				bPwdIsCorrent = false;
				mLockPatternView
						.setDisplayMode(LockPatternView.DisplayMode.Wrong);
				if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
					mFailedPatternAttemptsSinceLastTimeout++;
					int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT
							- mFailedPatternAttemptsSinceLastTimeout;
					if (retry >= 0) {
						if (retry == 0)
							ToastUtils.showToast(String.format(getResources()
									.getString(R.string.password_error_wait),
									delayTime[errorCount] / 1000 / 60));
						String format = getResources().getString(
								R.string.password_error_count);
						String str = String.format(format, retry);
						mHeadTextView.setText(str);
						mHeadTextView.setTextColor(getResources().getColor(
								R.color.text_red));
						mHeadTextView.startAnimation(mShakeAnim);
					}

				} else {
					ToastUtils.showToast(R.string.password_short);
				}

				if (mFailedPatternAttemptsSinceLastTimeout >= 3) {
					LookMyPrivate lookMyPrivate = new LookMyPrivate();
					lookMyPrivate.setLookDate(new Date());
					lookMyPrivate.setResolver("com.cleanwiz.applock");
					long id = pService.addNewLookMyPrivate(lookMyPrivate);
					lookMyPrivate.setId(id);
					if (appLockApplication.getAutoRecordPic()) {
						if (cameraFuncation != null) {
							cameraFuncation.lookMyPrivate = lookMyPrivate;
							LogUtil.e("colin", "解锁失败，拍照来哦啦");
							cameraFuncation.tackPicture();
						}
					}
					// 播放声音
					if (appLockApplication.getPlayWarringSoundState()) {
						playWarringSoundService.playSound();
					}
				}
				if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) {
					mHandler.postDelayed(attemptLockout, 2000);
				} else {
					mLockPatternView.postDelayed(mClearPatternRunnable, 2000);
				}
			}
		}

		public void onPatternCellAdded(List<Cell> pattern) {

		}

		private void patternInProgress() {
		}
	};
	Runnable attemptLockout = new Runnable() {

		@Override
		public void run() {
			LogUtil.e("colin",
					"errorCounterrorCounterrorCounterrorCounterrorCounterrorCount:"
							+ errorCount);
			mLockPatternView.clearPattern();
			mLockPatternView.setEnabled(false);
			long millsInFature = 0;
			if (bIsFalseStart) {
				bIsFalseStart = false;
				long defauleTime = new Date().getTime()
						- appLockApplication
								.getLastAppEnterPwdLeaverDateMiliseconds();
				if (defauleTime < appLockApplication
						.getLastAppEnterPwdDelayTime() * 1000) {
					millsInFature = appLockApplication
							.getLastAppEnterPwdDelayTime() * 1000 - defauleTime;
				}
			} else {
				millsInFature = delayTime[errorCount] + 1;
			}
			LogUtil.e("colin", "attemptLockout处理:" + millsInFature);
			mCountdownTimer = new CountDownTimer(millsInFature, 1000) {

				@Override
				public void onTick(long millisUntilFinished) {
					int secondsRemaining = (int) (millisUntilFinished / 1000) - 1;
					lastDelayTime = secondsRemaining;
					if (secondsRemaining > 0) {
						String format = getResources().getString(
								R.string.password_time);
						String str = String.format(format, secondsRemaining);
						mHeadTextView.setText(str);
					} else {
						mHeadTextView.setText(R.string.password_gestrue_tips);
						mHeadTextView.setTextColor(Color.WHITE);
					}

				}

				@Override
				public void onFinish() {
					mLockPatternView.setEnabled(true);
					mFailedPatternAttemptsSinceLastTimeout = 0;
					errorCount += 1;
					if (errorCount > 4) {
						errorCount = 0;
					}
				}
			}.start();
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

}
