package com.example.classmate;

import android.util.Log;

import javax.annotation.Nullable;

public class Print {

    public static void i(@Nullable Object o) {
        Log.wtf("Log Info", String.valueOf(o));
    }

    public static void d(@Nullable Object o) {
        Log.wtf("Log Debug Info", String.valueOf(o));
        Log.wtf("Log Debug Stack", Log.getStackTraceString(new Exception()));
    }
}
