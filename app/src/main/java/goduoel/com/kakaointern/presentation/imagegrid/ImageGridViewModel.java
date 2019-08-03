package goduoel.com.kakaointern.presentation.imagegrid;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import goduoel.com.kakaointern.data.entity.ImageDataResult;
import goduoel.com.kakaointern.data.entity.ImageRequestType;
import goduoel.com.kakaointern.data.entity.RequestHeader;
import goduoel.com.kakaointern.data.repository.ImageRepository;
import goduoel.com.kakaointern.presentation.BaseViewModel;

public class ImageGridViewModel extends BaseViewModel {

    private ImageRepository repository;

    @NonNull
    private final MutableLiveData<List<ImageDataResult.ImageDocument>> imageDataList = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<Boolean> isEndData = new MutableLiveData<>();


    private int page = 1;
    private String beforeQuery = "";

    private ImageGridViewModel(ImageRepository repository) {
        this.repository = repository;
        initViewData();
    }

    private void initViewData() {
        isLoading.setValue(false);
        isEndData.setValue(false);
        imageDataList.setValue(new ArrayList<>());
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

    public void loadRepositoryData() {
        RequestHeader requestHeader = repository.loadRequestHeader();
        page = requestHeader.getPage();
        beforeQuery = requestHeader.getQuery();
        imageDataList.setValue(repository.loadImageList());
    }

    void getImage(String query) {

        // 쿼리가 비어있거나, 이전과 같으면 무시
        if (TextUtils.isEmpty(query) || beforeQuery.equals(query)) {
            return;
        }

        // 검색어가 달라진경우
        List<ImageDataResult.ImageDocument> loadImageDataList = imageDataList.getValue();

        if (loadImageDataList == null) {
            return;
        }

        page = 1;
        loadImageDataList.clear();
        imageDataList.setValue(loadImageDataList);

        beforeQuery = query;


        loadImage(query);
    }


    void getMoreImage() {

        // 로딩중이면
        if (isLoading.getValue() != null && isLoading.getValue()) {
            return;
        }

        // 데이터의 끝이면리턴
        if (isEndData.getValue() != null && isEndData.getValue()) {
            return;
        }

        page++;
        loadImage(beforeQuery);
    }

    private void loadImage(String query) {

        List<ImageDataResult.ImageDocument> loadImageDataList = imageDataList.getValue();

        if (loadImageDataList == null) {
            return;
        }

        isLoading.setValue(true);

        addDisposable(
                repository.getImageList(query, ImageRequestType.ACCURACY, page)
                        .subscribe(imageData -> {
                            Log.d("result", imageData.toString());
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
            if (modelClass.isAssignableFrom(ImageGridViewModel.class)) {
                //noinspection unchecked
                return (T) new ImageGridViewModel(repository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
    }
}
