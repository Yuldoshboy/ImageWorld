package uz.yura_sultonov.imageworld.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;

import java.util.Arrays;
import java.util.List;

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
                mApp.mAppModel.setCurrPage(1);
                loadNextDataFromPixabay();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        MenuItemCompat.setOnActionExpandListener(mSearch, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mSearchView.setIconifiedByDefault(true);
                mSearchView.setFocusable(true);
                mSearchView.setIconified(false);
                mSearchView.requestFocusFromTouch();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Utilities.object().hideKeyboard(MainActivity.this);
                if (!mApp.mAppModel.getSearchKey().equals("")) {
                    mApp.mAppModel.setSearchKey("");
                    mApp.mAppModel.setCurrPage(1);
                    loadNextDataFromPixabay();
                }
                return true;
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void showSortTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle(R.string.choose_order);

        builder.setSingleChoiceItems(
                SortTypes.getTitles(),
                Arrays.asList(SortTypes.ALL()).indexOf(mApp.mAppModel.getSortType().valueStr()),
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
                        mApp.mAppModel.setCurrPage(1);
                        loadNextDataFromPixabay();
                    }

                    sortTypeAlertDialog.dismiss();
                });

        sortTypeAlertDialog = builder.create();
        sortTypeAlertDialog.show();
    }


    private void loadNextDataFromPixabay() {
        if (Utilities.object().isNetAvailable(this)) {
            AndroidNetworking.get(Constants.API_BASE_URL + "key={apiKey}&order={orderBy}&page={pageNumber}&per_page={perPage}&q={searchKey}")
                    .addPathParameter("apiKey", Constants.API_KEY)
                    .addPathParameter("orderBy", mApp.mAppModel.getSortType().valueStr())
                    .addPathParameter("searchKey", mApp.mAppModel.getSearchKey())
                    .addPathParameter("perPage", String.valueOf(Constants.PER_PAGE))
                    .addPathParameter("pageNumber", String.valueOf(mApp.mAppModel.getCurrPage()))
                    .setPriority(Priority.IMMEDIATE)
                    .build()
                    .getAsObject(ImageResponse.class, new ParsedRequestListener<ImageResponse>() {
                        @Override
                        public void onResponse(ImageResponse response) {
                            if (mApp.mAppModel.getCurrPage() == 1) {
                                mApp.mAppModel.clearAllData();
                            }
                            mApp.mAppModel.setTotalHits(Math.max(mApp.mAppModel.getTotalHits(), response.getTotalHits()));
                            setDataToGridView(response.getHits());
                        }

                        @Override
                        public void onError(ANError anError) {
                            HLog.e("error", anError.getErrorDetail());
                        }
                    });
        } else {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_LONG).show();
        }
    }

    private void setDataToGridView(List<ImageHits> hits) {
        mApp.mAppModel.addNextPartData(hits);
        adapter.notifyDataSetChanged();
        scrollListener.continueListeningMoreData();
    }

    public void fetchMoreItems() {
        if ((mApp.mAppModel.getCurrPage() + 1) * Constants.PER_PAGE > mApp.mAppModel.getTotalHits()) {
            return;
        }
        loadNextDataFromPixabay();
        mApp.mAppModel.setNextPage();
    }
}
