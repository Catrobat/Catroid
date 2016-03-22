/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.utils;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.web.ServerCalls;

public class WebImageLoader {

    private static final String TAG = WebImageLoader.class.getSimpleName();
    //private static final int PLACEHOLDER_IMAGE_RESOURCE = R.drawable.ic_launcher;
    private static final int MAX_NUM_OF_RETRIES = 2;
    private static final int MIN_TIMEOUT = 1_000; // in ms

    private Context context;
    private MemoryImageCache memoryCache;
    private FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private ExecutorService executorService;

    public WebImageLoader(Context context, MemoryImageCache memoryCache, FileCache fileCache, ExecutorService executorService) {
        this.context = context;
        this.memoryCache = memoryCache;
        this.fileCache = fileCache;
        this.executorService = executorService;
    }

    private class ImageToLoad {
        public String url;
        public ImageView imageView;

        public ImageToLoad(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }
    }

    private class BitmapDisplayer implements Runnable {
        public Bitmap bitmap;
        public ImageToLoad imageToLoad;

        public BitmapDisplayer(Bitmap bitmap, ImageToLoad imageToLoad){
            this.bitmap = bitmap;
            this.imageToLoad = imageToLoad;
        }

        public void run() {
            if (! Looper.getMainLooper().equals(Looper.myLooper())) {
                throw new AssertionError("You should not change the UI from any thread "
                        + "except UI thread!");
            }

            if (imageViewReused(imageToLoad)) {
                Log.d(TAG, "REUSED!");
                return;
            }
            if (bitmap != null) {
                Log.d(TAG, "Bitmap given!");
                imageToLoad.imageView.setImageBitmap(bitmap);
            } else {
                Log.d(TAG, "Bitmap NOT given!");
                imageToLoad.imageView.setImageBitmap(null);//.setImageResource(PLACEHOLDER_IMAGE_RESOURCE);
            }
        }
    }

    public void fetchAndShowImage(String url, ImageView imageView) {
        Log.d(TAG, "Fetching image from URL: " + url);
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageBitmap(null);//.setImageResource(PLACEHOLDER_IMAGE_RESOURCE);
            // enqueue image
            executorService.submit(new ImageLoader(new ImageToLoad(url, imageView)));
        }
    }

    @Nullable
    private Bitmap decodeFile(File file) {
        Log.d(TAG, "Trying to decode file");
        if ((file == null) || ! file.exists()) {
            return null;
        }
        int width = context.getResources().getDimensionPixelSize(R.dimen.scratch_project_thumbnail_width);
        int height = context.getResources().getDimensionPixelSize(R.dimen.scratch_project_thumbnail_height);
        return ImageEditing.getScaledBitmapFromPath(file.getAbsolutePath(), width, height,
                ImageEditing.ResizeType.STAY_IN_RECTANGLE_WITH_SAME_ASPECT_RATIO, true);
    }

    boolean imageViewReused(ImageToLoad imageToLoad){
        String tag = imageViews.get(imageToLoad.imageView);
        return tag == null || (!tag.equals(imageToLoad.url));
    }

    class ImageLoader implements Runnable {
        ImageToLoad imageToLoad;
        ImageLoader(ImageToLoad imageToLoad) {
            this.imageToLoad = imageToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(imageToLoad)) {
                return;
            }
            File file = fileCache.getFile(imageToLoad.url);
            if (file == null) {
                return;
            }
            Bitmap bitmap = decodeFile(file); // try to load from disk
            if (bitmap == null) { // try to download file from web
                bitmap = fetchBitmapFromWeb(imageToLoad.url, file);
            }
            if (bitmap == null) {
                return; // give up!
            }
            memoryCache.put(imageToLoad.url, bitmap);
            if (imageViewReused(imageToLoad)) {
                return;
            }
            BitmapDisplayer bitmapDisplayer = new BitmapDisplayer(bitmap, imageToLoad);
            ((Activity)imageToLoad.imageView.getContext()).runOnUiThread(bitmapDisplayer);
        }

        @Nullable
        private Bitmap fetchBitmapFromWeb(String url, File file) {
            Log.d(TAG, "Downloading image from URL: " + url);
            if ((url == null) || (file == null)) {
                return null;
            }

            // exponential backoff
            int delay;
            for (int attempt = 0; attempt <= MAX_NUM_OF_RETRIES; attempt++) {
                try {
                    ServerCalls.getInstance().downloadImage(url, file);
                    return decodeFile(file);
                } catch (Throwable exception) {
                    Log.d(TAG, exception.getLocalizedMessage() + "\n" +  exception.getStackTrace());
                    /*if (exception instanceof OutOfMemoryError) {
                        memoryCache.clear();
                    }*/
                    delay = MIN_TIMEOUT + (int) (MIN_TIMEOUT * Math.random() * (attempt + 1));
                    Log.i(TAG, "Retry #" + (attempt+1) + " to fetch scratch project list scheduled in "
                            + delay + " ms due to " + exception.getLocalizedMessage());
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {}
                }
            }
            Log.w(TAG, "Maximum number of " + (MAX_NUM_OF_RETRIES + 1)
                    + " attempts exceeded! Server not reachable?!");
            return null;
        }

    }

}
