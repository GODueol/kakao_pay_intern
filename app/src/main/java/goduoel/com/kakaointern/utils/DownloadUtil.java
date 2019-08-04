package goduoel.com.kakaointern.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import java.io.File;

import goduoel.com.kakaointern.R;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.os.Environment.DIRECTORY_PICTURES;

public class DownloadUtil {
    private static final String FILE_EXTENSTION_PNG = ".png";

    public static void DownloadImageFIleToUrl(Context context, String url) {
        String appName = context.getString(R.string.app_name);
        String description = context.getString(R.string.donwloading);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setTitle(appName)
                .setDescription(description)
                .setMimeType("image/*")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationInExternalPublicDir(DIRECTORY_PICTURES, File.separator + appName + File.separator + System.currentTimeMillis() + FILE_EXTENSTION_PNG)
                .setAllowedOverRoaming(false)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }
}
