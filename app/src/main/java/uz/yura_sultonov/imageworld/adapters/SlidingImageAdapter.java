package uz.yura_sultonov.imageworld.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import uz.yura_sultonov.imageworld.R;
import uz.yura_sultonov.imageworld.activities.FullScreenActivity;
import uz.yura_sultonov.imageworld.entities.ImageHits;

public class SlidingImageAdapter extends PagerAdapter {


    private List<ImageHits> data;
    private LayoutInflater inflater;
    private Context context;
    private FullScreenActivity activity;

    public SlidingImageAdapter(FullScreenActivity page, List<ImageHits> imagesData) {
        this.activity = page;
        this.context = page.getApplicationContext();
        this.data = imagesData;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.sliding_image, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.image);

        String url = data.get(position).getLargeImageURL();
        Glide.with(this.context).load(url).into(imageView);

        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}
