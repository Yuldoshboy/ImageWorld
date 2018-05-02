package uz.yura_sultonov.imageworld;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

import butterknife.ButterKnife;
import uz.yura_sultonov.imageworld.utils.Utilities;

public class ImageWorldApp extends Application {

    public static volatile Handler appHandler;
    @SuppressLint("StaticFieldLeak")
    public static volatile Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appHandler = new Handler(getMainLooper());
        mContext = getApplicationContext();

        if (Utilities.object().DEBUG)
            ButterKnife.setDebug(true);
    }


}
