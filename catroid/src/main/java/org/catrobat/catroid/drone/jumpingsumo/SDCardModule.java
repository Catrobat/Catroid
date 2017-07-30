package org.catrobat.catroid.drone.jumpingsumo;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.parrot.arsdk.ardatatransfer.ARDATATRANSFER_ERROR_ENUM;
import com.parrot.arsdk.ardatatransfer.ARDataTransferException;
import com.parrot.arsdk.ardatatransfer.ARDataTransferManager;
import com.parrot.arsdk.ardatatransfer.ARDataTransferMedia;
import com.parrot.arsdk.ardatatransfer.ARDataTransferMediasDownloader;
import com.parrot.arsdk.ardatatransfer.ARDataTransferMediasDownloaderCompletionListener;
import com.parrot.arsdk.ardatatransfer.ARDataTransferMediasDownloaderProgressListener;
import com.parrot.arsdk.arutils.ARUtilsManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SDCardModule extends AppCompatActivity{

    private static final String TAG = "SDCardModule";
    int gl_picCount = -1;

    private static final String DRONE_MEDIA_FOLDER = "internal_000";
    private static final String MOBILE_MEDIA_FOLDER = "/JumpingSumo/";

    public interface Listener {
        /**
         * Called before medias will be downloaded
         * Called on a separate thread
         * @param nbMedias the number of medias that will be downloaded
         */
        void onMatchingMediasFound(int nbMedias);

        /**
         * Called each time the progress of a download changes
         * Called on a separate thread
         * @param mediaName the name of the media
         * @param progress the progress of its download (from 0 to 100)
         */
        void onDownloadProgressed(String mediaName, int progress);

        /**
         * Called when a media download has ended
         * Called on a separate thread
         * @param mediaName the name of the media
         */
        void onDownloadComplete(String mediaName);
    }

    private final List<Listener> mListeners;

    private ARDataTransferManager mDataTransferManager;
    private ARUtilsManager mFtpList;
    private ARUtilsManager mFtpQueue;

    private boolean mThreadIsRunning;
    private boolean mIsCancelled;

    private int mNbMediasToDownload;
    private int mCurrentDownloadIndex;

    public SDCardModule(@NonNull ARUtilsManager ftpListManager, @NonNull ARUtilsManager ftpQueueManager) {

        mThreadIsRunning = false;
        mListeners = new ArrayList<>();

        mFtpList = ftpListManager;
        mFtpQueue = ftpQueueManager;

        ARDATATRANSFER_ERROR_ENUM result = ARDATATRANSFER_ERROR_ENUM.ARDATATRANSFER_OK;
        try {
            mDataTransferManager = new ARDataTransferManager();
        } catch (ARDataTransferException e) {
            Log.e(TAG, "Exception", e);
            result = ARDATATRANSFER_ERROR_ENUM.ARDATATRANSFER_ERROR;
        }

        if (result == ARDATATRANSFER_ERROR_ENUM.ARDATATRANSFER_OK) {
            // direct to external directory
            String externalDirectory = Environment.getExternalStorageDirectory().toString().concat(MOBILE_MEDIA_FOLDER);

            // if the directory doesn't exist, create it
            File f = new File(externalDirectory);
            if(!(f.exists() && f.isDirectory())) {
                boolean success = f.mkdir();
                if (!success) {
                    Log.e(TAG, "Failed to create the folder " + externalDirectory);
                }
            }
            try {
                mDataTransferManager.getARDataTransferMediasDownloader().createMediasDownloader(mFtpList, mFtpQueue, DRONE_MEDIA_FOLDER, externalDirectory);
            } catch (ARDataTransferException e) {
                Log.e(TAG, "Exception", e);
                result = e.getError();
            }
        }

        if (result != ARDATATRANSFER_ERROR_ENUM.ARDATATRANSFER_OK) {
            // clean up here because an error happened
            mDataTransferManager.dispose();
            mDataTransferManager = null;
        }
    }

	//region Listener functions
	public void addListener(Listener listener) {
		mListeners.add(listener);
	}

    public void getallFlightMedias() {
        if (!mThreadIsRunning) {
            mThreadIsRunning = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<ARDataTransferMedia> mediaList = getMediaList();
                    mNbMediasToDownload = mediaList.size();
                    notifyMatchingMediasFound(mNbMediasToDownload);
                    if ((mediaList != null) && (mNbMediasToDownload != 0) && !mIsCancelled) {
                        downloadMedias(mediaList);
                    }
                    mThreadIsRunning = false;
                    mIsCancelled = false;
                }
            }).start();
        }
    }

    public void deleteLastReceivedPic(final String mediaName) {
        if (mThreadIsRunning) {
            ArrayList<ARDataTransferMedia> mediaList = getMediaList();
            if ((mediaList != null) && !mIsCancelled) {
                ARDataTransferMediasDownloader mediasDownloader = null;
                if (mDataTransferManager != null) {
                    mediasDownloader = mDataTransferManager.getARDataTransferMediasDownloader();
                }

                for (ARDataTransferMedia media : mediaList) {
                    if (media.getName().equals(mediaName)) {
                        Log.i(TAG, "delete Files: " + media.getName() + " returns " + mediasDownloader.deleteMedia(media));
                    }
                }
            }

        }
    }

    public int getPicCount() {
        gl_picCount = -1;
        if (mThreadIsRunning) {
            ArrayList<ARDataTransferMedia> mediaList = getMediaList();
            gl_picCount = mediaList.size();
        }
        else {
            mThreadIsRunning = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mThreadIsRunning = false;
                    mIsCancelled = false;
                }
            }).start();
            ArrayList<ARDataTransferMedia> mediaList = getMediaList();
            gl_picCount = mediaList.size();
        }
        return gl_picCount;
    }


    private ArrayList<ARDataTransferMedia> getMediaList() {
        ArrayList<ARDataTransferMedia> mediaList = null;

        ARDataTransferMediasDownloader mediasDownloader = null;
        if (mDataTransferManager != null)
        {
            mediasDownloader = mDataTransferManager.getARDataTransferMediasDownloader();
        }

        if (mediasDownloader != null)
        {
            try
            {
                int mediaListCount = mediasDownloader.getAvailableMediasSync(false);
                mediaList = new ArrayList<>(mediaListCount);
                for (int i = 0; ((i < mediaListCount) && !mIsCancelled) ; i++)
                {
                    ARDataTransferMedia currentMedia = mediasDownloader.getAvailableMediaAtIndex(i);
                    mediaList.add(currentMedia);
                }
            }
            catch (ARDataTransferException e)
            {
                Log.e(TAG, "Exception", e);
                mediaList = null;
            }
        }
        Log.i(TAG, "check Media Files Size: " + mediaList.size());
        return mediaList;
    }

    private void downloadMedias(@NonNull ArrayList<ARDataTransferMedia> matchingMedias) {
        mCurrentDownloadIndex = 1;

        ARDataTransferMediasDownloader mediasDownloader = null;
        if (mDataTransferManager != null)
        {
            mediasDownloader = mDataTransferManager.getARDataTransferMediasDownloader();
        }

        if (mediasDownloader != null)
        {
            for (ARDataTransferMedia media : matchingMedias) {
                try {
                    mediasDownloader.addMediaToQueue(media, mDLProgressListener, null, mDLCompletionListener, null);
                } catch (ARDataTransferException e) {
                    Log.e(TAG, "Exception", e);
                }

                // exit if the async task is cancelled
                if (mIsCancelled) {
                    break;
                }
            }

            if (!mIsCancelled) {
                mediasDownloader.getDownloaderQueueRunnable().run();
                Log.i(TAG, "download complete4");
            }
        }
    }

    //region notify listener block
    private void notifyMatchingMediasFound(int nbMedias) {
        List<Listener> listenersCpy = new ArrayList<>(mListeners);
        for (Listener listener : listenersCpy) {
            listener.onMatchingMediasFound(nbMedias);
        }
    }

    private void notifyDownloadProgressed(String mediaName, int progress) {
        List<Listener> listenersCpy = new ArrayList<>(mListeners);
        for (Listener listener : listenersCpy) {
            listener.onDownloadProgressed(mediaName, progress);
        }
    }

    private void notifyDownloadComplete(String mediaName) {
        List<Listener> listenersCpy = new ArrayList<>(mListeners);
        for (Listener listener : listenersCpy) {
            listener.onDownloadComplete(mediaName);
        }
    }
    //endregion notify listener block

    private final ARDataTransferMediasDownloaderProgressListener mDLProgressListener = new ARDataTransferMediasDownloaderProgressListener() {
        private int mLastProgressSent = -1;
        @Override
        public void didMediaProgress(Object arg, ARDataTransferMedia media, float percent) {
            final int progressInt = (int) Math.floor(percent);
            if (mLastProgressSent != progressInt) {
                mLastProgressSent = progressInt;
                notifyDownloadProgressed(media.getName(), progressInt);
            }
        }

    };

    private final ARDataTransferMediasDownloaderCompletionListener mDLCompletionListener = new ARDataTransferMediasDownloaderCompletionListener() {
        @Override
        public void didMediaComplete(Object arg, ARDataTransferMedia media, ARDATATRANSFER_ERROR_ENUM error) {
            notifyDownloadComplete(media.getName());

            // when all download are finished, stop the download runnable
            // in order to get out of the downloadMedias function
            mCurrentDownloadIndex ++;
            if (mCurrentDownloadIndex > mNbMediasToDownload ) {
                ARDataTransferMediasDownloader mediasDownloader = null;
                if (mDataTransferManager != null) {
                    mediasDownloader = mDataTransferManager.getARDataTransferMediasDownloader();
                }

                if (mediasDownloader != null) {
                    mediasDownloader.cancelQueueThread();
                }
            }
        }
    };
}
