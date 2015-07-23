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
package com.cleanwiz.applock.model;

public class SettingItem {

	public static final int SET_TYPE_ONOFF = 0;
	public static final int SET_TYPE_ENTER = 1;
	public static final int SET_TYPE_TEXT = 2;
	public static final int SET_TYPE_SECTION = 3;
	public static final int SET_TYPE_TITLE = 4;

	private int key;
	private int picId;
	private int titleId;
	private int text1;
	private int text2;
	private int type;

	public SettingItem(int key, int picId, int titleId, int text1, int text2,
			int type) {
		super();
		this.key = key;
		this.picId = picId;
		this.titleId = titleId;
		this.type = type;
		this.text1 = text1;
		this.text2 = text2;
	}

	public int getText1() {
		return text1;
	}

	public int getText2() {
		return text2;
	}

	public int getKey() {
		return key;
	}

	public int getPicId() {
		return picId;
	}

	public int getTitleId() {
		return titleId;
	}

	public int getType() {
		return type;
	}

}
