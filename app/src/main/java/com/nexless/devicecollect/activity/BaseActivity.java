package com.nexless.devicecollect.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.nexless.devicecollect.view.DialogHelper;


/**
 * @date: 2019/5/5
 * @author: su qinglin
 * @description:
 */
public class BaseActivity extends Activity {

    protected DialogHelper mDialogHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDialogHelper = new DialogHelper(this);

    }

    /**
     * 所需的所有权限信息
     */
    public static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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

    public void uploadSucc() {
        showToast("上传成功");
    }

    public void uploadFail(String msg) {
        showToast(msg);
    }

}
