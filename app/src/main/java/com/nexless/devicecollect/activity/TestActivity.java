package com.nexless.devicecollect.activity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nexless.ccommble.conn.ConnectionHelper;
import com.nexless.ccommble.scan.NexlessBluetoothScanner;
import com.nexless.devicecollect.AppConstants;
import com.nexless.devicecollect.R;
import com.nexless.devicecollect.model.SearchDeviceBean;
import com.nexless.devicecollect.util.BluetoothUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TestActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = TestActivity.class.getSimpleName();
    private SearchDeviceBean mSearchDevice;
    private ImageView mIvRssi;
    private TextView mTvRssi;
    private TextView mTvRegLog;
    private TextView mTvFaLog;
    private TextView mTvParaLog;
    private NexlessBluetoothScanner mBluetoothScanner;
    private Disposable timerSearchTimeout;
    private static final int OPT_PRD_A = 0x3001;
    private static final int OPT_PRD_FA = 0x3002;
    private static final int OPT_PRD_PARA = 0x3003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mSearchDevice = getIntent().getParcelableExtra(AppConstants.EXTRA_DEVICE);
        mIvRssi = findViewById(R.id.iv_test_rssi);
        mTvRssi = findViewById(R.id.tv_test_rssi);
        mTvRegLog = findViewById(R.id.tv_test_reg_log);
        mTvFaLog = findViewById(R.id.tv_test_fa_log);
        mTvParaLog = findViewById(R.id.tv_test_para_log);
        mTvRegLog.setMovementMethod(new ScrollingMovementMethod());
        mTvFaLog.setMovementMethod(new ScrollingMovementMethod());
        mTvParaLog.setMovementMethod(new ScrollingMovementMethod());
        findViewById(R.id.iv_test_rssi_refresh).setOnClickListener(this);
        findViewById(R.id.btn_test_prdreg).setOnClickListener(this);
        findViewById(R.id.btn_test_prdfa).setOnClickListener(this);
        findViewById(R.id.btn_test_prdpara).setOnClickListener(this);
        mBluetoothScanner = NexlessBluetoothScanner.getIntance(this);
        startScan();
    }

    private void startScan() {
        mBluetoothScanner.startScan(mScannerCallback);
        mDialogHelper.showProgressDialog();
        timerSearchTimeout = Observable.timer(5000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (timerSearchTimeout != null) {
                        timerSearchTimeout.dispose();
                        timerSearchTimeout = null;
                    }
                    mDialogHelper.dismissProgressDialog();
                    mBluetoothScanner.stopScan();
                    mIvRssi.setImageResource(R.drawable.ic_rssi_none);
                    mTvRssi.setText("None");
                });
    }

    private NexlessBluetoothScanner.NexlessScannerCallBack mScannerCallback = new NexlessBluetoothScanner.NexlessScannerCallBack() {
        @Override
        public void onScannerResultCallBack(BluetoothDevice device, int rssi, String uuid) {
            if (device != null) {
                refreshDevice(device, rssi);
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

    private void refreshDevice(BluetoothDevice device, int rssi) {
        if (device.getAddress().equals(mSearchDevice.device.getAddress())) {
            if (timerSearchTimeout != null) {
                timerSearchTimeout.dispose();
                timerSearchTimeout = null;
            }
            mDialogHelper.dismissProgressDialog();
            mBluetoothScanner.stopScan();
            mTvRssi.setText(rssi + "dBm");
            setRssiLevel(rssi);
        }
    }

    private void sendCommand(String cmd, int opt) {
        mDialogHelper.showProgressDialog();
        new BluetoothUtil(mSearchDevice.device.getAddress())
                .sendCommand(cmd, false, new BluetoothUtil.SendCmdCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        mDialogHelper.dismissProgressDialog();
                        switch (opt) {
                            case OPT_PRD_A:
                                mTvRegLog.append(result);
                                break;
                            case OPT_PRD_FA:
                                mTvFaLog.append(result);
                                break;
                            case OPT_PRD_PARA:
                                mTvParaLog.append(result);
                                break;
                        }
                        if (result.toLowerCase().contains("#prdackok")) {
                            ConnectionHelper.getInstance().disConnDevice(mSearchDevice.device.getAddress());
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        mDialogHelper.dismissProgressDialog();
                        switch (opt) {
                            case OPT_PRD_A:
                                mTvRegLog.append("\n" + message);
                                break;
                            case OPT_PRD_FA:
                                mTvFaLog.append("\n" + message);
                                break;
                            case OPT_PRD_PARA:
                                mTvParaLog.append("\n" + message);
                                break;
                        }
                    }
                }, 3000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_test_rssi_refresh:
                if (!mBluetoothScanner.isScanning()) {
                    startScan();
                }
                break;
            case R.id.btn_test_prdreg:
//                sendCommand("PrdReg 20", OPT_PRD_A);
                sendCommand("PrdA", OPT_PRD_A);
                break;
            case R.id.btn_test_prdfa:
                sendCommand("PrdFa", OPT_PRD_FA);
                break;
            case R.id.btn_test_prdpara:
                sendCommand("PrdPara", OPT_PRD_PARA);
                break;
        }
    }

    private void setRssiLevel(int rssi) {
        if (rssi > -80) {
            mIvRssi.setImageResource(R.drawable.ic_rssi_high);
        } else if (rssi > -95) {
            mIvRssi.setImageResource(R.drawable.ic_rssi_middle);
        } else if (rssi > -110) {
            mIvRssi.setImageResource(R.drawable.ic_rssi_low);
        } else {
            mIvRssi.setImageResource(R.drawable.ic_rssi_none);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerSearchTimeout != null) {
            timerSearchTimeout.dispose();
            timerSearchTimeout = null;
        }
        mDialogHelper.dismissProgressDialog();
        mBluetoothScanner.stopScan();
    }
}
