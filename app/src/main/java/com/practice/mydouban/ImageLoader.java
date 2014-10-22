package com.practice.mydouban;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ypeng on 10/22/14.
 */
public class ImageLoader {
    private static final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(80);

    public interface ImageLoaderListener {
        public void onImageLoaded(Bitmap bitmap);
    }

    public static void loadImage(String url, final ImageLoaderListener imageLoaderListener) {
        new AsyncTask<String, Void, Bitmap>() {

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                imageLoaderListener.onImageLoaded(bitmap);
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                String urlString = params[0];
                Bitmap bitmap;
                if (cache.get(urlString) == null) {
                    bitmap = fetchBitmap(urlString);
                    cache.put(urlString, bitmap);
                } else {
                    bitmap = cache.get(urlString);
                }
                return bitmap;
            }
        }.execute(url);
    }

    private static Bitmap fetchBitmap(String urlString) {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL imageUrl = new URL(urlString);
            httpURLConnection = (HttpURLConnection) imageUrl.openConnection();
            inputStream = httpURLConnection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            httpURLConnection.disconnect();
        }
        return null;
    }
}
