package com.aliyun.avatarsdksample.util;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.avatarsdk.data.AvatarInstanceInfo;
import com.aliyun.avatarsdksample.openapi.AvatarInstanceResponse;

/**
 * Created by xbaoyuan on 2022/11/10
 *
 * @date 2022/11/10
 */
public  class InstanceUtil {

    public static AvatarInstanceInfo convertByAvatarInstanceResponse(String json){
        AvatarInstanceResponse startInstanceData = JSONObject.parseObject(json, AvatarInstanceResponse.class);
        AvatarInstanceInfo avatarInstanceInfo = new AvatarInstanceInfo();
        avatarInstanceInfo.setToken(startInstanceData.getImToken());
        avatarInstanceInfo.setTenantId(String.valueOf(startInstanceData.getTenantId()));
        avatarInstanceInfo.setAppId(startInstanceData.getAppId());
        avatarInstanceInfo.setSessionId(startInstanceData.getSessionId());

        AvatarInstanceInfo.Channel channel = new AvatarInstanceInfo.Channel();
        channel.setChannelId(startInstanceData.getChannelId());
        channel.setExpiredTime(startInstanceData.getExpiredTime());
        channel.setNonce(startInstanceData.getNonce());
        channel.setGslb(startInstanceData.getGslbUrls());
        channel.setType("RTMP");
        channel.setToken(startInstanceData.getToken());
        channel.setUserId(startInstanceData.getUserId());
        channel.setAppId(startInstanceData.getRtcAppId());
        channel.setName("avatar_sdk_android_demo");
        avatarInstanceInfo.setChannel(channel);

        return avatarInstanceInfo;
    }
}
