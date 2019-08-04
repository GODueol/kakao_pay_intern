package goduoel.com.kakaointern.presentation.imagemenu;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import goduoel.com.kakaointern.R;
import goduoel.com.kakaointern.databinding.ImageMenuFragmentBinding;
import goduoel.com.kakaointern.presentation.BaseFragment;

public class ImageMenuFragment extends BaseFragment<ImageMenuFragmentBinding> {

    private ImageMenuViewModel mViewModel;

    public ImageMenuFragment() {
    }

    OnImageMenuListener onHandleCurrentImage;

    public interface OnImageMenuListener {
        void onShared();

        void onStie();

        void onDown();
    }

    public void setOnImageMenuListener(OnImageMenuListener onHandleCurrentImage) {
        this.onHandleCurrentImage = onHandleCurrentImage;
    }

    public static ImageMenuFragment newInstance() {
        return new ImageMenuFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.image_menu_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        initViewModel();
    }

    private void initView() {
        binding.btnShare.setOnClickListener(v -> {
            onHandleCurrentImage.onShared();
        });

        binding.btnSite.setOnClickListener(v -> {
            onHandleCurrentImage.onStie();
        });

        binding.btnDownload.setOnClickListener(v -> {
            onHandleCurrentImage.onDown();
        });
    }

    private void initViewModel() {
        ImageMenuViewModel viewModel = ViewModelProviders.of(this).get(ImageMenuViewModel.class);
    }
}
