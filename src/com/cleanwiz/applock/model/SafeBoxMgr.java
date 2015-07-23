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

import java.util.ArrayList;
import java.util.List;

import com.cleanwiz.applock.R;

public class SafeBoxMgr {

	public static final int BOX_ID_PIC = 1;
	public static final int BOX_ID_VIDEO = 2;
	public static final int BOX_ID_AUDIO = 3;
	public static final int BOX_ID_FILE = 4;

	private List<SafeBox> safeBoxList;

	public SafeBoxMgr() {
		super();
		init();
	}

	public List<SafeBox> getSafeBoxList() {
		return safeBoxList;
	}

	private void init() {
		safeBoxList = new ArrayList<SafeBoxMgr.SafeBox>();
		safeBoxList.add(new SafeBox(BOX_ID_PIC, R.drawable.box_image,
				R.string.box_title_pic, R.string.box_detail_pic));
		safeBoxList.add(new SafeBox(BOX_ID_VIDEO, R.drawable.box_avi,
				R.string.box_title_video, R.string.box_detail_file));
		safeBoxList.add(new SafeBox(BOX_ID_AUDIO, R.drawable.box_audio,
				R.string.box_title_aduio, R.string.box_detail_file));
		safeBoxList.add(new SafeBox(BOX_ID_FILE, R.drawable.box_file,
				R.string.box_title_file, R.string.box_detail_file));

	}

	public class SafeBox {
		private int id;
		private int iconId;
		private int titleId;
		private int detailId;

		public SafeBox(int id, int iconId, int titleId, int detailId) {
			super();
			this.id = id;
			this.iconId = iconId;
			this.titleId = titleId;
			this.detailId = detailId;
		}

		public int getId() {
			return id;
		}

		public int getIconId() {
			return iconId;
		}

		public int getTitleId() {
			return titleId;
		}

		public int getDetailId() {
			return detailId;
		}

	}

}
