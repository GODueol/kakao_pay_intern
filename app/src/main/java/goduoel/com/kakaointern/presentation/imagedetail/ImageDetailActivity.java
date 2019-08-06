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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Map;

import goduoel.com.kakaointern.R;
import goduoel.com.kakaointern.data.entity.ImageDataResult;
import goduoel.com.kakaointern.data.error.RetryException;
import goduoel.com.kakaointern.data.repository.ImageRepository;
import goduoel.com.kakaointern.databinding.ActivityImageDetailBinding;
import goduoel.com.kakaointern.presentation.BaseActivity;
import goduoel.com.kakaointern.utils.Constants;
import goduoel.com.kakaointern.utils.DownloadUtil;
import goduoel.com.kakaointern.utils.ImageUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class ImageDetailActivity extends BaseActivity<ActivityImageDetailBinding> {

    public static final String EXTRA_CURRENT_POSITION = "EXTRA_CURRENT_POSITION";
    private int imageDocumentPosition;
    ImageDetailViewModel viewmodel;

    private Disposable disposable;
    private int newPosition;
    private boolean isReturning = false;

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
        if (savedInstanceState != null) {
            int savePosition = savedInstanceState.getInt(Constants.EXTRA_IMAGE_POSITION, -1);
            imageDocumentPosition = savePosition != -1 ? savePosition : imageDocumentPosition;
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
            if (getCurrentGridITem() == null) {
                return;
            }
            String url = getCurrentGridITem().getImageUrl();
            beginDownload(url);
        });

        binding.overlayMenu.setOnShareListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            if (getCurrentGridITem() == null) {
                return;
            }
            String url = getCurrentGridITem().getImageUrl();
            disposable = ImageUtil.getViewBitmapUri(this, url)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(uri -> {
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(intent, getString(R.string.share)));
                    }, e -> Toast.makeText(this, getString(R.string.share_fail), Toast.LENGTH_SHORT).show());

        });

        binding.overlayMenu.setOnSiteListener(v -> {
            if (getCurrentGridITem() == null) {
                return;
            }
            String url = getCurrentGridITem().getDocUrl();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        binding.overlayMenu.setOnBackListner(v -> finishAfterTransition());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // 화면 전환 페이지 저장
        outState.putInt(Constants.EXTRA_IMAGE_POSITION, binding.viewpagerImageDetail.getCurrentItem());
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        unregisterReceiver(onDownloadComplete);
        super.onDestroy();
    }

    private void initViewPager() {
        binding.viewpagerImageDetail.setAdapter(new ImageDetailViewPagerAdapter(viewmodel, imageDocumentPosition, () -> binding.getImagedetailVm().getMoreImage(false)));
        binding.viewpagerImageDetail.post(() -> binding.viewpagerImageDetail.setCurrentItem(imageDocumentPosition, false));
    }

    private void initViewModel() {
        ViewModelProvider.Factory factory = new ImageDetailViewModel.Factory(ImageRepository.getInstance());
        viewmodel = ViewModelProviders.of(this, factory).get(ImageDetailViewModel.class);
        binding.setImagedetailVm(viewmodel);
        observeLiveData();
    }

    private void observeLiveData() {

        binding.getImagedetailVm().getError().observe(this, e -> {
            if (e instanceof RetryException) {
                switch (((RetryException) e).getRetryType()) {
                    case RETRY_REQUEST:
                        Snackbar.make(binding.getRoot(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                        break;
                    case RETRY_FAIL:
                        Snackbar snackbar = Snackbar.make(binding.getRoot(), e.getMessage(), Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("Yes", view -> binding.getImagedetailVm().getMoreImage(true));
                        snackbar.show();
                        break;
                }
                return;
            }
            Snackbar.make(binding.getRoot(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        });

        binding.getImagedetailVm().getImageDataList().observe(this, imageDocuments -> Log.e(TAG, imageDocuments.toString()));

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
        return imageDetailViewPagerAdapter != null ? imageDetailViewPagerAdapter.getCurrentList().get(binding.viewpagerImageDetail.getCurrentItem()) : null;
    }

    private void beginDownload(String url) {
        DownloadUtil.DownloadImageFIleToUrl(this, url);
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {

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

    private SharedElementCallback enterElementCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            super.onMapSharedElements(names, sharedElements);
            if (isReturning) {

                RecyclerView recyclerView = ((RecyclerView) binding.viewpagerImageDetail.getChildAt(0));
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(newPosition);
                if (viewHolder == null) {
                    return;
                }
                View sharedElement = viewHolder.itemView;
                String transitionName = ViewCompat.getTransitionName(sharedElement);
                if (transitionName == null) {
                    return;
                }
                names.clear();
                names.add(transitionName);

                sharedElements.clear();
                sharedElements.put(transitionName, sharedElement);
            }

        }
    };


}
