package goduoel.com.kakaointern.extension.databinding;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;

import goduoel.com.kakaointern.R;
import goduoel.com.kakaointern.data.entity.ImageDataResult;
import goduoel.com.kakaointern.presentation.imagedetail.ImageDetailViewPagerAdapter;

public class BindingAdapters {

    @SuppressWarnings("unchecked")
    @BindingAdapter({"items"})
    public static <T, VH extends RecyclerView.ViewHolder> void setItems(
            @NonNull final RecyclerView recyclerView,
            @Nullable final List<T> items) {
        final ListAdapter<T, VH> adapter = (ListAdapter<T, VH>) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.submitList(items == null ? null : new ArrayList<>(items));
        }
    }

    @BindingAdapter("items")
    public static void bindItem(ViewPager2 viewPager2, List<ImageDataResult.ImageDocument> items) {
        ImageDetailViewPagerAdapter adapter = (ImageDetailViewPagerAdapter) viewPager2.getAdapter();
        if (adapter != null) {
            adapter.setItemList(items);
        }
    }

    @BindingAdapter({"uri"})
    public static void loadUrlImage(AppCompatImageView imageView, String imageUri) {
        if (imageUri == null)
            return;

        Glide.with(imageView).load(imageUri)
                .fitCenter()
                .apply(new RequestOptions().error(R.drawable.img_load_fail))
                .placeholder(R.drawable.img_load_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }


    @BindingAdapter({"uri","radius"})
    public static void loadUrlImage(AppCompatImageView imageView, String imageUri,int radius) {
        if (imageUri == null)
            return;

        Glide.with(imageView).load(imageUri)
                .fitCenter()
                .apply(new RequestOptions().transform(new RoundedCorners(radius)).error(R.drawable.img_load_fail))
                .placeholder(R.drawable.img_load_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    @BindingAdapter({"uri", "thumbnail"})
    public static void loadUrlImage(AppCompatImageView imageView, String imageUri, Drawable thumbnailUri) {
        if (imageUri == null)
            return;

        Glide.with(imageView).load(imageUri)
                .apply(new RequestOptions().error(R.drawable.img_load_fail))
                .placeholder(thumbnailUri)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

}
