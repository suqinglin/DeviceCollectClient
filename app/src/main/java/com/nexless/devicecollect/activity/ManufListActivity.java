package com.nexless.devicecollect.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.nexless.ccommble.codec.digest.DigestUtils;
import com.nexless.devicecollect.AppConstants;
import com.nexless.devicecollect.R;
import com.nexless.devicecollect.adapter.PAdapter;
import com.nexless.devicecollect.adapter.PViewHolder;
import com.nexless.devicecollect.httpservice.RxHelper;
import com.nexless.devicecollect.httpservice.ServiceFactory;
import com.nexless.devicecollect.model.ManufInfo;
import com.nexless.devicecollect.model.ManufListResponse;
import com.nexless.devicecollect.model.TResponse;
import com.nexless.devicecollect.util.DoNetUnicodeUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * @date: 2019/7/15
 * @author: su qinglin
 * @description:
 */
public class ManufListActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private static String TAG = ManufListActivity.class.getSimpleName();
    private List<ManufInfo> manufList = new ArrayList<>();
    private PAdapter<ManufInfo> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manuf_list);
        ListView lvManufList = findViewById(R.id.comm_listview_list);
        mAdapter = new PAdapter<ManufInfo>(this, manufList, R.layout.item_device) {
            @Override
            public void convert(PViewHolder helper, ManufInfo item, int position) {

                TextView tvDeviceName = helper.getView(R.id.tv_device_item_device_name);
                tvDeviceName.setText(item.getName() + "(" + item.getId() + ")");
            }
        };
        lvManufList.setAdapter(mAdapter);
        lvManufList.setOnItemClickListener(this);
        getManufList();
    }

    private void getManufList() {
        mDialogHelper.showProgressDialog();
        Observable<TResponse<ManufListResponse>> observable
                = ServiceFactory.getInstance().getApiService().getManufList();
        RxHelper.getInstance().sendRequest(TAG, observable, response -> {
            mDialogHelper.dismissProgressDialog();
            if (response.isSuccess()) {
                manufList.clear();
                manufList.addAll(response.data.getManufList());
                mAdapter.notifyDataSetChanged();
            } else {
                mDialogHelper.dismissProgressDialog();
                showToast(response.message);
            }
        }, throwable -> {
            mDialogHelper.dismissProgressDialog();
            showToast(RxHelper.getInstance().getErrorInfo(throwable));
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final ManufInfo manufInfo = (ManufInfo) parent.getItemAtPosition(position);
        View view1 = LayoutInflater.from(this).inflate(R.layout.layout_manuf_pwd, null, false);
        EditText editText = view1.findViewById(R.id.edt_manuf_pwd);
        new AlertDialog.Builder(this)
                .setTitle("Manuf Password")
                .setView(view1)
                .setPositiveButton("OK", (dialog, which) -> {
                    String pwd = editText.getEditableText().toString();
                    String ePwd = DigestUtils.md5Hex(DoNetUnicodeUtils.toUnicodeMC(pwd)).toUpperCase();
                    if (manufInfo.getPassword().equals(ePwd)) {
                        dialog.dismiss();
                        Intent intent = getIntent();
                        intent.putExtra(AppConstants.EXTRA_MANUF, manufInfo);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        showToast("密码错误");
                    }
                })
        .show();

    }
}
