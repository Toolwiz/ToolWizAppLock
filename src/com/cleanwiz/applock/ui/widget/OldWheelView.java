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
package com.cleanwiz.applock.ui.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cleanwiz.applock.R;
import com.cleanwiz.applock.utils.ABViewUtil;
import com.cleanwiz.applock.utils.DensityUtil;
import com.cleanwiz.applock.utils.ScreenUtil;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 7/1/14.
 */
public class OldWheelView extends ScrollView {
	public static final String TAG = OldWheelView.class.getSimpleName();

	public static class OnWheelViewListener {
		public void onSelected(int selectedIndex, WheelItem item) {
		}

		;
	}

	private Context context;
	private LayoutInflater inflater;

	private LinearLayout views;

	public OldWheelView(Context context) {
		super(context);
		init(context);
	}

	public OldWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public OldWheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	// String[] items;
	List<WheelItem> items;

	public List<WheelItem> getItems() {
		return items;
	}

	public void setItems(List<WheelItem> list) {
		if (null == items) {
			items = new ArrayList<WheelItem>();
		}
		items.clear();
		items.addAll(list);

		// 前面和后面补全
		for (int i = 0; i < offset; i++) {
			items.add(0, new WheelItem(TYPE_OFFSET, 0, 0, ""));
			items.add(new WheelItem(TYPE_OFFSET, 0, 0, ""));
		}

		initData();

	}

	public static final int OFF_SET_DEFAULT = 1;
	int offset = OFF_SET_DEFAULT; // 偏移量（需要在最前面和最后面补全）

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	int displayItemCount; // 每页显示的数量

	int selectedIndex = 1;

	private void init(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.setVerticalScrollBarEnabled(false);

		views = new LinearLayout(context);
		views.setOrientation(LinearLayout.VERTICAL);
		this.addView(views);

		scrollerTask = new Runnable() {

			public void run() {

				int newY = getScrollY();
				if (initialY - newY == 0) {
					final int remainder = initialY % itemHeight;
					final int divided = initialY / itemHeight;
					if (remainder == 0) {
						selectedIndex = divided + offset;

						onSeletedCallBack();
					} else {
						if (remainder > itemHeight / 2) {
							OldWheelView.this.post(new Runnable() {
								@Override
								public void run() {
									OldWheelView.this.smoothScrollTo(0, initialY
											- remainder + itemHeight);
									selectedIndex = divided + offset + 1;
									onSeletedCallBack();
								}
							});
						} else {
							OldWheelView.this.post(new Runnable() {
								@Override
								public void run() {
									OldWheelView.this.smoothScrollTo(0, initialY
											- remainder);
									selectedIndex = divided + offset;
									onSeletedCallBack();
								}
							});
						}

					}

				} else {
					initialY = getScrollY();
					OldWheelView.this.postDelayed(scrollerTask, newCheck);
				}
			}
		};

	}

	int initialY;

	Runnable scrollerTask;
	int newCheck = 50;

	public void startScrollerTask() {

		initialY = getScrollY();
		this.postDelayed(scrollerTask, newCheck);
	}

	private void initData() {
		displayItemCount = offset * 2 + 1;

		for (WheelItem item : items) {
			views.addView(createView(item));
		}

		refreshItemView(0);
	}

	int itemHeight = 0;

	private View createView(WheelItem item) {

		View itemView = inflater.inflate(R.layout.item_wheel_time, null);

		TextView tv = (TextView) itemView.findViewById(R.id.tv_text);
		RelativeLayout layout = (RelativeLayout) itemView
				.findViewById(R.id.rl_wv);

		tv.setLayoutParams(new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		tv.setSingleLine(true);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
		tv.setText("   ");
		tv.setGravity(Gravity.CENTER);

		if (item.type == TYPE_TEXT) {
			tv.setText(item.getText());
		}

		int padding = ScreenUtil.dip2px(context, 4);
		tv.setPadding(padding, padding, padding, padding);

		if (0 == itemHeight) {
			itemHeight = ABViewUtil.getViewMeasuredHeight(tv);
			views.setLayoutParams(new LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, itemHeight
							* displayItemCount));
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this
					.getLayoutParams();
			this.setLayoutParams(new LinearLayout.LayoutParams(lp.width,
					itemHeight * displayItemCount));
		}
		return itemView;

	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);

		refreshItemView(t);

		if (t > oldt) {
			scrollDirection = SCROLL_DIRECTION_DOWN;
		} else {
			scrollDirection = SCROLL_DIRECTION_UP;

		}

	}

	private void refreshItemView(int y) {
		int position = y / itemHeight + offset;
		int remainder = y % itemHeight;
		int divided = y / itemHeight;

		if (remainder == 0) {
			position = divided + offset;
		} else {
			if (remainder > itemHeight / 2) {
				position = divided + offset + 1;
			}
		}

	}

	/**
	 * 获取选中区域的边界
	 */
	int[] selectedAreaBorder;

	private int[] obtainSelectedAreaBorder() {
		if (null == selectedAreaBorder) {
			selectedAreaBorder = new int[2];
			selectedAreaBorder[0] = itemHeight * offset;
			selectedAreaBorder[1] = itemHeight * (offset + 1);
		}
		return selectedAreaBorder;
	}

	private int scrollDirection = -1;
	private static final int SCROLL_DIRECTION_UP = 0;
	private static final int SCROLL_DIRECTION_DOWN = 1;

	Paint paint;
	int viewWidth;

	@Override
	public void setBackgroundDrawable(Drawable background) {
		super.setBackgroundDrawable(background);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		viewWidth = w;
		setBackgroundDrawable(null);
	}

	/**
	 * 选中回调
	 */
	private void onSeletedCallBack() {
		if (null != onWheelViewListener) {

			if (selectedIndex < 0) {
				selectedIndex = 0;
			} else if (selectedIndex > items.size() - 1) {
				selectedIndex = items.size() - 1;
			}

			onWheelViewListener.onSelected(selectedIndex,
					items.get(selectedIndex));
		}

	}

	public void setSeletion(int position) {
		final int p = position;
		selectedIndex = p + offset;
		this.post(new Runnable() {
			@Override
			public void run() {
				OldWheelView.this.smoothScrollTo(0, p * itemHeight);
			}
		});

	}

	public WheelItem getSeletedItem() {
		return items.get(selectedIndex);
	}

	public int getSeletedIndex() {
		return selectedIndex - offset;
	}

	@Override
	public void fling(int velocityY) {
		super.fling(velocityY / 3);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_UP) {

			startScrollerTask();
		}
		return super.onTouchEvent(ev);
	}

	private OnWheelViewListener onWheelViewListener;

	public OnWheelViewListener getOnWheelViewListener() {
		return onWheelViewListener;
	}

	public void setOnWheelViewListener(OnWheelViewListener onWheelViewListener) {
		this.onWheelViewListener = onWheelViewListener;
	}

	public static final int TYPE_TEXT = 0;
	public static final int TYPE_COLOR = 1;
	public static final int TYPE_OFFSET = 2;

	public static class WheelItem {

		private int type;
		private int arg1;
		private int arg2;
		private String text;

		public WheelItem(int type, int arg1, int arg2, String text) {
			super();
			this.type = type;
			this.arg1 = arg1;
			this.arg2 = arg2;
			this.text = text;
		}

		public int getType() {
			return type;
		}

		public int getArg1() {
			return arg1;
		}

		public int getArg2() {
			return arg2;
		}

		public String getText() {
			return text;
		}

	}

}
