package uz.yura_sultonov.imageworld.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.security.auth.x500.X500Principal;

import uz.yura_sultonov.imageworld.BuildConfig;
import uz.yura_sultonov.imageworld.R;
import uz.yura_sultonov.imageworld.activities.FullScreenActivity;

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

    private String getPermissionString(int permissionCode) {
        String permission = "";
        switch (permissionCode) {
            case Constants.CODE_PERMISSION_WRITE_STORAGE:
                permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                break;
            case Constants.CODE_PERMISSION_READ_STORAGE:
                permission = Manifest.permission.READ_EXTERNAL_STORAGE;
                break;
        }
        return permission;
    }

    public boolean isPermissionGranted(Context context, int permissionCode) {
        String permission = getPermissionString(permissionCode);

        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(AppCompatActivity activity, int permissionCode) {
        String permission = getPermissionString(permissionCode);
        ActivityCompat.requestPermissions(activity, new String[]{permission},
                permissionCode);
    }

    public void saveImageToStorage(FullScreenActivity activity, Bitmap image) {

        String imageFileName = "ImageWorld_" + activity.mApp.mAppModel.getImages().get(activity.mApp.mAppModel.getCurrPosition()).getId() + ".jpg";
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        + "/ImageWorld");
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
                Snackbar.make(activity.findViewById(android.R.id.content), R.string.file_downloaded,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.open_file, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                File file = new File(storageDir, imageFileName);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                Uri uri = FileProvider.getUriForFile(activity,
                                        BuildConfig.APPLICATION_ID + ".provider",
                                        file);
                                intent.setDataAndType(uri, "image/*");
                                PackageManager pm = activity.getPackageManager();
                                if (intent.resolveActivity(pm) != null) {
                                    activity.startActivity(intent);
                                }
                            }
                        }).setDuration(2000)
                        .show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void shareImage(AppCompatActivity activity, Bitmap bitmap) {
        try {
            File file = new File(activity.getExternalCacheDir(), "ImageWorldPhoto.png");
            writeToCache(file, bitmap);
            file = new File(activity.getExternalCacheDir(), "ImageWorldPhoto.png");
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(activity,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            PackageManager pm = activity.getPackageManager();
            if (intent.resolveActivity(pm) != null) {
                activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.share_file)));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void hideKeyboard(AppCompatActivity act) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) act.getSystemService(Activity.INPUT_METHOD_SERVICE);
            IBinder windowToken = act.getCurrentFocus().getWindowToken();
            if(windowToken != null) inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeToCache(File file, Bitmap bitmap) throws IOException {
        FileOutputStream fOut = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        fOut.flush();
        fOut.close();
    }
}

