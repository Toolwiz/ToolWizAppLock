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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class FileService {

	public static final String TAG = "FileService";
	private Context context;

	// 得到传入的上下文对象的引用
	public FileService(Context context) {
		this.context = context;
	}

	/**
	 * 保存文件
	 * 
	 * @param fileName
	 *            文件名
	 * @param content
	 *            文件内容
	 * @throws Exception
	 */
	public void save(String fileName, String content) throws Exception {

		// 由于页面输入的都是文本信息，所以当文件名不是以.txt后缀名结尾时，自动加上.txt后缀
		if (!fileName.endsWith(".txt")) {
			fileName = fileName + ".txt";
		}

		byte[] buf = fileName.getBytes("iso8859-1");

		LogUtil.e(TAG, new String(buf, "utf-8"));

		fileName = new String(buf, "utf-8");

		LogUtil.e(TAG, fileName);

		// Context.MODE_PRIVATE：为默认操作模式，代表该文件是私有数据，只能被应用本身访问，在该模式下，写入的内容会覆盖原文件的内容，如果想把新写入的内容追加到原文件中。可以使用Context.MODE_APPEND
		// Context.MODE_APPEND：模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件。
		// Context.MODE_WORLD_READABLE和Context.MODE_WORLD_WRITEABLE用来控制其他应用是否有权限读写该文件。
		// MODE_WORLD_READABLE：表示当前文件可以被其他应用读取；MODE_WORLD_WRITEABLE：表示当前文件可以被其他应用写入。
		// 如果希望文件被其他应用读和写，可以传入：
		// openFileOutput("output.txt", Context.MODE_WORLD_READABLE +
		// Context.MODE_WORLD_WRITEABLE);

		FileOutputStream fos = context.openFileOutput(fileName,
				context.MODE_PRIVATE);
		fos.write(content.getBytes());
		fos.close();
	}

	/**
	 * 读取文件内容
	 * 
	 * @param fileName
	 *            文件名
	 * @return 文件内容
	 * @throws Exception
	 */
	public String read(String fileName) throws Exception {

		// 由于页面输入的都是文本信息，所以当文件名不是以.txt后缀名结尾时，自动加上.txt后缀
		if (!fileName.endsWith(".txt")) {
			fileName = fileName + ".txt";
		}

		FileInputStream fis = context.openFileInput(fileName);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] buf = new byte[1024];
		int len = 0;

		// 将读取后的数据放置在内存中---ByteArrayOutputStream
		while ((len = fis.read(buf)) != -1) {
			baos.write(buf, 0, len);
		}

		fis.close();
		baos.close();

		// 返回内存中存储的数据
		return baos.toString();

	}

	public static String createShareImage(Bitmap bm, Context context) {
		String name = "lockwiz_show_2.0.jpg";
		// File dir = context.getFilesDir();
		File dir = new File(Environment.getExternalStorageDirectory(),
				"lockwiz");
		if (!dir.exists()) {
			dir.mkdir();
		}
		File file = new File(dir.getAbsolutePath(), name);
		if (!file.exists()) {
			boolean s = save(bm, dir.getAbsolutePath(), name, false);
			LogUtil.d("demo3", "share pic :" + s);
		}

		LogUtil.d("demo3", "file:" + file.getAbsolutePath());
		LogUtil.d("demo3", "uri:" + Uri.fromFile(file).toString());
		return file.getAbsolutePath();
	}

	private static boolean save(Bitmap bm, String dir, String name,
			boolean alpha) {
		FileOutputStream fos = null;
		try {
			File file = new File(dir);
			if (!file.exists()) {
				file.mkdirs();
			}
			LogUtil.d("demo3", "save:" + dir + "/" + name);
			fos = new FileOutputStream(dir + "/" + name);
			if (null != fos) {
				if (alpha) {
					return bm.compress(Bitmap.CompressFormat.PNG, 60, fos);
				} else {
					return bm.compress(Bitmap.CompressFormat.JPEG, 60, fos);
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

}
