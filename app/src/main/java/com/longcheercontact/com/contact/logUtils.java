package com.longcheercontact.com.contact;
import android.util.Log;
public class logUtils {
    public static  final void i(String logi){
        if (BuildConfig.DEBUG){
            Log.i("logUtils", logi);
        }
    }

}
