package com.element.fmex;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.element.camera.ElementFaceMatchingActivity;
import com.element.camera.ProviderUtil;
import com.element.camera.UserInfo;
import com.element.common.DeviceStatus;
import com.element.common.PermissionUtils;

public class MainActivity extends AppCompatActivity {

    public static final int FM_REQ_CODE = 25600;

    private ProgressDialog progressDialog;

    private MainRecyclerAdapter mainRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(MainActivity.this);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        mainRecyclerAdapter = new MainRecyclerAdapter(MainActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setAdapter(mainRecyclerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isAllGranted = PermissionUtils.verifyPermissions(
                this,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (!isAllGranted) {
            return;
        }

        String version = "Version: " + DeviceStatus.getAppVersion(getBaseContext());
        TextView info = findViewById(R.id.info);
        info.setText(version);
        refreshData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FM_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String userId = data.getStringExtra(ElementFaceMatchingActivity.EXTRA_ELEMENT_USER_ID);
                UserInfo userInfo = ProviderUtil.getUser(getBaseContext(), getPackageName(), userId);
                String results = data.getStringExtra(ElementFaceMatchingActivity.EXTRA_AUTH_RESULTS);
                final String message;
                if (ElementFaceMatchingActivity.USER_VERIFIED.equals(results)) {
                    message = getString(R.string.msg_verified, userInfo.getFullName());
                } else {
                    message = getString(R.string.msg_not_verified);
                }
                showToastMessage(message);
            } else {
                showToastMessage(getString(R.string.auth_cancelled));
            }
        }
    }

    void refreshData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainRecyclerAdapter.addAll(ProviderUtil.getUsers(getBaseContext(), getPackageName(), ""));
            }
        });
    }

    void showProgressDialog(final Activity activity, final String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != msg && msg.length() > 0) {
                    progressDialog.setMessage(msg);
                }
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        });
    }

    void dismissProgressDialog(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        });
    }

    void showToastMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void clickOnNewUser(View view) {
        UserQueryFragment fragment = new UserQueryFragment();
        fragment.setMainActivity(MainActivity.this);
        fragment.show(getSupportFragmentManager(), null);
    }

    protected void clickOnUserRow(String userId) {
        Intent intent = new Intent(MainActivity.this, ElementFaceMatchingActivity.class);
        intent.putExtra(ElementFaceMatchingActivity.EXTRA_ELEMENT_USER_ID, userId);
        startActivityForResult(intent, FM_REQ_CODE);
    }
}
