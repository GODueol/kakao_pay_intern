package goduoel.com.kakaointern.data.repository;

import java.util.List;

import goduoel.com.kakaointern.data.entity.ImageDataResult;
import goduoel.com.kakaointern.data.entity.ImageRequestType;
import goduoel.com.kakaointern.data.entity.RequestHeader;
import io.reactivex.Single;

interface ImageDataSource {

    Single<ImageDataResult> getImageList(String query, ImageRequestType sort, int page);

    void saveImageList(List<ImageDataResult.ImageDocument> list);

    void saveRequestHeader(RequestHeader query);

    RequestHeader loadRequestHeader();

    List<ImageDataResult.ImageDocument> loadImageList();
}
