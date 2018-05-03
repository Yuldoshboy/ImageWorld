package uz.yura_sultonov.imageworld.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uz.yura_sultonov.imageworld.R;
import uz.yura_sultonov.imageworld.adapters.GridAdapter;
import uz.yura_sultonov.imageworld.entities.ImageHits;
import uz.yura_sultonov.imageworld.entities.ImageResponse;
import uz.yura_sultonov.imageworld.helpers.ScrollListener;
import uz.yura_sultonov.imageworld.utils.Constants;
import uz.yura_sultonov.imageworld.utils.HLog;
import uz.yura_sultonov.imageworld.utils.Utilities;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.grid_view)
    public GridView gv;

    public static List<ImageHits> imagesData;
    public int currPage = 1;
    private int totalHits = 0;
    public GridAdapter adapter;
    public ScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        imagesData = new ArrayList<>();
        adapter = new GridAdapter(this);
        scrollListener = new ScrollListener(this);
        gv.setAdapter(adapter);
        gv.setOnScrollListener(scrollListener);

        getDataFromPixabay();
    }

    private void getDataFromPixabay() {
        if (Utilities.object().isNetAvailable()) {
            AndroidNetworking.get(Constants.API_BASE_URL + "key={apiKey}&order={orderBy}&page={pageNumber}&per_page={perPage}")
                    .addPathParameter("apiKey", Constants.API_KEY)
                    .addPathParameter("orderBy", Constants.SORT_LATEST)
                    .addPathParameter("perPage", String.valueOf(Constants.PER_PAGE))
                    .addPathParameter("pageNumber", String.valueOf(currPage))
                    .setPriority(Priority.IMMEDIATE)
                    .build()
                    .getAsObject(ImageResponse.class, new ParsedRequestListener<ImageResponse>() {
                        @Override
                        public void onResponse(ImageResponse response) {
                            totalHits = Math.max(totalHits, response.getTotalHits());
                            setDataToGridView(response.getHits());
                        }

                        @Override
                        public void onError(ANError anError) {
                            HLog.e("error", anError.getErrorDetail());
                        }
                    });
        } else {
            Toast.makeText(this, "Connect to internet", Toast.LENGTH_LONG).show();
        }
    }

    private void setDataToGridView(List<ImageHits> hits) {
        imagesData.addAll(hits);
        adapter.updateList(hits);
        scrollListener.continueListeningMoreData();
    }

    public void itemClicked(int position) {
        Toast.makeText(this, position + " clicked", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, FullScreenActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    public void fetchMoreItems() {
        if ((currPage + 1) * Constants.PER_PAGE > totalHits) {
            Toast.makeText(this, "No more data", Toast.LENGTH_LONG).show();
        }
        getDataFromPixabay();
        currPage++;
    }
}
