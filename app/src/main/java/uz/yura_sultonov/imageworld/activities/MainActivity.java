package uz.yura_sultonov.imageworld.activities;

import android.app.SharedElementCallback;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import uz.yura_sultonov.imageworld.ImageWorldApp;
import uz.yura_sultonov.imageworld.R;
import uz.yura_sultonov.imageworld.adapters.GridAdapter;
import uz.yura_sultonov.imageworld.entities.ImageHits;
import uz.yura_sultonov.imageworld.entities.ImageResponse;
import uz.yura_sultonov.imageworld.entities.SortTypes;
import uz.yura_sultonov.imageworld.helpers.ScrollListener;
import uz.yura_sultonov.imageworld.utils.Constants;
import uz.yura_sultonov.imageworld.utils.HLog;
import uz.yura_sultonov.imageworld.utils.Utilities;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.grid_recycle)
    RecyclerView gridRecycle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private int currPage = 1;
    private int totalHits = 0;
    private GridAdapter adapter;
    private ScrollListener scrollListener;
    private AlertDialog sortTypeAlertDialog;

    public ImageWorldApp mApp;
    private GridLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Slide slide = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            slide = new Slide();
            slide.setDuration(5000);
            getWindow().setExitTransition(slide);
        }
        ButterKnife.bind(this);

        // Setting actionbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        mApp = (ImageWorldApp) getApplication();

        adapter = new GridAdapter(this);
        scrollListener = new ScrollListener(this);
        layoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.column_count));
        gridRecycle.setLayoutManager(layoutManager);
        gridRecycle.setAdapter(adapter);
        gridRecycle.addOnScrollListener(scrollListener);

        loadNextDataFromPixabay();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main, menu);

        final MenuItem mSearch = menu.findItem(R.id.search);
        final SearchView mSearchView = (SearchView) mSearch.getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mApp.mAppModel.setSearchKey(query);
                currPage = 1;
                loadNextDataFromPixabay();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                break;
            case R.id.sort:
                showSortTypeDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void showSortTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Select Your Choice");

        CharSequence[] allSortTypes = SortTypes.ALL();

        builder.setSingleChoiceItems(
                allSortTypes,
                Arrays.asList(allSortTypes).indexOf(mApp.mAppModel.getSortType().valueStr()),
                (dialog, item) -> {
                    boolean is_item_selected = false;
                    switch (item) {
                        case 0:
                            is_item_selected = !mApp.mAppModel.getSortType().equals(SortTypes.SORT_LATEST);
                            mApp.mAppModel.setSortType(SortTypes.SORT_LATEST);
                            break;
                        case 1:
                            is_item_selected = !mApp.mAppModel.getSortType().equals(SortTypes.SORT_POPULAR);
                            mApp.mAppModel.setSortType(SortTypes.SORT_POPULAR);
                            break;
                    }
                    if (is_item_selected) {
                        currPage = 1;
                        loadNextDataFromPixabay();
                    }

                    sortTypeAlertDialog.dismiss();
                });

        sortTypeAlertDialog = builder.create();
        sortTypeAlertDialog.show();
    }



    private void loadNextDataFromPixabay() {
        if (Utilities.object().isNetAvailable(MainActivity.this)) {
            AndroidNetworking.get(Constants.API_BASE_URL + "key={apiKey}&order={orderBy}&page={pageNumber}&per_page={perPage}&q={searchKey}")
                    .addPathParameter("apiKey", Constants.API_KEY)
                    .addPathParameter("orderBy", mApp.mAppModel.getSortType().valueStr())
                    .addPathParameter("searchKey", mApp.mAppModel.getSearchKey())
                    .addPathParameter("perPage", String.valueOf(Constants.PER_PAGE))
                    .addPathParameter("pageNumber", String.valueOf(currPage))
                    .setPriority(Priority.IMMEDIATE)
                    .build()
                    .getAsObject(ImageResponse.class, new ParsedRequestListener<ImageResponse>() {
                        @Override
                        public void onResponse(ImageResponse response) {
                            if (currPage == 1) {
                                mApp.mAppModel.clearAllData();
                                adapter.notifyDataSetChanged();
                            }

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
        mApp.mAppModel.addNextPartData(hits);
        adapter.notifyDataSetChanged();
        scrollListener.continueListeningMoreData();
    }

    public void fetchMoreItems() {
        if ((currPage + 1) * Constants.PER_PAGE > totalHits) {
            Toast.makeText(this, "No more data", Toast.LENGTH_LONG).show();
        }
        loadNextDataFromPixabay();
        currPage++;
    }
}
