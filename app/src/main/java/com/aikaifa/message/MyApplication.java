package com.aikaifa.message;

import android.app.Application;

import cn.bmob.v3.Bmob;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, "430fa9541dfb03560e2ea6ee1d0baba5");
    }
}
