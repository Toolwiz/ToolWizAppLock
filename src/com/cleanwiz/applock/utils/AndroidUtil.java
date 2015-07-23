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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class AndroidUtil {
	private static final String TAG = "AndroidUtil";
	
    private static List<String> INVALID_IMEIs = new ArrayList<String>();
    static {
        INVALID_IMEIs.add("358673013795895");
        INVALID_IMEIs.add("004999010640000");
        INVALID_IMEIs.add("00000000000000");
        INVALID_IMEIs.add("000000000000000");
    }

    private static String OSVersion;

    public static boolean isValidImei(String imei) {
        if (StringUtils.isEmpty(imei)) return false;
        if (imei.length() < 10) return false;
        if (INVALID_IMEIs.contains(imei)) return false;
        return true;
    }
    
    public static void showSoftInput(final Context context, final EditText et){
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(et, 0);
			}
		}, 998);
    }


    private static final String INVALID_ANDROIDID = "9774d56d682e549c";
	
    public static String getUdid(Context context) {
    	if(context == null){
    		return "";
    	}
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(telephonyManager==null){
        	return "";
        }
        String imei = telephonyManager.getDeviceId();

        if (AndroidUtil.isValidImei(imei)) {
        	
        }else{
            if(imei==null)imei="";
        }
        
        String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

        if (!StringUtils.isEmpty(androidId) && !INVALID_ANDROIDID.equals(androidId.toLowerCase())) {
        	
        }else{
            if(androidId==null)androidId="";

        }
        
        String macAddress = AndroidUtil.getWifiMacAddress(context);
        if (!StringUtils.isEmpty(macAddress)) {
            String udid = StringUtils.toMD5(macAddress
                    + Build.MODEL + Build.MANUFACTURER
                    + Build.ID + Build.DEVICE);
            
        }else{
            if(macAddress==null)macAddress="";
        }

        return StringUtils.toMD5(imei+androidId+macAddress);
    }
    
    private static final String UDID_PATH = Environment.getExternalStorageDirectory().getPath() + "/data/.pushtalk_udid";
    private static final String UDID_PREF_KEY = "pushtalk_udid";
    
    public static String getWifiMacAddress(final Context context) {
        try {
            WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            String mac = wifimanager.getConnectionInfo().getMacAddress();
            if (StringUtils.isEmpty(mac)) return null;
            return mac;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getRelation(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowMgr = (WindowManager) context
                .getApplicationContext().getSystemService(
                        Context.WINDOW_SERVICE);
        windowMgr.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        return String.valueOf(width) + "*" + String.valueOf(height);
    }

	
    // 打印所有的 intent extra 数据
    public static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
        }
        return sb.toString();
    }

    //TODO 40% unknown
    public static String getNetworkTypeName(int type) {
    	String name = "Unknown";
    	switch (type) {
			case TelephonyManager.NETWORK_TYPE_GPRS:
				name = "GPRS";break;
			case TelephonyManager.NETWORK_TYPE_EDGE:
				name = "EDGE";break;
    		case TelephonyManager.NETWORK_TYPE_CDMA:
    			name = "CDMA";break;
    		case TelephonyManager.NETWORK_TYPE_EVDO_0:
				name = "EVDO_0";break;
    		case TelephonyManager.NETWORK_TYPE_EVDO_A:
				name = "EVDO_A";break; 
    		case TelephonyManager.NETWORK_TYPE_HSDPA:
    			name = "HSDPA";break;
    		case TelephonyManager.NETWORK_TYPE_HSPA:
    			name = "HSPA";break;
    		case TelephonyManager.NETWORK_TYPE_HSUPA:
				name = "HSUPA";break;
    		case TelephonyManager.NETWORK_TYPE_UMTS:
				name = "UMTS";break;
    		default: 
    	}
    	
    	return name;
    }
    
    public static boolean is2gNetwork(Context context) {
        TelephonyManager tm = (TelephonyManager) context.
			getSystemService(Context.TELEPHONY_SERVICE);
	    int type = tm.getNetworkType();
	    if (type == TelephonyManager.NETWORK_TYPE_GPRS
	    		|| type == TelephonyManager.NETWORK_TYPE_EDGE) {
	    	return true;
	    }
	    return false;
    }
    
    
	public static int getCurrentSdkVersion() {
		return Build.VERSION.SDK_INT;
	}

    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }
	
	public static void sendEmail(Context context, String chooserTitle, 
			String mailAddress, String subject, String preContent) {
	    final Intent emailIntent = new Intent(Intent.ACTION_SEND);
	    emailIntent.setType("plain/text");
	    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ mailAddress });
	    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
	    String content = "\n\n=====================\n";
	    content += "Device Environment: \n----\n" + preContent;
	    emailIntent.putExtra(Intent.EXTRA_TEXT, content);
	    context.startActivity(Intent.createChooser(emailIntent, chooserTitle));
	}


	// some apps only show content, some apps show both subject and content
	public static Intent getAndroidShareIntent(CharSequence chooseTitle,
			CharSequence subject, CharSequence content) {
    	Intent shareIntent = new Intent(Intent.ACTION_SEND);
    	shareIntent.setType("text/plain");
    	shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
    	shareIntent.putExtra(Intent.EXTRA_TEXT, content);
    	return Intent.createChooser(shareIntent, chooseTitle);
	}
	
	public static Intent getAndroidImageShareIntent(CharSequence chooseTitle, 
			String pathfile) {
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("image/*");
		share.putExtra(Intent.EXTRA_STREAM, Uri.parse(pathfile));
		return Intent.createChooser(share, chooseTitle);
	}
	
	public static String getImei(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}
	
	public static String getImsi(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSubscriberId();
	}
	
	public static String getSimSerialNumber(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSimSerialNumber();
	}
	 public static boolean canNetworkUseful(Context context)
	    {

	        ConnectivityManager manager = (ConnectivityManager) context .getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

	        if (manager == null)
	        {
	            return false;
	        }

	        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
	        if (networkinfo == null || !networkinfo.isAvailable())
	        {
	            return false;
	        }

	        return true;
	    }

    public static String getNetworkTypeName(Context context) {
        int kind=0;
        try{
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            kind = tm.getNetworkType();
        }catch (Exception e){

        }
        return String.valueOf(kind);
    }

    public static long getRamSize(Context context) {
        return 0;
    }

    public static long getRomSize(Context context) {
        return 0;
    }

    public static boolean hasExtSdcard(Context context){
        String[] paths;
        StorageManager service = (StorageManager) context.getSystemService(Activity.STORAGE_SERVICE);
        try {
            Method mMethod= service.getClass().getMethod("getVolumePaths");
            paths= (String[]) mMethod.invoke(service);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
    public static boolean isRunningForeground(Context context){
        String packageName=context.getPackageName();
        String topActivityClassName=getTopActivityName(context);
        if (packageName!=null&&topActivityClassName!=null&&topActivityClassName.startsWith(packageName)) {
            return true;
        } else {
            return false;
        }
    }
    
    public static String getTopActivityName(Context context){
        String topActivityClassName=null;
         ActivityManager activityManager =
        (ActivityManager)(context.getSystemService(android.content.Context.ACTIVITY_SERVICE )) ;
         List<RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1) ;
         if(runningTaskInfos != null){
             ComponentName f=runningTaskInfos.get(0).topActivity;
             topActivityClassName=f.getClassName();
         }
         return topActivityClassName;
    }
}
