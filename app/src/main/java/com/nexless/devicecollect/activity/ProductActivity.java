package com.nexless.devicecollect.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.calm.comm.lib.qr.zxing.activity.CaptureActivity;
import com.nexless.ccommble.conn.ConnectionHelper;
import com.nexless.ccommble.util.CommHandler;
import com.nexless.ccommble.util.CommLog;
import com.nexless.devicecollect.AppConstants;
import com.nexless.devicecollect.R;
import com.nexless.devicecollect.httpservice.RxHelper;
import com.nexless.devicecollect.httpservice.ServiceFactory;
import com.nexless.devicecollect.model.DeviceInfo;
import com.nexless.devicecollect.model.ManufInfo;
import com.nexless.devicecollect.model.SearchDeviceBean;
import com.nexless.devicecollect.model.TResponse;
import com.nexless.devicecollect.model.UploadDeviceInfoResponse;
import com.nexless.devicecollect.util.BluetoothUtil;
import com.nexless.devicecollect.util.DeviceTypeUtil;
import com.nexless.devicecollect.util.DeviceUtil;
import com.nexless.devicecollect.util.Function;
import com.nexless.devicecollect.view.AppTitleBar;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @date: 2019/6/11
 * @author: su qinglin
 * @description:
 */
public class ProductActivity extends BaseActivity implements View.OnClickListener, CommHandler
        .MessageHandler {

    private static final String TAG = ProductActivity.class.getSimpleName();
    private static final int ACTION_REQUEST_PERMISSIONS = 0x0001;
    private static final int REQ_SEARCH_DEVICE = 0x0002;
    private static final int REQ_QR_CODE = 0x1001;
    private static final int OPT_READ_DEVICE_INFO_FIRST = 0x2000;
    private static final int OPT_READ_VERSION = 0x2001;
    private static final int OPT_SET_BR = 0x2002;
    private static final int OPT_WRITE_TOKEN = 0x3001;
    private static final int OPT_WRITE_MAC = 0x3002;
    private static final int OPT_WRITE_SN = 0x3003;
    private static final int OPT_WRITE_TIME = 0x3004;
    private static final int OPT_WRITE_MANUF_ID = 0x3005;
    private static final int OPT_WRITE_TOOL_ID = 0x3006;
    private static final int OPT_WRITE_ERROR_TOKEN = 0x3007;
    private static final int OPT_READ_DEVICE_INFO = 0x4001;
    private static final int MSG_READ_DEVICE_INFO = 0x5001;
    private SearchDeviceBean mSearchDevice;
    private ManufInfo manufInfo;
    private int mToolId;
    private TextView tvMessage;
    private TextView tvQr;
    private TextView tvToolName;
    private TextView tvDevice;
    private String mSn;
    private String mDevMac;
    private String mRemark;
    private DeviceInfo mDeviceInfo;
    private String mToken;
    private String mModel;
    private CommHandler mHandle = new CommHandler(this);
    private boolean isReaddingInfo = false;
    private boolean isSelectTool;
    private boolean mHasPrdAck;
    private Disposable timerDelayTimeout;
    private long mTimeStamp;

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_READ_DEVICE_INFO:
                if (!isReaddingInfo) {
                    isReaddingInfo = true;
//                    readDeviceInfo(msg.arg1);
                    sendCmdToDeviceByFunPos(msg.arg1, OPT_READ_DEVICE_INFO);
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Intent intent = getIntent();
        mSearchDevice = intent.getParcelableExtra(AppConstants.EXTRA_DEVICE);
        manufInfo = intent.getParcelableExtra(AppConstants.EXTRA_MANUF);
        mToolId = intent.getIntExtra(AppConstants.EXTRA_TOOL_ID, 0);
        isSelectTool = intent.getBooleanExtra(AppConstants.EXTRA_IS_SELECT_TOOL, false);

        AppTitleBar titleBar = findViewById(R.id.titlebar);
        titleBar.setRightListener("TEST", this);
        tvDevice = findViewById(R.id.tv_product_device);
        tvToolName = findViewById(R.id.tv_product_tool_name);
        TextView tvManufId = findViewById(R.id.tv_product_manuf_id);
        tvMessage = findViewById(R.id.tv_product_msg);
        tvQr = findViewById(R.id.tv_product_qr);
        LinearLayout llDevice = findViewById(R.id.ll_product_device);
        findViewById(R.id.ll_product_scan_qr).setOnClickListener(this);
//        findViewById(R.id.ll_product_select_tool).setOnClickListener(this);
        findViewById(R.id.btn_product_start).setOnClickListener(this);
        findViewById(R.id.btn_product_finish).setOnClickListener(this);
        llDevice.setOnClickListener(this);

        if (isSelectTool) {
            llDevice.setVisibility(View.GONE);
            tvToolName.setVisibility(View.VISIBLE);
            tvToolName.setText(String.format(getString(R.string.tool_pre), mSearchDevice
                    .bluetoothName + "(" + mToolId + ")"));
        } else {
            llDevice.setVisibility(View.VISIBLE);
            tvToolName.setVisibility(View.GONE);
        }
        tvManufId.setText(String.format(getString(R.string.manuf_pre), manufInfo.getName() + "("
                + manufInfo.getId() + ")"));
        tvMessage.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.apptitlebar_ll_right:
                if (mSearchDevice != null) {
                    Intent testIntent = new Intent(this, TestActivity.class);
                    testIntent.putExtra(AppConstants.EXTRA_DEVICE, mSearchDevice);
                    startActivity(testIntent);
                } else {
                    showToast("请选择设备");
                }
                break;
            case R.id.ll_product_scan_qr:
                if (!checkPermissions(NEEDED_PERMISSIONS)) {
                    ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS,
                            ACTION_REQUEST_PERMISSIONS);
                } else {
                    Intent intent = new Intent(this, CaptureActivity.class);
                    startActivityForResult(intent, REQ_QR_CODE);
                }
                break;
            case R.id.ll_product_device:
                startActivityForResult(new Intent(this, SearchDeviceActivity.class),
                        REQ_SEARCH_DEVICE);
                break;
            case R.id.btn_product_start:
                if (mDevMac == null) {
                    showToast("请扫描二维码");
                    return;
                }
                if (mSearchDevice == null) {
                    showToast("请选择设备");
                    return;
                }
                mDialogHelper.showProgressDialog();
                isReaddingInfo = false;
                getDeviceToken();
                break;
            case R.id.btn_product_finish:
                ConnectionHelper.getInstance().disConnDevice(mSearchDevice.device.getAddress());
                clearView();
                break;
        }
    }

    private void clearView() {
        tvQr.setText("");
        tvMessage.setText("");
        mSn = null;
        mToken = null;
        mDevMac = null;
        mDeviceInfo = null;
        mTimeStamp = 0;
    }

    @SuppressLint("SetTextI18n")
    private void getDeviceToken() {
        Observable<TResponse<UploadDeviceInfoResponse>> observable
                = ServiceFactory.getInstance().getApiService().getDeviceToken(mDevMac);
        RxHelper.getInstance().sendRequest(TAG, observable, response -> {
            if (response.isSuccess()) {
                mToken = response.data.getToken();
                mModel = response.data.getModel();
                tvMessage.setText(String.format(getString(R.string.token_result_pre), mModel + "," + Long.valueOf(mToken, 16)) +
                        "\n");
                if (DeviceTypeUtil.isGateway(mRemark, ProductActivity.this)) {
                    sendCmdToDevice("PrdSetBr 57600", 0, true, OPT_SET_BR);
                } else {
                    sendCmdToDevice("?PrdVer", 0, false, OPT_READ_VERSION);
//                    sendCmdToDevice("?Info", 0, false, OPT_READ_DEVICE_INFO_FIRST);
                }
            } else {
                mDialogHelper.dismissProgressDialog();
                tvMessage.setText(String.format(getString(R.string.token_result_pre),
                        response.message) + "\n");
            }
        }, throwable -> {
            mDialogHelper.dismissProgressDialog();
            tvMessage.setText(String.format(
                    getString(R.string.token_result_pre),
                    RxHelper.getInstance().getErrorInfo(throwable)) + "\n");
        });
    }

    /**
     * 上传设备信息
     */
    private void uploadDevice() {
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
            if (response.isSuccess()) {
                tvMessage.append(String.format(getString(R.string.upload_result_pre),
                        "SUCCESS") + "\n");
            } else {
                tvMessage.append(String.format(getString(R.string.upload_result_pre),
                        response.message) + "\n");
            }
        }, throwable -> {
            mDialogHelper.dismissProgressDialog();
            tvMessage.append(String.format(
                    getString(R.string.upload_result_pre),
                    RxHelper.getInstance().getErrorInfo(throwable)) + "\n");
        });
    }

    private boolean checkResult(DeviceInfo deviceInfo, int version) {
        // 如果支持UUID功能位，则判断读取UUID是否与写入一致
        if (DeviceUtil.supportFun(version, Function.SUPPORT_UUID)) {
            if (TextUtils.isEmpty(deviceInfo.getUuid())) {
                tvMessage.append("UUID无效\n");
                return false;
            }
        } else {
            mDeviceInfo.setUuid("FFFFFFFFFFFFFFFF");
        }
        // 如果支持MAC功能位，则判断读取MAC是否与写入一致
        if (DeviceUtil.supportFun(version, Function.SUPPORT_MAC)) {
            if (!mDevMac.equals(deviceInfo.getMac())) {
                tvMessage.append("MAC写入失败\n");
                return false;
            }
        } else if ("0".equals(mDeviceInfo.getMac())) {
            mDeviceInfo.setMac("FFFFFFFFFFFFFFFF");
        }
        // 如果支持SN功能位，则判断读取SN是否与写入一致
        if (DeviceUtil.supportFun(version, Function.SUPPORT_SN)) {
            if (!mSn.equals(String.format("%010d", Integer.valueOf(deviceInfo.getSn())))) {
                tvMessage.append("SN写入失败\n");
                return false;
            }
        } else if ("0".equals(mSn)){
            mDeviceInfo.setSn(String.valueOf(Long.valueOf("FFFFFFFF", 16)));
        }
        // 如果支持TIME功能位，则判断读取TIME是否与写入一致
        if (DeviceUtil.supportFun(version, Function.SUPPORT_TIME)) {
            if (mTimeStamp != deviceInfo.getTime()) {
                tvMessage.append("Time写入失败\n");
                return false;
            }
        } else if (mDeviceInfo.getTime() == 0) {
            mDeviceInfo.setTime(Long.valueOf("FFFFFFFF", 16));
        }
        if (!DeviceUtil.supportFun(version, Function.SUPPORT_HW_VER)) {
            mDeviceInfo.setHwVer(DeviceUtil.convertVersion556(Integer.valueOf("0800", 16)));
        }
        if (!DeviceUtil.supportFun(version, Function.SUPPORT_FW_VER)) {
            mDeviceInfo.setFwVer(DeviceUtil.convertVersion556(Integer.valueOf("0800", 16)));
        }
        // 如果支持MANUF功能位，则判断读取MANUF是否与写入一致
        if (DeviceUtil.supportFun(version, Function.SUPPORT_MANUF)) {
            if (manufInfo.getId() != deviceInfo.getManufId()) {
                tvMessage.append("Manuf Id写入失败\n");
                return false;
            }
        } else if (manufInfo.getId() == 0){
            mDeviceInfo.setManufId(Integer.valueOf("FFFF", 16));
        }
        // 如果支持TOOL功能位，则判断读取TOOL是否与写入一致
        if (DeviceUtil.supportFun(version, Function.SUPPORT_TOOL)) {
            if (isSelectTool && mToolId != deviceInfo.getToolId()) {
                tvMessage.append("Tool Id写入失败\n");
                return false;
            }
        } else if (mToolId == 0){
            mDeviceInfo.setToolId(Integer.valueOf("FFFF", 16));
        }
        return true;
    }

    private void sendCmdToDeviceByFunPos(int ver, int opt) {
        if (opt <= OPT_READ_DEVICE_INFO_FIRST && DeviceUtil.supportFun(ver, Function.SUPPORT_INFO)) {
            sendCmdToDevice("?Info", ver, mHasPrdAck, OPT_READ_DEVICE_INFO_FIRST);
        } else if (opt <= OPT_WRITE_TOKEN && DeviceUtil.supportFun(ver, Function.SUPPORT_TORKEN)) {
            sendCmdToDevice("PrdTorken " + Long.valueOf(mToken, 16), ver,
                    mHasPrdAck, OPT_WRITE_TOKEN);
        } else if (opt <= OPT_WRITE_MAC && DeviceUtil.supportFun(ver, Function.SUPPORT_MAC)) {
            sendCmdToDevice("PrdMac " + new BigInteger(mDevMac, 16), ver,
                    mHasPrdAck, OPT_WRITE_MAC);
        } else if (opt <= OPT_WRITE_SN && DeviceUtil.supportFun(ver, Function.SUPPORT_SN)) {
            sendCmdToDevice("PrdSn " + Long.valueOf(mSn), ver, mHasPrdAck,
                    OPT_WRITE_SN);
        } else if (opt <= OPT_WRITE_TIME && DeviceUtil.supportFun(ver, Function.SUPPORT_TIME)) {
            mTimeStamp = System.currentTimeMillis() / 1000;
            sendCmdToDevice("PrdTime " + mTimeStamp, ver, mHasPrdAck,
                    OPT_WRITE_TIME);
        } else if (opt <= OPT_WRITE_MANUF_ID && DeviceUtil.supportFun(ver, Function.SUPPORT_MANUF)) {
            sendCmdToDevice("PrdManuf " + manufInfo.getId(), ver, mHasPrdAck,
                    OPT_WRITE_MANUF_ID);
        } else if (opt <= OPT_WRITE_TOOL_ID && DeviceUtil.supportFun(ver, Function.SUPPORT_TOOL)) {
            sendCmdToDevice("PrdTool " + mToolId, ver, mHasPrdAck,
                    OPT_WRITE_TOOL_ID);
        } else if (opt <= OPT_READ_DEVICE_INFO) {
            if (DeviceUtil.supportFun(ver, Function.SUPPORT_INFO)) {
                sendCmdToDevice("?Info", ver, mHasPrdAck,
                        OPT_READ_DEVICE_INFO);
            } else {
                mDeviceInfo = new DeviceInfo();
                mDeviceInfo.setSn(mSn);
                mDeviceInfo.setMac(mDevMac);
                if (checkResult(mDeviceInfo, ver)) {
                    uploadDevice();
                }
            }
        }
    }

    private void sendCmdToDevice(String cmd, int ver, boolean hasPrdAck, int opt) {
        try {
            new BluetoothUtil(mSearchDevice.device.getAddress())
                    .sendCommand(cmd, hasPrdAck, new BluetoothUtil.SendCmdCallBack() {
                        @Override
                        public void onSuccess(String result) {
                            result = result.substring(0, result.lastIndexOf("\r\n"));
                            switch (opt) {
                                case OPT_SET_BR:
                                    tvMessage.append("Set Br -> " + result + "\n");
                                    if ("OK".equals(result)) {
                                        timerDelayTimeout = Observable.timer(1000, TimeUnit
                                                .MILLISECONDS)

                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(aLong -> {
                                                    if (timerDelayTimeout != null &&
                                                            !timerDelayTimeout.isDisposed()) {

                                                        timerDelayTimeout.dispose();
                                                        timerDelayTimeout = null;
                                                    }
                                                    sendCmdToDevice("?PrdVer", 0, true, OPT_READ_VERSION);
//                                                    sendCmdToDevice("?Info", 0, true, OPT_READ_DEVICE_INFO_FIRST);
                                                });
                                    } else {
                                        mDialogHelper.dismissProgressDialog();
                                    }
                                    break;
                                case OPT_READ_VERSION:
                                    if (result.contains("Bad Command")) {
                                        tvMessage.append("Read Version -> 1\n");
                                        mHasPrdAck = false;
                                        sendCmdToDeviceByFunPos(Integer.valueOf("07FF", 16), OPT_READ_DEVICE_INFO_FIRST);
                                    } else if (result.contains("Ver:") && result.split
                                            ("Ver:").length >= 2) {
                                        String verStr = result.split("Ver:")[1];
                                        tvMessage.append("Read Version -> " + verStr + "\n");
                                        int version = Integer.valueOf(verStr, 16);
                                        mHasPrdAck = true;
                                        if (DeviceUtil.supportFun(version, Function.SUPPORT_FUN)) {
                                            sendCmdToDeviceByFunPos(version, OPT_READ_DEVICE_INFO_FIRST);
                                        } else if (version == 2) {
                                            sendCmdToDeviceByFunPos(Integer.valueOf("07FF", 16), OPT_READ_DEVICE_INFO_FIRST);
                                        } else if (version == 1) {
                                            sendCmdToDeviceByFunPos(Integer.valueOf("0014", 16), OPT_READ_DEVICE_INFO_FIRST);
//                                            sendCmdToDevice("PrdMac " + new BigInteger(mDevMac, 16),
//                                                    Integer.valueOf("0014", 16), mHasPrdAck, OPT_WRITE_MAC);
                                        }
                                    } else {
                                        mDialogHelper.dismissProgressDialog();
                                        tvMessage.append("Read Version -> " + result + "\n");
                                    }
                                    break;
                                case OPT_READ_DEVICE_INFO_FIRST:
                                    tvMessage.append("Read Info -> " + result + "\n");
                                    try {
                                        mDeviceInfo = DeviceUtil.convertDeviceInfo(result);
                                        if (mDeviceInfo != null) {
                                            if (mModel.equals(mDeviceInfo.getModel())) {
                                                sendCmdToDeviceByFunPos(ver, OPT_WRITE_TOKEN);
                                            } else {
                                                tvMessage.append("Read Info -> 二维码上的Model与设备中不一致！\n");
                                                mDialogHelper.dismissProgressDialog();
                                            }
                                        } else {
                                            mDialogHelper.dismissProgressDialog();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        mDialogHelper.dismissProgressDialog();
                                        tvMessage.append("Read Info -> 数据格式异常\n");
                                    }
                                    break;
                                case OPT_WRITE_TOKEN:
                                    tvMessage.append("Write Token -> " + result + "\n");
                                    if ("OK".equals(result)) {
                                        sendCmdToDeviceByFunPos(ver, OPT_WRITE_MAC);
                                    } else {
                                        mDialogHelper.dismissProgressDialog();
                                        tvMessage.append("Write Token -> 请重试\n");
                                    }
                                    break;
                                case OPT_WRITE_MAC:
                                    tvMessage.append("Write MAC -> " + result + "\n");
                                    if ("OK".equals(result)) {
                                        sendCmdToDeviceByFunPos(ver, OPT_WRITE_SN);
                                    } else {
                                        mDialogHelper.dismissProgressDialog();
                                    }
                                    break;
                                case OPT_WRITE_SN:
                                    tvMessage.append("Write SN -> " + result + "\n");
                                    if ("OK".equals(result)) {
                                        mTimeStamp = System.currentTimeMillis() / 1000;
                                        sendCmdToDeviceByFunPos(ver, OPT_WRITE_TIME);
                                    } else {
                                        mDialogHelper.dismissProgressDialog();
                                    }
                                    break;
                                case OPT_WRITE_TIME:
                                    tvMessage.append("Write Time -> " + result + "\n");
                                    if ("OK".equals(result)) {
                                        sendCmdToDeviceByFunPos(ver, OPT_WRITE_MANUF_ID);
                                    } else {
                                        mDialogHelper.dismissProgressDialog();
                                    }
                                    break;
                                case OPT_WRITE_MANUF_ID:
                                    tvMessage.append("Write Manuf Id -> " + result + "\n");
                                    if ("OK".equals(result)) {
                                        if (isSelectTool) {
                                            sendCmdToDeviceByFunPos(ver, OPT_WRITE_TOOL_ID);
                                        } else {
                                            Message message = Message.obtain();
                                            message.what = MSG_READ_DEVICE_INFO;
                                            message.arg1 = ver;
                                            mHandle.sendMessageDelayed(message, 500);
                                        }
                                    } else {
                                        mDialogHelper.dismissProgressDialog();
                                    }
                                    break;
                                case OPT_WRITE_TOOL_ID:
                                    tvMessage.append("Write Tool Id -> " + result + "\n");
                                    if ("OK".equals(result)) {
                                        Message message = Message.obtain();
                                        message.what = MSG_READ_DEVICE_INFO;
                                        message.arg1 = ver;
                                        mHandle.sendMessageDelayed(message, 500);
                                    } else {
                                        mDialogHelper.dismissProgressDialog();
                                    }
                                    break;
                                case OPT_WRITE_ERROR_TOKEN:
                                    tvMessage.append("Write Error Token -> " + result + "\n");
                                    ConnectionHelper.getInstance().disConnDevice(mSearchDevice.device
                                            .getAddress());
                                    uploadDevice();
                                    break;
                                case OPT_READ_DEVICE_INFO:
                                    tvMessage.append(String.format(getString(R.string
                                            .device_info_second_result_pre), result) + "\n");
                                    try {
                                        mDeviceInfo = DeviceUtil.convertDeviceInfo(result);
                                        if (mDeviceInfo != null && checkResult(mDeviceInfo, ver)) {
                                            sendCmdToDevice("PrdTorken " + (Long.valueOf(mToken, 16) + 1),
                                                    0, mHasPrdAck, OPT_WRITE_ERROR_TOKEN);
                                        } else {
                                            mDialogHelper.dismissProgressDialog();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        mDialogHelper.dismissProgressDialog();
                                        tvMessage.append(String.format(getString(R.string
                                                .device_info_second_result_pre), "数据格式异常\n"));
                                    }
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(String message) {
                            mDialogHelper.dismissProgressDialog();
                            switch (opt) {
                                case OPT_SET_BR:
                                    tvMessage.append("Set Br -> " + message + "\n");
                                    break;
                                case OPT_READ_VERSION:
                                    tvMessage.append("Read Version -> " + message + "\n");
                                    break;
                                case OPT_WRITE_TOKEN:
                                    tvMessage.append("Write Token -> " + message + "\n");
                                    break;
                                case OPT_WRITE_MAC:
                                    tvMessage.append("Write MAC -> " + message + "\n");
                                    break;
                                case OPT_WRITE_SN:
                                    tvMessage.append("Write SN -> " + message + "\n");
                                    break;
                                case OPT_WRITE_TIME:
                                    tvMessage.append("Write Time -> " + message + "\n");
                                    break;
                                case OPT_WRITE_MANUF_ID:
                                    tvMessage.append("Write Manuf Id -> " + message + "\n");
                                    break;
                                case OPT_WRITE_TOOL_ID:
                                    tvMessage.append("Write Tool Id -> " + message + "\n");
                                    break;
                                case OPT_WRITE_ERROR_TOKEN:
                                    tvMessage.append("Write Error Token -> " + message + "\n");
                                    break;
                                case OPT_READ_DEVICE_INFO:
                                    tvMessage.append(String.format(getString(R.string
                                            .device_info_second_result_pre), message + "\n"));
                                    break;
                            }
                        }
                    });
        } catch (NumberFormatException e) {
            e.printStackTrace();
            mDialogHelper.dismissProgressDialog();
            tvMessage.append("数据转换异常\n");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == CaptureActivity.RESULT_CODE_QR_SCAN) {
                String result = data.getStringExtra(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);
                CommLog.logE("qr_result:" + result);
                if (TextUtils.isEmpty(result)
                        || !result.contains("#")
                        || result.split("#").length < 3) {
                    showToast("二维码格式错误");
                    return;
                }
                result = result.replace("http://downapp.xeiot.com/sn/", "");
                tvQr.setText(result);
                String[] resultArr = result.split("#");
                mSn = resultArr[2];
                mDevMac = resultArr[1];
                mRemark = resultArr[0];
            } else if (requestCode == REQ_SEARCH_DEVICE) {
                mSearchDevice = data.getParcelableExtra(AppConstants.EXTRA_DEVICE);
                if (isSelectTool) {
                    tvToolName.setText(String.format(getString(R.string.tool_pre), mSearchDevice
                            .bluetoothName + "(" + mToolId + ")"));
                } else {
                    tvDevice.setText(mSearchDevice.bluetoothName);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchDevice != null) {
            ConnectionHelper.getInstance().disConnDevice(mSearchDevice.device.getAddress());
        }
        if (timerDelayTimeout != null && !timerDelayTimeout.isDisposed()) {
            timerDelayTimeout.dispose();
            timerDelayTimeout = null;
        }
    }
}
