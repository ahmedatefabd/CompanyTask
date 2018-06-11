package com.task.ateftask.application;

import android.app.Application;

import com.facebook.FacebookSdk;

public class TaskApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
