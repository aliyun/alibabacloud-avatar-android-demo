package com.aliyun.avatarsdksample.openapi;

public class AvatarInstanceRequest {
	private String accessKeyId;
	private String accessKeySecret;
	private Long tenantId;
	private String appId;
	private String env;
	private boolean alphaSwitch;

	public String getAccessKeyId() {
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public String getAccessKeySecret() {
		return accessKeySecret;
	}

	public void setAccessKeySecret(String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}

	public Long gettenantId() {
		return tenantId;
	}

	public void settenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public boolean isAlphaSwitch() {
		return alphaSwitch;
	}

	public void setAlphaSwitch(boolean alphaSwitch) {
		this.alphaSwitch = alphaSwitch;
	}
}
