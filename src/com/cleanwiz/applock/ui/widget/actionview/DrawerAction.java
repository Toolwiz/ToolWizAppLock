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

public class DrawerAction extends Action {

	public DrawerAction() {

		final float startX = 0.1375f;
		final float endX = 1f - startX;
		final float endY = 0.707f;
		final float startY = 1f - endY;
		final float center = 0.5f;

		lineData = new float[]{
				// line 1
				startX, startY, endX, startY,
				// line 2
				startX, center, endX, center,
				// line 3
				startX, endY, endX, endY,};
	}
}
