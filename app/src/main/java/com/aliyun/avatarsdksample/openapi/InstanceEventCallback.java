package com.aliyun.avatarsdksample.openapi;

public interface InstanceEventCallback {
	public void onStartInstance(AvatarInstanceResponse response);

	public void onStopInstance(AvatarInstanceResponse response);

	public void onQueryInstance(AvatarInstanceResponse response);
}
