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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;

/**
 * Created with IntelliJ IDEA. Author: wangjie email:tiantian.china.2@gmail.com
 * Date: 14-3-28 Time: 上午10:57
 */
public class ABTextUtil {
	public static final String TAG = ABTextUtil.class.getSimpleName();

	/**
	 * 获得字体的缩放密度
	 * 
	 * @param context
	 * @return
	 */
	public static float getScaledDensity(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.scaledDensity;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * *************************************************************
	 */

	public static boolean isEmpty(Collection collection) {
		return null == collection || collection.isEmpty();
	}

	public static boolean isEmpty(Map map) {
		return null == map || map.isEmpty();
	}

	public static boolean isEmpty(Object[] objs) {
		return null == objs || objs.length <= 0;
	}

	public static boolean isEmpty(int[] objs) {
		return null == objs || objs.length <= 0;
	}

	public static boolean isEmpty(CharSequence charSequence) {
		return null == charSequence || charSequence.length() <= 0;
	}

	public static boolean isBlank(CharSequence charSequence) {
		return null == charSequence
				|| charSequence.toString().trim().length() <= 0;
	}

	public static boolean isLeast(Object[] objs, int count) {
		return null != objs && objs.length >= count;
	}

	public static boolean isLeast(int[] objs, int count) {
		return null != objs && objs.length >= count;
	}

	public static boolean isEquals(String str1, String str2) {
		if (null != str1) {
			return str1.equals(str2);
		}
		if (null != str2) {
			return str2.equals(str1);
		}
		return true;
	}

	/**
	 * 替换文本为图片
	 * 
	 * @param charSequence
	 * @param regPattern
	 * @param drawable
	 * @return
	 */
	public static SpannableString replaceImageSpan(CharSequence charSequence,
			String regPattern, Drawable drawable) {
		SpannableString ss = charSequence instanceof SpannableString ? (SpannableString) charSequence
				: new SpannableString(charSequence);
		try {
			ImageSpan is = new ImageSpan(drawable);
			Pattern pattern = Pattern.compile(regPattern);
			Matcher matcher = pattern.matcher(ss);
			while (matcher.find()) {
				String key = matcher.group();
				ss.setSpan(is, matcher.start(), matcher.start() + key.length(),
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return ss;
	}

	/**
	 * 压缩字符串到Zip
	 * 
	 * @param str
	 * @return 压缩后字符串
	 * @throws IOException
	 */
	public static String compress(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(str.getBytes());
		gzip.close();
		return out.toString("ISO-8859-1");
	}

	/**
	 * 解压Zip字符串
	 * 
	 * @param str
	 * @return 解压后字符串
	 * @throws IOException
	 */
	public static String uncompress(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayInputStream in = new ByteArrayInputStream(
				str.getBytes("UTF-8"));
		return uncompress(in);
	}

	/**
	 * 解压Zip字符串
	 * 
	 * @param inputStream
	 * @return 解压后字符串
	 * @throws IOException
	 */
	public static String uncompress(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPInputStream gunzip = new GZIPInputStream(inputStream);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		return out.toString();
	}

	/**
	 * InputStream convert to string
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String inputStream2String(InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}

	/**
	 * 解压Gzip获取
	 * 
	 * @param is
	 * @return
	 */
	public static String inputStream2StringFromGZIP(InputStream is) {
		StringBuilder resultSb = new StringBuilder();
		BufferedInputStream bis = null;
		InputStreamReader reader = null;
		try {
			bis = new BufferedInputStream(is);
			bis.mark(2);
			// 取前两个字节
			byte[] header = new byte[2];
			int result = bis.read(header);
			// reset输入流到开始位置
			bis.reset();
			// 判断是否是GZIP格式
			int headerData = getShort(header);
			// Gzip流的前两个字节是0x1f8b
			if (result != -1 && headerData == 0x1f8b) {
				is = new GZIPInputStream(bis);
			} else {
				is = bis;
			}
			reader = new InputStreamReader(is, "utf-8");
			char[] data = new char[100];
			int readSize;
			while ((readSize = reader.read(data)) > 0) {
				resultSb.append(data, 0, readSize);
			}
		} catch (Exception e) {
			// Logger.e(TAG, e);
		} finally {
			closeIO(is, bis, reader);
		}
		return resultSb.toString();
	}

	/**
	 * 关闭流
	 * 
	 * @param closeables
	 */
	public static void closeIO(Closeable... closeables) {
		if (null == closeables || closeables.length <= 0) {
			return;
		}
		for (Closeable cb : closeables) {
			try {
				if (null == cb) {
					continue;
				}
				cb.close();
			} catch (IOException e) {
			}
		}
	}

	private static int getShort(byte[] data) {
		return (int) ((data[0] << 8) | data[1] & 0xFF);
	}

}
