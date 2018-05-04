package uz.yura_sultonov.imageworld.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.security.auth.x500.X500Principal;

import okhttp3.OkHttpClient;

public class Utilities {
    private Context appContext;

    public boolean DEBUG = false;
    @SuppressLint("StaticFieldLeak")
    private static Utilities _instance = new Utilities();

    private static volatile OkHttpClient mImageHttpClient;

    public static Utilities object() {
        return _instance;
    }

    private Utilities() {
    }

    public void init(Context appContext) {
        this.appContext = appContext;
        DEBUG = isDebuggable();
        mImageHttpClient = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .build();
    }

    // is app debug mode
    private boolean isDebuggable() {
        if (appContext == null) return false;

        boolean debuggable = false;
        boolean problemsWithData = false;

        PackageManager pm = appContext.getPackageManager();
        try {
            ApplicationInfo appinfo = pm.getApplicationInfo(appContext.getPackageName(), 0);
            debuggable = (0 != (appinfo.flags &= ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (PackageManager.NameNotFoundException e) {
            problemsWithData = true;
        }

        if (problemsWithData) {
            try {
                PackageInfo pinfo = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), PackageManager.GET_SIGNATURES);
                Signature signatures[] = pinfo.signatures;

                for (Signature signature : signatures) {
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    ByteArrayInputStream stream = new ByteArrayInputStream(signature.toByteArray());
                    X509Certificate cert = (X509Certificate) cf.generateCertificate(stream);
                    debuggable = cert.getSubjectX500Principal().equals(new X500Principal("CN=Android Debug,O=Android,C=US"));
                    if (debuggable)
                        break;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            }
        }
        return debuggable;
    }

    // is connected
    public boolean isNetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) appContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo acNetInfo = connectivityManager.getActiveNetworkInfo();
        return acNetInfo != null && acNetInfo.isAvailable() && acNetInfo.isConnected();
    }

    // is connecting or connected
    public boolean isNetAvailableOrLing() {
        ConnectivityManager connectivityManager = (ConnectivityManager) appContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo acNetInfo = connectivityManager.getActiveNetworkInfo();
        return acNetInfo != null && acNetInfo.isAvailable() && acNetInfo.isConnectedOrConnecting();
    }

    public static void showKeyboard(EditText editText) {
        if (editText == null) {
            return;
        }
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void showForcedKeyboard(AppCompatActivity mAct) {
        InputMethodManager imm = (InputMethodManager) mAct.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static boolean isKeyboardShowed(View view) {
        if (view == null) {
            return false;
        }
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return inputManager.isActive(view);
    }

    public static void hideKeyboard(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!imm.isActive()) {
            return;
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboard(FragmentActivity act) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) act.getSystemService(Activity.INPUT_METHOD_SERVICE);
            IBinder windowToken = act.getCurrentFocus().getWindowToken();
            if(windowToken != null) inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

