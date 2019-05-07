package com.example.mvvmforjava;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class App extends Application {
    private static RefWatcher refWatcher;
    public static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance  = this;
        refWatcher = LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(/*Context context*/) {
        return refWatcher;
    }
}
