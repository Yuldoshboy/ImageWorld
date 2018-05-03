package uz.yura_sultonov.imageworld.helpers;

import android.content.Context;
import android.widget.AbsListView;

import com.bumptech.glide.Glide;

import uz.yura_sultonov.imageworld.activities.MainActivity;

public class ScrollListener implements AbsListView.OnScrollListener {
    private final Context context;
    private final MainActivity activity;
    private boolean is_loading = true;

    public ScrollListener(MainActivity page) {
        this.activity = page;
        this.context = page.getApplicationContext();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
            Glide.with(this.context).resumeRequests();
        } else {
            Glide.with(this.context).pauseRequests();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        //Algorithm to check if the last item is visible or not
        final int lastItem = firstVisibleItem + visibleItemCount;
        if (lastItem == totalItemCount && !is_loading) {
            this.activity.fetchMoreItems();
            this.is_loading = true;
        }
    }

    public void continueListeningMoreData() {
        this.is_loading = false;
    }
}
