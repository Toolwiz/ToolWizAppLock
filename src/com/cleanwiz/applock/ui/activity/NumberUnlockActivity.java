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
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.cleanwiz.applock.ui.widget.actionview.ActionView;
import com.cleanwiz.applock.ui.widget.actionview.CloseAction;
import com.cleanwiz.applock.ui.widget.actionview.MoreAction;
import com.cleanwiz.applock.utils.FastBlur;
import com.cleanwiz.applock.utils.LogUtil;
import com.cleanwiz.applock.utils.SharedPreferenceUtil;
import com.cleanwiz.applock.utils.StringUtils;
import com.cleanwiz.applock.utils.ToastUtils;

public class NumberUnlockActivity extends BaseActivity {

	public static final String CHANGE_PASSWORD = "change_password";

	private TextView mHeadTextView;
	private Animation mShakeAnim;
	private ImageView mAppImageView;
	private RelativeLayout backLayout;

	private Handler mHandler = new Handler();

	private boolean changeFlag;
	private boolean numberDisable = false;
	private CountDownTimer mCountdownTimer = null;

	private int mFailedPatternAttemptsSinceLastTimeout = 0;

	private String pkgName;
	private String appLabel;
	private LookMyPrivateService pService;
	private PlayWarringSoundService playWarringSoundService;

	private int[] delayTime = { 60000, 120000, 180000, 600000, 1800000 };
	private int errorCount = 0;
	private boolean bPwdIsCorrent = true;
	private boolean bIsFalseStart = false;
	private int lastDelayTime = 0;
	private AppLockApplication appLockApplication = AppLockApplication
			.getInstance();

	private CameraFuncation cameraFuncation;
	private SurfaceView surfaceView;

	private View popView;
	private ActionView actionView;
	private ScaleAnimation pop_in;
	private ScaleAnimation pop_out;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_num_unlock);
		pService = new LookMyPrivateService(getApplicationContext());
		playWarringSoundService = new PlayWarringSoundService(
				getApplicationContext());
		surfaceView = (SurfaceView) findViewById(R.id.picSurfaceView);
		cameraFuncation = new CameraFuncation(getApplicationContext(),
				surfaceView, pService);
		backLayout = (RelativeLayout) findViewById(R.id.gesturepwd_root);
		mAppImageView = (ImageView) findViewById(R.id.iv_app);
		mHeadTextView = (TextView) findViewById(R.id.tv_text);
		mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);
		pkgName = getIntent().getStringExtra(MyConstants.LOCK_PACKAGE_NAME);
		PackageManager packageManager = getPackageManager();
		ApplicationInfo appInfo;
		try {
			appInfo = packageManager.getApplicationInfo(pkgName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			if (appInfo != null) {
				mAppImageView.setImageDrawable(packageManager
						.getApplicationIcon(appInfo));
				appLabel = (String) packageManager.getApplicationLabel(appInfo);
				mHeadTextView.setText(getString(R.string.num_create_text_01)
						+ " " + appLabel);
				final Drawable icon = packageManager
						.getApplicationIcon(appInfo);
				backLayout.setBackgroundDrawable(icon);
				backLayout.getViewTreeObserver().addOnPreDrawListener(
						new OnPreDrawListener() {

							@Override
							public boolean onPreDraw() {
								// TODO Auto-generated method stub
								backLayout.getViewTreeObserver()
										.removeOnPreDrawListener(this);
								backLayout.buildDrawingCache();
								Bitmap bmp = drawableToBitmap(icon);
								blur(big(bmp), backLayout);
								return true;
							}
						});
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pService = new LookMyPrivateService(this);

		initNumLayout();
		
		changeFlag = getIntent().getBooleanExtra(CHANGE_PASSWORD, false);
		if (changeFlag) {
			findViewById(R.id.btn_user_model).setVisibility(View.INVISIBLE);
		}
		bPwdIsCorrent = appLockApplication.getLastUserEnterCorrentPwd();
		errorCount = appLockApplication.getLastUserEnterPwdErrorCount();
		if (!bPwdIsCorrent) {
			bIsFalseStart = true;
			errorCount = appLockApplication.getLastUserEnterPwdErrorCount();
			long defauleTime = new Date().getTime()
					- appLockApplication
							.getLastUserEnterPwdLeaverDateMiliseconds();
			LogUtil.e("colin", "上次解锁密码错误，到现在的时间为:" + defauleTime + "上次时间为:"
					+ appLockApplication.getLastUserEnterPwdDelayTime());
			if (defauleTime < appLockApplication.getLastUserEnterPwdDelayTime() * 1000) {
				LogUtil.e("colin", "上次解锁密码错误，时间孙艳");
				mHandler.postDelayed(attemptLockout, 100);
			} else {
				LogUtil.e("colin", "上次解锁密码错误，时间不孙艳");
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
		super.onCreate(savedInstanceState);
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

		Bitmap bitmap2 = Bitmap.createBitmap(backLayout.getWidth(),
				backLayout.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas2 = new Canvas(bitmap2);
		RectF rectF = new RectF(0, 0, backLayout.getWidth(),
				backLayout.getHeight());
		canvas2.drawBitmap(Bitmap.createBitmap(pixex, w, h, Config.ARGB_8888),
				null, rectF, null);

		return bitmap2;
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
	protected void onResume() {
		super.onResume();
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
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
		super.onClickEvent(view);
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

		if (numberDisable) {
			return;
		}

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
		SUCCESS, ERROR, CONTINUE
	}

	private void doForResult(InputResult result) {
		switch (result) {
		case CONTINUE:
			break;
		case SUCCESS:
			Intent intent = new Intent(LockService.LOCK_SERVICE_LASTTIME);
			intent.putExtra(LockService.LOCK_SERVICE_LASTTIME,
					new Date().getTime());
			sendBroadcast(intent);
			AppLockApplication.getInstance().setLastUnlockPkg(pkgName);
			unlockFlag = true;
			bPwdIsCorrent = true;
			getApplicationContext().sendBroadcast(new Intent("finish"));
			finish();
			break;

		case ERROR:
			bPwdIsCorrent = false;
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
				numberDisable = true;
				mHandler.postDelayed(clearPassword, 2000);
			}

			break;

		default:
			break;
		}
	}

	private InputResult inputCheck(String password) {
		InputResult result;
		LogUtil.d("demo3", "input:" + password);
		if (numInput.size() == COUNT) {
			numInput.clear();
			String md5 = StringUtils.toMD5(password);
			if (md5.equals(SharedPreferenceUtil.readNumPassword())) {
				result = InputResult.SUCCESS;
			} else {
				result = InputResult.ERROR;
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

	private Runnable clearPassword = new Runnable() {
		public void run() {
			for (ImageView iv : pointList) {
				iv.setImageResource(R.drawable.num_point);
			}
			numberDisable = false;
		}
	};

	Runnable attemptLockout = new Runnable() {

		@Override
		public void run() {
			numberDisable = true;
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
					if (secondsRemaining > 0) {
						String format = getResources().getString(
								R.string.password_time);
						String str = String.format(format, secondsRemaining);
						mHeadTextView.setText(str);
					} else {
						mHeadTextView
								.setText(getString(R.string.num_create_text_01)
										+ appLabel);
						mHeadTextView.setTextColor(Color.WHITE);
					}

				}

				@Override
				public void onFinish() {
					for (ImageView iv : pointList) {
						iv.setImageResource(R.drawable.num_point);
					}
					numberDisable = false;
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
