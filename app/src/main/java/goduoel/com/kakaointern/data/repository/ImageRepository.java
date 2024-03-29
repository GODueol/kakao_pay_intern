package goduoel.com.kakaointern.data.repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

import goduoel.com.kakaointern.BuildConfig;
import goduoel.com.kakaointern.data.entity.ImageDataResult;
import goduoel.com.kakaointern.data.entity.ImageRequestType;
import goduoel.com.kakaointern.data.error.NoItemException;
import goduoel.com.kakaointern.data.error.RetryException;
import goduoel.com.kakaointern.data.error.RetryType;
import goduoel.com.kakaointern.utils.Constants;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class ImageRepository implements ImageDataSource {

    private PublishSubject<Throwable> errorMessage = PublishSubject.create();
    private List<ImageDataResult.ImageDocument> cacheList;

    private ImageRepository() {
    }

    static public ImageRepository getInstance() {
        return ImageRepository.LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        static final ImageRepository INSTANCE = new ImageRepository();
    }

    @Override
    public Single<ImageDataResult> getImageList(String query, ImageRequestType sort, int page) {
        return ImageService.getInstance().getImageSearchService()
                .getImageList(BuildConfig.KAKAO_IMAGE_API_KEY, query, sort.getType(), page, Constants.REQUEST_DEFAULT_PAGE)
                .doOnSuccess(item -> {
                    if (item.getMeta().getTotalCount() == 0) {
                        errorMessage.onNext(new NoItemException());
                    }
                })
                .retryWhen(throwableFlowable ->
                        throwableFlowable.zipWith(Flowable.range(1, 4), (e, count) -> {
                            if (count == 4) {
                                errorMessage.onNext(new RetryException("재시도 하겠습니까?", RetryType.RETRY_FAIL));
                                throw new Exception(e);
                            }
                            errorMessage.onNext(new RetryException(count, RetryType.RETRY_REQUEST));
                            return count;
                        }).flatMap(count -> Flowable.timer(2, TimeUnit.SECONDS)))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Throwable> handleError() {
        return errorMessage;
    }

    @Override
    public void saveImageList(List<ImageDataResult.ImageDocument> list) {
        cacheList = list;
    }

    @Override
    public List<ImageDataResult.ImageDocument> loadImageList() {
        return cacheList;
    }
}
