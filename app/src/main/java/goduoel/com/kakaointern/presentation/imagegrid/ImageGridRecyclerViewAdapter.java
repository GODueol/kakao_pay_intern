package goduoel.com.kakaointern.presentation.imagegrid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import goduoel.com.kakaointern.R;
import goduoel.com.kakaointern.data.entity.ImageDataResult;
import goduoel.com.kakaointern.databinding.ItemGridBinding;
import goduoel.com.kakaointern.presentation.listener.OnPagingScrollListener;
import goduoel.com.kakaointern.presentation.listener.OnItemClickListener;

public class ImageGridRecyclerViewAdapter extends ListAdapter<ImageDataResult.ImageDocument, ImageGridRecyclerViewAdapter.ImageGridAdapterViewHolder> {


    private OnItemClickListener<Integer> onItemClickListener;
    private OnPagingScrollListener onPagingScrollListener;

    ImageGridRecyclerViewAdapter(OnItemClickListener<Integer> onItemClickListener, OnPagingScrollListener onPagingScrollListener) {
        super(DIFF_CALLBACK);
        this.onItemClickListener = onItemClickListener;
        this.onPagingScrollListener = onPagingScrollListener;
    }

    @NonNull
    @Override
    public ImageGridAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid, parent, false);
        ImageGridAdapterViewHolder holder = new ImageGridAdapterViewHolder(itemView);
        holder.binding.getRoot().setOnClickListener(view -> onItemClickListener.onItemClick(view, holder.getAdapterPosition()));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageGridAdapterViewHolder holder, int position) {

        holder.binding.thumbnailImage.setImageResource(0);
        holder.binding.thumbnailImage.setImageDrawable(null);
        holder.binding.thumbnailImage.setImageURI(null);

        ImageDataResult.ImageDocument item = getItem(position);
        holder.binding.setUrl(item.getThumbnailUrl());
        if (ViewCompat.getTransitionName(holder.binding.thumbnailImage) == null) {
            ViewCompat.setTransitionName(holder.binding.thumbnailImage, item.getImageUrl());
        }

        if (position == getItemCount() - 5) {
            onPagingScrollListener.onLoadMore();
        }
    }

    private static DiffUtil.ItemCallback<ImageDataResult.ImageDocument> DIFF_CALLBACK = new DiffUtil.ItemCallback<ImageDataResult.ImageDocument>() {

        @Override
        public boolean areItemsTheSame(@NonNull ImageDataResult.ImageDocument oldItem, @NonNull ImageDataResult.ImageDocument newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull ImageDataResult.ImageDocument oldItem, @NonNull ImageDataResult.ImageDocument newItem) {
            return oldItem.getDocUrl().equals(newItem.getDocUrl());
        }
    };


    static class ImageGridAdapterViewHolder extends RecyclerView.ViewHolder {

        ItemGridBinding binding;

        ImageGridAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
