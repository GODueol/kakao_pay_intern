package goduoel.com.kakaointern.presentation.imagegrid;

import android.app.SharedElementCallback;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import goduoel.com.kakaointern.R;
import goduoel.com.kakaointern.data.repository.ImageRepository;
import goduoel.com.kakaointern.databinding.ActivityMainGridBinding;
import goduoel.com.kakaointern.presentation.BaseActivity;
import goduoel.com.kakaointern.presentation.imagedetail.ImageDetailActivity;
import goduoel.com.kakaointern.utils.Constants;


public class ImageGridActivity extends BaseActivity<ActivityMainGridBinding> {
    private Bundle reenterState = null;

    private SharedElementCallback exitElementCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            super.onMapSharedElements(names, sharedElements);
            postponeEnterTransition();

            if (reenterState != null) {
                int position = reenterState.getInt(ImageDetailActivity.EXTRA_CURRENT_POSITION);
                final RecyclerView.ViewHolder viewHolder = binding.recyclerGirdIamge.findViewHolderForAdapterPosition(position);
                reenterState = null;
                
                if (viewHolder == null) {
                    return;
                }

                View newElement = viewHolder.itemView;
                String newTransitionName = ViewCompat.getTransitionName(newElement);

                if (newTransitionName == null) {
                    return;

                }
                names.clear();
                names.add(newTransitionName);

                sharedElements.clear();
                sharedElements.put(newTransitionName, newElement);
            }
        }
    };

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(binding.toolbar);
        setExitSharedElementCallback(exitElementCallback);

        initGridView();
        initViewModel();
    }


    private void initMenuView(Menu menu) {
        MenuItem searchMenu = menu.findItem(R.id.image_search);
        searchMenu.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if (getSupportActionBar() != null) {
                    ColorDrawable whiteColor = new ColorDrawable(ContextCompat.getColor(ImageGridActivity.this, R.color.colorWhite));
                    getSupportActionBar().setBackgroundDrawable(whiteColor);
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (getSupportActionBar() != null) {
                    ColorDrawable primaryColor = new ColorDrawable(ContextCompat.getColor(ImageGridActivity.this, R.color.colorPrimary));
                    getSupportActionBar().setBackgroundDrawable(primaryColor);
                }
                return true;
            }
        });

        initSearchView(searchMenu);
    }

    private void initSearchView(MenuItem searchMenu) {
        SearchView searchView = (SearchView) searchMenu.getActionView();
        searchView.setQueryHint(getString(R.string.image_search_hint));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        ImageView searchImageView = searchView.findViewById(androidx.appcompat.R.id.search_button);
        searchImageView.setImageResource(R.drawable.btn_search);

        View underline = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        underline.setBackgroundColor(Color.TRANSPARENT);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                binding.getImagegridVm().getImage(query.trim());
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                binding.appBar.setExpanded(true);
                return true;
            }
        });
    }


    private void initGridView() {

        int spanCount = 4;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 7;
        }

        binding.recyclerGirdIamge.setLayoutManager(new GridLayoutManager(this, spanCount));
        binding.recyclerGirdIamge.addItemDecoration(new GridEqualSpacingItemDecoration(spanCount, 4));
        binding.recyclerGirdIamge.setAdapter(
                new ImageGridRecyclerViewAdapter((sharedElement, position) -> {
                    binding.appBar.setExpanded(false);
                    overridePendingTransition(0, 0);
                    saveToPassData();
                    Intent intent = new Intent(this, ImageDetailActivity.class);
                    intent.putExtra(Constants.EXTRA_IMAGE_POSITION, position);
                    intent.putExtra(Constants.EXTRA_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(sharedElement));
                    String transitionName = ViewCompat.getTransitionName(sharedElement);

                    if (transitionName == null) {
                        return;
                    }

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this,
                            sharedElement,
                            transitionName);
                    startActivityForResult(intent, Constants.REQUEST_CURRENT_POSITION, options.toBundle());

                }, () -> binding.getImagegridVm().getMoreImage())
        );
    }

    private void saveToPassData() {
        binding.getImagegridVm().saveDataToRepository();
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        if (data.getExtras() == null) {
            return;
        }

        if (resultCode == BaseActivity.RESULT_OK) {
            reenterState = new Bundle(data.getExtras());
            int position = data.getIntExtra(ImageDetailActivity.EXTRA_CURRENT_POSITION, 0);
            binding.getImagegridVm().loadRepositoryData();

            postponeEnterTransition();
            syncRecyclerViewScroll(position);
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
            binding.recyclerGirdIamge.postDelayed(ImageGridActivity.this::startPostponedEnterTransition, 100);
        } else {
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


