package goduoel.com.kakaointern.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileOutputStream;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ImageUtil {

    public static Single<Uri> getViewBitmapUri(Context context, String url) {

        return Single.fromCallable(() -> {
            Drawable drawable = Glide.with(context)
                    .asDrawable()
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .load(url)
                    .submit()
                    .get();

            Bitmap bmp = null;

            if (drawable instanceof BitmapDrawable) {
                bmp = ((BitmapDrawable) drawable).getBitmap();
            }

            Uri bmpUri;
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                bmpUri = FileProvider.getUriForFile(context, "goduoel.com.kakaointern.fileprovider", file);
            } else {
                bmpUri = Uri.fromFile(file);
            }
            return bmpUri;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
