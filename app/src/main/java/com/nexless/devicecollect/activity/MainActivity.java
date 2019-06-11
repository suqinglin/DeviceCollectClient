package com.nexless.devicecollect.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.calm.comm.lib.qr.zxing.activity.CaptureActivity;
import com.nexless.ccommble.util.CommLog;
import com.nexless.devicecollect.AppConstants;
import com.nexless.devicecollect.R;
import com.nexless.devicecollect.httpservice.RxHelper;
import com.nexless.devicecollect.httpservice.ServiceFactory;
import com.nexless.devicecollect.model.DeviceInfo;
import com.nexless.devicecollect.model.SearchDeviceBean;
import com.nexless.devicecollect.model.TResponse;
import com.nexless.devicecollect.model.UploadDeviceInfoResponse;
import com.nexless.devicecollect.util.BluetoothUtil;
import com.nexless.devicecollect.util.DeviceUtil;

import java.math.BigInteger;

import io.reactivex.Observable;

/**
 * @date: 2019/6/10
 * @author: su qinglin
 * @description:
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private static final int REQ_QR_CODE = 0x0001;
    private static final int REQ_SEARCH_DEVICE = 0x0002;
    private static final int OPT_WRITE_TOKEN = 0x0003;
    private static final int OPT_WRITE_MAC = 0x0004;
    private static final int OPT_WRITE_SN = 0x0005;
    private static final int OPT_WRITE_TIME = 0x0006;
    private static final int OPT_WRITE_MANUF_ID = 0x0007;
    private static final int OPT_WRITE_TOOL_ID = 0x0008;
    private String mSn;
    private String mDevMac;
    private String mToken;
    private String mToolsMac;
    private TextView tvQrResult;
    private TextView tvDeviceInfoFirstResult;
    private TextView tvSelectToolsResult;
    private TextView tvTokenResult;
    private TextView tvWriteResult;
    private TextView tvDeviceInfoSecondResult;
    private Button btnReadDeviceInfoFirst;
    private Button btnSelectToolsFirst;
    private Button btnGetToken;
    private Button btnReadDeviceInfoSecond;
    private Button btnFinish;
    private EditText mEdtManufId;
    private EditText mEdtToolId;
    private LinearLayout llWriteContainer;
    private DeviceInfo mDeviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvQrResult = findViewById(R.id.tv_main_qr_result);
        tvDeviceInfoFirstResult = findViewById(R.id.tv_main_device_info_first_result);
        tvDeviceInfoSecondResult = findViewById(R.id.tv_main_device_info_second_result);
        tvSelectToolsResult = findViewById(R.id.tv_main_select_tools_result);
        tvTokenResult = findViewById(R.id.tv_main_token_result);
        tvWriteResult = findViewById(R.id.tv_main_write_result);
        btnReadDeviceInfoFirst = findViewById(R.id.btn_main_read_device_info_first);
        btnReadDeviceInfoSecond = findViewById(R.id.btn_main_read_device_info_second);
        btnFinish = findViewById(R.id.btn_main_finish);
        btnSelectToolsFirst = findViewById(R.id.btn_main_select_tools);
        btnGetToken = findViewById(R.id.btn_main_get_token);
        llWriteContainer = findViewById(R.id.ll_main_write_container);
        mEdtManufId = findViewById(R.id.edt_main_manuf_id);
        mEdtToolId = findViewById(R.id.edt_main_tool_id);
        findViewById(R.id.btn_main_scan_qr).setOnClickListener(this);
        findViewById(R.id.btn_main_write_token).setOnClickListener(this);
        findViewById(R.id.btn_main_write_sn).setOnClickListener(this);
        findViewById(R.id.btn_main_write_mac).setOnClickListener(this);
        findViewById(R.id.btn_main_write_manuf_id).setOnClickListener(this);
        findViewById(R.id.btn_main_write_time).setOnClickListener(this);
        findViewById(R.id.btn_main_write_tool_id).setOnClickListener(this);
        btnSelectToolsFirst.setOnClickListener(this);
        btnReadDeviceInfoFirst.setOnClickListener(this);
        btnGetToken.setOnClickListener(this);
        btnReadDeviceInfoSecond.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_main_scan_qr:
                scanQr();
                break;
            case R.id.btn_main_select_tools:
                selectTools();
                break;
            case R.id.btn_main_read_device_info_first:
                readDeviceInfo(1);
                break;
            case R.id.btn_main_get_token:
                uploadDevice(1);
                break;
            case R.id.btn_main_write_token:
                writeToDevice("PrdTorken " + mToken, OPT_WRITE_TOKEN);
                break;
            case R.id.btn_main_write_mac:
                writeToDevice("PrdMac " + new BigInteger(mDevMac, 16), OPT_WRITE_MAC);
//                writeToDevice("PrdMac " + "1234567866666666", OPT_WRITE_MAC);
                break;
            case R.id.btn_main_write_sn:
                writeToDevice("PrdSn " + Long.valueOf(mSn), OPT_WRITE_SN);
                break;
            case R.id.btn_main_write_time:
                long timeStamp = System.currentTimeMillis() / 1000;
                writeToDevice("PrdTime " + timeStamp, OPT_WRITE_TIME);
                break;
            case R.id.btn_main_write_manuf_id:
                String manufId = mEdtManufId.getEditableText().toString();
                if (TextUtils.isEmpty(manufId)) {
                    showToast("请输入Manuf Id");
                    return;
                }
                writeToDevice("PrdManuf " + manufId, OPT_WRITE_MANUF_ID);
                break;
            case R.id.btn_main_write_tool_id:
                String toolId = mEdtToolId.getEditableText().toString();
                if (TextUtils.isEmpty(toolId)) {
                    showToast("请输入Tool Id");
                    return;
                }
                writeToDevice("PrdTool " + toolId, OPT_WRITE_TOOL_ID);
                break;
            case R.id.btn_main_read_device_info_second:
                readDeviceInfo(2);
                break;
            case R.id.btn_main_finish:
                uploadDevice(2);
                break;
        }
    }

    private void scanQr() {
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        } else {
            Intent intent = new Intent(this, CaptureActivity.class);
            startActivityForResult(intent, REQ_QR_CODE);
        }
    }

    private void selectTools() {
        startActivityForResult(new Intent(this, SearchDeviceActivity.class), REQ_SEARCH_DEVICE);
    }

    private void writeToDevice(String cmd, int opt) {
        mDialogHelper.showProgressDialog();
        new BluetoothUtil(mToolsMac)
                .sendCommand(cmd, new BluetoothUtil.SendCmdCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        mDialogHelper.dismissProgressDialog();
                        switch (opt) {
                            case OPT_WRITE_TOKEN:
                                tvWriteResult.append("\nWrite Token:" + result);
                                break;
                            case OPT_WRITE_MAC:
                                tvWriteResult.append("\nWrite MAC:" + result);
                                break;
                            case OPT_WRITE_SN:
                                tvWriteResult.append("\nWrite SN:" + result);
                                break;
                            case OPT_WRITE_TIME:
                                tvWriteResult.append("\nWrite Time:" + result);
                                break;
                            case OPT_WRITE_MANUF_ID:
                                tvWriteResult.append("\nWrite Manuf Id:" + result);
                                break;
                            case OPT_WRITE_TOOL_ID:
                                tvWriteResult.append("\nWrite Tool Id:" + result);
                                break;
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        mDialogHelper.dismissProgressDialog();
                        switch (opt) {
                            case OPT_WRITE_TOKEN:
                                tvWriteResult.append("\nWrite Token:" + message);
                                break;
                            case OPT_WRITE_MAC:
                                tvWriteResult.append("\nWrite MAC:" + message);
                                break;
                            case OPT_WRITE_SN:
                                tvWriteResult.append("\nWrite SN:" + message);
                                break;
                            case OPT_WRITE_TIME:
                                tvWriteResult.append("\nWrite Time:" + message);
                                break;
                            case OPT_WRITE_MANUF_ID:
                                tvWriteResult.append("\nWrite Manuf Id:" + message);
                                break;
                            case OPT_WRITE_TOOL_ID:
                                tvWriteResult.append("\nWrite Tool Id:" + message);
                                break;
                        }
                    }
                });
    }

    private void readDeviceInfo(int index) {
        mDialogHelper.showProgressDialog();
        new BluetoothUtil(mToolsMac)
        .sendCommand("?Info", new BluetoothUtil.SendCmdCallBack() {
            @Override
            public void onSuccess(String result) {
                mDialogHelper.dismissProgressDialog();
                if (index == 1) {
                    tvDeviceInfoFirstResult.setVisibility(View.VISIBLE);
                    btnGetToken.setVisibility(View.VISIBLE);
                    tvDeviceInfoFirstResult.setText(String.format(getString(R.string.device_info_second_result_pre), result));
                } else {
                    tvDeviceInfoSecondResult.setVisibility(View.VISIBLE);
                    btnFinish.setVisibility(View.VISIBLE);
                    tvDeviceInfoSecondResult.setText(String.format(getString(R.string.device_info_second_result_pre), result));
                }
                try {
                    mDeviceInfo = DeviceUtil.convertDeviceInfo(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    tvDeviceInfoSecondResult.setText(String.format(getString(R.string.device_info_second_result_pre), "数据格式异常"));
                }
            }

            @Override
            public void onFailure(String message) {
                mDialogHelper.dismissProgressDialog();
                if (index == 1) {
                    tvDeviceInfoFirstResult.setVisibility(View.VISIBLE);
                    btnGetToken.setVisibility(View.VISIBLE);
                    tvDeviceInfoFirstResult.setText(String.format(getString(R.string.device_info_first_result_pre), message));
                } else {
                    tvDeviceInfoSecondResult.setVisibility(View.VISIBLE);
                    btnFinish.setVisibility(View.VISIBLE);
                    tvDeviceInfoSecondResult.setText(String.format(getString(R.string.device_info_second_result_pre), message));
                }
            }
        });
    }

    /**
     * 上传设备信息
     */
    private void uploadDevice(int index) {
        if (index == 1) {
            mDeviceInfo.setSn(mSn);
            mDeviceInfo.setMac(mDevMac);
        }
        mDialogHelper.showProgressDialog();
        Observable<TResponse<UploadDeviceInfoResponse>> observable
                = ServiceFactory.getInstance().getApiService().uploadDeviceInfo(
                mDeviceInfo.getUuid(),
                mDeviceInfo.getMac(),
                mDeviceInfo.getSn(),
                mDeviceInfo.getModel(),
                mDeviceInfo.getHwVer(),
                mDeviceInfo.getFwVer(),
                mDeviceInfo.getManufId() + "",
                mDeviceInfo.getToolId(),
                mDeviceInfo.getTime(),
                null);
        RxHelper.getInstance().sendRequest(TAG, observable, response -> {
            mDialogHelper.dismissProgressDialog();
            if (index == 1) {
                llWriteContainer.setVisibility(View.VISIBLE);
                tvTokenResult.setVisibility(View.VISIBLE);
                if (response.isSuccess()) {
                    mToken = response.data.getToken();
                    tvTokenResult.setText(String.format(getString(R.string.token_result_pre), mToken));

                } else {
                    tvTokenResult.setText(String.format(getString(R.string.token_result_pre),
                            response.message));
                }
            } else {
                if (response.isSuccess()) {
                    showToast("保存成功");
                    recoveryView();
                } else {
                    showToast(response.message);
                }
            }
        }, throwable -> {
            mDialogHelper.dismissProgressDialog();
            if (index == 1) {
                tvTokenResult.setVisibility(View.VISIBLE);
                tvTokenResult.setText(String.format(
                        getString(R.string.token_result_pre),
                        RxHelper.getInstance().getErrorInfo(throwable)));
            } else {
                showToast(RxHelper.getInstance().getErrorInfo(throwable));
            }
        });
    }

    private void recoveryView() {
        tvDeviceInfoSecondResult.setText("");
        tvDeviceInfoFirstResult.setText("");
        tvTokenResult.setText("");
        tvSelectToolsResult.setText("");
        tvQrResult.setText("");
        tvWriteResult.setText("Write Result -> ");
        tvDeviceInfoSecondResult.setVisibility(View.GONE);
        tvDeviceInfoFirstResult.setVisibility(View.GONE);
        btnReadDeviceInfoFirst.setVisibility(View.GONE);
        btnGetToken.setVisibility(View.GONE);
        tvTokenResult.setVisibility(View.GONE);
        llWriteContainer.setVisibility(View.GONE);
        mEdtToolId.setText("");
        mEdtManufId.setText("");
        btnFinish.setVisibility(View.GONE);
        btnSelectToolsFirst.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫码
        if (resultCode == CaptureActivity.RESULT_CODE_QR_SCAN) {
            String result = data.getStringExtra(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);
            CommLog.logE("qr_result:" + result);
            if (!result.contains("SN:") || !result.contains("MAC:") || !result.contains(",") || result.length() != 34) {
                showToast("二维码格式错误");
                return;
            }
            tvQrResult.setVisibility(View.VISIBLE);
            btnSelectToolsFirst.setVisibility(View.VISIBLE);
            tvQrResult.setText(String.format(getString(R.string.qr_result_pre), result));
            mSn = result.substring(3, 13);
            mDevMac = result.substring(18, 34);
        } else if (requestCode == REQ_SEARCH_DEVICE && data != null) {
            SearchDeviceBean device = data.getParcelableExtra(AppConstants.EXTRA_DEVICE);
            mToolsMac = device.device.getAddress();
            tvSelectToolsResult.setVisibility(View.VISIBLE);
            btnReadDeviceInfoFirst.setVisibility(View.VISIBLE);
            tvSelectToolsResult.setText(String.format(getString(R.string.select_tools_result_pre), device.device.getName()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (isAllGranted) {
                Intent intent = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent, REQ_QR_CODE);
            } else {
                showToast("权限被拒绝");
            }
        }
    }
}
