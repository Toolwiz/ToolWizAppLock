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
package com.cleanwiz.applock.ui.widget.actionview;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.cleanwiz.applock.R;

/**
 * ActionView allows you to dynamically display action and automatically animate
 * from one action to another.
 * <p/>
 * ActionView can be included in your layout xml quite easy:
 * 
 * <pre>
 * {@code
 * <at.markushi.ui.ActionView
 *      android:id="@+id/action"
 *      android:layout_width="56dip"
 *      android:layout_height="56dip"
 *      android:padding="16dip"
 *      app:av_color="@android:color/white"
 *      app:av_action="drawer"/>
 * }
 * </pre>
 * 
 * In order to start a transition to another {@link Action} call
 * {@link #setAction(Action)}
 * 
 * @see BackAction
 * @see CloseAction
 * @see DrawerAction
 * @see PlusAction
 */
public class ActionView extends View {

	public static final int ROTATE_CLOCKWISE = 0;
	public static final int ROTATE_COUNTER_CLOCKWISE = 1;
	private static final int STROKE_SIZE_DIP = 2;
	private static final int STROKE_SIZE_PLUS = 3;

	private int color;
	private Action oldAction;
	private Action currentAction;
	private Action animatedAction;

	private float animationProgress;
	private float scale;
	private int padding;
	private Path path;
	private Paint paint;

	private float centerX;
	private float centerY;
	private boolean ready = false;
	private boolean animateWhenReady = false;
	private int rotation = ROTATE_CLOCKWISE;
	private long animationDuration;
	private int size;

	private float strokeSize;
	private float strokeSizePlus;

	public ActionView(Context context) {
		this(context, null);
	}

	public ActionView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ActionView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		animationDuration = getResources().getInteger(
				R.integer.av_animationDuration);
		animatedAction = new Action(new float[Action.ACTION_SIZE], null);

		strokeSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				STROKE_SIZE_DIP, context.getResources().getDisplayMetrics());
		strokeSizePlus = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				STROKE_SIZE_PLUS, context.getResources().getDisplayMetrics());

		path = new Path();
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(0xDDFFFFFF);
		paint.setStrokeWidth(strokeSize);
		paint.setStyle(Paint.Style.STROKE);

		if (attrs == null) {
			return;
		}
		final TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ActionView);
		final int color = a
				.getColor(R.styleable.ActionView_av_color, 0xDDFFFFF);
		final int actionId = a.getInt(R.styleable.ActionView_av_action, 0);
		a.recycle();

		paint.setColor(color);
		final Action action = getActionFromEnum(actionId);
		setAction(action);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.centerX = w / 2;
		this.centerY = h / 2;
		padding = getPaddingLeft();
		size = Math.min(w, h);
		scale = Math.min(w, h) - (2 * padding);
		ready = true;
		transformActions();

		if (animateWhenReady) {
			animateWhenReady = false;
			startAnimation();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (currentAction == null) {
			return;
		}

		if (oldAction == null) {
			updatePath(currentAction);
		} else {
			final float inverseProgress = 1f - animationProgress;
			final float[] current = currentAction.getLineData();
			final float[] old = oldAction.getLineData();
			final float[] animated = animatedAction.getLineData();
			for (int i = 0; i < animated.length; i++) {
				animated[i] = current[i] * animationProgress + old[i]
						* inverseProgress;
			}
			updatePath(animatedAction);
		}
		if (currentAction.isPlus()) {
			paint.setStrokeWidth(strokeSizePlus);
		} else {
			paint.setStrokeWidth(strokeSize);
		}
		canvas.rotate((rotation == ROTATE_CLOCKWISE ? 180f : -180f)
				* animationProgress, centerX, centerY);
		canvas.drawPath(path, paint);
	}

	@SuppressWarnings("UnusedDeclaration")
	public float getAnimationProgress() {
		return animationProgress;
	}

	@SuppressWarnings("UnusedDeclaration")
	public void setAnimationProgress(float animationProgress) {
		this.animationProgress = animationProgress;
		UiHelper.postInvalidateOnAnimation(this);
	}

	/**
	 * Set the color used for drawing an {@link Action}.
	 * 
	 * @param color
	 */
	public void setColor(final int color) {
		this.color = color;
		paint.setColor(color);
		UiHelper.postInvalidateOnAnimation(this);
	}

	/**
	 * @return the current action
	 */
	public Action getAction() {
		return currentAction;
	}

	/**
	 * Sets the new action. If an action was set before a transition will be
	 * started.
	 * 
	 * @param action
	 * @see #setAction(Action, boolean)
	 * @see #setAction(Action, boolean, int)
	 * @see #setAction(Action, Action, int, long)
	 */
	public void setAction(final Action action) {
		setAction(action, true, ROTATE_CLOCKWISE);
	}

	/**
	 * Sets the new action. If an action was set before a transition will be
	 * started.
	 * 
	 * @param action
	 * @param rotation
	 *            Can be either {@link #ROTATE_CLOCKWISE} or
	 *            {@link #ROTATE_COUNTER_CLOCKWISE}.
	 */
	public void setAction(final Action action, final int rotation) {
		setAction(action, true, rotation);
	}

	/**
	 * Sets the new action.
	 * 
	 * @param action
	 * @param animate
	 *            If a prior action was set and {@code true} a transition will
	 *            be started, otherwise not.
	 */
	public void setAction(final Action action, final boolean animate) {
		setAction(action, animate, ROTATE_CLOCKWISE);
	}

	/**
	 * Sets a new action transition.
	 * 
	 * @param fromAction
	 *            The initial action.
	 * @param toAction
	 *            The target action.
	 * @param rotation
	 *            The rotation direction used for the transition
	 *            {@link #ROTATE_CLOCKWISE} or {@link #ROTATE_COUNTER_CLOCKWISE}
	 *            .
	 * @param delay
	 *            The delay in ms before the transition is started.
	 */
	public void setAction(final Action fromAction, final Action toAction,
			final int rotation, long delay) {
		setAction(fromAction, false, ROTATE_CLOCKWISE);
		postDelayed(new Runnable() {
			@TargetApi(Build.VERSION_CODES.KITKAT)
			@Override
			public void run() {
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT
						|| !isAttachedToWindow()) {
					return;
				}
				setAction(toAction, true, rotation);
			}
		}, delay);
	}

	/**
	 * Set the animation duration used for Action transitions
	 * 
	 * @param animationDuration
	 *            the duration in ms
	 */
	public void setAnimationDuration(long animationDuration) {
		this.animationDuration = animationDuration;
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();
		final SavedState ss = new SavedState(superState);
		ss.currentAction = currentAction;
		ss.color = color;
		return ss;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());
		this.color = ss.color;
		this.currentAction = ss.currentAction;
		this.animationProgress = 1f;
	}

	private void setAction(Action action, boolean animate, int rotation) {
		if (action == null) {
			return;
		}

		this.rotation = rotation;
		if (currentAction == null) {
			currentAction = action;
			currentAction.flipHorizontally();
			animationProgress = 1f;
			UiHelper.postInvalidateOnAnimation(this);
			return;
		}

		if (currentAction.getClass().equals(action.getClass())) {
			return;
		}

		oldAction = currentAction;
		currentAction = action;

		if (animate) {
			animationProgress = 0f;
			if (ready) {
				startAnimation();
			} else {
				animateWhenReady = true;
			}
		} else {
			animationProgress = 1f;
			UiHelper.postInvalidateOnAnimation(this);
		}
	}

	private void updatePath(Action action) {
		path.reset();

		final float[] data = action.getLineData();
		// Once we're near the end of the animation we use the action segments
		// to draw linked lines
		if (animationProgress > 0.95f && !action.getLineSegments().isEmpty()) {
			for (LineSegment s : action.getLineSegments()) {
				path.moveTo(data[s.getStartIdx() + 0],
						data[s.getStartIdx() + 1]);
				path.lineTo(data[s.getStartIdx() + 2],
						data[s.getStartIdx() + 3]);
				for (int i = 1; i < s.indexes.length; i++) {
					path.lineTo(data[s.indexes[i] + 0], data[s.indexes[i] + 1]);
					path.lineTo(data[s.indexes[i] + 2], data[s.indexes[i] + 3]);
				}
			}
		} else {
			for (int i = 0; i < data.length; i += 4) {
				path.moveTo(data[i + 0], data[i + 1]);
				path.lineTo(data[i + 2], data[i + 3]);
			}
		}
	}

	private void transformActions() {
		if (currentAction != null && !currentAction.isTransformed()) {
			currentAction.transform(padding, padding, scale, size);
		}

		if (oldAction != null && !oldAction.isTransformed()) {
			oldAction.transform(padding, padding, scale, size);
		}
	}

	private void startAnimation() {
		oldAction.flipHorizontally();
		currentAction.flipHorizontally();

		transformActions();

		animatedAction.setLineSegments(currentAction.getLineSegments());
		final ObjectAnimator animator = ObjectAnimator.ofFloat(ActionView.this,
				"animationProgress", 0f, 1f);
		animator.setInterpolator(BakedBezierInterpolator.getInstance());
		animator.setDuration(animationDuration).start();
	}

	private Action getActionFromEnum(int id) {
		switch (id) {
		case 0:
			return new DrawerAction();
		case 1:
			return new BackAction();
		case 2:
			return new CloseAction();
		case 3:
			return new PlusAction();
		case 4:
			return new MoreAction();
		}

		return null;
	}

	static class SavedState extends BaseSavedState {

		Action currentAction;
		int color;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		public SavedState(Parcel source) {
			super(source);
			currentAction = source.readParcelable(getClass().getClassLoader());
			color = source.readInt();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeParcelable(currentAction, 0);
			dest.writeInt(color);
		}

		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

			@Override
			public SavedState createFromParcel(Parcel parcel) {
				return new SavedState(parcel);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}
}
