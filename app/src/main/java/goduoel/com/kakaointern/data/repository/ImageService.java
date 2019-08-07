package goduoel.com.kakaointern.data.repository;

import goduoel.com.kakaointern.data.entity.ImageDataResult;
import goduoel.com.kakaointern.utils.Constants;
import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

class ImageService {
    private imageSearchService imageSearchService;


    private ImageService() {
        initRetrofit();
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.KAKAO_IMAGE_API_LOCATION)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        imageSearchService = retrofit.create(imageSearchService.class);
    }

    static ImageService getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        static final ImageService INSTANCE = new ImageService();
    }

    imageSearchService getImageSearchService() {
        return imageSearchService;
    }

    interface imageSearchService {
        @GET("v2/search/image")
        Single<ImageDataResult> getImageList(@Header("Authorization") String authorization, @Query("query") String qeury, @Query("sort") String sorty, @Query("page") int page, @Query("size") int size);
    }
}
