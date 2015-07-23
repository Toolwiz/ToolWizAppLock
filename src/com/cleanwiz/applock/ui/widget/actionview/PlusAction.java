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

public class PlusAction extends Action {

	public PlusAction() {

		final float bottom = 76f / 96f;
		final float top = 1f - bottom;
		final float left = 20f / 96f;
		final float right = 1f - left;
		final float center = 0.5f;

		lineData = new float[]{
				// line 1
				center, top, center, bottom,
				// line 2
				left, center, right, center,
				// line 3
				center, top, center, bottom,};
	}
}
