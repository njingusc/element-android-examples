package com.element.fmex;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.element.camera.ProviderUtil;
import com.element.camera.UserInfo;
import com.element.camera.UserQueryTask;
import com.element.common.DeviceStatus;

public class UserQueryFragment extends DialogFragment {

    private MainActivity mainActivity;

    protected EditText etUserId;

    protected Button enroll;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_user_query, container, false);

        etUserId = rootView.findViewById(R.id.userId);

        TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                execQuery();
                return true;
            }
        };

        etUserId.setImeActionLabel(getString(R.string.signUp), EditorInfo.IME_ACTION_DONE);
        etUserId.setOnEditorActionListener(onEditorActionListener);

        enroll = rootView.findViewById(R.id.query);
        enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(View view) {
                execQuery();
            }
        });

        return rootView;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public synchronized void execQuery() {
        final String userId = etUserId.getText().toString();

        if (!DeviceStatus.isNetworkConnected(mainActivity.getBaseContext())) {
            mainActivity.showToastMessage(getString(R.string.error_no_network));
            return;
        }

        if (TextUtils.isEmpty(userId)) {
            mainActivity.showToastMessage(getString(R.string.error_empty_fields));
            return;
        }

        if (ProviderUtil.userExist(mainActivity.getBaseContext(), mainActivity.getPackageName(), userId)) {
            mainActivity.showToastMessage(getString(R.string.user_existed));
            return;
        }

        mainActivity.showProgressDialog(mainActivity, mainActivity.getString(R.string.processing));
        query(userId);
    }

    private void query(final String userId) {
        UserQueryTask.Callback callback = new UserQueryTask.Callback() {
            @Override
            public void onResult(UserQueryTask.UserQueryResult result) {
                if (mainActivity == null || mainActivity.isFinishing()) {
                    return;
                }

                if (result.isSuccess()) {
                    UserInfo userInfo = new UserInfo(
                            mainActivity.getPackageName(),
                            userId,
                            result.firstName,
                            result.lastName
                    );
                    ProviderUtil.insertUserInfo(mainActivity.getBaseContext(), userInfo);
                    mainActivity.refreshData();
                } else if (result.isTimeout()) {
                    mainActivity.showToastMessage(getString(R.string.msg_connection_timeout));
                } else {
                    mainActivity.showToastMessage(result.displayMessage);
                }
                mainActivity.dismissProgressDialog(mainActivity);
                dismiss();
            }
        };

        new UserQueryTask(callback, mainActivity.getPackageName(), userId).execute();
    }
}