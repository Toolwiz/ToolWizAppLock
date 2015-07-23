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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * �߶�
 * A LineSegment describes which lines within an Action are linked together
 */
public class LineSegment implements Parcelable {

	public int[] indexes;

	public LineSegment(int... indexes) {
		this.indexes = indexes;
	}

	public int getStartIdx() {
		return indexes[0];
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeIntArray(this.indexes);
	}

	private LineSegment(Parcel in) {
		this.indexes = in.createIntArray();
	}

	public static final Creator<LineSegment> CREATOR = new Creator<LineSegment>() {
		public LineSegment createFromParcel(Parcel source) {
			return new LineSegment(source);
		}

		public LineSegment[] newArray(int size) {
			return new LineSegment[size];
		}
	};
}
