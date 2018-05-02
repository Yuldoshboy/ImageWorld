package uz.yura_sultonov.imageworld;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatDelegate;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.jacksonandroidnetworking.JacksonParserFactory;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import uz.yura_sultonov.imageworld.utils.Utilities;

public class ImageWorldApp extends Application {

    public static volatile Handler appHandler;
    @SuppressLint("StaticFieldLeak")
    public static volatile Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Utilities.object().init(this);
        appHandler = new Handler(getMainLooper());
        mContext = getApplicationContext();
        initNetworkServices();

        // show butterknife's logs if debugging mode
        if (Utilities.object().DEBUG)
            ButterKnife.setDebug(true);
    }

    // configuring connection settings
    private void initNetworkServices() {
        // 20 second for waiting response
        OkHttpClient mHttpClient = new OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();

        AndroidNetworking.initialize(getApplicationContext(), mHttpClient);
        AndroidNetworking.setParserFactory(new JacksonParserFactory());

        // show request/response logs if debugging mode
        if (Utilities.object().DEBUG) {
            AndroidNetworking.enableLogging();
            AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BODY);
        }
    }

}
