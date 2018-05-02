package uz.yura_sultonov.imageworld.utils;

import android.annotation.SuppressLint;

public class Utilities {

    public boolean DEBUG = false;
    @SuppressLint("StaticFieldLeak")
    private static Utilities _instance = new Utilities();

    public static Utilities object() {
        return _instance;
    }

    private Utilities() {
    }

}

