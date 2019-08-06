package goduoel.com.kakaointern.data.repository;

import goduoel.com.kakaointern.data.entity.ImageDataResult;
import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

class ImageService {

    private static final String HOST = "https://dapi.kakao.com/";

    private imageSearchService imageSearchService;


    private ImageService() {
        initRetrofit();
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HOST)
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

    public imageSearchService getImageSearchService() {
        return imageSearchService;
    }
    
    interface imageSearchService {
        @GET("v2/search/image")
        @Headers("Authorization:KakaoAK 41f6c29216f4c9bd522e7e09434ebdfa")
        Single<ImageDataResult> getImageList(@Query("query") String qeury, @Query("sort") String sorty, @Query("page") int page, @Query("size") int size);
    }
}
