package com.brioal.baselib.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;

import java.io.File;



public class DeviceUtil {

    
    
    public static String getMacDevice(Context context) {
        String macAddress;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        macAddress = info.getMacAddress();
        if (null == macAddress) {
            return "";
        }
        macAddress = macAddress.replace(":", "");
        return macAddress;
    }

    
    public static String getManuFacturer() {
        String manuFacturer = Build.MANUFACTURER;
        return manuFacturer;
    }

    
    public static String getMode() {
        String model = Build.MODEL;
        if (model == null) {
            model = model.trim().replaceAll("\\s", "");
        } else {
            model = "";
        }
        return model;
    }

    
    public static boolean isSDCardEnable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()); 
    }

    
    public static String getSDPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }
}

package jakarta.faces.flow;

import jakarta.faces.context.FacesContext;


public abstract class ReturnNode extends FlowNode
{
    public abstract String getFromOutcome(FacesContext context);
    
}