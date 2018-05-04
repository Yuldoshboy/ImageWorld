package uz.yura_sultonov.imageworld.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import uz.yura_sultonov.imageworld.R;
import uz.yura_sultonov.imageworld.activities.FullScreenActivity;

public class HorizontalImageAdapter extends RecyclerView.Adapter<HorizontalImageAdapter.ImageViewHolder> {

    private FullScreenActivity mAct;
    private LayoutInflater mInflatter;

    public HorizontalImageAdapter(FullScreenActivity activity) {
        this.mAct = activity;
        this.mInflatter = LayoutInflater.from(mAct);
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemView = mInflatter.inflate(R.layout.image_item, parent, false);

        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        // Get the image URL for the current position.
        String url = mAct.mApp.mAppModel.getImages().get(position).getPreviewURL();

        //holder.imageView = new SquaredImageView(this.context);
        Glide.with(mAct).load(url).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAct.itemSelected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAct.mApp.mAppModel.getImages().size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageview)
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}