package com.element.facex;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.element.camera.ElementFaceAuthActivity;
import com.element.camera.ElementFaceEnrollActivity;
import com.element.camera.ElementFaceSDK;
import com.element.camera.ProviderUtil;
import com.element.camera.UserInfo;
import com.element.common.PermissionUtils;

public class MainActivity extends AppCompatActivity {

    public static final int ENROLL_REQ_CODE = 12800;

    public static final int AUTH_REQ_CODE = 12801;

    private MainRecyclerAdapter mainRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        mainRecyclerAdapter = new MainRecyclerAdapter(MainActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerView.setAdapter(mainRecyclerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PermissionUtils.verifyPermissions(
                this,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        mainRecyclerAdapter.addAll(ProviderUtil.getUsers(getBaseContext(), getPackageName(), ""));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENROLL_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                showMessage(getString(R.string.enroll_completed));
            } else {
                showMessage(getString(R.string.enroll_cancelled));
            }
        } else if (requestCode == AUTH_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String userId = data.getStringExtra(ElementFaceAuthActivity.EXTRA_ELEMENT_USER_ID);
                UserInfo userInfo = ProviderUtil.getUser(getBaseContext(), getPackageName(), userId);
                String results = data.getStringExtra(ElementFaceAuthActivity.EXTRA_RESULTS);
                final String message;
                if (ElementFaceAuthActivity.USER_VERIFIED.equals(results)) {
                    message = getString(R.string.msg_verified, userInfo.getFullName());
                } else if (ElementFaceAuthActivity.USER_FAKE.equals(results)) {
                    message = BuildConfig.DEBUG ? getString(R.string.msg_fake) : getString(R.string.msg_try_again) + ".";
                } else {
                    message = BuildConfig.DEBUG ? getString(R.string.msg_not_verified) : getString(R.string.msg_try_again);
                }
                showMessage(message);
            } else {
                showMessage(getString(R.string.auth_cancelled));
            }
        }
    }

    private void showMessage(String message) {
        Snackbar snackbar = Snackbar.make(
                findViewById(R.id.constraintLayout),
                message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void clickOnNewUser(View view) {
        UserDataFormFragment userDataFormFragment = new UserDataFormFragment();
        userDataFormFragment.setMainActivity(MainActivity.this);
        userDataFormFragment.show(getSupportFragmentManager(), null);
    }

    protected void clickOnUserRow(String userId) {
        if (isEnrolled(userId)) {
            startAuth(userId);
        } else {
            startEnroll(userId);
        }
    }

    protected boolean isEnrolled(String userId) {
        return ElementFaceSDK.isEnrolled(userId);
    }

    protected void startEnroll(String userId) {
        Intent intent = new Intent(MainActivity.this, ElementFaceEnrollActivity.class);
        intent.putExtra(ElementFaceEnrollActivity.EXTRA_ELEMENT_USER_ID, userId);
        startActivityForResult(intent, ENROLL_REQ_CODE);
    }

    protected void startAuth(String userId) {
        Intent intent = new Intent(MainActivity.this, ElementFaceAuthActivity.class);
        intent.putExtra(ElementFaceAuthActivity.EXTRA_ELEMENT_USER_ID, userId);
        startActivityForResult(intent, AUTH_REQ_CODE);
    }
}
