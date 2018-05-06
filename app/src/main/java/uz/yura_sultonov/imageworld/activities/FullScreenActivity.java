package uz.yura_sultonov.imageworld.activities;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uz.yura_sultonov.imageworld.ImageWorldApp;
import uz.yura_sultonov.imageworld.R;
import uz.yura_sultonov.imageworld.adapters.ImagesListAdapter;
import uz.yura_sultonov.imageworld.adapters.SlidingImageAdapter;
import uz.yura_sultonov.imageworld.entities.ImageHits;
import uz.yura_sultonov.imageworld.entities.ImageResponse;
import uz.yura_sultonov.imageworld.utils.Constants;
import uz.yura_sultonov.imageworld.utils.HLog;
import uz.yura_sultonov.imageworld.utils.Utilities;

public class FullScreenActivity extends AppCompatActivity {

    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.indicator)
    ViewPager bottomPager;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    public ImageWorldApp mApp;
    private ImagesListAdapter imageListAdapter;
    private SlidingImageAdapter slidingImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_full_screen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.setDuration(5000);
            getWindow().setEnterTransition(fade);
        }
        ButterKnife.bind(this);

        mToolbar.getBackground().setAlpha(45);
        bottomPager.getBackground().setAlpha(45);
        mApp = (ImageWorldApp) getApplication();

        // Setting actionbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.full_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                shareCurrentImage();
                break;
            case R.id.download:
                downloadCurrentImage();
                break;
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareCurrentImage() {
        Glide.with(this)
                .asBitmap()
                .load(mApp.mAppModel.getImages().get(pager.getCurrentItem()).getLargeImageURL())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Utilities.object().shareImage(FullScreenActivity.this, resource);
                    }
                });
    }

    private void downloadCurrentImage() {
        if (!Utilities.object().isPermissionGranted(this, Constants.CODE_PERMISSION_READ_STORAGE)) {
            Utilities.object().requestPermission(this, Constants.CODE_PERMISSION_READ_STORAGE);
            return;
        }
        if (!Utilities.object().isPermissionGranted(this, Constants.CODE_PERMISSION_WRITE_STORAGE)) {
            Utilities.object().requestPermission(this, Constants.CODE_PERMISSION_WRITE_STORAGE);
            return;
        }
        Glide.with(this)
                .asBitmap()
                .load(mApp.mAppModel.getImages().get(pager.getCurrentItem()).getLargeImageURL())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Utilities.object().saveImageToStorage(FullScreenActivity.this, resource);
                    }
                });
    }

    private void init() {
        imageListAdapter = new ImagesListAdapter(this);
        bottomPager.setAdapter(imageListAdapter);
        bottomPager.setOffscreenPageLimit(2);
        bottomPager.setCurrentItem(mApp.mAppModel.getCurrPosition(), true);

        slidingImageAdapter = new SlidingImageAdapter(this);
        pager.setAdapter(slidingImageAdapter);
        pager.setOffscreenPageLimit(2);
        pager.setCurrentItem(mApp.mAppModel.getCurrPosition(), true);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mApp.mAppModel.setCurrPosition(position);
                bottomPager.setCurrentItem(position, true);
                if (position > mApp.mAppModel.getTotalHits() - Constants.PER_PAGE / 4) {
                    fetchMoreItems();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
                            setDataToPagers(response.getHits());
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

    private void setDataToPagers(List<ImageHits> hits) {
        mApp.mAppModel.addNextPartData(hits);
        slidingImageAdapter.notifyDataSetChanged();
        imageListAdapter.notifyDataSetChanged();
    }

    public void fetchMoreItems() {
        if ((mApp.mAppModel.getCurrPage() + 1) * Constants.PER_PAGE > mApp.mAppModel.getTotalHits()) {
            return;
        }
        loadNextDataFromPixabay();
        mApp.mAppModel.setNextPage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.CODE_PERMISSION_READ_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    downloadCurrentImage();
                else
                    Toast.makeText(this, R.string.you_must_allow, Toast.LENGTH_LONG).show();
                break;
            case Constants.CODE_PERMISSION_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    downloadCurrentImage();
                else
                    Toast.makeText(this, R.string.you_must_allow, Toast.LENGTH_LONG).show();
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void imageListClicked(int position) {
        pager.setCurrentItem(position, true);
    }

}
