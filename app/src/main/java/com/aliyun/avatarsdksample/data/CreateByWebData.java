package com.aliyun.avatarsdksample.data;

/**
 * Created by xbaoyuan on 2022/11/10
 *
 * @date 2022/11/10
 */
public class CreateByWebData {
    private String imToken;
    private String sessionId;
    private RtcData rtcData;

    public String getImToken() {
        return imToken;
    }

    public void setImToken(String imToken) {
        this.imToken = imToken;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public RtcData getRtc() {
        return rtcData;
    }

    public void setRtc(RtcData rtcData) {
        this.rtcData = rtcData;
    }
}
