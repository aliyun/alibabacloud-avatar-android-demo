package com.aliyun.avatarsdksample;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.avatarsdk.data.AvatarOptions;
import com.aliyun.avatarsdksample.openapi.AvatarInstanceClient;
import com.aliyun.avatarsdksample.openapi.AvatarInstanceRequest;
import com.aliyun.avatarsdksample.openapi.AvatarInstanceResponse;
import com.aliyun.avatarsdksample.openapi.InstanceEventCallback;
import com.aliyun.avatarsdksample.util.InstanceSharedPref;
import com.aliyun.tea.utils.StringUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, InstanceEventCallback, RadioButton.OnCheckedChangeListener {
    private static final String TAG = "MainActivity";

    private Button startBtn;
    private Button stopBtn;
    private Button queryBtn;
    private TextView accessKeyIdTxt;
    private TextView accessKeySecretTxt;
    private TextView tenantIdTxt;
    private TextView appIdTxt;

    private AvatarInstanceClient client;
    private String sessionId;
    private RadioGroup rgAvatarType;
    private RadioButton rbDialog, rbBroadcast, rbAecRtc, rbAecSystem, rbAecNone, rbSample8k, rbSample16k;
    private CheckBox cbAutoStartRecord, cbAutoDodge, cbCustomSource;
    private ConstraintLayout clInitConfig;
    private EditText etInterval;

    private AvatarInstanceRequest request;

    private boolean isAutostartRecord, isAutoDodge, isCustomSource;
    private int sampleRate, aecConfig, interval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        aecConfig= AvatarOptions.AEC_SYSTEM;
        sampleRate = AvatarOptions.SAMPLE_RATE_16K;
        AvatarInstanceResponse response = InstanceSharedPref.loadInstanceResponse(getApplicationContext());
        if (!StringUtils.isEmpty(response.getAccessKeyId()) &&
                !StringUtils.isEmpty(response.getAccessKeySecret()) &&
                !StringUtils.isEmpty(response.getAppId()) &&
                !StringUtils.isEmpty(response.getTenantId()) &&
                !StringUtils.isEmpty(response.getEnv())
        ) {
            accessKeyIdTxt.setText(response.getAccessKeyId());
            accessKeySecretTxt.setText(response.getAccessKeySecret());
            tenantIdTxt.setText(String.valueOf(response.getTenantId()));
            appIdTxt.setText(response.getAppId());
        }
        if (!StringUtils.isEmpty(response.getAccessKeyId())) {
            queryInstance(response);
        }
    }

    private void initView() {
        startBtn = findViewById(R.id.btn_start);
        stopBtn = findViewById(R.id.btn_stop);
        queryBtn = findViewById(R.id.btn_query);
        startBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
        queryBtn.setOnClickListener(this);
        accessKeyIdTxt = findViewById(R.id.accessKeyIdTxt);
        accessKeySecretTxt = findViewById(R.id.accessKeySecretTxt);
        tenantIdTxt = findViewById(R.id.tenantIdTxt);
        appIdTxt = findViewById(R.id.appIdTxt);
        rgAvatarType = findViewById(R.id.radioType);
        rbDialog = findViewById(R.id.radioDialog);
        rbBroadcast = findViewById(R.id.radioBroadcast);
        rbAecNone = findViewById(R.id.rb_aec_none);
        rbAecRtc = findViewById(R.id.rb_aec_rtc);
        rbAecSystem = findViewById(R.id.rb_aec_system);
        rbSample8k = findViewById(R.id.rb_sample_8k);
        rbSample16k = findViewById(R.id.rb_sample_16k);
        cbAutoStartRecord = findViewById(R.id.cb_auto_start);
        cbAutoDodge = findViewById(R.id.cb_auto_douge);
        cbCustomSource = findViewById(R.id.cb_custom_source);

        rbDialog.setOnCheckedChangeListener(this);
        rbBroadcast.setOnCheckedChangeListener(this);
        rbAecRtc.setOnCheckedChangeListener(this);
        rbAecNone.setOnCheckedChangeListener(this);
        rbAecSystem.setOnCheckedChangeListener(this);
        rbSample16k.setOnCheckedChangeListener(this);
        rbSample8k.setOnCheckedChangeListener(this);
        cbAutoStartRecord.setOnCheckedChangeListener(this);
        cbAutoDodge.setOnCheckedChangeListener(this);
        cbCustomSource.setOnCheckedChangeListener(this);

        clInitConfig = findViewById(R.id.cl_config);
        etInterval = findViewById(R.id.et_interval);
    }

    private void queryInstance(AvatarInstanceResponse response) {
        String accessKeyId = TextUtils.isEmpty(accessKeyIdTxt.getText().toString()) ? response.getAccessKeyId() : accessKeyIdTxt.getText().toString();
        String accessKeySecret = TextUtils.isEmpty(accessKeySecretTxt.getText().toString()) ? response.getAccessKeySecret() : accessKeySecretTxt.getText().toString();
        long tenantId = StringUtils.isEmpty(tenantIdTxt.getText().toString()) ? response.getTenantId() : Long.parseLong(tenantIdTxt.getText().toString());
        String appId = TextUtils.isEmpty(appIdTxt.getText().toString()) ? response.getAppId() : appIdTxt.getText().toString();


        if (TextUtils.isEmpty(accessKeyId)) {
            showToast("AccessKeyId 不能为空");
            return;
        }

        if (TextUtils.isEmpty(accessKeyId)) {
            showToast("AccessKeySecret 不能为空");
            return;
        }
        if (TextUtils.isEmpty(accessKeyId)) {
            showToast("TenantId 不能为空");
            return;
        }
        if (TextUtils.isEmpty(accessKeyId)) {
            showToast("AppId 不能为空");
            return;
        }


        String env = "PROD";
        request = new AvatarInstanceRequest();
        request.setAccessKeyId(accessKeyId);
        request.setAccessKeySecret(accessKeySecret);
        request.settenantId(tenantId);
        request.setAppId(appId);
        request.setEnv(env);
        client = new AvatarInstanceClient(this, request);
        client.setEventCallback(this);

        client.queryInstance(accessKeyId, accessKeySecret, appId, tenantId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(sessionId)) {
            return;
        }
        client.stopInstance(this.sessionId);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start: {
                String accessKeyId = accessKeyIdTxt.getText().toString();
                String accessKeySecret = accessKeySecretTxt.getText().toString();
                Long tenantId = StringUtils.isEmpty(tenantIdTxt.getText().toString()) ? 0 : Long.parseLong(tenantIdTxt.getText().toString());
                String appId = appIdTxt.getText().toString();
//				String env = rbProd.isChecked() ? "PROD" : "PRE";
                String env = "PROD";
                request = new AvatarInstanceRequest();
                request.setAccessKeyId(accessKeyId);
                request.setAccessKeySecret(accessKeySecret);
                request.settenantId(tenantId);
                request.setAppId(appId);
                request.setEnv(env);
                client = new AvatarInstanceClient(this, request);
                client.setEventCallback(this);
                client.startInstance();
                break;
            }
            case R.id.btn_stop: {
                if (TextUtils.isEmpty(sessionId)) {
                    showToast("sessionId为空，请先查询执行Query Instance");
                    return;
                }
                client.stopInstance(this.sessionId);
                break;
            }
            case R.id.btn_query:
                AvatarInstanceResponse response = InstanceSharedPref.loadInstanceResponse(getApplicationContext());
                queryInstance(response);
            default:
                break;
        }
    }


    @Override
    public void onStartInstance(AvatarInstanceResponse response) {
        if (response.getStatusCode() != 200 || response.getSuccess() == null || !response.getSuccess()) {
            showToast(response.getMessage());
            return;
        }

        response.setAccessKeyId(request.getAccessKeyId());
        response.setAccessKeySecret(request.getAccessKeySecret());
        response.setAppId(request.getAppId());
        response.setTenantId(request.gettenantId());
        response.setEnv(request.getEnv());
        InstanceSharedPref.saveInstanceResponse(getApplicationContext(), response);
        startRtcActivity(response);
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);

    }

    @Override
    public void onStopInstance(AvatarInstanceResponse response) {
        if (response.getStatusCode() != 200 || response.getSuccess() == null || !response.getSuccess()) {
            Log.e(TAG, "failed to stop instance, msg = " + response.getMessage());
            showToast(response.getMessage());
            return;
        }

        Log.i(TAG, "Stop instance successfully");
        this.sessionId = null;

        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
    }

    @Override
    public void onQueryInstance(AvatarInstanceResponse response) {
        if (response.getStatusCode() != 200 || response.getSuccess() == null || !response.getSuccess()) {
            showToast(response.getMessage());
            return;
        }

        if (StringUtils.isEmpty(response.getRtcAppId()) || StringUtils.isEmpty(response.getToken())) {
            showToast("没有运行中的实例");
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
            return;
        }
        showToast("有实例正在运行，清先将其停止");
        this.sessionId = response.getSessionId();
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
    }

    public void startRtcActivity(AvatarInstanceResponse response) {
        String sessionId = response.getSessionId();
        Log.i(TAG, "Start instance success, sessionId = " + sessionId + ", response=" + response);
        this.sessionId = sessionId;

        int avatarType = rbDialog.isChecked() ? 0 : 1;
        Intent intent = new Intent(this, AvatarActivity.class);
        intent.putExtra("response", JSONObject.toJSONString(response));
        intent.putExtra("avatarType", avatarType);
        intent.putExtra("aecConfig", aecConfig);
        intent.putExtra("sampleRate", sampleRate);
        intent.putExtra("isAutoStartRecord", isAutostartRecord);
        intent.putExtra("isAutoDodge", isAutoDodge);
        intent.putExtra("interval",TextUtils.isEmpty(etInterval.getText())? 100: Integer.parseInt(etInterval.getText().toString()));
        intent.putExtra("isCustomSource", false);
        startActivity(intent);
    }

    private void showToast(final String msg) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.radioBroadcast:
                    clInitConfig.setVisibility(View.GONE);
                    break;
                case R.id.radioDialog:
                    clInitConfig.setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_aec_none:
                    aecConfig = AvatarOptions.AEC_NONE;
                    break;
                case R.id.rb_aec_rtc:
                    aecConfig = AvatarOptions.AEC_RTC;
                    break;
                case R.id.rb_aec_system:
                    aecConfig = AvatarOptions.AEC_SYSTEM;
                    break;
                case R.id.rb_sample_8k:
                    sampleRate = AvatarOptions.SAMPLE_RATE_8K;
                    break;
                case R.id.rb_sample_16k:
                    sampleRate = AvatarOptions.SAMPLE_RATE_16K;
                    break;
                case R.id.cb_auto_douge:
                    isAutoDodge = true;
                    break;
                case R.id.cb_auto_start:
                    isAutostartRecord = true;
                    break;
                case R.id.cb_custom_source:
                    isCustomSource = true;
                    break;
                default:
                    break;
            }
        } else {
            switch (buttonView.getId()) {
                case R.id.cb_auto_douge:
                    isAutoDodge = false;
                    break;
                case R.id.cb_auto_start:
                    isAutostartRecord = false;
                    break;
                case R.id.cb_custom_source:
                    isCustomSource = false;
                    break;
                default:
                    break;

            }
        }
    }

}