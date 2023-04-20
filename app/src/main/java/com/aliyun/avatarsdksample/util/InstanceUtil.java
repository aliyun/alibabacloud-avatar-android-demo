package com.aliyun.avatarsdksample.util;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.avatarsdk.data.AvatarInstanceInfo;
import com.aliyun.avatarsdksample.data.StartInstanceData;

/**
 * Created by xbaoyuan on 2022/11/10
 *
 * @date 2022/11/10
 */
public  class InstanceUtil {

    public static AvatarInstanceInfo convertByAvatarInstanceResponseData(String json){
        StartInstanceData startInstanceData = JSONObject.parseObject(json, StartInstanceData.class);
        AvatarInstanceInfo avatarInstanceInfo = new AvatarInstanceInfo();
        avatarInstanceInfo.setToken(startInstanceData.getToken());
        avatarInstanceInfo.setSessionId(startInstanceData.getSessionId());

        AvatarInstanceInfo.Channel channel = new AvatarInstanceInfo.Channel();
        channel.setChannelId(startInstanceData.getChannel().getChannelId());
        channel.setExpiredTime(Long.parseLong(startInstanceData.getChannel().getExpiredTime()));
        channel.setNonce(startInstanceData.getChannel().getNonce());
        channel.setGslb(startInstanceData.getChannel().getGslb().toArray(new String[0]));
        channel.setType("RTMP");
        channel.setToken(startInstanceData.getChannel().getToken());
        channel.setUserId(startInstanceData.getChannel().getUserId());
        channel.setAppId(startInstanceData.getChannel().getAppId());
        channel.setName("avatar_sdk_android_demo");
        avatarInstanceInfo.setChannel(channel);

        return avatarInstanceInfo;
    }
}
