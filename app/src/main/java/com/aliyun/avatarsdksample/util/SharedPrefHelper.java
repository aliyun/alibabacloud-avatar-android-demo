package com.aliyun.avatarsdksample.util;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPrefHelper {
    public static void saveString(Context context, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences("instance_response", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String  loadString(Context context, String key){
        SharedPreferences sharedPref = context.getSharedPreferences("instance_response", Context.MODE_PRIVATE);

        return sharedPref.getString(key,"");
    }

    public static void clearSharedPref(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("instance_response", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }
}
