package goduoel.com.kakaointern.presentation.imagegrid;

import android.app.Activity;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import goduoel.com.kakaointern.R;
import goduoel.com.kakaointern.data.repository.ImageRepository;
import goduoel.com.kakaointern.databinding.ActivityMainGridBinding;
import goduoel.com.kakaointern.presentation.BaseActivity;
import goduoel.com.kakaointern.presentation.imagedetail.ImageDetailActivity;
import goduoel.com.kakaointern.utils.KeyboardUtil;


public class ImageGridActivity extends BaseActivity<ActivityMainGridBinding> {
    public static final String EXTRA_IMAGE_POSITION = "EXTRA_IMAGE_POSITION";
    public static final String EXTRA_IMAGE_TRANSITION_NAME = "EXTRA_IMAGE_TRANSITION_NAME";
    public static final int REQUEST_CURRENT_POSITION = 1234;

    private Bundle reenterState = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main_grid;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.image_search, menu);
        initMenuView(menu);
        return true;
    }


    private SharedElementCallback exitElementCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            super.onMapSharedElements(names, sharedElements);
            postponeEnterTransition();

            if (reenterState != null) {
                int position = reenterState.getInt(ImageDetailActivity.EXTRA_CURRENT_POSITION);
                final RecyclerView.LayoutManager layoutManager = binding.recyclerGirdIamge.getLayoutManager();

                if (layoutManager == null) {
                    return;
                }

                View newElement = layoutManager.findViewByPosition(position);
                String newTransitionName = ViewCompat.getTransitionName(newElement);
                
                if (newElement != null) {
                    names.clear();
                    names.add(newTransitionName);

                    sharedElements.clear();
                    sharedElements.put(newTransitionName, newElement);
                }
                reenterState = null;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExitSharedElementCallback(exitElementCallback);
        initView();
        initViewModel();
    }

    private void initMenuView(Menu menu) {
        MenuItem searchMenu = menu.findItem(R.id.image_search);
        SearchView searchView = (SearchView) searchMenu.getActionView();

        searchView.setQueryHint(getString(R.string.image_search_hint));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                binding.getImagegridVm().getImage(query);
                KeyboardUtil.closeKeyboard(getBaseContext());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    private void initView() {
        setSupportActionBar(binding.toolbar);

        binding.recyclerGirdIamge.setAdapter(
                new ImageGridRecyclerViewAdapter((sharedImageView, position) -> {
                    binding.getImagegridVm().saveDataToRepository();
                    Intent intent = new Intent(this, ImageDetailActivity.class);
                    intent.putExtra(EXTRA_IMAGE_POSITION, position);
                    intent.putExtra(EXTRA_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(sharedImageView));
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this,
                            sharedImageView,
                            ViewCompat.getTransitionName(sharedImageView));
                    startActivityForResult(intent, REQUEST_CURRENT_POSITION, options.toBundle());
                }, () -> binding.getImagegridVm().getMoreImage())
        );
    }


    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        if (data == null) {
            return;
        }
        reenterState = new Bundle(data.getExtras());
        if (resultCode == BaseActivity.RESULT_OK) {
            int position = data.getIntExtra(ImageDetailActivity.EXTRA_CURRENT_POSITION, 0);
            binding.getImagegridVm().loadRepositoryData();
            syncRecyclerViewScroll(position);

            postponeEnterTransition();
            binding.recyclerGirdIamge.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    binding.recyclerGirdIamge.getViewTreeObserver().removeOnPreDrawListener(this);
                    startPostponedEnterTransition();
                    return true;
                }
            });
        }
    }

    private void syncRecyclerViewScroll(int position) {
        final RecyclerView.LayoutManager layoutManager = binding.recyclerGirdIamge.getLayoutManager();

        if (layoutManager == null) {
            return;
        }

        View viewAtPosition = layoutManager.findViewByPosition(position);

        if (viewAtPosition == null || layoutManager.isViewPartiallyVisible(viewAtPosition, false, true)) {
            binding.recyclerGirdIamge.post(() -> layoutManager.scrollToPosition(position));
        }
    }

    private void initViewModel() {
        ViewModelProvider.Factory factory = new ImageGridViewModel.Factory(ImageRepository.getInstance());
        ImageGridViewModel viewmodel = ViewModelProviders.of(this, factory).get(ImageGridViewModel.class);
        binding.setImagegridVm(viewmodel);
        observeLiveData();
    }

    private void observeLiveData() {
        binding.getImagegridVm().getImageDataList().observe(this, imageDocuments -> {
        });

        binding.getImagegridVm().getIsLoading().observe(this, bool ->
                Log.e("test", "boolean : " + bool));

        binding.getImagegridVm().getIsEndData().observe(this, bool -> {
        });
    }

}


