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

import java.io.Serializable;

public class AppSettingItemInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	public int classID = 0;
	public int parentID = 0;
	public boolean isTopShow = false;
	public boolean isShowSwitch = false;
	public boolean isShowGoAnother = false;
	public String topTitle = "";
	public String DetailTitle = "";
	public String DetailDescription = "";
	public String tips = "";

	public AppSettingItemInfo() {

	}

	public AppSettingItemInfo(int classID, int parentID, boolean isTopShow,
			boolean isShowSwitch, boolean isShowGoAnother, String topTitle,
			String detailTitle, String detailDescription, String tips) {
		this.classID = classID;
		this.parentID = parentID;
		this.isTopShow = isTopShow;
		this.isShowSwitch = isShowSwitch;
		this.isShowGoAnother = isShowGoAnother;
		this.topTitle = topTitle;
		DetailTitle = detailTitle;
		DetailDescription = detailDescription;
		this.tips = tips;
	}

	public int getClassID() {
		return classID;
	}

	public void setClassID(int classID) {
		this.classID = classID;
	}

	public int getParentID() {
		return parentID;
	}

	public void setParentID(int parentID) {
		this.parentID = parentID;
	}

	public boolean isTopShow() {
		return isTopShow;
	}

	public void setTopShow(boolean isTopShow) {
		this.isTopShow = isTopShow;
	}

	public boolean isShowGoAnother() {
		return isShowGoAnother;
	}

	public void setShowGoAnother(boolean isShowGoAnother) {
		this.isShowGoAnother = isShowGoAnother;
	}

	public String getTopTitle() {
		return topTitle;
	}

	public void setTopTitle(String topTitle) {
		this.topTitle = topTitle;
	}

	public String getDetailTitle() {
		return DetailTitle;
	}

	public void setDetailTitle(String detailTitle) {
		DetailTitle = detailTitle;
	}

	public String getDetailDescription() {
		return DetailDescription;
	}

	public void setDetailDescription(String detailDescription) {
		DetailDescription = detailDescription;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

	public boolean isShowSwitch() {
		return isShowSwitch;
	}

	public void setShowSwitch(boolean isShowSwitch) {
		this.isShowSwitch = isShowSwitch;
	}

	@Override
	public String toString() {
		return "classID:" + classID + ":parentID:" + parentID + ":isTopShow:"
				+ isTopShow + ":isShowGoAnother:" + isShowGoAnother
				+ ":topTitle:" + topTitle + ":DetailTitle:" + DetailTitle
				+ ":DetailDescription:" + DetailDescription;
	}
}
