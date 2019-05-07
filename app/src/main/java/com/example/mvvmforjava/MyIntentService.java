package com.example.mvvmforjava;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.mvvmforjava.model.net.NetworkUtil;

public class MyIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
//    public MyIntentService(String name) {
//        super(name);
//    }

    public MyIntentService() {
        super("MyIntentService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while (true){
            NetworkUtil.ping();
            SystemClock.sleep(3000);
            Log.e("tag","intent service run......");
        }
    }
}
