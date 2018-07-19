package com.element.facex;

import android.app.Application;

import com.element.camera.ElementFaceSDK;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ElementFaceSDK.initSDK(MainApplication.this);
    }
}
