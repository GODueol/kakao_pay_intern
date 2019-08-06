package goduoel.com.kakaointern.data.error;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import retrofit2.HttpException;

public class RxNetworkErrorHandler<T> implements Function<Throwable, ObservableSource<? extends T>> {
    @Override
    public ObservableSource<? extends T> apply(Throwable throwable) throws Exception {
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            if (httpException.response().isSuccessful()) {
                return Observable.error(new Exception("Parsing error occurred"));
            } else {
                return Observable.error(new Exception(httpException.response().errorBody().string()));
            }
        }
        return Observable.error(new Exception(throwable.getMessage()));
    }
}
