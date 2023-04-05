package com.example.dprotect;

public class NativeLib {

    // Used to load the 'dprotect' library on application startup.
    static {
        System.loadLibrary("dprotect");
    }

    /**
     * A native method that is implemented by the 'dprotect' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}