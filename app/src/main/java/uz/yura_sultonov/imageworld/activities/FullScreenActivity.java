package uz.yura_sultonov.imageworld.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import uz.yura_sultonov.imageworld.ImageWorldApp;
import uz.yura_sultonov.imageworld.R;
import uz.yura_sultonov.imageworld.adapters.HorizontalImageAdapter;
import uz.yura_sultonov.imageworld.adapters.SlidingImageAdapter;

public class FullScreenActivity extends AppCompatActivity {

    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.indicator)
    RecyclerView recyclerView;

    public ImageWorldApp mApp;

    private int position;
    private HorizontalImageAdapter adapter;
    private LinearLayoutManager horizontalLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);

        mApp = (ImageWorldApp) getApplication();

        init();
    }

    private void init() {
        horizontalLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        );

        recyclerView.setLayoutManager(horizontalLayoutManager);

        adapter = new HorizontalImageAdapter(this);
        recyclerView.setAdapter(adapter);

        pager.setAdapter(new SlidingImageAdapter(this));
        pager.setOffscreenPageLimit(3);
        pager.setCurrentItem(position);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setHorizontalScroll(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setHorizontalScroll(position);
    }

    private void setHorizontalScroll(int pos) {
        position = pos;
        recyclerView.smoothScrollToPosition(position);
    }

    public void itemSelected(int pos) {
        position = pos;
        pager.setCurrentItem(position);
    }
}
