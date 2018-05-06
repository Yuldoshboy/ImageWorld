package uz.yura_sultonov.imageworld.helpers;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;

import com.bumptech.glide.Glide;

import uz.yura_sultonov.imageworld.activities.MainActivity;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;

public class ScrollListener extends RecyclerView.OnScrollListener {
    private MainActivity mAct;
    private boolean is_loading = true;

    public ScrollListener(MainActivity activity) {
        this.mAct = activity;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_SETTLING)
            Glide.with(mAct).pauseRequests();
        else
            Glide.with(mAct).resumeRequests();
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        //Algorithm to check if the last item is visible or not
        if (!recyclerView.canScrollVertically(1) && !is_loading) {
            this.mAct.fetchMoreItems();
            this.is_loading = true;
        }
    }

    public void continueListeningMoreData() {
        this.is_loading = false;
    }
}
