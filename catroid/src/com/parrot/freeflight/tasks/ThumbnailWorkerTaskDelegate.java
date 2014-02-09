package com.parrot.freeflight.tasks;

import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

public interface ThumbnailWorkerTaskDelegate
{
    public void onThumbnailReady(final ImageView view, final String key, final BitmapDrawable thumbnail);
}
