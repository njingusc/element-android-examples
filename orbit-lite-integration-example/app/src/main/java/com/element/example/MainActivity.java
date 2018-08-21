package com.element.example;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.element.sync.ElementSyncState;
import com.element.sync.ElementSyncState.SyncState;
import com.element.utils.ElementSDKManager;

import java.util.ArrayList;
import java.util.List;

import static com.element.example.ElementSDKExampleApplication.LOG_TAG;

public class MainActivity extends AppCompatActivity implements
        ElementSDKManager.SearchListener,
        //ElementSDKManager.SyncStateListener, <-- Example uses LoaderCallbacks instead
        ElementSDKManager.AuthListener,
        MainRecyclerAdapter.UserListActionListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private final int LOADER_ID = 1235;
    static final String KEY_DEMO_USER_LIST = "demo_user_list";

    private RecyclerView mRecyclerView;
    private MainRecyclerAdapter mAdapter;

    private PrefsManager prefsManager;

    private static final String TAG = "ElementSDKCallbacks";

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return ElementSDKManager.getCursorLoader(this, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Cursor cursor) {
        if (cursor != null) {
            onSyncStateFetched(ElementSDKManager.cursorToSyncState(cursor));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefsManager = new PrefsManager(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new MainRecyclerAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), R.string.msg_request_identification, Toast.LENGTH_LONG).show();
                ElementSDKManager.identifyUser(MainActivity.this, getAllElementUserIds());
            }
        });
        findViewById(R.id.info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), R.string.msg_request_sync, Toast.LENGTH_LONG).show();
                ElementSDKManager.requestServerSync(MainActivity.this);
            }
        });
    }

    private ArrayList<String> getAllElementUserIds() {
        List<DemoAppUser> demoAppUsers = prefsManager.getList(KEY_DEMO_USER_LIST, DemoAppUser.class);
        ArrayList<String> userIds = new ArrayList<>();
        for (DemoAppUser demoAppUser : demoAppUsers) {
            userIds.add(demoAppUser.elementId);
        }
        return userIds;
    }

    @Override
    protected void onStart() {
        super.onStart();
        ElementSDKManager.initElementSDK(this);

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getSupportLoaderManager().destroyLoader(LOADER_ID);
    }


    @Override
    public void onUsersFound(List<String> elementUserIds, boolean isPrimary) {
        String[] resultsArray = new String[elementUserIds.size()];
        for (int i = 0; i < elementUserIds.size(); i++) {
            List<DemoAppUser> demoAppUsers = prefsManager.getList(KEY_DEMO_USER_LIST, DemoAppUser.class);
            String name = null;
            for (DemoAppUser demoAppUser : demoAppUsers) {
                if (demoAppUser.elementId.equals(elementUserIds.get(i))) {
                    name = demoAppUser.name;
                    break;
                }
            }
            if (name == null) {
                resultsArray[i] = elementUserIds.get(i);
            } else {
                resultsArray[i] = name;
            }
        }
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Results: isPrimary = " + isPrimary)
                .setItems(resultsArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

        Toast.makeText(this, "found " + elementUserIds.size() + " matches", Toast.LENGTH_SHORT).show();
        for (int i = 0; i < elementUserIds.size(); i++) {
            Log.v(LOG_TAG, "found user at index: " + i + " id = " + elementUserIds.get(i) + " confidence = " + isPrimary);
        }
    }

    @Override
    public void onNoUsersFound() {
        Toast.makeText(this, "No Users Identified", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onIdentifyCanceled() {
        Log.i(TAG, "Identify Canceled");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ElementSDKManager.onActivityResult(requestCode, resultCode, data, this);
    }

    //@Override  <-- Example uses LoaderCallbacks instead of SyncStateListener
    public void onSyncStateFetched(ElementSyncState elementSyncState) {
        List<DemoAppUser> demoAppUsers = prefsManager.getList(KEY_DEMO_USER_LIST, DemoAppUser.class);
        for (String elementId : elementSyncState.getAsMap().keySet()) {
            SyncState syncState = elementSyncState.getState(elementId);
            if (syncState.hasModel() && !containsId(demoAppUsers, elementId)) {
                demoAppUsers.add(new DemoAppUser(syncState.getName(), elementId));
            }
        }
        prefsManager.saveList(KEY_DEMO_USER_LIST, demoAppUsers);

        mAdapter.setData(demoAppUsers);
    }

    private boolean containsId(List<DemoAppUser> items, String elementId) {
        boolean returnVal = false;
        for (DemoAppUser demoAppUser : items) {
            if (demoAppUser.equals(elementId)) {
                return true;
            }
        }
        return returnVal;
    }

    @Override
    public void onAuthSuccess(String userId) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("Verified: " + userId)
                .setPositiveButton("OK", null).show();
    }

    @Override
    public void onAuthFailed(String userId) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("NOT Verified: " + userId)
                .setPositiveButton("OK", null).show();
    }

    @Override
    public void onAuthCanceled() {
        Log.i(TAG, "Auth Canceled");
    }

    @Override
    public void onClickUser(DemoAppUser user) {
        Log.v(LOG_TAG, "demoAppUser: " + user.name);

        Toast.makeText(getBaseContext(), R.string.msg_request_auth, Toast.LENGTH_LONG).show();
        ElementSDKManager.authenticateUser(this, user.elementId);
    }

    @Override
    public void onLongClickUser(final DemoAppUser user, final int position) {

        new AlertDialog.Builder(this)
                .setMessage("Delete user " + user.name + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(LOG_TAG, "user info: " + user.name + " id = " + user.elementId);

                        ElementSDKManager.deleteUser(MainActivity.this, user.elementId);
                        deleteUserInLocalStorage(user.elementId);
                        ((MainRecyclerAdapter) mRecyclerView.getAdapter()).remove(position);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteUserInLocalStorage(String userId) {
        List<DemoAppUser> items = prefsManager.getList(KEY_DEMO_USER_LIST, DemoAppUser.class);
        int indexOf = -1;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).elementId.equals(userId)) {
                indexOf = i;
                break;
            }
        }
        if (indexOf != -1) {
            items.remove(indexOf);
        }
        prefsManager.saveList(KEY_DEMO_USER_LIST, items);
    }
}
