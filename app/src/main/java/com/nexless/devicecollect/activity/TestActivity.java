package com.nexless.devicecollect.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.nexless.devicecollect.R;
import com.nexless.devicecollect.httpservice.RxHelper;
import com.nexless.devicecollect.httpservice.ServiceFactory;
import com.nexless.devicecollect.model.MacAndTokenResponse;
import com.nexless.devicecollect.model.TResponse;
import com.nexless.devicecollect.model.UploadDeviceInfoResponse;
import com.nexless.devicecollect.util.QrUtil;
import io.reactivex.Observable;

public class TestActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = TestActivity.class.getSimpleName();
    private EditText mEdtUuid;
    private EditText mEdtModel;
    private EditText mEdtHwVersion;
    private EditText mEdtManufacturer;
    private TextView mTvMacTokenResult;
    private TextView mTvUploadDeviceResult;
    private ImageView mIvQrShow;
    private String remark;
    private String mac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mEdtUuid = findViewById(R.id.edt_test_uuid);
        mEdtModel = findViewById(R.id.edt_test_model);
        mEdtHwVersion = findViewById(R.id.edt_test_hw_version);
        mEdtManufacturer = findViewById(R.id.edt_test_manufacturer);
        mTvMacTokenResult = findViewById(R.id.tv_test_mac_token_result);
        mTvUploadDeviceResult = findViewById(R.id.tv_test_upload_device_result);
        mIvQrShow = findViewById(R.id.iv_test_qr_show);
        findViewById(R.id.btn_test_mac_token).setOnClickListener(this);
        findViewById(R.id.btn_test_upload_device).setOnClickListener(this);
        findViewById(R.id.btn_test_genera_qr_code).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test_mac_token:
                String uuid = mEdtUuid.getEditableText().toString();
                if (TextUtils.isEmpty(uuid)) {
                    showToast("UUID不能为空！");
                    return;
                }
                mDialogHelper.showProgressDialog();
                getMacAndToken(uuid);
                break;
            case R.id.btn_test_upload_device:
                String uuid1 = mEdtUuid.getEditableText().toString();
                String model = mEdtModel.getEditableText().toString();
                String hwVersion = mEdtHwVersion.getEditableText().toString();
                String manufacturer = mEdtManufacturer.getEditableText().toString();
                if (TextUtils.isEmpty(uuid1)) {
                    showToast("UUID不能为空！");
                    return;
                }
                if (TextUtils.isEmpty(model)) {
                    showToast("Model不能为空！");
                    return;
                }
                if (TextUtils.isEmpty(hwVersion)) {
                    showToast("Hardware Version不能为空！");
                    return;
                }
                if (TextUtils.isEmpty(manufacturer)) {
                    showToast("Manufacturer不能为空！");
                    return;
                }
//                mDialogHelper.showProgressDialog();
//                uploadDevice(uuid1, model, hwVersion, manufacturer);
                break;
            case R.id.btn_test_genera_qr_code:
                if (TextUtils.isEmpty(remark) || TextUtils.isEmpty(mac)) {
                    showToast("请先上传设备信息！");
                    return;
                }
                QrUtil.createQRcodeImage(remark + "#" + mac, mIvQrShow);
                break;
        }
    }

    /**
     * 获取MAC和Token
     * @param uuid
     */
    private void getMacAndToken(String uuid) {
        Observable<TResponse<MacAndTokenResponse>> observable = ServiceFactory.getInstance().getApiService().getMacAndToken(uuid);
        RxHelper.getInstance().sendRequest(TAG, observable, response -> {
            mDialogHelper.dismissProgressDialog();
            if (response.isSuccess()) {
                mTvMacTokenResult.setText("mac:" + response.data.getMac() + "\n" + "token:" + response.data.getToken());
            } else {
                showToast(response.message);
            }
        }, throwable -> {
            mDialogHelper.dismissProgressDialog();
            showToast(RxHelper.getInstance().getErrorInfo(throwable));
        });
    }

}
