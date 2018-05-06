package uz.yura_sultonov.imageworld.adapters;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import uz.yura_sultonov.imageworld.R;
import uz.yura_sultonov.imageworld.activities.FullScreenActivity;

public class ImagesListAdapter extends PagerAdapter {

    private LayoutInflater mInflatter;
    private FullScreenActivity mAct;

    public ImagesListAdapter(FullScreenActivity activity) {
        this.mAct = activity;
        mInflatter = LayoutInflater.from(mAct);
    }

    @Override
    public float getPageWidth(int position) {
        return 0.2f;
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
        View imageLayout = mInflatter.inflate(R.layout.image_list_item, view, false);

        final ImageView imageView = imageLayout.findViewById(R.id.image_item);

        String url = mAct.mApp.mAppModel.getImages().get(position).getLargeImageURL();
        Glide.with(mAct).load(url).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAct.imageListClicked(position);
            }
        });

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

