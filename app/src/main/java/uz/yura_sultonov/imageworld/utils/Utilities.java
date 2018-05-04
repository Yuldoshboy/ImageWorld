package uz.yura_sultonov.imageworld.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.security.auth.x500.X500Principal;

public class Utilities {

    public boolean DEBUG = false;

    private static Utilities _instance = new Utilities();

    public static Utilities object() {
        return _instance;
    }

    private Utilities() {
    }

    public void init(Context appContext) {
        DEBUG = isDebuggable(appContext);
    }

    // is app debug mode
    private boolean isDebuggable(Context appContext) {
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
    public boolean isNetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo acNetInfo = connectivityManager.getActiveNetworkInfo();
        return acNetInfo != null && acNetInfo.isAvailable() && acNetInfo.isConnected();
    }

}

