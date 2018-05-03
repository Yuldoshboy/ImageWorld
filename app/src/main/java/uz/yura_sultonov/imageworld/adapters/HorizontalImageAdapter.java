package uz.yura_sultonov.imageworld.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uz.yura_sultonov.imageworld.R;
import uz.yura_sultonov.imageworld.activities.FullScreenActivity;
import uz.yura_sultonov.imageworld.entities.ImageHits;

public class HorizontalImageAdapter extends RecyclerView.Adapter<HorizontalImageAdapter.ImageViewHolder> {

    private List<ImageHits> data;
    private FullScreenActivity activity;
    private Context context;

    public HorizontalImageAdapter(FullScreenActivity fullScreenActivity, List<ImageHits> imagesData) {
        this.activity = fullScreenActivity;
        this.context = fullScreenActivity.getApplicationContext();
        this.data = imagesData;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);

        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        // Get the image URL for the current position.
        String url = data.get(position).getPreviewURL();
        //holder.imageView = new SquaredImageView(this.context);
        Glide.with(this.context).load(url).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.itemSelected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
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

