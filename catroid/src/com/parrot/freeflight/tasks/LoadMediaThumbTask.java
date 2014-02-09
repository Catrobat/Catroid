package com.parrot.freeflight.tasks;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.widget.ImageView;

import com.parrot.freeflight.utils.ImageUtils;
import com.parrot.freeflight.utils.ThumbnailUtils;
import com.parrot.freeflight.vo.MediaVO;

public class LoadMediaThumbTask
        extends AsyncTask<Void, Void, Drawable>
{
    private final MediaVO media;
    private final ImageView imageView;
    private Resources res;

    public LoadMediaThumbTask(MediaVO media, ImageView imageView)
    {
        this.media = media;
        this.imageView = imageView;
        res = imageView.getContext().getResources();
    }

    @Override
    protected Drawable doInBackground(Void... params)
    {
        Drawable drawable = null;
        Bitmap bitmap = null;
        ContentResolver contentResolver = imageView.getContext().getContentResolver();
        
        if (media.isVideo()) {
            bitmap = MediaStore.Video.Thumbnails.getThumbnail(contentResolver, media.getId(),
                    MediaStore.Video.Thumbnails.MINI_KIND, null);
            
            if (bitmap == null) { // can`t get thumb from media database, use
                                  // thumb
                if (media.isVideo()) {
                    bitmap = ThumbnailUtils.createVideoThumbnail(media.getPath(), Thumbnails.MINI_KIND);
                }
            }
        } else {
            bitmap = ImageUtils.decodeBitmapFromFile(media.getPath(), imageView.getWidth(), imageView.getHeight());
        }
        
        if (bitmap != null) {
            drawable = new BitmapDrawable(res, bitmap);
        }
        
        return drawable;
    }

    @Override
    protected void onPostExecute(Drawable result)
    {
        imageView.setImageDrawable(result);
    }

}
