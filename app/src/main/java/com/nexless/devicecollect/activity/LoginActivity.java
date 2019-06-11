package com.nexless.devicecollect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.nexless.ccommble.util.CommLog;
import com.nexless.devicecollect.R;
import com.nexless.devicecollect.httpservice.RxHelper;
import com.nexless.devicecollect.httpservice.ServiceFactory;
import com.nexless.devicecollect.model.LoginResponse;
import com.nexless.devicecollect.model.TResponse;
import com.nexless.devicecollect.util.AppPreference;

import io.reactivex.Observable;


/**
 * @date: 2019/5/23
 * @author: su qinglin
 * @description:
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText mEdtUserPhone;
    private EditText mEdtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEdtPassword = findViewById(R.id.edt_login_password);
        mEdtUserPhone = findViewById(R.id.edt_login_user_phone);
        mEdtUserPhone.setText(AppPreference.getString(AppPreference.USER_PHONE, ""));
        mEdtPassword.setText(AppPreference.getString(AppPreference.USER_PASSWORD, ""));
        findViewById(R.id.btn_login).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String userPhone = mEdtUserPhone.getEditableText().toString();
        String password = mEdtPassword.getEditableText().toString();
        if (TextUtils.isEmpty(userPhone)) {
            showToast("请输入手机号");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast("请输入密码");
            return;
        }
        mDialogHelper.showProgressDialog();
        Observable<TResponse<LoginResponse>> observable = ServiceFactory.getInstance().getApiService().login(userPhone, password);
        RxHelper.getInstance().sendRequest(TAG, observable, loginResponseTResponse -> {
            mDialogHelper.dismissProgressDialog();
            CommLog.logE(TAG, "登录成功");
            if (loginResponseTResponse.isSuccess()) {
                AppPreference.putString(AppPreference.USER_PHONE, userPhone);
                AppPreference.putString(AppPreference.USER_PASSWORD, password);
                AppPreference.putString(AppPreference.LOGIN_TOKEN, loginResponseTResponse.data.getUserToken());
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                LoginActivity.this.finish();
            } else {
                showToast(loginResponseTResponse.message);
            }
        }, throwable -> {
            mDialogHelper.dismissProgressDialog();
            showToast(RxHelper.getInstance().getErrorInfo(throwable));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxHelper.getInstance().unSubscribeTask(TAG);
    }
}
