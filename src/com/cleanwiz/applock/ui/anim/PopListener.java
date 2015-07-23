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
package com.cleanwiz.applock.ui.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class PopListener implements AnimationListener {

	public static final int TYPE_IN = 0;
	public static final int TYPE_OUT = 1;

	private View animView;
	private int type;

	public PopListener(View animView, int type) {
		super();
		this.animView = animView;
		this.type = type;
	}

	@Override
	public void onAnimationStart(Animation animation) {
		if (TYPE_IN == type) {
			animView.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (TYPE_OUT == type) {
			animView.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

}
