package com.aliyun.avatarsdksample.data;

/**
 * Created by xbaoyuan on 2022/11/10
 *
 * @date 2022/11/10
 * Api : StartInstance
 * {@see "https://help.aliyun.com/document_detail/412625.html"}
 */
public class StartInstanceData {
    private String token;
    private String sessionId;
    private RtcData rtcData;

    public String getToken() {
        return token;
    }

    public void setToken(String imToken) {
        this.token = imToken;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public RtcData getChannel() {
        return rtcData;
    }

    public void setChannel(RtcData rtcData) {
        this.rtcData = rtcData;
    }
}
