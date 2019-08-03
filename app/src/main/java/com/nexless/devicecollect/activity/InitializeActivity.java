package com.nexless.devicecollect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nexless.devicecollect.AppConstants;
import com.nexless.devicecollect.R;
import com.nexless.devicecollect.model.ManufInfo;
import com.nexless.devicecollect.model.SearchDeviceBean;

/**
 * @date: 2019/6/11
 * @author: su qinglin
 * @description: 初始化页面
 */
public class InitializeActivity extends BaseActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private static final int REQ_SEARCH_DEVICE = 0x0002;
    private static final int REQ_SELECT_MANUF = 0x0003;
    private TextView tvTool;
    private TextView tvManuf;
    private CheckBox cbSeriaPortTool;
    private LinearLayout llToolContainer;
//    private EditText edtToolId;
//    private EditText edtManufId;
    private SearchDeviceBean mSeachDevice;
    private ManufInfo mSelectManuf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialize);

        tvTool = findViewById(R.id.tv_init_tool);
        tvManuf = findViewById(R.id.tv_init_manuf);
        cbSeriaPortTool = findViewById(R.id.cb_init_seria_port_tool);
        llToolContainer = findViewById(R.id.ll_init_select_tool_container);
//        edtToolId = findViewById(R.id.edt_init_tool_id);
//        edtManufId = findViewById(R.id.edt_init_manuf_id);
        findViewById(R.id.ll_init_select_tool).setOnClickListener(this);
        findViewById(R.id.ll_init_select_manuf).setOnClickListener(this);
        findViewById(R.id.btn_init_save).setOnClickListener(this);
        cbSeriaPortTool.setOnCheckedChangeListener(this);
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_init_select_tool:
                Intent searchIntent = new Intent(this, SearchDeviceActivity.class);
                searchIntent.putExtra(AppConstants.EXTRA_IS_SELECT_TOOL, cbSeriaPortTool.isChecked());
                startActivityForResult(searchIntent, REQ_SEARCH_DEVICE);
                break;

            case R.id.ll_init_select_manuf:
                startActivityForResult(new Intent(this, ManufListActivity.class), REQ_SELECT_MANUF);
                break;
            case R.id.btn_init_save:
//                String manufId = edtManufId.getEditableText().toString();
//                String toolId = edtToolId.getEditableText().toString();
                if (mSelectManuf == null) {
                    showToast("请选择厂商");
                    return;
                }
                if (cbSeriaPortTool.isChecked() && mSeachDevice == null) {
                    showToast("请选择工具");
                    return;
                }
//                if (TextUtils.isEmpty(manufId)) {
//                    showToast("请输入Manuf Id");
//                    return;
//                }
//                if (TextUtils.isEmpty(toolId)) {
//                    showToast("请输入Tool Id");
//                    return;
//                }
                Intent intent = new Intent(this, ProductActivity.class);
                intent.putExtra(AppConstants.EXTRA_DEVICE, mSeachDevice);
                intent.putExtra(AppConstants.EXTRA_MANUF, mSelectManuf);
                intent.putExtra(AppConstants.EXTRA_TOOL_ID, Integer.valueOf("1111"));
                intent.putExtra(AppConstants.EXTRA_IS_SELECT_TOOL, cbSeriaPortTool.isChecked());
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == REQ_SEARCH_DEVICE) {
                mSeachDevice = data.getParcelableExtra(AppConstants.EXTRA_DEVICE);
                tvTool.setText(mSeachDevice.bluetoothName);
//            mToolsMac = device.device.getAddress();
//            tvSelectToolsResult.setVisibility(View.VISIBLE);
//            btnReadDeviceInfoFirst.setVisibility(View.VISIBLE);
//            tvSelectToolsResult.setText(String.format(getString(R.string.select_tools_result_pre), device.device.getName()));

            } else if (requestCode == REQ_SELECT_MANUF){
                mSelectManuf = data.getParcelableExtra(AppConstants.EXTRA_MANUF);
                tvManuf.setText(mSelectManuf.getName());
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        llToolContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }
}
