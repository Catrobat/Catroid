package com.parrot.freeflight.tasks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.widget.ImageView;

import com.parrot.freeflight.utils.ImageUtils;
import com.parrot.freeflight.utils.ThumbnailUtils;
import com.parrot.freeflight.vo.MediaVO;

public class MediaThumbnailExecutorManager
{
    private final ExecutorService execture = Executors.newSingleThreadExecutor();
    private ThumbnailWorkerTaskDelegate delegate;
    private Handler handler;

    public MediaThumbnailExecutorManager(final Context context, ThumbnailWorkerTaskDelegate delegate)
    {
        this.delegate = delegate;
        handler = new Handler();
    }

    
    final public void execute(final MediaVO media, final ImageView imageView)
    {
        execture.execute(getThumbnailRunnable(media, imageView));
    }
    
    
    final public void stop()
    {
        execture.shutdownNow();
    }

 
    private Runnable getThumbnailRunnable(final MediaVO media, final ImageView imageView)
    {
        final Runnable loadImage = new Runnable()
        {
            public void run()
            {
                Bitmap bitmap = null;

                final Context context = imageView.getContext();

                // get thumb from media database
                if (media.isVideo()) {

                    bitmap = MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(), media.getId(),
                            MediaStore.Video.Thumbnails.MICRO_KIND, null);

                } else {
                    bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), media.getId(),
                            MediaStore.Images.Thumbnails.MICRO_KIND, null);
                }

                
                if (bitmap == null) {
                    if (media.isVideo()) {                      
                      bitmap = ThumbnailUtils.createVideoThumbnail(media.getPath(), Thumbnails.MICRO_KIND);
                    } else { 
                      bitmap = ImageUtils.decodeBitmapFromFile(media.getPath(), imageView.getWidth(),
                              imageView.getHeight());
                    }
                }
                

                if (bitmap != null) {
                    handler.post(new OnThumbnailReadyMessage(imageView, media.getKey(), new BitmapDrawable(context.getResources(), bitmap)));
                } else {
                    Log.w("ThumbnailWorker", "Can't load thumbnail for file " + media.getPath());
                }
            }
        };

        return loadImage;
    }
    
    protected void onThumbnailReady(final ImageView view, final String key, final BitmapDrawable thumbnail)
    {
        if (delegate != null) {
            delegate.onThumbnailReady(view, key, thumbnail);
        }
    }

    
    private final class OnThumbnailReadyMessage implements Runnable
    {
        public BitmapDrawable thumbnail;
        public String key;
        public ImageView view;

        public OnThumbnailReadyMessage(ImageView view, String key, BitmapDrawable thumbnail) {
            this.thumbnail = thumbnail;
            this.key = key;
            this.view = view;
            
        }
        
        public void run()
        {
           onThumbnailReady(view, key, thumbnail);
        }
        
    }
}
