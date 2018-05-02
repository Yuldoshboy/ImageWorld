package uz.yura_sultonov.imageworld.utils;

import android.util.Log;

// logger only on mode debug
public class HLog {
    public static void i(String tag, String log) {
        if (Utilities.object().DEBUG) Log.i(tag, log);
    }
    public static void i(String tag, String log, Exception e) {
        if (Utilities.object().DEBUG) Log.i(tag, log, e);
    }

    public static void v(String tag, String log) {
        if (Utilities.object().DEBUG) Log.v(tag, log);
    }
    public static void v(String tag, String log, Exception e) {
        if (Utilities.object().DEBUG) Log.v(tag, log, e);
    }

    public static void d(String tag, String log) {
        if (Utilities.object().DEBUG) Log.d(tag, log);
    }
    public static void d(String tag, String log, Exception e) {
        if (Utilities.object().DEBUG) Log.d(tag, log, e);
    }

    public static void w(String tag, String log) {
        if (Utilities.object().DEBUG) Log.w(tag, log);
    }
    public static void w(String tag, String log, Exception e) {
        if (Utilities.object().DEBUG) Log.w(tag, log, e);
    }

    public static void e(String tag, String log) {
        if (Utilities.object().DEBUG) Log.e(tag, log);
    }
    public static void e(String tag, String log, Exception e) {
        if (Utilities.object().DEBUG) Log.e(tag, log, e);
    }
}

