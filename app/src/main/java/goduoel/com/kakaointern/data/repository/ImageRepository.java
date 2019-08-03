package goduoel.com.kakaointern.data.repository;

import java.util.List;

import goduoel.com.kakaointern.data.entity.ImageDataResult;
import goduoel.com.kakaointern.data.entity.ImageRequestType;
import goduoel.com.kakaointern.data.entity.RequestHeader;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ImageRepository implements ImageDataSource {

    private RequestHeader cacheRequestHeader;
    private List<ImageDataResult.ImageDocument> cacheList;

    private ImageRepository() {
        cacheRequestHeader = new RequestHeader("", ImageRequestType.ACCURACY, 1);
    }

    static public ImageRepository getInstance() {
        return ImageRepository.LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        static final ImageRepository INSTANCE = new ImageRepository();
    }

    @Override
    public Single<ImageDataResult> getImageList(String query, ImageRequestType sort, int page) {
        cacheRequestHeader.setQuery(query);
        cacheRequestHeader.setSort(sort);
        cacheRequestHeader.setPage(page);
        return ImageService.getInstance().api
                .getImageList(query, sort.getType(), page, 5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void saveImageList(List<ImageDataResult.ImageDocument> list) {
        cacheList = list;
    }

    @Override
    public void saveRequestHeader(RequestHeader requestHeader) {
        cacheRequestHeader = requestHeader;
    }

    @Override
    public RequestHeader loadRequestHeader() {
        return cacheRequestHeader;
    }

    @Override
    public List<ImageDataResult.ImageDocument> loadImageList() {
        return cacheList;
    }
}
