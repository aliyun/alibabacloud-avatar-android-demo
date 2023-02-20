package com.aliyun.avatarsdksample.openapi;

import static org.webrtc.ali.ThreadUtils.runOnUiThread;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.avatar20220130.Client;
import com.aliyun.avatar20220130.models.QueryRunningInstanceRequest;
import com.aliyun.avatar20220130.models.QueryRunningInstanceResponse;
import com.aliyun.avatar20220130.models.QueryRunningInstanceResponseBody;
import com.aliyun.avatar20220130.models.SendMessageRequest;
import com.aliyun.avatar20220130.models.SendMessageResponse;
import com.aliyun.avatar20220130.models.StartInstanceRequest;
import com.aliyun.avatar20220130.models.StartInstanceResponse;
import com.aliyun.avatar20220130.models.StartInstanceResponseBody;
import com.aliyun.avatar20220130.models.StopInstanceRequest;
import com.aliyun.avatar20220130.models.StopInstanceResponse;
import com.aliyun.tea.utils.StringUtils;
import com.aliyun.teaopenapi.models.Config;

import java.util.List;

public class AvatarInstanceClient {
    private static final String TAG = "AvatarInstanceClient";

    private AvatarInstanceRequest instanceRequest;
    private Client client;
    private Handler handler;
    private Context context;
    private InstanceEventCallback eventCallback;

    public AvatarInstanceClient(Context context, AvatarInstanceRequest instanceRequest) {
        this.context = context;
        this.instanceRequest = instanceRequest;

        final HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        this.handler = new Handler(handlerThread.getLooper());
    }

    public void setEventCallback(InstanceEventCallback eventCallback) {
        this.eventCallback = eventCallback;
    }

    public void startInstance() {
        this.handler.post(() -> {
            try {
                asyncStartInstance();
            } catch (Exception e) {
                showToast("failed to call startInstance");
                Log.e(TAG, "failed to call startInstance, err=" + e.getMessage());
            }
        });
    }

    public void stopInstance(String sessionId) {
        this.handler.post(() -> {
            try {
                asyncStopInstance(sessionId);
            } catch (Exception e) {
                showToast("failed to call stopInstance");
                Log.e(TAG, "failed to call stopInstance, err=" + e.getMessage());
            }
        });
    }

    public void queryInstance(String accessKeyId, String accessKeySecret, String appId, long tenantId) {
        this.handler.post(() -> {
            try {
                asyncQueryInstance(accessKeyId, accessKeySecret, appId, tenantId);
            } catch (Exception e) {
                showToast("failed to call queryInstance");
                Log.e(TAG, "failed to call queryInstance, err=" + e.getMessage());
            }
        });
    }

    public void sendMessage(String msg, String sessionId) {
        this.handler.post(() -> {
            try {
                asyncSendMessage(msg, sessionId);
            } catch (Exception e) {
                showToast("failed to call SendMessage");
                Log.e(TAG, "failed to call SendMessage, err=" + e.getMessage());
            }
        });
    }

    public void asyncSendMessage(String msg, String sessionId) throws Exception {
        SendMessageRequest.SendMessageRequestTextRequest textRequest = new SendMessageRequest.SendMessageRequestTextRequest()
                .setInterrupt(false)
                .setCommandType("START")
                .setSpeechText(msg);
        SendMessageRequest request = new SendMessageRequest()
                .setTenantId(instanceRequest.gettenantId())
                .setSessionId(sessionId)
                .setTextRequest(textRequest);
        String accessKeyId = instanceRequest.getAccessKeyId();
        String accessKeySecret = instanceRequest.getAccessKeySecret();

        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.setEndpoint(getEndpoint("PROD"));
        client = new Client(config);

        SendMessageResponse response = client.sendMessage(request);
        Log.d("SendMessageResponse", JSONObject.toJSONString(response.body));
    }

    private void asyncStartInstance() throws Exception {
        String accessKeyId = instanceRequest.getAccessKeyId();
        String accessKeySecret = instanceRequest.getAccessKeySecret();
        Long tenantId = instanceRequest.gettenantId();
        String appId = instanceRequest.getAppId();
        String env = instanceRequest.getEnv();

        if (StringUtils.isEmpty(accessKeyId)) {
            showToast("access key id is required");
            return;
        }

        if (StringUtils.isEmpty(accessKeySecret)) {
            showToast("access key secret is required");
            return;
        }

        if (StringUtils.isEmpty(appId)) {
            showToast("app id is required");
            return;
        }

        if (tenantId <= 0) {
            showToast("talent id is required");
            return;
        }

        Log.i(TAG, "start instance, appId=" + appId + ", tenantId=" + tenantId + ", env=" + getEndpoint(env));
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.setEndpoint(getEndpoint(env));
        client = new Client(config);

        StartInstanceRequest request = new StartInstanceRequest();
        request.setTenantId(tenantId);
        request.setApp(new StartInstanceRequest.StartInstanceRequestApp().setAppId(appId));
        request.setUser(new StartInstanceRequest.StartInstanceRequestUser().setUserId("12345").setUserName("测试用户"));
        request.setCommandRequest(new StartInstanceRequest.StartInstanceRequestCommandRequest().setAlphaSwitch(instanceRequest.isAlphaSwitch()));
        StartInstanceResponse response = client.startInstance(request);

        Log.d("StartInstanceResponse", JSONObject.toJSONString(response.body));
        Bundle data = new Bundle();
        data.putString("type", "startInstance");
        data.putInt("statusCode", response.statusCode);

        if (response.body != null) {
            data.putBoolean("success", response.body.success != null && response.body.success);
            data.putString("message", response.body.message);
            if (response.body.data != null) {
                data.putString("imToken", response.body.data.token);
                data.putString("sessionId", response.body.data.sessionId);
                StartInstanceResponseBody.StartInstanceResponseBodyDataChannel channel = response.body.data.channel;

                if (channel != null) {
                    data.putString("rtcAppId", channel.getAppId());
                    data.putString("userId", channel.getUserId());
                    data.putString("nonce", channel.getNonce());
                    data.putLong("expiredTime", Long.parseLong(channel.getExpiredTime()));
                    data.putString("channelId", channel.getChannelId());
                    data.putString("token", channel.getToken());
                    data.putStringArray("gslbUrls", channel.getGslb().toArray(new String[0]));
                }
            }
        }

        Message message = callbackHandler.obtainMessage();
        message.setData(data);
        callbackHandler.sendMessage(message);
    }

    public void asyncQueryInstance(String accessKeyId, String accessKeySecret, String appId, long tenantId) throws Exception {
//		if (StringUtils.isEmpty(response.getSessionId())) {
//			showToast("Instance not started");
//			return;
//		}

        if (client == null) {
            Config config = new Config()
                    .setAccessKeyId(accessKeyId)
                    .setAccessKeySecret(accessKeySecret);
            config.setEndpoint(getEndpoint("PROD"));
            client = new Client(config);
        }

        QueryRunningInstanceRequest request = new QueryRunningInstanceRequest()
                .setTenantId(tenantId)
                .setApp(new QueryRunningInstanceRequest.QueryRunningInstanceRequestApp().setAppId(appId));
        QueryRunningInstanceResponse instanceResponse = client.queryRunningInstance(request);
        Log.d("QueryRunningInstanceResponse", JSONObject.toJSONString(instanceResponse.body));
        Bundle data = new Bundle();
        data.putString("type", "queryInstance");
        data.putInt("statusCode", instanceResponse.statusCode);
        data.putBoolean("success", instanceResponse.body.success != null && instanceResponse.body.success);
        data.putString("message", instanceResponse.body.message);
        if (instanceResponse.body.getData() != null && instanceResponse.body.getData().size() > 0) {
            data.putString("sessionId", instanceResponse.body.getData().get(0).getSessionId());
        }
        if (instanceResponse.body.data != null) {
            List<QueryRunningInstanceResponseBody.QueryRunningInstanceResponseBodyData> channels = instanceResponse.body.data;
            if (channels.size() > 0) {
                QueryRunningInstanceResponseBody.QueryRunningInstanceResponseBodyDataChannel channel = channels.get(0).channel;
                if (channel != null) {
                    data.putString("rtcAppId", channel.getAppId());
                    data.putString("userId", channel.getUserId());
                    data.putString("nonce", channel.getNonce());
                    data.putLong("expiredTime", Long.parseLong(channel.getExpiredTime()));
                    data.putString("channelId", channel.getChannelId());
                    data.putString("token", channel.getToken());
                    data.putStringArray("gslbUrls", channel.getGslb().toArray(new String[0]));
                }
            }
        }

        Message message = callbackHandler.obtainMessage();
        message.setData(data);
        callbackHandler.sendMessage(message);
    }

    public void asyncStopInstance(String sessionId) throws Exception {
        if (StringUtils.isEmpty(sessionId) || client == null) {
            showToast("Instance not started");
            return;
        }

        StopInstanceRequest request = new StopInstanceRequest()
                .setTenantId(instanceRequest.gettenantId())
                .setSessionId(sessionId);
        StopInstanceResponse response = client.stopInstance(request);
        Log.d("StopInstanceResponse", JSONObject.toJSONString(response.body));
        Bundle data = new Bundle();
        data.putString("type", "stopInstance");
        data.putInt("statusCode", response.statusCode);
        data.putBoolean("success", response.body.success);
        data.putString("message", response.body.message);

        Message message = callbackHandler.obtainMessage();
        message.setData(data);
        callbackHandler.sendMessage(message);
    }

    private final Handler callbackHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (eventCallback == null) {
                return false;
            }

            Bundle data = msg.getData();

            String type = data.getString("type");

            AvatarInstanceResponse response = new AvatarInstanceResponse();
            response.setStatusCode(data.getInt("statusCode"));
            response.setSuccess(data.getBoolean("success"));
            response.setMessage(data.getString("message"));
            response.setSessionId(data.getString("sessionId"));

            response.setRtcAppId(data.getString("rtcAppId"));
            response.setUserId(data.getString("userId"));
            response.setNonce(data.getString("nonce"));
            response.setExpiredTime(data.getLong("expiredTime"));
            response.setChannelId(data.getString("channelId"));
            response.setToken(data.getString("token"));
            response.setGslbUrls(data.getStringArray("gslbUrls"));
            response.setImToken(data.getString("imToken"));
            Log.d(TAG, JSONObject.toJSONString(response));
            if ("startInstance".equalsIgnoreCase(type)) {
                eventCallback.onStartInstance(response);
            } else if ("stopInstance".equalsIgnoreCase(type)) {
                eventCallback.onStopInstance(response);
            } else if ("queryInstance".equalsIgnoreCase(type)) {
                eventCallback.onQueryInstance(response);
            }

            return true;
        }
    });

    private void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getEndpoint(String env) {
        if ("PROD".equalsIgnoreCase(env)) {
            return "avatar.cn-zhangjiakou.aliyuncs.com";
        } else {
            return "avatar-pre.cn-hangzhou.aliyuncs.com";
        }
    }
}
