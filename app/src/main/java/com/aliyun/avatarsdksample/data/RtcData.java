package com.aliyun.avatarsdksample.data;

/**
 * Created by xbaoyuan on 2022/11/29
 *
 * @date 2022/11/29
 */
public class RtcData {
    private String[] gslb;
    private String appId;
    private String userId;
    private String nonce;
    private String channelId;
    private String expiredTime;
    private long timestamp;
    private String token;
    private String name;

    public String[] getGslb() {
        return gslb;
    }

    public void setGslb(String[] gslb) {
        this.gslb = gslb;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
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

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(String expiredTime) {
        this.expiredTime = expiredTime;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

