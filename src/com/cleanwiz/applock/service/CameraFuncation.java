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
package com.cleanwiz.applock.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import u.aly.ca;

import com.cleanwiz.applock.AppLockApplication;
import com.cleanwiz.applock.data.LookMyPrivate;
import com.cleanwiz.applock.utils.LogUtil;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

public class CameraFuncation implements SurfaceHolder.Callback,
		Camera.PictureCallback {

	private Camera camera;
	private Context context;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private LookMyPrivateService privateService;
	private int cameraId;
	public LookMyPrivate lookMyPrivate;

	@SuppressLint("NewApi")
	public CameraFuncation(Context context, SurfaceView surfaceView,
			LookMyPrivateService lookMyPrivateService) {
		if (!AppLockApplication.getInstance().getAutoRecordPic()) {
			return;
		}
		this.privateService = lookMyPrivateService;
		this.context = context;
		this.surfaceView = surfaceView;
		this.surfaceHolder = this.surfaceView.getHolder();
		this.surfaceHolder.addCallback(this);
	}

	public void clearCamera() {
		if (camera != null) {
			camera.release();
			camera = null;
		}
	}

	public void tackPicture() {
		if (!AppLockApplication.getInstance().getAutoRecordPic()) {
			return;
		}
		new Thread(tackPictureRunnable).start();
	}
	
	private Runnable tackPictureRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				if (camera != null) {
					camera.takePicture(null, null, null, CameraFuncation.this);
				}
			} catch (Exception e) {
				// TODO: handle exception
				clearCamera();
			}
		}
	};

	private Camera openFacingBackCamera() {
		if (!AppLockApplication.getInstance().getAutoRecordPic()) {
			return null;
		}
		Camera cam = null;
		try {
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			for (int camIdx = 0, cameraCount = Camera.getNumberOfCameras(); camIdx < cameraCount; camIdx++) {
				Camera.getCameraInfo(camIdx, cameraInfo);
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					try {
						cam = Camera.open(camIdx);
						cameraId = camIdx;
						break;
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}
			}
			if (cam == null) {
				cam = Camera.open();
			}
		} catch (Exception e) {
			// TODO: handle exception
			cam = null;
		}

		return cam;
	}

	private File getDir() {
		if (!AppLockApplication.getInstance().getAutoRecordPic()) {
			return null;
		}
		File sdDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		return new File(sdDir, "ServiceCamera");
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		File pictureFileDir = getDir();
		if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
			return;

		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
		String date = dateFormat.format(new Date());
		String photoFile = "Picture_" + date + ".jpg";
		String filename = pictureFileDir.getPath() + File.separator + photoFile;
		File pictureFile = new File(filename);
		LogUtil.e("colin", "filename is " + filename);
		privateService.picPath = filename;
		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);

			if (privateService != null && pictureFile.exists()) {
				Message msg = new Message();
				msg.what = LookMyPrivateService.LOOKMYPRIVATE_PICOK;
				privateService.lookMyPrivate = lookMyPrivate;
				privateService.mHandler.sendMessage(msg);
			}
			fos.write(data);
			fos.close();
		} catch (Exception error) {
		}
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (!AppLockApplication.getInstance().getAutoRecordPic()) {
			return;
		}

		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
					CameraInfo info = new CameraInfo();
					Camera.getCameraInfo(i, info);
					if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
						camera = Camera.open(i);
					}
				}
			}
			if (camera == null) {
				camera = Camera.open();
			}

			camera.setDisplayOrientation(90);
			camera.setPreviewDisplay(holder);
		} catch (Exception e) {
			if (camera != null) {
				camera.release();
			}
			camera = null;
			e.printStackTrace();
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		if (camera != null) {
			Camera.Parameters parameters = camera.getParameters();
			parameters.setRotation(270);
			camera.setParameters(parameters);
			camera.startPreview();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		LogUtil.e("colin",
				"xiaohuixiaohuixiaohuixiaohuixiaohuixiaohuixiaohuixiaohuixiaohuixiaohui");
		if (camera != null) {
			camera.stopPreview();
			camera.release();
		}
		camera = null;
	}

	public int getDegree() {
		CameraInfo info = new CameraInfo();
		camera.getCameraInfo(cameraId, info);
		int result = info.orientation;
		LogUtil.e("colin", "result:" + result);
		return result;
	}
}
