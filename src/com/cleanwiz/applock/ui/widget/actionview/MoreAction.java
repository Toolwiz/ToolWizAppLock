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

public class MoreAction extends Action {

	public MoreAction() {
		final float start = 0.435f;
		final float end = 0.565f;

		final float bottom = 0.75f;
		final float top = 1f - bottom;
		final float center = 0.5f;

		lineData = new float[] {
				// line 1
				start, top, end, top,
				// line 2
				start, center, end, center,
				// line 3
				start, bottom, end, bottom, };
	}

	@Override
	public boolean isPlus() {
		return true;
	}
}
