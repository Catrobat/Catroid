package com.parrot.freeflight.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.provider.MediaStore.Images;
import android.util.Log;

/**
 * This class is just replacement of android.media.ThumbnailUtils. The only difference is that
 * createVideoThumbnail returns first frame of the video.
 */
public class ThumbnailUtils
{
    /**
     * Constant used to indicate we should recycle the input in
     * {@link #extractThumbnail(Bitmap, int, int, int)} unless the output is the input.
     */
    public static final int OPTIONS_RECYCLE_INPUT = 0x2;
    
    /**
     * Constant used to indicate the dimension of mini thumbnail.
     * @hide Only used by media framework and media provider internally.
     */
    public static final int TARGET_SIZE_MINI_THUMBNAIL = 320;

    /**
     * Constant used to indicate the dimension of micro thumbnail.
     * @hide Only used by media framework and media provider internally.
     */
    public static final int TARGET_SIZE_MICRO_THUMBNAIL = 96;

    
    @SuppressLint("NewApi")
    public static Bitmap createVideoThumbnail(String filePath, int kind) {
        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT < 10) {
            // This peace of code is for compatibility with android 8 and 9.
            return android.media.ThumbnailUtils.createVideoThumbnail(filePath, kind);
        }
        
        // MediaMetadataRetriever is not available for Android version less than 10
        // but we need to use it in order to get first frame of the video for thumbnail.
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(0);
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
                Log.w("ThumbnailUtils", "MediaMetadataRetriever failed with exception: " + ex);
            }
        }

        if (bitmap == null) return null;

        if (kind == Images.Thumbnails.MINI_KIND) {
            // Scale down the bitmap if it's too large.
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int max = Math.max(width, height);
            if (max > 512) {
                float scale = 512f / max;
                int w = Math.round(scale * width);
                int h = Math.round(scale * height);
                bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
            }
        } else if (kind == Images.Thumbnails.MICRO_KIND) {
            
            bitmap = android.media.ThumbnailUtils.extractThumbnail(bitmap,
                    TARGET_SIZE_MICRO_THUMBNAIL,
                    TARGET_SIZE_MICRO_THUMBNAIL,
                    OPTIONS_RECYCLE_INPUT);
        }
        
        return bitmap;
    }
    
    
    public static Bitmap extractThumbnail(Bitmap source, int width, int height) 
    {
        return android.media.ThumbnailUtils.extractThumbnail(source,
                TARGET_SIZE_MICRO_THUMBNAIL,
                TARGET_SIZE_MICRO_THUMBNAIL,
                OPTIONS_RECYCLE_INPUT);
    }
    
    
    public static Bitmap extractThumbnail(Bitmap source, int width, int height, int options) 
    {
        return android.media.ThumbnailUtils.extractThumbnail(source,
                TARGET_SIZE_MICRO_THUMBNAIL,
                TARGET_SIZE_MICRO_THUMBNAIL,
                options);
    }
}
