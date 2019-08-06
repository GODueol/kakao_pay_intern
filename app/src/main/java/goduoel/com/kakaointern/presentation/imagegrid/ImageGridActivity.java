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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Map;

import goduoel.com.kakaointern.R;
import goduoel.com.kakaointern.data.entity.ImageRequestType;
import goduoel.com.kakaointern.data.error.RetryException;
import goduoel.com.kakaointern.data.repository.ImageRepository;
import goduoel.com.kakaointern.databinding.ActivityMainGridBinding;
import goduoel.com.kakaointern.presentation.BaseActivity;
import goduoel.com.kakaointern.presentation.imagedetail.ImageDetailActivity;
import goduoel.com.kakaointern.utils.Constants;


public class ImageGridActivity extends BaseActivity<ActivityMainGridBinding> {

    private static final String EXTRA_FILTER_TYPE = "EXTRA_FILTER_TYPE";
    private ImageRequestType filterType = ImageRequestType.ACCURACY;
    private ImageRequestType selectedType = ImageRequestType.ACCURACY;
    private Bundle reenterState = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main_grid;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        initMenuView(menu);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(binding.toolbar);
        setExitSharedElementCallback(exitElementCallback);

        if (savedInstanceState != null) {
            filterType = (ImageRequestType) savedInstanceState.getSerializable(EXTRA_FILTER_TYPE);
        }
        initGridView();
        initViewModel();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // 검색 필터 저장
        outState.putSerializable(EXTRA_FILTER_TYPE, filterType);
        super.onSaveInstanceState(outState);
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
            syncRecyclerViewScroll(position);
        }
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
                    appBarExpand(false);
                    saveToPassData();
                    Intent intent = new Intent(this, ImageDetailActivity.class);
                    intent.putExtra(Constants.EXTRA_IMAGE_POSITION, position);
                    intent.putExtra(Constants.EXTRA_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(sharedElement));
                    String transitionName = ViewCompat.getTransitionName(sharedElement);

                    if (transitionName == null) {
                        return;
                    }

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this, sharedElement, transitionName);
                    startActivityForResult(intent, Constants.REQUEST_CURRENT_POSITION, options.toBundle());

                }, () -> binding.getImagegridVm().getMoreImage(false))
        );
    }

    private void initViewModel() {
        ViewModelProvider.Factory factory = new ImageGridViewModel.Factory(ImageRepository.getInstance());
        ImageGridViewModel viewmodel = ViewModelProviders.of(this, factory).get(ImageGridViewModel.class);
        binding.setImagegridVm(viewmodel);
        observeLiveData();
    }

    private void observeLiveData() {
        binding.getImagegridVm().getError().observe(this, e -> {
            if (e instanceof RetryException) {
                switch (((RetryException) e).getRetryType()) {
                    case RETRY_REQUEST:
                        Snackbar.make(binding.getRoot(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                        break;
                    case RETRY_FAIL:
                        Snackbar snackbar = Snackbar.make(binding.getRoot(), e.getMessage(), Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("Yes", view -> binding.getImagegridVm().getMoreImage(true));
                        snackbar.show();
                        break;
                }
                return;
            }
            Snackbar.make(binding.getRoot(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        });
        binding.getImagegridVm().getImageDataList().observe(this, imageDocuments -> {
        });

        binding.getImagegridVm().getIsLoading().observe(this, bool ->
                Log.e("test", "boolean : " + bool));

        binding.getImagegridVm().getIsEndData().observe(this, bool -> {
        });
    }

    private void initMenuView(Menu menu) {

        getMenuInflater().inflate(R.menu.image_search, menu);
        MenuItem searchMenu = menu.findItem(R.id.image_search);
        MenuItem searchFilter = menu.findItem(R.id.search_filter);
        searchMenu.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                searchFilter.setVisible(true);
                if (getSupportActionBar() != null) {
                    ColorDrawable whiteColor = new ColorDrawable(ContextCompat.getColor(ImageGridActivity.this, R.color.colorWhite));
                    getSupportActionBar().setBackgroundDrawable(whiteColor);
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchFilter.setVisible(false);
                if (getSupportActionBar() != null) {
                    ColorDrawable primaryColor = new ColorDrawable(ContextCompat.getColor(ImageGridActivity.this, R.color.colorPrimary));
                    getSupportActionBar().setBackgroundDrawable(primaryColor);
                }
                return true;
            }
        });

        searchFilter.setOnMenuItemClickListener(item -> {
            createFilterDialog();
            return false;
        });
        initSearchView(searchMenu);
    }

    private void createFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("검색 필터")
                .setSingleChoiceItems(getResources().getStringArray(R.array.filter_list), filterType.ordinal(), (dialog, which) -> {
                    switch (which) {
                        case 0:
                            selectedType = ImageRequestType.ACCURACY;
                            break;
                        case 1:
                            selectedType = ImageRequestType.RECENCY;
                            break;
                        default:
                            break;
                    }
                })
                .setPositiveButton("확인", (dialog, which) -> {
                    filterType = selectedType;
                    binding.getImagegridVm().setFilterOption(filterType);
                    Log.d("TAG", "which : " + which);
                }).setNegativeButton("취소", (dialog, which) -> {
                });
        AlertDialog dialog = builder.create();
        dialog.show();
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
                binding.getImagegridVm().getImage(query.trim(), false);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                appBarExpand(true);
                return true;
            }
        });
        searchMenu.expandActionView();
    }

    private void saveToPassData() {
        binding.getImagegridVm().saveDataToRepository();
    }

    private void appBarExpand(boolean expanded) {
        binding.appBar.setExpanded(expanded);
    }

    private void syncRecyclerViewScroll(int position) {
        postponeEnterTransition();

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
}


