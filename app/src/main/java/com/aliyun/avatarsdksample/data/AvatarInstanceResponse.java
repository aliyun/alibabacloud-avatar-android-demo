package com.aliyun.avatarsdksample.data;

import java.util.Arrays;

public class AvatarInstanceResponse {
	private Integer statusCode;
	private Boolean success;
	private String message;
	private String env;

	private Long tenantId;
	private String appId;

	private String sessionId;
	private String rtcAppId;
	private String userId;
	private String nonce;
	private Long expiredTime;
	private String channelId;
	private String token;

	private String imToken;
	private String session;


	private String[] gslbUrls;

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getRtcAppId() {
		return rtcAppId;
	}

	public void setRtcAppId(String rtcAppId) {
		this.rtcAppId = rtcAppId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public Long getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(Long expiredTime) {
		this.expiredTime = expiredTime;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String[] getGslbUrls() {
		return gslbUrls;
	}

	public void setGslbUrls(String[] gslbUrls) {
		this.gslbUrls = gslbUrls;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public String getImToken() {
		return imToken;
	}

	public void setImToken(String imToken) {
		this.imToken = imToken;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	@Override
	public String toString() {
		String sb = "AvatarInstanceResponse{" + "statusCode=" + statusCode +
				", success=" + success +
				", message='" + message + '\'' +
				", env='" + env + '\'' +
				", tenantId=" + tenantId +
				", appId='" + appId + '\'' +
				", sessionId='" + sessionId + '\'' +
				", rtcAppId='" + rtcAppId + '\'' +
				", userId='" + userId + '\'' +
				", nonce='" + nonce + '\'' +
				", expiredTime=" + expiredTime +
				", channelId='" + channelId + '\'' +
				", token='" + token + '\'' +
				", gslbUrls=" + Arrays.toString(gslbUrls) +
				'}';
		return sb;
	}
}
