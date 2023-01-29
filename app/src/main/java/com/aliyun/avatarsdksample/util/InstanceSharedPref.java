package com.aliyun.avatarsdksample.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.aliyun.avatarsdksample.openapi.AvatarInstanceResponse;

public class InstanceSharedPref {
	public static void saveInstanceResponse(Context context, AvatarInstanceResponse response) {
		SharedPreferences sharedPref = context.getSharedPreferences("instance_response", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();

		editor.putString("accessKeyId", response.getAccessKeyId());
		editor.putString("accessKeySecret", response.getAccessKeySecret());
		editor.putString("env", response.getEnv());

		editor.putString("appId", response.getAppId());
		editor.putLong("tenantId", response.getTenantId());

		editor.putString("sessionId", response.getSessionId());
		editor.putString("rtcAppId", response.getAppId());
		editor.putString("userId", response.getUserId());
		editor.putString("nonce", response.getNonce());
		editor.putLong("expiredTime", response.getExpiredTime());
		editor.putString("channelId", response.getChannelId());
		editor.putString("token", response.getToken());
		editor.apply();
	}

	public static AvatarInstanceResponse loadInstanceResponse(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences("instance_response", Context.MODE_PRIVATE);

		AvatarInstanceResponse response = new AvatarInstanceResponse();
		response.setAccessKeyId(sharedPref.getString("accessKeyId", ""));
		response.setAccessKeySecret(sharedPref.getString("accessKeySecret", ""));
		response.setEnv(sharedPref.getString("env", ""));

		response.setAppId(sharedPref.getString("appId", ""));
		response.setTenantId(sharedPref.getLong("tenantId", 0));

		response.setSessionId(sharedPref.getString("sessionId", ""));
		response.setRtcAppId(sharedPref.getString("rtcAppId", ""));
		response.setUserId(sharedPref.getString("userId", ""));
		response.setNonce(sharedPref.getString("nonce", ""));
		response.setExpiredTime(sharedPref.getLong("expiredTime", 0));
		response.setChannelId(sharedPref.getString("channelId", ""));
		response.setToken(sharedPref.getString("token", ""));

		return response;
	}

	public static void clearInstanceResponse(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences("instance_response", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.clear();
		editor.apply();
	}
}
