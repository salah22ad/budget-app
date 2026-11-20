package com.smartwallet;

import android.app.Application;

public class SmartWalletApp extends Application {

    private static SmartWalletApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static SmartWalletApp getInstance() {
        return instance;
    }
}