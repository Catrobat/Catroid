
package com.parrot.freeflight.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.parrot.freeflight.utils.ARDroneMediaGallery;
import com.parrot.freeflight.vo.MediaVO;

public class GetMediaObjectsListTask
        extends AsyncTask<Void, Void, List<MediaVO>>
{
    private static final String TAG = GetMediaObjectsListTask.class.getSimpleName();

    public enum MediaFilter
    {
        IMAGES,
        VIDEOS,
        ALL
    }

    private final MediaFilter filter;
    private final ARDroneMediaGallery gallery;


    public GetMediaObjectsListTask(Context context, MediaFilter filter)
    {
        this.filter = filter;
        gallery = new ARDroneMediaGallery(context);
    }


    @Override
    protected List<MediaVO> doInBackground(final Void... params)
    {
        final ArrayList<MediaVO> mediaList = new ArrayList<MediaVO>();

        if (filter == MediaFilter.IMAGES) {
            mediaList.addAll(gallery.getMediaImageList());
        } else if (filter == MediaFilter.VIDEOS) {
            mediaList.addAll(gallery.getMediaVideoList());
        } else if (filter == MediaFilter.ALL) {
            mediaList.addAll(gallery.getMediaImageList());

            if (!isCancelled()) {
                mediaList.addAll(gallery.getMediaVideoList());
            }

            if (!isCancelled()) {
                Collections.sort(mediaList);
            }
        }

        Log.d(TAG, "Total files in gallery " + mediaList.size());

        return mediaList;
    }

}
