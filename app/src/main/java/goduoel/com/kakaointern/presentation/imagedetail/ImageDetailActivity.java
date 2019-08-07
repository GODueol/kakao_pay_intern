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
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import io.reactivex.disposables.Disposable;

public class ImageDetailActivity extends BaseActivity<ActivityImageDetailBinding> {

    public static final String EXTRA_CURRENT_POSITION = "EXTRA_CURRENT_POSITION";

    private ImageDetailViewModel viewmodel;
    private ImageDetailViewPagerAdapter pagerAdapter;

    private Disposable disposable;

    private int enterPosition;
    private int exitPosition;
    private boolean isExit = false;

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
            enterPosition = extras.getInt(Constants.EXTRA_IMAGE_POSITION);
        }

        if (savedInstanceState != null) {
            int savePosition = savedInstanceState.getInt(Constants.EXTRA_IMAGE_POSITION, -1);
            enterPosition = savePosition != -1 ? savePosition : enterPosition;
        }

        registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        initViewModel();
        initView();
        initViewPager();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(Constants.EXTRA_IMAGE_POSITION, binding.viewpagerImageDetail.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        unregisterReceiver(downloadReceiver);
        super.onDestroy();
    }

    private void initView() {

        binding.overlayMenu.setOnDownListener(v ->
                hasStoragePermission(() -> {
                    String url = getCurrentGridITem().getImageUrl();
                    beginDownload(url);
                }));

        binding.overlayMenu.setOnShareListener(v ->
                hasStoragePermission(() -> {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    String url = getCurrentGridITem().getImageUrl();
                    disposable = ImageUtil.getViewBitmapUri(ImageDetailActivity.this, url)
                            .subscribe(uri -> {
                                intent.putExtra(Intent.EXTRA_STREAM, uri);
                                startActivity(Intent.createChooser(intent, getString(R.string.share)));
                            }, e -> Toast.makeText(ImageDetailActivity.this, getString(R.string.share_fail), Toast.LENGTH_SHORT).show());

                }));

        binding.overlayMenu.setOnSiteListener(v -> {
            String url = getCurrentGridITem().getDocUrl();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        binding.overlayMenu.setOnBackListner(v -> finishAfterTransition());
    }

    private void initViewPager() {
        pagerAdapter = new ImageDetailViewPagerAdapter(viewmodel, enterPosition);
        binding.viewpagerImageDetail.setAdapter(pagerAdapter);
        binding.viewpagerImageDetail.post(() -> binding.viewpagerImageDetail.setCurrentItem(enterPosition, false));
    }

    private void initViewModel() {
        ViewModelProvider.Factory factory = new ImageDetailViewModel.Factory(ImageRepository.getInstance());
        viewmodel = ViewModelProviders.of(this, factory).get(ImageDetailViewModel.class);
        binding.setImagedetailVm(viewmodel);
        observeLiveData();
    }

    private void observeLiveData() {
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
        setResult();
        super.finishAfterTransition();
    }

    private void setResult() {
        isExit = true;

        exitPosition = binding.viewpagerImageDetail.getCurrentItem();
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CURRENT_POSITION, binding.viewpagerImageDetail.getCurrentItem());
        setResult(Activity.RESULT_OK, intent);
    }

    private ImageDataResult.ImageDocument getCurrentGridITem() {
        return pagerAdapter.getCurrentList().get(binding.viewpagerImageDetail.getCurrentItem());
    }

    private void beginDownload(String url) {
        try {
            DownloadUtil.DownloadImageFIleToUrl(this, url);
        } catch (Exception e) {
            Toast.makeText(ImageDetailActivity.this, "다운로드 실패", Toast.LENGTH_SHORT).show();
        }
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
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
                        } else if (status == DownloadManager.STATUS_FAILED) {
                            Toast.makeText(ImageDetailActivity.this, "다운로드 실패", Toast.LENGTH_SHORT).show();
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
            if (isExit) {

                RecyclerView recyclerView = ((RecyclerView) binding.viewpagerImageDetail.getChildAt(0));
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(exitPosition);
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
