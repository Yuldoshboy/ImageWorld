package uz.yura_sultonov.imageworld.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import uz.yura_sultonov.imageworld.activities.MainActivity;
import uz.yura_sultonov.imageworld.entities.ImageHits;
import uz.yura_sultonov.imageworld.helpers.SquaredImageView;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public final class GridAdapter extends BaseAdapter {
    private final Context context;
    private final MainActivity activity;
    private List<String> urls;

    public GridAdapter(MainActivity page) {
        this.activity = page;
        this.context = page.getApplicationContext();
        urls = new ArrayList<>();
    }

    public void updateList(List<ImageHits> list) {
        for (ImageHits item : list) {
            urls.add(item.getPreviewURL());
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SquaredImageView view = (SquaredImageView) convertView;
        if (view == null) {
            view = new SquaredImageView(context);
            view.setScaleType(CENTER_CROP);
        }

        // Get the image URL for the current position.
        String url = getItem(position);
        Glide.with(this.context).load(url).into(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.itemClicked(position);
            }
        });

        return view;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public String getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clearItems() {
        urls = new ArrayList<>();
        notifyDataSetChanged();
    }
}