package uz.yura_sultonov.imageworld.adapters;

import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import uz.yura_sultonov.imageworld.R;
import uz.yura_sultonov.imageworld.activities.MainActivity;
import uz.yura_sultonov.imageworld.helpers.SquaredImageView;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public final class GridAdapter extends BaseAdapter {
    private MainActivity mAct;

    public GridAdapter(MainActivity activity) {
        this.mAct = activity;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SquaredImageView view = (SquaredImageView) convertView;

        if (view == null) {
            view = new SquaredImageView(mAct);
            view.setScaleType(CENTER_CROP);
        }

        // Get the image URL for the current position.
        String url = getItem(position);
        Glide.with(mAct)
                .load(url)
                //.apply(new RequestOptions().placeholder(getProgressBarIndeterminate()))
                .into(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAct.itemClicked(position);
            }
        });

        return view;
    }

    @Override
    public int getCount() {
        return mAct.mApp.mAppModel.getImages().size();
    }

    @Override
    public String getItem(int position) {
        return mAct.mApp.mAppModel.getImages().get(position).getPreviewURL();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}