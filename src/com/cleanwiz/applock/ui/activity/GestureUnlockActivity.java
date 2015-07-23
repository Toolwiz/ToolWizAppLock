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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.MyConstants;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.data.LookMyPrivate;
import com.cleanwiz.applock.service.CameraFuncation;
import com.cleanwiz.applock.service.LockService;
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
import com.cleanwiz.applock.utils.FastBlur;
import com.cleanwiz.applock.utils.LogUtil;
import com.cleanwiz.applock.utils.ToastUtils;

public class GestureUnlockActivity extends BaseActivity {

	private LockPatternView mLockPatternView;
	private int mFailedPatternAttemptsSinceLastTimeout = 0;
	private CountDownTimer mCountdownTimer = null;
	private Handler mHandler = new Handler();
	private TextView mHeadTextView;
	private Animation mShakeAnim;
	private ImageView mLockFaceImageView;
	private TextView tipsTextView;
	private RelativeLayout backLinearLayout;

	private Toast mToast;

	private View popView;
	private ActionView actionView;
	private ScaleAnimation pop_in;
	private ScaleAnimation pop_out;

	private String pkgName;
	private LookMyPrivateService pService;

	private int[] delayTime = { 60000, 120000, 180000, 600000, 1800000 };
	private int errorCount = 0;
	private boolean bPwdIsCorrent = true;
	private boolean bIsFalseStart = false;
	private int lastDelayTime = 0;
	private AppLockApplication appLockApplication = AppLockApplication
			.getInstance();

	private CameraFuncation cameraFuncation;
	private SurfaceView surfaceView;
	private PlayWarringSoundService playWarringSoundService;

	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture_unlock);
		mContext = this;
		pService = new LookMyPrivateService(getApplicationContext());
		playWarringSoundService = new PlayWarringSoundService(
				getApplicationContext());
		surfaceView = (SurfaceView) findViewById(R.id.picSurfaceView);
		cameraFuncation = new CameraFuncation(getApplicationContext(),
				surfaceView, pService);
		backLinearLayout = (RelativeLayout) findViewById(R.id.gesturepwd_root);
		mLockFaceImageView = (ImageView) findViewById(R.id.gesturepwd_unlock_face);
		tipsTextView = (TextView) findViewById(R.id.gesturepwd_unlock_text);
		mLockPatternView = (LockPatternView) this
				.findViewById(R.id.gesturepwd_unlock_lockview);
		mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
		mLockPatternView.setTactileFeedbackEnabled(true);
		mHeadTextView = (TextView) findViewById(R.id.gesturepwd_unlock_text);
		mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);

		pkgName = getIntent().getStringExtra(MyConstants.LOCK_PACKAGE_NAME);

		PackageManager packageManager = getPackageManager();
		ApplicationInfo appInfo;
		try {
			appInfo = packageManager.getApplicationInfo(pkgName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			if (appInfo != null) {
				mLockFaceImageView.setImageDrawable(packageManager
						.getApplicationIcon(appInfo));
				tipsTextView.setText(getString(R.string.password_gestrue_tips)
						+ " " + packageManager.getApplicationLabel(appInfo));
				final Drawable icon = packageManager
						.getApplicationIcon(appInfo);
				backLinearLayout.setBackgroundDrawable(icon);
				backLinearLayout.getViewTreeObserver().addOnPreDrawListener(
						new ViewTreeObserver.OnPreDrawListener() {

							@Override
							public boolean onPreDraw() {
								// TODO Auto-generated method stub
								backLinearLayout.getViewTreeObserver()
										.removeOnPreDrawListener(this);
								backLinearLayout.buildDrawingCache();
								Bitmap bmp = drawableToBitmap(icon);
								// backLayout.getDrawingCache();
								blur(big(bmp), backLinearLayout);
								return true;
							}
						});
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pService = new LookMyPrivateService(this);

		bPwdIsCorrent = appLockApplication.getLastUserEnterCorrentPwd();
		errorCount = appLockApplication.getLastUserEnterPwdErrorCount();
		LogUtil.e("colin", "状态为：" + bPwdIsCorrent + "11上次解锁密码错误，上次时间为:"
				+ appLockApplication.getLastUserEnterPwdDelayTime() + "错误次数为:"
				+ errorCount);
		if (!bPwdIsCorrent) {
			bIsFalseStart = true;
			long defauleTime = new Date().getTime()
					- appLockApplication
							.getLastUserEnterPwdLeaverDateMiliseconds();
			if (defauleTime < appLockApplication.getLastUserEnterPwdDelayTime() * 1000) {
				LogUtil.e("colin", "11上次解锁密码错误，时间孙艳");
				mHandler.postDelayed(attemptLockout, 100);
			} else {
				LogUtil.e("colin", "11上次解锁密码错误，时间不孙艳");
				bIsFalseStart = false;
				errorCount += 1;
				if (errorCount > 4) {
					errorCount = 0;
				}
				appLockApplication.setLastUserEnterPwdErrorCount(errorCount);
			}
		}

		// pop
		popView = findViewById(R.id.layout_pop);
		actionView = (ActionView) findViewById(R.id.btn_more);

		initAnim();
		String secretQuestion = AppLockApplication.getInstance()
				.getSecretQuestionString();
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

	public Bitmap drawableToBitmap(Drawable drawable) {
		int w = 20;
		int h = 20;
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas);

		int[] pixex = new int[w * h];
		List<Integer> trIndexs = new ArrayList<Integer>();
		for (int i = 0; i < bitmap.getHeight(); i++) {
			for (int j = 0; j < bitmap.getWidth(); j++) {
				int color = bitmap.getPixel(j, i);
				int alpha = Color.alpha(color);
				if (alpha < 200) {
					trIndexs.add(i * h + j);
				} else if (trIndexs.size() > 0) {
					for (Integer tr : trIndexs) {
						pixex[tr] = color;
					}
					trIndexs.clear();
					pixex[i * h + j] = color;
				} else {
					pixex[i * h + j] = color;
				}

			}
		}

		Bitmap bitmap2 = Bitmap.createBitmap(backLinearLayout.getWidth(),
				backLinearLayout.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas2 = new Canvas(bitmap2);
		RectF rectF = new RectF(0, 0, backLinearLayout.getWidth(),
				backLinearLayout.getHeight());
		canvas2.drawBitmap(Bitmap.createBitmap(pixex, w, h, Config.ARGB_8888),
				null, rectF, null);

		return bitmap2;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	boolean unlockFlag = false;

	@Override
	protected void onStop() {
		cameraFuncation.clearCamera();
		appLockApplication.setLastUserEnterPwdState(bPwdIsCorrent,
				new Date().getTime(), errorCount, lastDelayTime);
		if (!unlockFlag) {
			AppLockApplication.getInstance().goHome(this);
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cameraFuncation.clearCamera();
		appLockApplication.setLastUserEnterPwdState(bPwdIsCorrent,
				new Date().getTime(), errorCount, lastDelayTime);
		if (mCountdownTimer != null)
			mCountdownTimer.cancel();
	}

	@Override
	public void onClickEvent(View view) {
		switch (view.getId()) {
		case R.id.btn_more:
			btnClickMore();
			break;
		case R.id.gesturepwd_unlock_forget:
			unlockFlag = true;
			Intent intent = new Intent(this, SecretCheckActivity.class);
			intent.putExtra(SecretCheckActivity.COME_FROM_LOCK, true);
			intent.putExtra("fromUnlock", true);
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}
		super.onClickEvent(view);
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
				Intent intent = new Intent(LockService.LOCK_SERVICE_LASTTIME);
				intent.putExtra(LockService.LOCK_SERVICE_LASTTIME,
						new Date().getTime());
				sendBroadcast(intent);

				AppLockApplication.getInstance().setLastUnlockPkg(pkgName);
				unlockFlag = true;
				bPwdIsCorrent = true;
				getApplicationContext().sendBroadcast(new Intent("finish"));
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
					lookMyPrivate.setResolver(pkgName);
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
			mLockPatternView.clearPattern();
			mLockPatternView.setEnabled(false);
			long millsInFature = 0;
			if (bIsFalseStart) {
				bIsFalseStart = false;
				long defauleTime = new Date().getTime()
						- appLockApplication
								.getLastUserEnterPwdLeaverDateMiliseconds();
				if (defauleTime < appLockApplication
						.getLastUserEnterPwdDelayTime() * 1000) {
					millsInFature = appLockApplication
							.getLastUserEnterPwdDelayTime()
							* 1000
							- defauleTime;
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
					LogUtil.e("colin", "还有:" + lastDelayTime);
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
			AppLockApplication.getInstance().goHome(this);
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void blur(Bitmap bkg, View view) {
		long startMs = System.currentTimeMillis();
		float radius = 70;
		float scaleFactor = 8;

		Bitmap overlay = Bitmap.createBitmap(
				(int) (view.getMeasuredWidth() / scaleFactor),
				(int) (view.getMeasuredHeight() / scaleFactor),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(overlay);
		canvas.translate(-view.getLeft() / scaleFactor, -view.getTop()
				/ scaleFactor);
		canvas.scale(1 / scaleFactor, 1 / scaleFactor);
		Paint paint = new Paint();
		paint.setFlags(Paint.FILTER_BITMAP_FLAG);
		canvas.drawBitmap(bkg, 0, 0, paint);
		overlay = FastBlur.doBlur(overlay, (int) radius, true);
		view.setBackgroundDrawable(new BitmapDrawable(getResources(), overlay));
	}

	private static Bitmap big(Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.postScale(2.5f, 2.5f);
		LogUtil.e("colin", "bitmap.getWidth:" + bitmap.getWidth()
				+ "bitmap.getHeight" + bitmap.getHeight());
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 4,
				bitmap.getHeight() / 4, bitmap.getWidth() / 2 - 1,
				bitmap.getHeight() / 2 - 1, matrix, true);
		return handleImage(resizeBmp, 85);
	}

	public static Bitmap handleImage(Bitmap bm, int hue) {
		Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		ColorMatrix mHueMatrix = new ColorMatrix();
		ColorMatrix mAllMatrix = new ColorMatrix();
		float mHueValue = hue * 1.0F / 127;
		mHueMatrix.reset();
		mHueMatrix.setScale(mHueValue, mHueValue, mHueValue, 1);

		mAllMatrix.reset();
		mAllMatrix.postConcat(mHueMatrix);

		paint.setColorFilter(new ColorMatrixColorFilter(mAllMatrix));
		canvas.drawBitmap(bm, 0, 0, paint);
		return bmp;
	}
}
