package com.element.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.element.utils.ElementSDKManager;

import java.util.HashMap;
import java.util.List;

import static com.element.example.MainActivity.KEY_DEMO_USER_LIST;

public class UserActivity extends AppCompatActivity implements ElementSDKManager.EnrollListener {

    private EditText editText;
    private EditText extraInfoEditText;
    private PrefsManager prefsManager;
    private Button save;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        prefsManager = new PrefsManager(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editText = findViewById(R.id.edit_text);
        extraInfoEditText = findViewById(R.id.extraInfo_editText);
        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> extras = null;
                if (!(TextUtils.isEmpty(extraInfoEditText.getText()))) {
                    extras = new HashMap<>();
                    extras.put("extraInfo", extraInfoEditText.getText().toString());
                }
                Toast.makeText(getBaseContext(), R.string.msg_request_enroll, Toast.LENGTH_LONG).show();
                ElementSDKManager.enrollNewUser(UserActivity.this, editText.getText().toString(), extras);
            }
        });
        extraInfoEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    save.callOnClick();
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onUserEnrolled(String userId, boolean isNewUser) {
        Toast.makeText(this, "UserId: " + userId + " isNewUser" + isNewUser, Toast.LENGTH_SHORT).show();
        List<DemoAppUser> demoAppUserList = prefsManager.getList(KEY_DEMO_USER_LIST, DemoAppUser.class);
        DemoAppUser demoAppUser = new DemoAppUser(
                editText.getText().toString(),
                userId
        );
        demoAppUserList.add(demoAppUser);
        prefsManager.saveList(KEY_DEMO_USER_LIST, demoAppUserList);
        finish();
    }

    @Override
    public void onUserFailedToEnroll() {
        Toast.makeText(this, "got result canceled!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ElementSDKManager.onActivityResult(requestCode, resultCode, data, this);
    }
}
