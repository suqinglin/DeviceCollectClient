package com.nexless.devicecollect.activity;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.nexless.ccommble.conn.ConnectionHelper;
import com.nexless.ccommble.scan.NexlessBluetoothScanner;
import com.nexless.devicecollect.AppConstants;
import com.nexless.devicecollect.R;
import com.nexless.devicecollect.adapter.KeyboardAdapter;
import com.nexless.devicecollect.model.ManufInfo;
import com.nexless.devicecollect.model.SearchDeviceBean;
import com.nexless.devicecollect.util.BluetoothUtil;
import com.nexless.devicecollect.view.AppTitleBar;
import com.nexless.devicecollect.view.DialogHelper;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LockTestActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 所需的所有权限信息
     */
    public static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int OPT_ADD_LOCK = 0x1001;

    private static final int ACTION_REQUEST_PERMISSIONS = 0x0001;
    private KeyboardAdapter mKeyboardAdapter;
    private NexlessBluetoothScanner mBluetoothScanner;
    private Button btnAddLock, btnOpenLock, btnAddFinger, btnTouchTest, btnSleep, btnDeleteLock, btnClearLogs, btnLoraTest, btnBurn;
    private TextView tvBattery, tvCardNum, tvLogs, tvTime;
    private BluetoothDevice mDevice;
    private Disposable searchTimeout;
    private Disposable operateTime;
    private SearchDeviceBean mSearchDevice;
    private ManufInfo manufInfo;
    private boolean isMyLock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_test);

        manufInfo = getIntent().getParcelableExtra(AppConstants.EXTRA_MANUF);

        AppTitleBar titleBar = findViewById(R.id.titlebar);
        btnAddLock = findViewById(R.id.btn_add_lock);
        btnOpenLock = findViewById(R.id.btn_open_lock);
        btnAddFinger = findViewById(R.id.btn_add_finger);
        btnTouchTest = findViewById(R.id.btn_touch_test);
        btnSleep = findViewById(R.id.btn_sleep);
        btnDeleteLock = findViewById(R.id.btn_next);
        btnClearLogs = findViewById(R.id.btn_clear_log);
        btnLoraTest = findViewById(R.id.btn_lora_test);
        btnBurn = findViewById(R.id.btn_burn);
        tvBattery = findViewById(R.id.tv_battery);
        tvCardNum = findViewById(R.id.tv_card_num);
        tvLogs = findViewById(R.id.tv_logs);
        tvTime = findViewById(R.id.tv_time);

        btnAddLock.setOnClickListener(this);
        btnOpenLock.setOnClickListener(this);
        btnAddFinger.setOnClickListener(this);
        btnTouchTest.setOnClickListener(this);
        btnSleep.setOnClickListener(this);
        btnDeleteLock.setOnClickListener(this);
        btnClearLogs.setOnClickListener(this);
        btnLoraTest.setOnClickListener(this);
        btnBurn.setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);

//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setCustomView();
//        }
        GridView gridView = findViewById(R.id.gv_keyboard);
        mKeyboardAdapter = new KeyboardAdapter(this);
        gridView.setAdapter(mKeyboardAdapter);
        tvLogs.setMovementMethod(new ScrollingMovementMethod());
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_lock:
                mDialogHelper.showProgressDialog();
                clear();
                tvLogs.setText("");
                tvBattery.setText("电量：");
                mSearchDevice = null;
                isMyLock = false;
                operateTime = Observable.interval(1000, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aLong -> tvTime.setText(aLong + "s"));
                mBluetoothScanner = NexlessBluetoothScanner.getIntance(this);
                mBluetoothScanner.startScan(mScannerCallback);
                searchTimeout = Observable.timer(10000, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aLong -> {
                            mBluetoothScanner.stopScan();
                            mDialogHelper.dismissProgressDialog();
                            showToast("搜索设备超时！");
                            if (searchTimeout != null && !searchTimeout.isDisposed()) {
                                searchTimeout.dispose();
                                searchTimeout = null;
                            }
                        });
                break;
            case R.id.btn_open_lock:
                mDialogHelper.showProgressDialog();
                sendCommand(AppConstants.CMD_REQ_DOOR, OPT_ADD_LOCK);
                break;
            case R.id.btn_add_finger:
                mDialogHelper.showProgressDialog();
                sendCommand(AppConstants.CMD_REQ_INS_FP, OPT_ADD_LOCK);
                break;
            case R.id.btn_touch_test:
                mDialogHelper.showProgressDialog();
                sendCommand(AppConstants.CMD_REQ_TOUCH, OPT_ADD_LOCK);
                break;
            case R.id.btn_sleep:
                mDialogHelper.showProgressDialog();
                sendCommand(AppConstants.CMD_REQ_SLEEP, OPT_ADD_LOCK);
                break;
            case R.id.btn_delete:
                mDialogHelper.showProgressDialog();
                sendCommand(AppConstants.CMD_REQ_DEL, OPT_ADD_LOCK);
                break;
            case R.id.btn_next:
                if (!isMyLock) {
                    showToast("请先完成添加指纹或触摸测试！");
                    return;
                }
//                mDialogHelper.showProgressDialog();
//                sendCommand(AppConstants.CMD_REQ_DEL, OPT_ADD_LOCK);
                Intent intent = new Intent(this, ProductActivity.class);
                intent.putExtra(AppConstants.EXTRA_DEVICE, mSearchDevice);
                intent.putExtra(AppConstants.EXTRA_MANUF, manufInfo);
                intent.putExtra(AppConstants.EXTRA_TOOL_ID, Integer.valueOf("1111"));
                intent.putExtra(AppConstants.EXTRA_IS_SELECT_TOOL, false);
                startActivityForResult(intent, 0);
                break;
            case R.id.btn_clear_log:
                tvLogs.setText("");
                break;
            case R.id.btn_lora_test:
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("当前操作耗时较长，是否确定需要继续？")
                        .setNegativeButton("取消", (dialog, which) -> dialog.cancel())
                        .setPositiveButton("确定", (dialog, which) -> {
                            dialog.cancel();
                            mDialogHelper.showProgressDialog();
                            sendCommand(AppConstants.CMD_REQ_FA, OPT_ADD_LOCK);
                        })
                        .show();
                break;
            case R.id.btn_burn:
                mDialogHelper.showProgressDialog();
                sendCommand(AppConstants.CMD_REQ_BURN, OPT_ADD_LOCK);
                break;
        }
    }

    private NexlessBluetoothScanner.NexlessScannerCallBack mScannerCallback = new NexlessBluetoothScanner.NexlessScannerCallBack() {
        @Override
        public void onScannerResultCallBack(BluetoothDevice device, int rssi, String uuid) {
            if (AppConstants.BLE_BROADCAST_UUID.equals(uuid)) {
                boolean isLoraDevice = device.getName().charAt(3) == 'L';
                btnLoraTest.setVisibility(isLoraDevice ? View.VISIBLE : View.GONE);
                if (searchTimeout != null && !searchTimeout.isDisposed()) {
                    searchTimeout.dispose();
                    searchTimeout = null;
                }
                mDevice = device;
                mSearchDevice = new SearchDeviceBean(device, rssi, false);
                mBluetoothScanner.stopScan();
                sendCommand(AppConstants.CMD_REQ_PAIR, OPT_ADD_LOCK);
            }
        }

        @Override
        public void onScanFinished() {
            if (mBluetoothScanner != null && mBluetoothScanner.isScanning()) {
                mBluetoothScanner.stopScan();
            }
        }

        @Override
        public void onScanStarted() {
        }

        @Override
        public void onScanFailed(NexlessBluetoothScanner.ScanFailType type) {
            showToast("蓝牙不可用");
        }
    };

    private void sendCommand(String cmd, int opt) {
        if (mDevice == null) {
            showToast("请添加钥匙！");
            mDialogHelper.dismissProgressDialog();
            return;
        }
        new BluetoothUtil(mDevice.getAddress())
                .sendCommand(cmd, false, new BluetoothUtil.SendCmdCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        mDialogHelper.dismissProgressDialog();
                        tvLogs.append(result);
//                        switch (opt) {
//                            case OPT_ADD_LOCK:
//                                if (result.contains("OK")) {
//                                    btnAddLock.setBackgroundResource(R.color.green);
//                                }
//                                break;
//                        }
                        result = result.replace("\r\n", "");
                        if (result.contains(AppConstants.CMD_ACK_VOLTAGE)) {
                            String[] resArr = result.split(":");
                            if (resArr.length > 1) {
                                tvBattery.setText("电量：" + resArr[1] + "mV");
                            }
                        } else if (result.contains(AppConstants.CMD_ACK_PAIR_OK)) {
                            btnAddLock.setBackgroundResource(R.color.green);
                        } else if (result.contains(AppConstants.CMD_ACK_DOOR)) {
                            btnOpenLock.setBackgroundResource(R.color.green);
                        } else if (result.contains(AppConstants.CMD_ACK_TOUCH_OK)) {
                            btnTouchTest.setBackgroundResource(R.color.green);
                        } else if (result.contains(AppConstants.CMD_ACK_TOUCH_NUM)) {
                            isMyLock = true;
                            String[] resArr = result.split(":");
                            if (resArr.length > 1) {
                                int key = Integer.valueOf(resArr[1]);
                                if (key == 0) {
                                    mKeyboardAdapter.selectKey(10);
                                } else if (key == 11) {
                                    mKeyboardAdapter.selectKey(11);
                                } else {
                                    mKeyboardAdapter.selectKey(Integer.valueOf(resArr[1]) - 1);
                                }
                            }
                        } else if (result.contains(AppConstants.CMD_ACK_SLEEP)) {
                            ConnectionHelper.getInstance().disConnDevice(mDevice.getAddress());
                            btnSleep.setBackgroundResource(R.color.green);
                        } else if (result.contains(AppConstants.CMD_ACK_FP_SUCC)) {
                            isMyLock = true;
                            btnAddFinger.setBackgroundResource(R.color.green);
                        } else if (result.contains(AppConstants.CMD_ACK_DEL)) {
                            ConnectionHelper.getInstance().disConnDevice(mDevice.getAddress());
                            clear();
                        } else if (result.contains(AppConstants.CMD_ACK_PAIR_ERROR)) {
                            showToast("错误信息，请对门锁恢复出厂设置状态。");
                            tvLogs.append("错误信息，请对门锁恢复出厂设置状态。\n");
                        } else if (result.contains(AppConstants.CMD_ACK_FA_END)) {
                            btnLoraTest.setBackgroundResource(R.color.green);
                        } else if (result.contains(AppConstants.CMD_ACK_FA_START)) {
                            btnLoraTest.setBackgroundResource(R.color.blue);
                        } else if (result.contains(AppConstants.CMD_ACK_INS_FP)) {
                            btnAddFinger.setBackgroundResource(R.color.blue);
                        } else if (result.contains(AppConstants.CMD_ACK_BURN)) {
                            btnBurn.setBackgroundResource(R.color.green);
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        mDialogHelper.dismissProgressDialog();
                        showToast(message);
                        tvLogs.append(message + "\n");
                    }
                });
    }

    private void clear() {
        if (operateTime != null && !operateTime.isDisposed()) {
            operateTime.dispose();
            operateTime = null;
        }
        mDevice = null;
        btnAddLock.setBackgroundResource(R.color.gray);
        btnOpenLock.setBackgroundResource(R.color.gray);
        btnAddFinger.setBackgroundResource(R.color.gray);
        btnTouchTest.setBackgroundResource(R.color.gray);
        btnSleep.setBackgroundResource(R.color.gray);
        btnLoraTest.setBackgroundResource(R.color.gray);
        btnBurn.setBackgroundResource(R.color.gray);
        mKeyboardAdapter.reset();
        tvCardNum.setText("卡号：");
    }

    public boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(getApplicationContext(), neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (!isAllGranted) {
                showToast("权限被拒绝");
            }
            btnAddLock.setEnabled(isAllGranted);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
//            mSearchDevice = null;
//            isMyLock = false;
//            tvTime.setText("0S");
//            tvLogs.setText("");
//            tvBattery.setText("电量：");
//            mDialogHelper.showProgressDialog();
//            sendCommand(AppConstants.CMD_REQ_DEL, OPT_ADD_LOCK);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchTimeout != null && !searchTimeout.isDisposed()) {
            searchTimeout.dispose();
            searchTimeout = null;
        }
        if (operateTime != null && !operateTime.isDisposed()) {
            operateTime.dispose();
            operateTime = null;
        }
    }
}
