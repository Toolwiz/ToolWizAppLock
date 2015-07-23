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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.R;
import com.cleanwiz.applock.data.UpdateVersionManafer;
import com.cleanwiz.applock.service.ImageService;
import com.cleanwiz.applock.service.UpdateService;
import com.cleanwiz.applock.service.UpdateVersionManagerService;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.ui.activity.SplashActivity.SplashHandler;
import com.cleanwiz.applock.ui.adapter.MainPagerAdapter;
import com.cleanwiz.applock.ui.anim.PopListener;
import com.cleanwiz.applock.ui.widget.actionview.*;
import com.cleanwiz.applock.utils.*;
import com.gc.materialdesign.widgets.Dialog;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;

import java.io.File;
import java.util.Date;
import java.util.List;

public class LockMainActivity extends BaseActivity {

	private LockMainActivity mContext;

	private DrawerLayout drawerLayout;
	private ViewPager vp_main;
	private MainPagerAdapter mainAdapter;
	private View popView;
	private View pop_background;

	private TextView tv_tab_box;
	private TextView tv_tab_lock;
	private TextView tv_lock_status;
	private ImageView drawer_logo;
	private ActionView actionView;
	private View tab_thumb;
	private Animation tab_left;
	private Animation tab_right;
	private AlphaAnimation tab_alpha_1;
	private AlphaAnimation tab_alpha_2;
	private ScaleAnimation pop_in;
	private ScaleAnimation pop_out;

	private TextView txt_drawer_version_num;
	private TextView txt_drawer_info_reply;
	private ImageView btn_menu;

	private int tabW = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = LockMainActivity.this;
		setContentView(R.layout.activity_lock_main);
		setStatusBarMargin(findViewById(R.id.menu));
		tabW = ScreenUtil.getScreenDispaly(mContext)[0] / 2;
		initView();
		initAnim();
		ImageService imageService = new ImageService(getApplicationContext());
		imageService.getList();

		startFirstServices();
		doUpdate();
	}

	private void initView() {
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		RelativeLayout menu = (RelativeLayout) findViewById(R.id.menu);
		int width = getResources().getDisplayMetrics().widthPixels
				- DensityUtil.dip2px(mContext, 56 - 8);
		DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) menu
				.getLayoutParams();
		params.width = width;
		menu.setLayoutParams(params);
		drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
			@Override
			public void onDrawerSlide(View view, float v) {

			}

			@Override
			public void onDrawerOpened(View view) {
				closePopView();
				updateLockStatus();
			}

			@Override
			public void onDrawerClosed(View view) {

			}

			@Override
			public void onDrawerStateChanged(int i) {

			}
		});

		tv_lock_status = (TextView) findViewById(R.id.tv_lock_status);
		drawer_logo = (ImageView) findViewById(R.id.drawer_logo);

		actionView = (ActionView) findViewById(R.id.btn_more);
		tv_tab_box = (TextView) findViewById(R.id.tab_box);
		tv_tab_lock = (TextView) findViewById(R.id.tab_lock);

		// tab_thumb
		tab_thumb = findViewById(R.id.tab_thumb);
		RelativeLayout.LayoutParams thumbLp = (RelativeLayout.LayoutParams) tab_thumb
				.getLayoutParams();
		thumbLp.width = 0;
		tab_thumb.requestLayout();

		// vp_main
		vp_main = (ViewPager) findViewById(R.id.vp_main);
		mainAdapter = new MainPagerAdapter(mContext);
		vp_main.setAdapter(mainAdapter);
		vp_main.setOnPageChangeListener(new PagerListener());

		// pop
		popView = findViewById(R.id.layout_pop);
		pop_background = findViewById(R.id.pop_background);
		pop_background.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closePopView();
			}
		});
		pop_background.setVisibility(View.INVISIBLE);

		// image info
		btn_menu = (ImageView) findViewById(R.id.btn_menu);

		// txt
		txt_drawer_version_num = (TextView) findViewById(R.id.txt_drawer_version_num);
		txt_drawer_info_reply = (TextView) findViewById(R.id.txt_drawer_info_reply);
	}

	private void initAnim() {

		long duration = 300;
		long durationS = 160;
		float alpha = 0.3f;
		AccelerateInterpolator accInterpolator = new AccelerateInterpolator();

		tab_left = new TranslateAnimation(tabW, 0, 0, 0);
		tab_right = new TranslateAnimation(0, tabW, 0, 0);
		tab_alpha_1 = new AlphaAnimation(1.0f, alpha);
		tab_alpha_2 = new AlphaAnimation(alpha, 1.0f);
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

		tab_left.setFillAfter(true);
		tab_left.setFillEnabled(true);
		tab_left.setDuration(duration);
		tab_left.setInterpolator(accInterpolator);

		tab_right.setFillAfter(true);
		tab_right.setFillEnabled(true);
		tab_right.setDuration(duration);
		tab_right.setInterpolator(accInterpolator);

		tab_alpha_1.setFillAfter(true);
		tab_alpha_1.setFillEnabled(true);
		tab_alpha_1.setDuration(duration);
		tab_alpha_1.setInterpolator(accInterpolator);

		tab_alpha_2.setFillAfter(true);
		tab_alpha_2.setFillEnabled(true);
		tab_alpha_2.setDuration(duration);
		tab_alpha_2.setInterpolator(accInterpolator);

		AlphaAnimation alphaInit = new AlphaAnimation(alpha, alpha);
		alphaInit.setFillAfter(true);
		alphaInit.setFillEnabled(true);
		tv_tab_box.startAnimation(alphaInit);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClickEvent(View view) {
		switch (view.getId()) {
		case R.id.btn_menu:
			drawerLayout.openDrawer(Gravity.LEFT);
			break;
		case R.id.btn_more:
			btnClickMore();
			break;
		case R.id.tab_box:
			vp_main.setCurrentItem(1, true);
			break;
		case R.id.tab_lock:
			vp_main.setCurrentItem(0, true);
			break;
		case R.id.lr_pop_log:
			startActivity(new Intent(this, LookMyPrivateActivity.class));
			closePopView();
			break;
		case R.id.lr_pop_set:
			startActivity(new Intent(this, SettingActivity.class));
			closePopView();
			break;
		case R.id.slide_share:
			share();
			break;
		case R.id.slide_feedback:
			feedback();
			break;
		case R.id.slide_check_update:
			checkVersion();
			break;
		case R.id.slide_app:
			recommendApp();
			break;
		case R.id.drawer_logo:
			break;
		default:
			break;
		}
		super.onClickEvent(view);
	}

	private void updateLockStatus() {
		AppLockApplication appLockApplication = AppLockApplication
				.getInstance();
		if (appLockApplication.appLockState) {
			tv_lock_status.setText(R.string.server_startlock_detail);
			drawer_logo.setImageResource(R.drawable.slide_logo);
		} else {
			tv_lock_status.setText(R.string.server_unlock_detail);
			drawer_logo.setImageResource(R.drawable.slide_logo_un);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 保险箱数据初始化
		mainAdapter.resetSafeBox();
		if (AppLockApplication.getInstance().isNeedSetSecret()) {
			startActivity(new Intent(this, SecretConfig.class));
			AppLockApplication.getInstance().setStartGuide(false);
		}

		sysFeedback();
	}

	private void closePopView() {
		if (View.VISIBLE == popView.getVisibility()) {
			actionView.setAction(new MoreAction(),
					ActionView.ROTATE_COUNTER_CLOCKWISE);
			popView.clearAnimation();
			popView.startAnimation(pop_out);
			pop_background.setVisibility(View.INVISIBLE);
		}
	}

	private void btnClickMore() {
		if (View.VISIBLE == popView.getVisibility()) {
			closePopView();
		} else {
			actionView.setAction(new CloseAction(),
					ActionView.ROTATE_COUNTER_CLOCKWISE);
			popView.clearAnimation();
			popView.startAnimation(pop_in);
			pop_background.setVisibility(View.VISIBLE);
		}

	}

	int flag = 0;

	public void actionMore() {

		switch (++flag) {
		case 1:
			actionView.setAction(new BackAction(),
					ActionView.ROTATE_COUNTER_CLOCKWISE);
			break;
		case 2:
			actionView.setAction(new MoreAction(),
					ActionView.ROTATE_COUNTER_CLOCKWISE);
			break;
		case 3:
			actionView.setAction(new DrawerAction(),
					ActionView.ROTATE_COUNTER_CLOCKWISE);
			break;
		case 4:
			actionView.setAction(new PlusAction(),
					ActionView.ROTATE_COUNTER_CLOCKWISE);
			break;
		case 5:
			actionView.setAction(new CloseAction(),
					ActionView.ROTATE_COUNTER_CLOCKWISE);
			break;
		}

		if (flag == 5)
			flag = 0;

	}

	class PagerListener implements ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			if (arg0 == 0) {
				tab_thumb.clearAnimation();
				tab_thumb.startAnimation(tab_left);
				tv_tab_box.clearAnimation();
				tv_tab_box.startAnimation(tab_alpha_1);
				tv_tab_lock.clearAnimation();
				tv_tab_lock.startAnimation(tab_alpha_2);
			} else {
				tab_thumb.clearAnimation();
				tab_thumb.startAnimation(tab_right);
				tv_tab_box.clearAnimation();
				tv_tab_box.startAnimation(tab_alpha_2);
				tv_tab_lock.clearAnimation();
				tv_tab_lock.startAnimation(tab_alpha_1);
			}
		}

	}

	void checkVersion() {
		AppLockApplication appLockApplication = AppLockApplication
				.getInstance();
		if (appLockApplication.hasNewVersion()) {
			downLoadFileUrl = appLockApplication.getUpdateVersionUrl();
			showUpdateDialog(appLockApplication.getUpdateVersionIntro());
		} else {
			ToastUtils.showToast(R.string.no_new_version);
		}
	}

	void recommendApp() {
		startActivity(new Intent(this, AppsLinkActivity.class));
	}

	void feedback() {
		startActivity(new Intent(this, FeedbackActivity.class));
	}

	void share() {
		Bitmap bm = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher);
		String imgPath = FileService.createShareImage(bm, this);
		String title = getString(R.string.pwdsetting_share_detail);
		String text = getString(R.string.pwdsetting_share_text);

		shareMsg(title, title, text, imgPath);
	}

	public void shareMsg(String activityTitle, String msgTitle, String msgText,
			String imgPath) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		if (imgPath == null || imgPath.equals("")) {
			intent.setType("text/plain");
		} else {
			File f = new File(imgPath);
			if (f.exists() && f.isFile()) {
				intent.setType("image/jpg");
				Uri uri = Uri.fromFile(f);
				intent.putExtra(Intent.EXTRA_STREAM, uri);
			}
		}
		intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
		intent.putExtra(Intent.EXTRA_TEXT, msgText);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, activityTitle));
	}

	String downLoadFileUrl = "";

	public void showUpdateDialog(String intro) {
		final Dialog dialog = new Dialog(this,
				getString(R.string.update_title), intro);
		dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Intent updateIntent = new Intent(LockMainActivity.this,
						UpdateService.class);
				updateIntent.putExtra("appUrl", downLoadFileUrl);
				startService(updateIntent);
			}
		});
		dialog.addCancelButton(getString(R.string.cancel),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
		dialog.show();
		dialog.getButtonAccept().setText(getString(R.string.update_buttontext));
	}

	public void startFirstServices() {
		if (!SharedPreferenceUtil.readEnterFlag()) {
			LogUtil.e("colin", "testService_start");
			startService(new Intent("com.cleanwiz.applock.service.LockService")
					.setPackage("com.cleanwiz.applock"));
			SharedPreferenceUtil.editEnterFlag(true);
		}
	}

	// 定义一个变量，来标识是否退出
	private static boolean isExit = false;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(),
					getString(R.string.enter_double_exit), Toast.LENGTH_SHORT)
					.show();
			// 利用handler延迟发送更改状态信息
			mHandler.sendEmptyMessageDelayed(0, 3000);
		} else {
			finish();
		}
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};

	private FeedbackAgent mAgent;
	private Conversation mComversation;

	/**
	 * 反馈意见收到条目数
	 */
	private void sysFeedback() {

		AppLockApplication appLockApplication = AppLockApplication
				.getInstance();
		txt_drawer_version_num.setText(getString(R.string.version_now)
				+ appLockApplication.getApplicationVersion());

		setReplySize(appLockApplication.getReplySize());

		mAgent = new FeedbackAgent(this);
		mComversation = mAgent.getDefaultConversation();
		sync();
	}

	// 数据同步
	private void sync() {
		mComversation.sync(new SyncListener() {
			@Override
			public void onSendUserReply(List<Reply> replyList) {

			}

			@Override
			public void onReceiveDevReply(List<Reply> replyList) {
				AppLockApplication appLockApplication = AppLockApplication
						.getInstance();
				int count = appLockApplication.getReplySize();
				if (replyList != null) {
					count += replyList.size();
				}
				appLockApplication.setReplySize(count);
				setReplySize(count);
			}
		});
	}

	private void setReplySize(int count) {
		if (count != 0) {
			txt_drawer_info_reply.setText(getString(R.string.received_replies,
					count));
			btn_menu.setImageResource(R.drawable.menu_p);
		} else {
			txt_drawer_info_reply.setText("");
			btn_menu.setImageResource(R.drawable.menu);
		}
	}

	private void doUpdate() {
		AppLockApplication appLockApplication = AppLockApplication
				.getInstance();
		if (System.currentTimeMillis()
				- SharedPreferenceUtil.readUpdateTipTime() >= 1000 * 60 * 60 * 20
				&& appLockApplication.hasNewVersion()) {
			downLoadFileUrl = appLockApplication.getUpdateVersionUrl();
			showUpdateDialog(appLockApplication.getUpdateVersionIntro());
			SharedPreferenceUtil.editUpdateTipTime(System.currentTimeMillis());
		}
	}

}
