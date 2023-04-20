package com.aliyun.avatarsdksample;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.aliyun.avatarsdk.data.AvatarOptions;
import com.aliyun.avatarsdksample.util.SharedPrefHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioButton.OnCheckedChangeListener {
    private static final String TAG = "MainActivity";

    private Button startBtn;
    private Button btnToggleConfig;

    private String sessionId;
    private RadioGroup rgAvatarType;
    private RadioButton rbDialog, rbBroadcast, rbAecRtc, rbAecSystem, rbAecNone, rbSample8k, rbSample16k, rbGreenBgTure, rbGreenBgFalse, rbDecodeHW, rbDecodeSW;
    private CheckBox cbAutoStartRecord, cbAutoDodge, cbCustomSource;
    private ConstraintLayout clInitConfig;
    private EditText etInterval ,etTenantId, etAppId, etResponseData;
    private ScrollView scrollView;
    private ConstraintLayout clGreenBg;


    private boolean isAutostartRecord, isAutoDodge, isCustomSource, isGreenBg;
    private int sampleRate, aecConfig, interval;
    private AvatarOptions.DecodeMode decodeConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        aecConfig = AvatarOptions.AEC_SYSTEM;
        sampleRate = AvatarOptions.SAMPLE_RATE_16K;
        decodeConfig = AvatarOptions.DecodeMode.SOFTWARE_DECODE;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(sessionId)) {
            return;
        }
    }

    private void initView() {
        startBtn = findViewById(R.id.btn_start);
        startBtn.setOnClickListener(this);
        rgAvatarType = findViewById(R.id.radioType);
        rbDialog = findViewById(R.id.radioDialog);
        rbBroadcast = findViewById(R.id.radioBroadcast);
        rbAecNone = findViewById(R.id.rb_aec_none);
        rbAecRtc = findViewById(R.id.rb_aec_rtc);
        rbAecSystem = findViewById(R.id.rb_aec_system);
        rbSample8k = findViewById(R.id.rb_sample_8k);
        rbSample16k = findViewById(R.id.rb_sample_16k);
        rbGreenBgTure = findViewById(R.id.rb_green_bg_true);
        rbGreenBgFalse = findViewById(R.id.rb_green_bg_false);
        cbAutoStartRecord = findViewById(R.id.cb_auto_start);
        cbAutoDodge = findViewById(R.id.cb_auto_douge);
        cbCustomSource = findViewById(R.id.cb_custom_source);
        clGreenBg = findViewById(R.id.cl_greenbg);
        rbDecodeHW = findViewById(R.id.rb_decode_hard);
        rbDecodeSW = findViewById(R.id.rb_decode_soft);

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
        rbGreenBgTure.setOnCheckedChangeListener(this);
        rbGreenBgFalse.setOnCheckedChangeListener(this);
        rbDecodeSW.setOnCheckedChangeListener(this);
        rbDecodeHW.setOnCheckedChangeListener(this);

        clInitConfig = findViewById(R.id.cl_config);
        etInterval = findViewById(R.id.et_interval);
        btnToggleConfig = findViewById(R.id.btn_toggle_config);
        btnToggleConfig.setOnClickListener(this);
        scrollView = findViewById(R.id.scrollView);

        etAppId = findViewById(R.id.appIdTxt);
        etResponseData = findViewById(R.id.responseDataTxt);
        etTenantId = findViewById(R.id.tenantIdTxt);

        String response =  SharedPrefHelper.loadString(this,"response");
        String appId =  SharedPrefHelper.loadString(this,"appId");
        String tenantId =  SharedPrefHelper.loadString(this,"tenantId");

        etResponseData.setText(response);
        etAppId.setText(appId);
        etTenantId.setText(tenantId);
    }

    public void startRtcActivity() {
        int avatarType = rbDialog.isChecked() ? 0 : 1;
        Intent intent = new Intent(this, AvatarActivity.class);
        intent.putExtra("avatarType", avatarType);
        intent.putExtra("aecConfig", aecConfig);
        intent.putExtra("decodeConfig", decodeConfig.ordinal());
        intent.putExtra("sampleRate", sampleRate);
        intent.putExtra("isAutoStartRecord", isAutostartRecord);
        intent.putExtra("isAutoDodge", isAutoDodge);
        intent.putExtra("interval", TextUtils.isEmpty(etInterval.getText()) ? 100 : Integer.parseInt(etInterval.getText().toString()));
        intent.putExtra("isCustomSource", false);
        intent.putExtra("isGreenBg", isGreenBg);
        intent.putExtra("response", etResponseData.getText().toString());
        intent.putExtra("tenant", etTenantId.getText().toString());
        intent.putExtra("appid",etAppId.getText().toString());
        startActivity(intent);
    }

    private void showToast(final String msg) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start: {
                startRtcActivity();
                break;
            }
            case R.id.btn_toggle_config:
                if (scrollView.getVisibility() == View.VISIBLE || clGreenBg.getVisibility() == View.VISIBLE) {
                    btnToggleConfig.setText("显示配置列表");
                    scrollView.setVisibility(View.GONE);
                    clGreenBg.setVisibility(View.GONE);
                } else {
                    btnToggleConfig.setText("隐藏配置列表");
                    if (rbDialog.isChecked()) {
                        scrollView.setVisibility(View.VISIBLE);
                    }
                    clGreenBg.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.btn_start:
                    if (TextUtils.isEmpty(etResponseData.getText())) {
                        showToast(" StartInstanceResponseData不能为空");
                        return;
                    }
                    if (TextUtils.isEmpty(etTenantId.getText())) {
                        showToast("TenantId 不能为空");
                        return;
                    }
                    if (TextUtils.isEmpty(etAppId.getText())) {
                        showToast("AppId 不能为空");
                        return;
                    }
                    startRtcActivity();
                    break;
                case R.id.radioBroadcast:
                    scrollView.setVisibility(View.GONE);
                    break;
                case R.id.radioDialog:
                    scrollView.setVisibility(View.GONE);
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
                case R.id.rb_green_bg_true:
                    isGreenBg = true;
                    break;
                case R.id.rb_green_bg_false:
                    isGreenBg = false;
                    break;
                case R.id.rb_decode_hard:
                    decodeConfig = AvatarOptions.DecodeMode.HARDWARE_DECODE;
                    break;
                case R.id.rb_decode_soft:
                    decodeConfig = AvatarOptions.DecodeMode.SOFTWARE_DECODE;
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