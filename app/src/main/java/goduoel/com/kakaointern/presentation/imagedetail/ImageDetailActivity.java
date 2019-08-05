package goduoel.com.kakaointern.presentation.imagedetail;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.SharedElementCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import goduoel.com.kakaointern.R;
import goduoel.com.kakaointern.data.entity.ImageDataResult;
import goduoel.com.kakaointern.data.repository.ImageRepository;
import goduoel.com.kakaointern.databinding.ActivityImageDetailBinding;
import goduoel.com.kakaointern.presentation.BaseActivity;
import goduoel.com.kakaointern.utils.Constants;
import goduoel.com.kakaointern.utils.DownloadUtil;
import goduoel.com.kakaointern.utils.ImageUtil;

public class ImageDetailActivity extends BaseActivity<ActivityImageDetailBinding> {

    public static final String EXTRA_CURRENT_POSITION = "EXTRA_CURRENT_POSITION";
    private int imageDocumentPosition;
    ImageDetailViewModel viewmodel;

    private int newPosition;
    private boolean isReturning = false;

    private SharedElementCallback enterElementCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            super.onMapSharedElements(names, sharedElements);
            if (isReturning) {

                View sharedElement = ((RecyclerView) binding.viewpagerImageDetail.getChildAt(0)).findViewHolderForAdapterPosition(newPosition).itemView;
                names.clear();
                names.add(ViewCompat.getTransitionName(sharedElement));

                sharedElements.clear();
                sharedElements.put(ViewCompat.getTransitionName(sharedElement), sharedElement);
            }

        }
    };

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_image_detail;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();

        postponeEnterTransition();
        setEnterSharedElementCallback(enterElementCallback);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            imageDocumentPosition = extras.getInt(Constants.EXTRA_IMAGE_POSITION);
        }
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        initViewModel();
        initView();
        initViewPager();
    }

    private void hideStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initView() {

        binding.overlayMenu.setOnDownListener(v -> {
            String url = getCurrentGridITem().getImageUrl();
            beginDownload(url);
        });

        binding.overlayMenu.setOnShareListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            AppCompatImageView currentView = ((RecyclerView) binding.viewpagerImageDetail.getChildAt(0)).getLayoutManager().findViewByPosition(binding.viewpagerImageDetail.getCurrentItem()).findViewById(R.id.detail_image);
            Uri bmpUri = ImageUtil.getViewBitmapUri(currentView);
            intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            startActivity(Intent.createChooser(intent, getString(R.string.share)));
        });

        binding.overlayMenu.setOnSiteListener(v -> {
            String url = getCurrentGridITem().getDocUrl();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        binding.overlayMenu.setOnBackListner(v -> finishAfterTransition());
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(onDownloadComplete);
        super.onDestroy();
    }

    private void initViewPager() {
        binding.viewpagerImageDetail.setPageTransformer((page, position) -> binding.overlayMenu.enableButtons(false));
        binding.viewpagerImageDetail.setAdapter(new ImageDetailViewPagerAdapter(viewmodel, imageDocumentPosition, () -> binding.getImagedetailVm().getMoreImage()));
        binding.viewpagerImageDetail.post(() -> binding.viewpagerImageDetail.setCurrentItem(imageDocumentPosition, false));
    }

    private void initViewModel() {
        ViewModelProvider.Factory factory = new ImageDetailViewModel.Factory(ImageRepository.getInstance());
        viewmodel = ViewModelProviders.of(this, factory).get(ImageDetailViewModel.class);
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

        binding.getImagedetailVm().getMenuShowAndHide().observe(this, showMenu -> {
            if (showMenu) {
                binding.overlayMenu.setLayoutShow(true);
            } else {
                binding.overlayMenu.setLayoutShow(false);
            }

        });
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }

    @Override
    public void finishAfterTransition() {
        saveToPassData();
        setResult();
        super.finishAfterTransition();
    }

    private void setResult() {
        isReturning = true;

        newPosition = binding.viewpagerImageDetail.getCurrentItem();
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CURRENT_POSITION, binding.viewpagerImageDetail.getCurrentItem());
        setResult(Activity.RESULT_OK, intent);
    }

    private void saveToPassData() {
        binding.getImagedetailVm().saveDataToRepository();
    }


    private ImageDataResult.ImageDocument getCurrentGridITem() {
        ImageDetailViewPagerAdapter imageDetailViewPagerAdapter =
                ((ImageDetailViewPagerAdapter) binding.viewpagerImageDetail.getAdapter());
        return imageDetailViewPagerAdapter.getItem(binding.viewpagerImageDetail.getCurrentItem());
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {

                DownloadManager.Query query = new DownloadManager.Query();

                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);

                query.setFilterById(downloadId);
                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

                Cursor cursor = manager.query(query);
                if (cursor.moveToFirst()) {
                    if (cursor.getCount() > 0) {
                        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            Toast.makeText(ImageDetailActivity.this, "다운로드 완료", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                cursor.close();

            }
        }
    };

    private void beginDownload(String url) {
        DownloadUtil.DownloadImageFIleToUrl(this, url);
    }

}
