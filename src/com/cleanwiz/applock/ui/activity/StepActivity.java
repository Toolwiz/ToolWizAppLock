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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;

import com.cleanwiz.applock.R;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.utils.LangUtils;
import com.cleanwiz.applock.utils.ScreenUtil;
import com.cleanwiz.applock.utils.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.List;

public class StepActivity extends BaseActivity {

	private StepActivity mContext;

	private ViewPager viewPager;
	private PagerAdapter pAdapter;
	private LayoutInflater inflater;

	boolean isCn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step);
		setStatusBarMargin(findViewById(R.id.layout_setting));
		mContext = this;
		isCn = LangUtils.isChinese();
		inflater = LayoutInflater.from(this);
		viewPager = (ViewPager) findViewById(R.id.vp_step);
		pAdapter = new MyViewPagerAdapter();
		viewPager.setAdapter(pAdapter);
		initPoints(pAdapter.getCount());
		viewPager.setOnPageChangeListener(new PageListener());

	}

	@Override
	public void onClickEvent(View view) {
		switch (view.getId()) {
		case R.id.btn_step:
			goToPassword();
			break;
		case R.id.btn_skip:
			goToPassword();
			break;
		default:
			break;
		}
		super.onClickEvent(view);
	}

	private void goToPassword() {
		if (SharedPreferenceUtil.readIsNumModel()) {
			startActivity(new Intent(this, NumberCheckActivity.class));
			finish();
		} else {
			startActivity(new Intent(this, GestureCheckActivity.class));
			finish();
		}
		SharedPreferenceUtil.editIsFirst(false);
	}

	private LinearLayout layout_points;
	private List<ImageView> points;

	private void initPoints(int pagers) {

		layout_points = (LinearLayout) findViewById(R.id.layout_point);
		layout_points.removeAllViews();
		points = new ArrayList<ImageView>();
		int width = ScreenUtil.dip2px(mContext, 24);
		int height = ScreenUtil.dip2px(mContext, 16);
		for (int i = 0; i < pagers; i++) {
			ImageView iv = new ImageView(mContext);
			iv.setScaleType(ScaleType.CENTER_INSIDE);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,
					height);
			if (i == 0) {
				iv.setImageResource(R.drawable.pager_point_white);
			} else {
				iv.setImageResource(R.drawable.pager_point_green);
			}
			layout_points.addView(iv, lp);
			points.add(iv);
		}

	}

	class PageListener implements ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			for (ImageView iv : points) {
				iv.setImageResource(R.drawable.pager_point_green);
			}
			try {
				points.get(arg0).setImageResource(R.drawable.pager_point_white);
			} catch (Exception e) {
			}

		}

	}

	public class MyViewPagerAdapter extends PagerAdapter {

		private List<View> mListViews;

		public MyViewPagerAdapter() {
			mListViews = new ArrayList<View>();
			View step0 = inflater.inflate(R.layout.item_step1, null);
			ImageView iv0 = (ImageView) step0.findViewById(R.id.iv_step);
			iv0.setImageResource(isCn ? R.drawable.nav01 : R.drawable.nav01_e);
			View step1 = inflater.inflate(R.layout.item_step1, null);
			ImageView iv1 = (ImageView) step1.findViewById(R.id.iv_step);
			iv1.setImageResource(isCn ? R.drawable.nav02 : R.drawable.nav02_e);
			View step2 = inflater.inflate(R.layout.item_step1, null);
			ImageView iv2 = (ImageView) step2.findViewById(R.id.iv_step);
			iv2.setImageResource(isCn ? R.drawable.nav03 : R.drawable.nav03_e);

			step2.findViewById(R.id.btn_step).setVisibility(View.VISIBLE);
			mListViews.add(step0);
			mListViews.add(step1);
			mListViews.add(step2);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mListViews.get(position));// 删除页卡
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) { // 这个方法用来实例化页卡
			container.addView(mListViews.get(position), 0);// 添加页卡
			return mListViews.get(position);
		}

		@Override
		public int getCount() {
			return mListViews.size();// 返回页卡的数量
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;// 官方提示这样写
		}
	}

}
