package goduoel.com.kakaointern.presentation.imagedetail;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import goduoel.com.kakaointern.data.entity.ImageDataResult;
import goduoel.com.kakaointern.data.repository.ImageRepository;
import goduoel.com.kakaointern.presentation.BaseViewModel;

public class ImageDetailViewModel extends BaseViewModel {

    private ImageRepository repository;

    @NonNull
    private final MutableLiveData<List<ImageDataResult.ImageDocument>> imageDataList = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<Boolean> menuShowAndHide = new MutableLiveData<>();

    private ImageDetailViewModel(ImageRepository repository) {
        this.repository = repository;
        initViewData();
        loadRepositoryData();
    }

    private void loadRepositoryData() {
        imageDataList.setValue(repository.loadImageList());
    }

    private void initViewData() {
        menuShowAndHide.setValue(false);
    }

    @NonNull
    public LiveData<List<ImageDataResult.ImageDocument>> getImageDataList() {
        return imageDataList;
    }

    @NonNull
    LiveData<Boolean> getMenuShowAndHide() {
        return menuShowAndHide;
    }

    public void showAndHideMenu() {
        boolean isShow = menuShowAndHide.getValue();
        menuShowAndHide.setValue(!isShow);
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
