package goduoel.com.kakaointern.presentation.imagedetail;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import goduoel.com.kakaointern.data.entity.ImageDataResult;
import goduoel.com.kakaointern.data.entity.ImageRequestType;
import goduoel.com.kakaointern.data.entity.RequestHeader;
import goduoel.com.kakaointern.data.repository.ImageRepository;
import goduoel.com.kakaointern.presentation.BaseViewModel;

public class ImageDetailViewModel extends BaseViewModel {

    private ImageRepository repository;

    @NonNull
    private final MutableLiveData<List<ImageDataResult.ImageDocument>> imageDataList = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<Boolean> isEndData = new MutableLiveData<>();

    private int page;
    private String beforeQuery;

    private ImageDetailViewModel(ImageRepository repository) {
        this.repository = repository;

        initViewData();
        loadRepositoryData();
    }

    private void loadRepositoryData() {
        RequestHeader requestHeader = repository.loadRequestHeader();
        page = requestHeader.getPage();
        beforeQuery = requestHeader.getQuery();
        imageDataList.setValue(repository.loadImageList());
    }

    private void initViewData() {
        isLoading.setValue(false);
        isEndData.setValue(false);
    }

    @NonNull
    public LiveData<List<ImageDataResult.ImageDocument>> getImageDataList() {
        return imageDataList;
    }

    @NonNull
    LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    @NonNull
    LiveData<Boolean> getIsEndData() {
        return isEndData;
    }

    void getMoreImage() {

        if (isLoading.getValue() != null && isLoading.getValue()) {
            return;
        }

        page++;

        isLoading.setValue(true);

        addDisposable(
                repository.getImageList(beforeQuery, ImageRequestType.ACCURACY, page)
                        .subscribe(imageData -> {
                            Log.d("result", imageData.toString());
                            List<ImageDataResult.ImageDocument> loadImageDataList = imageDataList.getValue();
                            loadImageDataList.addAll(imageData.getDocuments());
                            imageDataList.setValue(loadImageDataList);
                            isEndData.setValue(imageData.getMeta().getIsEnd());
                            isLoading.setValue(false);
                        }, e -> {
                            Log.e("error", e.getMessage());
                            isLoading.setValue(false);
                        })
        );
    }

    void saveDataToRepository() {
        repository.saveRequestHeader(new RequestHeader(beforeQuery, ImageRequestType.ACCURACY, page));
        repository.saveImageList(imageDataList.getValue());
    }

    public static class Factory implements ViewModelProvider.Factory {

        @NonNull
        private final ImageRepository repository;

        Factory(@NonNull ImageRepository repository) {
            this.repository = repository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ImageDetailViewModel.class)) {
                //noinspection unchecked
                return (T) new ImageDetailViewModel(repository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
    }
}
