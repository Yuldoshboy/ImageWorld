package uz.yura_sultonov.imageworld.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

import butterknife.ButterKnife;
import uz.yura_sultonov.imageworld.R;
import uz.yura_sultonov.imageworld.utils.HLog;
import uz.yura_sultonov.imageworld.utils.Utilities;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // test connecting
        if (Utilities.object().isNetAvailable()) {
            AndroidNetworking.get("http://api-fotki.yandex.ru/api/users/styleroom/album/156904/photos/")
                    .addHeaders("Accept", "application/json")
                    .setPriority(Priority.IMMEDIATE)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            HLog.i("received", response.toString());
                        }

                        @Override
                        public void onError(ANError error) {
                            HLog.e("error", error.getErrorBody());
                        }
                    });
        }
        else{
            Toast.makeText(this, "Connect to internet", Toast.LENGTH_LONG).show();
        }
    }

}
