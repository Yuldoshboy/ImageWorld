package uz.yura_sultonov.imageworld.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import uz.yura_sultonov.imageworld.R;
import uz.yura_sultonov.imageworld.adapters.HorizontalImageAdapter;
import uz.yura_sultonov.imageworld.adapters.SlidingImageAdapter;

public class FullScreenActivity extends AppCompatActivity {

    @BindView(R.id.pager)
    public ViewPager pager;
    @BindView(R.id.indicator)
    public RecyclerView recyclerView;

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
        Toast.makeText(this, MainActivity.imagesData.get(position).getPreviewURL(), Toast.LENGTH_LONG).show();

        init();
    }

    private void init() {
        horizontalLayoutManager = new LinearLayoutManager(FullScreenActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        adapter = new HorizontalImageAdapter(this, MainActivity.imagesData);
        recyclerView.setAdapter(adapter);

        pager.setAdapter(new SlidingImageAdapter(this, MainActivity.imagesData));
        pager.setCurrentItem(position);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
    }

    private void setHorizontalScroll(int pos) {
        position = pos;
        recyclerView.smoothScrollToPosition(position);
        int firstVisibleItemPosition = horizontalLayoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = horizontalLayoutManager.findLastVisibleItemPosition();

        int centerPosition = (firstVisibleItemPosition + lastVisibleItemPosition) / 2;

        if (position > centerPosition) {
            recyclerView.smoothScrollToPosition(position + 1);
        } else if (position < centerPosition) {
            recyclerView.smoothScrollToPosition(position - 1);
        }
    }

    public void itemSelected(int pos) {
        position = pos;
        pager.setCurrentItem(position);
    }
}
