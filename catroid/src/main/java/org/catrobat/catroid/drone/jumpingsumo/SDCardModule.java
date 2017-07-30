/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
	int picCount = -1;

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

	private final List<Listener> listeners;

	private ARDataTransferManager dataTransferManager;
	private ARUtilsManager ftpList;
	private ARUtilsManager ftpQueue;

	private boolean threadisRunning;
	private boolean isCancelled;

	private int mediastoDownload;
	private int currentDownloadIndex;

	public SDCardModule(@NonNull ARUtilsManager ftpListManager, @NonNull ARUtilsManager ftpQueueManager) {

		threadisRunning = false;
		listeners = new ArrayList<>();

		ftpList = ftpListManager;
		ftpQueue = ftpQueueManager;

		ARDATATRANSFER_ERROR_ENUM result = ARDATATRANSFER_ERROR_ENUM.ARDATATRANSFER_OK;
		try {
			dataTransferManager = new ARDataTransferManager();
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
				dataTransferManager.getARDataTransferMediasDownloader().createMediasDownloader(ftpList, ftpQueue,
						DRONE_MEDIA_FOLDER,
						externalDirectory);
			} catch (ARDataTransferException e) {
				Log.e(TAG, "Exception", e);
				result = e.getError();
			}
		}

		if (result != ARDATATRANSFER_ERROR_ENUM.ARDATATRANSFER_OK) {
			// clean up here because an error happened
			dataTransferManager.dispose();
			dataTransferManager = null;
		}
	}

	//region Listener functions
	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void getallFlightMedias() {
		if (!threadisRunning) {
			threadisRunning = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					ArrayList<ARDataTransferMedia> mediaList = getMediaList();
					mediastoDownload = mediaList.size();
					notifyMatchingMediasFound(mediastoDownload);
					if ((mediaList != null) && (mediastoDownload != 0) && !isCancelled) {
						downloadMedias(mediaList);
					}
					threadisRunning = false;
					isCancelled = false;
				}
			}).start();
		}
	}

	public void deleteLastReceivedPic(final String mediaName) {
		if (threadisRunning) {
			ArrayList<ARDataTransferMedia> mediaList = getMediaList();
			if ((mediaList != null) && !isCancelled) {
				ARDataTransferMediasDownloader mediasDownloader = null;
				if (dataTransferManager != null) {
					mediasDownloader = dataTransferManager.getARDataTransferMediasDownloader();
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
		picCount = -1;
		if (threadisRunning) {
			ArrayList<ARDataTransferMedia> mediaList = getMediaList();
			picCount = mediaList.size();
		} else {
			threadisRunning = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					threadisRunning = false;
					isCancelled = false;
				}
			}).start();
			ArrayList<ARDataTransferMedia> mediaList = getMediaList();
			picCount = mediaList.size();
		}
		return picCount;
	}

	private ArrayList<ARDataTransferMedia> getMediaList() {
		ArrayList<ARDataTransferMedia> mediaList = null;

		ARDataTransferMediasDownloader mediasDownloader = null;
		if (dataTransferManager != null) {
			mediasDownloader = dataTransferManager.getARDataTransferMediasDownloader();
		}

		if (mediasDownloader != null) {
			try {
				int mediaListCount = mediasDownloader.getAvailableMediasSync(false);
				mediaList = new ArrayList<>(mediaListCount);
				for (int i = 0; ((i < mediaListCount) && !isCancelled) ; i++) {
					ARDataTransferMedia currentMedia = mediasDownloader.getAvailableMediaAtIndex(i);
					mediaList.add(currentMedia);
				}
			} catch (ARDataTransferException e) {
				Log.e(TAG, "Exception", e);
				mediaList = null;
			}
		}
		Log.i(TAG, "check Media Files Size: " + mediaList.size());
		return mediaList;
	}

	private void downloadMedias(@NonNull ArrayList<ARDataTransferMedia> matchingMedias) {
		currentDownloadIndex = 1;

		ARDataTransferMediasDownloader mediasDownloader = null;
		if (dataTransferManager != null) {
			mediasDownloader = dataTransferManager.getARDataTransferMediasDownloader();
		}

		if (mediasDownloader != null) {
			for (ARDataTransferMedia media : matchingMedias) {
				try {
					mediasDownloader.addMediaToQueue(media, progressListener, null, completionListener, null);
				} catch (ARDataTransferException e) {
					Log.e(TAG, "Exception", e);
				}

				// exit if the async task is cancelled
				if (isCancelled) {
					break;
				}
			}

			if (!isCancelled) {
				mediasDownloader.getDownloaderQueueRunnable().run();
				Log.i(TAG, "download complete4");
			}
		}
	}

	//region notify listener block
	private void notifyMatchingMediasFound(int nbMedias) {
		List<Listener> listenersCpy = new ArrayList<>(listeners);
		for (Listener listener : listenersCpy) {
			listener.onMatchingMediasFound(nbMedias);
		}
	}

	private void notifyDownloadProgressed(String mediaName, int progress) {
		List<Listener> listenersCpy = new ArrayList<>(listeners);
		for (Listener listener : listenersCpy) {
			listener.onDownloadProgressed(mediaName, progress);
		}
	}

	private void notifyDownloadComplete(String mediaName) {
		List<Listener> listenersCpy = new ArrayList<>(listeners);
		for (Listener listener : listenersCpy) {
			listener.onDownloadComplete(mediaName);
		}
	}
	//endregion notify listener block

	private final ARDataTransferMediasDownloaderProgressListener progressListener = new
			ARDataTransferMediasDownloaderProgressListener() {
		private int lastProgressSent = -1;
		@Override
		public void didMediaProgress(Object arg, ARDataTransferMedia media, float percent) {
			final int progressInt = (int) Math.floor(percent);
			if (lastProgressSent != progressInt) {
				lastProgressSent = progressInt;
				notifyDownloadProgressed(media.getName(), progressInt);
			}
		}
	};

	private final ARDataTransferMediasDownloaderCompletionListener completionListener = new ARDataTransferMediasDownloaderCompletionListener() {
		@Override
		public void didMediaComplete(Object arg, ARDataTransferMedia media, ARDATATRANSFER_ERROR_ENUM error) {
			notifyDownloadComplete(media.getName());

			// when all download are finished, stop the download runnable
			// in order to get out of the downloadMedias function
			currentDownloadIndex ++;
			if (currentDownloadIndex > mediastoDownload ) {
				ARDataTransferMediasDownloader mediasDownloader = null;
				if (dataTransferManager != null) {
					mediasDownloader = dataTransferManager.getARDataTransferMediasDownloader();
				}

				if (mediasDownloader != null) {
					mediasDownloader.cancelQueueThread();
				}
			}
		}
	};
}
