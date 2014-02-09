package com.parrot.freeflight.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Log;

import com.parrot.freeflight.vo.MediaVO;

public class ARDroneMediaGallery
        implements MediaScannerConnectionClient
{
    private static final String TAG = ARDroneMediaGallery.class.getSimpleName();

    private MediaScannerConnection connection;
    private ContentResolver contentResolver;
    private List<String> mediaToScan;

    public ARDroneMediaGallery(Context context)
    {
        this.contentResolver = context.getContentResolver();
        mediaToScan = new Vector<String>();
        connection = new MediaScannerConnection(context, this);
    }
   
    /**
     * Use {@link insertMedia()} instead
     * @param file - media file
     */
    @Deprecated
    public void scanMediaFile(final File file)
    {
        scanMediaFile(file.getAbsolutePath());
    }

    @Deprecated
    /**
     * Use insertMedia instead
     * @param file
     */
    public void scanMediaFile(final String file)
    {
        if (!connection.isConnected()) {
            synchronized (mediaToScan) {
                mediaToScan.add(file);
            }
            
            connection.connect();
        } else { 
            connection.scanFile(file, null);
        }
    }
    
    
    @SuppressLint("NewApi")
    /** 
     * Adds media to Android's media gallery
     * @param file - media file
     */
    public void insertMedia(File file) throws RuntimeException
    {
        String filename = file.getName();
        
        if (filename.endsWith("jpg")) {
            Uri uri = FileUtils.isExtStorgAvailable() ? Images.Media.EXTERNAL_CONTENT_URI : Images.Media.INTERNAL_CONTENT_URI;
            
            ContentValues values = new ContentValues();
            values.put(Images.Media.TITLE, filename); 
            values.put(Images.Media.DISPLAY_NAME, filename);
            values.put(Images.Media.BUCKET_DISPLAY_NAME, FileUtils.MEDIA_PUBLIC_FOLDER_NAME);
            values.put(Images.Media.DATA, file.getAbsolutePath());
            values.put(Images.Media.MIME_TYPE, "image/jpg");
            values.put(Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            
            ExifInterface exif = null;
            
            try {
                exif = new ExifInterface(file.getAbsolutePath());
                float latlon[] = new float[2];
                if (exif.getLatLong(latlon)) {
                    values.put(Images.Media.LATITUDE, latlon[0]);
                    values.put(Images.Media.LONGITUDE, latlon[1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            contentResolver.insert(uri, values);
        } else if (filename.endsWith("mp4")) {
            Uri uri = FileUtils.isExtStorgAvailable() ? Video.Media.EXTERNAL_CONTENT_URI : Video.Media.INTERNAL_CONTENT_URI;
           
            ContentValues values = new ContentValues();
            values.put(Video.Media.DISPLAY_NAME, filename);
            values.put(Video.Media.TITLE, filename);            
            values.put(Video.Media.BUCKET_DISPLAY_NAME, FileUtils.MEDIA_PUBLIC_FOLDER_NAME);
            values.put(Video.Media.DATA, file.getAbsolutePath());
            values.put(Video.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(Video.Media.DATE_ADDED, System.currentTimeMillis());
            values.put(Video.Media.MIME_TYPE, "video/mp4");            
            values.put(Video.Media.IS_PRIVATE, 0);
            values.put(Video.Media.ARTIST, "");
            values.put(Video.Media.ALBUM, FileUtils.MEDIA_PUBLIC_FOLDER_NAME);
            
            if (Build.VERSION.SDK_INT >= 10) {

                try {
                    // Trying to get some metadata from the file
                    MediaMetadataRetriever metadata = new MediaMetadataRetriever();
                    metadata.setDataSource(file.getAbsolutePath());
                    String videoWidth = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                    String videoHeight = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                    values.put(Video.Media.MIME_TYPE, metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE));
                    values.put(Video.Media.DURATION, Integer.parseInt(metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
                    
                    values.put(Video.Media.RESOLUTION, videoWidth + "x" + videoHeight);
                    values.put(Video.Media.WIDTH, videoWidth);
                    values.put(Video.Media.HEIGHT, videoHeight);
                } catch (RuntimeException e) {
                    Log.w(TAG, "Can't get metadata from the file. Looks like it is corrupted.");
                }
            }
            
            contentResolver.insert(uri, values);
        }
    }

    public void deleteMedia(File file)
    {
        String filename = file.getName();
        String[] args = new String[] { filename, FileUtils.MEDIA_PUBLIC_FOLDER_NAME };

        if (filename.endsWith("jpg")) {
            Uri uri = FileUtils.isExtStorgAvailable() ? Images.Media.EXTERNAL_CONTENT_URI
                    : Images.Media.INTERNAL_CONTENT_URI;
            String where = Images.Media.DISPLAY_NAME + "=? and " + Images.Media.BUCKET_DISPLAY_NAME + "=?";
            contentResolver.delete(uri, where, args);
        } else if (filename.endsWith("mp4")) {
            Uri uri = FileUtils.isExtStorgAvailable() ? Video.Media.EXTERNAL_CONTENT_URI
                    : Video.Media.INTERNAL_CONTENT_URI;
            String where = Video.Media.DISPLAY_NAME + "=? and " + Video.Media.BUCKET_DISPLAY_NAME + "=?";
            contentResolver.delete(uri, where, args);
        }
    }

    public void deleteMedia(int id)
    {
        String[] args = new String[] { String.valueOf(id) };
        Uri uri = FileUtils.isExtStorgAvailable() ? Images.Media.EXTERNAL_CONTENT_URI
                : Images.Media.INTERNAL_CONTENT_URI;
        String where = Images.Media._ID + "=?";

        contentResolver.delete(uri, where, args);
    }
    
    
    public void deleteVideos(int[] ids)
    {
        Uri uri = FileUtils.isExtStorgAvailable() ? Video.Media.EXTERNAL_CONTENT_URI
                : Video.Media.INTERNAL_CONTENT_URI;
        String where = "";
        
        int size = ids.length;        
        String[] args = new String[size];
        
        for (int i=0; i<ids.length; ++i) {
            args[i] = String.valueOf(ids[i]);
            where += Video.Media._ID + "=?";
            
            if (i<size-1) {
                where += " OR ";
            }
        }

        contentResolver.delete(uri, where, args);
    }
    
    
    public void deleteImages(int[] ids)
    {
        Uri uri = FileUtils.isExtStorgAvailable() ? Images.Media.EXTERNAL_CONTENT_URI
                : Images.Media.INTERNAL_CONTENT_URI;
        String where = "";
        
        int size = ids.length;        
        String[] args = new String[size];
        
        for (int i=0; i<ids.length; ++i) {
            args[i] = String.valueOf(ids[i]);
            where += Images.Media._ID + "=?";
            
            if (i<size-1) {
                where += " OR ";
            }
        }

        contentResolver.delete(uri, where, args);
    }

    public void deleteMedia(String file)
    {
        deleteMedia(new File(file));
    }

    public void onMediaScannerConnected()
    {
        Log.d(TAG, "Media scanner [CONNECTED]");

        if (mediaToScan.isEmpty()) {
            Log.d(TAG, "Media scaner: No media in queue [DISCONNECTING]");
            connection.disconnect();
            return;
        }

        connection.scanFile(mediaToScan.get(0), null);
    }

    public void onScanCompleted(String path, Uri uri)
    {
        Log.d(TAG, "File " + path + " has been added to media gallery");
 
        synchronized (mediaToScan) {
            mediaToScan.remove(path);
    
            if (mediaToScan.isEmpty()) {
                Log.d(TAG, "Media scaner: No media in queue [DISCONNECTING]");
                connection.disconnect();
            } else {
                connection.scanFile(mediaToScan.get(0), null);
            }
        }
    }
    
    
    public Cursor getImagesCursor()
    {
        boolean extStorgAvailable = FileUtils.isExtStorgAvailable();
        Uri imageURI = extStorgAvailable ? Images.Media.EXTERNAL_CONTENT_URI : Images.Media.INTERNAL_CONTENT_URI;
        
        return getMediaCursor(imageURI);
    }

    
    public Cursor getVideosCursor()
    {
        boolean extStorgAvailable = FileUtils.isExtStorgAvailable();
        Uri videoURI = extStorgAvailable ? Video.Media.EXTERNAL_CONTENT_URI : Video.Media.INTERNAL_CONTENT_URI;
        
        return getMediaCursor(videoURI);
    }
    
    
    public Cursor getAllMediaCursor()
    {
        Cursor cursor1 = getImagesCursor();
        Cursor cursor2 = getVideosCursor();
        
        MergeCursor mergedCursor = new MergeCursor(new Cursor[] {cursor1, cursor2});
        return mergedCursor;
    }
    
    
    public ArrayList<MediaVO> getMediaImageList()
    {
        final ArrayList<MediaVO> resultList = new ArrayList<MediaVO>();
        boolean extStorgAvailable = FileUtils.isExtStorgAvailable();

        Uri imageURI = extStorgAvailable ? Images.Media.EXTERNAL_CONTENT_URI : Images.Media.INTERNAL_CONTENT_URI;
        addMedia(resultList, imageURI);

        return resultList;

    }
    
    public ArrayList<MediaVO> getMediaVideoList()
    {
        final ArrayList<MediaVO> resultList = new ArrayList<MediaVO>();
        boolean extStorgAvailable = FileUtils.isExtStorgAvailable();

        Uri videoURI = extStorgAvailable ? Video.Media.EXTERNAL_CONTENT_URI : Video.Media.INTERNAL_CONTENT_URI;
        addMedia(resultList, videoURI);

        return resultList;

    }

    
    private void addMedia(final ArrayList<MediaVO> resultList, final Uri uri)
    {
        Cursor cursor = getMediaCursor(uri);

        try {
            if (cursor == null) {
                Log.w(TAG, "Unknown error");
            } else if (!cursor.moveToFirst()) {
                Log.w(TAG, "No media on the device");
            } else {
    
                int _ID = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
                int DATA = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                int DATE_ADDED = cursor.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED);
   
                do {
                    int id = cursor.getInt(_ID);
                    int dateAdded = cursor.getInt(DATE_ADDED);
                    String path = cursor.getString(DATA);
                    boolean isVideo = FileUtils.isVideo(path);
    
                    MediaVO media = new MediaVO();
    
                    media.setId(id);
                    media.setDateAdded(dateAdded);
                    media.setPath(path);
                    media.setUri(Uri.withAppendedPath(uri, Integer.toString(id)));
                    media.setVideo(isVideo);
    
                    resultList.add(media);
    
                } while (cursor.moveToNext());
    
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    private Cursor getMediaCursor(final Uri uri)
    {
        String[] requestedColumns = { MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DATE_ADDED };

        String selection = MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + "=?";

        String[] selectionArgs = { FileUtils.MEDIA_PUBLIC_FOLDER_NAME };

        Cursor cursor = contentResolver.query(uri, requestedColumns, selection, selectionArgs,
                MediaStore.MediaColumns.DATE_ADDED + " ASC");
        return cursor;
    }
    
    
    public void onDestroy()
    {
        Log.d(TAG, "Media scaner: OnDestroyReceived [DISCONNECTING]");
        if (connection != null) {
            connection.disconnect();
        }
    }


    public int countOfMedia()
    {
        return countOfVideos() + countOfPhotos();
    }
    
    
    public int countOfVideos()
    {
        String[] projection = { "count("+MediaStore.MediaColumns._ID+") as result"};
        String selection = MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME + "=?";
        String[] selectionArgs = { FileUtils.MEDIA_PUBLIC_FOLDER_NAME };
        Uri uri = FileUtils.isExtStorgAvailable() ? Video.Media.EXTERNAL_CONTENT_URI : Video.Media.INTERNAL_CONTENT_URI;
        Cursor cursor = null;
       
        int count = 0;       
       
        try {
            cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);
            
            if (cursor == null) {
                Log.w(TAG, "Unknown error");
            } else if (!cursor.moveToFirst()) {
                Log.w(TAG, "No media on the device");
            } else {
                int resultIdx = cursor.getColumnIndex("result");

                if (resultIdx != -1) {
                    cursor.moveToFirst();
                    count = cursor.getInt(resultIdx);
                }  
            }
        } finally {           
            if (cursor != null) {
                cursor.close();
            }
        }
        
        return count;
    }
    
    
    public int countOfPhotos()
    {
        String[] projection = { "count("+MediaStore.MediaColumns._ID+") as result"};
        String selection = MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + "=?";
        String[] selectionArgs = { FileUtils.MEDIA_PUBLIC_FOLDER_NAME };
        Uri uri = FileUtils.isExtStorgAvailable() ? Images.Media.EXTERNAL_CONTENT_URI : Images.Media.INTERNAL_CONTENT_URI;
        Cursor cursor = null; 
        int count = 0;       
       
        try {
            cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);
            if (cursor == null) {
                Log.w(TAG, "Unknown error");
            } else if (!cursor.moveToFirst()) {
                Log.w(TAG, "No media on the device");
            } else {
                int resultIdx = cursor.getColumnIndex("result");

                if (resultIdx != -1) {
                    cursor.moveToFirst();
                    count = cursor.getInt(resultIdx);
                }  
            }
        } finally {   
            if (cursor != null) {
                cursor.close();
            }
        }
        
        return count;
    }
}
