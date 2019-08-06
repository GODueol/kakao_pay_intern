package goduoel.com.kakaointern.data.repository;

import java.util.List;

import goduoel.com.kakaointern.data.entity.ImageDataResult;
import goduoel.com.kakaointern.data.entity.ImageRequestType;
import io.reactivex.Observable;
import io.reactivex.Single;

interface ImageDataSource {

    Single<ImageDataResult> getImageList(String query, ImageRequestType sort, int page);

    Observable<Throwable> handleError();

    void saveImageList(List<ImageDataResult.ImageDocument> list);

    List<ImageDataResult.ImageDocument> loadImageList();
}
