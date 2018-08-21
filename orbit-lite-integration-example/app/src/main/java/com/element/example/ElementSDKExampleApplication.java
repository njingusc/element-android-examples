package com.element.example;

import android.app.Application;

public class ElementSDKExampleApplication extends Application {

    static final String LOG_TAG = "ElementSDKExample";

    @Override
    public void onCreate() {
        super.onCreate();
       // ElementSDKManager.initElementSDK(this);
    }
}
