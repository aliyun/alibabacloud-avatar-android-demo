package com.aliyun.avatarsdksample.util;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtil {
    static final int REQUEST_CODE_ASK_PERMISSIONS = 125;



    public static void requestPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            addPermission(activity, permissions, Manifest.permission.READ_PHONE_STATE);
            addPermission(activity, permissions, Manifest.permission.RECORD_AUDIO);
            addPermission(activity, permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            addPermission(activity, permissions, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
            }

            if (permissions.size() > 0)
                activity.requestPermissions(permissions.toArray(new String[0]),
                        REQUEST_CODE_ASK_PERMISSIONS);
        }
    }


    static boolean activityFinished;

    public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults == null || grantResults.length == 0)
            return;

        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
            }

            break;
        }
    }

    private static boolean addPermission(Activity activity, List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                if (!activity.shouldShowRequestPermissionRationale(permission))
                    return false;
            }
        }
        return true;
    }

    public static boolean lacksPermission(Context context) {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.READ_PHONE_STATE);
        permissions.add(Manifest.permission.RECORD_AUDIO);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        }

        for (String permission : permissions) {
            if(ContextCompat.checkSelfPermission(context, permission) ==
                    PackageManager.PERMISSION_DENIED){
                return true;
            }
        }
        return false;
    }


    public static boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (appOpsMgr == null)
                return false;
            int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context
                    .getPackageName());
            return mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED;
        } else {
            return Settings.canDrawOverlays(context);
        }
    }
}
