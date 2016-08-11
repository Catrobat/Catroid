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
package org.catrobat.catroid.drone;

import android.os.Environment;
import android.support.annotation.NonNull;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class JumpingSumoSDCardModule {

	private static final String TAG = "SDCardModule";
	int picCounter = -1;

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
	private boolean threadIsRunning;
	private boolean isCancelled;

	private int nbMediasToDownload;
	private int currentDownloadIndex;

	public JumpingSumoSDCardModule(@NonNull ARUtilsManager ftpListManager, @NonNull ARUtilsManager ftpQueueManager) {

		threadIsRunning = false;
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
			String externalDirectory = Environment.getExternalStorageDirectory().toString().concat(MOBILE_MEDIA_FOLDER);
			File f = new File(externalDirectory);
			if (!(f.exists() && f.isDirectory())) {
				boolean success = f.mkdir();
				if (!success) {
					Log.e(TAG, "Failed to create the folder " + externalDirectory);
				}
			}
			try {
				dataTransferManager.getARDataTransferMediasDownloader().createMediasDownloader(ftpList, ftpQueue, DRONE_MEDIA_FOLDER, externalDirectory);
			} catch (ARDataTransferException e) {
				Log.e(TAG, "Exception", e);
				result = e.getError();
			}
		}

		if (result != ARDATATRANSFER_ERROR_ENUM.ARDATATRANSFER_OK) {
			dataTransferManager.dispose();
			dataTransferManager = null;
		}
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public void getFlightMedias(final String runId) {
		if (!threadIsRunning) {
			threadIsRunning = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					ArrayList<ARDataTransferMedia> mediaList = getMediaList();
					ArrayList<ARDataTransferMedia> mediasFromRun = null;
					nbMediasToDownload = 0;
					if ((mediaList != null) && !isCancelled) {
						mediasFromRun = getRunIdMatchingMedias(mediaList, runId);
						nbMediasToDownload = mediasFromRun.size();
					}

					notifyMatchingMediasFound(nbMediasToDownload);

					if ((mediasFromRun != null) && (nbMediasToDownload != 0) && !isCancelled) {
						downloadMedias(mediasFromRun);
					}
					threadIsRunning = false;
					isCancelled = false;
				}
			}).start();
		}
	}

	public void getTodaysFlightMedias() {
		if (!threadIsRunning) {
			threadIsRunning = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					ArrayList<ARDataTransferMedia> mediaList = getMediaList();

					ArrayList<ARDataTransferMedia> mediasFromDate = null;
					nbMediasToDownload = 0;
					if ((mediaList != null) && !isCancelled) {
						GregorianCalendar today = new GregorianCalendar();
						mediasFromDate = getDateMatchingMedias(mediaList, today);
						nbMediasToDownload = mediasFromDate.size();
					}

					notifyMatchingMediasFound(nbMediasToDownload);

					if ((mediasFromDate != null) && (nbMediasToDownload != 0) && !isCancelled) {
						downloadMedias(mediasFromDate);
					}
					threadIsRunning = false;
					isCancelled = false;
				}
			}).start();
		}
	}

	public void getallFlightMedias() {
		if (!threadIsRunning) {
			threadIsRunning = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					ArrayList<ARDataTransferMedia> mediaList = getMediaList();
					nbMediasToDownload = mediaList.size();
					notifyMatchingMediasFound(nbMediasToDownload);
					if ((mediaList != null) && (nbMediasToDownload != 0) && !isCancelled) {
						downloadMedias(mediaList);
					}
					threadIsRunning = false;
					isCancelled = false;
				}
			}).start();
		}
	}

	public void cancelGetFlightMedias() {
		if (threadIsRunning) {
			isCancelled = true;
			ARDataTransferMediasDownloader mediasDownloader = null;
			if (dataTransferManager != null) {
				mediasDownloader = dataTransferManager.getARDataTransferMediasDownloader();
			}

			if (mediasDownloader != null) {
				mediasDownloader.cancelQueueThread();
			}
		}
	}

	public void deleteLastReceivedPic(final String mediaName) {
		if (threadIsRunning) {
			ArrayList<ARDataTransferMedia> mediaList = getMediaList();
			if ((mediaList != null) && !isCancelled) {
				ARDataTransferMediasDownloader mediasDownloader = null;
				if (dataTransferManager != null) {
					mediasDownloader = dataTransferManager.getARDataTransferMediasDownloader();
				}

				for (ARDataTransferMedia media : mediaList) {
					if (media.getName().toString().equals(mediaName.toString())) {
						Log.i(TAG, "delete Files: " + media.getName() + " returns " + mediasDownloader.deleteMedia(media));
					}
				}
			}
		}
	}

	public int getPicCount() {
		picCounter = -1;
		if (threadIsRunning) {
			ArrayList<ARDataTransferMedia> mediaList = getMediaList();
			picCounter = mediaList.size();
		} else {
			threadIsRunning = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					threadIsRunning = false;
					isCancelled = false;
				}
			}).start();
			ArrayList<ARDataTransferMedia> mediaList = getMediaList();
			picCounter = mediaList.size();
		}
		return picCounter;
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
				for (int i = 0; ((i < mediaListCount) && !isCancelled); i++) {
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

	@NonNull
	private ArrayList<ARDataTransferMedia> getRunIdMatchingMedias(
			ArrayList<ARDataTransferMedia> mediaList,
			String runId) {
		ArrayList<ARDataTransferMedia> matchingMedias = new ArrayList<>();
		for (ARDataTransferMedia media : mediaList) {
			if (media.getName().contains(runId)) {
				matchingMedias.add(media);
			}

			if (isCancelled) {
				break;
			}
		}
		return matchingMedias;
	}

	private ArrayList<ARDataTransferMedia> getDateMatchingMedias(ArrayList<ARDataTransferMedia> mediaList, GregorianCalendar matchingCal) {
		ArrayList<ARDataTransferMedia> matchingMedias = new ArrayList<>();
		Calendar mediaCal = new GregorianCalendar();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HHmmss", Locale.getDefault());
		for (ARDataTransferMedia media : mediaList) {
			String dateStr = media.getDate();
			try {
				Date mediaDate = dateFormatter.parse(dateStr);
				mediaCal.setTime(mediaDate);

				if ((mediaCal.get(Calendar.DAY_OF_MONTH) == (matchingCal.get(Calendar.DAY_OF_MONTH)))
						&& (mediaCal.get(Calendar.MONTH) == (matchingCal.get(Calendar.MONTH)))
						&& (mediaCal.get(Calendar.YEAR) == (matchingCal.get(Calendar.YEAR)))) {
					matchingMedias.add(media);
				}
			} catch (ParseException e) {
				Log.e(TAG, "Exception", e);
			}
			if (isCancelled) {
				break;
			}
		}
		return matchingMedias;
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
					mediasDownloader.addMediaToQueue(media, dlProgressListener, null, dlCompletionListener, null);
				} catch (ARDataTransferException e) {
					Log.e(TAG, "Exception", e);
				}

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
		//context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
	}
	//endregion notify listener block

	private final ARDataTransferMediasDownloaderProgressListener dlProgressListener = new ARDataTransferMediasDownloaderProgressListener() {
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

	private final ARDataTransferMediasDownloaderCompletionListener dlCompletionListener = new ARDataTransferMediasDownloaderCompletionListener() {
		@Override
		public void didMediaComplete(Object arg, ARDataTransferMedia media, ARDATATRANSFER_ERROR_ENUM error) {
			notifyDownloadComplete(media.getName());
			currentDownloadIndex++;
			if (currentDownloadIndex > nbMediasToDownload) {
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
