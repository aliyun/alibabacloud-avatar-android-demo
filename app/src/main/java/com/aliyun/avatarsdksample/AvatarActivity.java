package com.aliyun.avatarsdksample;

import static com.alivc.rtc.AliRtcEngine.AliRtcRenderMode.AliRtcRenderModeAuto;
import static com.alivc.rtc.AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackBoth;
import static com.alivc.rtc.AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackCamera;
import static com.alivc.rtc.AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackNo;
import static com.alivc.rtc.AliRtcEngine.AliRtcVideoTrack.AliRtcVideoTrackScreen;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.alivc.rtc.AliRtcEngine;
import com.alivc.rtc.AliRtcRemoteUserInfo;
import com.aliyun.avatarsdk.AvatarSDK;
import com.aliyun.avatarsdk.AvatarSDKListener;
import com.aliyun.avatarsdk.data.AvatarInstanceInfo;
import com.aliyun.avatarsdk.data.AvatarOptions;
import com.aliyun.avatarsdk.rtc.AvatarRtcEngineEventListener;
import com.aliyun.avatarsdk.rtc.AvatarRtcEngineNotify;
import com.aliyun.avatarsdksample.data.ChartUserBean;
import com.aliyun.avatarsdksample.openapi.AvatarInstanceClient;
import com.aliyun.avatarsdksample.openapi.AvatarInstanceRequest;
import com.aliyun.avatarsdksample.openapi.AvatarInstanceResponse;
import com.aliyun.avatarsdksample.util.InstanceSharedPref;
import com.aliyun.avatarsdksample.util.InstanceUtil;
import com.aliyun.avatarsdksample.util.PermissionUtil;

import org.webrtc.sdk.SophonSurfaceView;

public class AvatarActivity extends AppCompatActivity {
    private static final String TAG = "AvatarSdkSample";

    private TextView tvStatus;
    private Button btnDestroy;
    private StringBuffer stringBuffer = new StringBuffer();
    private AvatarSDK avatarSDK;
    /**
     * SDK提供的对音视频通话处理的引擎类
     */
    private AliRtcEngine mAliRtcEngine;
    private AvatarInstanceRequest request;
    private AvatarInstanceClient client;
    private AvatarSDKListener avatarSDKListener = new AvatarSDKListener() {

        @Override
        public void onInitSuccess(String uid, AliRtcEngine.AliRtcAudioTrack audioTrack, AliRtcEngine.AliRtcVideoTrack videoTrack) {

            Log.d(TAG, "onInitSuccess");
            stringBuffer.append("\n");
            stringBuffer.append("----初始化成功----");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvStatus.setText(stringBuffer);
                }
            });
            updateRemoteDisplay(uid, audioTrack, videoTrack);

        }

        @Override
        public void onError(int errorCode, String source, String errorMsg) {

            Log.e(TAG, "onError code : " + errorCode + " ,message :" + errorMsg);
            stringBuffer.append("\n");
            stringBuffer.append("----发生异常----");
            stringBuffer.append("\n");
            stringBuffer.append("ErrorCode : ");
            stringBuffer.append(errorCode);
            stringBuffer.append(", source : ");
            stringBuffer.append(source);
            stringBuffer.append(", message: ");
            stringBuffer.append(errorMsg);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvStatus.setText(stringBuffer);
                    int offset = getTextViewContentHeight(tvStatus);
                    if (offset > tvStatus.getHeight()) {
                        tvStatus.scrollTo(0, offset - tvStatus.getHeight());
                    }
                }
            });
        }

        @Override
        public void onAsr(String asrResult, String sentenceId) {
            Log.d(TAG, "onAsr asrResult " + asrResult);
            Log.d(TAG, "onAsr sentenceId " + sentenceId);
            stringBuffer.append("\n");
            stringBuffer.append("onAsr: ");
            stringBuffer.append(asrResult);
            stringBuffer.append("\n");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvStatus.setText(stringBuffer);
                    int offset = getTextViewContentHeight(tvStatus);
                    if (offset > tvStatus.getHeight()) {
                        tvStatus.scrollTo(0, offset - tvStatus.getHeight());
                    }
                }
            });

        }

        @Override
        public void onAnswer(String answerResult, String sentenceId) {
            Log.d(TAG, "onAnswer answerResult " + answerResult);
            Log.d(TAG, "onAnswer sentenceId " + sentenceId);
            stringBuffer.append("\n");
            stringBuffer.append("onAnswer: ");
            stringBuffer.append(answerResult);
            stringBuffer.append("\n");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvStatus.setText(stringBuffer);
                    int offset = getTextViewContentHeight(tvStatus);
                    if (offset > tvStatus.getHeight()) {
                        tvStatus.scrollTo(0, offset - tvStatus.getHeight());
                    }

                }
            });

        }

        @Override
        public void onRecordingStart() {
            super.onRecordingStart();
            stringBuffer.append("\n");
            stringBuffer.append("----开始录音----");
            stringBuffer.append("\n");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvStatus.setText(stringBuffer);
                }
            });
            Log.d(TAG, "onRecordingStart");
        }

        @Override
        public void onRecordingStop() {
            super.onRecordingStop();
            stringBuffer.append("\n");
            stringBuffer.append("----停止录音----");
            stringBuffer.append("\n");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvStatus.setText(stringBuffer);
                }
            });
            Log.d(TAG, "onRecordingStop");
        }

        @Override
        public void onUnknown(String msg) {
            super.onUnknown(msg);
            Log.d(TAG, "onUnknown " + msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);
        tvStatus = findViewById(R.id.tv_status);
        tvStatus.setMovementMethod(ScrollingMovementMethod.getInstance());


        if (!PermissionUtil.lacksPermission(this)) {
            initAvatar();
        } else{
            PermissionUtil.requestPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i : grantResults) {
            if (i == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "权限异常，请手动开启全部权限", Toast.LENGTH_LONG).show();
                this.finish();
                return;
            }
        }
        initAvatar();
    }

    private void initAvatar() {
        Intent intent = getIntent();
        String response = intent.getStringExtra("response");
        if (TextUtils.isEmpty(response)) {
            return;
        }
        int avatarType = intent.getIntExtra("avatarType", 0);
        AvatarInstanceInfo avatarInstanceInfo = InstanceUtil.convertByAvatarInstanceResponse(response);
        Log.d(TAG, "avatarInstanceInfo : " + JSONObject.toJSONString(avatarInstanceInfo));
        Log.d(TAG, "avatarType " + avatarType);
        if (avatarType == 0) {
            initAsDialogAvatar(avatarInstanceInfo);
        } else {
            initAsBroadcastAvatar(avatarInstanceInfo);
        }
    }

    private void initAsDialogAvatar(AvatarInstanceInfo avatarInstanceInfo) {
        Intent intent = getIntent();
        int aecConfig = intent.getIntExtra("aecConfig", AvatarOptions.AEC_SYSTEM);
        int interval = intent.getIntExtra("interval",100);
        int sampleRate = intent.getIntExtra("sampleRate",16000);
        boolean isAutoStartRecord = intent.getBooleanExtra("isAutoStartRecord",true);
        boolean isAutoDodge = intent.getBooleanExtra("isAutoDodge",false);
        boolean isCustomSource = intent.getBooleanExtra("isCustomSource" ,false);

        AvatarOptions options = new AvatarOptions.Builder().aecConfig(aecConfig).interval(interval).sampleRate(sampleRate).setMaxReconnectTimeout(120000).autoStartRecord(isAutoStartRecord).autoDodge(isAutoDodge).useCustomAudioSource(false).build();
        stringBuffer.append("开始创建对话数字人...");
        stringBuffer.append("\n");
        stringBuffer.append("详细配置：");
        stringBuffer.append(JSONObject.toJSONString(options));
        stringBuffer.append("\n");
        avatarSDK = AvatarSDK.createDialogAvatarInstance(this, avatarSDKListener, avatarInstanceInfo, options);
        mAliRtcEngine = avatarSDK.getAliRtcEngine();


//        本地用户行为回调，相当于AliRtcEngineEventListener，必须调用super.onOccurError(error, message); 否则AvatarSDK无法抛出RTC异常信息
        avatarSDK.setRtcEngineEventListener(new AvatarRtcEngineEventListener() {
            @Override
            public void onJoinChannelResult(int result, String channel, String userId, int elapsed) {
                super.onJoinChannelResult(result, channel, userId, elapsed);
                Log.d(TAG, "onJoinChannelResult result " + result);
                Log.d(TAG, "onJoinChannelResult channel " + channel);
                Log.d(TAG, "onJoinChannelResult userId " + userId);
            }

            @Override
            public void onConnectionStatusChange(AliRtcEngine.AliRtcConnectionStatus status, AliRtcEngine.AliRtcConnectionStatusChangeReason reason) {
                super.onConnectionStatusChange(status, reason);
                Log.d(TAG, "onConnectionStatusChange status " + status);
                Log.d(TAG, "onConnectionStatusChange reason " + reason);
            }

            @Override
            public void onOccurError(int error, String message) {
                super.onOccurError(error, message);
            }
        });
        // 远端用户行为回调，相当于AliRtcEngineNotify，必须调用super.onRemoteTrackAvailableNotify(uid, audioTrack, videoTrack); 否则AvatarSDK无法回调onInitSuccess
        avatarSDK.setRtcEngineNotify(new AvatarRtcEngineNotify() {
            @Override
            public void onRemoteTrackAvailableNotify(String uid, AliRtcEngine.AliRtcAudioTrack audioTrack, AliRtcEngine.AliRtcVideoTrack videoTrack) {
                super.onRemoteTrackAvailableNotify(uid, audioTrack, videoTrack);
            }

            @Override
            public void onBye(int i) {
                super.onBye(i);
            }
        });

        //获取音频裸数据回调，相当于AliRtcAudioObserver
//        avatarSDK.setAvatarRtcAudioObserver(new AvatarRtcAudioObserver() {
//            @Override
//            public void onCaptureRawData(AliRtcEngine.AliRtcAudioSample aliRtcAudioSample) {
//            }
//
//            @Override
//            public void onCaptureData(AliRtcEngine.AliRtcAudioSample aliRtcAudioSample) {
//
//            }
//
//            @Override
//            public void onRenderData(AliRtcEngine.AliRtcAudioSample aliRtcAudioSample) {
//
//            }
//
//            @Override
//            public void onPlaybackAudioFrameBeforeMixing(String s, AliRtcEngine.AliRtcAudioSample aliRtcAudioSample) {
//
//            }
//        });

        //音量回调，相当于AliRtcAudioVolumeObserver，仅在启用RTC AEC 时生效。
//        avatarSDK.setAvatarRtcAudioVolumeObserver(new AvatarRtcAudioVolumeObserver() {
//            @Override
//            public void onAudioVolume(List<AliRtcEngine.AliRtcAudioVolume> speakers, int totalVolume) {
//                super.onAudioVolume(speakers, totalVolume);
//            }
//
//            @Override
//            public void onActiveSpeaker(String uid) {
//                super.onActiveSpeaker(uid);
//            }
//        });
        Button btnStart = findViewById(R.id.btn_start_record);
        btnStart.setVisibility(View.VISIBLE);
        Button btnStop = findViewById(R.id.btn_stop_record);
        btnStop.setVisibility(View.VISIBLE);

//        Button customVoiceSource = findViewById(R.id.btn_custom_source);
//        Button btnAutoDodge = findViewById(R.id.btn_auto_dodge);
//
//        if (options.isCustomAudioSource()) {
//            customVoiceSource.setEnabled(true);
//            btnStart.setEnabled(false);
//            btnStop.setEnabled(false);
//            btnAutoDodge.setEnabled(false);
//        } else {
//            customVoiceSource.setEnabled(false);
//        }
//
//
        btnStart.setOnClickListener(v -> avatarSDK.startRecording());
        btnStop.setOnClickListener(v -> avatarSDK.stopRecording());
        Button btnSendMessage = findViewById(R.id.btn_send_message);
        btnSendMessage.setVisibility(View.VISIBLE);

        btnSendMessage.setOnClickListener(v -> sendMessage(avatarInstanceInfo));
//        btnAutoDodge.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (avatarSDK.isAutoDodge()) {
//                    avatarSDK.setAutoDodge(false);
//                    stringBuffer.append("\n");
//                    stringBuffer.append("----音频闪避：关闭----");
//                    tvStatus.setText(stringBuffer);
//
//                } else {
//                    avatarSDK.setAutoDodge(true);
//                    stringBuffer.append("\n");
//                    stringBuffer.append("----音频闪避：打开----");
//                    tvStatus.setText(stringBuffer);
//                }
//            }
//        });
//
//        customVoiceSource.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//
//                            InputStream is = getResources().getAssets().open("test16k.wav");
//
//                            int read;
//                            byte[] buff = new byte[10240];
//
//                            while (true) {
//                                if (!((read = is.read(buff)) > 0)) break;
//
//                                Log.d(TAG, "sendAudioBuffer");
//                                avatarSDK.sendAudioBuffer(buff);
//                                Thread.sleep(200);
//                            }
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }).start();
//            }
//        });

        avatarSDK.init();

    }

    private void initAsBroadcastAvatar(AvatarInstanceInfo avatarInstanceInfo) {
        stringBuffer.append("开始创建播报数字人...");
        tvStatus.setText(stringBuffer);
        Button btnSendMessage = findViewById(R.id.btn_send_message);
        btnSendMessage.setVisibility(View.VISIBLE);
        AvatarOptions options = new AvatarOptions.Builder().setMaxReconnectTimeout(180000).build();
        btnSendMessage.setOnClickListener(v -> sendMessage(avatarInstanceInfo));
        avatarSDK = AvatarSDK.createBroadcastAvatarInstance(this, avatarSDKListener, avatarInstanceInfo, options);
        mAliRtcEngine = avatarSDK.getAliRtcEngine();
        avatarSDK.init();
    }

    private void sendMessage(AvatarInstanceInfo avatarInstanceInfo) {
        if (request == null) {
            AvatarInstanceResponse responseCache = InstanceSharedPref.loadInstanceResponse(getApplicationContext());
            request = new AvatarInstanceRequest();
            request.setAccessKeyId(responseCache.getAccessKeyId());
            request.setAccessKeySecret(responseCache.getAccessKeySecret());
            request.settenantId(responseCache.getTenantId());
            request.setAppId(responseCache.getAppId());
            client = new AvatarInstanceClient(this, request);
        }
        EditText etMessage = findViewById(R.id.et_message);
        String msg = etMessage.getText().toString();
        client.sendMessage(msg, avatarInstanceInfo.getSessionId());
        etMessage.setText("");
    }


    private void updateRemoteDisplay(String uid, AliRtcEngine.AliRtcAudioTrack at, AliRtcEngine.AliRtcVideoTrack vt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null == mAliRtcEngine) {
                    return;
                }
                AliRtcRemoteUserInfo remoteUserInfo = mAliRtcEngine.getUserInfo(uid);
                // 如果没有，说明已经退出了或者不存在。则不需要添加，并且删除
                if (remoteUserInfo == null) {
                    // remote user exit room
                    Log.e(TAG, "updateRemoteDisplay remoteUserInfo = null, uid = " + uid);
                    return;
                }
                //change
                AliRtcEngine.AliRtcVideoCanvas cameraCanvas = remoteUserInfo.getCameraCanvas();
                AliRtcEngine.AliRtcVideoCanvas screenCanvas = remoteUserInfo.getScreenCanvas();
                //视频情况
                if (vt == AliRtcVideoTrackNo) {
                    //没有视频流
                    cameraCanvas = null;
                    screenCanvas = null;
                } else if (vt == AliRtcVideoTrackCamera) {
                    //相机流
                    screenCanvas = null;
                    cameraCanvas = createCanvasIfNull(cameraCanvas);
//                    cameraCanvas.mirrorMode = AliRtcEngine.AliRtcRenderMirrorMode.AliRtcRenderMirrorModeAllEnabled;
                    //SDK内部提供进行播放的view
                    mAliRtcEngine.setRemoteViewConfig(cameraCanvas, uid, AliRtcVideoTrackCamera);
                } else if (vt == AliRtcVideoTrackScreen) {
                    //屏幕流
                    cameraCanvas = null;
                    screenCanvas = createCanvasIfNull(screenCanvas);
                    //SDK内部提供进行播放的view
                    mAliRtcEngine.setRemoteViewConfig(screenCanvas, uid, AliRtcVideoTrackScreen);
                } else if (vt == AliRtcVideoTrackBoth) {
                    //多流
                    cameraCanvas = createCanvasIfNull(cameraCanvas);
                    //SDK内部提供进行播放的view
                    mAliRtcEngine.setRemoteViewConfig(cameraCanvas, uid, AliRtcVideoTrackCamera);
                    screenCanvas = createCanvasIfNull(screenCanvas);
                    //SDK内部提供进行播放的view
                    mAliRtcEngine.setRemoteViewConfig(screenCanvas, uid, AliRtcVideoTrackScreen);
                } else {
                    return;
                }
                ChartUserBean chartUserBean = convertRemoteUserInfo(remoteUserInfo, cameraCanvas, screenCanvas);
//                mUserListAdapter.updateData(chartUserBean, true);
                FrameLayout surfaceContainer = findViewById(R.id.surface_container);
                if (chartUserBean.mCameraSurface instanceof SophonSurfaceView) {
                    SophonSurfaceView surfaceView = (SophonSurfaceView) chartUserBean.mCameraSurface;
                    surfaceView.setZOrderOnTop(false);
                    surfaceView.setZOrderMediaOverlay(false);
                }
                Log.d("chartUserBean", chartUserBean.mUserName);
                if (chartUserBean.mCameraSurface == null) {
                    surfaceContainer.removeAllViews();
                } else if (chartUserBean.mCameraSurface.getParent() == null) {
                    if (chartUserBean.mUserName.equals("avatar")) {
                        surfaceContainer.addView(chartUserBean.mCameraSurface,
                                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                    }
                }
            }
        });
    }

    private AliRtcEngine.AliRtcVideoCanvas createCanvasIfNull(AliRtcEngine.AliRtcVideoCanvas canvas) {
        if (canvas == null || canvas.view == null) {
            //创建canvas，Canvas为SophonSurfaceView或者它的子类
            canvas = new AliRtcEngine.AliRtcVideoCanvas();
            SophonSurfaceView surfaceView = new SophonSurfaceView(this);
            surfaceView.setZOrderOnTop(true);
            surfaceView.setZOrderMediaOverlay(true);
            canvas.view = surfaceView;
            //renderMode提供四种模式：Auto、Stretch、Fill、Crop，建议使用Auto模式。
            canvas.renderMode = AliRtcRenderModeAuto;
        }
        return canvas;
    }

    private ChartUserBean convertRemoteUserInfo(AliRtcRemoteUserInfo remoteUserInfo,
                                                AliRtcEngine.AliRtcVideoCanvas cameraCanvas,
                                                AliRtcEngine.AliRtcVideoCanvas screenCanvas) {
        String uid = remoteUserInfo.getUserID();
        ChartUserBean ret = new ChartUserBean();
        ret.mUserId = remoteUserInfo.getUserID();

        ret.mUserName = remoteUserInfo.getDisplayName();

        ret.mCameraSurface = cameraCanvas != null ? cameraCanvas.view : null;
        ret.mIsCameraFlip = true;

        ret.mScreenSurface = screenCanvas != null ? screenCanvas.view : null;
        ret.mIsScreenFlip = screenCanvas != null && screenCanvas.mirrorMode == AliRtcEngine.AliRtcRenderMirrorMode.AliRtcRenderMirrorModeAllEnabled;

        return ret;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (avatarSDK != null) {
            avatarSDK.destroy();
        }
    }

    private int getTextViewContentHeight(TextView textView) {
        Layout layout = textView.getLayout();
        int desired = layout.getLineTop(textView.getLineCount());
        int padding = textView.getCompoundPaddingTop() + textView.getCompoundPaddingBottom();
        return desired + padding;
    }


}