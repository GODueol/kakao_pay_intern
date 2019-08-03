package goduoel.com.kakaointern.presentation.imagedetail;

import android.app.Activity;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import goduoel.com.kakaointern.R;
import goduoel.com.kakaointern.data.repository.ImageRepository;
import goduoel.com.kakaointern.databinding.ActivityImageDetailBinding;
import goduoel.com.kakaointern.presentation.BaseActivity;
import goduoel.com.kakaointern.presentation.imagegrid.ImageGridActivity;

public class ImageDetailActivity extends BaseActivity<ActivityImageDetailBinding> {

    public static final String EXTRA_CURRENT_POSITION = "EXTRA_CURRENT_POSITION";
    private int imageDocumentPosition;
    private String imageTransitionName;
    private int newPosition;
    private boolean isReturning = false;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_image_detail;
    }

    private SharedElementCallback enterElementCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            super.onMapSharedElements(names, sharedElements);
            if (isReturning) {
                View sharedElement = ((RecyclerView) binding.viewpagerImageDetail.getChildAt(0)).getLayoutManager().findViewByPosition(newPosition);
                names.clear();
                names.add(ViewCompat.getTransitionName(sharedElement));

                sharedElements.clear();
                sharedElements.put(ViewCompat.getTransitionName(sharedElement), sharedElement);
            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        setEnterSharedElementCallback(enterElementCallback);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            imageDocumentPosition = extras.getInt(ImageGridActivity.EXTRA_IMAGE_POSITION);
            imageTransitionName = extras.getString(ImageGridActivity.EXTRA_IMAGE_TRANSITION_NAME);
        }

        initViewModel();
        initViewPager();
    }

    private void initViewPager() {
        binding.viewpagerImageDetail.setAdapter(new ImageDetailViewPagerAdapter(() -> binding.getImagedetailVm().getMoreImage()));
        binding.viewpagerImageDetail.post(() -> {
            binding.viewpagerImageDetail.setCurrentItem(imageDocumentPosition, false);
            binding.viewpagerImageDetail.post(this::startPostponedEnterTransition);
        });

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

        finishAfterTransition();
    }

    @Override
    public void finishAfterTransition() {
        setResult();
        super.finishAfterTransition();
    }

    private void setResult() {
        binding.getImagedetailVm().saveDataToRepository();
        isReturning = true;

        newPosition = binding.viewpagerImageDetail.getCurrentItem();
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CURRENT_POSITION, binding.viewpagerImageDetail.getCurrentItem());
        setResult(Activity.RESULT_OK, intent);
    }
    /*
    private void initView() {
        // 로딩 전 깜빡임 처리
        postponeEnterTransition();

        Glide.with(binding.imgDetail)
                .load(imageDocumentPosition.getImageUrl())
                .fitCenter()
                .listener(postPoneListener)
                .apply(new RequestOptions().error(R.drawable.img_load_fail))
                .listener(postPoneListener)
                .into(binding.imgDetail);

        Log.d(TAG, imageDocumentPosition.toString());
    }

    private RequestListener<Drawable> postPoneListener = new RequestListener<Drawable>() {

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            startPostponedEnterTransition();
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            startPostponedEnterTransition();
            return false;
        }
    };*/
}
