package goduoel.com.kakaointern.presentation.imagemenu;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.View;

import goduoel.com.kakaointern.R;
import goduoel.com.kakaointern.data.entity.ImageDataResult;
import goduoel.com.kakaointern.databinding.ImageMenuFragmentBinding;
import goduoel.com.kakaointern.presentation.BaseFragment;
import goduoel.com.kakaointern.presentation.imagedetail.ImageDetailActivity;

public class ImageMenuFragment extends BaseFragment<ImageMenuFragmentBinding> {

    private ImageMenuViewModel mViewModel;

    public ImageMenuFragment() {
    }

    OnImageMenuListener onHandleCurrentImage;

    public interface OnImageMenuListener {
        void onShared();

        void onStie();
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

        binding.btnSite.setOnClickListener(v ->{
            onHandleCurrentImage.onStie();
        });
    }

    private void initViewModel() {
        ImageMenuViewModel viewModel = ViewModelProviders.of(this).get(ImageMenuViewModel.class);
    }
}
