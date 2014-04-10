package com.vuseniordesign.safekey;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application{

    private static Context context;

    public void onCreate(){
        super.onCreate();
        context = this;
    }

    public static Context getAppContext() {
        return context;
    }
}
