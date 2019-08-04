package goduoel.com.kakaointern.presentation.imagedetail;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import goduoel.com.kakaointern.R;
import goduoel.com.kakaointern.data.repository.ImageRepository;
import goduoel.com.kakaointern.databinding.ActivityImageDetailBinding;
import goduoel.com.kakaointern.presentation.BaseActivity;
import goduoel.com.kakaointern.presentation.imagegrid.ImageGridActivity;
import goduoel.com.kakaointern.presentation.imagemenu.ImageMenuFragment;
import goduoel.com.kakaointern.utils.ImageUtil;

public class ImageDetailActivity extends BaseActivity<ActivityImageDetailBinding> implements ImageMenuFragment.OnImageMenuListener {

    public static final String EXTRA_CURRENT_POSITION = "EXTRA_CURRENT_POSITION";
    private int imageDocumentPosition;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_image_detail;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            imageDocumentPosition = extras.getInt(ImageGridActivity.EXTRA_IMAGE_POSITION);
        }

        initViewModel();
        initViewPager();
        initFragment();
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof ImageMenuFragment) {
            ImageMenuFragment headlinesFragment = (ImageMenuFragment) fragment;
            headlinesFragment.setOnImageMenuListener(this);
        }
    }

    private void initFragment() {
        Fragment menuFragment = ImageMenuFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_menu, menuFragment)
                .commitAllowingStateLoss();
    }

    private void initViewPager() {
        binding.viewpagerImageDetail.setAdapter(new ImageDetailViewPagerAdapter(() -> binding.getImagedetailVm().getMoreImage()));
        binding.viewpagerImageDetail.post(() -> binding.viewpagerImageDetail.setCurrentItem(imageDocumentPosition, false));

    }

    private void initViewModel() {
        ViewModelProvider.Factory factory = new ImageDetailViewModel.Factory(ImageRepository.getInstance());
        ImageDetailViewModel viewmodel = ViewModelProviders.of(this, factory).get(ImageDetailViewModel.class);
        binding.setImagedetailVm(viewmodel);
        observeLiveData();
    }

    private void observeLiveData() {
        binding.getImagedetailVm().getImageDataList().observe(this, imageDocuments -> {
            Log.e(TAG, imageDocuments.toString());
        });

        binding.getImagedetailVm().getIsLoading().observe(this, bool ->
                Log.e("test", "boolean : " + bool));

        binding.getImagedetailVm().getIsEndData().observe(this, bool -> {
        });

    }

    @Override
    public void onBackPressed() {
        saveToPassData();
        setResult();
        finish();
    }

    private void setResult() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CURRENT_POSITION, binding.viewpagerImageDetail.getCurrentItem());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void saveToPassData() {
        binding.getImagedetailVm().saveDataToRepository();
    }

    @Override
    public void onShared() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        AppCompatImageView currentView = ((RecyclerView) binding.viewpagerImageDetail.getChildAt(0)).getLayoutManager().findViewByPosition(binding.viewpagerImageDetail.getCurrentItem()).findViewById(R.id.detail_image);

        Uri bmpUri = ImageUtil.getViewBitmapUri(currentView);
        intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }

    @Override
    public void onStie() {
        String url = ((ImageDetailViewPagerAdapter) binding.viewpagerImageDetail.getAdapter()).getItem(binding.viewpagerImageDetail.getCurrentItem()).getDocUrl();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

}
