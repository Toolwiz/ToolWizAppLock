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

public class BackAction extends Action {

	public BackAction() {

		final float endX = 0.82f;
		final float startX = 0.21875f;
		final float center = 0.5f;

		lineData = new float[]{
				// line 1
				startX, center, 0.52f, 0.2f,
				// line 2
				startX, center, endX, center,
				// line 2
				startX, center, 0.52f, 0.8f,};

		lineSegments.add(new LineSegment(0, 8));
		lineSegments.add(new LineSegment(4));
	}
}
