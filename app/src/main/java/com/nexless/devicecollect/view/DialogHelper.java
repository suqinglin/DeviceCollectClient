package com.nexless.devicecollect.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.nexless.devicecollect.R;

/**
 * @date: 2019/1/22
 * @author: su qinglin
 * @description: Dialog帮助类
 */
public class DialogHelper
{
    private Activity mActivity;
    private AlertDialog mDialog;
    public DialogHelper(Activity activity)
    {
        mActivity = activity;
    }
    public void dismissProgressDialog()
    {
        if(mActivity == null)
        {
            return;
        }
        if(mDialog != null && mDialog.isShowing())
        {
            mActivity.runOnUiThread(() -> {
                if(mDialog != null && mDialog.isShowing())
                {
                    mDialog.dismiss();
                    mDialog = null;
                }
            });
        }
    }
    public void showProgressDialog()
    {
        showProgressDialog("",true,null);
    }
    public void showProgressDialog(String msg)
    {
        showProgressDialog(msg,null);
    }
    public void showProgressDialog(DialogInterface.OnCancelListener cancelListener)
    {
        showProgressDialog("",true,cancelListener);
    }
    public void showProgressDialog(String msg, DialogInterface.OnCancelListener cancelListener)
    {
        showProgressDialog(msg,true,cancelListener);
    }
    public void showProgressDialog(final String msg, final boolean cancelable, final DialogInterface.OnCancelListener cancelListener)
    {
        dismissProgressDialog();
        if(mActivity == null)
        {
            return;
        }
        mActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mDialog = new AlertDialog.Builder(mActivity).create();
                // 设置全屏(去掉标签)
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                // 只去掉电池信息栏 保持触摸功能
                mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mDialog.show();
                // 这里注意一定要先show dialog 再去加载 contentView。否则会出现异常。
                mDialog.getWindow().setContentView(R.layout.loading_progress);
                mDialog.setCancelable(cancelable);
                if (cancelListener != null)
                {
                    mDialog.setOnCancelListener(cancelListener);
                }
                mDialog.setCanceledOnTouchOutside(false);
                final TextView infoView = (TextView) mDialog.getWindow().getDecorView().findViewById(R.id.loadingdialoginfo);
                if(!TextUtils.isEmpty(msg))
                {
                    infoView.setText(msg);
                }
                else
                {
                    infoView.setVisibility(View.GONE);
                }
            }
        });
    }
}
