package com.element.fmex;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.ContextThemeWrapper;

public class ProgressDialogHelper {

    private ProgressDialog progressDialog;

    public void showProgressDialog(final Activity activity, final String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(new ContextThemeWrapper(
                            activity, android.R.style.Theme_DeviceDefault_Light));
                }

                if (null != msg && msg.length() > 0) {
                    progressDialog.setMessage(msg);
                }

                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        });
    }

    public void dismissProgressDialog(final Activity activity) {
        if (progressDialog != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            });
        }
    }
}