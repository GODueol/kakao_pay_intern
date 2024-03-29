package goduoel.com.kakaointern.extension.databinding;

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

import java.util.ArrayList;
import java.util.List;

import goduoel.com.kakaointern.R;
import goduoel.com.kakaointern.utils.UnitUtil;

public class BindingAdapters {

    @SuppressWarnings("unchecked")
    @BindingAdapter({"items"})
    public static <T, VH extends RecyclerView.ViewHolder> void setRecyclerViewItems(
            @NonNull final RecyclerView recyclerView,
            @Nullable final List<T> items) {
        final ListAdapter<T, VH> adapter = (ListAdapter<T, VH>) recyclerView.getAdapter();
        setItems(adapter, items);
    }

    @SuppressWarnings("unchecked")
    @BindingAdapter({"items"})
    public static <T, VH extends RecyclerView.ViewHolder> void setViewpagerItems(
            @NonNull final ViewPager2 viewPager2,
            @Nullable final List<T> items) {
        final ListAdapter<T, VH> adapter = (ListAdapter<T, VH>) viewPager2.getAdapter();
        setItems(adapter, items);
    }

    private static <T, VH extends RecyclerView.ViewHolder> void setItems(
            @Nullable ListAdapter<T, VH> adapter,
            @Nullable List<T> items) {
        if (adapter != null) {
            adapter.submitList(items == null ? null : new ArrayList<>(items));
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
                .into(imageView);
    }


    @BindingAdapter({"uri", "radius"})
    public static void loadUrlImage(AppCompatImageView imageView, String imageUri, int radius) {
        if (imageUri == null)
            return;
        int dpRadius = UnitUtil.convertDpToPixel(imageView.getContext(), radius);
        Glide.with(imageView).load(imageUri)
                .fitCenter()
                .apply(new RequestOptions().transform(new RoundedCorners(dpRadius)).error(R.drawable.img_load_fail))
                .placeholder(R.drawable.ic_loading)
                .into(imageView);
    }
}
