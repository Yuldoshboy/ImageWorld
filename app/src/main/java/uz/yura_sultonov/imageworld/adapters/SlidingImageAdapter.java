package uz.yura_sultonov.imageworld.adapters;

import android.graphics.drawable.AnimationDrawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import uz.yura_sultonov.imageworld.R;
import uz.yura_sultonov.imageworld.activities.FullScreenActivity;
import uz.yura_sultonov.imageworld.helpers.TouchImageView;
import uz.yura_sultonov.imageworld.utils.HLog;

public class SlidingImageAdapter extends PagerAdapter {

    private LayoutInflater mInflatter;
    private FullScreenActivity mAct;

    public SlidingImageAdapter(FullScreenActivity activity) {
        this.mAct = activity;
        mInflatter = LayoutInflater.from(mAct);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mAct.mApp.mAppModel.getImages().size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = mInflatter.inflate(R.layout.sliding_image, view, false);

        final TouchImageView imageView = imageLayout.findViewById(R.id.image);

        String url = mAct.mApp.mAppModel.getImages().get(position).getLargeImageURL();
        Glide.with(mAct).load(url)
                .apply(new RequestOptions().error(R.drawable.error).placeholder(R.drawable.placeholder))
                .into(imageView);

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
