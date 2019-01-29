package com.element.fmex;

import android.content.Context;
import android.os.Bundle;

import com.element.camera.Capture;
import com.element.camera.ElementFaceCaptureActivity;
import com.element.common.PermissionUtils;
import com.google.gson.Gson;

import java.net.HttpURLConnection;

import okhttp3.Response;

public class FmActivity extends ElementFaceCaptureActivity {

    private ProgressDialogHelper progressDialogHelper;

    @Override
    protected void onCreate(Bundle bundle) {
        getIntent().putExtra(EXTRA_USER_APP_ID, getPackageName());
        getIntent().putExtra(EXTRA_ELEMENT_USER_ID, getString(R.string.user_id));

        super.onCreate(bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!PermissionUtils.isGranted(getBaseContext(), android.Manifest.permission.CAMERA)) {
            toastMessage("Please grant all permissions in Settings -> Apps");
            finish();
        }
    }

    @Override
    public void onImageCaptured(Capture[] captures) {
        if (progressDialogHelper == null) {
            progressDialogHelper = new ProgressDialogHelper();
        }
        progressDialogHelper.showProgressDialog(FmActivity.this, getString(R.string.processing));

        String userId = getIntent().getStringExtra(EXTRA_ELEMENT_USER_ID);
        new FmTask(faceMatchingTaskCallback).execute(userId, captures);
    }

    private FmTask.FaceMatchingTaskCallback faceMatchingTaskCallback = new FmTask.FaceMatchingTaskCallback() {
        @Override
        public Context getContext() {
            return getBaseContext();
        }

        @Override
        public void onResult(Response response) throws Exception {
            if (response.code() == HttpURLConnection.HTTP_OK) {
                FmTask.FmResponse fmResponse = new Gson().fromJson(response.body().string(), FmTask.FmResponse.class);
                showResult(fmResponse.displayMessage, R.drawable.icon_check);
            } else {
                FmTask.ServerMessage serverMessage = new Gson().fromJson(response.body().string(), FmTask.ServerMessage.class);
                showResult(serverMessage.message, R.drawable.icon_focus);
            }
            progressDialogHelper.dismissProgressDialog(FmActivity.this);
        }

        @Override
        public void onException(String message) {
            showResult(message, R.drawable.icon_locker_white);
            progressDialogHelper.dismissProgressDialog(FmActivity.this);
        }
    };
}
