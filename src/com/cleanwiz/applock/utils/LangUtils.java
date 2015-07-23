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
package com.cleanwiz.applock.utils;

import java.util.Locale;

public class LangUtils {

	public static String getSysLang() {

		return Locale.getDefault().getLanguage();
	}

	public static boolean isChinese(String lang) {
		boolean rst = lang.equalsIgnoreCase(Locale.SIMPLIFIED_CHINESE
				.getLanguage());
		return rst;
	}

	public static boolean isChinese() {
		return isChinese(getSysLang());
	}

}
