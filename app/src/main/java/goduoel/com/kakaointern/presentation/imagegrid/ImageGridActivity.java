package goduoel.com.kakaointern.presentation.imagegrid;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import goduoel.com.kakaointern.R;
import goduoel.com.kakaointern.data.repository.ImageRepository;
import goduoel.com.kakaointern.databinding.ActivityMainGridBinding;
import goduoel.com.kakaointern.presentation.BaseActivity;
import goduoel.com.kakaointern.presentation.imagedetail.ImageDetailActivity;
import goduoel.com.kakaointern.utils.Constants;


public class ImageGridActivity extends BaseActivity<ActivityMainGridBinding> {

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

        initGridView();
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
                searchView.clearFocus();
//                KeyboardUtil.closeKeyboard(getBaseContext());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    private void initGridView() {

        int spanCount = 4;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 7;
        }

        binding.recyclerGirdIamge.setLayoutManager(new GridLayoutManager(this, spanCount));
        binding.recyclerGirdIamge.setAdapter(
                new ImageGridRecyclerViewAdapter((sharedImageView, position) -> {
                    saveToPassData();
                    Intent intent = new Intent(this, ImageDetailActivity.class);
                    intent.putExtra(Constants.EXTRA_IMAGE_POSITION, position);
                    startActivityForResult(intent, Constants.REQUEST_CURRENT_POSITION);
                }, () -> binding.getImagegridVm().getMoreImage())
        );
    }

    private void saveToPassData() {
        binding.getImagegridVm().saveDataToRepository();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }

        if (resultCode == BaseActivity.RESULT_OK) {
            int position = data.getIntExtra(ImageDetailActivity.EXTRA_CURRENT_POSITION, 0);
            binding.getImagegridVm().loadRepositoryData();
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


