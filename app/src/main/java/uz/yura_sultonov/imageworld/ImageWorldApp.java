package uz.yura_sultonov.imageworld;

import android.app.Application;
import android.os.Handler;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.jacksonandroidnetworking.JacksonParserFactory;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import uz.yura_sultonov.imageworld.utils.Utilities;
import uz.yura_sultonov.imageworld.viewmodels.AppViewModel;

public class ImageWorldApp extends Application {

    public static volatile Handler appHandler;
    public AppViewModel mAppModel;

    @Override
    public void onCreate() {
        super.onCreate();
        Utilities.object().init(this);
        appHandler = new Handler(getMainLooper());
        initNetworkServices();

        // show butterknife's logs if debugging mode
        if (Utilities.object().DEBUG)
            ButterKnife.setDebug(true);
        mAppModel = new AppViewModel(this);
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
