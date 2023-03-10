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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.alivc.rtc.AliRtcEngine;
import com.alivc.rtc.AliRtcRemoteUserInfo;
import com.aliyun.avatar.media.sdk.android.RenderPixelFormat;
import com.aliyun.avatar.media.sdk.android.TransparentSurfaceView;
import com.aliyun.avatar.media.sdk.android.VideoRenderer;
import com.aliyun.avatarsdk.AvatarSDK;
import com.aliyun.avatarsdk.AvatarSDKListener;
import com.aliyun.avatarsdk.data.AvatarInstanceInfo;
import com.aliyun.avatarsdk.data.AvatarOptions;
import com.aliyun.avatarsdk.rtc.AvatarRtcEngineEventListener;
import com.aliyun.avatarsdk.rtc.AvatarRtcEngineNotify;
import com.aliyun.avatarsdk.utils.FrameStats;
import com.aliyun.avatarsdk.utils.VideoRecorder;
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
    private StringBuffer stringBuffer = new StringBuffer();
    private AvatarSDK avatarSDK;
    /**
     * SDK?????????????????????????????????????????????
     */
    private AliRtcEngine mAliRtcEngine;
    private AvatarInstanceRequest request;
    private AvatarInstanceClient client;
    private TransparentSurfaceView surfaceView;
    private FrameStats renderFrameStats;
    private boolean isGreenBg;
    private VideoRecorder videoRecorder = null;

    //?????????SDK????????????
    private AvatarSDKListener avatarSDKListener = new AvatarSDKListener() {

        @Override
        public void onInitSuccess(String uid, AliRtcEngine.AliRtcAudioTrack audioTrack, AliRtcEngine.AliRtcVideoTrack videoTrack) {

            Log.d(TAG, "onInitSuccess");
            stringBuffer.append("\n");
            stringBuffer.append("----???????????????----");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvStatus.setText(stringBuffer);
                }
            });
            if (isGreenBg) {
                updateRemoteDisplayByGreenBackground();
            } else {
                updateRemoteDisplay(uid, audioTrack, videoTrack);
            }

        }

        @Override
        public void onError(int errorCode, String source, String errorMsg) {
            Log.e(TAG, "onError code : " + errorCode + " ,message :" + errorMsg);
            stringBuffer.append("\n");
            stringBuffer.append("----????????????----");
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
            stringBuffer.append("----????????????----");
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
            stringBuffer.append("----????????????----");
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
        } else {
            PermissionUtil.requestPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i : grantResults) {
            if (i == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "??????????????????????????????????????????", Toast.LENGTH_LONG).show();
                this.finish();
                return;
            }
        }
        initAvatar();
    }

    /**
     * ??????MainActivity???????????????????????????????????????
     */
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
        int interval = intent.getIntExtra("interval", 100);
        int sampleRate = intent.getIntExtra("sampleRate", 16000);
        boolean isAutoStartRecord = intent.getBooleanExtra("isAutoStartRecord", true);
        boolean isAutoDodge = intent.getBooleanExtra("isAutoDodge", false);
        boolean isCustomSource = intent.getBooleanExtra("isCustomSource", false);
        isGreenBg = intent.getBooleanExtra("isGreenBg", false);

        Button btnStart = findViewById(R.id.btn_start_record);
        btnStart.setVisibility(View.VISIBLE);
        Button btnStop = findViewById(R.id.btn_stop_record);
        btnStop.setVisibility(View.VISIBLE);
        btnStart.setOnClickListener(v -> avatarSDK.startRecording());
        btnStop.setOnClickListener(v -> avatarSDK.stopRecording());
        Button btnSendMessage = findViewById(R.id.btn_send_message);
        btnSendMessage.setVisibility(View.VISIBLE);
        btnSendMessage.setOnClickListener(v -> sendMessage(avatarInstanceInfo));

        AvatarOptions options = new AvatarOptions.Builder().aecConfig(aecConfig).interval(interval).sampleRate(sampleRate).setMaxReconnectTimeout(120000).autoStartRecord(isAutoStartRecord).autoDodge(isAutoDodge).useCustomAudioSource(false).build();
        stringBuffer.append("???????????????????????????...");
        stringBuffer.append("\n");
        stringBuffer.append("???????????????");
        stringBuffer.append(JSONObject.toJSONString(options));
        stringBuffer.append("\n");
        avatarSDK = AvatarSDK.createDialogAvatarInstance(this, avatarSDKListener, avatarInstanceInfo, options);
        mAliRtcEngine = avatarSDK.getAliRtcEngine();
        //?????????????????????????????????
        mAliRtcEngine.registerVideoSampleObserver(mVideoObserver);

        //????????????????????????????????????AliRtcEngineEventListener???????????????super.onOccurError(error, message); ??????AvatarSDK????????????RTC????????????
        avatarSDK.setRtcEngineEventListener(new AvatarRtcEngineEventListener() {
            @Override
            public void onJoinChannelResult(int result, String channel, String userId, int elapsed) {
                super.onJoinChannelResult(result, channel, userId, elapsed);
            }

            @Override
            public void onConnectionStatusChange(AliRtcEngine.AliRtcConnectionStatus status, AliRtcEngine.AliRtcConnectionStatusChangeReason reason) {
                super.onConnectionStatusChange(status, reason);
            }

            @Override
            public void onOccurError(int error, String message) {
                super.onOccurError(error, message);
            }

        });
        // ????????????????????????????????????AliRtcEngineNotify???????????????super.onRemoteTrackAvailableNotify(uid, audioTrack, videoTrack); ??????AvatarSDK????????????onInitSuccess
        avatarSDK.setRtcEngineNotify(new AvatarRtcEngineNotify() {
            @Override
            public void onRemoteTrackAvailableNotify(String uid, AliRtcEngine.AliRtcAudioTrack audioTrack, AliRtcEngine.AliRtcVideoTrack videoTrack) {
                super.onRemoteTrackAvailableNotify(uid, audioTrack, videoTrack);
            }

            @Override
            public void onBye(int code) {
                super.onBye(code);
            }
        });

        //???????????????????????????????????????AliRtcAudioObserver
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

        //????????????????????????AliRtcAudioVolumeObserver???????????????RTC AEC ????????????
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
        /**** ???????????????????????????????????????????????????????????????????????? ***/
//
//        Button btnAutoDodge = findViewById(R.id.btn_auto_dodge);
//        btnAutoDodge.setVisibility(View.VISIBLE);
//        btnAutoDodge.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (avatarSDK.isAutoDodge()) {
//                    avatarSDK.setAutoDodge(false);
//                    stringBuffer.append("\n");
//                    stringBuffer.append("----?????????????????????----");
//                    tvStatus.setText(stringBuffer);
//
//                } else {
//                    avatarSDK.setAutoDodge(true);
//                    stringBuffer.append("\n");
//                    stringBuffer.append("----?????????????????????----");
//                    tvStatus.setText(stringBuffer);
//                }
//            }
//        });
        /**** ?????????????????? ****/
        /**** ????????????????????????????????????????????????????????????????????????????????????activity_main.xml??????cb_custom_source??????visible ***/
//
//        Button customVoiceSource = findViewById(R.id.btn_custom_source);
//        customVoiceSource.setVisibility(View.VISIBLE);
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
        /**** ????????????????????? ****/

        avatarSDK.init();

    }

    /**
     * ????????????????????????
     *
     * @param avatarInstanceInfo
     */
    private void initAsBroadcastAvatar(AvatarInstanceInfo avatarInstanceInfo) {
        Intent intent = getIntent();
        isGreenBg = intent.getBooleanExtra("isGreenBg", false);
        stringBuffer.append("???????????????????????????...");
        tvStatus.setText(stringBuffer);
        Button btnSendMessage = findViewById(R.id.btn_send_message);
        btnSendMessage.setVisibility(View.VISIBLE);
        btnSendMessage.setOnClickListener(v -> sendMessage(avatarInstanceInfo));
        avatarSDK = AvatarSDK.createBroadcastAvatarInstance(this, avatarSDKListener, avatarInstanceInfo, null);
        mAliRtcEngine = avatarSDK.getAliRtcEngine();
        //?????????????????????????????????
        mAliRtcEngine.registerVideoSampleObserver(mVideoObserver);
        avatarSDK.init();
    }

    /**
     * ??????OpenApi ???????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param avatarInstanceInfo
     */
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

    /**** ???????????????????????????????????????????????????????????????AliRtc??????????????? ****/

    private void updateRemoteDisplay(String uid, AliRtcEngine.AliRtcAudioTrack at, AliRtcEngine.AliRtcVideoTrack vt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null == mAliRtcEngine) {
                    return;
                }
                AliRtcRemoteUserInfo remoteUserInfo = mAliRtcEngine.getUserInfo(uid);
                // ???????????????????????????????????????????????????????????????????????????????????????
                if (remoteUserInfo == null) {
                    // remote user exit room
                    Log.e(TAG, "updateRemoteDisplay remoteUserInfo = null, uid = " + uid);
                    return;
                }
                //change
                AliRtcEngine.AliRtcVideoCanvas cameraCanvas = remoteUserInfo.getCameraCanvas();
                AliRtcEngine.AliRtcVideoCanvas screenCanvas = remoteUserInfo.getScreenCanvas();
                //????????????
                if (vt == AliRtcVideoTrackNo) {
                    //???????????????
                    cameraCanvas = null;
                    screenCanvas = null;
                } else if (vt == AliRtcVideoTrackCamera) {
                    //?????????
                    screenCanvas = null;
                    cameraCanvas = createCanvasIfNull(cameraCanvas);
//                    cameraCanvas.mirrorMode = AliRtcEngine.AliRtcRenderMirrorMode.AliRtcRenderMirrorModeAllEnabled;
                    //SDK???????????????????????????view
                    mAliRtcEngine.setRemoteViewConfig(cameraCanvas, uid, AliRtcVideoTrackCamera);
                } else if (vt == AliRtcVideoTrackScreen) {
                    //?????????
                    cameraCanvas = null;
                    screenCanvas = createCanvasIfNull(screenCanvas);
                    //SDK???????????????????????????view
                    mAliRtcEngine.setRemoteViewConfig(screenCanvas, uid, AliRtcVideoTrackScreen);
                } else if (vt == AliRtcVideoTrackBoth) {
                    //??????
                    cameraCanvas = createCanvasIfNull(cameraCanvas);
                    //SDK???????????????????????????view
                    mAliRtcEngine.setRemoteViewConfig(cameraCanvas, uid, AliRtcVideoTrackCamera);
                    screenCanvas = createCanvasIfNull(screenCanvas);
                    //SDK???????????????????????????view
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
                        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(1080,1920);
                        layout.setMargins(0, 100, 0, 0);
                        chartUserBean.mCameraSurface.setLayoutParams(layout);
                        surfaceContainer.addView(chartUserBean.mCameraSurface);
                    }
                }
            }
        });
    }

    private AliRtcEngine.AliRtcVideoCanvas createCanvasIfNull(AliRtcEngine.AliRtcVideoCanvas canvas) {
        if (canvas == null || canvas.view == null) {
            //??????canvas???Canvas???SophonSurfaceView??????????????????
            canvas = new AliRtcEngine.AliRtcVideoCanvas();
            SophonSurfaceView surfaceView = new SophonSurfaceView(this);
            surfaceView.setZOrderOnTop(true);
            surfaceView.setZOrderMediaOverlay(true);
            canvas.view = surfaceView;
            //renderMode?????????????????????Auto???Stretch???Fill???Crop???????????????Auto?????????
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

    /**** ??????????????????????????????????????????????????? ****/

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


    /**
     * ??????Textview?????????????????????
     *
     * @param textView
     * @return
     */
    private int getTextViewContentHeight(TextView textView) {
        Layout layout = textView.getLayout();
        int desired = layout.getLineTop(textView.getLineCount());
        int padding = textView.getCompoundPaddingTop() + textView.getCompoundPaddingBottom();
        return desired + padding;
    }


    /******* ????????????????????????????????? ********/

    /**
     * ????????????????????????????????????????????????
     */
    private void updateRemoteDisplayByGreenBackground() {
        surfaceView = new TransparentSurfaceView(AvatarActivity.this);
        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(1080,1920);
        layout.setMargins(0, 100, 0, 0);
        surfaceView.setLayoutParams(layout);
        surfaceView.init();

        FrameLayout surfaceContainer = findViewById(R.id.surface_container);
        ImageView imageView = new ImageView(AvatarActivity.this);
        FrameLayout.LayoutParams framelayoutParam = new FrameLayout.LayoutParams(1080,1920);
        framelayoutParam.setMargins(0, 100, 0, 0);
        imageView.setLayoutParams(framelayoutParam);
        imageView.setImageResource(R.drawable.bg);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                surfaceContainer.addView(imageView);
                surfaceContainer.addView(surfaceView);

            }
        });
    }


    //??????AliRtc???????????????????????????????????????????????????????????????
    private final AliRtcEngine.AliRtcVideoObserver mVideoObserver = new AliRtcEngine.AliRtcVideoObserver() {
        @Override
        public boolean onRemoteVideoSample(String callId, AliRtcEngine.AliRtcVideoSourceType sourceType, AliRtcEngine.AliRtcVideoSample videoSample) {
//            Log.i(TAG, "onRemoteVideoSample, " + videoSample.format.getValue() + ", sourceType= " + sourceType.name() + ", width=" + videoSample.width + ", height=" + videoSample.height
//                    + ", strideY=" + videoSample.strideY + ", strideU=" + videoSample.strideU + ", strideV=" + videoSample.strideV + ", size=" + videoSample.data.length);

            long start = System.currentTimeMillis();
            if (surfaceView != null) {
                VideoRenderer.VideoRenderFrame frame = new VideoRenderer.VideoRenderFrame(RenderPixelFormat.I420,
                        videoSample.dataFrameY, videoSample.dataFrameU, videoSample.dataFrameV,
                        videoSample.width, videoSample.height,
                        videoSample.strideY, videoSample.strideU, videoSample.strideV);
                surfaceView.renderFrame(frame);
            }

            return super.onRemoteVideoSample(callId, sourceType, videoSample);
        }
    };

}