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
package com.cleanwiz.applock.ui.activity;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.Toast;

import com.cleanwiz.applock.R;
import com.cleanwiz.applock.ui.BaseActivity;
import com.cleanwiz.applock.utils.LogUtil;

import java.io.File;

public class AppsLinkActivity extends BaseActivity {

	private WebView appsWebView;
	private WebView localWebView;
	private ListView appsListView;
	public String outUrl;
	public DownloadManager dm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apps);
		setStatusBarMargin(findViewById(R.id.layout_setting));

		dm = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
		appsWebView = (WebView) findViewById(R.id.appswebview);
		localWebView = (WebView) findViewById(R.id.local_view);
		appsListView = (ListView) findViewById(R.id.appslistview);

		appsListView.setVisibility(View.GONE);
		String laString = getResources().getConfiguration().locale.getLanguage();
		String testUrl = "";
		if (laString.equals("zh")) {
			testUrl = "http://www.toolwiz.com/api/recommendList.php?fr=锁锁&v=1.20&c=self&t=1&l="
					+ laString;
		} else {
			testUrl = "http://www.toolwiz.com/api/recommendList.php?fr=LockWiz&v=1.20&c=self&t=1&l="
					+ laString;
		}
		outUrl = testUrl;


		localWebView.getSettings().setJavaScriptEnabled(true);
		localWebView.getSettings().setRenderPriority(RenderPriority.HIGH);
		localWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
			}
		});
		localWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		localWebView.getSettings().setDomStorageEnabled(true);
		localWebView.getSettings().setDatabaseEnabled(true);
		localWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				LogUtil.e("colin", "local 来加载网页了");
				view.loadUrl(url);
				return true;
			}
		});
		if (laString.equals("zh")) {
			localWebView.loadUrl("file:///android_asset/ss/recommendList.html");
		} else {
			localWebView.loadUrl("file:///android_asset/ss/recommendListen.html");
		}

		localWebView.setDownloadListener(new MyWebViewDownLoadListener());

		appsWebView.getSettings().setJavaScriptEnabled(true);
		appsWebView.getSettings().setRenderPriority(RenderPriority.HIGH);
		appsWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
			}
		});
		if (checkNetworkState()) {
			appsWebView.getSettings().setCacheMode(
					WebSettings.LOAD_DEFAULT);
			appsWebView.getSettings().setDomStorageEnabled(true);
			appsWebView.getSettings().setDatabaseEnabled(true);
			appsWebView.loadUrl(testUrl);
			appsWebView.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					// TODO Auto-generated method stub
					LogUtil.e("colin", "remote 来加载网页了");
					view.loadUrl(url);
					return true;
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					localWebView.setVisibility(View.GONE);
					appsWebView.setVisibility(View.VISIBLE);
				}
			});
		} else {
			if (laString.equals("zh")) {
				appsWebView.loadUrl("file:///android_asset/ss/recommendList.html");
			} else {
				appsWebView.loadUrl("file:///android_asset/ss/recommendListen.html");
			}
		}
		
		appsWebView.setDownloadListener(new MyWebViewDownLoadListener());
	}

	@Override
	public void onClickEvent(View view) {
		switch (view.getId()) {
			case R.id.btn_menu:
			finish();
			break;

		default:
			break;
		}
		super.onClickEvent(view);
	}

	public boolean checkNetworkState() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			return false;
		}
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();

		if (networkinfo == null || !networkinfo.isAvailable()) {
			return false;
		}
		return true;
	}
	
	private class MyWebViewDownLoadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			Toast.makeText(AppsLinkActivity.this, R.string.downloadtips,
					Toast.LENGTH_SHORT).show();

			try {
				String name = url.substring(url.lastIndexOf("/") + 1);
				Request request = new Request(Uri.parse(url));
				request.allowScanningByMediaScanner();
				request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
				request.setDestinationInExternalPublicDir(
						Environment.DIRECTORY_DOWNLOADS, name);
				dm.enqueue(request);
				appsWebView.loadUrl(outUrl);
				registerReceiver(receiver, new IntentFilter(
						DownloadManager.ACTION_DOWNLOAD_COMPLETE));
			} catch (Exception e) {
				e.printStackTrace();
			} catch (Error e) {
				e.printStackTrace();
			}
		}

	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
			queryDownloadStatus(id);
		}
	};

	private void queryDownloadStatus(long id) {
		DownloadManager.Query query = new DownloadManager.Query();
		query.setFilterById(id);
		Cursor c = dm.query(query);
		if (c != null && c.moveToFirst()) {
			int status = c.getInt(c
					.getColumnIndex(DownloadManager.COLUMN_STATUS));
			String filename = c.getString(c
					.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
			switch (status) {
			case DownloadManager.STATUS_SUCCESSFUL:
				installApk(getApplicationContext(), filename);
				break;
			case DownloadManager.STATUS_FAILED:
				dm.remove(id);
				break;
			}
		}
	}

	/**
	 * 安装
	 * 
	 * @param context
	 *            接收外部传进来的context
	 */
	public void installApk(Context context, String mUrl) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(mUrl)),
				"application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}
