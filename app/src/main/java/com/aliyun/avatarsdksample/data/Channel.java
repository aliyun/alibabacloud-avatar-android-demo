package com.aliyun.avatarsdksample.data;

import java.util.List;

public class Channel{
	private List<String> gslb;
	private String appId;
	private String type;
	private String nonce;
	private String userId;
	private String channelId;
	private String expiredTime;
	private String token;

	public void setGslb(List<String> gslb){
		this.gslb = gslb;
	}

	public List<String> getGslb(){
		return gslb;
	}

	public void setAppId(String appId){
		this.appId = appId;
	}

	public String getAppId(){
		return appId;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setNonce(String nonce){
		this.nonce = nonce;
	}

	public String getNonce(){
		return nonce;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return userId;
	}

	public void setChannelId(String channelId){
		this.channelId = channelId;
	}

	public String getChannelId(){
		return channelId;
	}

	public void setExpiredTime(String expiredTime){
		this.expiredTime = expiredTime;
	}

	public String getExpiredTime(){
		return expiredTime;
	}

	public void setToken(String token){
		this.token = token;
	}

	public String getToken(){
		return token;
	}
}