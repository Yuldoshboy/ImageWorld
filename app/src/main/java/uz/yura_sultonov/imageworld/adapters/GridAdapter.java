package uz.yura_sultonov.imageworld.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import uz.yura_sultonov.imageworld.R;
import uz.yura_sultonov.imageworld.activities.FullScreenActivity;
import uz.yura_sultonov.imageworld.activities.MainActivity;

public final class GridAdapter extends RecyclerView.Adapter<GridAdapter.ImageItemViewHolder> {
    private MainActivity mAct;

    public GridAdapter(MainActivity activity) {
        this.mAct = activity;
    }

    @Override
    public ImageItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mAct).inflate(R.layout.image_grid_item, parent, false);
        return new ImageItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageItemViewHolder holder, int position) {
        String url = getItem(position);
        Glide.with(mAct.getApplicationContext())
                .load(url)
                .apply(new RequestOptions().error(R.drawable.error).placeholder(R.drawable.placeholder))
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAct.mApp.mAppModel.setCurrPosition(position);
                Intent intent = new Intent(mAct, FullScreenActivity.class);
                mAct.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAct.mApp.mAppModel.getImages().size();
    }

    public String getItem(int position) {
        return mAct.mApp.mAppModel.getImages().get(position).getPreviewURL();
    }

    public class ImageItemViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imageView;

        public ImageItemViewHolder(View view) {
            super(view);
            imageView = (ImageView) itemView;
        }
    }


}