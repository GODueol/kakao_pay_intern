package goduoel.com.kakaointern.presentation.imagedetail;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import goduoel.com.kakaointern.R;
import goduoel.com.kakaointern.data.entity.ImageDataResult;
import goduoel.com.kakaointern.databinding.ItemViewpagerBinding;
import goduoel.com.kakaointern.presentation.listener.OnPagingScrollListener;

public class ImageDetailViewPagerAdapter extends RecyclerView.Adapter<ImageDetailViewPagerAdapter.ImageDetailViewPagerViewHolder> {

    private List<ImageDataResult.ImageDocument> itemList;
    private OnPagingScrollListener onPagingScrollListener;

    private ImageDetailViewModel viewModel;
    private int currentPosition;

    ImageDetailViewPagerAdapter(ImageDetailViewModel viewModel, int currentPosition, OnPagingScrollListener onPagingScrollListener) {
        this.viewModel = viewModel;
        this.currentPosition = currentPosition;
        this.onPagingScrollListener = onPagingScrollListener;
    }

    public void setItemList(List<ImageDataResult.ImageDocument> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ImageDetailViewPagerAdapter.ImageDetailViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_viewpager, parent, false);
        ImageDetailViewPagerViewHolder holder = new ImageDetailViewPagerViewHolder(itemView);
        holder.binding.setImagedetailVm(viewModel);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageDetailViewPagerAdapter.ImageDetailViewPagerViewHolder holder, int position) {

        if (position >= itemList.size()) {
            Glide.with(holder.binding.detailImage).load(R.drawable.img_load_image)
                    .fitCenter()
                    .apply(new RequestOptions().error(R.drawable.img_load_fail))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.binding.detailImage);
            onPagingScrollListener.onLoadMore();
            return;
        }

        ImageDataResult.ImageDocument item = itemList.get(position);
        ViewCompat.setTransitionName(holder.itemView, item.getImageUrl());

        Glide.with(holder.binding.detailImage)
                .load(item.getImageUrl())
                .fitCenter()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (position == currentPosition) {
                            ActivityCompat.startPostponedEnterTransition((Activity) holder.itemView.getContext());
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (position == currentPosition) {
                            ActivityCompat.startPostponedEnterTransition((Activity) holder.itemView.getContext());
                        }
                        return false;
                    }
                })
                .apply(new RequestOptions().error(R.drawable.img_load_fail))
                .placeholder(R.drawable.img_load_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.detailImage);
    }


    @Override
    public int getItemCount() {
        if (itemList == null)
            return 0;

        return itemList.size() + 1;
    }

    @Nullable
    ImageDataResult.ImageDocument getItem(int position) {
        if (itemList == null) {
            return null;
        } else if (itemList.size() <= position) {
            return null;
        }
        return itemList.get(position);
    }

    static class ImageDetailViewPagerViewHolder extends RecyclerView.ViewHolder {
        ItemViewpagerBinding binding;

        ImageDetailViewPagerViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            if (binding != null) {
                binding.detailImage.setOnClickListener(v -> Log.d("test", "누름"));
            }
        }

    }
}
